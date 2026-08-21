# 📄 Project Context — MusicHome

## 🎯 System Overview
**Music Home** is a Software-Defined Digital Audio Player (DAP) designed for Android devices. It replaces traditional consumer music app paradigms with a hardware-inspired appliance interface, transparent audio signal flow telemetry, and persistent playback state recovery.

---

## 🏛️ Architecture & Module Structure

The project is structured into clean, modular Android library modules:

```
MusicHome/
├── app/          ← Application entry point, main navigation shell & UI wiring
├── core/         ← Core abstractions, DeviceState, Database, Repository & DI
├── ui/           ← Reusable hardware-inspired UI components & theme system
├── media/        ← Audio engine integration (Media3 ExoPlayer), telemetry & pipeline
└── organizer/    ← Library maintenance, tag editing, metadata scanning & tools
```

### Module Boundary Principles
- **`:core`**: Holds shared domain models, Room persistence entities (`PlaybackStateEntity`), and `DeviceState` contracts.
- **`:media`**: Encapsulates `ExoPlayer` audio engine logic, gapless playback, ReplayGain metadata processing, and high-honesty audio telemetry.
- **`:ui`**: Provides design system components (`WalkmanTheme`, persistent playback strip, hardware dock).
- **`:organizer`**: Implements library health metrics, tag editing, and background delta-indexing pipelines.
- **`:app`**: Assembles modules and controls top-level navigation.

---

## 🔄 L.I.S.A. Engineering Workflow Cycle

All features, refactorings, and fixes follow the L.I.S.A. 6-step engineering cycle:

1. **OBSERVE**: Inspect code, verify logs, check environment & module boundaries.
2. **UNDERSTAND**: Analyze data flows, constraints, and hardware telemetry requirements.
3. **PLAN**: Outline implementation, evaluate risks, and update documentation or ADRs.
4. **EXECUTE**: Write clean Kotlin code using established abstractions & explicit error handling.
5. **VERIFY**: Run `./gradlew test` and `./gradlew assembleDebug` to guarantee build health.
6. **DOCUMENT**: Preserve system knowledge in `PROJECT_CONTEXT.md`, `ARCHITECTURE_DECISIONS.md`, and `CHANGELOG.md`.

---

## 📋 Quality & Verification Standard

Before declaring any change complete:
- `./gradlew test` must pass clean.
- `./gradlew assembleDebug` must compile cleanly without errors.
- Any architectural modifications must be documented in `docs/PROJECT_CONTEXT.md` or `docs/ARCHITECTURE_DECISIONS.md`.
