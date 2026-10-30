# SearchBar

`SearchBar` is a component in COUI used for user search input. It provides an intuitive and
easy-to-use search interface with support for expanded/collapsed state switching and search
suggestions display.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=searchBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.CancelButton
```

On ColorOS 17 the search capsule and the cancel button are both glass capsules with the top-bar
material (filtered backdrop, shadow, edge lighting, touch spotlight and press/drag motion), falling
back to the opaque bar colour (`#FAFAFA` light, `#1E1E1E` dark) where the blend modes are missing.
Entering and leaving the edit state runs on a spring (response 0.5, bounce 0.25): the capsule
narrows while the cancel button grows in from half size, the two merging like metaballs while
they move. Pressing or dragging the capsule scales and stretches it and lights a touch spotlight,
the clear button included; a gesture that starts on another clickable element inside it (your own
icon buttons) leaves the capsule still and dark.

## Basic Usage

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // Search results content
    Column {
        // Add search suggestions or results here
    }
}
```

## Properties

### SearchBar Properties

| Property Name    | Type                               | Description                                     | Default Value       | Required |
|------------------|------------------------------------|-------------------------------------------------|---------------------|----------|
| inputField       | @Composable () -> Unit             | Search input field component                    | -                   | Yes      |
| onExpandedChange | (Boolean) -> Unit                  | Callback when expanded state changes            | -                   | Yes      |
| modifier         | Modifier                           | Modifier applied to the search bar              | Modifier            | No       |
| insideMargin     | DpSize                             | Internal padding                                | SearchBarDefaults.InsideMargin | No       |
| expanded         | Boolean                            | Whether to show search results                  | false               | No       |
| outsideEndAction | @Composable (() -> Unit)?          | Action shown on the end when expanded, typically a `CancelButton` | null                | No       |
| content          | @Composable ColumnScope.() -> Unit | Content shown when expanded                     | -                   | Yes      |

### InputField Properties

| Property Name     | Type                      | Description                          | Default Value        | Required |
|-------------------|---------------------------|--------------------------------------|----------------------|----------|
| query             | String                    | Text content in search field         | -                    | Yes      |
| onQueryChange     | (String) -> Unit          | Callback when text content changes   | -                    | Yes      |
| onSearch          | (String) -> Unit          | Callback when search is executed     | -                    | Yes      |
| expanded          | Boolean                   | Whether in expanded state            | -                    | Yes      |
| onExpandedChange  | (Boolean) -> Unit         | Callback when expanded state changes | -                    | Yes      |
| modifier          | Modifier                  | Modifier applied to the input field  | Modifier             | No       |
| label             | String                    | Placeholder text when empty          | ""                   | No       |
| enabled           | Boolean                   | Whether search field is enabled      | true                 | No       |
| material          | Boolean                   | Whether the capsule uses the COUI 17 glass material; `false` draws a flat `backgroundColor` capsule | true | No       |
| textStyle         | TextStyle?                | Style of text in search box          | null                 | No       |
| colors            | SearchBarColors           | Colors of the input field            | SearchBarDefaults.searchBarColors() | No       |
| leadingIcon       | @Composable (() -> Unit)? | Leading icon                         | default search icon  | No       |
| trailingIcon      | @Composable (() -> Unit)? | Trailing icon                        | default clear button | No       |
| interactionSource | MutableInteractionSource? | Interaction source                   | null                 | No       |
| hints             | List<String>              | Rotating hints shown instead of `label` while the query is empty; empty keeps `label` | emptyList() | No       |
| hintRepeatCount   | Int                       | Hint changes played before the rotation stops | Int.MAX_VALUE | No       |
| onHintChange      | ((Int) -> Unit)?          | Called with the index of each newly shown hint | null          | No       |

### SearchBarDefaults Object

The SearchBarDefaults object provides default values for SearchBar and InputField components.

#### Constants

| Constant Name              | Type     | Description                                        | Default Value      |
| -------------------------- | -------- | -------------------------------------------------- | ------------------ |
| InsideMargin               | DpSize   | Internal padding of the SearchBar                  | DpSize(16.dp, 6.dp)|
| InputFieldMinHeight        | Dp       | Minimum height of the InputField background        | 40.dp              |
| InputFieldFontSize         | TextUnit | Font size for the InputField text and label        | 15.sp              |
| LeadingIconStartPadding    | Dp       | Start padding for default leading icon             | 12.dp              |
| LeadingIconEndPadding      | Dp       | End padding for default leading icon               | 8.dp               |
| TrailingIconStartPadding   | Dp       | Start padding for default clear button             | 4.dp               |
| TrailingIconEndPadding     | Dp       | End padding for default clear button               | 4.dp               |
| TrailingIconTouchTargetSize | Dp      | Circular touch target size of default clear button | 36.dp              |
| TrailingIconVisualSize     | Dp       | Visual circle size of default clear button         | 18.dp              |
| CancelHeight               | Dp       | Height of the CancelButton capsule                 | 40.dp              |
| CancelPaddingHorizontal    | Dp       | Horizontal label padding inside the CancelButton   | 16.dp              |
| LiftAnimationSpec          | AnimationSpec<Float> | Animation of `Modifier.searchBarLift` (450ms, COUI move-ease) | tween(450) |
| CancelMaxWidth             | Dp       | Maximum width of the CancelButton                  | 152.dp             |
| CancelFontSize             | TextUnit | Font size of the CancelButton label                | 14.sp              |

#### Methods

| Method Name       | Type            | Description                             |
| ----------------- | --------------- | --------------------------------------- |
| searchBarColors() | SearchBarColors | Creates the default input field and cancel button colors |

### SearchBarColors Class

| Property Name   | Type  | Description                                                                       |
| --------------- | ----- | --------------------------------------------------------------------------------- |
| backgroundColor | Color | Capsule fill without the material, and the colour a collapsing capsule starts from (bar colour, `#FAFAFA` / `#1E1E1E`) |
| labelColor      | Color | Label (hint) text color (onSurfaceSecondary, 54% black / 54% white)               |
| iconColor       | Color | Tint of the default leading search icon (onSurfaceSecondary)                      |
| clearIconColor  | Color | Fill of the default clear button circle (onSurfaceContainerHigh)                  |
| cancelTextColor | Color | CancelButton label colour (onSurface, 90% black / 90% white)                      |

### CancelButton Properties

`CancelButton` is the COUI 17 functional button: a 40dp glass capsule with a 14sp medium label.
Inside `outsideEndAction` it follows the edit-state spring both ways: it scales between half and full
size, its glass capsule comes out of (or dissolves into) a blur of up to 30px and is opaque for all
but the first fifth of the progress, and its label fades with the progress.

| Property Name | Type            | Description                                                   | Default Value                       | Required |
| ------------- | --------------- | ------------------------------------------------------------- | ----------------------------------- | -------- |
| onClick       | () -> Unit      | Callback when the button is clicked                           | -                                   | Yes      |
| modifier      | Modifier        | Modifier applied to the button                                | Modifier                            | No       |
| text          | String          | Label of the button                                           | "Cancel"                            | No       |
| enabled       | Boolean         | Whether the button responds to clicks                         | true                                | No       |
| material      | Boolean         | Whether the capsule uses the COUI 17 glass material           | true                                | No       |
| colors        | SearchBarColors | Label and fallback fill colours                               | SearchBarDefaults.searchBarColors() | No       |

## Advanced Usage

### SearchBar with Icons

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            leadingIcon = {
                Icon(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    imageVector = COUIIcons.Basic.Search,
                    contentDescription = "Search"
                )
            }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // Search results content
}
```

### SearchBar with Suggestions

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }
val suggestions = listOf("Suggestion 1", "Suggestion 2", "Suggestion 3")

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    Column {
        suggestions.forEach { suggestion ->
            BasicComponent(
                title = suggestion,
                onClick = {
                    searchText = suggestion
                    expanded = false
                }
            )
        }
    }
}
```

### SearchBar with Cancel Button

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { expanded = false },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            label = "Search"
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outsideEndAction = {
        // The SearchBar places this slot 12dp after the capsule and animates it in.
        CancelButton(
            onClick = {
                expanded = false
                searchText = ""
            }
        )
    }
) {
    // Search results content
}
```

### Expanding from the Toolbar

On ColorOS 17 a search bar can live in the toolbar and expand out of its search icon
(`COUISearchBar.showInToolBar`). The capsule starts as a circle on the icon, slides home while it
widens, its text fades in and it blurs most half way; the cancel button flies out of the icon and
merges with the capsule like a metaball, while the toolbar's own icons fade and shrink to 0.8
and its title fades (the toolbar background stays). While the bar folds back it ignores touches;
tapping the search icon then expands it again, and a touch in the last 5% of the collapse first
completes the collapse at once.
The expansion runs on a response 0.55 / bounce 0.2 spring (0.5 / 0.2 back), the toolbar icons on
0.4 / 0.3 (0.5 / 0.2 back, growing from 0.5).

```kotlin
val searchState = rememberToolbarSearchState()
var query by remember { mutableStateOf("") }

Box {
    ToolbarSearchAnchor(state = searchState) {
        val titleColor = COUITheme.colorScheme.onSurface
        TopAppBar(
            title = "Title",
            titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
            navigationIcon = {
                IconButton(modifier = Modifier.toolbarSearchItem(searchState), onClick = { /* back */ }) { /* ... */ }
            },
            actions = {
                IconButton(
                    modifier = Modifier.toolbarSearchEntry(searchState),
                    onClick = { searchState.expand() },
                ) {
                    Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "Search")
                }
            },
        )
    }
    ToolbarFloatingSearchBar(
        state = searchState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) {
        SearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = searchState.expanded,
                    onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
                )
            },
            outsideEndAction = {
                CancelButton(onClick = { query = ""; searchState.collapse() })
            },
            expanded = searchState.expanded,
            onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
        ) {}
    }
}
```

| API | Description |
| --- | --- |
| `rememberToolbarSearchState()` / `ToolbarSearchState` | Expanded state with `expand()` / `collapse()` |
| `ToolbarSearchAnchor` | Hosts the toolbar content (the toolbar itself does not fade) |
| `ToolbarSearchState.toolbarTitleAlpha` | Alpha to apply to the toolbar title colour while the bar is out |
| `Modifier.toolbarSearchEntry(state)` | Marks the toolbar search icon the bar grows out of; hides it while the bar is out |
| `Modifier.toolbarSearchItem(state)` | Fades and shrinks another toolbar icon along with the expansion |
| `ToolbarFloatingSearchBar` | Hosts the `SearchBar` over the toolbar row; empty while the bar is folded away |

### Rotating Hints

`hints` turns the placeholder into the COUI hint carousel (`COUIHintAnimationLayout`): the first
hint shows at once, and every 3 seconds the current hint slides up and fades out over 250ms while
the next one rises from below 150ms later. The travel is half the free height of the capsule plus
5dp; the slide runs on `PathInterpolator(0.3, 0, 0.2, 1)` and the fade on the COUI ease curve.
Typing hides the hints; clearing the query shows the current hint again and restarts the rotation.
Use `onHintChange` to remember the shown hint, for example to search for it on an empty submit.

```kotlin
val hints = remember { listOf("Wi-Fi", "Bluetooth", "Battery saver") }
var shownHint by remember { mutableIntStateOf(0) }

InputField(
    query = query,
    onQueryChange = { query = it },
    onSearch = { onSearch(it.ifEmpty { hints[shownHint] }) },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    hints = hints,
    onHintChange = { shownHint = it },
)
```

### Lifting into the Title Bar

`Modifier.searchBarLift(expanded, targetTop)` reproduces the standard `COUISearchViewAnimate`
enter / exit: while `expanded` the bar moves from where it is laid out up to `targetTop` (window
coordinates, for example the status bar height) over 450ms on the COUI move-ease curve
(`SearchBarDefaults.LiftAnimationSpec`), and back when it collapses. Only the bar is translated;
hide or fade the title it replaces yourself. The keyboard opens through the `InputField` focus
request as before.

```kotlin
val statusBarTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }

SearchBar(
    modifier = Modifier.searchBarLift(expanded = expanded, targetTop = statusBarTop),
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
) {
    // Search results
}
```
