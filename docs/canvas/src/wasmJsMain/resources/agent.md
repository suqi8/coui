# COUI Canvas: sketches from an AI agent

COUI Canvas is a browser editor for ColorOS 17 screens built from the real COUI components (the Compose Multiplatform library `io.github.suqi8.coui.kmp`). A design is one JSON document. You, the agent, write that document and hand it back; the person opens it on their canvas, refines it, and turns it into a prompt or Kotlin code.

## What to deliver

**Reply with a share link.** If you cannot run code, reply with the JSON document in a code block instead; the person saves it as a `.json` file and opens it with **Share & files → Open project**. Do not verify, decode or round-trip your output: the app checks the document when it opens and tells the person what it repaired.

To make the link:

1. Save the document to a file, for example `design.json`. Do not inline it in a shell command (quoting breaks in PowerShell and long shells).
2. Run one of these on the file and reply with the printed link. `BASE` is the address you read this guide from, without `agent.md` (for example `https://example.com/coui/canvas/`).

```js
// Node (link.mjs): node link.mjs design.json https://example.com/coui/canvas/
import { readFileSync } from "node:fs";
import { deflateRawSync } from "node:zlib";
const [file, base] = process.argv.slice(2);
console.log(base + "#docz=" + deflateRawSync(readFileSync(file, "utf8")).toString("base64url"));
```

```python
# Python (link.py): python link.py design.json https://example.com/coui/canvas/
import sys, zlib, base64
data = open(sys.argv[1], "rb").read()
c = zlib.compressobj(9, zlib.DEFLATED, -15)          # raw deflate, no header
raw = c.compress(data) + c.flush()
print(sys.argv[2] + "#docz=" + base64.urlsafe_b64encode(raw).decode().rstrip("="))
```

The link is long (a few thousand characters for a few screens); it carries the whole design and nothing is stored anywhere. Keep the document under about 100 KB.

Rough placement is fine. The person presses **Tidy** and bars snap to the edges, the FAB goes to its corner, and the rest stacks the COUI way (16dp side margins, 16dp under every card, category titles right above their card). Spend your effort on the right parts, real labels and the navigation between screens.

## The document

```jsonc
{
  "title": "Notes",                     // the app's name
  "brief": "Write and organise notes.",  // one or two sentences on what the app is for
  "theme": { "dark": false, "bothModes": true, "keyColor": null, "platform": "android" },
  "frames": [ /* screens */ ],
  "groups": [ /* parts, bottom layer first */ ]
}
```

`theme` is optional. `keyColor` is `"#RRGGBB"` or `null` for the stock COUI blue. `platform` is `android` or `multiplatform` (Compose Multiplatform). `bothModes: true` means the app follows the system's light and dark mode; `dark` picks which one the canvas shows.

### Screens (`frames`)

A phone screen is **360 × 800** dp (the defaults, so omit `w` and `h`); a wide screen is **1024 × 720**. Place screens side by side, 80 apart:

```json
{ "id": "home", "name": "Notes", "x": 0, "y": 0, "note": "Lists every note, newest first." }
{ "id": "edit", "name": "Edit note", "x": 440, "y": 0 }
{ "id": "settings", "name": "Settings", "x": 880, "y": 0 }
```

- `id`: any unique string. `name`: what the screen is called in the prompt and the code.
- `note` (optional): what the screen is for, in a sentence. It goes into the prompt.
- `length` (optional): the scroll length in dp when the content is longer than the screen (for example `1400`).
- `bg` (optional): `card` for the card colour; the default is the window background.
- `swipe` (optional): `{ "left": "<frame id>", "right": "<frame id>" }`, screens reached by swiping.

Every phone screen draws a 32dp status bar at its top; a top bar already leaves room for it. Without a top bar, start content at `y + 48`.

### Parts (`groups`)

Every part sits in a **group**. Coordinates are **canvas coordinates**, so add the screen's `x` and `y`. Later groups draw on top of earlier ones. A group is one part, or a **connected run** drawn as a unit:

- preference rows stacked in **one card** (`"axis": "y"`) — the COUI settings layout: each run is a `Card { CardListGroup { … } }` with the dividers and rounded ends drawn for you;
- buttons, icon buttons or chips side by side (`"axis": "x"`).

```json
{ "id": "g1", "x": 0, "y": 0, "items": [ { "id": "bar", "kind": "topBar", "label": "Notes", "variant": "home", "supporting": "Search notes" } ] }
{ "id": "g2", "x": 0, "y": 216, "items": [ { "id": "c1", "kind": "category", "label": "Pinned" } ] }
{ "id": "g3", "x": 16, "y": 256, "axis": "y", "items": [
  { "id": "n1", "kind": "arrowRow", "label": "Shopping list", "supporting": "Milk, eggs, bread", "action": { "to": "edit" } },
  { "id": "n2", "kind": "arrowRow", "label": "Ideas", "supporting": "Edited yesterday", "action": { "to": "edit" } }
] }
{ "id": "g4", "x": 280, "y": 616, "items": [ { "id": "fab", "kind": "fab", "icon": "Add", "action": { "to": "edit", "transition": "up" }, "note": "Starts a new empty note." } ] }
{ "id": "g5", "x": 0, "y": 704, "items": [ { "id": "nav", "kind": "bottomNav",
  "tabs": [ { "label": "Notes", "icon": "Notes" }, { "label": "Tasks", "icon": "Tasks" }, { "label": "Settings", "icon": "Settings" } ],
  "selected": 0, "actions": { "tab:2": { "to": "settings", "transition": "fade" } } } ] }
```

Every item needs `id` and `kind`; everything else is optional. Rows of a run share the run's width (content width **328** on a phone, at `x = screen.x + 16`).

### Kinds

| kind | COUI component | useful fields | size |
|---|---|---|---|
| `topBar` | `TopAppBar` (collapsing large title) | `label` title; `variant` `large` (default) / `home` (ColorOS Settings home: 40sp title and a search capsule, `supporting` = its placeholder; it searches in place, linked with the bar, unless `actions.search` opens another frame) / `small`; `supporting` subtitle (large/small); `icon: "Back"` shows the back button; `icon2` one action icon; `actions` keys `icon`, `icon2`, `search` | full width, ~150 tall (home ~220), at the top |
| `searchBar` | `InputField` search capsule | `label` placeholder, `action` | 328 × 52 |
| `tabs` | `TabRow` | `tabs` (labels), `selected`, `variant` `underline` / `contour` | 328 × 48 |
| `bottomNav` | `FloatingNavigationBar` | `tabs` (2–5 of `{label, icon}`), `selected`, `actions` keys `tab:0`… | full width, ~96 tall, at the bottom |
| `category` | `PreferenceCategory` | `label` | full width (`x = screen.x`), 40 tall, right above its card |
| `switchRow` | `SwitchPreference` | `label`, `supporting` summary, `icon`, `checked` | run row, ~64 |
| `arrowRow` | `ArrowPreference` (opens a page) | `label`, `supporting`, `end` value on the right, `icon`, `action` | run row |
| `basicRow` | `BasicComponent` (runs an action / opens a dialog) | `label`, `supporting`, `end`, `icon`, `action` | run row |
| `dropdownRow` | `OverlayDropdownPreference` | `label`, `tabs` the options, `selected`, `icon` | run row |
| `checkboxRow` / `radioRow` / `markRow` | `CheckboxPreference` / `RadioButtonPreference` / `MarkPreference` | `label`, `supporting`, `checked` | run row |
| `sliderRow` | `SliderPreference` | `label`, `value` 0–100, `end` value text | run row, ~96 |
| `inputRow` | `InputPreference` | `label`, `supporting` hint | run row |
| `button` | `TextButton` | `label`, `variant` `primary` / `secondary`, `size` width (328 = full content width, 158 = half), `action` | 44 tall |
| `iconButton` | `IconButton` | `icon`, `action` | 40 × 40 |
| `fab` | `FloatingActionButton` | `icon`, `action` | 56 × 56, bottom right |
| `chip` | `Chip` | `label`, `icon`, `checked` selected, `action` | 32 tall |
| `card` | `Card` | `label` title, `supporting` body, `icon`, `size`, `size2` min height, `action` | 328 wide |
| `text` | `Text` | `label`, `variant` `title` / `body` / `caption`, `size` | |
| `image` | image placeholder | `label` what the picture shows, `size`, `size2` height | 328 × 180 |
| `topTips` | `TopTips` | `label` text, `supporting` action label, `icon`, `action` | 328 wide |
| `emptyState` | `EmptyState` | `label` title, `supporting` subtitle, `end` button label, `action` | 328 × ~260 |
| `textField` | `TextField` | `label` placeholder, `supporting` hint, `variant` `line` / `filled` | 328 × 52 |
| `slider` | `Slider` | `value` 0–100 | 328 × 36 |
| `progress` | progress indicator | `variant` `linear` / `circular` / `loading`, `value` | |
| `divider` | `HorizontalDivider` | | 328 × 16 |
| `dialog` | `OverlayDialog` with `DialogButtonBar`, drawn open | `label` title, `supporting` message, `tabs` 1–3 buttons (the last is the recommended one), `actions` keys `tab:0`… | 328 wide, near the bottom of the screen |
| `bottomSheet` | `OverlayBottomSheet`, drawn open | `label` title, `size2` height; parts placed inside its box are its content | full width, at the bottom |
| `snackbar` | snackbar | `label` message, `supporting` action label | 328 wide, above the bottom bar |

Fields any part may carry:

- `note`: what the part does, in your words. It goes into the prompt verbatim, so say what happens on tap, what is saved, what is validated.
- `action`: `{ "to": "<frame id>" | "back", "transition": "push" | "fade" | "up" | "none" }`, the screen a tap opens (`push` is the default COUI page transition).
- `actions`: the same, per target, for parts with several (`tab:N`, `icon`, `icon2`, `search`).

Icons are `COUIIcons` names: `Add`, `Back`, `Search`, `Settings`, `Home`, `Edit`, `Delete`, `Share`, `More`, `Info`, `Notes`, `Tasks`, `Photos`, `Image`, `Music`, `Messages`, `Email`, `Phone`, `Contacts`, `Location`, `Lock`, `Favorites`, `Download`, `Folder`, `File`, `Alarm`, `Timer`, `Theme`, `Tune`, `VolumeUp`, `Store`, `Help`, `Scan`, `Send`, `Copy`, `Filter`, `Sort`, `Refresh`, … An unknown name shows no icon.

## Keep it COUI

- A settings-style page is: top bar, then for each topic a `category` and a run of rows. One run per card; do not stack single-row groups when they belong in one card.
- Pick the row for the job: on/off → `switchRow`; opens a page → `arrowRow` with an `action`; runs something or opens a dialog → `basicRow`; one of a few → `dropdownRow`; a level → `sliderRow`.
- The home page of an app uses `"variant": "home"` on its top bar; pages pushed from it use the default large title with `"icon": "Back"` and `"actions": { "icon": { "to": "back" } }`.
- Dialogs and sheets are drawn open on the screen they belong to; if a tap should open one, put it on its own copy of the screen or say so in the tapped part's `note`.
- Real labels in the person's language, not lorem ipsum. Leave out what the app does not need: no `icon`, no `supporting`, no `note` by default.
- Three to six screens is plenty. Navigation should close: rows open detail screens, detail screens go back.

## Checklist before you reply

- Every `id` is unique; every `action.to` is a frame `id` or `back`.
- Group coordinates include the screen offset; rows are in runs (`"axis": "y"`), not one group per row.
- You are replying with the link (or the JSON), not with a description of it.
