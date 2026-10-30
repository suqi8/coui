# ResponsiveGrid

`ResponsiveGrid` and `Modifier.percentWidth` size content on the COUI responsive layout grid. They are ported from COUI `COUIGridLayout` and the percent-width containers (`COUIPercentWidthLinearLayout`, `COUIPercentWidthFrameLayout`, …) driven by `COUIResponsiveUtils`.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=responsiveGrid" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ResponsiveGrid
import io.github.suqi8.coui.kmp.basic.ResponsiveGridSpec
import io.github.suqi8.coui.kmp.utils.percentWidth
```

## The Layout Grid

The grid is built over the width the component is measured at and the window height (`LayoutGridSystem`, `WindowTotalSizeClass`):

| Width               | Columns | Margin (small / large) | Gutter |
| ------------------- | ------- | ---------------------- | ------ |
| < 600dp             | 4       | 16dp / 24dp            | 8dp    |
| 600dp – 840dp       | 8       | 24dp / 24dp            | 8dp    |
| ≥ 840dp             | 12      | 40dp / 40dp            | 12dp   |

A window under 480dp tall, or a portrait window narrower than 960dp and at least 900dp tall, keeps the 8-column grid at 840dp and above.

## ResponsiveGrid

```kotlin
// Every tile spans 2 grid columns: 2 / 4 / 6 tiles per row.
ResponsiveGrid(
    spec = ResponsiveGridSpec.Grid(childGridNumber = 2),
    verticalGap = 12.dp,
    childHeight = 72.dp,
) {
    tiles.forEach { Tile(it) }
}
```

| Spec                                                 | COUI mode            | Columns and widths |
| ---------------------------------------------------- | -------------------- | ------------------ |
| `Grid(childGridNumber, marginType)`                  | `GRID_MODE`          | `columnCount / childGridNumber` columns, each child spans `childGridNumber` grid columns; the row starts after the grid margin and children are a gutter apart |
| `SpecificGap(childMinWidth, horizontalGap, childMinHeight)` | `SPECIFIC_GAP_MODE` | As many columns as fit `childMinWidth` with a fixed gap; children stretch to fill the row. With `childMinHeight` the height keeps the minimum size's aspect ratio |
| `SpecificSize(childWidth, minHorizontalGap)`         | `SPECIFIC_SIZE_MODE` | Fixed-width children, as many as fit with the minimum gap; the leftover width is spread between columns |

### ResponsiveGrid Properties

| Property Name | Type                   | Description                                         | Default Value                       | Required |
| ------------- | ---------------------- | --------------------------------------------------- | ----------------------------------- | -------- |
| spec          | ResponsiveGridSpec     | How columns and child widths are derived            | -                                   | Yes      |
| modifier      | Modifier               | Modifier applied to the grid                        | Modifier                            | No       |
| verticalGap   | Dp                     | Space between rows (`couiVerticalGap`)              | ResponsiveGridDefaults.VerticalGap  | No       |
| childHeight   | Dp                     | Fixed child height, or `Dp.Unspecified` to wrap     | Dp.Unspecified                      | No       |
| content       | @Composable () -> Unit | The grid children                                   | -                                   | Yes      |

## percentWidth

`Modifier.percentWidth` resolves the content area like `COUIResponsiveUtils.measureLayout`: the content spans `gridNumber` centred grid columns (automatically 4 / 6 / 8 by the grid size), widened by 24dp (list) or 16dp (card list) on each side so the rows' own insets line up with the columns, and never wider than the width minus both margins.

```kotlin
Box(Modifier.fillMaxWidth().percentWidth(type = PercentWidthType.CardList)) {
    Card(Modifier.padding(horizontal = 16.dp)) { /* rows */ }
}
```

| Parameter            | Type             | Description                                                       | Default            |
| -------------------- | ---------------- | ----------------------------------------------------------------- | ------------------ |
| gridNumber           | Int              | Columns to span, or `AUTO_GRID_NUMBER` (-1)                        | AUTO_GRID_NUMBER   |
| type                 | PercentWidthType | `Default` / `List` / `CardList` (special padding)                 | Default            |
| marginType           | GridMarginType   | `Small` / `Large` grid margin                                     | Large              |
| mode                 | PercentWidthMode | `Padding`: keep the incoming width and inset the content; `Remeasure`: the layout itself is the grid width | Padding |
| parentChildHierarchy | Boolean          | Child pane of a list-detail split: spans every column on large windows, and plain content insets by 24dp (40dp on large grids) | false |
| enabled              | Boolean          | `percentIndentEnabled`                                            | true               |

`PercentWidthDefaults.resolve(parentWidth, windowWidth, windowHeight, …)` returns the same geometry as a `PercentWidthResult(contentWidth, startPadding, endPadding)` for custom layouts.

## Differences from COUI

- `COUIGridLayout` gives all children one fixed height (zero when none is set); here a row wraps its tallest child unless `childHeight` is set. Per-child margins (`isIgnoreChildMargin = false`) are not supported.
- The grid is computed in dp, like `ResponsiveUIModel` with `layoutGridCalculateWithDp`, without the pixel truncation of the View implementation.
