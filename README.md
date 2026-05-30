# ChatRoom

A privacy-first, room-based chat application built with **Kotlin Multiplatform** for Android and iOS. Shared business logic, native UI via Compose Multiplatform, Supabase as the backend, and push notifications on both platforms.

> Status: V1 in progress. Email/password auth, private rooms, realtime messaging, offline cache, and push notifications all working on Android.

---

## Screenshots

<table>
  <tr>
    <td align="center">
      <img src="./screenshots/auth.png" width="220" alt="Sign in" /><br/>
      <sub><b>Sign in</b></sub>
    </td>
    <td align="center">
      <img src="./screenshots/rooms.png" width="220" alt="Rooms list" /><br/>
      <sub><b>Rooms list</b></sub>
    </td>
    <td align="center">
      <img src="./screenshots/create_room.png" width="220" alt="Create room" /><br/>
      <sub><b>Create room</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="./screenshots/chat.png" width="220" alt="Chat" /><br/>
      <sub><b>Chat (realtime)</b></sub>
    </td>
    <td align="center">
      <img src="./screenshots/notification.png" width="220" alt="Push notification" /><br/>
      <sub><b>Push notification</b></sub>
    </td>
    <td align="center">
      <img src="./screenshots/profile.png" width="220" alt="Profile" /><br/>
      <sub><b>Profile</b></sub>
    </td>
  </tr>
</table>

> Drop your PNG/JPG files into `./screenshots/` with the names shown above. Recommended size: **1080×2400** (portrait), trimmed to remove status bar if you want a cleaner look.

---

## Features

- 🔐 Email/password authentication (Supabase Auth)
- 🏠 Private rooms — invite-only, no public rooms in V1
- 💬 Realtime messaging via Supabase Realtime (Postgres Changes on private channels)
- 📥 Offline-aware message cache (SQLDelight)
- 🔔 Push notifications (FCM on Android; APNs scaffolded on iOS)
- 🌓 Dark mode ready
- ↩️ Optimistic send with failure handling and retry
- 📄 Pagination-ready message design

## Tech stack

| Layer | Choice |
|-------|--------|
| Shared logic | Kotlin Multiplatform |
| UI | Compose Multiplatform |
| Backend | Supabase (Auth, Postgres, Realtime, Edge Functions) |
| Local DB | SQLDelight |
| DI | Koin |
| Networking | Ktor (via supabase-kt) |
| Serialization | kotlinx.serialization |
| Concurrency | Coroutines + Flow |
| Push | FCM (Android) / APNs (iOS scaffold) |
| Config | BuildKonfig (compile-time secrets from `local.properties`) |

## Architecture

Clean Architecture + MVVM. Shared modules under `composeApp/src/commonMain/kotlin/com/fahim/chatroom/`:

```
core/         # cross-cutting: di, db, dispatchers, error, logging, config, supabase factory, navigation
data/         # repositories, DTOs, mappers (Supabase + SQLDelight)
domain/       # use cases, repository interfaces, domain models
presentation/ # Compose screens, viewmodels, design system
```

Feature areas across all layers: `auth`, `rooms`, `chat`, `profile`, `notifications`.

**Data flow**: DTO → entity (SQLDelight) → domain model → UI state. Each direction has an explicit mapper. Repositories expose `StateFlow` for UI and suspend functions for actions.

---

## Setup

### Prerequisites

- **macOS** (required for iOS)
- **Android Studio** Koala (2024.1) or later
- **Xcode** 15+ for iOS
- **JDK 17+** — install with `brew install --cask zulu@21` and `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` in your shell rc
- A **Supabase** project (free tier is fine)
- A **Firebase** project with FCM enabled

### 1. Clone & open

```bash
git clone https://github.com/ifahimkhan/ChatRoom.git
cd ChatRoom
```

Open the root in Android Studio. Wait for Gradle sync.

### 2. Configure `local.properties` (gitignored)

Copy the template and fill in real values:

```bash
cp local.properties.example local.properties
```

Then edit `local.properties`:

```properties
sdk.dir=/Users/YOU/Library/Android/sdk

SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_ANON_KEY=<your-legacy-anon-jwt-hs256>
```

> The anon key is the **legacy HS256 JWT**, not `sb_publishable_*`. Find it in Supabase Dashboard → Settings → API → "Project API keys" → `anon` `public`.

BuildKonfig will inject these at compile time. They never enter source control.

### 3. Apply Supabase schema

Push the migrations under `supabase/migrations/`:

```bash
# Install CLI if you don't have it
brew install supabase/tap/supabase

supabase login
supabase link --project-ref <your-project-ref>
supabase db push
```

This creates `profiles`, `rooms`, `room_members`, `messages`, and `device_tokens` tables with full RLS and realtime authorization policies.

### 4. Deploy the notification edge function

```bash
supabase functions deploy notify-message
```

Set the function's secrets (see `supabase/functions/notify-message/index.ts` for what each does):

```bash
supabase secrets set WEBHOOK_SECRET=$(openssl rand -hex 32)
supabase secrets set FCM_PROJECT_ID=<your-firebase-project-id>
supabase secrets set FCM_SERVICE_ACCOUNT_JSON='<paste-entire-service-account-json>'
```

### 5. Configure the Database Webhook

Supabase Dashboard → **Database → Webhooks → Create a new hook**:

| Field | Value |
|-------|-------|
| Name | `notify_message_on_insert` |
| Table | `public.messages` |
| Events | `Insert` only |
| Type | Supabase Edge Functions |
| Function | `notify-message` |
| Method | `POST` |
| HTTP Headers | `Authorization: Bearer <same-WEBHOOK_SECRET-value>` |

### 6. Firebase (Android only)

1. Create a Firebase project, add an Android app with package `com.fahim.chatroom`.
2. Enable **Cloud Messaging**.
3. Download `google-services.json` → drop it at `composeApp/google-services.json`.
4. In **Firebase Console → Project Settings → Service accounts**, generate a private key (JSON). That's the `FCM_SERVICE_ACCOUNT_JSON` value from step 4.

### 7. iOS extras

In `iosApp/Configuration/Config.xcconfig`, set `TEAM_ID` to your Apple Developer team ID (otherwise device builds fail signing).

### 8. (iOS-only fix if missed) SQLite linker

Already wired in `composeApp/build.gradle.kts`. If you ever see `Undefined symbol '_sqlite3_*'`, ensure this is present:

```kotlin
iosTarget.binaries.framework {
    baseName = "ComposeApp"
    isStatic = true
    linkerOpts("-lsqlite3")
}
```

---

## Build & run

### Android

```bash
./gradlew :composeApp:assembleDebug
```

Or hit **Run** in Android Studio with a connected device / emulator.

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode (or use Android Studio's iOS run configuration), pick a simulator/device, and Run.

---

## Project layout

```
ChatRoom/
├── composeApp/                       # KMP shared module + Android app
│   ├── src/
│   │   ├── commonMain/kotlin/com/fahim/chatroom/
│   │   │   ├── core/                 # DI, db, dispatchers, errors, logging, config
│   │   │   ├── data/                 # repositories, DTOs, mappers
│   │   │   ├── domain/               # use cases, models, repository interfaces
│   │   │   ├── presentation/         # Compose screens, viewmodels, design system
│   │   │   └── App.kt                # composable root + routing
│   │   ├── androidMain/              # Android-specific actuals + FCM service
│   │   ├── iosMain/                  # iOS-specific actuals + entry point
│   │   └── commonMain/sqldelight/    # SQLDelight schema
│   ├── google-services.json          # (gitignored) Firebase config
│   └── build.gradle.kts
├── iosApp/                           # SwiftUI host for ComposeUIViewController
├── supabase/
│   ├── migrations/                   # SQL schema + RLS + realtime policies
│   └── functions/notify-message/     # Edge function for push fan-out
├── gradle/libs.versions.toml         # Version catalog
├── local.properties.example          # Template for required secrets
└── README.md
```

---

## Security model

- **No service_role key on the client.** Only the publishable anon JWT.
- **RLS everywhere.** All access via membership-based policies (`is_room_member`, `is_room_admin`).
- **Realtime authorization.** Private channels (`room:<id>`) gated by `realtime.messages` policies that re-check membership per subscriber.
- **Edge function auth.** `notify-message` rejects any webhook call without the shared `WEBHOOK_SECRET`.
- **No plaintext logs.** Tokens, secrets, and message content never go through `AppLogger`.
- **Secure storage abstractions** isolate keychain (iOS) and EncryptedSharedPreferences (Android) behind a common interface.
- **E2EE-ready.** Message body is treated as opaque content; the wire format and storage layer don't depend on it being plaintext.

---

## Roadmap

- [ ] iOS push notifications end-to-end (APNs token → FCM bridge or direct APNs)
- [ ] Realtime room-list updates (currently pull-to-refresh + foreground refresh)
- [ ] Message edit/delete UI
- [ ] Read receipts
- [ ] Typing indicators
- [ ] E2EE for message bodies
- [ ] Image / file attachments

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `Undefined symbol '_sqlite3_*'` on iOS | Missing `-lsqlite3` linker flag | Check `composeApp/build.gradle.kts` `iosTarget.binaries.framework` block |
| `Unable to locate a Java Runtime` | No system JDK | `brew install --cask zulu@21`, then `JAVA_HOME` in `~/.zshenv` |
| Android notifications silent | Webhook returning 401 | Confirm `WEBHOOK_SECRET` matches between edge function env var and webhook `Authorization` header |
| New room not appearing for invited user | Realtime not subscribed to `room_members` | Pull-to-refresh on rooms list, or foreground the app |
| iOS device build fails signing | `TEAM_ID` empty in xcconfig | Set in Xcode → Signing & Capabilities |

---

## License

TBD.