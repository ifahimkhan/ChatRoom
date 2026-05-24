package com.fahim.chatroom.core.config

/**
 * Client-safe Supabase configuration. [anonKey] must be the legacy anon JWT (HS256),
 * NOT the sb_publishable_* key — PostgREST and Kong require the JWT for role assignment.
 *
 * TODO: source these from a non-committed config (BuildKonfig / xcconfig / CI secret) before release.
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    companion object {
        val Default: SupabaseConfig = SupabaseConfig(
            url = "https://wzndlbtuacwjoqdsguqq.supabase.co",
            anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind6bmRsYnR1YWN3am9xZHNndXFxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg2MDI1OTYsImV4cCI6MjA5NDE3ODU5Nn0.nZ68GmkNUmgFMMo6_ThJgKcKBfu6-UFMNfJHAMuIDMo",
        )
    }
}

/*
//
//curl -i -X POST \
//    "https://wzndlbtuacwjoqdsguqq.supabase.co/functions/v1/notify-message" \
//    -H "Authorization:  Bearer eyJhbGciOiJFUzI1NiIsImtpZCI6IjgxNzAxODQ5LWVkMWMtNDg5OC1iYWRjLWUyNTRhMWQzYmMyMyIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3d6bmRsYnR1YWN3am9xZHNndXFxLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiI0NGNiMzdhNi1iNjc1LTQ0NzgtYjc2OC1kMjk3MDJlMGU4M2QiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzc5NjU5MDM2LCJpYXQiOjE3Nzk2NTU0MzYsImVtYWlsIjoicmltc2hhaXNoZXJlQGdtYWlsLmNvbSIsInBob25lIjoiIiwiYXBwX21ldGFkYXRhIjp7InByb3ZpZGVyIjoiZW1haWwiLCJwcm92aWRlcnMiOlsiZW1haWwiXX0sInVzZXJfbWV0YWRhdGEiOnsiZW1haWwiOiJyaW1zaGFpc2hlcmVAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBob25lX3ZlcmlmaWVkIjpmYWxzZSwic3ViIjoiNDRjYjM3YTYtYjY3NS00NDc4LWI3NjgtZDI5NzAyZTBlODNkIn0sInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiYWFsIjoiYWFsMSIsImFtciI6W3sibWV0aG9kIjoicGFzc3dvcmQiLCJ0aW1lc3RhbXAiOjE3Nzg4Mjk5Mjh9XSwic2Vzc2lvbl9pZCI6IjBjODE5MzEwLTY3NjctNGRhZi05YTEwLTZlNTVhZWFjYzNkYyIsImlzX2Fub255bW91cyI6ZmFsc2V9.X31Zeww_bsNsJaANN4A5NG_MN3cqpGcyHjR6CmKphf-sM0BxuGH84-3YaQuUcNgTjPc_h9axwsFPXFjRbQNEyw
//    -H "Content-Type: application/json" \
//    -d '{"type":"INSERT","table":"messages","schema":"public","record":{"id":"0000-0000-0000-0000-000000000000","room_id":"60212354-bffa-42ad-883d-cf543f626942","s
//  ender_id":"46ed7d3c-34aa-40cb-be33-b2576eb007f5","content":"test curl","created_at":"2026-05-25T00:00:00Z","edited_at":null,"deleted_at":null}}'

 why



*
*
*
*
* */