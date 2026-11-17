# ActivityDialogPreference

`ActivityDialogPreference` is a list preference whose choices open on a full page, mirroring ColorOS's `COUIActivityDialogPreference`. In COUI it is a `COUIListPreference` shown through `COUIActivityDialogFragment`: a full-screen window that slides in from the end edge like an activity (350ms), with a toolbar (back arrow + title) above a card list of single-choice rows. Tapping a row commits the value and closes the page; the back arrow or the system back gesture closes it without changes.

This library renders the page as a full-size, undimmed layer of the `Scaffold` popup host (the same layer the overlay popups use), so it **must be used inside a `Scaffold`**. The page contains a `SmallTopAppBar` and a `Card` of `RadioButtonPreference` rows with the radio button at the end. The preference row itself is an `ArrowPreference` showing the selected entry.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=activityDialogPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.ActivityDialogPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
```

## Basic Usage

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("Follow system"),
        ListPreferenceEntry("Light"),
        ListPreferenceEntry("Dark", summary = "Easier on the eyes at night")
    )
}
var selected by remember { mutableIntStateOf(0) }

Scaffold {
    ActivityDialogPreference(
        entries = entries,
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
        title = "Appearance"
    )
}
```

## Properties

### ActivityDialogPreference Properties

| Property Name         | Type                      | Description                                        | Default Value                         | Required |
| --------------------- | ------------------------- | -------------------------------------------------- | ------------------------------------- | -------- |
| entries               | List\<ListPreferenceEntry> | Entries to choose from                            | -                                     | Yes      |
| selectedIndex         | Int                       | Index of the selected entry, -1 for none           | -                                     | Yes      |
| onSelectedIndexChange | (Int) -> Unit             | Callback when an entry is selected on the page     | -                                     | Yes      |
| title                 | String                    | Preference title                                   | -                                     | Yes      |
| modifier              | Modifier                  | Component modifier                                 | Modifier                              | No       |
| titleColor            | BasicComponentColors      | Title text color configuration                     | BasicComponentDefaults.titleColor()   | No       |
| summary               | String?                   | Preference summary                                 | null                                  | No       |
| summaryColor          | BasicComponentColors      | Summary text color configuration                   | BasicComponentDefaults.summaryColor() | No       |
| dialogTitle           | String                    | Toolbar title of the page                          | title                                 | No       |
| startAction           | @Composable (() -> Unit)? | Custom start side content                          | null                                  | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom side content                         | null                                  | No       |
| insideMargin          | PaddingValues             | Component internal content padding                 | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition      | CardListPosition          | Row position inside its card group                 | LocalCardListPosition.current         | No       |
| enabled               | Boolean                   | Whether the preference is clickable                | true                                  | No       |
| showValue             | Boolean                   | Whether to show the selected entry on the row      | true                                  | No       |
| renderInRootScaffold  | Boolean                   | Whether to render the page in the root Scaffold    | true                                  | No       |

### ActivityDialogPreferenceDefaults Object

| Name                     | Type          | Default Value                                              | Description                                                       |
| ------------------------ | ------------- | ---------------------------------------------------------- | ----------------------------------------------------------------- |
| ListContentPadding       | PaddingValues | PaddingValues(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 32.dp) | Card list margins, support_preference_listview_margin_top, coui_list_to_ex_bottom_padding |
| DividerHorizontalPadding | Dp            | 16.dp                                                      | coui_preference_divider_default_horizontal_padding minus the card margin |
| TransitionDurationMillis | Int           | 350                                                        | coui_open_slide_enter / coui_close_slide_exit duration            |
