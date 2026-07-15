# Merge MusicOrganizer Functionality into MusicHome (The "Library Tools" Vision)

Merge the `MusicOrganizer` functionality into `MusicHome` as a built-in "maintenance workshop" called **Library Tools**. Instead of embedding a separate app, we will integrate its features (Scanner, Metadata Editor, Duplicate Engine) directly into the Music Home ecosystem, adopting its theme and navigation system.

## User Review Required

> [!IMPORTANT]
> **Navigation Refactoring**: We are completely removing `Navigation3` from the organizer code. All organizer features (Scanner, Duplicate Finder, etc.) will be registered as top-level or secondary routes in the main `MusicHome` `NavHost`.

> [!TIP]
> **Theme Adoption**: The "Library Tools" will strictly follow the `WalkmanTheme` (Orange/Black/White) to ensure a seamless "built-in" feel.

## Proposed Changes

### 1. Build & Module Configuration

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/settings.gradle.kts)
- Include the new `:organizer` module.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/gradle/libs.versions.toml)
- Sync and add necessary dependencies (Room 2.7.0, Moshi, CameraX for potential future visual scanning).

#### [NEW] [build.gradle.kts](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/build.gradle.kts)
- Define `:organizer` as an Android Library.

---

### 2. Organizer Module Implementation (Feature-Based)

Refactor `MusicOrganizer` code into functional engines:

#### [NEW] [com.lemonsquad.musichome.organizer.scanner](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/scanner)
- Refined `MediaScanner` that extracts metadata and populates the database.

#### [NEW] [com.lemonsquad.musichome.organizer.duplicates](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/duplicates)
- Dedicated `DuplicateEngine` logic extracted from the old ViewModel.

#### [NEW] [com.lemonsquad.musichome.organizer.metadata](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/metadata)
- Metadata Editor UI and logic, refactored to use `WalkmanTheme`.

#### [NEW] [com.lemonsquad.musichome.organizer.data](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/organizer/src/main/kotlin/com/lemonsquad/musichome/organizer/data)
- Organizer-specific Room database (keeping it separate for V1.0 as agreed).

---

### 3. Integration into MusicHome UI

#### [MODIFY] [MusicHomeApp.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/MusicHomeApp.kt)
- Add "Library Tools" to the `NavigationSuiteScaffold` items.
- Define routes for:
    - `library-tools` (Home/Dashboard of tools)
    - `duplicate-finder`
    - `metadata-editor/{songId}`
    - `scanner-progress`

#### [NEW] [LibraryToolsScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/LibraryToolsScreen.kt)
- A central hub for the "maintenance workshop" features:
    - Library Health Overview (Stats)
    - Buttons for Scanner, Duplicate Finder, etc.

#### [MODIFY] [LibraryScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/LibraryScreen.kt)
- Add "Edit Metadata" option to song/album context menus that navigates to the Organizer's editor.

## Verification Plan

### Automated Tests
- Build verification for the new `:organizer` module.
- Unit tests for the `DuplicateEngine` logic.

### Manual Verification
1. Open "Library Tools" from the main menu.
2. Verify the "Walkman" styling is applied everywhere.
3. Run a scan and check the "Library Health" stats.
4. Find and merge duplicates.
5. Edit a song's metadata from the Library screen and confirm it updates.
