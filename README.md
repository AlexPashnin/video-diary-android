# video-diary-android

A mobile-first video diary app. Upload a daily video, pick a 1-second highlight clip, and automatically compile them into a timeline of your life — with each second stamped with the date it was recorded.

## Prerequisites

- **Android SDK** — install via [Android Studio](https://developer.android.com/studio) or the standalone command-line tools
- **JDK 17**
- **Gradle wrapper** — the `gradle-wrapper.jar` binary is not checked in; generate it once before building:
  ```bash
  gradle wrapper --gradle-version 8.9
  ```
- **`local.properties`** — create this file at the repo root and point it at your SDK:
  ```
  sdk.dir=/path/to/your/Android/Sdk
  ```
- **`google-services.json`** — place your Firebase config file at `app/google-services.json` (not committed to the repo)

## Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing config)
./gradlew assembleRelease
```

The debug build targets `http://10.0.2.2:8080` (Android emulator localhost). The release build targets the production API URL defined in `app/build.gradle.kts`.

## Testing

```bash
# All unit tests
./gradlew test

# Debug unit tests only
./gradlew testDebugUnitTest

# Single test class
./gradlew test --tests "com.videodiary.android.LoginViewModelTest"

# Instrumentation tests (requires connected device or emulator)
./gradlew connectedAndroidTest
```

## Code Quality

```bash
# Run all checks
./gradlew lint detekt ktlintCheck

# Auto-format with ktlint
./gradlew ktlintFormat

# Static analysis
./gradlew detekt
```