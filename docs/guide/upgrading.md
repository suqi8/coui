# Upgrading

## Unreleased

### TabRow is the sliding tab layout

The plain `TabRow` used to draw the segment button without its container — a shape COUI does not
have. It is now the ColorOS sliding tab layout (`COUITabLayout`): a 48dp row of labels over a 2dp
track, with a 2dp underline indicator under the selected label. `TabRowWithContour` is unchanged and
remains the segment button.

Your code still compiles, but the row looks and behaves differently:

- The default height is 48dp (was 40dp), and 72dp when a tab has an icon and a label.
- `colors` now come from `TabRowDefaults.underlineColors()`: `backgroundColor` is the track and
  `selectedBackgroundColor` the indicator. Pass `TabRowDefaults.tabRowColors()` explicitly only if
  you want the segment colors on the new layout.
- `cornerRadius` shapes the 8dp press mask instead of a capsule indicator, `minWidth` defaults to
  0dp, and `maxWidth` defaults to 70% of the row.
- `itemSpacing` is the minimum gap between labels and, like the new `horizontalMargin`, follows the
  COUI screen size when left unspecified.
- The press mask comes from `LocalIndication`; pass `indication = null` to remove it.
- An overflowing row has fading edges and the COUI rubber-band overscroll.

New: `enabled`, `pagerState` (the row follows a dragged pager) and a `TabRow(items = ...)` overload
taking `TabRowItem`s with an icon, a red dot badge or a disabled state. If you need the old
look, use `TabRowWithContour` with a transparent `backgroundColor`.

### Tooltip is the COUI 17 tooltip

The Miuix tooltip (a caret-less Material-style `PlainTooltip` / `RichTooltip`) is replaced by a
port of `COUIToolTips`. The new entry point is `TooltipBox(state, tooltip)` with one of the three
COUI bubble styles: `Tooltip` (the accent tooltips bubble with a close icon, or
`mode = TooltipMode.Info` for the white bubble), `IconTooltip` and `ImageTooltip`. Every bubble draws
an arrow at the anchor, grows from it on a spring, and places itself the COUI way (above by default,
16dp from the window edges).

The old API still compiles but is deprecated: `TooltipBox(positionProvider, …)`, `TooltipBox(text, …)`,
`RichTooltipBox`, `PlainTooltip`, `RichTooltip`, `RichTooltipColors`,
`TooltipDefaults.rememberTooltipPositionProvider`, `TooltipDefaults.caretShape()` / `caretSize` and
the `PlainTooltip*` / `RichTooltip*` constants. Migrate like this:

```kotlin
// Before
TooltipBox(text = "Edit") { anchor() }

// After
val state = rememberTooltipState()
TooltipBox(
    state = state,
    tooltip = { Tooltip(text = "Edit", mode = TooltipMode.Info) },
    enableUserInput = true, // keep the old long-press / hover trigger
) { anchor() }
```

`RichTooltipBox(title, text, actionText)` maps to `IconTooltip(title = …, text = …, dismissText = …)`
or `ImageTooltip`.

Behaviour changes:

- `rememberTooltipState()` now defaults to `isPersistent = true`: COUI tooltips have no timeout. Pass
  `isPersistent = false` to keep the auto-dismiss.
- The new `TooltipBox` defaults to `enableUserInput = false` (COUI shows tooltips programmatically)
  and `dismissOnTouchOutside = false`; set them to keep the old long-press / outside-tap behaviour.
- The new bubbles still render inside the deprecated `TooltipBox(positionProvider, …)`, without an
  arrow.

## 1.0.0 → 1.1.0

Update the version in every COUI coordinate you depend on:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
        }
    }
}
```

This release recalibrates most components against ColorOS 16 — partly from decompiled resources,
partly from measuring the real thing on a device. Two of them corrected values we had inherited
from Miuix and never checked. **Expect visible changes even where you changed nothing**, and give
your screens a look after upgrading.

### One compile break

`InfiniteProgressIndicator` lost its `orbitingDotSize` parameter, along with
`ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize`. The indicator used to
be a ring with a dot orbiting inside it — a Miuix design with no counterpart in COUI, which draws a
bare arc whose sweep pulses as it spins. There is no dot left to size.

```kotlin
// Before
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp, orbitingDotSize = 4.dp)

// After — drop the argument
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp)
```

The same arc is also available directly as `RotatingProgressIndicator`, with the two size tiers COUI
ships (16dp and 26dp).

### Text is taller

Every entry in `COUITheme.textStyles` now carries the line-spacing multiplier its COUI text
appearance declares, so text occupies more vertical space than it did in 1.0.0. `headline1` also
dropped from 17sp to **16sp**: a preference title measured 21.71dp on a ColorOS 16 device, which is
16sp at COUI's multiplier, not the 17sp Miuix used.

If you sized a container to fit a specific string, re-check it. If you were compensating for the
tightness with your own `lineHeight`, remove it — you will now be adding leading twice.

### Rows and dialogs shifted

- A row's height now depends on its position in its card group: a standalone row is 4dp taller than
  a middle row, and a first or last row 2dp taller, on the edge that is rounded. Nothing changes
  unless you pass `cardListPosition`, which every preference component now accepts.
- Dialog button bars stack vertically when a label is too wide to fit, instead of squeezing it. The
  stacked tier has its own metrics, and the buttons reverse order — cancel moves from the far left
  to the bottom.
- Dialog bar buttons no longer shrink when pressed, and their press tint fills the whole cell as a
  square rather than a capsule. `Button` and `TextButton` gained `pressScaleEnabled` for this;
  it defaults to `true`, so your own buttons are unaffected.

### Dropdown menus behave differently

The flat dropdown was rebuilt around COUI's own gesture model, so the interaction changed rather
than just the pixels:

- On a menu short enough not to scroll, **dragging moves the highlight between rows** and releasing
  selects whichever row the finger is over — not the one first pressed. Each row crossed fires a
  haptic. Once the menu scrolls, dragging scrolls it instead.
- A dropdown opened from a preference row now appears **at the point you tapped**, not centred under
  the row.
- Menus no longer dim the content behind them. `enableWindowDim` now defaults to `false` on
  `OverlayListPopup`, `WindowListPopup` and both cascading popups; pass `true` if you want the old
  behaviour.
- Rows are separated by hairlines, and the ones next to the pressed row fade out while it is held.

### New in this release

`DialogButtonBar` (with `DialogButtonBarAction`) replaces hand-rolled dialog button rows and handles
the vertical stacking for you. Dropdown items gained a `hint` slot for a trailing badge, an `alert`
flag for destructive entries, and `DropdownEntry.title` for in-menu group headers.

## Older versions

Coming from Miuix rather than an earlier COUI release? See [Migrating from Miuix](./migration.md).
