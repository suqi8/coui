# TabWithSearchLayout

`TabWithSearchLayout` 是 ColorOS 的响应式「标签栏 + 搜索框」容器 (`COUIPercentTabWithSearchView`)。它按自身宽度对应的 COUI 百分比栅格排布标签栏与搜索框:

- **紧凑** (宽度小于 600dp): 搜索框在上方占满宽度, 起始侧内缩 16dp、末尾侧内缩 7dp; 标签栏在下方占满宽度。
- **中等**: 标签栏占起始侧 4 列栅格加栅格边距, 垂直居中; 搜索框隔一个列间距排在其后, 宽度相同, 末尾侧内缩 15dp。
- **大屏**: 标签栏占 8 列加边距、起始侧内缩 16dp; 搜索框占 4 列加边距, 隔一个列间距排在其后, 末尾侧内缩 31dp。

从右到左布局时整体镜像。两个槽位都按给定宽度测量, 直接传入现有的 `TabRow` 与 `SearchBar` 即可。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tabWithSearchLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayout
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayoutDefaults
```

## 基本用法

```kotlin
val tabs = listOf("全部", "照片", "视频", "相册")
var selected by remember { mutableIntStateOf(0) }
var query by remember { mutableStateOf("") }

TabWithSearchLayout(
    tabs = {
        TabRow(tabs = tabs, selectedTabIndex = selected, onTabSelected = { selected = it })
    },
) {
    SearchBar(
        inputField = {
            InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                label = "搜索",
            )
        },
        onExpandedChange = {},
        // 由布局施加 COUI 的内缩, 搜索框自身不再加水平内边距。
        insideMargin = TabWithSearchLayoutDefaults.SearchBarInsideMargin,
    ) {}
}
```

## 属性

### TabWithSearchLayout 属性

| 属性名    | 类型                   | 说明               | 默认值   | 必需 |
| --------- | ---------------------- | ------------------ | -------- | ---- |
| tabs      | @Composable () -> Unit | 标签栏             | -        | 是   |
| modifier  | Modifier               | 应用于布局的修饰符 | Modifier | 否   |
| searchBar | @Composable () -> Unit | 搜索框             | -        | 是   |

### TabWithSearchLayoutDefaults 对象

| 属性名                        | 类型   | 说明                                     | 值                 |
| ----------------------------- | ------ | ---------------------------------------- | ------------------ |
| SearchBarInsideMargin         | DpSize | 传给 `SearchBar` 的内边距: 无水平内边距  | DpSize(0.dp, 6.dp) |
| ExpandedHorizontalPadding     | Dp     | 大屏时标签栏起始侧内缩                   | 16.dp              |
| ListSpecialPadding            | Dp     | 紧凑时标签栏两侧额外宽度                 | 24.dp              |
| SearchBarPaddingStartCompact  | Dp     | 紧凑时搜索框起始侧内缩                   | 16.dp              |
| SearchBarPaddingEndCompact    | Dp     | 紧凑时搜索框末尾侧内缩                   | 7.dp               |
| SearchBarPaddingStartMedium   | Dp     | 中等时搜索框起始侧内缩                   | 0.dp               |
| SearchBarPaddingEndMedium     | Dp     | 中等时搜索框末尾侧内缩                   | 15.dp              |
| SearchBarPaddingStartExpanded | Dp     | 大屏时搜索框起始侧内缩                   | 0.dp               |
| SearchBarPaddingEndExpanded   | Dp     | 大屏时搜索框末尾侧内缩                   | 31.dp              |

## 进阶用法

### 栅格细节

列宽取自大边距类型的栅格 (`COUIResponsiveUtils.calculateWidth(width, n, 0, 0)`), 额外加上的边距与列间距取自小边距类型。中等 / 大屏的划分还参考窗口高度, 与 `COUIResponsiveUtils.isMediumScreen(context, width, height)` 一致: 宽度不小于 840dp 但高度小于 480dp, 或竖屏宽度小于 960dp 且高度不小于 900dp 的窗口, 仍按中等排布。
