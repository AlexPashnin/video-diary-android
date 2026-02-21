# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status
A mobile-first video diary application where users upload daily videos, select a meaningful 1-second clip from each day, and automatically create compilations showing their life over time. Each second in the compilation displays the date it was recorded, creating a visual timeline of memories.

**Implementation status:**
- ✅ Phase 1 — Project Bootstrap & Core Infrastructure (Steps 1–6)
- ✅ Phase 2 — Authentication (Steps 7–8)
- ✅ Phase 3 — Home & Calendar (Step 9)
- ✅ Phase 4 — Video Upload Flow (Steps 10–12)
- ✅ Phase 5 — Clip Selection (Step 13)
- ✅ Phase 6 — Compilations (Steps 14–16)
- ✅ Phase 7 — Video Playback (Step 17)
- ✅ Phase 8 — Settings & Profile (Step 18)
- ⏳ Phase 9 — Push Notifications (Step 19)
- ⏳ Phase 10 — Polish & Production Readiness (Steps 20–24)

Specification documents:
- `ANDROID_PLAN.md` — 24-step implementation roadmap
- `requirements.md` — Functional and non-functional requirements
- `openapi.yaml` — Backend API specification

## Build Commands

The project uses Gradle with Kotlin DSL:

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run all unit tests
./gradlew testDebugUnitTest      # Run debug unit tests
./gradlew connectedAndroidTest   # Run instrumentation tests
./gradlew lint                   # Run Android lint
./gradlew detekt                 # Run Detekt static analysis
./gradlew ktlintCheck            # Run ktlint
./gradlew ktlintFormat           # Auto-format with ktlint
```

Run a single test class:
```bash
./gradlew test --tests "com.example.videodiary.LoginViewModelTest"
```

## Architecture

**Pattern:** MVVM + Clean Architecture with 3 layers:

```
presentation/ → domain/ → data/
```

- **data/**: Remote APIs (Retrofit), local storage (Room + DataStore), repository implementations
- **domain/**: Framework-agnostic models, repository interfaces, use cases
- **presentation/**: Jetpack Compose screens, ViewModels (StateFlow), navigation

**Module structure within `app/`:**
```
di/                    # Hilt modules (Network, Database, Repository, DataStore)
data/
  remote/api/          # Retrofit interfaces
  remote/dto/          # API request/response models
  remote/interceptor/  # AuthInterceptor (JWT injection), TokenRefreshAuthenticator (401 handling)
  remote/websocket/    # STOMP client for compilation progress
  local/db/            # Room database, DAOs, entities
  local/datastore/     # Proto DataStore for tokens and preferences
  repository/          # Repository implementations
domain/
  model/               # Domain models (User, Video, Clip, Compilation, CalendarDay)
  repository/          # Repository interfaces
  usecase/             # Business logic organized by feature
presentation/
  navigation/          # Compose Navigation graph, Screen sealed class, BottomNavBar
  theme/               # Material 3 theme, colors, typography
  common/              # Shared composables (VideoPlayer, LoadingIndicator, ErrorDialog)
  screens/             # Per-feature Screen + ViewModel pairs
```

## Key Technology Decisions

| Concern | Technology |
|---------|-----------|
| DI | Hilt |
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation (type-safe), single-activity |
| Networking | Retrofit 2 + OkHttp 4 + Kotlinx Serialization |
| Image/Video loading | Coil 3 (Compose) |
| Video playback | Media3 ExoPlayer |
| Video recording | CameraX |
| Local DB | Room |
| Preferences/Tokens | Jetpack DataStore (Proto) |
| Background uploads | WorkManager |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 5, MockK, Turbine (Flow), Compose Testing |

## Authentication Flow

- Bearer JWT tokens; access token expires in 15 min, refresh token in 7 days
- `AuthInterceptor` injects `Authorization: Bearer <token>` and `X-User-Id` headers on every request
- `TokenRefreshAuthenticator` intercepts 401 responses, silently refreshes tokens, and retries the original request
- Tokens stored in Proto DataStore
- On app launch: check token validity → silent refresh if expired → redirect to Login on failure

## Video Upload Flow

1. `POST /videos/initiate-upload` → receive presigned S3 URL + `videoId`
2. PUT file directly to presigned URL (expires in 30 min) — bypasses backend for large file transfer
3. `POST /videos/{videoId}/upload-complete` to confirm
4. Backend worker processes video, generates sprite sheet; status → `READY`
5. WorkManager handles upload to survive app backgrounding/restart

## Clip Selection Flow

1. User scrubs video timeline using sprite sheet visualization
2. Selects a start time → `POST /clips/select` with `videoId`, `date`, `startTimeSeconds`
3. Backend extracts 1-second clip, deletes original video
4. Clip is stored and available for compilations

## Compilation Flow

1. User selects clip date range + quality (480p/720p/1080p/4K) + watermark position
2. `POST /compilations/create` → backend queues processing
3. Progress tracked via WebSocket (STOMP) or polling `GET /compilations/{id}/status`
4. Compiled video available for 7 days

## Build Variants

- `debug`: Base URL = `http://localhost:8080`
- `release`: Base URL = production URL

## State Management Pattern

ViewModels expose `StateFlow<ScreenState>` sealed classes. Screens collect state and render accordingly:

```kotlin
private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
val state: StateFlow<LoginState> = _state.asStateFlow()
```

## Testing Approach

- **ViewModels/UseCases/Repositories**: JUnit 5 + MockK + Turbine for Flow assertions
- **Room DAOs**: In-memory test database
- **Retrofit**: MockWebServer
- **UI**: Compose Testing library for critical flows (login, upload, clip selection, compilation)
