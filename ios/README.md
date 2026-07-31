# Combat Timer — iOS

Native SwiftUI port of the Android Combat Timer app. Same sport presets,
same timer logic, same manga-poster visual theme, same four sound cues.

This folder contains only Swift source files and resources — no
`.xcodeproj`. Xcode project files are fragile to hand-author outside of
Xcode itself, so instead you create the project shell in Xcode (which
guarantees it's valid) and drop these files in.

## Setup (on a Mac, in Xcode)

1. **File > New > Project… > iOS > App**
   - Product Name: `CombatTimer`
   - Interface: **SwiftUI**
   - Language: **Swift**
   - Minimum Deployment: **iOS 16.0**
   - Save it anywhere convenient (e.g. inside this `ios/` folder, replacing
     this placeholder structure once imported).

2. In the new project, **delete** the auto-generated `ContentView.swift`
   and `CombatTimerApp.swift` (or whatever Xcode named the `@main` file) —
   the versions in this folder replace them.

3. Drag the `CombatTimer/` folder from here into the Xcode project
   navigator. Check **"Copy items if needed"** and make sure the
   `CombatTimer` target is checked under "Add to targets".

4. Drag the 4 files in `CombatTimer/Resources/` into the project too
   (same "Copy items if needed" + target checkbox). These are the bundled
   sound clips:
   - `wood_clap_triple.mp3` — 10-second warning clapper
   - `mma_horn.wav` — round-end horn
   - `timer_beep.mp3` — rest countdown beep (5, 4, 3, 2, 1)
   - `boxing_bell.mp3` — round-start bell

5. Build & run (`Cmd+R`) on a simulator or your iPhone.

## Structure

```
CombatTimer/
  CombatTimerApp.swift        - @main entry point
  ContentView.swift           - screen navigation (selection/config/timer)
  TimerViewModel.swift        - the timer state machine
  Model/
    SportPreset.swift         - the 7 sport rulesets
    TimerPhase.swift
    TimerUiState.swift
  Engine/
    SoundEngine.swift         - AVAudioPlayer-based sound cues
  Theme/
    CombatColors.swift        - shared color palette
  Util/
    TimeFormat.swift
  Views/
    Components/
      SevenSegmentDisplay.swift  - digital-clock readout
      MangaEffects.swift         - speed-line burst, ring-rope divider, ComicText
    Screens/
      SportSelectionScreen.swift
      ConfigScreen.swift
      TimerScreen.swift
  Resources/                  - bundled audio clips (see step 4 above)
```

## Not yet done

- App icon (AppIcon.appiconset) — needs a 1024×1024 PNG. The Android
  launcher icon design (octagon cage + clock) can be adapted; ask if you
  want this generated.
- No mute toggle / vibration, matching the Android app's behavior by design.
