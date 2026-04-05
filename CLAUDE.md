# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build                        # Full build (all modules)
./gradlew :app:assembleDebug           # Debug APK
./gradlew :feature:streak:domain:test  # Run tests for a single module
./gradlew test                         # Run all unit tests
./gradlew lint                         # Run Android lint
```

## Architecture

KiddoStreak is an Android streak-tracking app using **Clean Architecture** with **MVI** presentation pattern, **Koin** for DI, and **Jetpack Compose** for UI.

### Module Layout (14 modules)

```
app                          — Application entry, navigation host, Koin module assembly
core/
  domain                     — Result<D,E>, DataError, Error types (pure Kotlin JVM)
  data                       — DataStore factory & JSON serializer
  presentation               — UiText, ObserveAsEvents, error-to-UI mapping
  design-system              — Material3 theme, shared composables (ContributionGrid, ColorPicker)
  notifications              — Notification channel setup
feature/
  streak/{domain,data,presentation}  — Main streak feature (3-layer split)
  settings/{domain,data,presentation} — Settings feature (3-layer split)
  widget                     — Glance app widgets
```

### Dependency Rules

- `domain` modules are **pure Kotlin JVM** — no Android dependencies
- `data` modules depend on their sibling `domain` + `core:data`
- `presentation` modules depend on their sibling `domain` + `core:presentation` + `core:design-system`
- `app` depends on all modules and assembles Koin modules in `AppModule.kt`
- Features never depend on other features directly; cross-feature communication goes through `app` navigation callbacks

### Convention Plugins (build-logic)

Applied via `plugins { alias(libs.plugins.kiddostreak.xxx) }`:

| Plugin ID | What it does |
|-----------|-------------|
| `kiddostreak.android.application` | Android app module (API 26–36, JVM 17) |
| `kiddostreak.android.library` | Android library (auto-generates namespace from module path) |
| `kiddostreak.android.feature` | Library + Compose + Koin + Navigation + Lifecycle |
| `kiddostreak.domain.module` | Kotlin JVM + Coroutines Core + KotlinX DateTime |
| `kiddostreak.compose` | Compose compiler + BOM + Material3 + Animation |
| `kiddostreak.koin` | Koin BOM + Core + Android + Compose |
| `kiddostreak.kotlinx.serialization` | Serialization plugin + JSON library |

### MVI Pattern (per screen)

Each screen has four files:
- **State** — `@Stable` data class with UI state
- **Action** — Sealed interface for user intents
- **Event** — Sealed interface for one-time side effects (navigation, snackbars)
- **ViewModel** — `StateFlow<State>` + `Channel<Event>`, dispatches via `onAction()`

Composables split into **Root** (gets ViewModel via `koinViewModel()`, collects state/events) and **Screen** (pure presentation, takes state + `onAction` lambda).

### Navigation

Type-safe routes using `@Serializable` data objects/classes. Each feature defines:
- Route classes in a `navigation/` package
- A `NavGraphBuilder` extension function wiring routes to Root composables
- `AppNavHost.kt` in `:app` composes all feature nav graphs

### DI (Koin)

- Each `data` module exports a Koin `module {}` (repositories, DataStore instances)
- Each `presentation` module exports a Koin `module {}` (use cases as `factory`, ViewModels as `viewModel`)
- All assembled in `app/di/AppModule.kt` → `appModules` list
- `KiddoStreakApplication` calls `startKoin { modules(appModules) }`

### Data Layer

- **DataStore** with JSON serialization (via `KiddoDataStoreFactory.create()`)
- DTOs (`*Dto`) are serialized; domain models are not
- Mappers (`toStreak()`, `toDto()`) in dedicated mapper files
- Repository interfaces in domain, implementations in data

### Error Handling

- `Result<D, E : Error>` sealed interface (Success/Failure) in `core:domain`
- `DataError.Local` enum: `DISK_FULL`, `NOT_FOUND`, `UNKNOWN`
- `EmptyResult<E>` typealias for `Result<Unit, E>`
- Extension functions: `map()`, `onSuccess()`, `onFailure()`, `asEmptyResult()`

### Testing Stack

JUnit5, Turbine (Flow testing), AssertK (assertions), Coroutines Test, Compose UI Test.
