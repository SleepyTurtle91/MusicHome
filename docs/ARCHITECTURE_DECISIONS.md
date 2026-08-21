# 🏛️ Architecture Decision Records (ADRs)

This document records key architectural decisions made in the development of **Music Home**.

---

## ADR-001: Hardware-Inspired Software DAP Architecture

### Context
Consumer Android music players treat audio playback as a secondary software tab within a generic touch interface. Audiophile hardware players (DAPs) prioritize instant physical controls, transparent audio signal paths, and session state persistence.

### Decision
Music Home adopts a **Software-Defined DAP Platform** architecture:
1. **DeviceState Machine**: Centralized state model reflecting source, engine, output, and verification status.
2. **Persistent Playback Session**: Hardware-style resume on app launch restoring exact position, queue revision, and active tab.
3. **High-Honesty Telemetry**: Audio pipeline metadata is strictly classified into `VERIFIED` (hardware confirmed), `ESTIMATED` (inferred), or `UNKNOWN`.

### Status
Accepted & Implemented in v2.0.0.

---

## ADR-002: Modular Multi-Module Architecture

### Context
To maintain separation of concerns between media playback, UI design system, metadata management, and core persistence, code separation is required.

### Decision
Split codebase into distinct Android library modules:
- `:core`: Data repositories, Room database, domain state.
- `:media`: ExoPlayer engine wrapper, telemetry pipeline, audio focus.
- `:ui`: Theme tokens, DAP hardware dock, playback strip.
- `:organizer`: Metadata indexing engine v2, tag editor, duplicate finder.
- `:app`: Top-level composition and navigation.

### Status
Accepted & Implemented in v2.0.0 / v2.5.0.

---

## ADR-003: Persist-First Queue Mutation Lifecycle

### Context
Unexpected app termination during queue modifications (reordering, removing items) can corrupt or desynchronize UI state with persisted state.

### Decision
All queue mutations follow a strict **Persist-First** flow:
`User Action → Increment Revision → Write to Room DB → StateFlow Emit → UI Update`.

### Status
Accepted & Implemented in v2.5.0.
