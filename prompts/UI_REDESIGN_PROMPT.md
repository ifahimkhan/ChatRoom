# UI_REDESIGN_PROMPT.md

You are a senior Kotlin Multiplatform UI engineer, product designer, and Jetpack Compose / Compose Multiplatform specialist.

I already have a working KMP chat app, but the current UI looks too basic and generic.
I want you to redesign it into a premium, futuristic, highly aesthetic, modern tech product UI.

## Goal
Transform the existing app UI into a flagship-quality chat experience while preserving the current architecture and business logic.

## App context
- Kotlin Multiplatform
- Compose Multiplatform
- Material 3 base is allowed, but it should be deeply customized
- Private chat room app
- Supabase backend
- Performance, maintainability, and readability still matter

## Design goals
- Futuristic but clean
- Premium dark theme first
- Elegant lighting and glow effects used sparingly
- Translucent layered surfaces only where appropriate
- Advanced spacing, hierarchy, and typography
- High-end messaging app feel
- Distinctive visual identity
- Production-ready, not a dribbble-only concept

## Visual direction
Think:
- modern AI product
- cyber-tech but restrained
- premium productivity app
- sleek layered panels
- subtle gradients
- refined shadows
- controlled accent colors
- rich message bubbles
- premium input bar
- immersive room header
- refined icons, chips, avatars, and states

## Anti-goals
Avoid:
- generic Material sample app look
- boring grey/white surfaces
- plain blue default buttons
- overly neon sci-fi effects
- excessive blur/glow
- unreadable low-contrast text
- overcomplicated layouts
- recomposition/performance issues

## Scope
Redesign and implement these parts:
1. App theme
2. Color system
3. Typography system
4. Shapes and surface styling
5. Room list screen
6. Chat room screen
7. Message bubbles
8. Top app bar / room header
9. Message input bar
10. Empty, loading, and error states
11. Optional micro-interactions if feasible and safe

## Required output behavior
First do a short audit of the current UI and identify the main reasons it feels basic.
Then propose exactly 2 redesign directions.
Then choose the better direction for this app and explain why briefly.
Then implement the redesign directly in code.

## Implementation rules
- Preserve architecture and business logic
- Do not rewrite unrelated backend code
- Do not create unnecessary code churn
- Use Compose Multiplatform best practices
- Keep state stable and recomposition-safe
- Prefer reusable design primitives
- Support dark mode properly
- Maintain accessibility and readability
- Keep the UI elegant, immersive, and premium

## Technical direction
Please improve:
- spacing rhythm
- visual hierarchy
- theme tokens
- surface layering
- message bubble styling
- room list cards/items
- iconography treatment
- typography contrast
- premium depth without visual clutter

## Output format
1. Short UI audit
2. Two redesign directions
3. Chosen direction
4. Implement directly
5. Show only changed files
6. Keep explanations concise

## Additional instruction
If screenshots or current screen files are available, use them as the main reference.
If some screens are missing, redesign what exists and create reusable primitives for the rest.

## Final instruction
Make the UI look like a real high-end shipped app, not a starter template.
Start now.