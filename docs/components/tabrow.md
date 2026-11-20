# TabRow

`TabRow` is a navigation component in COUI for content categorization and navigation scenarios. It provides two variants: the standard style reproduces the ColorOS sliding tab layout (`COUITabLayout`: text tabs over a thin track with a sliding underline indicator), and the contour style reproduces the ColorOS segment button (`COUISegmentButtonLayout`: a capsule container with a sliding capsule indicator).

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TabRow // Standard style (sliding tab layout)
import io.github.suqi8.coui.kmp.basic.TabRowWithContour // Contour style (segment button)
```

## Basic Usage

### Standard Style

The standard style matches `COUITabLayout`: a 48dp row of 14sp labels (medium weight when selected), a 2dp track inset 24dp from both edges, and a 2dp neutral indicator as wide as the selected label (at least 32dp). Changing the selection slides the indicator over `min(300, 50 × Δtabs + 150)` ms with `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)` while the label colors crossfade. Each tab has an 8dp press mask, and touching an unselected tab plays a haptic tick; like `COUITabView`, both start on touch down, also when the row sits in a scrolling page.

Labels that fit into the 360dp content width are spread with equal gaps, labels that fit into the row are centered with `itemSpacing` between them, and otherwise the row scrolls with 58dp fading edges and keeps the selected tab centered. The scrolling row has the `COUIHorizontalScrollView` motion: a drag past either end follows at 40% and stiffens towards a screen width, and a fling that hits an end overshoots by up to 37% of the screen width and springs back. The row itself never vibrates: `COUITabLayoutBaseStyle` turns the tab layout's own haptics off, so neither the scroll edges nor a pager swipe tick.

```kotlin
val tabs = listOf("Recommended", "Following", "Popular", "Featured")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Icons, Red Dots and Disabled Tabs

Pass `TabRowItem`s to give tabs an icon, a red dot or a disabled state. A row whose tabs have both an
icon and a label is 72dp tall; the icon sits before the label and the red dot after it, aligned to
the top of the row. The indicator stays under the label.

```kotlin
val items = listOf(
    TabRowItem(text = "Photos", icon = COUIIcons.Photos),
    TabRowItem(text = "Music", icon = COUIIcons.Music, badge = 0),
    TabRowItem(text = "Images", icon = COUIIcons.Image, badge = 12),
    TabRowItem(text = "Settings", icon = COUIIcons.Settings, enabled = false),
)
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    items = items,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Contour Style

The contour style matches the ColorOS segment button: a 40dp capsule container with a 4dp inset around the sliding indicator. Segments are measured from their label width (at least 52dp, every label in the medium `couiTextButtonM`) and distributed to fill the row.

The indicator moves on the COUI 17 segment button springs shared with [SegmentButton](./segmentbutton): position (response 0.4, bounce 0.1), width (response 0.6 → 0.4 and bounce 0.5 → 0.1 by how different the two widths are) and a height that dips towards 90% and springs back. A touched segment shrinks with the COUI 17 press scale from the touch down, and pressing the selected segment presses the indicator in. `SegmentButton` adds dragging the indicator between segments and the COUI 17 material.

```kotlin
val tabs = listOf("All", "Photos", "Videos", "Documents")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Preserve Scroll Position

When the tabs cannot fit, both styles become scrollable. Pass a `LazyListState` to keep the scroll position.

```kotlin
val tabs = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5")
var selectedTabIndex by remember { mutableStateOf(3) }
val tabListState = rememberLazyListState()

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    listState = tabListState,
)
```

## Properties

### TabRow Properties

| Property Name     | Type                      | Description                                                                                       | Default Value                    | Required |
| ----------------- | ------------------------- | ------------------------------------------------------------------------------------------------- | -------------------------------- | -------- |
| tabs / items      | List\<String> / List\<TabRowItem> | Tab labels, or tabs with an icon, a red dot or a disabled state                          | -                                | Yes      |
| selectedTabIndex  | Int                       | Current selected tab index                                                                        | -                                | Yes      |
| onTabSelected     | (Int) -> Unit             | Callback when tab is selected                                                                     | -                                | Yes      |
| modifier          | Modifier                  | Modifier for the tab row                                                                          | Modifier                         | No       |
| enabled           | Boolean                   | Whether the tabs can be selected; a disabled row greys out its labels and indicator               | true                             | No       |
| colors            | TabRowColors              | Color configuration                                                                               | TabRowDefaults.underlineColors() | No       |
| minWidth          | Dp                        | Minimum width of the content of each tab                                                          | TabRowDefaults.TabRowMinWidth    | No       |
| maxWidth          | Dp                        | Maximum width of the content of each tab; longer labels are ellipsized                            | TabRowDefaults.TabRowMaxWidth    | No       |
| height            | Dp                        | Height of the tab row; `Dp.Unspecified` = 48dp, or 72dp when a tab has an icon and a label        | Dp.Unspecified                   | No       |
| cornerRadius      | Dp                        | Corner radius of the press mask of a tab                                                          | TabRowDefaults.TabRowCornerRadius | No      |
| itemSpacing       | Dp                        | Minimum spacing between tabs; `Dp.Unspecified` = 20dp on small screens, 24dp otherwise            | Dp.Unspecified                   | No       |
| horizontalMargin  | Dp                        | Minimum margin at both ends; `Dp.Unspecified` = 16dp, 24dp or 40dp by screen size                 | Dp.Unspecified                   | No       |
| contentAlignment  | Alignment                 | Alignment of the tab content in its tab                                                           | Alignment.Center                 | No       |
| listState         | LazyListState?            | External scroll state for tabs                                                                    | null                             | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items                                                                  | null                             | No       |
| indication        | Indication?               | Press mask of tab items, clipped to `cornerRadius`                                                | LocalIndication.current          | No       |
| pagerState        | PagerState?               | A pager the row follows while the user drags it                                                   | null                             | No       |

Screen sizes follow COUI's window classes: small below 600dp; large from 840dp wide with a window
height between 480dp and 900dp (or at least 900dp unless the window is a portrait under 960dp
wide); medium otherwise. The margin is judged from the row width and the window height, the spacing
from the row width and the row height, as `COUISlidingTabStrip` does.

### TabRowItem

| Property Name | Type         | Description                                                            | Default Value |
| ------------- | ------------ | ---------------------------------------------------------------------- | ------------- |
| text          | String       | Label; may be empty for an icon-only tab                               | -             |
| icon          | ImageVector? | 24dp icon before the label, tinted like the label                      | null          |
| badge         | Int?         | Red dot after the label: `null` none, `0` a dot, a positive number shown in a capsule | null |
| enabled       | Boolean      | Whether the tab can be selected                                        | true          |

### TabRowWithContour Properties

| Property Name     | Type                      | Description                              | Default Value                                | Required |
| ----------------- | ------------------------- | ---------------------------------------- | -------------------------------------------- | -------- |
| tabs              | List\<String>             | List of tab texts                        | -                                            | Yes      |
| selectedTabIndex  | Int                       | Current selected tab index               | -                                            | Yes      |
| onTabSelected     | (Int) -> Unit             | Callback when tab is selected            | -                                            | Yes      |
| modifier          | Modifier                  | Modifier for the tab row                 | Modifier                                     | No       |
| colors            | TabRowColors              | Color configuration                      | TabRowDefaults.tabRowColors()                | No       |
| minWidth          | Dp                        | Minimum width of each tab                | TabRowDefaults.TabRowWithContourMinWidth     | No       |
| maxWidth          | Dp                        | Maximum width of each tab                | TabRowDefaults.TabRowWithContourMaxWidth     | No       |
| height            | Dp                        | Height of the tab row                    | TabRowDefaults.TabRowWithContourHeight       | No       |
| cornerRadius      | Dp                        | Corner radius of the indicator           | TabRowDefaults.TabRowWithContourCornerRadius | No       |
| contourPadding    | Dp                        | Inset between container and indicator    | TabRowDefaults.TabRowWithContourPadding      | No       |
| itemSpacing       | Dp                        | Spacing between tabs                     | 0.dp                                         | No       |
| contentAlignment  | Alignment                 | Alignment of tab content                 | Alignment.Center                             | No       |
| listState         | LazyListState?            | External scroll state for tabs           | null                                         | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items         | null                                         | No       |
| indication        | Indication?               | Indication for tab items                 | null                                         | No       |

### TabRowDefaults Object

The TabRowDefaults object provides default configurations for the TabRow component.

#### Constants

| Constant Name                     | Type | Value       | Description                                                       |
| --------------------------------- | ---- | ----------- | ----------------------------------------------------------------- |
| TabRowHeight                      | Dp   | 48.dp       | Height of the standard style with text tabs                       |
| TabRowIconHeight                  | Dp   | 72.dp       | Height of the standard style when a tab has an icon and a label   |
| TabRowWithContourHeight           | Dp   | 40.dp       | Default height of tab row for contour style                       |
| TabRowWithContourTinyHeight       | Dp   | 32.dp       | Height of the COUI `SegmentButton.Tiny` style                     |
| TabRowCornerRadius                | Dp   | 8.dp        | Press mask corner radius of a tab for standard style              |
| TabRowWithContourCornerRadius     | Dp   | 16.dp       | Default indicator corner radius (capsule) for contour style       |
| TabRowWithContourTinyCornerRadius | Dp   | 14.dp       | Indicator corner radius of the `SegmentButton.Tiny` style         |
| TabRowWithContourPadding          | Dp   | 4.dp        | Default inset between container and indicator                     |
| TabRowWithContourTinyPadding      | Dp   | 2.dp        | Contour inset of the `SegmentButton.Tiny` style                   |
| TabRowMinWidth                    | Dp   | 0.dp        | Min width of the label slot for standard style                    |
| TabRowWithContourMinWidth         | Dp   | 52.dp       | Min width of tabs for contour style                               |
| TabRowMaxWidth                    | Dp   | Dp.Unspecified | Max width of the label slot for standard style (70% of the row; `Dp.Infinity` removes the limit) |
| TabRowWithContourMaxWidth         | Dp   | Dp.Infinity | Max width of tabs for contour style (unbounded, tabs fill row)    |

#### Methods

| Method Name       | Type         | Description                                                    |
| ----------------- | ------------ | -------------------------------------------------------------- |
| underlineColors() | TabRowColors | Create the default color configuration of `TabRow`             |
| tabRowColors()    | TabRowColors | Create the default color configuration of `TabRowWithContour`  |

`underlineColors()` parameters:

| Parameter Name       | Type  | Description                              | Default Value                            |
| -------------------- | ----- | ---------------------------------------- | ---------------------------------------- |
| trackColor           | Color | Color of the 2dp track                   | `#0F000000` (light) / `#19FFFFFF` (dark) |
| indicatorColor       | Color | Color of the 2dp indicator               | COUITheme.colorScheme.labelPrimary          |
| contentColor         | Color | Label color of unselected tabs           | COUITheme.colorScheme.labelSecondary |
| selectedContentColor | Color | Label color of selected and pressed tabs | COUITheme.colorScheme.labelPrimary          |
| disabledContentColor | Color | Label color of disabled tabs             | COUITheme.colorScheme.disabledNeutral  |
| disabledIndicatorColor | Color | Indicator color of a disabled row      | `#FFD9D9D9`                              |

### TabRowColors Class

| Property Name           | Type  | Description                        |
| ----------------------- | ----- | ---------------------------------- |
| backgroundColor         | Color | Container color (`TabRowWithContour`) or track color (`TabRow`) |
| contentColor            | Color | Default content color of tabs      |
| selectedBackgroundColor | Color | Color of the sliding indicator     |
| selectedContentColor    | Color | Content color of the selected tab  |
| disabledContentColor    | Color | Label color of disabled tabs (`TabRow`); defaults to `contentColor` |
| disabledSelectedBackgroundColor | Color | Indicator color of a disabled row (`TabRow`); defaults to `selectedBackgroundColor` |

## Advanced Usage

### Custom Colors

```kotlin
val tabs = listOf("Latest", "Popular", "Following")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    colors = TabRowDefaults.underlineColors(
        indicatorColor = COUITheme.colorScheme.primary,
        selectedContentColor = COUITheme.colorScheme.primary
    )
)
```

### Tiny Variant

```kotlin
val tabs = listOf("Photo", "Video", "Portrait")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    height = TabRowDefaults.TabRowWithContourTinyHeight,
    cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
    contourPadding = TabRowDefaults.TabRowWithContourTinyPadding
)
```

### Using with Pager

Pass the `PagerState` as `pagerState` and the row follows the pager while the user drags it, like
`COUITabLayout.setScrollPosition`: the indicator slides between the two labels, the label colors
crossfade over the second half of the way to the next page (page by page when one drag crosses
several) and the row scrolls along. A page change made by swiping does not vibrate; a tab click
keeps its own indicator animation.

```kotlin
val tabs = listOf("Page 1", "Page 2", "Page 3")
val pagerState = rememberPagerState { tabs.size }
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.targetPage,
            onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
            pagerState = pagerState
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Page Content ${page + 1}")
            }
        }
    }
}
```
