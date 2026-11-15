---
name: Apex Shonen Combat Timer
colors:
  surface: '#0e1323'
  surface-dim: '#0e1323'
  surface-bright: '#34394a'
  surface-container-lowest: '#080d1d'
  surface-container-low: '#161b2b'
  surface-container: '#1a1f30'
  surface-container-high: '#25293a'
  surface-container-highest: '#2f3446'
  on-surface: '#dee1f9'
  on-surface-variant: '#e7bdb8'
  inverse-surface: '#dee1f9'
  inverse-on-surface: '#2b3041'
  outline: '#ae8883'
  outline-variant: '#5d3f3c'
  surface-tint: '#ffb4ab'
  primary: '#ffb4ab'
  on-primary: '#690006'
  primary-container: '#e31e24'
  on-primary-container: '#fffafa'
  inverse-primary: '#c00014'
  secondary: '#b7c4ff'
  on-secondary: '#002681'
  secondary-container: '#043ebd'
  on-secondary-container: '#a8b9ff'
  tertiary: '#ebc300'
  on-tertiary: '#3b2f00'
  tertiary-container: '#caa800'
  on-tertiary-container: '#4c3e00'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdad6'
  primary-fixed-dim: '#ffb4ab'
  on-primary-fixed: '#410002'
  on-primary-fixed-variant: '#93000d'
  secondary-fixed: '#dce1ff'
  secondary-fixed-dim: '#b7c4ff'
  on-secondary-fixed: '#001551'
  on-secondary-fixed-variant: '#0039b4'
  tertiary-fixed: '#ffe177'
  tertiary-fixed-dim: '#ebc300'
  on-tertiary-fixed: '#231b00'
  on-tertiary-fixed-variant: '#554500'
  background: '#0e1323'
  on-background: '#dee1f9'
  surface-variant: '#2f3446'
typography:
  display-timer:
    fontFamily: Bebas Neue
    fontSize: 96px
    fontWeight: '400'
    lineHeight: 96px
    letterSpacing: 0.04em
  display-timer-mobile:
    fontFamily: Bebas Neue
    fontSize: 72px
    fontWeight: '400'
    lineHeight: 72px
    letterSpacing: 0.04em
  headline-xl:
    fontFamily: Bebas Neue
    fontSize: 48px
    fontWeight: '400'
    lineHeight: 52px
    letterSpacing: 0.05em
  headline-xl-mobile:
    fontFamily: Bebas Neue
    fontSize: 36px
    fontWeight: '400'
    lineHeight: 40px
    letterSpacing: 0.05em
  headline-lg:
    fontFamily: Bebas Neue
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 36px
    letterSpacing: 0.04em
  headline-md:
    fontFamily: Bebas Neue
    fontSize: 24px
    fontWeight: '400'
    lineHeight: 28px
    letterSpacing: 0.03em
  title-md:
    fontFamily: Space Grotesk
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 24px
    letterSpacing: 0.02em
  body-lg:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 22px
  body-md:
    fontFamily: Space Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Space Grotesk
    fontSize: 13px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.08em
  label-sm:
    fontFamily: Space Grotesk
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.1em
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.25rem
---

## Brand & Style

This design system channels the sharp, calculated intensity of modern high-production shonen tournament arcs translated into an elite combat sports tool. Tailored for fighters, corner coaches, and combat athletes across MMA, boxing, Brazilian Jiu-Jitsu, and Judo, the aesthetic marries Japanese graphic editorial poster craft with utilitarian athletic instrumentation.

The interface prioritizes immediate clarity at distance (gym ring canvas, heavy bag stands, across the mat) while delivering visceral energy through razor-sharp lines, hard-inked outlines, and dynamic angular composition. It entirely avoids grungy distress, torn textures, or chaotic splatter; instead, it enforces surgically crisp comic borders, rhythmic halftone screentones, sharp chamfered badges, and intense, saturated focal states. 

The emotional tone is hyper-focused, disciplined, cinematic, and unyielding—radiating technical mastery and competitive drive.

## Colors

The palette is engineered around dark-arena contrast, optimizing tactical visibility across gyms under bright fluorescent lights or dim cage lamps.

### Core Architecture
- **Canvas Base (`#0B1020`)**: Deep obsidian navy. The foundational void serving as the backdrop for all active states.
- **Card Panel Base (`#121A33`)**: Structural containment for secondary modules, rounds history, and drill parameters.
- **Card Panel Elevated (`#1A2547`)**: Interactive surfaces, configuration selectors, and active state containers.
- **Ink Border / Shadow (`#0A0A0A` / `#000000`)**: Deep black comic ink outlining panels and projecting stark drop-shadows.

### Phase & Tactical Signaling
- **Combat / Fight Phase (`#E31E24`)**: Primary kinetic red. Triggers high-cadence fight rounds, knockout drills, and urgent actions.
- **Recovery / Rest Phase (`#1746C4`)**: Electric cobalt blue. Signals calm, breath regulation, corner hydration, and tactical resets.
- **Golden Score / Overtime / Warnings (`#FFD400`)**: High-voltage yellow. Reserved for sudden death rounds, golden score judo states, warning bells, and elite badges.
- **Display Text / Foreground (`#F5F1E6`)**: Warm crisp cream. Provides superior optical clarity against obsidian navy, reducing the optical vibration typically caused by pure hex `#FFFFFF`.

## Typography

The type system pairs the punchy, tall proportions of `Bebas Neue` with the geometric, technical precision of `Space Grotesk`.

### Display & Headlines (`Bebas Neue`)
All primary metrics—countdown clocks, round counters, phase statuses ("FIGHT", "REST", "GOLDEN SCORE")—leverage uppercase `Bebas Neue`. For maximum dramatic manga flair, hero status titles can adopt a forward faux-italic angle (`transform: skewX(-6deg)`). Tracking is slightly opened to ensure distinct numeral recognition from 3–5 meters away.

### Body & Data Interfaces (`Space Grotesk`)
Interval lists, setup forms, audio cues, and workout summaries employ `Space Grotesk`. Its mechanical nuances, monospaced-style figures, and wide aperture maintain structural balance alongside the tall display type. Labels and tactical indicators are always rendered in uppercase with wide letter-spacing (`0.08em` to `0.1em`).

## Layout & Spacing

The layout is built upon a fluid single-column model for compact hand-held mobile screens that adapts to a structured 4-column layout on wide landscape mounts (such as tablet ring timers or gym wall docks).

### Safe Zones & Athletic Clearance
- **Margins**: A persistent `1.25rem` outer canvas padding guarantees that touchable triggers stay well clear of device chassis edges and hardware bezels.
- **Vertical Hierarchy**: When a timer is actively running, 65% of the viewport is allocated directly to the primary metric cluster (Time Remaining, Round X/Y, Phase Badge). Secondary controls sit grouped in the lower 35% thumb zone.
- **Touch Target Integrity**: All tap targets enforce a strict minimum boundary of `56px` height (expanding up to `72px` for primary Play/Pause/Buzzer triggers) to accommodate taped hands, boxing gloves, or sweaty thumb interactions without mis-clicks.

## Elevation & Depth

This design system rejects blurred, ambient Gaussian drop shadows in favor of crisp, graphic dimensionality inspired by comic book printing and screen-toned cel art.

### Solid Ink Shadows
- **Base Offset Depth**: Primary cards and interactive modules apply hard, solid black offset shadows (`box-shadow: 4px 4px 0px #0A0A0A`).
- **Elevated Hero Controls**: Active floating controls, large start buttons, or golden score alerts scale up the offset (`box-shadow: 6px 6px 0px #000000`).
- **Pressed State Dynamics**: When pressed, interactive buttons shift down and right (`transform: translate(2px, 2px); box-shadow: 2px 2px 0px #0A0A0A`), simulating a mechanical punch card click.

### Shonen Graphic Embellishments
- **Halftone Dot Pattern**: Subtle, low-opacity halftone screentones (`radial-gradient(#1A2547 15%, transparent 16%)` at 8px spacing) are applied to empty module backgrounds or phase transition cards.
- **Speed Line Overlays**: During the final 10 seconds of a round or within Golden Score, subtle SVG diagonal speed lines sweep inwards from panel perimeters to build tension without impairing timer readability.

## Shapes

The design system is strictly anchored to `roundedness: 0` (Sharp). Every structural perimeter, card, button, and indicator uses sharp 90-degree corners or aggressive 45-degree chamfers (`clip-path: polygon(...)`).

### Chamfers & Geometry
- **Combat Badges**: Badges for round counts and phase titles feature clipped diagonal top-right and bottom-left corners (e.g., `clip-path: polygon(8px 0, 100% 0, 100% calc(100% - 8px), calc(100% - 8px) 100%, 0 100%, 0 8px)`).
- **Ink Framing**: All surfaces are locked within explicit `2px` or `3px` solid `#0A0A0A` borders.
- **Status Slashes**: Section separators make frequent use of forward-slanted comic panel dividers (`transform: skewX(-12deg)`).

## Components

### Buttons & Core Triggers
- **Primary Kinetic Trigger (Fight / Start)**: Filled in `#E31E24` with `#F5F1E6` text, bordered in `3px solid #0A0A0A`, featuring `5px 5px 0px #000000` solid drop shadow. Minimum height is `64px`. Typography is set in `headline-lg` (`Bebas Neue`).
- **Rest / Recovery Action**: Filled in `#1746C4` with `#F5F1E6` text and standard ink border.
- **Secondary / Utility Trigger**: Background `#1A2547`, border `2px solid #0A0A0A`, text `#F5F1E6`.
- **Active / Pressed State**: Button translates `+2px` horizontally and vertically; drop shadow collapses to `2px 2px 0px #0A0A0A`.

### Display Cards & Panels
- **Container Structure**: Background `#121A33` with a `2px solid #0A0A0A` frame and `4px 4px 0px #000000` offset shadow.
- **Card Headers**: Angular banner ribbons across top borders with inverted cream-on-obsidian fills and uppercase `label-lg` tracking.

### Phase & Tactical Badges
- **Fight Badge**: High-saturation red background (`#E31E24`), sharp chamfered edges, bold cream text, bordered in thick black.
- **Rest Badge**: Cobalt blue background (`#1746C4`), clean chamfer cuts.
- **Golden Score / Overtime**: Signal yellow (`#FFD400`) background with jet-black `#0A0A0A` typography for extreme punch and alertness.

### Input Fields & Steppers
- **Round / Interval Steppers**: Massive split counters featuring stacked `+` and `-` blocks with `56px` minimum touch clearance. Counter value displayed in bold `headline-xl`.
- **Form Inputs**: Background `#0B1020`, inner inset `2px solid #0A0A0A`, active focus ring shifts to a razor-sharp `2px solid #FFD400` border without blur.

### Match Progress & Round Tracker (Manga Meter)
- **Segmented Bar**: Discrete rectangular cells representing rounds. Completed rounds are solid `#E31E24`, active round pulses with an animated diagonal `#FFD400` hazard slash, upcoming rounds remain `#1A2547` with dark outlines.