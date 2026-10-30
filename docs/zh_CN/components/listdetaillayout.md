# ListDetailLayout

`ListDetailLayout` 是列表-详情双栏容器，移植自 COUI `COUIListDetailView`。紧凑布局下一次只显示一栏；宽度达到 600dp 后列表与详情并排，中间以细分割线隔开。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=listDetailLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
```

## 基本用法

```kotlin
var selected by remember { mutableStateOf<Item?>(null) }

ListDetailLayout(
    list = { ItemList(onSelect = { selected = it }) },
    detail = selected?.let { item -> { ItemDetail(item, onBack = { selected = null }) } },
    emptyDetail = { Text("Select an item") },
)
```

## 行为

尺寸档位取自布局自身尺寸（`COUIListDetailView.onMeasure`）：

- **紧凑**（宽度不足 600dp，`WindowTotalSizeClass.Compact`）：单栏，有 `detail` 时显示详情，否则显示 `list`。把 `detail` 置回 null 即返回列表；期间列表保持组合，滚动位置不会丢失。
- **分栏**：列表宽 `clamp(width × 0.4, 280dp, 412dp)`（`DEFAULT_MAIN_WIDTH_PERCENT`、`coui_main_fragment_min_width` / `coui_main_fragment_max_width`），其后是 0.33dp 的 `couiColorDivider` 分割线（`coui_fragment_gap_width`），剩余宽度显示 `detail`，未选中时显示 `emptyDetail`（COUI 的空页面容器）。

布局在单栏和分栏之间切换时，两栏都会保留各自的状态。

## 属性

### ListDetailLayout 属性

| 属性名            | 类型                      | 说明                                   | 默认值                                     | 必须 |
| ----------------- | ------------------------- | -------------------------------------- | ------------------------------------------ | ---- |
| list              | @Composable () -> Unit    | 列表（主）栏                           | -                                          | 是   |
| detail            | (@Composable () -> Unit)? | 详情（子）栏，未选中时为 null          | -                                          | 是   |
| modifier          | Modifier                  | 应用于布局的修饰符                     | Modifier                                   | 否   |
| listWidthFraction | Float                     | 列表占宽度的比例（`setMainFragmentPercent`） | ListDetailLayoutDefaults.ListWidthFraction | 否 |
| minListWidth      | Dp                        | 列表最小宽度                           | ListDetailLayoutDefaults.MinListWidth      | 否   |
| maxListWidth      | Dp                        | 列表最大宽度                           | ListDetailLayoutDefaults.MaxListWidth      | 否   |
| dividerColor      | Color                     | 分割线颜色                             | DividerDefaults.DividerColor               | 否   |
| dividerThickness  | Dp                        | 分割线粗细                             | ListDetailLayoutDefaults.DividerThickness  | 否   |
| emptyDetail       | @Composable () -> Unit    | 分栏模式下的详情占位                   | {}                                         | 否   |

### ListDetailLayoutDefaults 对象

| 名称                       | 类型    | 说明                                 | 值      |
| -------------------------- | ------- | ------------------------------------ | ------- |
| ListWidthFraction          | Float   | `DEFAULT_MAIN_WIDTH_PERCENT`         | 0.4     |
| MinListWidth               | Dp      | `coui_main_fragment_min_width`       | 280.dp  |
| MaxListWidth               | Dp      | `coui_main_fragment_max_width`       | 412.dp  |
| DividerThickness           | Dp      | `coui_fragment_gap_width`            | 0.33.dp |
| isSplitMode(width, height) | Boolean | 某布局尺寸是否显示双栏               | width ≥ 600dp |

## 与 COUI 的差异

COUI 用 `FragmentContainerView` 承载各栏，页面过渡由 Fragment 负责；`ListDetailLayout` 接收可组合插槽，切换时不带过渡动画。
