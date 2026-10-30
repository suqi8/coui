# ResponsiveGrid

`ResponsiveGrid` 和 `Modifier.percentWidth` 按 COUI 响应式栅格确定内容尺寸，分别移植自 COUI `COUIGridLayout` 以及由 `COUIResponsiveUtils` 驱动的百分比宽度容器（`COUIPercentWidthLinearLayout`、`COUIPercentWidthFrameLayout` 等）。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=responsiveGrid" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ResponsiveGrid
import io.github.suqi8.coui.kmp.basic.ResponsiveGridSpec
import io.github.suqi8.coui.kmp.utils.percentWidth
```

## 栅格

栅格基于组件自身的测量宽度和窗口高度构建（`LayoutGridSystem`、`WindowTotalSizeClass`）：

| 宽度                | 列数 | 边距（small / large） | 间隔 |
| ------------------- | ---- | --------------------- | ---- |
| < 600dp             | 4    | 16dp / 24dp           | 8dp  |
| 600dp – 840dp       | 8    | 24dp / 24dp           | 8dp  |
| ≥ 840dp             | 12   | 40dp / 40dp           | 12dp |

窗口高度不足 480dp，或竖屏窗口宽度小于 960dp 且高度不小于 900dp 时，840dp 以上仍保持 8 列栅格。

## ResponsiveGrid

```kotlin
// 每个块跨 2 个栅格列：每行 2 / 4 / 6 个。
ResponsiveGrid(
    spec = ResponsiveGridSpec.Grid(childGridNumber = 2),
    verticalGap = 12.dp,
    childHeight = 72.dp,
) {
    tiles.forEach { Tile(it) }
}
```

| Spec                                                 | COUI 模式            | 列数与宽度 |
| ---------------------------------------------------- | -------------------- | ---------- |
| `Grid(childGridNumber, marginType)`                  | `GRID_MODE`          | `columnCount / childGridNumber` 列，每个子项跨 `childGridNumber` 个栅格列；行从栅格边距之后开始，子项间隔为栅格间隔 |
| `SpecificGap(childMinWidth, horizontalGap, childMinHeight)` | `SPECIFIC_GAP_MODE` | 按 `childMinWidth` 与固定间隔能放下的最多列数；子项拉伸填满整行。给出 `childMinHeight` 时高度保持最小尺寸的宽高比 |
| `SpecificSize(childWidth, minHorizontalGap)`         | `SPECIFIC_SIZE_MODE` | 固定宽度子项，按最小间隔能放下的最多列数；剩余宽度平均分到列间 |

### ResponsiveGrid 属性

| 属性名      | 类型                   | 说明                                         | 默认值                              | 必须 |
| ----------- | ---------------------- | -------------------------------------------- | ----------------------------------- | ---- |
| spec        | ResponsiveGridSpec     | 列数与子项宽度的推导方式                     | -                                   | 是   |
| modifier    | Modifier               | 应用于网格的修饰符                           | Modifier                            | 否   |
| verticalGap | Dp                     | 行间距（`couiVerticalGap`）                  | ResponsiveGridDefaults.VerticalGap  | 否   |
| childHeight | Dp                     | 固定子项高度，`Dp.Unspecified` 时自适应      | Dp.Unspecified                      | 否   |
| content     | @Composable () -> Unit | 网格子项                                     | -                                   | 是   |

## percentWidth

`Modifier.percentWidth` 按 `COUIResponsiveUtils.measureLayout` 计算内容区：内容跨 `gridNumber` 个居中的栅格列（自动时按栅格大小取 4 / 6 / 8），两侧各加宽 24dp（列表）或 16dp（卡片列表），使行自身的内边距与栅格列对齐，且不超过宽度减去两侧边距。

```kotlin
Box(Modifier.fillMaxWidth().percentWidth(type = PercentWidthType.CardList)) {
    Card(Modifier.padding(horizontal = 16.dp)) { /* rows */ }
}
```

| 参数                 | 类型             | 说明                                                       | 默认值             |
| -------------------- | ---------------- | ---------------------------------------------------------- | ------------------ |
| gridNumber           | Int              | 跨越的列数，或 `AUTO_GRID_NUMBER`（-1）                     | AUTO_GRID_NUMBER   |
| type                 | PercentWidthType | `Default` / `List` / `CardList`（特殊内边距）              | Default            |
| marginType           | GridMarginType   | `Small` / `Large` 栅格边距                                 | Large              |
| mode                 | PercentWidthMode | `Padding`：保持传入宽度并内缩内容；`Remeasure`：布局自身就是栅格宽度 | Padding    |
| parentChildHierarchy | Boolean          | 列表-详情分栏中的子栏：大窗口下跨全部列，普通内容内缩 24dp（大栅格 40dp） | false |
| enabled              | Boolean          | `percentIndentEnabled`                                     | true               |

`PercentWidthDefaults.resolve(parentWidth, windowWidth, windowHeight, …)` 以 `PercentWidthResult(contentWidth, startPadding, endPadding)` 返回相同的几何结果，供自定义布局使用。

## 与 COUI 的差异

- `COUIGridLayout` 为所有子项使用同一个固定高度（未设置时为 0）；这里未设置 `childHeight` 时每行高度取最高子项。不支持逐个子项的外边距（`isIgnoreChildMargin = false`）。
- 栅格按 dp 计算，相当于 `ResponsiveUIModel` 开启 `layoutGridCalculateWithDp`，没有 View 实现中的像素截断。
