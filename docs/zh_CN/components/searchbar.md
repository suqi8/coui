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
| insideMargin      | DpSize                             | 内部边距          | SearchBarDefaults.InsideMargin | 否    |
| expanded          | Boolean                            | 是否展开显示搜索结果    | false               | 否    |
| outsideEndAction  | @Composable (() -> Unit)?          | 展开时显示在右侧的操作组件，通常为 `CancelButton` | null                | 否    |
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
| label             | String                    | 搜索框为空时显示的提示文本 | ""       | 否    |
| enabled           | Boolean                   | 是否启用搜索框       | true     | 否    |
| material          | Boolean                   | 胶囊是否使用 COUI 17 玻璃材质；为 `false` 时绘制纯色 `backgroundColor` 胶囊 | true | 否    |
| textStyle         | TextStyle?                | 搜索框中文本的样式     | null     | 否    |
| colors            | SearchBarColors           | 输入框的颜色配置      | SearchBarDefaults.searchBarColors() | 否    |
| leadingIcon       | @Composable (() -> Unit)? | 前置图标          | 默认放大镜    | 否    |
| trailingIcon      | @Composable (() -> Unit)? | 后置图标          | 默认清除按钮   | 否    |
| interactionSource | MutableInteractionSource? | 交互源           | null     | 否    |

### SearchBarDefaults 对象

SearchBarDefaults 对象提供了 SearchBar 和 InputField 组件的默认值。

#### 常量

| 常量名                      | 类型     | 说明                             | 默认值              |
| --------------------------- | -------- | -------------------------------- | ------------------- |
| InsideMargin                | DpSize   | SearchBar 的内部边距             | DpSize(16.dp, 6.dp) |
| InputFieldMinHeight         | Dp       | InputField 背景的最小高度        | 40.dp               |
| InputFieldFontSize          | TextUnit | InputField 文本与标签字号        | 15.sp               |
| LeadingIconStartPadding     | Dp       | 默认前置图标的起始内边距         | 12.dp               |
| LeadingIconEndPadding       | Dp       | 默认前置图标的结束内边距         | 8.dp                |
| TrailingIconStartPadding    | Dp       | 默认清除按钮的起始内边距         | 4.dp                |
| TrailingIconEndPadding      | Dp       | 默认清除按钮的结束内边距         | 4.dp                |
| TrailingIconTouchTargetSize | Dp       | 默认清除按钮的圆形触摸目标尺寸   | 36.dp               |
| TrailingIconVisualSize      | Dp       | 默认清除按钮的可见圆形尺寸       | 18.dp               |
| CancelHeight                | Dp       | CancelButton 胶囊高度            | 40.dp               |
| CancelPaddingHorizontal     | Dp       | CancelButton 文字水平内边距      | 16.dp               |
| CancelMaxWidth              | Dp       | CancelButton 最大宽度            | 152.dp              |
| CancelFontSize              | TextUnit | CancelButton 文字字号            | 14.sp               |

#### 方法

| 方法名            | 类型            | 说明                     |
| ----------------- | --------------- | ------------------------ |
| searchBarColors() | SearchBarColors | 创建输入框与取消按钮的默认颜色配置 |

### SearchBarColors 类

| 属性名          | 类型  | 说明                                                              |
| --------------- | ----- | ----------------------------------------------------------------- |
| backgroundColor | Color | 关闭材质时的胶囊填充色，也是折叠过渡的起始色（栏颜色，`#FAFAFA` / `#1E1E1E`） |
| labelColor      | Color | 提示（hint）文字颜色（onSurfaceSecondary，54% 黑 / 54% 白）       |
| iconColor       | Color | 默认前置放大镜图标着色（onSurfaceSecondary）                      |
| clearIconColor  | Color | 默认清除按钮圆形填充色（onSurfaceContainerHigh）                  |
| cancelTextColor | Color | CancelButton 文字颜色（onSurface，90% 黑 / 90% 白）               |

### CancelButton 属性

`CancelButton` 是 COUI 17 的功能按钮：40dp 玻璃胶囊，14sp 中粗文字。放在 `outsideEndAction` 中时，文字随编辑态弹簧淡入。

| 属性名   | 类型            | 说明                         | 默认值                              | 是否必须 |
| -------- | --------------- | ---------------------------- | ----------------------------------- | -------- |
| onClick  | () -> Unit      | 点击回调                     | -                                   | 是       |
| modifier | Modifier        | 应用于按钮的修饰符           | Modifier                            | 否       |
| text     | String          | 按钮文字                     | "Cancel"                            | 否       |
| enabled  | Boolean         | 是否响应点击                 | true                                | 否       |
| material | Boolean         | 胶囊是否使用 COUI 17 玻璃材质 | true                                | 否       |
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

### 从工具栏展开

ColorOS 17 上，搜索栏可以放在工具栏里，从工具栏的搜索图标中展开（`COUISearchBar.showInToolBar`）。
胶囊先是图标位置上的一个圆，边移回原位边变宽，文字淡入，中途模糊最强；取消按钮从图标处飞出，与胶囊像融球一样粘连；
工具栏自身的图标同时淡出并缩小到 0.8，标题淡出（工具栏背景不变）；收起的最后 5% 内触摸搜索图标会立即完成收起。
展开使用 response 0.55 / bounce 0.2 的弹簧（收起 0.5 / 0.2），
工具栏图标使用 0.4 / 0.3（收起 0.5 / 0.2，从 0.5 放大回来）。

```kotlin
val searchState = rememberToolbarSearchState()
var query by remember { mutableStateOf("") }

Box {
    ToolbarSearchAnchor(state = searchState) {
        val titleColor = COUITheme.colorScheme.onSurface
        TopAppBar(
            title = "标题",
            titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
            navigationIcon = {
                IconButton(modifier = Modifier.toolbarSearchItem(searchState), onClick = { /* 返回 */ }) { /* ... */ }
            },
            actions = {
                IconButton(
                    modifier = Modifier.toolbarSearchEntry(searchState),
                    onClick = { searchState.expand() },
                ) {
                    Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "搜索")
                }
            },
        )
    }
    ToolbarFloatingSearchBar(
        state = searchState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) {
        SearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = searchState.expanded,
                    onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
                )
            },
            outsideEndAction = {
                CancelButton(text = "取消", onClick = { query = ""; searchState.collapse() })
            },
            expanded = searchState.expanded,
            onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
        ) {}
    }
}
```

| API | 说明 |
| --- | --- |
| `rememberToolbarSearchState()` / `ToolbarSearchState` | 展开状态，提供 `expand()` / `collapse()` |
| `ToolbarSearchAnchor` | 承载工具栏内容（工具栏本身不淡出） |
| `ToolbarSearchState.toolbarTitleAlpha` | 搜索栏展开期间应用到工具栏标题颜色上的透明度 |
| `Modifier.toolbarSearchEntry(state)` | 标记搜索栏从中展开的工具栏搜索图标；展开期间隐藏该图标 |
| `Modifier.toolbarSearchItem(state)` | 让其他工具栏图标随展开一起淡出并缩小 |
| `ToolbarFloatingSearchBar` | 在工具栏行上方承载 `SearchBar`；收起后不显示内容 |
