- `[x]` **Phase 1: Build & Infrastructure**
    - `[x]` Update `libs.versions.toml` with new dependencies
    - `[x]` Include `:organizer` module in `settings.gradle.kts`
    - `[x]` Create `:organizer` module `build.gradle.kts`
    - `[x]` Sync Gradle

- `[x]` **Phase 2: Core Organizer Engines**
    - `[x]` Implement Data Layer (Entities, DAO, Database)
    - `[x]` Implement Scanner Engine (`MediaScanner`)
    - `[x]` Implement Duplicate Engine (`DuplicateFinder`)
    - `[x]` Implement Repository and Library Health logic
    - `[x]` Implement Metadata Engine (Edit logic)

- `[x]` **Phase 3: UI Implementation (Walkman Style)**
    - `[x]` Create `LibraryToolsViewModel`
    - `[x]` Create `LibraryToolsScreen` (Dashboard/Technician Panel)
    - `[x]` Implement `DuplicateFinderScreen` (Walkman Style)
    - `[x]` Implement `MetadataEditorScreen` (Walkman Style)
    - `[x]` Implement `ScannerProgressScreen` (Walkman Style)

- `[x]` **Phase 4: Integration**
    - `[x]` Add "Library Tools" to `MusicHomeApp` Navigation Suite
    - `[x]` Register all routes in the main `NavHost`
    - `[x]` Add "Edit Metadata" to `LibraryScreen` context menus
    - `[x]` Verification & Cleanup
