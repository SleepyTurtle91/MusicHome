# GitHub Release Upload Script
# Fill in your token or set GITHUB_TOKEN env variable
$token = $env:GITHUB_TOKEN
if (-not $token) {
    Write-Output "Please configure GITHUB_TOKEN environment variable or set `$token in this script."
    exit
}

$repo = "SleepyTurtle91/MusicHome"
$tag = "v2.0.0"
$releaseNotes = Get-Content -Path "release_notes.md" -Raw
$apkPath = "app/build/outputs/apk/release/app-release.apk"

$headers = @{
    "Authorization" = "token $token"
    "Accept" = "application/vnd.github.v3+json"
}

Write-Output "Creating release $tag for $repo..."
$body = @{
    tag_name = $tag
    name = "Music Home $tag"
    body = $releaseNotes
    draft = $false
    prerelease = $false
} | ConvertTo-Json

$releaseUrl = "https://api.github.com/repos/$repo/releases"
try {
    $response = Invoke-RestMethod -Uri $releaseUrl -Method Post -Headers $headers -Body $body -ContentType "application/json"
    $uploadUrl = $response.upload_url -replace '\{.*\}', "?name=app-release.apk"
    
    Write-Output "Uploading APK asset..."
    Invoke-RestMethod -Uri $uploadUrl -Method Post -Headers @{
        "Authorization" = "token $token"
        "Accept" = "application/vnd.github.v3+json"
        "Content-Type" = "application/vnd.android.package-archive"
    } -InFile $apkPath

    Write-Output "Upload completed successfully! Release URL: $($response.html_url)"
} catch {
    Write-Output "Failed to upload release: $_"
}
