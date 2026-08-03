# MarkPreference

`MarkPreference` is a single-select row with a selection mark, mirroring ColorOS's `COUIMarkPreference`. The mark renders the COUI 16 `coui_btn_check_mark` selector — which resolves to the radio drawables (`coui_btn_radio_on` / `coui_btn_radio_off`, 24dp): a primary disc with a solid center dot when checked, and an outline ring otherwise. The mark sits at the end of the row by default (COUI `TAIL_MARK`) and the whole row is the click target.

Use a group of `MarkPreference` rows as a single-select list: clicking a row invokes `onClick` and the caller moves the checked state to that row.

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.MarkPreference
import io.github.suqi8.coui.kmp.preference.MarkLocation
```

## Basic Usage

```kotlin
var selected by remember { mutableIntStateOf(0) }
val options = listOf("Every day", "Workdays only", "Never")

Column {
    options.forEachIndexed { index, option ->
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index }
        )
    }
}
```

## Mark at the Start (HEAD_MARK)

```kotlin
MarkPreference(
    title = "Head Mark",
    checked = true,
    onClick = {},
    markLocation = MarkLocation.Start
)
```

## Component States

### Disabled State

```kotlin
MarkPreference(
    title = "Disabled Option",
    checked = true,
    onClick = {},
    enabled = false
)
```

## Properties

### MarkPreference Properties

| Property Name | Type                             | Description                                                        | Default Value                          | Required |
| ------------- | -------------------------------- | ------------------------------------------------------------------ | -------------------------------------- | -------- |
| title         | String                           | Preference title                                                   | -                                      | Yes      |
| checked       | Boolean                          | Whether this row currently carries the mark                        | -                                      | Yes      |
| onClick       | (() -> Unit)?                    | Callback when the row is clicked                                   | -                                      | Yes      |
| modifier      | Modifier                         | Component modifier                                                 | Modifier                               | No       |
| titleColor    | BasicComponentColors             | Title text color configuration                                     | BasicComponentDefaults.titleColor()    | No       |
| summary       | String?                          | Preference summary                                                 | null                                   | No       |
| summaryColor  | BasicComponentColors             | Summary text color configuration                                   | BasicComponentDefaults.summaryColor()  | No       |
| markColors    | RadioButtonColors                | Mark color configuration (shares the COUI radio drawables)         | RadioButtonDefaults.radioButtonColors() | No      |
| startAction   | @Composable (() -> Unit)?        | Custom start side content                                          | null                                   | No       |
| endActions    | @Composable (RowScope.() -> Unit)? | Custom end side content (before an end mark)                     | null                                   | No       |
| markLocation  | MarkLocation                     | Mark location: End (COUI TAIL_MARK) or Start (COUI HEAD_MARK)      | MarkLocation.End                       | No       |
| bottomAction  | @Composable (() -> Unit)?        | Custom bottom side content                                         | null                                   | No       |
| insideMargin  | PaddingValues                    | Component internal content padding                                 | BasicComponentDefaults.InsideMargin    | No       |
| cardListPosition | CardListPosition | Row position inside its card group; rounded outer edges gain extra padding | CardListPosition.None | No |
| holdDownState | Boolean                          | Whether the component is held down                                 | false                                  | No       |
| enabled       | Boolean                          | Component interactive state                                        | true                                   | No       |

### MarkLocation Options

| Value              | Description                                | COUI Equivalent |
| ------------------ | ------------------------------------------ | --------------- |
| MarkLocation.End   | The mark is placed at the end of the row   | TAIL_MARK       |
| MarkLocation.Start | The mark is placed at the start of the row | HEAD_MARK       |

## Advanced Usage

### With Summary and Custom Mark Colors

```kotlin
MarkPreference(
    title = "High Quality",
    summary = "Uses more data",
    checked = selected == 0,
    onClick = { selected = 0 },
    markColors = RadioButtonDefaults.radioButtonColors(
        selectedColor = COUITheme.colorScheme.primary
    )
)
```

### Single-Select Group inside a Card

```kotlin
var selected by remember { mutableIntStateOf(0) }
val options = listOf("Option A", "Option B", "Option C")

Card {
    options.forEachIndexed { index, option ->
        if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index }
        )
    }
}
```
