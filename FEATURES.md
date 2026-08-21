# 🧩 MusicHome Feature Registry

This catalog defines the formal contracts, ownership, states, and verification criteria for all features in **MusicHome**.

---

## Feature Index

| Feature ID | Name | Module | Status | Priority |
|---|---|---|---|---|
| `MH-FEAT-001` | Central DeviceState Machine | `:ui`, `:core` | IMPLEMENTED | P0 |
| `MH-FEAT-002` | Media Store & File Scanner | `:core` | IMPLEMENTED | P0 |
| `MH-FEAT-003` | Persistent Playback Queue | `:core`, `:media` | IMPLEMENTED | P0 |
| `MH-FEAT-004` | High-Honesty Audio Telemetry | `:ui`, `:media` | IMPLEMENTED | P0 |
| `MH-FEAT-005` | OLED-Safe Desk Mode | `:ui` | IMPLEMENTED | P1 |
| `MH-FEAT-006` | Global Search Engine | `:ui`, `:core` | IMPLEMENTED | P1 |
| `MH-FEAT-007` | Library Tools & Duplicate Finder | `:organizer`, `:core`| IMPLEMENTED | P1 |
| `MH-FEAT-008` | Metadata Tag Editor | `:organizer` | IMPLEMENTED | P2 |
| `MH-FEAT-009` | DAP Home Screen | `:ui` | IMPLEMENTED | P0 |

---

## 📋 Feature Specifications

### MH-FEAT-001 — Central DeviceState Machine
- **Status**: `IMPLEMENTED`
- **Module**: `:ui` / `:core`
- **Purpose**: Provide a single, immutable source of truth for physical audio hardware status, current playback, and output signal telemetry.
- **Contract**:
  1. `DeviceState` aggregates playback state, volume, output device, and telemetry.
  2. State changes are exposed as a single `StateFlow<DeviceState>` to UI components.
  3. No UI component may mutate state directly without calling `AudioController` or `MusicRepository`.

---

### MH-FEAT-002 — Media Store & File Scanner
- **Status**: `IMPLEMENTED`
- **Module**: `:core`
- **Purpose**: Index local media files and observe file system changes without blocking the UI.
- **Contract**:
  1. `MediaStoreObserver` monitors Android MediaStore URI changes.
  2. `ManualScanner` scans user-added watched folders (`WatchedFolderEntity`).
  3. Scanned tracks are persisted in `LocalSongEntity` using upsert logic.

---

### MH-FEAT-003 — Persistent Playback Queue
- **Status**: `IMPLEMENTED`
- **Module**: `:core` / `:media`
- **Purpose**: Maintain queue order, index, timestamp, and active screen across app restarts.
- **Contract**:
  1. Any queue mutation (reorder, delete, append) increments `revision`.
  2. Writes complete to `PlaybackStateEntity` before updating `StateFlow`.
  3. On launch, silent resume restores track, position, and active tab.

---

### MH-FEAT-004 — High-Honesty Audio Telemetry
- **Status**: `IMPLEMENTED`
- **Module**: `:ui` / `:media`
- **Purpose**: Display transparent audio processing metadata without making unverified audiophile claims.
- **Contract**:
  1. Output state is categorized into `VERIFIED`, `ESTIMATED`, or `UNKNOWN`.
  2. Direct USB DAC connections are marked `VERIFIED`.
  3. Wireless/Bluetooth paths are marked `ESTIMATED` based on available system capabilities.

---

### MH-FEAT-005 — OLED-Safe Desk Mode (`AmbientPlayer`)
- **Status**: `IMPLEMENTED`
- **Module**: `:ui`
- **Purpose**: Provide an immersive, battery-conscious desktop display mode for docked devices.
- **Contract**:
  1. Background uses true OLED black `#000000`.
  2. Applies periodic pixel-shifting to prevent panel burn-in.
  3. Dims UI elements after idle timeout while retaining sample rate & track info visibility.

---

### MH-FEAT-006 — Global Search Engine
- **Status**: `IMPLEMENTED`
- **Module**: `:ui` / `:core`
- **Purpose**: Provide instant, unified search across Songs, Albums, and Artists.
- **Contract**:
  1. Executes queries asynchronously across indexed database tables.
  2. Groups results into distinct categories: Songs, Albums, Artists.

---

### MH-FEAT-007 — Library Tools & Duplicate Finder
- **Status**: `IMPLEMENTED`
- **Module**: `:organizer` / `:core`
- **Purpose**: Calculate library health and identify duplicate audio tracks.
- **Contract**:
  1. Evaluates track title, artist, duration, and file size to calculate health scores.
  2. Identifies duplicate audio files for cleanup.

---

### MH-FEAT-008 — Metadata Tag Editor
- **Status**: `IMPLEMENTED`
- **Module**: `:organizer`
- **Purpose**: Provide Walkman-styled editing of audio tags (Title, Artist, Album, Track Number).
- **Contract**:
  1. Writes updated tags directly to file metadata or local database override.
  2. Triggers media re-indexing upon save.

---

### MH-FEAT-009 — DAP Home Screen
- **Status**: `VERIFIED`
- **Module**: `:ui`
- **Purpose**: Serves as the primary appliance launcher dashboard (WALKMAN Home concept) exposing 2x2 primary navigation grid, Recently Added track previews, and storage status.
- **Contract**:
  1. Acts as the `startDestination` (`MusicDestination.Home.route`) in `NavHost`.
  2. Tiles navigate directly to canonical destinations (`library`, `settings/library`, `queue`, `sound`).
  3. Displays recently added media with audio quality badges.
- **Verification & Remediation**:
  - Independent Auditor & Critic L.I.S.A. review passed.
  - `DapHomeScreen` state handling is explicit (`Loading`, `Error`, `Empty`, `Success`) and isolated for testability.
