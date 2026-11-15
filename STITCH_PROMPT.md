# Google Stitch prompt — Combat Timer

Paste into https://stitch.withgoogle.com (Mobile mode). One prompt per screen works best; the "App overview" block goes first in each.

---

## App overview (prefix every prompt with this)

Mobile app "Combat Timer" — a round timer for martial arts (MMA, boxing, judo) used in a loud gym, glanced at from 3–5 m away while training. Dark theme. Visual style: shonen-manga fight poster — thick black ink outlines, halftone dots, speed lines, bold italic condensed display type, slight panel tilt. Palette: Night #0B1020 background, Panel Navy #121A33 / #1A2547 cards, Burst Blue #1746C4, Glove Red #E31E24, Impact Yellow #FFD400, Paper #F5F1E6 text, Ink #0A0A0A outlines. Big touch targets (min 56dp), high contrast, no small text.

## Screen 1 — Sport selection

Title "COMBAT TIMER" in a manga burst header, subtitle "Pick your fight". Vertical list of 8 tappable fight cards, each showing name + ruleset summary + chevron, each card with a different accent stripe:
- MMA Amateur — 3 × 3:00, rest 1:00
- MMA Pro (Regular Fight) — 3 × 5:00, rest 1:00
- MMA Pro (Title Fight) — 5 × 5:00, rest 1:00
- Boxing Amateur — 3 × 3:00, rest 1:00
- Boxing Professional — 4–12 rounds × 3:00, rest 1:00 (configurable badge)
- Judo — 4:00 + Golden Score
- Interval Setting — custom (configurable badge)
- Shark Tank — back-to-back rounds, no rest (configurable badge)

## Screen 2 — Configure

"< BACK" top-left, sport name as comic title. Stacked stepper rows (label, big value, round − / + buttons in red outline): "ROUNDS" (e.g. 6), "ROUND TIME" (3:00, step 0:15), "REST TIME" (1:00, step 0:15). Summary line "Total: 23:00". Full-width red "START" button pinned to the bottom.

## Screen 3 — Timer (running)

"EXIT" top-left, "ROUND 2 / 5" top-right. Huge seven-segment style countdown "02:47" filling ~40% of the screen inside a framed panel. Phase banner above it changes color by phase: "FIGHT!" (red), "REST" (blue), "GOLDEN SCORE" (yellow, counts up), "FINISHED" (yellow burst). Round progress dots row. Bottom: large primary "PAUSE"/"RESUME" button + outlined "RESET" button. Manga speed lines behind the clock while running; last 10 seconds clock pulses red.

## Screen 4 — Judo golden-score decision

Same timer layout, clock frozen at "00:00", banner "TIME!". Two big buttons: outlined "MATCH DECIDED" and yellow "GOLDEN SCORE".

## Screen 5 — Finished

Big "FINISHED!" manga impact burst, summary "5 rounds · 29:00", buttons "RESTART" and "NEW FIGHT".
