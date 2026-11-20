# ToolbarSearch

`ToolbarSearch` 把 [SearchBar](./searchbar) 放进工具栏，并让它从工具栏的搜索图标中展开，对应 COUI 17
`COUISearchBar.showInToolBar` / `hideInToolBar`。

<div style="position: relative; height: 400px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=toolbarSearch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.basic.toolbarSearchEntry
import io.github.suqi8.coui.kmp.basic.toolbarSearchItem
```

## 行为

开启玻璃材质时（默认 `InputField(material = true)`），胶囊先是搜索图标位置上的一个圆，边移回原位边变宽，文字淡入，
中途模糊最强；取消按钮从图标处以 0.6 倍缩放飞出，与胶囊像融球一样粘连。同时工具栏自身的图标淡出并缩小到 0.8，
标题淡出（工具栏背景不变）。放大镜距胶囊起点 2dp（`coui_search_bar_icon_expand_search_icon_start_gap`），
因此一开始正好位于那个圆的圆心。

搜索栏收起过程中不响应触摸；此时点击搜索图标会重新展开，若触摸发生在收起的最后 5% 内，会先立即完成收起再展开。展开动画结束之前，触摸胶囊或取消按钮只会点亮聚光，不会触发按压缩放、橡皮筋与形变。

展开使用 response 0.55 / bounce 0.2 的弹簧（收起 0.5 / 0.2），工具栏图标使用 0.4 / 0.3（收起 0.5 / 0.2，从 0.5 放大回来）。

使用纯色胶囊（`InputField(material = false)`）时，COUI 不做图标展开（`mFadeInFadeOutAnimation`）：整条搜索栏在原位淡入，
工具栏的全部子元素（包括搜索图标）按同一弹簧淡出并缩小。

## 基本用法

```kotlin
val searchState = rememberToolbarSearchState()
var query by remember { mutableStateOf("") }

Box {
    ToolbarSearchAnchor(state = searchState) {
        val titleColor = COUITheme.colorScheme.labelPrimary
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
                CancelButton(text = "取消", onClick = { searchState.collapse() })
            },
            expanded = searchState.expanded,
            onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
        ) {}
    }
}
```

## API

| API | 说明 |
| --- | --- |
| `rememberToolbarSearchState(initiallyExpanded)` / `ToolbarSearchState` | 展开状态，提供 `expand()` / `collapse()` |
| `ToolbarSearchState.expanded` | 搜索栏是否在工具栏中展开 |
| `ToolbarSearchState.toolbarTitleAlpha` | 搜索栏展开期间应用到工具栏标题颜色上的透明度 |
| `ToolbarSearchAnchor(state, modifier, content)` | 承载工具栏内容（工具栏本身不淡出） |
| `Modifier.toolbarSearchEntry(state)` | 标记搜索栏从中展开的工具栏搜索图标；展开期间隐藏该图标 |
| `Modifier.toolbarSearchItem(state)` | 让其他工具栏图标随展开一起淡出并缩小 |
| `ToolbarFloatingSearchBar(state, modifier, content)` | 在工具栏行上方承载 `SearchBar`；收起后不显示内容 |
