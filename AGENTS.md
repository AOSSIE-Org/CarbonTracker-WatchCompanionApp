# CarbonTracker Watch Companion

Wear OS companion app for CarbonTracker. Kotlin + Jetpack Compose for Wear OS. Local-first, privacy-focused fitness tracker.
Exercise tracking: Health Services (`ExerciseClient`). Local storage: Room. Phone communication: Wearable Message API. No backend, no cloud.

## Commands

Run from the `wear/` directory.

Build: `./gradlew assembleDebug`
Install on device/emulator: `./gradlew installDebug`
Lint: `./gradlew lint`

## Code Style

Follow the official Kotlin coding conventions. Don't introduce new lint warnings.

Prefer `val` over `var`. Use immutable state where possible.
Use coroutines for async work, never block the main thread.
Cancel coroutine scopes and unregister listeners when a service or component is destroyed.

Extract large composables into smaller ones, don't let a single composable handle multiple concerns.
Keep UI state hoisted, composables should receive state and callbacks rather than own business logic.

Shared state is exposed as a read-only `StateFlow` backed by a private `MutableStateFlow`:

```kotlin
// State pattern used in this project (ExerciseStateHolder):
object ExerciseStateHolder {
    private val _state = MutableStateFlow<ExerciseState?>(null)
    val state: StateFlow<ExerciseState?> = _state
}
```

Database access goes through the singleton:

```kotlin
// Room pattern used in this project:
val dao = AppDatabase.getInstance(context).activityDao()
```

## Architecture

`wear/app/src/main/java/org/aossie/carbontracker/`

- `presentation/` - Compose UI: `screens/`, `theme/`, `ui/`, `MainActivity.kt`
- `services/` - `ExerciseService` (foreground service), `ExerciseManager`, `HealthServicesManager`, `WearListenerService` (phone messaging)
- `providers/` - `ExerciseStateHolder` (shared exercise state), `HealthClientProvider`
- `data/` - `database/` (Room: `ActivityDao`, `ActivityEntity`, `AppDatabase`) and `ActivitiesData.kt`
- `CarbonTrackerApp.kt` - Application class

`AppDatabase` is a singleton, never build the Room database directly, always go through `getInstance()`.

Never call Health Services APIs directly from composables, go through `ExerciseManager` / `HealthServicesManager`.

Sync with the phone uses `isSynced` on `ActivityEntity`. Only mark records as synced after the phone confirms receipt.

## Testing

No tests exist yet. Don't assume a testing pattern; ask before writing tests in a specific style.

## Boundaries

Never commit API keys, secrets, or signing keys.
Never add cloud storage, analytics, or telemetry that transmits user data off-device.
Don't log location, health, or activity data to Logcat.
Only request permissions the app actually needs, and keep `PRIVACY.md` accurate if they change.
This app is local-first by design, data only leaves the watch to sync with the paired phone via data layer api.

## Git

Conventional commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`.
Branch format: `feature/short-description` or `fix/short-description`.
