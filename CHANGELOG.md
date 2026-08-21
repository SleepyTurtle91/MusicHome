# Changelog

All notable changes to this project will be documented in this file.

## [2.7.0] - 2026-08-21

### Added
- **Dynamic Appearance & OLED Optimization**:
  - Live theme accent color selection (**Walkman Orange**, **Blue**, **Green**, **Pink**) with persistent storage across app restarts.
  - Switchable **True Black Mode** (Pure OLED Black vs Dark Zinc Appliance palette).
  - Navigation bar, headers, and UI controls dynamically bind to user's active theme accent.
- **Interactive Scrubber & Mini-Player Tri-Controls**:
  - Replaced static progress indicator with an interactive seek `Slider` featuring smooth dragging state and real-time position tracking.
  - Added dedicated `<|| Skip Previous` and `Skip Next ||>` buttons to the persistent bottom `PlaybackStrip`.
  - Optimized Player layout vertical spacing to guarantee complete visibility of physical hardware buttons on all device viewports.
- **Search & Navigation Integrity**:
  - Connected search result clicks to instant playback in Player and album detail inspection.
  - Added top navigation bar with back action to `AlbumDetailScreen`.
- **Database & Metadata Pipeline**:
  - Implemented Room `updateSongMetadata` and `deleteSong` queries across DAO, Repository, and `LibraryToolsViewModel`.

### Changed
- **Process Singleton Architecture (Tier 0)**:
  - Introduced `MusicHomeApplication` and `MusicRepositoryProvider` in `:core` to ensure a single process-scoped container for `MusicDatabase` and `LocalMediaRepository`.
  - Eliminated duplicate database builder instances and media scanner storms between UI and Background Service.
- **API-Aware Permission Engine**:
  - Added runtime audio permission handling supporting API 33+ `READ_MEDIA_AUDIO` and API ≤32 `READ_EXTERNAL_STORAGE` with seamless automatic library indexing.
- **High-Honesty Audio Telemetry**:
  - Replaced false sample-rate output heuristics with empirical Android `AudioDeviceInfo` output endpoint detection.
- **Orientation & Desk Mode**:
  - Removed manifest portrait orientation lock, unlocking tablet landscape and Desk Mode.

### Fixed
- Fixed Jetpack Compose `ArrayIndexOutOfBoundsException` in `Color.getColorSpace-impl` by migrating color construction to standard ARGB `Int` values.
- Fixed `MusicViewModel` playback controls to delegate directly to `MediaController` / ExoPlayer.

---

### Added
- **MH-FEAT-009 DAP Home Screen**: Completed L.I.S.A remediation cycle for `DapHomeScreen`.
  - Addressed UI state-handling defects by hoisting `MusicUiState` and introducing explicit `Error` handling.
  - Hardened navigation by replacing magic routes with canonical `MusicDestination` constants.
  - Made metadata presentation defensive against malformed MIME types.
  - Added new `DapHomeScreenTest` suite for robust state UI verification.
- **MusicHome Knowledge System v1.0**: Formalized system documentation hierarchy (`PROJECT_CONTEXT.md`, `ROADMAP.md`, `FEATURES.md`, `docs/DOCUMENT_INDEX.md`).
- **Architecture Specifications**: Comprehensive docs for module boundaries (`MODULE_ARCHITECTURE.md`), media pipeline state machine (`MEDIA_PIPELINE.md`), hardware state machine & high-honesty telemetry (`DEVICE_STATE.md`), and queue persistence (`QUEUE_ARCHITECTURE.md`).
- **Engineering Quality Gates**: Formalized testing strategy & verification protocol (`TESTING_STRATEGY.md`).

### Verified
- **MH-FEAT-009 L.I.S.A Remediation**: Passed independent Auditor & Critic re-review. Resolved a frozen-core governance violation (`gradlew` permission) and verified all states (Loading/Error/Empty/Success) through automated UI tests and `./gradlew test`.
- **Source/Doc Consistency Audit**: Aligned media pipeline docs with `ScanState` sealed class constructors (`Idle`, `Indexing`, `Enriching`, `Analyzing`, `Finished`, `Error`).
- **Build & Verification Suite**: Passed clean `./gradlew assembleDebug` build gate and `./gradlew test` unit test suite (101 tasks).

## [2.5.0] - 2026-07-29

### Added
- **Durable Playback Sessions**: Full persistence of queue order, current index, and exact position (timestamp) through app restarts.
- **Tactile Queue Mastery**: Professional-grade manual reordering (handles) and swipe-to-remove with Undo support.
- **Unified Global Search**: Centralized search for Songs, Albums, and Artists with parallel execution and ranking.
- **Intelligent Indexing Engine (v2)**: High-performance "Delta Scanning" using file timestamps and size to skip unchanged files.
- **Phased Scanning**: New background pipeline (Index -> Enrich -> Intelligence) to keep UI responsive during large library updates.
- **Library Configuration Hub**: Dedicated settings screen for managing music storage and viewing library statistics.
- **Artwork Crossfade**: Premium 400ms visual transition for album art.
- **"Up Next" Preview**: Vertical glanceable list of upcoming tracks in the Player.
- **Sub-Screen Settings Architecture**: Scalable navigation-based settings (Appearance, Playback, Library, Updates).

### Changed
- **Data Unification**: Consolidated `:core` and `:organizer` databases into a single Global Media Index.
- **Adaptive Transport Controls**: Dynamically resizing buttons (52dp-72dp) based on device screen width.
- **Advanced Gestures**: Restricted swipes and double-taps to artwork area for more reliable playback control.
- **Improved Information Architecture**: Moved music folder management from Library to Settings > Library.

---

## [2.0.0] - 2026-07-24

### Added
- **Global Hardware Shell**: New 5-tab navigation system.
- **Sony-inspired Identity**: Upgraded branding to 'MUSIC HOME'.
- **DeviceState System**: Central hardware state machine implemented.
- **Desk Mode**: Immersive AmbientPlayer for OLED protection.
- **Advanced Playback**: Added Gapless playback, ReplayGain, and Offload support.

### Changed
- Refined player with 'High-Honesty' technical telemetry and hardware LED indicator.
- Validated experience on POCO X7 physical hardware.

---
## [1.3.0] - 2026-07-15
### Added
- **Library Tools**: A new maintenance workshop for managing your music collection.
- **Library Health Score**: Visual indicator of library organization and metadata coverage.
- **Deep Scanner**: New engine for scanning local media and extracting advanced metadata.
- **Duplicate Finder**: Identify and manage duplicate music entries.
- **Metadata Editor**: Built-in tag editor with Walkman-style interface, integrated into Library context menus.
- **New Module**: `:organizer` library module for maintenance-related features.

### Changed
- Refactored `MusicHomeApp` navigation to include Library Tools.
- Adopted `WalkmanTheme` across all new organizer screens.
- Enhanced `LibraryScreen` with metadata editing capabilities.

---
[1.2.1] - Previous Version
