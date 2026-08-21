# 🧪 Testing Strategy & Verification Quality Gates

This document defines what "done" means for features, bug fixes, and refactorings in **MusicHome**.

---

## 🎯 Verification Matrix

| Layer | Primary Verification Tool | Verification Criteria |
|---|---|---|
| Domain / Core | Unit Tests (`./gradlew test`) | Business logic & entity state rules pass. |
| Persistence | Room Migration & DB Tests | `SongDao` and `PlaybackStateEntity` CRUD operations. |
| Audio Engine | Service Unit Tests | `TransitionStrategy` and `PlaybackQueue` handling. |
| UI & Theme | Manual & Preview Verification | Compose components render correctly under `WalkmanTheme`. |
| Whole App Build | Gradle Assembly (`./gradlew assembleDebug`) | Complete project builds clean without errors. |

---

## 🛑 Quality Rules

1. **Clean Build Gate**: A feature is incomplete until `./gradlew assembleDebug` succeeds cleanly.
2. **No Symptom Swallowing**: Never wrap broken code in silent `try/catch` or return dummy zeroed values to bypass a test failure.
3. **Documentation Updated**: Significant architectural or feature changes MUST update `ROADMAP.md`, `FEATURES.md`, and relevant `docs/` files.
