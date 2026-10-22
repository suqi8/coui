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
narrows while the cancel button grows in from half size.

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
Inside `outsideEndAction` its label fades in with the edit-state spring.

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