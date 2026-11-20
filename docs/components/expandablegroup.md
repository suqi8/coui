# ExpandableGroup

The expandable group reproduces ColorOS's expandable list (`COUIExpandableRecyclerView` / `COUIExpandableRecyclerAdapter` with a `COUIRotateView` arrow): a group header that expands or collapses its children, with the children growing in and out while they fade, and the header arrow turning over.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=expandableGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ExpandableGroupArrow
import io.github.suqi8.coui.kmp.basic.ExpandableGroupContent
import io.github.suqi8.coui.kmp.basic.ExpandableGroupDefaults
import io.github.suqi8.coui.kmp.basic.ExpandableGroupHeader
import io.github.suqi8.coui.kmp.basic.expandableGroup
```

## Basic Usage

In a `LazyColumn`, `expandableGroup` adds the header and the children as two items:

```kotlin
var expanded by remember { mutableStateOf(false) }

LazyColumn {
    expandableGroup(
        key = "more",
        title = "More settings",
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        BasicComponent(title = "Accessibility", onClick = {})
        BasicComponent(title = "Backup and reset", onClick = {})
    }
}
```

## Outside a Lazy List

The two building blocks can be used directly, for example inside a `Card`:

```kotlin
ExpandableGroupHeader(
    title = "Network",
    expanded = expanded,
    onExpandedChange = { expanded = it },
    summary = "Wi-Fi, mobile network, hotspot",
)
ExpandableGroupContent(expanded = expanded) {
    BasicComponent(title = "Wi-Fi", onClick = {})
    BasicComponent(title = "Mobile network", onClick = {})
}
```

`ExpandableGroupArrow` is the turning arrow on its own, for custom headers.

## Properties

### expandableGroup (LazyListScope)

| Property         | Type                             | Description                                         | Default Value | Required |
| ---------------- | -------------------------------- | --------------------------------------------------- | ------------- | -------- |
| key              | Any                              | Stable group key (item keys derive from `toString`) | -             | Yes      |
| title            | String                           | Header title                                        | -             | Yes      |
| expanded         | Boolean                          | Whether the group is expanded                       | -             | Yes      |
| onExpandedChange | (Boolean) -> Unit                | Called with the requested state on header click     | -             | Yes      |
| summary          | String?                          | Header summary                                      | null          | No       |
| enabled          | Boolean                          | Whether the header can be clicked                   | true          | No       |
| content          | @Composable ColumnScope.() -> Unit | Children of the group                             | -             | Yes      |

### ExpandableGroupHeader

| Property         | Type              | Description                                 | Default Value                        | Required |
| ---------------- | ----------------- | ------------------------------------------- | ------------------------------------ | -------- |
| title            | String            | Header title                                | -                                    | Yes      |
| expanded         | Boolean           | Whether the group is expanded               | -                                    | Yes      |
| onExpandedChange | (Boolean) -> Unit | Called with the requested state on click    | -                                    | Yes      |
| modifier         | Modifier          | Modifier applied to the header              | Modifier                             | No       |
| summary          | String?           | Header summary                              | null                                 | No       |
| enabled          | Boolean           | Whether the header can be clicked           | true                                 | No       |
| arrowColor       | Color             | Arrow color                                 | ExpandableGroupDefaults.arrowColor() | No       |

### ExpandableGroupContent

| Property | Type                               | Description                     | Default Value | Required |
| -------- | ---------------------------------- | ------------------------------- | ------------- | -------- |
| expanded | Boolean                            | Whether the group is expanded   | -             | Yes      |
| modifier | Modifier                           | Modifier applied to the content | Modifier      | No       |
| content  | @Composable ColumnScope.() -> Unit | Children of the group           | -             | Yes      |

### ExpandableGroupArrow

| Property | Type     | Description                       | Default Value                        | Required |
| -------- | -------- | --------------------------------- | ------------------------------------ | -------- |
| expanded | Boolean  | Whether the arrow shows expanded  | -                                    | Yes      |
| modifier | Modifier | Modifier applied to the arrow     | Modifier                             | No       |
| color    | Color    | Arrow color                       | ExpandableGroupDefaults.arrowColor() | No       |
| size     | Dp       | Size of the box the arrow turns in | ExpandableGroupDefaults.ArrowSize   | No       |

### ExpandableGroupDefaults

| Member       | Type  | Default Value                        | COUI Source                                                  |
| ------------ | ----- | ------------------------------------ | ------------------------------------------------------------ |
| ArrowSize    | Dp    | 24.dp                                | expandable_list_item_size                                    |
| arrowColor() | Color | COUITheme.colorScheme.labelSecondary | coui_line_arrow: couiColorLabelSecondary (#8A000000 / #8AFFFFFF) |

## Behavior

- The children grow from zero to their full height (or shrink back) in 400ms on COUIMoveEaseInterpolator (0.3, 0, 0.1, 1), with their alpha equal to the visible height fraction, like `ExpandableRecyclerConnector`'s placeholder view. An interrupting toggle starts from the current height: collapsing then takes `height / fullHeight × 400ms`, expanding always the full 400ms (`ExpandableRecyclerConnector.collapseAnimationStart` / `expandAnimationStart`).
- The arrow is the ColorOS 17 Settings expandable-row arrow (`settings_preference_widget_rotateview.xml`): the `coui_line_arrow` chevron (16 × 24dp) centered in a 24dp box, turned from 0 to 180 degrees over 400ms on (0.133, 0, 0.3, 1) (`COUIRotateView`, rotate type `couiRotateAloneZ`).
- The children are composed only while the group is expanded or animating. The header exposes the expand / collapse accessibility actions.
