# SUPABASE_SCHEMA_PROMPT.md

Follow my instructions strictly. Be concise. Optimize for low token usage and production-ready output.

You are a senior PostgreSQL + Supabase architect designing the backend for a private chat room app used by a Kotlin Multiplatform client.

## Goal
Design the complete Supabase database and authorization model for a private room-based chat system.

## Context
Client app stack:
- Kotlin Multiplatform
- Compose Multiplatform
- supabase-kt
- Supabase Auth
- Supabase Realtime
- SQLDelight local cache

This prompt is only for backend schema and security design.
Do not generate Android/iOS/KMP app code unless absolutely necessary.

## Product rules
- Private rooms only
- Only authenticated users can use the app
- Only room members can read room details
- Only room members can read member list for their rooms
- Only room members can read messages in their rooms
- Only room members can send messages in their rooms
- Realtime access must be restricted to room members only
- No public access paths
- Design should be simple, secure, and production-friendly

## Required tables
Generate schema for:
1. profiles
2. rooms
3. room_members
4. messages

## Required columns
Use practical production defaults, including:
- UUID primary keys where appropriate
- `created_at`
- `updated_at` where useful
- `created_by` for rooms
- `room_id` and `sender_id` for messages
- membership role if useful
- soft delete only if clearly justified

## Output required
Return only these sections in this order:

1. Schema overview
2. SQL migration for tables
3. Indexes
4. RLS policies
5. Realtime authorization strategy
6. Notes for KMP client integration

## Rules for output
- Use PostgreSQL / Supabase SQL
- Give real SQL, not pseudocode
- Keep explanations short
- Do not repeat requirements back to me
- Prefer secure defaults
- Avoid unnecessary tables
- Avoid overengineering
- Keep first response under 700 words if possible

## RLS expectations
You must include policies so that:
- a user can read their own profile
- a user can update their own profile
- a room is visible only to its members
- only valid members can see room membership rows for that room
- only valid members can read messages for that room
- only valid members can insert messages for that room
- room creation is restricted to authenticated users
- membership insertion logic is safe and practical

## Realtime expectations
Design Realtime for private channels only:
- use Realtime Authorization
- assume `private: true` channels
- explain channel topic naming strategy
- explain what policies are needed on `realtime.messages`
- keep it aligned with room membership checks

## Design preferences
- Optimize for secure V1
- Choose the simplest production-safe approach
- If admin/member roles are useful, include them
- If invites are too much for V1, mention them as Phase 2
- Do not add attachments, calls, reactions, or E2EE tables unless clearly marked as future scope

## Final line format
End with exactly:
NEXT: <short next action>