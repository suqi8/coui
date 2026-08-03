# DialogButtonBar

`DialogButtonBar` is the button bar of a COUI alert dialog, mirroring ColorOS's `COUIButtonBarLayout`. Its defining behaviour is **automatic vertical stacking**: the bar measures its own labels, and as soon as one of them is too wide to fit in its cell, the whole bar flips from a horizontal row to a vertical stack instead of squeezing or ellipsizing the text.

The two tiers are not just different directions — they use different min heights, paddings, divider thickness and divider insets, and the buttons appear in the **opposite order**.

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

A third, neutral action is also supported. COUI **always** stacks a three-button bar, regardless of how short the labels are:

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "Save", onClick = { showDialog = false }),
    neutral = DialogButtonBarAction(text = "Discard", onClick = { showDialog = false }),
)
```

## The Stacking Rule

The bar reproduces `COUIButtonBarLayout.onMeasure` exactly. It stays **horizontal only when all three of these hold**:

1. Every label fits in its cell, and
2. there are exactly two buttons, and
3. there is no recommend button (never the case here — see [Limitations](#limitations)).

In every other case the bar stacks. In particular, a three-button bar and a single-button bar both take the vertical path.

Cell width comes from `needSetButVertical`: the bar width — clamped to `DialogDefaults.MaxWidth` (`coui_dialog_max_width`, 392dp) — is split evenly between the buttons, minus the dividers, and each cell then loses its two horizontal paddings:

```
available = (min(392.dp, barWidth) - (buttonCount - 1) * StackedDividerThickness) / buttonCount
            - ButtonHorizontalPadding * 2
```

A label wider than `available` forces the flip. Labels are measured single-line with a `TextMeasurer` against the resolved `textStyle`, matching COUI's raw paint measurement.

::: tip
The divider subtracted in this formula is the **vertical** tier's 0.33dp thickness, even when the bar ends up drawing the thicker 1dp horizontal divider. This is what `COUIButtonBarLayout` does, and it is reproduced faithfully.
:::

Set `dynamicLayout = false` to pin the bar horizontal, like `COUIButtonBarLayout.setDynamicLayout(false)`.

## Order and Dividers

Button order reverses between tiers, matching `resortButton`:

| Tier | Order |
| :--- | :--- |
| Horizontal | negative → neutral → positive (left to right) |
| Vertical | neutral → positive → negative (top to bottom) |

So the negative (cancel) action sits on the **left** when horizontal but at the **bottom** when stacked.

Dividers differ as well:

| Tier | Thickness | Inset |
| :--- | :--- | :--- |
| Horizontal | 1dp (`DialogDefaults.ButtonBarDividerThickness`) | 17dp top / 21dp bottom |
| Vertical | 0.33dp (`DialogButtonBarDefaults.StackedDividerThickness`) | 24dp on each side |

A divider is drawn between each pair of adjacent present buttons. Set `showDivider = false` to suppress them (the `buttonBarShowDivider` attribute).

## Properties

### DialogButtonBar Properties

| Property        | Type                   | Description                                                                 | Default                                     | Required |
| :-------------- | :--------------------- | :-------------------------------------------------------------------------- | :------------------------------------------ | :------- |
| negative        | DialogButtonBarAction? | The negative (cancel) action; hidden when null                              | -                                           | Yes      |
| positive        | DialogButtonBarAction? | The positive (confirm) action; hidden when null                             | -                                           | Yes      |
| modifier        | Modifier               | Modifier applied to the bar                                                 | Modifier                                    | No       |
| neutral         | DialogButtonBarAction? | The neutral (third) action; hidden when null                                | null                                        | No       |
| dynamicLayout   | Boolean                | Whether the bar may stack itself; false pins it horizontal                  | true                                        | No       |
| showDivider     | Boolean                | Whether dividers between buttons are shown                                  | true                                        | No       |
| hasContentAbove | Boolean                | Whether a title, message or custom panel sits above the bar                 | true                                        | No       |
| colors          | TextButtonColors       | Colors of the buttons                                                       | ButtonDefaults.textButtonColorsBorderless() | No       |
| dividerColor    | Color                  | Color of the dividers                                                       | COUITheme.colorScheme.dividerLine           | No       |
| textStyle       | TextStyle              | Label text style, also used to measure labels for the stacking decision     | COUITheme.textStyles.button                 | No       |

::: warning
`hasContentAbove` affects spacing: COUI adds 6dp of extra top padding to the top-most stacked button only when nothing sits above it. Leave it `true` for a normal dialog with a title or message.
:::

### DialogButtonBarAction Properties

| Property | Type       | Description                        | Default | Required |
| :------- | :--------- | :--------------------------------- | :------ | :------- |
| text     | String     | The button label                   | -       | Yes      |
| enabled  | Boolean    | Whether the button is enabled      | true    | No       |
| onClick  | () -> Unit | Invoked when the button is clicked | -       | Yes      |

### DialogButtonBarDefaults Object

Metrics of the vertical tier and the values shared by both tiers. The horizontal tier's own metrics live in `DialogDefaults.ButtonBar*`.

| Property                      | Type | Description                                                                | Value   |
| :---------------------------- | :--- | :------------------------------------------------------------------------- | :------ |
| ButtonHorizontalPadding       | Dp   | Horizontal padding of a bar button, in both tiers                          | 24.dp   |
| StackedButtonMinHeight        | Dp   | Min height of a stacked button                                             | 52.dp   |
| StackedButtonMinHeightBottom  | Dp   | Min height of the bottom-most stacked button (52dp + 12dp extra)           | 64.dp   |
| StackedButtonPaddingVertical  | Dp   | Vertical padding of a stacked button                                       | 14.dp   |
| StackedButtonPaddingTopExtra  | Dp   | Extra top padding of the top-most stacked button when nothing sits above   | 6.dp    |
| StackedButtonPaddingBottomExtra | Dp | Extra bottom padding of the bottom-most stacked button (the panel inset)   | 12.dp   |
| StackedBarMarginTop           | Dp   | Top margin of a stacked bar holding more than one button                   | 16.dp   |
| StackedDividerThickness       | Dp   | Thickness of the divider between stacked buttons                           | 0.33.dp |
| StackedDividerInsetHorizontal | Dp   | Horizontal inset of the divider between stacked buttons                    | 24.dp   |
| ButtonCornerRadius            | Dp   | Corner radius of a bar button (a full-cell square-cornered rect)           | 0.dp    |

## Behavior Notes

- **Bottom button carries the panel inset.** The bottom-most stacked button gets 12dp of extra bottom padding and a 64dp min height, so the dialog panel needs no bottom padding of its own.
- **A stacked bar of more than one button gets a 16dp top margin.** A single stacked button gets none.
- **A lone negative button falls back to the horizontal metrics.** This is the one stacked case where `resetVerButsPadding` applies the 58dp panel min height and the horizontal paddings instead of the vertical ones.
- **Buttons are square-cornered with no press scale.** `COUIAlertDialogBottomButtonNewNormal` sets `drawableRadius=0dp`, `scaleEnable=false` and `stateListAnimator=@null`: the only press feedback is a tint over the whole cell.

## Limitations

The **recommend-button** tier is not implemented. In ColorOS, `COUIButtonBarLayout.setRecommendButtonId` promotes one button to a highlighted filled primary button (44dp tall, its own paddings and margins, all dividers hidden). This bar always behaves as `mRecommendButtonId == NO_RECOMMEND_ID`, which is the stock alert-dialog path.
