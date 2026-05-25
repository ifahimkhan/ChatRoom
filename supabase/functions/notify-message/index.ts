// notify-message: Supabase Database Webhook handler that fans out FCM pushes
// when a new row is inserted into public.messages.
//
// Auth: shared secret in Authorization header (no JWT — webhook is server-side).
// Recipients: room_members of the message's room, excluding the sender.
// Payload: generic "New message in <room>" / "Tap to view". Body content NEVER leaves this file.

import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient, SupabaseClient } from "@supabase/supabase-js";

// ───────────────────────── types

interface MessageRow {
  id: string;
  room_id: string;
  sender_id: string | null;
  content: string;
  created_at: string;
  edited_at: string | null;
  deleted_at: string | null;
}

interface WebhookPayload {
  type: "INSERT" | "UPDATE" | "DELETE";
  table: string;
  schema: string;
  record: MessageRow;
}

interface FcmSendResult {
  token: string;
  ok: boolean;
  errorCode?: string;
  httpStatus: number;
}

// ───────────────────────── env

const WEBHOOK_SECRET = Deno.env.get("WEBHOOK_SECRET") ?? "";
const SUPABASE_URL = Deno.env.get("SUPABASE_URL") ?? "";
const SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
const FCM_PROJECT_ID = Deno.env.get("FCM_PROJECT_ID") ?? "";
const FCM_SERVICE_ACCOUNT_JSON = Deno.env.get("FCM_SERVICE_ACCOUNT_JSON") ?? "";

// ───────────────────────── OAuth token cache

interface CachedToken { accessToken: string; expiresAt: number; }
let cachedToken: CachedToken | null = null;

async function getFcmAccessToken(): Promise<string> {
  const now = Math.floor(Date.now() / 1000);
  if (cachedToken && cachedToken.expiresAt - 60 > now) return cachedToken.accessToken;

  const sa = JSON.parse(FCM_SERVICE_ACCOUNT_JSON);
  const header = { alg: "RS256", typ: "JWT" };
  const claim = {
    iss: sa.client_email,
    scope: "https://www.googleapis.com/auth/firebase.messaging",
    aud: "https://oauth2.googleapis.com/token",
    iat: now,
    exp: now + 3600,
  };

  const enc = (obj: object) => base64UrlEncode(new TextEncoder().encode(JSON.stringify(obj)));
  const signingInput = `${enc(header)}.${enc(claim)}`;

  const key = await importPrivateKey(sa.private_key);
  const sigBuf = await crypto.subtle.sign(
    { name: "RSASSA-PKCS1-v1_5" },
    key,
    new TextEncoder().encode(signingInput),
  );
  const jwt = `${signingInput}.${base64UrlEncode(new Uint8Array(sigBuf))}`;

  const tokenRes = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: jwt,
    }),
  });
  if (!tokenRes.ok) {
    const text = await tokenRes.text();
    throw new Error(`oauth token exchange failed: ${tokenRes.status} ${text}`);
  }
  const json = await tokenRes.json() as { access_token: string; expires_in: number };
  cachedToken = { accessToken: json.access_token, expiresAt: now + json.expires_in };
  return cachedToken.accessToken;
}

async function importPrivateKey(pem: string): Promise<CryptoKey> {
  const cleaned = pem
    .replace(/-----BEGIN PRIVATE KEY-----/g, "")
    .replace(/-----END PRIVATE KEY-----/g, "")
    .replace(/\s+/g, "");
  const der = Uint8Array.from(atob(cleaned), (c) => c.charCodeAt(0));
  return await crypto.subtle.importKey(
    "pkcs8",
    der,
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"],
  );
}

function base64UrlEncode(bytes: Uint8Array): string {
  let bin = "";
  for (let i = 0; i < bytes.length; i++) bin += String.fromCharCode(bytes[i]);
  return btoa(bin).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

// ───────────────────────── recipient lookup

async function fetchRecipientTokens(
  client: SupabaseClient,
  roomId: string,
  senderId: string,
): Promise<Array<{ token: string; user_id: string }>> {
  const { data: members, error: memErr } = await client
    .from("room_members")
    .select("user_id")
    .eq("room_id", roomId)
    .neq("user_id", senderId);
  if (memErr) throw new Error(`room_members lookup: ${memErr.message}`);
  if (!members || members.length === 0) return [];

  const userIds = members.map((m: { user_id: string }) => m.user_id);
  const { data: tokens, error: tokErr } = await client
    .from("device_tokens")
    .select("token, user_id")
    .in("user_id", userIds);
  if (tokErr) throw new Error(`device_tokens lookup: ${tokErr.message}`);
  return tokens ?? [];
}

async function fetchRoomName(client: SupabaseClient, roomId: string): Promise<string> {
  const { data, error } = await client
    .from("rooms")
    .select("name")
    .eq("id", roomId)
    .maybeSingle();
  if (error || !data) return "Chat";
  return data.name;
}

async function pruneStaleToken(client: SupabaseClient, token: string): Promise<void> {
  const { error } = await client.from("device_tokens").delete().eq("token", token);
  if (error) console.error("prune failed:", error.message);
}

// ───────────────────────── FCM send

async function sendFcm(
  accessToken: string,
  token: string,
  roomId: string,
  messageId: string,
  senderId: string,
  roomName: string,
): Promise<FcmSendResult> {
  const body = {
    message: {
      token,
      notification: { title: `New message in ${roomName}`, body: "Tap to view" },
      data: { roomId, messageId, senderId, type: "chat_message" },
      android: {
        priority: "HIGH",
        notification: { channel_id: "chat_messages", tag: roomId },
      },
    },
  };
  const res = await fetch(
    `https://fcm.googleapis.com/v1/projects/${FCM_PROJECT_ID}/messages:send`,
    {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${accessToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    },
  );
  if (res.ok) return { token, ok: true, httpStatus: res.status };

  let errorCode: string | undefined;
  try {
    const errJson = await res.json();
    errorCode = errJson?.error?.details?.[0]?.errorCode ?? errJson?.error?.status;
  } catch (_) { /* ignore */ }
  return { token, ok: false, errorCode, httpStatus: res.status };
}

// ───────────────────────── handler

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") return new Response("method not allowed", { status: 405 });

  const auth = req.headers.get("Authorization") ?? "";
  const expected = `Bearer ${WEBHOOK_SECRET}`;
  if (!WEBHOOK_SECRET || auth !== expected) {
    return new Response("unauthorized", { status: 401 });
  }

  let payload: WebhookPayload;
  try {
    payload = await req.json();
  } catch (_) {
    return new Response("bad json", { status: 400 });
  }

  if (payload.type !== "INSERT" || payload.table !== "messages") {
    return new Response(JSON.stringify({ skipped: "not a messages insert" }), { status: 200 });
  }
  const row = payload.record;
  if (!row || !row.sender_id || row.deleted_at) {
    return new Response(JSON.stringify({ skipped: "no sender or deleted" }), { status: 200 });
  }

  const client = createClient(SUPABASE_URL, SERVICE_ROLE_KEY, {
    auth: { persistSession: false },
  });

  try {
    const [recipients, roomName] = await Promise.all([
      fetchRecipientTokens(client, row.room_id, row.sender_id),
      fetchRoomName(client, row.room_id),
    ]);

    if (recipients.length === 0) {
      console.log(`notify-message: no recipients room=${row.room_id} msg=${row.id}`);
      return new Response(JSON.stringify({ sent: 0 }), { status: 200 });
    }

    const accessToken = await getFcmAccessToken();
    const results = await Promise.all(
      recipients.map((r) =>
        sendFcm(accessToken, r.token, row.room_id, row.id, row.sender_id!, roomName)
      ),
    );

    const stale = results.filter((r) =>
      !r.ok && (r.errorCode === "UNREGISTERED" ||
                r.errorCode === "INVALID_ARGUMENT" ||
                r.errorCode === "NOT_FOUND" ||
                r.httpStatus === 404)
    );
    await Promise.all(stale.map((r) => pruneStaleToken(client, r.token)));

    const okCount = results.filter((r) => r.ok).length;
    const failCount = results.length - okCount;
    console.log(
      `notify-message: room=${row.room_id} msg=${row.id} sent=${okCount} failed=${failCount} pruned=${stale.length}`,
    );
    return new Response(JSON.stringify({ sent: okCount, failed: failCount, pruned: stale.length }), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    console.error(`notify-message error: ${msg}`);
    return new Response(JSON.stringify({ error: msg }), { status: 500 });
  }
});
