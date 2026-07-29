# Implementation Plan - Phase 5: Intelligent Indexing Engine v2

This phase addresses performance bottlenecks discovered during 1000+ song "stress tests." We are moving from a simple "full-rebuild" scanner to a phased, incremental indexing engine with background library intelligence.

## User Review Required

> [!IMPORTANT]
> **Data Unification**: Currently, the "Library" and "Explore/Organizer" modules use separate databases. I will begin work to unify them under a single **Global Media Index** to prevent redundant scanning and inconsistent data.
> **Background Analysis**: Deep analysis (Duplicate detection, Health score) will now be decoupled from the UI thread and executed as low-priority background tasks.

## Proposed Changes

### [Component: Core - Indexing Engine]

#### [MODIFY] [MediaScanner.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/scanner/MediaScanner.kt)
- **Incremental Discovery**: Implement a "Delta Scan" that compares file `DATE_MODIFIED` and `SIZE` against the current database. Only process new or changed files.
- **Phased Execution**:
    - **Phase 1 (Discovery)**: Quickly map file paths to the database (Fast path).
    - **Phase 2 (Metadata)**: Extract ID3 tags in optimized batches.
    - **Phase 3 (Artwork)**: Verify artwork existence and resolution in the background.

#### [MODIFY] [OrganizerRepository.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/data/OrganizerRepository.kt)
- **Debounced Analysis**: Use a `debounce(2000ms)` on the `healthStats` flow to prevent UI freezing during bulk database operations.
- **Cached Statistics**: Persist the `LibraryHealthStats` to the database so they are instantly available on app launch without recalculation.

### [Component: UI - Library Management]

#### [MODIFY] [LibraryToolsScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/LibraryToolsScreen.kt)
- **Granular Progress**: Update the "Analyzing Library" view to show the specific sub-phase and percentage (e.g., "Phase 2/3: Reading Tags - 45%").
- **Responsive Stats**: Show "stale" stats instantly from cache while the background update is running.

### [Component: UI - Search]

#### [MODIFY] [GlobalSearchScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/GlobalSearchScreen.kt)
- **Search Optimization**: Ensure search indexing is tied to Phase 1 (Discovery) so new songs appear in search results even before their metadata is fully parsed.

## Verification Plan

### Manual Verification
- **Cold Scan Performance**: Measure the time to index 1000 songs from scratch.
- **Delta Scan Performance**: Measure the time to scan when only 1 new song is added to a 1000-song library (Target: < 2 seconds).
- **UI Responsiveness**: Verify that the "Now Playing" UI remains 100% responsive and jitter-free while a deep scan is running in the background.
- **Storage Disconnect**: Verify the scanner gracefully handles situations where an SD card or USB drive is removed mid-scan.

## Open Questions
- Should we allow the user to prioritize specific folders for "Fast Scanning"?
- Should "Duplicate Detection" be moved to a completely manual trigger to save battery on legacy devices?
