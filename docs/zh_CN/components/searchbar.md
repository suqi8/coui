# SearchBar

`SearchBar` 是 COUI 中用于用户输入搜索内容的组件。它提供了一个直观且易用的搜索界面，支持展开/收起状态切换以及搜索建议展示。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=searchBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.SearchBarType
```

在 ColorOS 17 上，搜索胶囊与取消按钮都是带顶栏材质的玻璃胶囊（背景滤色、阴影、边缘光、触摸聚光与按压/拖拽动效）；
平台不支持所需混合模式时回退为不透明的栏颜色（浅色 `#FAFAFA`，深色 `#1E1E1E`）。进入与退出编辑态使用弹簧动画
（response 0.5，bounce 0.25）：胶囊收窄的同时，取消按钮从半尺寸放大淡入，两者运动中像融球一样粘连。按压或拖拽胶囊会让它缩放与形变；
清除按钮同样会带动胶囊并点亮触摸聚光；从胶囊内其他可点击元素（自定义图标按钮）开始的手势不会带动胶囊，也不会点亮聚光。

## 基本用法

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // 搜索结果内容
    Column {
        // 在这里添加搜索建议或结果
    }
}
```

## 属性

### SearchBar 属性

| 属性名               | 类型                                 | 说明            | 默认值                 | 是否必须 |
|-------------------|------------------------------------|---------------|---------------------|------|
| inputField        | @Composable () -> Unit             | 搜索输入框组件       | -                   | 是    |
| onExpandedChange  | (Boolean) -> Unit                  | 展开状态变化的回调     | -                   | 是    |
| modifier          | Modifier                           | 应用于搜索栏的修饰符    | Modifier            | 否    |
| insideMargin      | DpSize                             | 内部边距；宽度未指定时取响应式 16 / 24 / 40dp | SearchBarDefaults.InsideMargin | 否    |
| expanded          | Boolean                            | 是否展开显示搜索结果    | false               | 否    |
| type              | SearchBarType                      | `Instant`（带动画，功能按钮在胶囊外）或 `NonInstant`（无动画，功能按钮在胶囊内） | SearchBarType.Instant | 否    |
| navigationButton  | @Composable (() -> Unit)?          | 胶囊前 12dp 处的 40dp 按钮；仅 `NonInstant` 显示 | null                | 否    |
| outerPrimaryButton | @Composable (() -> Unit)?         | 胶囊后 12dp 处的 40dp 按钮 | null                | 否    |
| outerSecondaryButton | @Composable (() -> Unit)?       | 第二个外部按钮；`Instant` 形态只在仅设置了一个外部按钮时显示 | null                | 否    |
| outsideEndAction  | @Composable (() -> Unit)?          | 展开时显示的功能按钮，通常为 `CancelButton`；为 `null` 时隐藏 | null                | 否    |
| content           | @Composable ColumnScope.() -> Unit | 展开时显示的内容      | -                   | 是    |

### InputField 属性

| 属性名               | 类型                        | 说明            | 默认值      | 是否必须 |
|-------------------|---------------------------|---------------|----------|------|
| query             | String                    | 搜索框中的文本内容     | -        | 是    |
| onQueryChange     | (String) -> Unit          | 文本内容变化时的回调    | -        | 是    |
| onSearch          | (String) -> Unit          | 执行搜索操作时的回调    | -        | 是    |
| expanded          | Boolean                   | 是否处于展开状态      | -        | 是    |
| onExpandedChange  | (Boolean) -> Unit         | 展开状态变化的回调     | -        | 是    |
| modifier          | Modifier                  | 应用于输入框的修饰符    | Modifier | 否    |
| label             | String                    | 查询为空时显示的提示文本（展开态同样显示），单行、末尾省略 | ""       | 否    |
| enabled           | Boolean                   | 是否启用搜索框；禁用时提示文字与默认图标改用禁用色 | true     | 否    |
| material          | Boolean                   | 胶囊是否使用 COUI 17 玻璃材质；为 `false` 时绘制纯色 `backgroundColor` 胶囊 | `LocalCOUIMaterialEffectEnabled.current` | 否    |
| textStyle         | TextStyle?                | 搜索框中文本的样式     | null     | 否    |
| colors            | SearchBarColors           | 输入框的颜色配置      | SearchBarDefaults.searchBarColors() | 否    |
| leadingIcon       | @Composable (() -> Unit)? | 前置图标          | 默认放大镜    | 否    |
| innerPrimaryButton | @Composable (() -> Unit)? | 胶囊内 36dp 槽位中的按钮，查询为空时显示（输入文字后由清除按钮取代） | null | 否    |
| innerSecondaryButton | @Composable (() -> Unit)? | `innerPrimaryButton` 之后的第二个内部按钮 | null | 否    |
| interactionSource | MutableInteractionSource? | 交互源           | null     | 否    |
| hints             | List\<String>              | 查询为空时代替 `label` 轮播的提示文本；为空列表时保留 `label` | emptyList() | 否    |
| hintRepeatCount   | Int                       | 轮播停止前播放的切换次数 | Int.MAX_VALUE | 否    |
| onHintChange      | ((Int) -> Unit)?          | 每次切换到新提示时回调其下标 | null     | 否    |

### SearchBarDefaults 对象

SearchBarDefaults 对象提供了 SearchBar 和 InputField 组件的默认值。

#### 常量

| 常量名                      | 类型     | 说明                             | 默认值              |
| --------------------------- | -------- | -------------------------------- | ------------------- |
| InsideMargin                | DpSize   | SearchBar 的内部边距（宽度未指定即响应式） | DpSize(Dp.Unspecified, 6.dp) |
| HorizontalPaddingCompact    | Dp       | 响应式水平内边距：紧凑窗口       | 16.dp               |
| HorizontalPaddingMedium     | Dp       | 响应式水平内边距：中等窗口       | 24.dp               |
| HorizontalPaddingExpanded   | Dp       | 响应式水平内边距：展开窗口       | 40.dp               |
| OuterButtonSize             | Dp       | 导航按钮与外部按钮尺寸           | 40.dp               |
| OuterButtonGap              | Dp       | 胶囊与每个外部按钮的间距         | 12.dp               |
| NavigationButtonEndGap      | Dp       | 导航按钮之后的间距               | 12.dp               |
| InputFieldMinHeight         | Dp       | InputField 背景的最小高度        | 40.dp               |
| InputFieldFontSize          | TextUnit | InputField 文本与标签字号        | 15.sp               |
| LeadingIconStartPadding     | Dp       | 默认前置图标的起始内边距         | 12.dp               |
| IconExpandLeadingIconStartPadding | Dp | 从工具栏图标展开时默认前置图标的起始内边距 | 10.dp       |
| LeadingIconEndPadding       | Dp       | 默认前置图标的结束内边距         | 8.dp                |
| EditTextEndGap              | Dp       | 输入文字之后的间距               | 4.dp                |
| InnerButtonGap              | Dp       | 内部按钮收尾间距，以及非即时功能按钮两侧间距 | 4.dp    |
| InnerButtonSize             | Dp       | 清除按钮与内部按钮的槽位尺寸     | 36.dp               |
| ClearButtonVisualSize       | Dp       | 默认清除按钮的可见圆形尺寸       | 18.dp               |
| FunctionalDividerWidth      | Dp       | 非即时功能按钮前分隔线宽度       | 1.dp                |
| FunctionalDividerHeight     | Dp       | 该分隔线高度                     | 16.dp               |
| NonInstantCancelPadding     | PaddingValues | 非即时胶囊内 CancelButton 的内边距 | PaddingValues(12.dp, 4.dp) |
| CancelHeight                | Dp       | CancelButton 胶囊高度            | 40.dp               |
| CancelPaddingHorizontal     | Dp       | CancelButton 文字水平内边距      | 16.dp               |
| LiftAnimationSpec           | AnimationSpec\<Float> | `Modifier.searchBarLift` 的动画（450ms，COUI move-ease） | tween(450) |
| CancelMaxWidth              | Dp       | CancelButton 最大宽度            | 152.dp              |
| CancelFontSize              | TextUnit | CancelButton 文字字号            | 14.sp               |

#### 方法

| 方法名            | 类型            | 说明                     |
| ----------------- | --------------- | ------------------------ |
| searchBarColors() | SearchBarColors | 创建输入框与取消按钮的默认颜色配置 |
| responsiveHorizontalPadding(width, windowHeight) | Dp | 给定宽度的搜索栏应取的响应式水平内边距 |

### SearchBarColors 类

| 属性名          | 类型  | 说明                                                              |
| --------------- | ----- | ----------------------------------------------------------------- |
| backgroundColor | Color | 关闭材质时的胶囊填充色，也是折叠过渡的起始色（栏颜色，`#FAFAFA` / `#1E1E1E`） |
| labelColor      | Color | 提示（hint）文字颜色（labelSecondary，54% 黑 / 54% 白）       |
| disabledLabelColor | Color | 禁用时的提示文字颜色（disabledNeutral，即 couiColorLabelTertiary） |
| iconColor       | Color | 默认前置放大镜图标着色（labelSecondary）                      |
| disabledIconColor | Color | 禁用时的默认放大镜图标着色（disabledNeutral）                 |
| clearIconColor  | Color | 默认清除按钮圆形填充色（labelTertiary）                  |
| cancelTextColor | Color | CancelButton 文字颜色（labelPrimary，90% 黑 / 90% 白）               |
| disabledCancelTextColor | Color | 禁用时的 CancelButton 文字颜色（disabledNeutral）        |

### CancelButton 属性

`CancelButton` 是 COUI 17 的功能按钮：40dp 玻璃胶囊，14sp 中粗文字。放在 `outsideEndAction` 中时，展开与收起都跟随编辑态弹簧：在半尺寸与原尺寸之间缩放，玻璃胶囊从最多 30px 的模糊中浮现（收起时则溶入模糊），除进度最初的 1/5 外保持不透明，文字随进度淡入淡出。在 `NonInstant` 搜索栏中它位于胶囊内部，是一个纯文字按钮（12dp × 4dp 内边距，无胶囊与材质）。

| 属性名   | 类型            | 说明                         | 默认值                              | 是否必须 |
| -------- | --------------- | ---------------------------- | ----------------------------------- | -------- |
| onClick  | () -> Unit      | 点击回调                     | -                                   | 是       |
| modifier | Modifier        | 应用于按钮的修饰符           | Modifier                            | 否       |
| text     | String          | 按钮文字                     | "Cancel"                            | 否       |
| enabled  | Boolean         | 是否响应点击                 | true                                | 否       |
| material | Boolean         | 胶囊是否使用 COUI 17 玻璃材质 | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| colors   | SearchBarColors | 文字颜色与回退填充色         | SearchBarDefaults.searchBarColors() | 否       |

## 进阶用法

### 带图标的搜索栏

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            leadingIcon = {
                Icon(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    imageVector = COUIIcons.Basic.Search,
                    contentDescription = "搜索"
                )
            }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // 搜索结果内容
}
```

### 带搜索建议的搜索栏

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }
val suggestions = listOf("建议 1", "建议 2", "建议 3")

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
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

### 带取消按钮的搜索栏

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
            label = "搜索"
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outsideEndAction = {
        // SearchBar 会把该插槽放在胶囊后 12dp 处，并带入场动画
        CancelButton(
            text = "取消",
            onClick = {
                expanded = false
                searchText = ""
            }
        )
    }
) {
    // 搜索结果内容
}
```

### 内部按钮

`innerPrimaryButton` 与 `innerSecondaryButton` 位于胶囊内、文字之后，各占一个 36dp 槽位（COUI
`setInnerPrimaryButton` / `setInnerSecondaryButton`），典型用途是语音输入或扫一扫。查询为空时显示；
输入文字后立即换成清除按钮，清空后再换回来。从内部按钮开始的手势不会带动胶囊的触摸动效与聚光。

```kotlin
InputField(
    query = query,
    onQueryChange = { query = it },
    onSearch = {},
    expanded = expanded,
    onExpandedChange = { expanded = it },
    innerPrimaryButton = {
        IconButton(
            onClick = { /* 语音输入 */ },
            material = false,
            minWidth = 36.dp,
            minHeight = 36.dp,
            cornerRadius = 18.dp,
        ) {
            Icon(imageVector = myMicIcon, contentDescription = "语音输入")
        }
    },
)
```

### 外部按钮与导航按钮

`outerPrimaryButton` 与 `outerSecondaryButton` 是胶囊后 12dp 处的 40dp 按钮，通常传 `IconButton`（COUI
`setOuterPrimaryButton` / `setOuterSecondaryButton`）。默认的 `SearchBarType.Instant` 形态只在仅设置了一个外部按钮时显示它；
展开时它淡出、缩小到 0.8 并模糊，胶囊占据其位置；收起时以 response 0.4 / bounce 0.3 的弹簧从 0.5 放大回来。

```kotlin
SearchBar(
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outerPrimaryButton = {
        IconButton(onClick = { /* ... */ }) {
            Icon(imageVector = myScanIcon, contentDescription = "扫一扫")
        }
    },
    outsideEndAction = { CancelButton(text = "取消", onClick = { expanded = false }) },
) {
    // 搜索结果
}
```

### 非即时搜索

`type = SearchBarType.NonInstant` 对应 COUI 的 `TYPE_NON_INSTANT_SEARCH`：状态切换没有动画；展开时功能按钮
（`outsideEndAction`）移到胶囊内部、位于 1dp × 16dp 分隔线之后；`navigationButton`（胶囊前 12dp）和最多两个外部按钮常驻胶囊两侧。

```kotlin
SearchBar(
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    type = SearchBarType.NonInstant,
    navigationButton = {
        IconButton(onClick = { /* 返回 */ }) { /* 返回图标 */ }
    },
    outsideEndAction = { CancelButton(text = "搜索", onClick = { /* 提交 */ }) },
) {
    // 搜索结果
}
```

### 响应式内边距

使用默认 `insideMargin` 时，搜索栏的水平内边距随自身宽度变化（COUI `setUseResponsivePadding(true)`）：
紧凑窗口 16dp、中等窗口 24dp、展开窗口 40dp。给 `insideMargin` 一个确定的宽度（例如
`TabWithSearchLayoutDefaults.SearchBarInsideMargin`）即可固定。

### 从工具栏展开

搜索栏也可以放在工具栏里，从工具栏的搜索图标中展开（`COUISearchBar.showInToolBar`），见 [ToolbarSearch](./toolbarsearch)。

### 轮播提示

`hints` 把占位提示换成 COUI 的提示轮播（`COUIHintAnimationLayout`）：第一条提示立即显示，此后每 3 秒
当前提示在 250ms 内上移并淡出，下一条在 150ms 后从下方升起。位移距离为胶囊剩余高度的一半再加 5dp；
位移曲线为 `PathInterpolator(0.3, 0, 0.2, 1)`，透明度曲线为 COUI ease 曲线。输入文字时提示隐藏且轮播停止；
清空查询后重新显示当前提示，但不再轮播（重新进入组合后才恢复）。可用 `onHintChange` 记住当前显示的提示，例如在空查询提交时
直接搜索它。

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

### 上浮进标题栏

`Modifier.searchBarLift(expanded, targetTop)` 复刻 `COUISearchViewAnimate` 标准形态的进入 / 退出：
`expanded` 时搜索栏从其布局位置在 450ms 内沿 COUI move-ease 曲线（`SearchBarDefaults.LiftAnimationSpec`）
上移到 `targetTop`（窗口坐标，例如状态栏高度），收起时移回。只平移搜索栏本身；被替换的标题需自行隐藏或
淡出。键盘仍由 `InputField` 的焦点请求弹出。

```kotlin
val statusBarTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }

SearchBar(
    modifier = Modifier.searchBarLift(expanded = expanded, targetTop = statusBarTop),
    inputField = { /* InputField(...) */ },
    expanded = expanded,
    onExpandedChange = { expanded = it },
) {
    // 搜索结果
}
```
