# 🗺️ MusicHome Knowledge System Index

Welcome to the **MusicHome** System Documentation. This document serves as the entry point and authority map for all engineering, architectural, and product specifications.

---

## 📌 Master System Authority

| File | Purpose | Scope |
|---|---|---|
| [`/PROJECT_CONTEXT.md`](file:///workspace/Projects/MusicHome/docs/PROJECT_CONTEXT.md) | High-level system overview & L.I.S.A. workflow cycle | Global |
| [`/ROADMAP.md`](file:///workspace/Projects/MusicHome/ROADMAP.md) | Master development roadmap (Phases 0–7) based on empirical repository state | Global |
| [`/FEATURES.md`](file:///workspace/Projects/MusicHome/FEATURES.md) | Feature registry with contracts, IDs, states, and verification criteria | Features |
| [`/ARCHITECTURE_DECISIONS.md`](file:///workspace/Projects/MusicHome/docs/ARCHITECTURE_DECISIONS.md) | Immutable Architecture Decision Records (ADRs) | Architecture |

---

## 📂 Documentation Hierarchy

```text
docs/
├── DOCUMENT_INDEX.md                ← You are here
│
├── architecture/
│   ├── MODULE_ARCHITECTURE.md       ← Module boundaries & dependency invariants
│   ├── DATA_FLOW.md                 ← Unidirectional state flow (MVI/UDF)
│   ├── MEDIA_PIPELINE.md            ← Media store scanning & metadata enrichment
│   ├── PLAYBACK_ARCHITECTURE.md     ← ExoPlayer session management & audio transitions
│   ├── QUEUE_ARCHITECTURE.md        ← Persist-First queue revisioning & recovery
│   └── DEVICE_STATE.md              ← Central hardware state machine & High-Honesty telemetry
│
├── features/
│   ├── MEDIA_LIBRARY.md             ← Library indexing, filtering & stats
│   ├── MEDIA_SCANNING.md            ← MediaStoreObserver & ManualScanner pipelines
│   ├── DUPLICATE_DETECTION.md       ← Content hash & metadata duplicate finding
│   ├── PLAYBACK.md                  ← Audio engine, Media3 service & transport controls
│   ├── QUEUE.md                     ← Persistent queue, reordering & swipe-to-remove
│   ├── SEARCH.md                    ← Global parallel search engine
│   └── SETTINGS.md                  ← Sub-screen settings architecture & folder management
│
├── product/
│   ├── DAP_PHILOSOPHY.md            ← Core DAP appliance principles & identity
│   ├── DESIGN_REFERENCES.md         ← Walkman & hardware DAP reference studies
│   └── UI_SPECIFICATION.md          ← Canonical UI/UX design specification & hierarchy
│
└── engineering/
    ├── TESTING_STRATEGY.md          ← Unit testing, build verification & Gradle gates
    └── DEVELOPMENT_WORKFLOW.md      ← L.I.S.A. 6-step cycle execution guide
```

---

## 📜 Authority Rule

> **Source code describes implementation. Documentation describes intent, contracts, constraints, and decisions.**
