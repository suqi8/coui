---
title: ContextMenu
requiresScaffoldHost: true
prerequisites:
  - "`OverlayContextMenu` 必须在 `Scaffold` 中使用以提供 `COUIPopupHost`"
  - "`WindowContextMenu` 在平台窗口中渲染，不需要 `Scaffold`"
hostComponent: Scaffold
---

# ContextMenu

`OverlayContextMenu` 与 `WindowContextMenu` 为内容包裹一个在指针处弹出的上下文菜单，对应 ColorOS 的 `COUIContextMenu` / `COUIIsolatedPopupListWindow`：长按或鼠标右键点击时，下拉菜单锚定到该点弹出，而不是出现在内容下方。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=contextMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.menu.OverlayContextMenu
import io.github.suqi8.coui.kmp.menu.WindowContextMenu
```

## 基本用法

```kotlin
val entries = listOf(
    DropdownEntry(
        items = listOf("复制", "分享", "重命名").map { text ->
            DropdownItem(text = text, onClick = { /* 处理 */ })
        },
    ),
    DropdownEntry(items = listOf(DropdownItem(text = "删除", alert = true))),
)

Scaffold {
    // 共享该行的交互源，菜单显示期间该行保持悬停层。
    val row = remember { MutableInteractionSource() }
    OverlayContextMenu(entries = entries, anchorInteractionSource = row) {
        BasicComponent(title = "Report.pdf", onClick = { /* 打开 */ }, interactionSource = row)
    }
}
```

`WindowContextMenu` 参数相同（没有 `renderInRootScaffold`），无需 `Scaffold`。

## 属性

### OverlayContextMenu 属性

| 属性名                   | 类型                     | 说明                            | 默认值                               | 是否必须 |
| --------------------- | ---------------------- | ----------------------------- | --------------------------------- | ---- |
| entries               | List\<DropdownEntry>    | 菜单分组，组间以分割线分隔                 | -                                 | 是    |
| modifier              | Modifier               | 应用于内容外层包裹的修饰符                 | Modifier                          | 否    |
| enabled               | Boolean                | 长按或右键是否弹出菜单                   | true                              | 否    |
| dropdownColors        | DropdownColors         | 菜单行颜色                         | DropdownDefaults.dropdownColors() | 否    |
| maxHeight             | Dp?                    | 菜单最大高度                        | null                              | 否    |
| renderInRootScaffold  | Boolean                | 弹窗是否渲染在根脚手架浮层中（仅 Overlay）     | true                              | 否    |
| collapseOnSelection   | Boolean                | 点击菜单行后是否关闭菜单                  | true                              | 否    |
| hapticFeedbackEnabled | Boolean                | 长按时是否触发长按振动                   | true                              | 否    |
| anchorInteractionSource | MutableInteractionSource? | `content` 中列表行的交互源；菜单显示期间锁定该行的悬停层 | null | 否 |
| onExpandedChange      | ((Boolean) -> Unit)?   | 菜单打开或关闭时回调                    | null                              | 否    |
| content               | @Composable () -> Unit | 注册菜单的内容                       | -                                 | 是    |

## 行为

- **长按**：指针在触摸容差内保持到长按超时后，菜单在按下点弹出（COUI `PreciseLongPressHelper`），并触发长按振动。之后的手势被消费，内容自身的点击不会再触发。
- **右键**：鼠标次键按下时立即弹出菜单，不振动。
- **锚点行**：COUI 在菜单显示期间锁定列表行锚点的悬停层（`COUIPopupListWindow.setAnchorHoveredState`），菜单开始退出时即释放。把同一个 `MutableInteractionSource` 同时传给该行和 `anchorInteractionSource` 即可得到相同效果：长按后该行的按压层淡出，悬停层一直保持到菜单关闭。
- **定位**：锚点收缩为指针所在点（`COUIPopupListWindow.show(view, x, y)`、`COUIIsolatedPopupListWindow` 的 1px 虚拟锚点）；弹窗定位器把菜单放在该点下方或上方、不留锚点间距，并保持在窗口边界内。菜单本身就是常规下拉弹窗，材质、动画与行为不变。
- **无障碍**：包裹层不添加额外语义；请在其他位置为无法长按的用户提供相同操作。
