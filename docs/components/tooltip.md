# Tooltip

`Tooltip` is a port of COUI 17's `COUIToolTips`: a bubble with an arrow pointing at an anchor element. A `TooltipBox` anchors it to its content and positions it; its `tooltip` slot takes one of the three COUI bubble styles — `Tooltip` (Default), `IconTooltip` (Icon) or `ImageTooltip` (Image). Like COUI, tooltips are shown programmatically through `TooltipState` and have no timeout.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tooltip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TooltipBox
import io.github.suqi8.coui.kmp.basic.Tooltip
import io.github.suqi8.coui.kmp.basic.IconTooltip
import io.github.suqi8.coui.kmp.basic.ImageTooltip
import io.github.suqi8.coui.kmp.basic.TooltipBubble
import io.github.suqi8.coui.kmp.basic.TooltipStyle
import io.github.suqi8.coui.kmp.basic.TooltipMode
import io.github.suqi8.coui.kmp.basic.TooltipAnchorPosition
import io.github.suqi8.coui.kmp.basic.TooltipState
import io.github.suqi8.coui.kmp.basic.rememberTooltipState
import io.github.suqi8.coui.kmp.basic.TooltipDefaults
```

## Basic Usage

```kotlin
val state = rememberTooltipState()
val scope = rememberCoroutineScope()

TooltipBox(
    state = state,
    tooltip = { Tooltip(text = "Tap the close icon to dismiss this tip.") },
) {
    TextButton(text = "Show tip", onClick = { scope.launch { state.show() } })
}
```

## Styles

| Style | Composable | Look |
| ----- | ---------- | ---- |
| `TooltipStyle.Default`, `TooltipMode.Tooltips` | `Tooltip(text)` | Accent bubble (`couiColorTips`, here `primary`), white 13sp text, 18dp close icon at the end |
| `TooltipStyle.Default`, `TooltipMode.Info` | `Tooltip(text, mode = TooltipMode.Info)` | White "detail floating" bubble (`backgroundTop`), secondary 13sp text, no close icon, at least 48dp wide |
| `TooltipStyle.Icon` | `IconTooltip(text, icon)` | White card: 40dp icon with 8dp corners, optional 16sp medium title, 13sp body, optional dismiss text button |
| `TooltipStyle.Image` | `ImageTooltip(text, image)` | 260dp white card: cropped image (228 × 116dp inset with 8dp corners, or a full-bleed 132dp image), optional title, body, optional dismiss button |

Every bubble is a 17dp smooth round rect at weight 3.0 (`coui_tooltips_os_16_1_radius_17_dp`, `OS_16_1_WEIGHT`), at most 328dp wide (Image: 260dp), with the COUI SHADOW_LV4 shadow. The arrow (20 × 10.1dp with a 2dp rounded tip and flared base, or the 16.27 × 10.29dp edge variant near a corner) is joined to the bubble into one outline.

## TooltipBox

```kotlin
@Composable
fun TooltipBox(
    state: TooltipState,
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    showArrow: Boolean = true,
    dismissOnTouchOutside: Boolean = false,
    enableUserInput: Boolean = false,
    offset: DpOffset = DpOffset.Zero,
    arrowFollowsOffset: Boolean = false,
    content: @Composable () -> Unit,
)
```

| Parameter Name | Type | Description | Default Value | Required |
| -------------- | ---- | ----------- | ------------- | -------- |
| state | TooltipState | State controlling visibility | - | Yes |
| tooltip | @Composable TooltipScope.() -> Unit | The bubble: `Tooltip`, `IconTooltip`, `ImageTooltip` or a `TooltipBubble` | - | Yes |
| modifier | Modifier | Modifier applied to the anchor wrapper | Modifier | No |
| placement | TooltipAnchorPosition | Preferred side: `Above`, `Below`, `Left`, `Right`, `Start`, `End` | Above | No |
| showArrow | Boolean | Whether the arrow is drawn | true | No |
| dismissOnTouchOutside | Boolean | Whether an outside tap or Back dismisses the tooltip (makes it focusable) | false | No |
| enableUserInput | Boolean | Whether hover / long press on the anchor shows the tooltip | false | No |
| offset | DpOffset | Extra offset of the tooltip window (x mirrored in RTL) | DpOffset.Zero | No |
| arrowFollowsOffset | Boolean | Whether the arrow moves with `offset` instead of staying on the anchor | false | No |
| content | @Composable () -> Unit | The anchor content | - | Yes |

### Placement

- **Above / Below**: centred on the anchor, kept 16dp from the window edges. When the preferred side has no room the tooltip moves to the other side; when neither side fits it takes the larger one and its content scrolls. The bubble sits about 5.4dp + its 13dp window inset from the anchor, which puts the arrow tip about 8.6dp from it.
- **Left / Right / Start / End**: vertically centred on the anchor, 5.7dp away. They never flip sideways: the bubble narrows to the room on that side, and moves above the anchor when it would reach the bottom of the window.
- The arrow follows the anchor centre along the bubble edge. Within 9dp + 2dp of a corner it switches to the edge arrow, whose straight flank continues the bubble side.

### Animation and dismissal

The tooltip grows from the arrow tip: scale and alpha spring from 0 to 1 (COUISpringForce bounce 0.2, response 0.4 for the scale and 0.3 for the alpha). Every dismiss restarts both springs from their start values and runs them back to 0 — an exit that interrupts an enter first jumps to full size, as in COUI — drops the shadow, ignores touches, and removes the window 320ms after it began; a second `dismiss()` during that exit removes it at once.

It is dismissed by `state.dismiss()`, by the close icon / dismiss button, or — with `dismissOnTouchOutside` — by an outside tap or Back. It disappears immediately, without the exit animation, when its anchor leaves the composition, scrolls out of its visible area or collapses to zero size.

### Toolbar and navigation anchors

COUI pulls a tooltip into a toolbar or bottom navigation bar instead of keeping the 5.4dp gap: an anchor inside a toolbar uses a -10.1dp gap on every side, an anchor inside a bottom navigation bar -3.3dp plus the distance from the bar top to the anchor top; both also count toward the room below the anchor. `TopAppBar` / `SmallTopAppBar` (navigation icon and actions) and `NavigationBar` mark themselves for this. Wrap a custom bar with `ProvideTooltipAnchorContainer(TooltipAnchorType.Toolbar)` or `TooltipAnchorType.Navigation` to get the same spacing.

## Tooltip

```kotlin
@Composable
fun TooltipScope.Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    mode: TooltipMode = TooltipMode.Tooltips,
    colors: TooltipColors = TooltipDefaults.tooltipColors(mode = mode),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onCloseClick: (() -> Unit)? = null,
)
```

The close icon (Tooltips mode only, content description from COUI's `coui_tool_tips_delete_icon_description` in the current locale, e.g. "Close this tip" / "关闭提示", 58dp touch target) dismisses the tooltip and then calls `onCloseClick`. An overload takes a `content` lambda instead of `text` for custom content, framed by the same 12dp margins (42dp at the end in Tooltips mode).

## IconTooltip

```kotlin
@Composable
fun TooltipScope.IconTooltip(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Icon),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onDismissClick: (() -> Unit)? = null,
)
```

## ImageTooltip

```kotlin
@Composable
fun TooltipScope.ImageTooltip(
    text: String,
    image: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    imageWithEdges: Boolean = true,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Image),
    maxWidth: Dp = TooltipDefaults.ImageMaxWidth,
    onDismissClick: (() -> Unit)? = null,
)
```

An overload takes a `media: @Composable BoxScope.() -> Unit` slot instead of `image`, e.g. for a looping animation. The Icon and Image scroll areas fade their top / bottom 24dp while scrolled.

```kotlin
TooltipBox(
    state = state,
    placement = TooltipAnchorPosition.Below,
    tooltip = {
        ImageTooltip(
            title = "What's new",
            text = "Swipe down with three fingers to take a screenshot.",
            image = painterResource(Res.drawable.screenshot_gesture),
            dismissText = "Got it",
        )
    },
) { /* anchor */ }
```

## TooltipBubble

`TooltipBubble(style, mode, containerColor, maxWidth, cornerRadius, content)` is the bubble the three styles are built on: window inset, arrow, shadow, sizing and clipping. Use it for fully custom content. It measures its content for real — a side tooltip that falls back above the anchor is re-measured at full width, and a tooltip that fits neither above nor below is re-measured at the height of the larger side — so lazy lists work inside it.

## TooltipState

```kotlin
@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = true,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState
```

`isPersistent` defaults to true: a COUI tooltip stays until it is dismissed. Pass false to dismiss it automatically after the platform tooltip timeout. `show()` is a suspend function; a global `MutatorMutex` keeps only one tooltip visible at a time.

## TooltipDefaults

| Member | Description | Value |
| ------ | ----------- | ----- |
| CornerRadius | Bubble corner radius | 17.dp |
| CornerWeight | Bubble corner weight | 3.0f |
| MaxWidth | Default / Icon max width | 328.dp |
| ImageMaxWidth | Image bubble width | 260.dp |
| InfoMinWidth | Info window min width (inset included) | 80.dp |
| windowInset(style, mode) | Transparent inset around the bubble for the arrow and shadow | 13 × 13dp; Info 16 × 13dp |
| maxWidth(style) / minWidth(style, mode) | Per-style width limits | - |
| tooltipColors(style, mode, …) | `TooltipColors(containerColor, contentColor, titleColor, actionColor)` | Tooltips: `primary` / `onPrimary`; others: `backgroundTop` / `labelSecondary`; title `labelPrimary`; action `primary` |
