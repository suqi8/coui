# TabWithSearchLayout

`TabWithSearchLayout` is the responsive tab-and-search container of ColorOS (`COUIPercentTabWithSearchView`). It lays a tab bar and a search bar out on the COUI percent grid of its own width:

- **Compact** (narrower than 600dp): the search bar spans the width on top, inset 16dp at the start and 7dp at the end; the tab bar spans the width below it.
- **Medium**: the tab bar takes 4 grid columns plus the grid margin at the start, vertically centred; the search bar takes the same width after a gutter, inset 15dp at the end.
- **Expanded**: the tab bar takes 8 grid columns plus the margin, minus a 16dp inset at the start; the search bar 4 columns plus the margin after a gutter, inset 31dp at the end.

Right to left, the arrangement is mirrored. Both slots are measured at the width they are given, so you pass the existing `TabRow` and `SearchBar`.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tabWithSearchLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayout
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayoutDefaults
```

## Basic Usage

```kotlin
val tabs = listOf("All", "Photos", "Videos", "Albums")
var selected by remember { mutableIntStateOf(0) }
var query by remember { mutableStateOf("") }

TabWithSearchLayout(
    tabs = {
        TabRow(tabs = tabs, selectedTabIndex = selected, onTabSelected = { selected = it })
    },
) {
    SearchBar(
        inputField = {
            InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                label = "Search",
            )
        },
        onExpandedChange = {},
        // The layout applies COUI's insets; the search bar adds no horizontal padding of its own.
        insideMargin = TabWithSearchLayoutDefaults.SearchBarInsideMargin,
    ) {}
}
```

## Properties

### TabWithSearchLayout Properties

| Property Name | Type                   | Description                   | Default Value | Required |
| ------------- | ---------------------- | ----------------------------- | ------------- | -------- |
| tabs          | @Composable () -> Unit | The tab bar                   | -             | Yes      |
| modifier      | Modifier               | Modifier applied to the layout | Modifier     | No       |
| searchBar     | @Composable () -> Unit | The search bar                | -             | Yes      |

### TabWithSearchLayoutDefaults Object

| Property Name                 | Type   | Description                                                         | Value                                   |
| ----------------------------- | ------ | ------------------------------------------------------------------- | --------------------------------------- |
| SearchBarInsideMargin         | DpSize | Inside margin to give the `SearchBar`: no horizontal padding        | DpSize(0.dp, 6.dp)                      |
| ExpandedHorizontalPadding     | Dp     | Tab bar start inset on expanded windows                             | 16.dp                                   |
| ListSpecialPadding            | Dp     | Extra width of the compact tab bar on both sides                    | 24.dp                                   |
| SearchBarPaddingStartCompact  | Dp     | Search bar start inset, compact                                     | 16.dp                                   |
| SearchBarPaddingEndCompact    | Dp     | Search bar end inset, compact                                       | 7.dp                                    |
| SearchBarPaddingStartMedium   | Dp     | Search bar start inset, medium                                      | 0.dp                                    |
| SearchBarPaddingEndMedium     | Dp     | Search bar end inset, medium                                        | 15.dp                                   |
| SearchBarPaddingStartExpanded | Dp     | Search bar start inset, expanded                                    | 0.dp                                    |
| SearchBarPaddingEndExpanded   | Dp     | Search bar end inset, expanded                                      | 31.dp                                   |

## Advanced Usage

### Grid Details

Column widths come from the grid with the large margin type (`COUIResponsiveUtils.calculateWidth(width, n, 0, 0)`), the added margin and the gutter from the small margin type. The medium / expanded split also follows the window height, as `COUIResponsiveUtils.isMediumScreen(context, width, height)` does: a window at least 840dp wide but under 480dp tall, or a portrait one narrower than 960dp and at least 900dp tall, keeps the medium arrangement.
