# 🔁 Queue Architecture & Session Recovery

This document specifies the **Persist-First Queue Mutation** lifecycle and silent session recovery mechanisms in **MusicHome**.

---

## 🔄 Persist-First Mutation Sequence

All queue modifications (reordering, additions, removals) follow a strict transactional flow:

```
[ User Interaction ]
         │
         ▼
 1. Increment Queue Revision (revision++)
         │
         ▼
 2. Write to Room Database (PlaybackStateEntity)
         │
         ▼
 3. Emit Updated State via StateFlow
         │
         ▼
 4. Render UI Update
```

---

## 🎧 Hardware Silent Resume

On application cold boot:
1. `MusicViewModel` reads the persisted `PlaybackStateEntity`.
2. Restores song list, current index, and exact timestamp (`positionMs`).
3. If music was actively playing before termination, auto-navigates to the **Player** screen.
4. If paused, restores the user's **Last Active Tab**.
