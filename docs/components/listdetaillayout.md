# ListDetailLayout

`ListDetailLayout` is a two-pane list-detail container, ported from COUI `COUIListDetailView`. On compact layouts it shows one pane at a time; from 600dp wide the list and the detail sit side by side with a hairline divider between them.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=listDetailLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
```

## Basic Usage

```kotlin
var selected by remember { mutableStateOf<Item?>(null) }

ListDetailLayout(
    list = { ItemList(onSelect = { selected = it }) },
    detail = selected?.let { item -> { ItemDetail(item, onBack = { selected = null }) } },
    emptyDetail = { Text("Select an item") },
)
```

## Behavior

The size class comes from the layout's own size (`COUIListDetailView.onMeasure`):

- **Compact** (narrower than 600dp, `WindowTotalSizeClass.Compact`): a single pane, the `detail` when there is one, otherwise the `list`. Set `detail` back to null to return to the list; the list stays composed meanwhile, so it keeps its scroll position.
- **Split**: the list is `clamp(width × 0.4, 280dp, 412dp)` wide (`DEFAULT_MAIN_WIDTH_PERCENT`, `coui_main_fragment_min_width` / `coui_main_fragment_max_width`), a 0.33dp `couiColorDivider` line follows (`coui_fragment_gap_width`), and the remaining width shows the `detail`, or `emptyDetail` while nothing is selected (COUI's empty-page container).

Both panes keep their state when the layout switches between the single-pane and the split arrangement.

## Properties

### ListDetailLayout Properties

| Property Name     | Type                      | Description                                        | Default Value                              | Required |
| ----------------- | ------------------------- | -------------------------------------------------- | ------------------------------------------ | -------- |
| list              | @Composable () -> Unit    | The list (main) pane                               | -                                          | Yes      |
| detail            | (@Composable () -> Unit)? | The detail (sub) pane, null when nothing is selected | -                                        | Yes      |
| modifier          | Modifier                  | Modifier applied to the layout                     | Modifier                                   | No       |
| listWidthFraction | Float                     | The list's share of the width (`setMainFragmentPercent`) | ListDetailLayoutDefaults.ListWidthFraction | No |
| minListWidth      | Dp                        | Minimum list width                                 | ListDetailLayoutDefaults.MinListWidth      | No       |
| maxListWidth      | Dp                        | Maximum list width                                 | ListDetailLayoutDefaults.MaxListWidth      | No       |
| dividerColor      | Color                     | Divider color                                      | DividerDefaults.DividerColor               | No       |
| dividerThickness  | Dp                        | Divider thickness                                  | ListDetailLayoutDefaults.DividerThickness  | No       |
| emptyDetail       | @Composable () -> Unit    | Detail placeholder in split mode                   | {}                                         | No       |

### ListDetailLayoutDefaults Object

| Name                       | Type    | Description                                  | Value   |
| -------------------------- | ------- | -------------------------------------------- | ------- |
| ListWidthFraction          | Float   | `DEFAULT_MAIN_WIDTH_PERCENT`                 | 0.4     |
| MinListWidth               | Dp      | `coui_main_fragment_min_width`               | 280.dp  |
| MaxListWidth               | Dp      | `coui_main_fragment_max_width`               | 412.dp  |
| DividerThickness           | Dp      | `coui_fragment_gap_width`                    | 0.33.dp |
| isSplitMode(width, height) | Boolean | Whether a layout size shows both panes       | width ≥ 600dp |

## Differences from COUI

COUI hosts the panes in `FragmentContainerView`s and leaves page transitions to the fragments; `ListDetailLayout` takes composable slots and switches panes without a transition.
