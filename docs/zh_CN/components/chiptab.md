# ChipTab

`ChipTab` 复刻 ColorOS 17 的胶囊标签（`COUIChipTab`）：用于页面筛选的胶囊形单选标签。与 [Chip](./chip) 不同，胶囊标签从不使用强调色：两种状态都保持主要文字颜色，选中的胶囊浮起为抬升表面，带柔和阴影、COUI 17 边缘光，在录制的背景之上还会显示模糊材质。`ChipTabRow` 按 `COUIChipTabLayout` 的方式把标签排成可横向滚动、边缘渐隐的一行。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chipTab" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ChipTab
import io.github.suqi8.coui.kmp.basic.ChipTabContent
import io.github.suqi8.coui.kmp.basic.ChipTabRow
import io.github.suqi8.coui.kmp.basic.ChipTabDefaults
import io.github.suqi8.coui.kmp.basic.ChipTabMaterialType
```

## 基本用法

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

ChipTabRow(
    tabs = listOf("All", "Photos", "Videos", "Documents", "Music"),
    selectedTabIndex = selectedIndex,
    onTabSelected = { selectedIndex = it },
)
```

点击已选中的标签不会有任何变化（`COUIChipTabGroup` 始终保持一个标签选中）。当行可以向某一侧滚动时，该侧的标签在 16dp 内边距处被裁切并在 12dp 内渐隐；点击被可见边缘截断的标签时，会把它滚动到可见区域。

## 切换页面

`ChipTabContent` 按 `COUIChipTabGroupFragmentMediator` 的转场显示选中标签对应的页面：离开的页面以 COUISpringForce（response 0.2，bounce 0）淡出，新页面延迟 150ms 后以 response 0.3 的弹簧淡入。首个页面直接淡入、不延迟；页面隐藏期间保留各自的可保存状态，与中介器保留 Fragment 一致。

```kotlin
val tabs = listOf("全部", "照片", "视频")
var selectedIndex by remember { mutableIntStateOf(0) }

Column {
    ChipTabRow(
        tabs = tabs,
        selectedTabIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
    )
    ChipTabContent(selectedTabIndex = selectedIndex) { index ->
        Text("${tabs[index]}的内容")
    }
}
```

## 前置图标标签

`COUIChipTabLayout` 可以在滚动标签前显示一个仅图标的标签。它始终处于选中状态，单独回调点击，并把滚动标签的内边距收窄到 8dp。

```kotlin
ChipTabRow(
    tabs = tabs,
    selectedTabIndex = selectedIndex,
    onTabSelected = { selectedIndex = it },
    onLeadingIconClick = { /* 打开分类面板 */ },
    leadingIcon = {
        Icon(
            imageVector = COUIIcons.Basic.Sidebar,
            contentDescription = "Categories",
            modifier = Modifier.size(20.dp),
        )
    },
)
```

## 单个胶囊标签

`ChipTab` 也可以单独使用：带文字（可选 16dp 前置图标），或仅图标（32dp 圆形，`COUIChipTab.setShowIconOnly`）：

```kotlin
ChipTab(
    selected = selected,
    onClick = { selected = true },
    label = "Starred",
)

ChipTab(
    selected = searchSelected,
    onClick = { searchSelected = !searchSelected },
    icon = {
        Icon(
            imageVector = COUIIcons.Basic.Search,
            contentDescription = "Search",
            modifier = Modifier.size(16.dp),
        )
    },
)
```

## 材质背景

开启 `materialEffect`（默认）时，ColorOS 17 用其后方内容的背景模糊替代标签填充，并在未选中与选中两组 `COUIMaterialBlurEffect` 预设之间插值。coui-ui 自身不采集背景，因此只有在 `LocalMaterialBackdrop` 提供者之下才绘制模糊（见 [Button](./button)）；没有提供者时，标签保留 COUI 在关闭模糊时自绘的填充。`ChipTabMaterialType.NoBlur`（`TYPE_DEFAULT_NO_BLUR`）对未模糊的背景施加同类颜色滤镜。

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
    ChipTabRow(
        tabs = tabs,
        selectedTabIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
        materialType = ChipTabMaterialType.Default,
    )
}
```

## 组件状态

### 禁用状态

禁用的胶囊标签保留胶囊外观，文字和图标变灰（couiColorLabelTertiary）：

```kotlin
ChipTab(
    selected = true,
    onClick = {},
    label = "Disabled",
    enabled = false,
)
```

## 属性

### ChipTab

| 属性名            | 类型                      | 说明                         | 默认值                           | 是否必须 |
| ----------------- | ------------------------- | ---------------------------- | -------------------------------- | -------- |
| selected          | Boolean                   | 是否选中                     | -                                | 是       |
| onClick           | () -> Unit                | 点击回调                     | -                                | 是       |
| label             | String                    | 文字标签（单行，超出省略）   | -                                | 是       |
| modifier          | Modifier                  | 应用于标签的修饰符           | Modifier                         | 否       |
| enabled           | Boolean                   | 是否可用                     | true                             | 否       |
| materialEffect    | Boolean                   | 是否绘制 COUI 17 材质        | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| materialType      | ChipTabMaterialType       | 背景预设类型                 | ChipTabMaterialType.Default      | 否       |
| icon              | (@Composable () -> Unit)? | 可选前置图标                 | null                             | 否       |
| cornerRadius      | Dp                        | 圆角半径                     | ChipTabDefaults.CornerRadius     | 否       |
| minWidth          | Dp                        | 最小宽度                     | ChipTabDefaults.MinWidth         | 否       |
| minHeight         | Dp                        | 最小高度                     | ChipTabDefaults.MinHeight        | 否       |
| maxWidth          | Dp                        | 最大宽度                     | ChipTabDefaults.MaxWidth         | 否       |
| colors            | ChipTabColors             | 颜色配置                     | ChipTabDefaults.chipTabColors()  | 否       |
| insideMargin      | PaddingValues             | 标签内部水平内边距           | ChipTabDefaults.InsideMargin     | 否       |
| interactionSource | MutableInteractionSource? | 交互源                       | null                             | 否       |

仅图标的重载以 `icon: @Composable () -> Unit` 代替 `label`/`icon`，并以 `size`（默认 `ChipTabDefaults.IconOnlySize`）和 `iconSize`（默认 `ChipTabDefaults.IconSize`）代替尺寸与内边距参数。

### ChipTabRow

| 属性名             | 类型                      | 说明                                                   | 默认值                          | 是否必须 |
| ------------------ | ------------------------- | ------------------------------------------------------ | ------------------------------- | -------- |
| tabs               | List\<String\>            | 各标签的文字                                           | -                               | 是       |
| selectedTabIndex   | Int                       | 选中标签的索引                                         | -                               | 是       |
| onTabSelected      | (Int) -> Unit             | 点击未选中标签时的回调                                 | -                               | 是       |
| modifier           | Modifier                  | 应用于行的修饰符                                       | Modifier                        | 否       |
| enabled            | Boolean                   | 标签是否可用                                           | true                            | 否       |
| materialEffect     | Boolean                   | 标签是否绘制 COUI 17 材质                              | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| materialType       | ChipTabMaterialType       | 背景预设类型                                           | ChipTabMaterialType.Default     | 否       |
| colors             | ChipTabColors             | 标签颜色配置                                           | ChipTabDefaults.chipTabColors() | 否       |
| horizontalPadding  | Dp                        | 滚动标签的内边距；Unspecified = 16dp，有前置图标时 8dp | Dp.Unspecified                  | 否       |
| itemSpacing        | Dp                        | 标签间距                                               | ChipTabDefaults.ItemSpacing     | 否       |
| scrollState        | ScrollState?              | 标签滚动状态                                           | null                            | 否       |
| onLeadingIconClick | () -> Unit                | 点击前置图标标签的回调                                 | {}                              | 否       |
| leadingIcon        | (@Composable () -> Unit)? | 始终选中的前置标签的图标                               | null                            | 否       |

### ChipTabContent

| 属性名           | 类型                             | 说明                                   | 默认值   | 是否必须 |
| ---------------- | -------------------------------- | -------------------------------------- | -------- | -------- |
| selectedTabIndex | Int                              | 要显示的页面，通常是标签行的选中索引   | -        | 是       |
| modifier         | Modifier                         | 应用于页面容器的修饰符                 | Modifier | 否       |
| content          | @Composable (index: Int) -> Unit | 某个索引对应的页面                     | -        | 是       |

### ChipTabDefaults

| 常量名                              | 类型          | 默认值                            | COUI 来源                                             |
| ----------------------------------- | ------------- | --------------------------------- | ----------------------------------------------------- |
| MinWidth                            | Dp            | 52.dp                             | coui_chip_default_min_width                           |
| MinHeight                           | Dp            | 32.dp                             | coui_chip_tab_style_height                            |
| MaxWidth                            | Dp            | 300.dp                            | coui_chip_default_max_width                           |
| TextMaxWidth                        | Dp            | 200.dp                            | coui_chip_default_max_text_width                      |
| CornerRadius                        | Dp            | 16.dp                             | `COUIChipTab.setChipCornerRadius(16dp)`，平滑权重 1.0 |
| IconSize                            | Dp            | 16.dp                             | coui_chip_tab_style_chip_icon_size                    |
| IconSpacing                         | Dp            | 4.dp                              | coui_chip_suggest_style_chip_icon_end_padding         |
| InsideMargin                        | PaddingValues | PaddingValues(horizontal = 12.dp) | coui_chip_suggest_style_chip_horizontal_padding       |
| IconOnlySize                        | Dp            | 32.dp                             | `COUIChipTab.ICON_ONLY_DIAMETER_DP`                   |
| LeadingIconSize                     | Dp            | 20.dp                             | `COUIChipTabLayout.ICON_SIZE_20`                      |
| ItemSpacing                         | Dp            | 8.dp                              | layout_chip_tab `chipSpacingHorizontal`               |
| RowVerticalPadding                  | Dp            | 8.dp                              | `COUIChipTabGroup` paddingVertical                    |
| RowHorizontalPadding                | Dp            | 16.dp                             | `COUIChipTabLayout.PADDING_HORIZONTAL_16`             |
| RowHorizontalPaddingWithLeadingIcon | Dp            | 8.dp                              | `COUIChipTabLayout.PADDING_HORIZONTAL_WITH_ICON_ONLY_CHIP` |
| FadingEdgeLength                    | Dp            | 12.dp                             | `ChipMaskHorizontalScrollView.FADING_EDGE_WIDTH`      |

#### 方法

| 方法名                                              | 类型                 | 说明                                                         |
| --------------------------------------------------- | -------------------- | ------------------------------------------------------------ |
| textStyle()                                         | TextStyle            | 文字样式（COUITheme.textStyles.buttonM，couiTextButtonM 14sp 中粗） |
| chipTabColors()                                     | ChipTabColors        | 创建颜色配置                                                 |
| backdropSpec(isDark, materialType, selectedFraction) | MaterialBackdropSpec | 指定选中进度下插值后的 COUI 17 背景                          |

### `chipTabColors()` 工厂

| 参数                   | 类型  | 默认值                                        | COUI 来源                                     |
| ---------------------- | ----- | --------------------------------------------- | --------------------------------------------- |
| containerColor         | Color | COUITheme.colorScheme.container4（#0A000000 / #1AFFFFFF） | couiColorContainer4（未选中底色）             |
| selectedContainerColor | Color | COUITheme.colorScheme.surfaceContainerHighest | couiColorBackgroundElevated #FFFFFF / #1E1E1E |
| selectedHighlightColor | Color | #40FFFFFF                                     | coui_color_checked_background_gradient_start  |
| selectedStrokeColor    | Color | #0A000000 / #0AFFFFFF                         | coui_color_checked_background_stroke（0.6dp） |
| contentColor           | Color | COUITheme.colorScheme.labelPrimary               | couiColorLabelPrimary                         |
| disabledContentColor   | Color | COUITheme.colorScheme.labelTertiary | couiColorLabelTertiary                        |

### ChipTabMaterialType

| 值      | COUI 来源                                                                             |
| ------- | ------------------------------------------------------------------------------------- |
| Default | `TYPE_DEFAULT`：`TYPE_CONTENT_CHIP_TAB` → `TYPE_CONTENT_CHIP_TAB_SELECTED`，40px 模糊 |
| NoBlur  | `TYPE_DEFAULT_NO_BLUR`：`TYPE_CONTENT_CHIP_TAB_NO_BLUR` → `..._SELECTED_NO_BLUR`，0px |

## 行为

- 选中状态通过两层在 COUISpringForce(response 0.2, bounce 0) 弹簧上交叉淡变（`COUIChipTabDrawable` 的 checked / unchecked 图层透明度）：未选中底色淡出，抬升表面、#40FFFFFF 高光与选中效果淡入。
- 开启材质：背景（在提供者之下）以 `ArgbEvaluator` 混合未选中与选中预设；`TYPE_NO_EFFECT_BASE_1` → `TYPE_FRAMEWORK_CAPSULE_2` 边缘光与内阴影随选中增强；`TYPE_CHIP_TAB` 触摸聚光跟随手指（浅色 PLUS 0.2，深色 SRC_OVER 0.3），并取代按压与悬停遮罩。
- 关闭材质：选中标签绘制 0.6dp 选中描边，按压或悬停时用 `press` / `hover` 着色胶囊。
- 选中标签投射 `ShadowUtils` 5 级（`SHADOW_LV3_A`）光源阴影，其高度（100dp）随选中进度缩放。
- 按压时按 COUI 17 与尺寸相关的按压缩放；键盘焦点绘制 3dp `focusOutline` 外环。与 `COUIChip.handleActionDown` 一致，按压缩放和按压遮罩在手指按下时立即开始，在可滚动的 `ChipTabRow` 中也是如此。
- 文字单行，超出 `TextMaxWidth` 时省略，并在加宽至 `MinWidth` 的标签中居中。
