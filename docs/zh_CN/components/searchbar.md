# SearchBar

`SearchBar` 是 Miuix 中用于用户输入搜索内容的组件。它提供了一个直观且易用的搜索界面，支持展开/收起状态切换以及搜索建议展示。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=searchBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import com.suqi8.coui.kmp.basic.SearchBar
import com.suqi8.coui.kmp.basic.InputField
```

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
| outsideEndAction. | @Composable (() -> Unit)?          | 展开时显示在右侧的操作组件 | null                | 否    |
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
| textStyle         | TextStyle                 | 搜索框中文本的样式     | null     | 否    |
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
| InputFieldFontSize          | TextUnit | InputField 文本与标签字号        | 16.sp               |
| LeadingIconStartPadding     | Dp       | 默认前置图标的起始内边距         | 12.dp               |
| LeadingIconEndPadding       | Dp       | 默认前置图标的结束内边距         | 8.dp                |
| TrailingIconStartPadding    | Dp       | 默认清除按钮的起始内边距         | 4.dp                |
| TrailingIconEndPadding      | Dp       | 默认清除按钮的结束内边距         | 4.dp                |
| TrailingIconTouchTargetSize | Dp       | 默认清除按钮的圆形触摸目标尺寸   | 36.dp               |
| TrailingIconVisualSize      | Dp       | 默认清除按钮的可见圆形尺寸       | 18.dp               |

#### 方法

| 方法名            | 类型            | 说明                     |
| ----------------- | --------------- | ------------------------ |
| searchBarColors() | SearchBarColors | 创建输入框的默认颜色配置 |

### SearchBarColors 类

| 属性名          | 类型  | 说明                                                              |
| --------------- | ----- | ----------------------------------------------------------------- |
| backgroundColor | Color | 胶囊背景色（半透明；secondaryContainer，8% 黑 / 15% 白）          |
| labelColor      | Color | 提示（hint）文字颜色（onSurfaceSecondary，54% 黑 / 54% 白）       |
| iconColor       | Color | 默认前置放大镜图标着色（onSurfaceSecondary）                      |
| clearIconColor  | Color | 默认清除按钮圆形填充色（onSurfaceContainerHigh）                  |

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
                    imageVector = COUIIcons.Search,
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
    modifier = Modifier.padding(horizontal = 12.dp),
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
        // SearchBar 已按 COUI 规格提供该插槽两侧的间距
        Text(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    expanded = false
                    searchText = ""
                },
            text = "取消",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            color = COUITheme.colorScheme.primary
        )
    }
) {
    // 搜索结果内容
}
```