# SlideSelectPreference

`SlideSelectPreference` is a preference row that shows the current selection as trailing status text followed by an up/down select indicator, mirroring COUI's `COUISlideSelectPreference` (style `Preference.COUI.COUISelectPreference`, widget `coui_preference_widget_select`). Like the official class it has no built-in popup: the host decides what a tap does — typically opening a list popup or a picker — and then updates `selectionText`.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=slideSelectPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.SlideSelectPreference
import io.github.suqi8.coui.kmp.preference.SlideSelectPreferenceDefaults
```

## Basic Usage

```kotlin
val options = listOf("Standard", "Large", "Extra large")
var selected by remember { mutableIntStateOf(0) }

SlideSelectPreference(
    title = "Font size",
    selectionText = options[selected],
    onClick = { /* show a popup or picker, then update `selected` */ }
)
```

## Layout

- The status text (14sp body, `?couiColorSecondNeutral`) is right-aligned, at most 162dp wide (`coui_preference_status_text_max_width`), and is hidden when `selectionText` is null or empty.
- The indicator is the 24dp `coui_btn_select` glyph (two filled triangles), 16dp after the status text (`support_preference_image_padding_start`). It is decorative and hidden from accessibility services.
- The status text keeps 24dp to the title (`support_preference_widget_focus_jump_margin_start`).

## Component States

### Disabled State

```kotlin
SlideSelectPreference(
    title = "Disabled",
    selectionText = "Standard",
    enabled = false
)
```

## Properties

### SlideSelectPreference Properties

| Property Name    | Type                               | Description                                                 | Default Value                                             | Required |
| ---------------- | ---------------------------------- | ----------------------------------------------------------- | --------------------------------------------------------- | -------- |
| title            | String                             | Preference title                                            | -                                                         | Yes      |
| modifier         | Modifier                           | Component modifier                                          | Modifier                                                  | No       |
| selectionText    | String?                            | Current selection shown before the indicator; null / empty hides it | null                                              | No       |
| onClick          | (() -> Unit)?                      | Row click callback                                          | null                                                      | No       |
| titleColor       | BasicComponentColors               | Title text color configuration                              | BasicComponentDefaults.titleColor()                       | No       |
| summary          | String?                            | Preference summary                                          | null                                                      | No       |
| summaryColor     | BasicComponentColors               | Summary text color configuration                            | BasicComponentDefaults.summaryColor()                     | No       |
| colors           | SlideSelectPreferenceColors        | Status text and indicator colors                            | SlideSelectPreferenceDefaults.slideSelectPreferenceColors() | No     |
| startAction      | @Composable (() -> Unit)?          | Custom start side content                                   | null                                                      | No       |
| endActions       | @Composable (RowScope.() -> Unit)? | Custom end side content, before the status text             | null                                                      | No       |
| bottomAction     | @Composable (() -> Unit)?          | Custom bottom content                                       | null                                                      | No       |
| insideMargin     | PaddingValues                      | Internal content padding                                    | BasicComponentDefaults.InsideMargin                       | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| holdDownState    | Boolean                            | Whether the row is held down, e.g. while its popup is shown | false                                                     | No       |
| enabled          | Boolean                            | Whether the row is clickable                                | true                                                      | No       |

### SlideSelectPreferenceDefaults Object

| Constant                  | Type | Default Value | COUI source                                                                 |
| ------------------------- | ---- | ------------- | --------------------------------------------------------------------------- |
| SelectionTextPaddingStart | Dp   | 8.dp          | support_preference_widget_focus_jump_margin_start (24dp) less the 16dp title gap of BasicComponent |
| SelectionTextMaxWidth     | Dp   | 162.dp        | coui_preference_status_text_max_width                                       |
| IndicatorMarginStart      | Dp   | 16.dp         | support_preference_image_padding_start                                      |
| IndicatorSize             | Dp   | 24.dp         | coui_btn_select_*.png (72px at xxhdpi)                                      |

### `slideSelectPreferenceColors()` factory

| Parameter                  | Type  | Default                                                | COUI role                                   |
| -------------------------- | ----- | ------------------------------------------------------ | ------------------------------------------- |
| selectionTextColor         | Color | COUITheme.colorScheme.labelSecondary               | couiColorSecondNeutral                      |
| disabledSelectionTextColor | Color | COUITheme.colorScheme.labelTertiary          | couiColorLabelTertiary                      |
| indicatorColor             | Color | COUITheme.colorScheme.labelTertiary          | coui_btn_select_normal                      |
| disabledIndicatorColor     | Color | indicatorColor at half opacity                         | coui_btn_select_disable                     |
