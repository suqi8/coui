# DialogButtonBar

`DialogButtonBar` is the button bar of a COUI alert dialog, mirroring ColorOS 17's `COUIButtonBarLayout`. On COUI 17 the bar holds **filled capsule buttons** (`COUILoadingButton` styled `COUIAlertDialogBottomButtonSecondary`) instead of the borderless full-width cells of earlier releases, and there are no dividers. Its defining behaviour is **automatic vertical stacking**: the bar measures its own labels, and as soon as one of them is too wide to fit in its cell, the whole bar flips from a horizontal row to a vertical stack instead of squeezing or ellipsizing the text.

In both tiers the buttons appear in a different **order**, and the bottom-most cell carries the panel's bottom inset.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=dialogButtonBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogButtonBarDefaults
```

## Basic Usage

Pass the actions by role, not by position — the bar decides where each one goes:

```kotlin
OverlayDialog(
    show = showDialog,
    title = "Dialog Title",
    summary = "A dialog with a COUI button bar.",
    onDismissRequest = { showDialog = false },
) {
    DialogButtonBar(
        negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
        positive = DialogButtonBarAction(text = "Confirm", onClick = { showDialog = false }),
    )
}
```

Two short labels stay side by side. Long labels flip the bar to a stack automatically, with no extra configuration:

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Not Now", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(
        text = "Delete Backup And Local Copies",
        onClick = { showDialog = false },
    ),
)
```

A third, neutral action is also supported and is drawn in the error color (`COUIAlertDialogBottomButtonSecondary.Last`). COUI **always** stacks a three-button bar, regardless of how short the labels are:

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "Save", onClick = { showDialog = false }),
    neutral = DialogButtonBarAction(text = "Discard", onClick = { showDialog = false }),
)
```

### Recommended Button

Mark one action `recommended = true` to get the COUI recommend tier (`COUIAlertDialogBuilder.setPositiveButton(text, listener, isRecommend = true)`). `COUIButtonBarLayout.applyRecommendStyle` turns that button into a primary capsule — the theme-coloured opaque fill and a white label — while the other buttons stay secondary. Layout and order are unchanged.

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Not Now", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(
        text = "Turn On",
        recommended = true,
        onClick = { showDialog = false },
    ),
)
```

## The Stacking Rule

The bar reproduces `COUIButtonBarLayout.onMeasure`. It stays **horizontal only when** there are exactly two buttons and every label fits in its cell. A three-button bar and a single-button bar both take the vertical path.

Cell width comes from `getHorizontalButtonContentWidth`: the bar width — clamped to `DialogDefaults.MaxWidth` (`coui_dialog_max_width`, 392dp) — is split evenly between the buttons, and each cell then loses the button's two horizontal paddings and the cell's edge and central paddings:

```
available = min(392.dp, barWidth) / buttonCount
            - ButtonHorizontalPadding * 2 - CellPaddingEdge - CellPaddingCentral
```

A label wider than `available` forces the flip. Labels are measured single-line with a `TextMeasurer` against the resolved `textStyle`, matching COUI's raw paint measurement.

Set `dynamicLayout = false` to pin the bar horizontal, like `COUIButtonBarLayout.setDynamicLayout(false)`.

## Order and Spacing

Button order reverses between tiers, matching `resortButton`:

| Tier | Order |
| :--- | :--- |
| Horizontal | negative → neutral → positive (left to right) |
| Vertical | neutral → positive → negative (top to bottom) |

Each button sits in a padded cell (`setHorButtonsParentPadding` / `setVerButtonsParentPadding`):

| Tier | Cell paddings |
| :--- | :--- |
| Horizontal | first cell 16 / 4 / 4 / 16dp, second cell 4 / 4 / 16 / 16dp (start / top / end / bottom): 8dp between the buttons |
| Vertical | 16dp on both sides, 4dp on top, 4dp below (16dp below the bottom-most cell): 8dp between the buttons |

Buttons are 44dp tall (56dp when a stacked label wraps), with 12dp horizontal and 4dp vertical paddings and a capsule fill.

## Properties

### DialogButtonBar Properties

| Property      | Type                   | Description                                                             | Default                                         | Required |
| :------------ | :--------------------- | :---------------------------------------------------------------------- | :---------------------------------------------- | :------- |
| negative      | DialogButtonBarAction? | The negative (cancel) action; hidden when null                          | -                                               | Yes      |
| positive      | DialogButtonBarAction? | The positive (confirm) action; hidden when null                         | -                                               | Yes      |
| modifier      | Modifier               | Modifier applied to the bar                                             | Modifier                                        | No       |
| neutral       | DialogButtonBarAction? | The neutral (third) action, drawn in the error color; hidden when null  | null                                            | No       |
| dynamicLayout | Boolean                | Whether the bar may stack itself; false pins it horizontal              | true                                            | No       |
| material      | Boolean                | Whether the buttons draw the COUI 17 material; defaults to whether the hosting dialog draws its material panel (`materialStroke`) | LocalDialogMaterial.current | No       |
| colors        | TextButtonColors       | Colors of the negative and positive buttons                             | materialButtonColors() with `material`, else buttonColors() | No       |
| neutralColors | TextButtonColors       | Colors of the neutral button                                            | materialNeutralButtonColors() with `material`, else neutralButtonColors() | No       |
| recommendedColors | TextButtonColors   | Colors of the recommended button, whatever its role                     | recommendedButtonColors()                       | No       |
| textStyle     | TextStyle              | Label text style, also used to measure labels for the stacking decision | COUITheme.textStyles.buttonL                     | No       |

### DialogButtonBarAction Properties

| Property | Type       | Description                        | Default | Required |
| :------- | :--------- | :--------------------------------- | :------ | :------- |
| text     | String     | The button label                   | -       | Yes      |
| enabled  | Boolean    | Whether the button is enabled      | true    | No       |
| recommended | Boolean | Whether this is the recommended action, drawn as a primary capsule; mark at most one per bar | false | No |
| onClick  | () -> Unit | Invoked when the button is clicked | -       | Yes      |

### DialogButtonBarDefaults Object

The row tier's button height and paddings live in `DialogDefaults.ButtonBarMinHeight` (44.dp) and `DialogDefaults.ButtonBarInsideMargin` (12dp horizontal, 4dp vertical).

| Property                        | Type | Description                                                                 | Value |
| :------------------------------ | :--- | :-------------------------------------------------------------------------- | :---- |
| ButtonHorizontalPadding         | Dp   | Horizontal padding of a bar button, in both tiers                           | 12.dp |
| CellPaddingEdge                 | Dp   | Padding between a button cell and the panel's side edge                     | 16.dp |
| CellPaddingCentral              | Dp   | Padding on the inner side of a row cell (two make the 8dp gap)              | 4.dp  |
| CellPaddingTop                  | Dp   | Top padding of every cell; bottom padding of non-bottom stacked cells       | 4.dp  |
| CellPaddingBottom               | Dp   | Bottom padding of the bottom-most cell (the panel bottom inset)             | 16.dp |
| StackedButtonMinHeight          | Dp   | Min height of a stacked button                                              | 44.dp |
| StackedButtonMinHeightMultiline | Dp   | Min height of a stacked button whose label wraps                            | 56.dp |
| LabelMaxLines | Int | Max lines of a button label before it ellipsizes (`android:maxLines`) | 2 |

| Function              | Return Type      | Description                                                                                   |
| :-------------------- | :--------------- | :-------------------------------------------------------------------------------------------- |
| buttonColors()        | TextButtonColors | `colorScheme.dialogButton` fill (`?couiColorContainer8`), `labelPrimary` label          |
| neutralButtonColors() | TextButtonColors | `colorScheme.dialogButton` fill, `error` label (error at 30% alpha when disabled)             |
| materialButtonColors() | TextButtonColors | Material dialog fill: white at alpha 166 (`#A6FFFFFF`, light) / 51 (`#33FFFFFF`, dark); disabled fill stays `dialogButton` |
| materialNeutralButtonColors() | TextButtonColors | The material fill with the `error` labels of `neutralButtonColors()` |
| recommendedButtonColors() | TextButtonColors | Recommended button: `primary` fill (`?couiColorContainerTheme`, opaque even in a material dialog), `onPrimary` label; `disable` / `disabledOnPrimaryButton` when disabled |

## Behavior Notes

- **The bottom-most cell carries the panel inset.** Its 16dp bottom padding is the dialog's bottom inset, so the panel needs no bottom padding of its own.
- **Buttons press-scale.** `COUIAlertDialogBottomButtonSecondary` keeps `scaleEnable=true`, so the buttons shrink while pressed like other COUI buttons, and they share the `Button` drag motion (rubber band + deformation).
- **The touched button draws on top.** Like `COUIButtonBarLayout.getChildDrawingOrder`, the cell touched last is drawn above its neighbours until another one is touched, so a dragged button's rubber band and deformation (and its spring back) are never covered by the next button.
- **Material follows the dialog blur.** `COUIButtonBarLayout.setBlurBackgroundWindow(blur)` switches the buttons to `FOLLOW_SYSTEM` when the window is blurred and `FORCE_DISABLE` otherwise. Inside an `OverlayDialog` / `WindowDialog` with `materialStroke = true` the bar therefore turns material: the `drawableMaterialEffect` translucent white fill, the `CONTENT_CAPSULE_5` edge lighting + inner shadow (stroke weight 2), the `TYPE_OPAQUE_MEDIUM_2` touch spotlight, and no press tint. Pass `material = false` to opt out.
- **The recommended button stays opaque.** COUI keeps `drawableMaterialEffect = true` on the bar buttons, so in a material dialog the recommended button keeps its opaque theme fill (it never samples a backdrop) and takes the `CONTENT_CAPSULE_4` edge lighting with the `TYPE_OPAQUE_MEDIUM_1` touch spotlight. Without `material` it is the plain primary capsule with the press tint.

