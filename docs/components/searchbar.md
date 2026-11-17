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
import io.github.suqi8.coui.kmp.basic.SearchBarType
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
| insideMargin     | DpSize                             | Internal padding; an unspecified width takes the responsive 16 / 24 / 40dp padding | SearchBarDefaults.InsideMargin | No       |
| expanded         | Boolean                            | Whether to show search results                  | false               | No       |
| type             | SearchBarType                      | `Instant` (animated, functional button beside the capsule) or `NonInstant` (no animation, functional button inside the capsule) | SearchBarType.Instant | No       |
| navigationButton | @Composable (() -> Unit)?          | 40dp button 12dp before the capsule; `NonInstant` only | null                | No       |
| outerPrimaryButton | @Composable (() -> Unit)?        | 40dp button 12dp after the capsule              | null                | No       |
| outerSecondaryButton | @Composable (() -> Unit)?      | Second outer button; an `Instant` bar shows an outer button only while it is the only one | null                | No       |
| outsideEndAction | @Composable (() -> Unit)?          | Functional button shown when expanded, typically a `CancelButton`; `null` hides it | null                | No       |
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
| label             | String                    | Hint shown while the query is empty, expanded or not; single line, ellipsized | ""                   | No       |
| enabled           | Boolean                   | Whether search field is enabled; disabled, the hint and the default icon use the disabled colours | true                 | No       |
| material          | Boolean                   | Whether the capsule uses the COUI 17 glass material; `false` draws a flat `backgroundColor` capsule | `LocalCOUIMaterialEffectEnabled.current` | No       |
| textStyle         | TextStyle?                | Style of text in search box          | null                 | No       |
| colors            | SearchBarColors           | Colors of the input field            | SearchBarDefaults.searchBarColors() | No       |
| leadingIcon       | @Composable (() -> Unit)? | Leading icon                         | default search icon  | No       |
| innerPrimaryButton | @Composable (() -> Unit)? | Button in a 36dp slot inside the capsule, shown while the query is empty (the clear button takes its place once text is typed) | null | No       |
| innerSecondaryButton | @Composable (() -> Unit)? | Second inner button after `innerPrimaryButton` | null              | No       |
| interactionSource | MutableInteractionSource? | Interaction source                   | null                 | No       |
| hints             | List<String>              | Rotating hints shown instead of `label` while the query is empty; empty keeps `label` | emptyList() | No       |
| hintRepeatCount   | Int                       | Hint changes played before the rotation stops | Int.MAX_VALUE | No       |
| onHintChange      | ((Int) -> Unit)?          | Called with the index of each newly shown hint | null          | No       |

### SearchBarDefaults Object

The SearchBarDefaults object provides default values for SearchBar and InputField components.

#### Constants

| Constant Name              | Type     | Description                                        | Default Value      |
| -------------------------- | -------- | -------------------------------------------------- | ------------------ |
| InsideMargin               | DpSize   | Internal padding of the SearchBar (unspecified width: responsive) | DpSize(Dp.Unspecified, 6.dp)|
| HorizontalPaddingCompact   | Dp       | Responsive horizontal padding, compact windows     | 16.dp              |
| HorizontalPaddingMedium    | Dp       | Responsive horizontal padding, medium windows      | 24.dp              |
| HorizontalPaddingExpanded  | Dp       | Responsive horizontal padding, expanded windows    | 40.dp              |
| OuterButtonSize            | Dp       | Size of the navigation and outer buttons           | 40.dp              |
| OuterButtonGap             | Dp       | Gap between the capsule and each outer button      | 12.dp              |
| NavigationButtonEndGap     | Dp       | Gap after the navigation button                    | 12.dp              |
| InputFieldMinHeight        | Dp       | Minimum height of the InputField background        | 40.dp              |
| InputFieldFontSize         | TextUnit | Font size for the InputField text and label        | 15.sp              |
| LeadingIconStartPadding    | Dp       | Start padding for default leading icon             | 12.dp              |
| IconExpandLeadingIconStartPadding | Dp | Start padding for default leading icon while expanding out of a toolbar icon | 10.dp |
| LeadingIconEndPadding      | Dp       | End padding for default leading icon               | 8.dp               |
| EditTextEndGap             | Dp       | Gap after the edit text                            | 4.dp               |
| InnerButtonGap             | Dp       | Gap closing the inner buttons and around a non-instant functional button | 4.dp |
| InnerButtonSize            | Dp       | Slot size of the clear button and the inner buttons | 36.dp             |
| ClearButtonVisualSize      | Dp       | Visual circle size of default clear button         | 18.dp              |
| FunctionalDividerWidth     | Dp       | Width of the divider before a non-instant functional button | 1.dp      |
| FunctionalDividerHeight    | Dp       | Height of that divider                             | 16.dp              |
| NonInstantCancelPadding    | PaddingValues | Padding of a CancelButton inside a non-instant capsule | PaddingValues(12.dp, 4.dp) |
| CancelHeight               | Dp       | Height of the CancelButton capsule                 | 40.dp              |
| CancelPaddingHorizontal    | Dp       | Horizontal label padding inside the CancelButton   | 16.dp              |
| LiftAnimationSpec          | AnimationSpec<Float> | Animation of `Modifier.searchBarLift` (450ms, COUI move-ease) | tween(450) |
| CancelMaxWidth             | Dp       | Maximum width of the CancelButton                  | 152.dp             |
| CancelFontSize             | TextUnit | Font size of the CancelButton label                | 14.sp              |

#### Methods

| Method Name       | Type            | Description                             |
| ----------------- | --------------- | --------------------------------------- |
| searchBarColors() | SearchBarColors | Creates the default input field and cancel button colors |
| responsiveHorizontalPadding(width, windowHeight) | Dp | The responsive horizontal padding for a bar of that width |

### SearchBarColors Class

| Property Name   | Type  | Description                                                                       |
| --------------- | ----- | --------------------------------------------------------------------------------- |
| backgroundColor | Color | Capsule fill without the material, and the colour a collapsing capsule starts from (bar colour, `#FAFAFA` / `#1E1E1E`) |
| labelColor      | Color | Label (hint) text color (labelSecondary, 54% black / 54% white)               |
| disabledLabelColor | Color | Label (hint) text color while disabled (disabledNeutral, couiColorLabelTertiary) |
| iconColor       | Color | Tint of the default leading search icon (labelSecondary)                      |
| disabledIconColor | Color | Tint of the default leading search icon while disabled (disabledNeutral)      |
| clearIconColor  | Color | Fill of the default clear button circle (labelTertiary)                  |
| cancelTextColor | Color | CancelButton label colour (labelPrimary, 90% black / 90% white)                      |
| disabledCancelTextColor | Color | CancelButton label colour while disabled (disabledNeutral)             |

### CancelButton Properties

`CancelButton` is the COUI 17 functional button: a 40dp glass capsule with a 14sp medium label.
Inside `outsideEndAction` it follows the edit-state spring both ways: it scales between half and full
size, its glass capsule comes out of (or dissolves into) a blur of up to 30px and is opaque for all
but the first fifth of the progress, and its label fades with the progress. In a `NonInstant` bar it
sits inside the capsule as a plain text button (12dp x 4dp padding, no capsule or material).

| Property Name | Type            | Description                                                   | Default Value                       | Required |
| ------------- | --------------- | ------------------------------------------------------------- | ----------------------------------- | -------- |
| onClick       | () -> Unit      | Callback when the button is clicked                           | -                                   | Yes      |
| modifier      | Modifier        | Modifier applied to the button                                | Modifier                            | No       |
| text          | String          | Label of the button                                           | "Cancel"                            | No       |
| enabled       | Boolean         | Whether the button responds to clicks                         | true                                | No       |
| material      | Boolean         | Whether the capsule uses the COUI 17 glass material           | `LocalCOUIMaterialEffectEnabled.current` | No       |
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

### Inner Buttons

`innerPrimaryButton` and `innerSecondaryButton` sit inside the capsule after the text, each in a
36dp slot (COUI `setInnerPrimaryButton` / `setInnerSecondaryButton`), typically voice input or a
scanner. They show while the query is empty; typing swaps them for the clear button at once, and
clearing the text brings them back. A gesture that starts on one of them leaves the capsule's touch
motion and spotlight alone.

```kotlin
InputField(
    query = query,
    onQueryChange = { query = it },
    onSearch = {},
    expanded = expanded,
    onExpandedChange = { expanded = it },
    innerPrimaryButton = {
        IconButton(
            onClick = { /* voice input */ },
            material = false,
            minWidth = 36.dp,
            minHeight = 36.dp,
            cornerRadius = 18.dp,
        ) {
            Icon(imageVector = myMicIcon, contentDescription = "Voice input")
        }
    },
)
```

### Outer and Navigation Buttons

`outerPrimaryButton` and `outerSecondaryButton` are 40dp buttons 12dp after the capsule, typically
an `IconButton` (COUI `setOuterPrimaryButton` / `setOuterSecondaryButton`). On the default
`SearchBarType.Instant` bar an outer button is shown only while it is the only one; it fades out
and shrinks to 0.8 with a blur as the bar expands and the capsule takes its place, and grows back
from 0.5 on a response 0.4 / bounce 0.3 spring.

```kotlin
SearchBar(
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outerPrimaryButton = {
        IconButton(onClick = { /* ... */ }) {
            Icon(imageVector = myScanIcon, contentDescription = "Scan")
        }
    },
    outsideEndAction = { CancelButton(onClick = { expanded = false }) },
) {
    // Search results
}
```

### Non-instant Search

`type = SearchBarType.NonInstant` is COUI's `TYPE_NON_INSTANT_SEARCH`: the bar switches state
without animation, the functional button (`outsideEndAction`) moves inside the capsule while it is
expanded, behind a 1dp x 16dp divider, and `navigationButton` (12dp before the capsule) and up to two
outer buttons stay beside it.

```kotlin
SearchBar(
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    type = SearchBarType.NonInstant,
    navigationButton = {
        IconButton(onClick = { /* back */ }) { /* back icon */ }
    },
    outsideEndAction = { CancelButton(text = "Search", onClick = { /* submit */ }) },
) {
    // Search results
}
```

### Responsive Padding

With the default `insideMargin` the bar's horizontal padding follows its own width as COUI does
(`setUseResponsivePadding(true)`): 16dp on compact, 24dp on medium and 40dp on expanded windows.
Give `insideMargin` a specified width (for example `TabWithSearchLayoutDefaults.SearchBarInsideMargin`)
to fix it.

### Expanding from the Toolbar

A search bar can also live in the toolbar and expand out of its search icon
(`COUISearchBar.showInToolBar`); see [ToolbarSearch](./toolbarsearch).

### Rotating Hints

`hints` turns the placeholder into the COUI hint carousel (`COUIHintAnimationLayout`): the first
hint shows at once, and every 3 seconds the current hint slides up and fades out over 250ms while
the next one rises from below 150ms later. The travel is half the free height of the capsule plus
5dp; the slide runs on `PathInterpolator(0.3, 0, 0.2, 1)` and the fade on the COUI ease curve.
Typing hides the hints and stops the rotation; clearing the query shows the current hint again,
still (the rotation resumes only when the field enters the composition again).
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
