# Tasks - Phase 5: Intelligent Indexing Engine v2

- `[x]` **Phase 1: Global Media Index Unification**
    - `[x]` Consolidate `:core` and `:organizer` databases
    - `[x]` Remove redundant `OrganizerRepository` and `MusicDao`
    - `[x]` Update `LocalSongEntity` with metadata and analysis fields
- `[x]` **Phase 2: Intelligent Indexing Engine v2**
    - `[x]` Implement "Delta Scan" (Size/DateModified) in `MediaStoreScanner`
    - `[x]` Implement Phased Scanning: Index -> Enrich -> Intelligence
    - `[x]` Add `LibraryStatsEntity` and persistent caching
- `[x]` **Phase 3: UI Feedback & Responsiveness**
    - `[x]` Update `LibraryToolsScreen` with granular progress reporting
    - `[x]` Refactor `DuplicateFinderScreen` to use unified media model
    - `[x]` Fix search indexing to support partial discovery state
- `[x]` **Phase 4: Verification & Testing**
    - `[x]` Verify delta scan speed with 1000+ song library
    - `[x]` Confirm UI responsiveness during deep background analysis
