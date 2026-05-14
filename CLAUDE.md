# CLAUDE.md

You are working on a production-grade private chat room app built with Kotlin Multiplatform for Android and iOS.

## Mission
Build a privacy-first, room-based chat application using Supabase as backend with strong security, clean architecture, and minimal token waste.

## Product defaults
- Kotlin Multiplatform shared business logic
- Compose Multiplatform UI
- Supabase via `supabase-kt`
- Supabase Auth + Postgres + Realtime
- Clean Architecture
- MVVM
- Coroutines + Flow
- `kotlinx.serialization`
- SQLDelight for local cache
- Koin for DI
- Expect/actual or interface-based platform adapters for secure storage and crypto

## Core requirements
- Private rooms only
- Only authenticated users can access app data
- Only room members can read/write messages
- Only room members can receive realtime updates
- No public rooms in V1
- No insecure logs
- No hardcoded secrets
- Never expose `service_role` key in client
- Use only publishable anon key on client
- Design for future E2EE support without major refactor

## V1 features
1. Email/password auth
2. Room list
3. Create private room
4. Add members to room
5. Chat screen
6. Message history
7. Realtime incoming messages
8. Send message
9. Offline-aware cached chat history
10. Basic profile
11. Sign out

## Architecture rules
- Shared modules should be organized clearly under:
  - `core`
  - `data`
  - `domain`
  - `presentation`
- Feature areas:
  - `auth`
  - `rooms`
  - `chat`
  - `profile`
- Explicit mapping layers: DTO -> entity -> domain
- Repository pattern required
- Use cases per feature
- Strong typing and sealed result models where useful
- Prefer maintainable code over clever abstractions
- Pagination-ready message design
- UTC timestamps
- Optimistic send with failure handling

## Supabase rules
Always design and align client code with these backend expectations:
- Tables:
  - `profiles`
  - `rooms`
  - `room_members`
  - `messages`
- RLS enabled everywhere needed
- Membership-based access enforcement
- Realtime authorization must be private and membership-aware
- Backend-only privileged actions must be clearly separated from client-safe logic

## Security rules
- Do not print access token, refresh token, secret, or plaintext message in logs
- Avoid storing plaintext chat cache if encryption abstraction is available
- Sensitive local data must go through a secure storage abstraction
- Keep crypto and key handling isolated behind interfaces
- If a security decision is ambiguous, choose the safer practical default

## Output behavior
- Be concise
- Avoid repeating previous code
- Do not explain obvious Kotlin/Supabase basics
- Prefer small complete implementation steps
- Show only files changed in current step unless asked otherwise
- Keep prose short
- If blocked, ask the minimum required question

## First-response rule
On a fresh session, do not start with full code.
First provide only:
1. Architecture summary
2. Folder structure
3. Supabase schema
4. RLS policy plan
5. Step-by-step implementation plan

Then stop.

## Implementation rule
After approval, implement in small phases:
- one feature or layer at a time
- output only changed/new files
- no giant dumps unless asked
- code should be real and compilable where possible

## UI direction
- Clean modern chat UI
- Compose Multiplatform
- Message bubbles
- Date separators
- Input bar with send button
- Loading/error/empty states
- Dark mode ready
- Member-only room access handling

## Decision policy
If a decision is not critical, choose a strong production default and proceed.
Ask questions only when truly blocked.

## End-of-response format
End every response with exactly:
`NEXT: <short next action>`