# Changelog

All notable changes to this project will be documented in this file.

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
