# 🗺️ MusicHome Development Roadmap

This master roadmap tracks system capabilities based strictly on verified code evidence in the repository.

---

## 📌 Phase Overview

```
Phase 0: Foundation              [✓ COMPLETE]
Phase 1: Media Foundation         [✓ COMPLETE]
Phase 2: Audio Pipeline           [✓ COMPLETE]
Phase 3: Library Experience       [✓ COMPLETE]
Phase 4: Queue & Persistence      [✓ COMPLETE]
Phase 5: DAP Hardware Appliance   [✓ COMPLETE]
Phase 6: Intelligence & Tools     [✓ COMPLETE]
Phase 7: Production Release       [IN PROGRESS]
```

---

## 🚦 Phase Details & Status

### Phase 0 — Foundation
- [x] Multi-module Android architecture (`:app`, `:core`, `:media`, `:ui`, `:organizer`)
- [x] Unidirectional State Flow (UDF) state architecture
- [x] L.I.S.A. Engineering Workflow integration (`PROJECT_CONTEXT.md`, `ARCHITECTURE_DECISIONS.md`)
- [x] Document authority index (`docs/DOCUMENT_INDEX.md`)
- [x] Unit test setup (`PlaybackSessionTest.kt`)

### Phase 1 — Media Foundation & Storage
- [x] Room media database schema (`MusicDatabase`, `SongDao`, `LocalSongEntity`)
- [x] Watched folder entity persistence (`WatchedFolderEntity`)
- [x] Automatic system MediaStore observer (`MediaStoreObserver`)
- [x] Manual file system scanner (`ManualScanner`)
- [x] Audio metadata extraction engine (`BasicAudioMetadataReader`)
- [x] Artwork caching system (`ArtworkCache`)

### Phase 2 — Audio Pipeline & Telemetry
- [x] Media3 ExoPlayer service integration (`MusicPlaybackService`)
- [x] High-Honesty audio telemetry engine (`AudioTelemetry`)
- [x] Signal chain status calculation (`AudioPipelineState`: Verified / Estimated / Unknown)
- [x] ReplayGain metadata processing (Track & Album mode)
- [x] Gapless playback configuration
- [x] Audio transition strategy abstraction (`TransitionStrategy`)
- [x] Hardware Volume Guard (50% cap on high volume boot)

### Phase 3 — Library Experience & Navigation
- [x] 5-Tab Hardware Navigation Dock (Library, Player, Explore, Sound, Settings)
- [x] Category browsing (Songs, Albums, Artists, Folders)
- [x] Album detail view with dynamic palette extraction (`AlbumPalette`)
- [x] Global parallel search engine (`GlobalSearchScreen`)
- [x] Sub-screen settings navigation (Appearance, Playback, Library, Updates)

### Phase 4 — Persistent Queue & Session Recovery
- [x] Versioned `PlaybackQueue` model
- [x] Room database persistence (`PlaybackStateEntity`)
- [x] Persist-First queue mutation pipeline (Revisioning, Atomic DB Writes)
- [x] Hardware silent resume (Restores song, positionMs, and active tab)
- [x] Tactile queue controls (Drag handle reordering, swipe-to-remove with Undo)

### Phase 5 — DAP Hardware Appliance Experience
- [x] Sony Walkman inspired DAP UI system (`WalkmanTheme`, `DapDefinitions`)
- [x] Persistent Playback Strip (Front-panel display)
- [x] Central `DeviceState` hardware state machine
- [x] OLED-safe Desk Mode (`AmbientPlayer` with pixel-shifting and automatic dimming)
- [x] Signal Chain Card & VU Meter visualizations (`SignalChainCard`, `VUMeter`, `SpectrumVisualizer`)

### Phase 6 — Intelligence & Library Tools
- [x] Duplicate file detection engine (`DuplicateFinder`)
- [x] Library health score calculation (`LibraryHealthAnalyzer`)
- [x] Embedded audio tag editor (`MetadataEditorScreen`)
- [x] Scanner progress visualizer (`ScannerProgressScreen`)

### Phase 7 — Production Release & Hardening
- [x] Build verification gates (`./gradlew assembleDebug` passing clean)
- [ ] Warning-free Kotlin compilation audit
- [ ] Automated regression test suite expansion
- [ ] Release APK assembly & performance profiling
