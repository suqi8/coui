# COUI Canvas

COUI Canvas is a browser tool for sketching ColorOS 17 screens with the real COUI components. Lay parts out on phone or wide screens, connect the screens and tap through the flow, then hand the design to an AI coding tool as a prompt, or take the Kotlin code it generates. It runs entirely in the browser: no account, no server, nothing uploaded.

<a href="/coui/canvas/" target="_blank">Open COUI Canvas</a>

## How it works

1. **Lay out**: click a part in the palette to add it to the selected screen, or drag it onto the canvas. Parts snap to the 4dp grid, to the 16dp screen margins and to each other. Preference rows dropped against another row run join it and become one `Card` + `CardListGroup`; buttons and chips side by side join into one row. **Tidy** pins bars to the edges and stacks the rest the COUI way.
2. **Connect and preview**: give a row, a button, a tab or a dialog button a target screen (or *Go back*) and a transition in the inspector. **Preview** plays the flow with COUI page transitions; switches flip, sliders slide, dialogs close.
3. **Hand it over**:
   - **Prompt**: the whole app (or one screen) as a build brief in Chinese or English, naming the exact COUI component for every part and pointing the tool at the [`coui-app` skill](./ai).
   - **Kotlin code**: one page per screen built on the skill's `CouiPage` scaffold, the navigation with `coui-nav`, the theme, and `strings` resources.
   - **Share link**: the design compressed into the address (`#docz=`), so opening the link opens the design.

## Parts

| Section | Parts |
| --- | --- |
| Bars | Top bar (large title, Settings home with search, small), search bar, tabs, floating navigation bar |
| Lists | Category title, switch, arrow, action, dropdown, checkbox, radio, check-mark, slider and input rows |
| Buttons | Button (primary, secondary), icon button, FAB, chip |
| Content | Card, text, image placeholder, top tips, empty state, text field, slider, progress, divider |
| Overlays | Dialog with the COUI 17 button bar, bottom sheet, snackbar |

Every part is drawn by the library itself, in the design's theme (light or dark, any key colour), so what you sketch is what the code will look like.

## Sketches from an AI agent

**Share & files → Copy instructions for an AI agent** copies a short brief for Claude Code, Codex or any agent that can read a web page. The agent reads [`agent.md`](/coui/canvas/agent.md), writes the design document, and replies with a link that opens its sketch on your canvas, where you can refine it before generating the prompt or code.

## Files

**Save project** writes the design as a `.json` file; **Open project** reads one back, including documents written by agents. The canvas also keeps the current design in the browser, so it is still there next time.

## Keyboard

| Keys | Action |
| --- | --- |
| Wheel, `Ctrl` + wheel | Pan, zoom |
| Drag on empty canvas | Pan |
| `Ctrl` + `+` / `-` / `0` | Zoom in, zoom out, fit |
| `Ctrl+Z` / `Ctrl+Shift+Z` | Undo / redo |
| `Ctrl+D` | Duplicate |
| Arrow keys (`Shift` = 8dp) | Nudge |
| `Ctrl` + drag | Move without snapping |
| `Delete` | Delete the part or screen |
| `T` | Tidy |
| `P` | Preview |

## Run it locally

```bash
./gradlew :docs:canvas:wasmJsBrowserDevelopmentRun
```

The build for the docs site is `./gradlew :docs:canvas:wasmJsBrowserDistribution` (output in `docs/canvas/build/dist/wasmJs/productionExecutable`).
