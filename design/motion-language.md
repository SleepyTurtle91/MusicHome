# Music Home Motion Language

## Philosophy: Restrained & Precise
Premium hardware doesn't bounce or blur unnecessarily. Motion should be functional, providing feedback and continuity without drawing attention to itself.

## 1. Mechanical Snaps (Toggles & Selection)
Used for: Gain switching, EQ presets, Tab switching.
- **Duration**: 150ms - 200ms
- **Easing**: `FastOutLinearInEasing`
- **Description**: A quick, precise transition with no overshoot. Like a physical switch clicking into place.

## 2. Fluid Transitions (Navigation & Artwork)
Used for: Screen transitions, Album art expansion, Playback Strip appearance.
- **Duration**: 300ms
- **Easing**: `LinearOutSlowInEasing` (for entering), `FastOutSlowInEasing` (for movement)
- **Description**: Smooth, controlled movement. Avoid standard "spring" physics unless mimicking a physical dial.

## 3. System Status (LED & Scanning)
Used for: LED pulsing, Loading indicators.
- **Duration**: 1000ms (Period)
- **Easing**: `LinearEasing`
- **Description**: A slow, rhythmic pulse. Mimics the breathing light of an idle or busy machine.
