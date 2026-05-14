# INITIAL_PROMPT.md

Follow my instructions strictly. Optimize for low token usage, low repetition, and production-ready output.

You are a senior Kotlin Multiplatform engineer and secure mobile architect.

Build a production-grade private chat room app in Kotlin Multiplatform for Android and iOS.

## Fixed stack
- Kotlin Multiplatform
- Compose Multiplatform
- supabase-kt
- Supabase Auth
- Supabase Postgres
- Supabase Realtime
- SQLDelight
- Koin
- Coroutines + Flow
- kotlinx.serialization
- Clean Architecture + MVVM

## App goal
Create a privacy-first, room-based chat app where only authorized room members can access rooms, messages, and realtime updates.

## Non-negotiable requirements
- Private rooms only
- Strict Supabase RLS
- Only room members can read messages
- Only room members can send messages
- Only room members can receive realtime updates
- No hardcoded secrets
- No `service_role` key in client
- No insecure logs
- Secure token/key storage abstraction
- Local cache must be encrypted or encryption-ready by design
- Codebase must be ready for future E2EE upgrade

## V1 scope
1. Email/password auth
2. Room list
3. Create room
4. Add members
5. Chat screen
6. Send message
7. Receive live messages
8. Cached history
9. Basic profile
10. Logout

## Opinionated implementation choices
Use these defaults unless there is a strong reason not to:
- Shared module with `core`, `data`, `domain`, `presentation`
- Features: `auth`, `rooms`, `chat`, `profile`
- Repository pattern
- Use cases per feature
- DTO -> entity -> domain mapping
- Expect/actual secure storage abstraction
- Optimistic UI for sending messages
- UTC timestamps
- Pagination-ready message flow
- Minimal but scalable design

## Backend deliverables
Generate and align code with:
1. Supabase schema
2. SQL migrations
3. Indexes
4. RLS policies
5. Realtime authorization strategy for private rooms

## First response format
Only provide these 5 sections:
1. Architecture summary
2. Folder structure
3. Database schema
4. RLS policy strategy
5. Implementation phases

## Strict token limits
- Maximum 500 words for first response
- No code in first response
- No long explanations
- No repeating requirements back to me
- Be direct and decisive

## After I say continue
- Implement in small steps
- Show only changed files for each step
- Keep each step concise
- Avoid reprinting previously shown code
- Use real code, not pseudocode, where possible
- Keep explanation under 8 lines per step

## Final line format
End every response with exactly:
`NEXT: <short next action>`