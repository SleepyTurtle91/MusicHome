# GitHub Release Upload Script
# Fill in your token and run to upload a release
# $token = "YOUR_GITHUB_TOKEN"
$repo = "SleepyTurtle91/MusicHome"
$tag = "v1.3.0"
$releaseNotes = @"
# Music Home v1.3.0

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
"@

$apkPath = "app/build/outputs/apk/release/app-release.apk"

# $headers = @{
#     "Authorization" = "token $token"
#     "Accept" = "application/vnd.github.v3+json"
# }

# Check if release exists
# ... rest of script removed for security or needs to be properly configured with env vars ...
Write-Output "Please configure GITHUB_TOKEN environment variable to use this script."
