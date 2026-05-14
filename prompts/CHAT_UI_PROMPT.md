# CHAT_UI_PROMPT.md

Follow my instructions strictly. Optimize for low token usage, low repetition, and production-ready output.

You are a senior Kotlin Multiplatform UI engineer and Compose Multiplatform designer.

## Goal
Build a polished private chat screen UI for a Kotlin Multiplatform app.
Focus on the shared Compose Multiplatform UI layer only.

## Tech context
- Kotlin Multiplatform
- Compose Multiplatform
- Material 3
- Shared UI for Android and iOS
- Backend is Supabase via supabase-kt
- Architecture is Clean Architecture + MVVM
- UI should be reusable, stable, and performance-conscious

## Scope
Design and implement only the chat-related UI for V1:
1. Room list screen
2. Chat room screen
3. Message bubble components
4. Message input bar
5. Empty state
6. Loading state
7. Error/retry state
8. Date separators
9. Typing/loading placeholder if useful
10. Basic top app bar for room details

## UI style direction
- Clean modern messaging UI
- Inspired by WhatsApp / Telegram usability, but not copied
- Minimal, elegant, mobile-first
- Good spacing and readability
- Dark mode ready
- Avoid generic Material demo look
- Keep design practical for production

## UX expectations
- Outgoing and incoming messages must be visually distinct
- Message timestamps should be subtle
- Date separators should be easy to scan
- Input bar should feel modern and compact
- Sending state and failed state should be supported visually
- Empty chat should feel intentional, not broken
- Room list should clearly show latest message preview and time
- Unread badge support is optional but structure for it if simple
- Avoid unnecessary animations unless they improve UX

## Performance expectations
- Avoid recomposition problems
- Use stable state patterns
- Keep composables modular
- Use lazy lists correctly
- Do not introduce heavy custom drawing unless needed
- Prefer maintainable Compose code over flashy tricks

## Implementation expectations
Use opinionated defaults:
- Material 3
- Shared theme support
- Composables split into reusable components
- UI models separated from domain models if useful
- Preview/sample data support if possible
- Keep navigation assumptions minimal
- Keep file structure clean and scalable

## Output format
First response only provide:
1. UI architecture summary
2. Proposed file structure
3. Design decisions
4. Component list
5. Implementation plan

Do not generate full code in the first response.

## After I say continue
- Implement one screen or component group at a time
- Output only changed/new files
- Keep explanations under 8 lines
- Avoid repeating code already shown
- Use real Compose Multiplatform code, not pseudocode

## Important constraints
- Maximum 500 words in the first response
- No backend code
- No database schema
- No Supabase SQL
- No auth flow unless directly required for UI state handling
- Stay focused on UI only

## Nice-to-have
If practical, include:
- preview/demo data models
- theme tokens for message bubble styling
- support for delivery state indicators
- support for failed message retry UI

## Final line format
End every response with exactly:
`NEXT: <short next action>`