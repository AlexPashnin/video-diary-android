# Android Application — Implementation Plan

## Technology Stack

| Category | Technology | Justification |
|---|---|---|
| **Language** | Kotlin 2.0+ | First-class Android language, matches backend codebase |
| **Min SDK** | 26 (Android 8.0) | Covers ~95% of devices, allows modern APIs |
| **Target SDK** | 35 (Android 15) | Latest stable target |
| **Build** | Gradle (Kotlin DSL) + Version Catalogs | Modern dependency management |
| **Architecture** | MVVM + Clean Architecture (3-layer) | Domain / Data / Presentation separation |
| **DI** | Hilt (Dagger under the hood) | Google-recommended, lifecycle-aware |
| **UI** | Jetpack Compose + Material 3 | Declarative, modern toolkit |
| **Navigation** | Compose Navigation (type-safe) | Single-Activity architecture |
| **Networking** | Retrofit 2 + OkHttp 4 | Industry standard, interceptor chain for JWT |
| **Serialization** | Kotlinx Serialization | Kotlin-native, no reflection |
| **Image Loading** | Coil 3 (Compose) | Kotlin-first, coroutine-native |
| **Video Player** | Media3 ExoPlayer | Google's modern media player, HLS/DASH/MP4 |
| **Video Recording** | CameraX | Jetpack camera abstraction |
| **File Upload** | OkHttp + presigned URL PUT | Direct-to-MinIO uploads via presigned URLs |
| **WebSocket** | OkHttp WebSocket + STOMP | Real-time compilation progress |
| **Local DB** | Room | Offline caching, SQLite abstraction |
| **DataStore** | Jetpack DataStore (Proto) | Token storage, user preferences |
| **Background Work** | WorkManager | Reliable video uploads that survive process death |
| **Coroutines** | Kotlin Coroutines + Flow | Async programming, reactive streams |
| **Push Notifications** | Firebase Cloud Messaging (FCM) | Matches backend notification-service |
| **Analytics/Crash** | Firebase Crashlytics + Analytics | Production monitoring |
| **Testing** | JUnit 5, Turbine, MockK, Compose Testing | Unit + UI tests |
| **CI/CD** | GitHub Actions | Automated builds, tests, signing |

---

## Project Structure

```
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   └── kotlin/com/videodiary/android/
│       ├── VideoDiaryApp.kt                 // Application class (Hilt entry point)
│       ├── MainActivity.kt                  // Single Activity
│       │
│       ├── di/                              // Hilt modules
│       │   ├── NetworkModule.kt             // Retrofit, OkHttp, interceptors
│       │   ├── DatabaseModule.kt            // Room database
│       │   ├── RepositoryModule.kt          // Repository bindings
│       │   └── DataStoreModule.kt           // DataStore instances
│       │
│       ├── data/
│       │   ├── remote/
│       │   │   ├── api/                     // Retrofit interfaces
│       │   │   │   ├── AuthApi.kt
│       │   │   │   ├── VideoApi.kt
│       │   │   │   ├── ClipApi.kt
│       │   │   │   ├── CompilationApi.kt
│       │   │   │   └── StorageApi.kt
│       │   │   ├── dto/                     // Network DTOs
│       │   │   │   ├── auth/
│       │   │   │   ├── video/
│       │   │   │   ├── clip/
│       │   │   │   └── compilation/
│       │   │   ├── interceptor/
│       │   │   │   ├── AuthInterceptor.kt   // Adds Bearer token
│       │   │   │   └── TokenRefreshAuthenticator.kt  // Auto-refresh on 401
│       │   │   └── websocket/
│       │   │       └── StompClient.kt       // Compilation progress
│       │   ├── local/
│       │   │   ├── db/
│       │   │   │   ├── VideoDiaryDatabase.kt
│       │   │   │   ├── dao/
│       │   │   │   └── entity/
│       │   │   └── datastore/
│       │   │       └── TokenDataStore.kt
│       │   └── repository/                  // Repository implementations
│       │       ├── AuthRepositoryImpl.kt
│       │       ├── VideoRepositoryImpl.kt
│       │       ├── ClipRepositoryImpl.kt
│       │       └── CompilationRepositoryImpl.kt
│       │
│       ├── domain/
│       │   ├── model/                       // Domain models
│       │   │   ├── User.kt
│       │   │   ├── Video.kt
│       │   │   ├── Clip.kt
│       │   │   ├── Compilation.kt
│       │   │   └── CalendarDay.kt
│       │   ├── repository/                  // Repository interfaces
│       │   │   ├── AuthRepository.kt
│       │   │   ├── VideoRepository.kt
│       │   │   ├── ClipRepository.kt
│       │   │   └── CompilationRepository.kt
│       │   └── usecase/                     // Business logic
│       │       ├── auth/
│       │       ├── video/
│       │       ├── clip/
│       │       └── compilation/
│       │
│       └── presentation/
│           ├── navigation/
│           │   ├── NavGraph.kt
│           │   ├── Screen.kt               // Route definitions
│           │   └── BottomNavBar.kt
│           ├── theme/
│           │   ├── Theme.kt
│           │   ├── Color.kt
│           │   └── Type.kt
│           ├── common/                      // Shared composables
│           │   ├── LoadingIndicator.kt
│           │   ├── ErrorDialog.kt
│           │   └── VideoPlayer.kt
│           └── screens/
│               ├── auth/
│               │   ├── LoginScreen.kt
│               │   ├── LoginViewModel.kt
│               │   ├── RegisterScreen.kt
│               │   └── RegisterViewModel.kt
│               ├── home/
│               │   ├── HomeScreen.kt        // Calendar view
│               │   └── HomeViewModel.kt
│               ├── upload/
│               │   ├── RecordScreen.kt      // Camera recording
│               │   ├── UploadScreen.kt      // Gallery picker + upload
│               │   └── UploadViewModel.kt
│               ├── clipselect/
│               │   ├── ClipSelectScreen.kt  // Video scrubbing + 1s selection
│               │   └── ClipSelectViewModel.kt
│               ├── compilation/
│               │   ├── CompilationCreateScreen.kt
│               │   ├── CompilationProgressScreen.kt
│               │   ├── CompilationHistoryScreen.kt
│               │   └── CompilationViewModel.kt
│               ├── player/
│               │   ├── PlayerScreen.kt      // Full-screen video player
│               │   └── PlayerViewModel.kt
│               └── settings/
│                   ├── SettingsScreen.kt
│                   └── SettingsViewModel.kt
```

---

## Implementation Plan — Step-by-Step

### ✅ Phase 1: Project Bootstrap & Core Infrastructure — IMPLEMENTED

#### Step 1 — Project Setup
- Create new Android project with Compose template
- Configure Gradle with version catalogs (`libs.versions.toml`)
- Add all dependencies (Hilt, Retrofit, Room, Coil, Media3, CameraX, etc.)
- Set up Hilt Application class and `@AndroidEntryPoint` on MainActivity
- Configure build variants: `debug` (localhost:8080) and `release` (production URL)
- Set up `.editorconfig`, `ktlint`, `detekt` for code quality

#### Step 2 — Design System & Theme
- Define Material 3 theme (Color, Typography, Shapes)
- Dark mode support from the start
- Create reusable composable atoms: buttons, text fields, cards, loading states, error states
- Define app icon and splash screen (Splash API)

#### Step 3 — Networking Layer
- Set up OkHttp client with logging interceptor (debug only)
- Create `AuthInterceptor` — injects `Authorization: Bearer <token>` and `X-User-Id` headers
- Create `TokenRefreshAuthenticator` — intercepts 401, calls `/auth/refresh`, retries original request
- Set up Retrofit with base URL pointing to API Gateway (`http://10.0.2.2:8080` for emulator)
- Define all 5 Retrofit API interfaces (`AuthApi`, `VideoApi`, `ClipApi`, `CompilationApi`, `StorageApi`)
- Create DTO classes matching backend JSON contracts (using `@Serializable`)
- Create `NetworkModule` Hilt module wiring everything together
- Write DTO mappers: `NetworkDto → DomainModel`

#### Step 4 — Local Storage Layer
- Set up Jetpack DataStore (Proto) for:
  - Access token, refresh token, token expiry
  - User ID, user tier
  - User preferences (watermark position, notifications enabled)
- Set up Room database with entities for offline caching:
  - `VideoEntity`, `ClipEntity`, `CalendarDayEntity`
- Create DAOs with Flow-based queries
- Create `DatabaseModule` Hilt module

#### Step 5 — Repository Layer
- Implement `AuthRepository`: login, register, refresh, logout, token management
- Implement `VideoRepository`: initiate upload, complete upload, list videos, get video details
- Implement `ClipRepository`: select clip, list clips, get calendar, delete clip
- Implement `CompilationRepository`: create, get status, list history, delete
- Each repository combines remote API + local cache (offline-first where applicable)

#### Step 6 — Navigation Shell
- Define `Screen` sealed class with all routes
- Create `NavGraph` with Compose Navigation
- Implement bottom navigation bar (Home / Upload / Compilations / Settings)
- Add auth-gated navigation (redirect to Login if no token)
- Splash screen → check token validity → Home or Login

---

### ✅ Phase 2: Authentication — IMPLEMENTED

#### Step 7 — Login & Registration Screens
- **LoginScreen**: email + password fields, login button, "Create account" link
  - Form validation (email format, password min 8 chars)
  - Loading state, error snackbar
  - On success: store tokens in DataStore, navigate to Home
- **RegisterScreen**: email, password, display name, timezone (auto-detect)
  - Same validation + display name max 100 chars
  - On success: store tokens, navigate to Home
- **LoginViewModel / RegisterViewModel**: handle state, call AuthRepository
- Auto token refresh integrated at the OkHttp level (Step 3)

#### Step 8 — Session Management
- On app launch: read token from DataStore, validate expiry
- If expired: attempt silent refresh via `/auth/refresh`
- If refresh fails: redirect to Login, clear local tokens
- Logout: call `/auth/logout`, clear DataStore, clear Room cache, navigate to Login

---

### ✅ Phase 3: Home & Calendar — IMPLEMENTED

#### Step 9 — Calendar Home Screen
- Fetch calendar data via `GET /clips/calendar?year=YYYY&month=MM`
- Display monthly calendar grid:
  - Days with clips: highlighted with a small thumbnail or colored dot
  - Days without clips: neutral
  - Today: special border
- Swipe left/right to change months
- Tap a day with a clip → navigate to clip playback
- Tap today (no clip yet) → navigate to Upload screen
- Cache calendar data in Room for offline viewing

---

### ✅ Phase 4: Video Upload Flow — IMPLEMENTED

#### Step 10 — Video Capture (CameraX)
- **RecordScreen**: full-screen camera preview
  - Front/back camera toggle
  - Record button (hold or tap to start/stop)
  - Preview recorded video before confirming
  - Save to temp file

#### Step 11 — Video Selection (Gallery)
- **UploadScreen**: option to pick from gallery (PhotoPicker API)
  - Validate: must be video, max 200 MB
  - Show selected video preview
  - Date picker (default: today) — user selects which day this video is for

#### Step 12 — Upload Pipeline (WorkManager)
- On confirm:
  1. Call `POST /videos/upload/initiate` → get `uploadUrl` + `videoId`
  2. Enqueue `UploadWorker` (WorkManager) for reliable background upload:
     - PUT binary video to presigned URL
     - Show progress notification (0-100%)
     - Retry on network failure (exponential backoff)
  3. On upload complete: call `POST /videos/{videoId}/upload/complete`
  4. Poll `GET /videos/{videoId}` until status = `READY`
  5. Auto-navigate to Clip Selection screen
- Show upload progress in a persistent notification + in-app indicator

---

### ✅ Phase 5: Clip Selection (Core UX) — IMPLEMENTED

#### Step 13 — Video Scrubbing & Clip Selection Screen
- **ClipSelectScreen**: the most important UX screen
  - Load video player (ExoPlayer/Media3) with the processed video
  - Display sprite sheet as a visual timeline scrubber below the video
  - User drags through the timeline to find their moment
  - 1-second highlight window shows the selected range
  - "Select This Moment" button confirms the selection
  - Optional: waveform overlay on the timeline
- On confirm:
  1. Call `POST /clips/select` with `videoId`, `date`, `startTimeSeconds`
  2. Poll `GET /clips/{clipId}` until status = `READY`
  3. Show success animation, navigate to Home
  4. Original video is automatically deleted by the backend

---

### Phase 6: Compilations

#### Step 14 — Create Compilation Screen
- **CompilationCreateScreen**:
  - Date range picker (start date → end date)
  - Show which days in the range have clips (mini calendar or list)
  - Quality selector: 480p / 720p / 1080p / 4K
  - Watermark position selector (visual preview)
  - Clip count summary
  - "Create Compilation" button
- On confirm: call `POST /compilations/create`

#### Step 15 — Compilation Progress Screen
- **CompilationProgressScreen**:
  - Circular progress indicator with percentage
  - Current clip / total clips counter
  - Real-time updates via WebSocket (STOMP over `/ws`)
  - Fallback: poll `GET /compilations/{id}/status` every 3 seconds
  - On complete: show "Ready!" with play and download buttons

#### Step 16 — Compilation History & Playback
- **CompilationHistoryScreen**:
  - List of past compilations with status, date range, clip count
  - Completed: tap to play or download
  - Failed: tap to retry
  - Swipe to delete
- **Download**: get presigned URL via `POST /storage/presigned-url/download`, then save to device Downloads folder using MediaStore

---

### Phase 7: Video Playback

#### Step 17 — Full-Screen Player
- **PlayerScreen**: Media3 ExoPlayer in full screen
  - Play/pause, seek bar, full-screen toggle
  - Date watermark visible on compilation playback
  - Share button (share via Android Sharesheet)
  - Download button for compilations

---

### Phase 8: Settings & Profile

#### Step 18 — Settings Screen
- **SettingsScreen**:
  - User profile info (display name, email, tier)
  - Default watermark position preference
  - Notifications toggle
  - Dark mode toggle
  - Storage usage summary
  - Quota info (videos/day, max size, compilation limits)
  - Logout button
  - App version, licenses

---

### Phase 9: Push Notifications

#### Step 19 — FCM Integration
- Integrate Firebase Cloud Messaging
- Register FCM token with backend (`POST /notifications/register-device` — to be added to backend)
- Handle notification types:
  - Video processing complete → deep link to Clip Selection
  - Clip extraction complete → deep link to Home
  - Compilation ready → deep link to Player
  - Compilation expiring soon → deep link to Compilation History

---

### Phase 10: Polish & Production Readiness

#### Step 20 — Error Handling & Edge Cases
- Global error handler for network failures (no connectivity banner)
- Retry mechanisms for all API calls
- Handle presigned URL expiration (re-request if 403)
- Handle token expiration race conditions
- Empty states for: no videos, no clips, no compilations
- Rate limiting handling (429 responses)

#### Step 21 — Offline Support
- Cache calendar data, clip list, compilation history in Room
- Show cached data when offline with "offline" indicator
- Queue uploads for when connectivity returns (WorkManager)
- Optimistic UI updates with rollback on failure

#### Step 22 — Performance Optimization
- Lazy loading and pagination for all list screens
- Image caching with Coil (sprite sheets, thumbnails)
- Video preloading for smooth scrubbing
- ProGuard/R8 configuration for release builds
- Baseline Profiles for startup performance

#### Step 23 — Testing
- **Unit Tests**: ViewModels, Repositories, UseCases, Mappers (JUnit 5 + MockK + Turbine)
- **Integration Tests**: Room DAOs, DataStore, API with MockWebServer
- **UI Tests**: Compose Testing (critical flows: login, upload, clip select, compile)
- **E2E Tests**: Full flow against running backend (optional, CI)

#### Step 24 — CI/CD & Release
- GitHub Actions workflow:
  - Build → Lint → Test → Assemble APK/AAB
  - Signing configuration for release builds
  - Upload to Google Play (internal track) via Fastlane or Gradle Play Publisher
- Firebase App Distribution for beta testing
- Crashlytics + Analytics for production monitoring
