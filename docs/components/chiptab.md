# ChipTab

`ChipTab` reproduces the ColorOS 17 chip tab (`COUIChipTab`): chip-shaped, single-selection tabs for filtering a page. Unlike [Chip](./chip), a chip tab never turns the accent color: both states keep the primary label, and the selected capsule rises into an elevated surface with a soft shadow, the COUI 17 edge light and, over a recorded backdrop, the blurred material. `ChipTabRow` lays the tabs out in the horizontally scrolling, edge-fading row of `COUIChipTabLayout`.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=chipTab" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ChipTab
import io.github.suqi8.coui.kmp.basic.ChipTabRow
import io.github.suqi8.coui.kmp.basic.ChipTabDefaults
import io.github.suqi8.coui.kmp.basic.ChipTabMaterialType
```

## Basic Usage

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

ChipTabRow(
    tabs = listOf("All", "Photos", "Videos", "Documents", "Music"),
    selectedTabIndex = selectedIndex,
    onTabSelected = { selectedIndex = it },
)
```

Clicking the selected chip changes nothing (`COUIChipTabGroup` always keeps one chip selected). When the row can scroll towards an edge, the chips are cut at the 16dp inset there and fade out over 12dp; a chip that the visible edge cuts through is scrolled into view when clicked.

## Leading Icon Chip

`COUIChipTabLayout` can show an icon-only chip before the scrolling chips. It is always selected, reports its own click, and narrows the inset of the scrolling chips to 8dp.

```kotlin
ChipTabRow(
    tabs = tabs,
    selectedTabIndex = selectedIndex,
    onTabSelected = { selectedIndex = it },
    onLeadingIconClick = { /* Open the category panel */ },
    leadingIcon = {
        Icon(
            imageVector = COUIIcons.Basic.Sidebar,
            contentDescription = "Categories",
            modifier = Modifier.size(20.dp),
        )
    },
)
```

## Single Chip Tabs

`ChipTab` can be used on its own, with a label (and an optional 16dp leading icon) or icon only (a 32dp circle, `COUIChipTab.setShowIconOnly`):

```kotlin
ChipTab(
    selected = selected,
    onClick = { selected = true },
    label = "Starred",
)

ChipTab(
    selected = searchSelected,
    onClick = { searchSelected = !searchSelected },
    icon = {
        Icon(
            imageVector = COUIIcons.Basic.Search,
            contentDescription = "Search",
            modifier = Modifier.size(16.dp),
        )
    },
)
```

## Material Backdrop

With `materialEffect` on (the default), ColorOS 17 replaces the chip fill with a backdrop blur of the content behind it, interpolated between the unselected and selected `COUIMaterialBlurEffect` presets. coui-ui has no backdrop capture of its own, so the blur is drawn only under a `LocalMaterialBackdrop` provider (see [Button](./button)); without one the chip keeps the fill COUI draws whenever the blur is off. `ChipTabMaterialType.NoBlur` (`TYPE_DEFAULT_NO_BLUR`) applies the same kind of colour filters to the unblurred backdrop.

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
    ChipTabRow(
        tabs = tabs,
        selectedTabIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
        materialType = ChipTabMaterialType.Default,
    )
}
```

## Component States

### Disabled State

A disabled chip tab keeps its capsule and greys its label and icon (couiColorLabelTertiary):

```kotlin
ChipTab(
    selected = true,
    onClick = {},
    label = "Disabled",
    enabled = false,
)
```

## Properties

### ChipTab

| Property          | Type                      | Description                                        | Default Value                    | Required |
| ----------------- | ------------------------- | -------------------------------------------------- | -------------------------------- | -------- |
| selected          | Boolean                   | Whether the chip tab is selected                   | -                                | Yes      |
| onClick           | () -> Unit                | Callback when the chip tab is clicked              | -                                | Yes      |
| label             | String                    | Text label (single line, ellipsized)               | -                                | Yes      |
| modifier          | Modifier                  | Modifier applied to the chip tab                   | Modifier                         | No       |
| enabled           | Boolean                   | Whether the chip tab is enabled                    | true                             | No       |
| materialEffect    | Boolean                   | Whether the COUI 17 material is drawn              | true                             | No       |
| materialType      | ChipTabMaterialType       | Backdrop preset family                             | ChipTabMaterialType.Default      | No       |
| icon              | (@Composable () -> Unit)? | Optional leading icon                              | null                             | No       |
| cornerRadius      | Dp                        | Corner radius                                      | ChipTabDefaults.CornerRadius     | No       |
| minWidth          | Dp                        | Minimum width                                      | ChipTabDefaults.MinWidth         | No       |
| minHeight         | Dp                        | Minimum height                                     | ChipTabDefaults.MinHeight        | No       |
| maxWidth          | Dp                        | Maximum width                                      | ChipTabDefaults.MaxWidth         | No       |
| colors            | ChipTabColors             | Color configuration                                | ChipTabDefaults.chipTabColors()  | No       |
| insideMargin      | PaddingValues             | Horizontal padding inside the chip tab             | ChipTabDefaults.InsideMargin     | No       |
| interactionSource | MutableInteractionSource? | Interaction source of the chip tab                 | null                             | No       |

The icon-only overload takes `icon: @Composable () -> Unit` instead of `label`/`icon`, plus `size` (default `ChipTabDefaults.IconOnlySize`) and `iconSize` (default `ChipTabDefaults.IconSize`) instead of the size and padding parameters.

### ChipTabRow

| Property           | Type                      | Description                                                      | Default Value                   | Required |
| ------------------ | ------------------------- | ---------------------------------------------------------------- | ------------------------------- | -------- |
| tabs               | List\<String\>            | Labels of the chips                                              | -                               | Yes      |
| selectedTabIndex   | Int                       | Index of the selected chip                                       | -                               | Yes      |
| onTabSelected      | (Int) -> Unit             | Callback when an unselected chip is clicked                      | -                               | Yes      |
| modifier           | Modifier                  | Modifier applied to the row                                      | Modifier                        | No       |
| enabled            | Boolean                   | Whether the chips are enabled                                    | true                            | No       |
| materialEffect     | Boolean                   | Whether the chips draw the COUI 17 material                      | true                            | No       |
| materialType       | ChipTabMaterialType       | Backdrop preset family                                           | ChipTabMaterialType.Default     | No       |
| colors             | ChipTabColors             | Color configuration of the chips                                 | ChipTabDefaults.chipTabColors() | No       |
| horizontalPadding  | Dp                        | Inset of the scrolling chips; Unspecified = 16dp, 8dp with a leading icon | Dp.Unspecified         | No       |
| itemSpacing        | Dp                        | Spacing between two chips                                        | ChipTabDefaults.ItemSpacing     | No       |
| scrollState        | ScrollState?              | Scroll state of the chips                                        | null                            | No       |
| onLeadingIconClick | () -> Unit                | Callback when the leading icon chip is clicked                   | {}                              | No       |
| leadingIcon        | (@Composable () -> Unit)? | Icon of the always-selected leading chip                         | null                            | No       |

### ChipTabDefaults

| Constant                            | Type          | Default Value                     | COUI Source                                           |
| ----------------------------------- | ------------- | --------------------------------- | ----------------------------------------------------- |
| MinWidth                            | Dp            | 52.dp                             | coui_chip_default_min_width                           |
| MinHeight                           | Dp            | 32.dp                             | coui_chip_tab_style_height                            |
| MaxWidth                            | Dp            | 300.dp                            | coui_chip_default_max_width                           |
| TextMaxWidth                        | Dp            | 200.dp                            | coui_chip_default_max_text_width                      |
| CornerRadius                        | Dp            | 16.dp                             | `COUIChipTab.setChipCornerRadius(16dp)`, smooth weight 1.0 |
| IconSize                            | Dp            | 16.dp                             | coui_chip_tab_style_chip_icon_size                    |
| IconSpacing                         | Dp            | 4.dp                              | coui_chip_suggest_style_chip_icon_end_padding         |
| InsideMargin                        | PaddingValues | PaddingValues(horizontal = 12.dp) | coui_chip_suggest_style_chip_horizontal_padding       |
| IconOnlySize                        | Dp            | 32.dp                             | `COUIChipTab.ICON_ONLY_DIAMETER_DP`                   |
| LeadingIconSize                     | Dp            | 20.dp                             | `COUIChipTabLayout.ICON_SIZE_20`                      |
| ItemSpacing                         | Dp            | 8.dp                              | layout_chip_tab `chipSpacingHorizontal`               |
| RowVerticalPadding                  | Dp            | 8.dp                              | `COUIChipTabGroup` paddingVertical                    |
| RowHorizontalPadding                | Dp            | 16.dp                             | `COUIChipTabLayout.PADDING_HORIZONTAL_16`             |
| RowHorizontalPaddingWithLeadingIcon | Dp            | 8.dp                              | `COUIChipTabLayout.PADDING_HORIZONTAL_WITH_ICON_ONLY_CHIP` |
| FadingEdgeLength                    | Dp            | 12.dp                             | `ChipMaskHorizontalScrollView.FADING_EDGE_WIDTH`      |

#### Methods

| Method Name                                         | Type                 | Description                                                              |
| --------------------------------------------------- | -------------------- | ------------------------------------------------------------------------ |
| textStyle()                                         | TextStyle            | Label style (COUITheme.textStyles.subtitle, couiTextButtonM 14sp medium) |
| chipTabColors()                                     | ChipTabColors        | Creates the color configuration                                          |
| backdropSpec(isDark, materialType, selectedFraction) | MaterialBackdropSpec | The interpolated COUI 17 backdrop at a selection fraction                |

### `chipTabColors()` factory

| Parameter              | Type  | Default                                        | COUI Source                                       |
| ---------------------- | ----- | ---------------------------------------------- | ------------------------------------------------- |
| containerColor         | Color | #0A000000 / #1AFFFFFF                          | couiColorContainer4 (unselected tint)             |
| selectedContainerColor | Color | COUITheme.colorScheme.surfaceContainerHighest  | couiColorBackgroundElevated #FFFFFF / #1E1E1E     |
| selectedHighlightColor | Color | #40FFFFFF                                      | coui_color_checked_background_gradient_start      |
| selectedStrokeColor    | Color | #0A000000 / #0AFFFFFF                          | coui_color_checked_background_stroke (0.6dp)      |
| contentColor           | Color | COUITheme.colorScheme.onSurface                | couiColorLabelPrimary                             |
| disabledContentColor   | Color | COUITheme.colorScheme.onSurfaceVariantActions  | couiColorLabelTertiary                            |

### ChipTabMaterialType

| Value   | COUI Source                                                                                  |
| ------- | -------------------------------------------------------------------------------------------- |
| Default | `TYPE_DEFAULT`: `TYPE_CONTENT_CHIP_TAB` → `TYPE_CONTENT_CHIP_TAB_SELECTED`, 40px blur        |
| NoBlur  | `TYPE_DEFAULT_NO_BLUR`: `TYPE_CONTENT_CHIP_TAB_NO_BLUR` → `..._SELECTED_NO_BLUR`, 0px        |

## Behavior

- Selection cross-fades two layers on COUISpringForce(response 0.2, bounce 0) springs (`COUIChipTabDrawable` checked / unchecked layer alpha): the unselected tint fades out while the elevated surface, the #40FFFFFF highlight and the selected effects fade in.
- Material on: the backdrop (under a provider) mixes the unselected and selected presets with an `ArgbEvaluator`; the `TYPE_NO_EFFECT_BASE_1` → `TYPE_FRAMEWORK_CAPSULE_2` edge light and inner shadow grow with the selection; the `TYPE_CHIP_TAB` touch spotlight follows the finger (PLUS 0.2 light, SRC_OVER 0.3 dark) and replaces the press and hover masks.
- Material off: the selected chip draws the 0.6dp checked stroke, and pressing or hovering tints the capsule with `press` / `hover`.
- The selected chip casts the `ShadowUtils` level 5 (`SHADOW_LV3_A`) light-source shadow, its elevation (100dp) scaled by the selection.
- Pressing scales the chip with the COUI 17 size-dependent press scale; keyboard focus draws the 3dp `focusOutline` ring.
- Labels are single line, ellipsized beyond `TextMaxWidth`, and centered in chips widened to `MinWidth`.
