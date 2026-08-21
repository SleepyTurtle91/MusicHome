# 📐 Module Architecture & Invariants

This document defines the module structure, dependency graph, and strict layer boundaries of the **Music Home** platform.

---

## 🏛️ Module Architecture Diagram

```
                       ┌─────────────────────────┐
                       │          :app           │
                       └───────────┬─────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         │                         │                         │
         ▼                         ▼                         ▼
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│       :ui       │──────►│     :media      │──────►│     :core       │
└────────┬────────┘       └─────────────────┘       └─────────────────┘
         │                                                   ▲
         └───────────────────────────────────────────────────┘
                                   ▲
┌─────────────────┐                │
│   :organizer    │────────────────┘
└─────────────────┘
```

---

## 📦 Module Responsibilities

### 1. `:app` (Application Gateway)
- **Role**: Application entry point (`MainActivity`, `BootReceiver`).
- **Dependencies**: `:ui`, `:media`, `:core`, `:organizer`.
- **Rules**: Contains zero domain logic or raw data operations. Acts purely as the dependency injection and navigation container.

### 2. `:ui` (Hardware Interface & Navigation Shell)
- **Role**: DAP hardware interface, 5-button hardware dock, `WalkmanTheme`, persistent playback strip, and `DeviceState` consumption.
- **Dependencies**: `:media`, `:core`.
- **Rules**: Must use Unidirectional Data Flow (UDF). Views consume `StateFlow` from ViewModels and dispatch intent events.

### 3. `:media` (Audio Engine & Telemetry)
- **Role**: Media3 `ExoPlayer` integration (`MusicPlaybackService`), `EqualizerManager`, transition strategies, and high-honesty telemetry extraction (`AudioTelemetry`).
- **Dependencies**: `:core`.
- **Rules**: Encapsulates all Android `MediaPlayer`/`ExoPlayer` framework calls. Exposes generic playback controllers to `:ui`.

### 4. `:core` (Domain Models, Database & Persistence)
- **Role**: Single source of truth. Room database (`MusicDatabase`), DAOs, `LocalMediaRepository`, domain models (`Song`, `Album`, `Artist`, `PlaybackQueue`).
- **Dependencies**: None (pure data/domain layer).
- **Rules**: Cannot import Android UI or Jetpack Compose classes.

### 5. `:organizer` (Library Maintenance & Tools)
- **Role**: Secondary feature module for library health analysis, duplicate detection, and metadata tag editing.
- **Dependencies**: `:core`.
- **Rules**: Isolated maintenance tools. Code must not leak into core playback paths.

---

## 🚫 Dependency Invariants

1. **Core Isolation**: `:core` MUST NEVER depend on any other module (`:core -> *` is strictly forbidden).
2. **Media Decoupling**: `:media` MUST NEVER depend on `:ui` or `:app`.
3. **No Circular Dependencies**: All dependencies flow downwards toward `:core`.
