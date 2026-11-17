# SegmentButton

`SegmentButton` 是 COUI 中的分段控件：胶囊形轨道内排列几个互斥的文字分段，胶囊指示器以弹簧动画移动到选中的分段上。它移植自 COUI 17 `COUISegmentButtonLayout`，适用于在少量视图或筛选条件之间切换。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=segmentButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SegmentButton
```

## 基本用法

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

SegmentButton(
    items = listOf("Day", "Week", "Month"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it }
)
```

## 尺寸

每个分段宽度为文字宽度加左右各 12dp，至少 52dp。未固定宽度时控件包裹文字；宽度固定（例如 `Modifier.fillMaxWidth()`）或文字放不下时，按 `COUISegmentButtonLayout.calculateChildrenWidths` 分配宽度：比均分宽度窄的分段保持原宽，其余分段平分剩余宽度。

固定宽度会被 COUI 响应式栅格截断（`getResponsiveWidth`，默认开启）：小屏取窗口的 4 栅格（即整个栅格宽度），中屏 6 栅格，大屏 8 栅格，并在给定宽度内居中。传入 `responsiveWidthEnabled = false` 可铺满整个固定宽度（`setResponsiveWidthEnabled(false)`）。

```kotlin
SegmentButton(
    items = listOf("All", "Allowed", "Not allowed"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    modifier = Modifier.fillMaxWidth()
)
```

小尺寸样式（`SegmentButton.Tiny`，高 32dp，内缩 2dp）：

```kotlin
SegmentButton(
    items = listOf("List", "Grid"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    height = SegmentButtonDefaults.HeightSmall,
    padding = SegmentButtonDefaults.PaddingSmall
)
```

## 交互

`SegmentButton` 遵循 `COUISegmentButtonLayout`：

- 点击分段在抬起时选中；被按下的分段按 COUI 按压缩放收缩。
- 指示器由三个弹簧驱动：位置（response 0.4，bounce 0.1）、宽度（按新旧宽度差异，response 0.6 → 0.4、bounce 0.5 → 0.1，初速度 1000 px/s；变宽时最多超出 7dp，小内缩时 5dp）、高度（先下探到 90% 再弹回）。
- 按住已选分段即抓起指示器：指示器按压收缩并可拖动。拖到其他分段会选中它并触发一次振动；在已选分段内拖动时指示器按橡皮筋跟随手指。拖动超过触摸阈值后控件独占该手势（`attemptClaimDrag`），外层的水平 Pager 或滚动容器不会再抢走；先触发的竖向滚动仍然优先。
- 从右到左布局中第一个分段位于右侧。
- 整个控件跟随 COUI 17 触摸动效（按压缩放、橡皮筋、拖拽形变）；`dragAnimationEnabled = false` 可关闭。
- 与 COUI 一致，禁用状态外观不变，只是不响应触摸。

## 属性

### SegmentButton 属性

| 属性名                | 类型                | 说明                                                   | 默认值                                      | 是否必须 |
| --------------------- | ------------------- | ------------------------------------------------------ | ------------------------------------------- | -------- |
| items                 | List\<String\>      | 分段文字                                               | -                                           | 是       |
| selectedIndex         | Int                 | 选中分段的下标                                         | -                                           | 是       |
| onSelectedIndexChange | (Int) -> Unit       | 用户通过点击或拖动选中其他分段时回调                   | -                                           | 是       |
| modifier              | Modifier            | 应用于控件的修饰符                                     | Modifier                                    | 否       |
| enabled               | Boolean             | 是否响应触摸                                           | true                                        | 否       |
| materialEffect        | Boolean             | 是否绘制 COUI 17 材质（见下文）；与 COUI 一致默认关闭  | false                                       | 否       |
| dragAnimationEnabled  | Boolean             | 整个控件是否跟随 COUI 17 触摸动效                      | true                                        | 否       |
| hapticFeedbackEnabled | Boolean             | 拖到其他分段时是否振动                                 | true                                        | 否       |
| responsiveWidthEnabled | Boolean            | 固定宽度是否按 4 / 6 / 8 栅格截断                     | true                                        | 否       |
| height                | Dp                  | 控件高度，至少为 `MinHeight`                           | SegmentButtonDefaults.Height                | 否       |
| padding               | Dp                  | 指示器相对轨道边缘的内缩                               | SegmentButtonDefaults.Padding               | 否       |
| colors                | SegmentButtonColors | 颜色配置                                               | SegmentButtonDefaults.segmentButtonColors() | 否       |
| textStyle             | TextStyle           | 文字样式                                               | SegmentButtonDefaults.textStyle()           | 否       |

### SegmentButtonDefaults 对象

#### 常量

| 常量名                   | 类型 | 说明                                         | 值     |
| ------------------------ | ---- | -------------------------------------------- | ------ |
| Height                   | Dp   | 默认高度（`SegmentButton` 样式）             | 40.dp  |
| HeightSmall              | Dp   | 小尺寸高度（`SegmentButton.Tiny` 样式）      | 32.dp  |
| MinHeight                | Dp   | 最小高度（`coui_segment_min_height`）        | 28.dp  |
| Padding                  | Dp   | 指示器内缩（`coui_segment_padding`）         | 4.dp   |
| PaddingSmall             | Dp   | 小指示器内缩（`coui_segment_padding_tiny`）  | 2.dp   |
| SegmentPaddingHorizontal | Dp   | 每个分段文字的水平内边距                     | 12.dp  |
| SegmentMinWidth          | Dp   | 分段最小宽度                                 | 52.dp  |

#### 方法

| 方法名                | 类型                 | 说明                                                       |
| --------------------- | -------------------- | ---------------------------------------------------------- |
| segmentButtonColors() | SegmentButtonColors  | 创建默认颜色配置                                           |
| textStyle()           | TextStyle            | 文字样式 `couiTextButtonM`（14sp 中粗，`textStyles.buttonM`） |
| backdropSpec(isDark)  | MaterialBackdropSpec | `TYPE_CONTENT_SEGMENT_BUTTON` 背景模糊：25px，浅色 `#7F333333` 颜色减淡 + `#99EFEFEF` 明度，深色 `#7F7F7F7F` 叠加 + `#99262626` 明度 |

### SegmentButtonColors 类

| 属性名                  | 类型  | 说明                                               | 默认值                       |
| ----------------------- | ----- | -------------------------------------------------- | ---------------------------- |
| backgroundColor         | Color | 轨道填充色                                         | #0F000000 / #1FFFFFFF（深色）|
| indicatorColor          | Color | 指示器填充色                                       | #B2FFFFFF / #33FFFFFF（深色）|
| selectedTextColor       | Color | 选中分段的文字颜色                                 | `labelPrimary`                  |
| unselectedTextColor     | Color | 其他分段的文字颜色                                 | `labelPrimary`                  |
| indicatorShadowColor    | Color | 指示器下方阴影：浅色为渐变中心色，深色为纯色填充   | #0D000000 / #14000000（深色）|
| indicatorShadowEndColor | Color | 浅色模式下指示器阴影渐变的边缘色                   | #03000000                    |

## 进阶用法

### 材质效果

`materialEffect = true` 时绘制 `COUISegmentButtonLayout.setMaterialEffectEnabled(true)` 打开的 COUI 17 材质：轨道的边缘光与内阴影、指示器的边缘光、触摸聚光（这三者需要运行时着色器），以及 `TYPE_CONTENT_SEGMENT_BUTTON` 背景模糊。coui-ui 自身不采集背景：通过 `LocalMaterialBackdrop` 提供 `MaterialBackdropProvider` 后，模糊会取代轨道填充色；没有提供者时轨道保留 `backgroundColor`，即 COUI 的无模糊回退。背景必须记录控件后方的内容，而不能包含控件本身。

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    SegmentButton(
        items = listOf("Recent", "Favorites", "Shared"),
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
        modifier = Modifier.fillMaxWidth(),
        materialEffect = true
    )
}
```

### 自定义颜色

```kotlin
SegmentButton(
    items = listOf("On", "Off"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    colors = SegmentButtonDefaults.segmentButtonColors(
        selectedTextColor = COUITheme.colorScheme.primary
    )
)
```
