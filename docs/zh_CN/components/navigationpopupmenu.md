# NavigationPopupMenu

`NavigationPopupMenu` 是锚定在底部导航栏上的导航目的地弹出菜单 (`COUINavigationPopupMenu`), 例如「更多」条目背后的其余页面。菜单末尾边对齐到锚点末尾边内缩 16dp 处, 在导航栏上方 8dp 弹出 (下方放得下时在下方), 宽度至少 208dp, 高度至多 324dp。每行按 `coui_navigation_popup_item.xml` 绘制: 上下各 12dp, 图标距起始边 16dp, 标题距图标与末尾边各 16dp, 16sp 粗体。与 `COUINavigationPopupMenu.MenuAdapter` 一致, 不绘制选中勾 (`selected` 只用于无障碍), 禁用条目置灰。点击可用的行会先关闭菜单, 再执行该条目的动作。

菜单使用 COUI 17 列表弹窗的面板、共享按压遮罩与动效绘制。

提供两种形态:

- `OverlayNavigationPopupMenu` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowNavigationPopupMenu` — 渲染在窗口层级, 无需 `Scaffold`。

::: danger 前提条件
`OverlayNavigationPopupMenu` 依赖 `Scaffold` 提供的 `COUIPopupHost` 渲染弹窗内容。没有 `Scaffold` 时请使用 `WindowNavigationPopupMenu`。
:::

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationPopupMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu
// 或
import io.github.suqi8.coui.kmp.window.WindowNavigationPopupMenu

import io.github.suqi8.coui.kmp.layout.NavigationPopupMenuDefaults
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## 基本用法

在包裹导航栏的布局内调用菜单: 该布局即为锚点。

```kotlin
var selected by remember { mutableIntStateOf(0) }
var showMenu by remember { mutableStateOf(false) }

Scaffold(
    bottomBar = {
        Box {
            NavigationBar {
                NavigationBarItem(selected = selected == 0, onClick = { selected = 0 }, icon = COUIIcons.Home, label = "首页")
                NavigationBarItem(selected = selected >= 1, onClick = { showMenu = true }, icon = COUIIcons.More, label = "更多")
            }
            OverlayNavigationPopupMenu(
                show = showMenu,
                items = listOf(
                    DropdownItem(text = "音乐", selected = selected == 1, onClick = { selected = 1 }),
                    DropdownItem(text = "设置", selected = selected == 2, onClick = { selected = 2 }),
                ),
                onDismissRequest = { showMenu = false },
            )
        }
    },
) { paddingValues ->
    // 内容
}
```

## 属性

### OverlayNavigationPopupMenu / WindowNavigationPopupMenu 属性

| 属性名               | 类型                | 说明                                                        | 默认值                                 | 必需 |
| -------------------- | ------------------- | ----------------------------------------------------------- | -------------------------------------- | ---- |
| show                 | Boolean             | 是否显示菜单                                                | -                                      | 是   |
| items                | List\<DropdownItem> | 目的地列表 (`text`、`icon`、`enabled`、`selected`、`onClick`) | -                                    | 是   |
| onDismissRequest     | () -> Unit          | 点击行、点击外部或返回时回调                                | -                                      | 是   |
| popupModifier        | Modifier            | 应用于弹窗内容区域的修饰符                                  | Modifier                               | 否   |
| onDismissFinished    | (() -> Unit)?       | 退出动画结束后回调                                          | null                                   | 否   |
| dropdownColors       | DropdownColors      | 行颜色                                                      | DropdownDefaults.dropdownColors()      | 否   |
| minWidth             | Dp                  | 菜单最小宽度                                                | NavigationPopupMenuDefaults.MinWidth   | 否   |
| maxHeight            | Dp                  | 菜单最大高度                                                | NavigationPopupMenuDefaults.MaxHeight  | 否   |
| renderInRootScaffold | Boolean             | (仅 Overlay) 是否渲染在根 Scaffold 中                       | true                                   | 否   |
| materialBlur         | Boolean             | 存在模糊提供者时, 是否用 COUI 17 弹窗材质模糊背后内容       | true                                   | 否   |

### NavigationPopupMenuDefaults 对象

| 属性名           | 类型                  | 说明                                         | 值     |
| ---------------- | --------------------- | -------------------------------------------- | ------ |
| ItemVerticalPadding | Dp                 | 行上下内边距 (`coui_navigation_popup_item.xml`) | 12.dp |
| ItemHorizontalPadding | Dp               | 图标起始边距与标题左右边距 (`coui_navigation_popup_horizontal_margin`) | 16.dp |
| MinWidth         | Dp                    | `coui_navigation_popup_item_min_width`       | 208.dp |
| MaxHeight        | Dp                    | `coui_navigation_popup_max_height`           | 324.dp |
| HorizontalMargin | Dp                    | 菜单末尾边相对锚点末尾边的内缩               | 16.dp  |
| VerticalMargin   | Dp                    | 锚点与菜单之间的间距                         | 8.dp   |
| PositionProvider | PopupPositionProvider | 末尾对齐, 下方放得下时在下方, 否则在上方     | -      |
