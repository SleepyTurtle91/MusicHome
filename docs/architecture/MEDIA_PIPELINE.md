# 🔄 Media Pipeline & Metadata Lifecycle

This document specifies the multi-stage media indexing, scanning, and metadata enrichment pipeline in **MusicHome**.

---

## ⚙️ Media Pipeline State Machine

Every local audio file discovered on the device moves through a strict state machine:

```
[ Idle ]
   │
   ▼
Indexing ────────► Enriching ────────► Analyzing ────────► Finished
   │                  │                   │
   └──────────────────┴───────────────────┴─────────────► Error
```

---

## 📊 Pipeline States (Reflecting `ScanState` Sealed Class)

| State | Class Signature | Definition | Trigger |
|---|---|---|---|
| `Idle` | `ScanState.Idle` | Scanner is inactive; standby mode. | System default |
| `Indexing` | `ScanState.Indexing(progress)` | Discovering files & basic filesystem metadata. | `MediaStoreObserver` / `ManualScanner` |
| `Enriching` | `ScanState.Enriching(current, total)` | Extracting ID3/FLAC metadata & artwork. | `BasicAudioMetadataReader` |
| `Analyzing` | `ScanState.Analyzing(phase)` | Deep analysis (health scoring, duplicate identification). | `DuplicateFinder` / `LibraryHealthAnalyzer` |
| `Finished` | `ScanState.Finished(count)` | Scan pipeline successfully complete. | Database transaction commit |
| `Error` | `ScanState.Error(message)` | Pipeline encountered an unrecoverable failure. | Exception during scanning |

---

## 🔍 Scanner Components

### 1. `MediaStoreObserver`
- Listens to Android `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` changes.
- Uses `Delta Scanning`: skips files where size and timestamp match existing DB entries.

### 2. `ManualScanner`
- Scans custom user folders registered in `WatchedFolderEntity`.
- Recursively walks file trees for `.mp3`, `.flac`, `.wav`, `.m4a`, `.dsf` files.
