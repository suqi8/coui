---
title: OverlayListPopup
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 内使用以提供 `COUIPopupHost`
  - 在 `Scaffold` 外使用会导致弹窗内容不渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayListPopup

`OverlayListPopup` 是 COUI 中的弹出列表组件，用于显示包含多个选项的弹出菜单。它提供了一个轻量级的、浮动的临时列表，适用于各种下拉菜单、上下文菜单等场景。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlayListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 前置条件
此组件依赖于 `Scaffold` 提供的 `COUIPopupHost` 来渲染弹窗内容。必须在 `Scaffold` 内部使用，否则弹窗内容将无法正常渲染。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.DropdownImpl
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
```

## 基本用法

OverlayListPopup 组件可用于创建简单的下拉菜单：

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Option 1", "Option 2", "Option 3")

Scaffold {
    Box {
        TextButton(
            text = "Click to show menu",
            onClick = { showPopup = true }
        )
        OverlayListPopup(
            show = showPopup,
            alignment = PopupPositionProvider.Align.Start,
            onDismissRequest = { showPopup = false } // Close the popup menu
        ) {
            ListPopupColumn {
                items.forEachIndexed { index, string ->
                    DropdownImpl(
                        text = string,
                        optionSize = items.size,
                        isSelected = selectedIndex == index,
                        index = index,
                        onSelectedIndexChange = {
                            selectedIndex = index
                            showPopup = false // Close the popup menu
                        }
                    )
                }
            }
        }
    }
}
```

## 组件状态

### 不同的对齐方式

OverlayListPopup 可以设置不同的对齐选项：

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    alignment = PopupPositionProvider.Align.Start
) {
    ListPopupColumn {
        // Custom content
    }
}
```

### 禁用窗口变暗

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    enableWindowDim = false // Disable dimming layer
) {
    ListPopupColumn {
        // Custom content
    }
}
```

### 上下文菜单定位

除默认的下拉定位器外，`ListPopupDefaults.ContextMenuPositionProvider` 会将弹窗锚定到锚点的某个角，配合角落对齐方式（`TopStart` / `TopEnd` / `BottomStart` / `BottomEnd`）使用：

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
    alignment = PopupPositionProvider.Align.TopEnd,
    onDismissRequest = { showPopup = false }
) {
    ListPopupColumn {
        // Custom content
    }
}
```

你也可以通过 `ListPopupDefaults.dropdownPositionProvider(verticalMargin, horizontalMargin)` 构建带自定义边距的下拉定位器。

## 属性

### OverlayListPopup

| 属性名                | 类型                        | 说明                                 | 默认值                                     |
| --------------------- | --------------------------- | ------------------------------------ | ------------------------------------------ |
| show                  | Boolean                     | 是否显示弹窗                         | -                                          |
| popupModifier         | Modifier                    | 应用于弹窗容器的修饰符               | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | 提供弹窗的位置计算逻辑               | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | 指定弹窗相对于锚点的对齐方式         | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | 是否在弹窗显示时使背景变暗           | true                                       |
| onDismissRequest      | (() -> Unit)?               | 当用户请求关闭（例如点击外部）时触发 | null                                       |
| onDismissFinished     | (() -> Unit)?               | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null    |
| maxHeight             | Dp?                         | 弹窗内容的最大高度                   | null                                       |
| minWidth              | Dp                          | 弹窗内容的最小宽度                   | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | 是否在根（最外层）Scaffold 中渲染弹窗。为 true 时，弹窗覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染并进行位置补偿 | true |
| content               | @Composable () -> Unit      | 要在弹窗内显示的内容                 | -                                          |

### ListPopupColumn

| 属性名  | 类型                   | 说明                   | 默认值 |
| ------- | ---------------------- | ---------------------- | ------ |
| content | @Composable () -> Unit | 要在列内显示的列表内容 | -      |

### DropdownImpl

`DropdownImpl` 可作为 `ListPopupColumn` 内的标准选项行使用。设置 `enabled = false` 可以禁用某一行；禁用行不可点击，并使用禁用文本颜色。

```kotlin
DropdownImpl(
    text = "Disabled option",
    optionSize = items.size,
    isSelected = false,
    index = 1,
    enabled = false,
    onSelectedIndexChange = {}
)
```

基于文本的重载：

| 属性名                | 类型           | 说明               | 默认值                            |
| --------------------- | -------------  | ------------------ | --------------------------------- |
| text                  | String         | 选项显示文本       | -                                 |
| optionSize            | Int            | 选项总数           | -                                 |
| isSelected            | Boolean        | 此选项是否被选中   | -                                 |
| index                 | Int            | 此选项的索引       | -                                 |
| dropdownColors        | DropdownColors | 选项颜色配置       | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | 此选项是否可点击   | true                              |
| dialogMode            | Boolean        | 是否以对话框模式显示此行 | false                        |
| onSelectedIndexChange | (Int) -> Unit  | 点击此选项时的回调 | -                                 |

基于条目的重载接受 `DropdownItem`（可携带 `icon` 与 `summary`），并暴露额外的布局标志：

| 属性名                | 类型           | 说明                                                                       | 默认值                            |
| --------------------- | -------------- | -------------------------------------------------------------------------- | --------------------------------- |
| item                  | DropdownItem   | 当前选项的条目                                                             | -                                 |
| optionSize            | Int            | 选项总数                                                                   | -                                 |
| isSelected            | Boolean        | 此选项是否被选中                                                           | -                                 |
| index                 | Int            | 此选项的索引                                                               | -                                 |
| dropdownColors        | DropdownColors | 选项颜色配置                                                               | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | 此选项是否可点击                                                           | item.enabled                      |
| dialogMode            | Boolean        | 是否以对话框模式显示此行                                                   | false                             |
| hasSubmenu            | Boolean        | 为 true 时此行作为子菜单触发行：尾部显示 chevron 而非选中对勾              | false                             |
| isFirst               | Boolean        | 此行是否为整个弹窗的第一行（控制弹窗模式下更大的顶部内边距）               | index == 0                        |
| isLast                | Boolean        | 此行是否为整个弹窗的最后一行（控制弹窗模式下更大的底部内边距）             | index == optionSize - 1           |
| onSelectedIndexChange | (Int) -> Unit  | 点击此选项时的回调                                                         | -                                 |

### PopupPositionProvider.Align

| 值          | 说明                         |
| ----------- | ---------------------------- |
| Start       | 将弹窗对齐到锚点的起始端     |
| End         | 将弹窗对齐到锚点的结束端     |
| TopStart    | 将弹窗对齐到锚点的顶部起始端 |
| TopEnd      | 将弹窗对齐到锚点的顶部结束端 |
| BottomStart | 将弹窗对齐到锚点的底部起始端 |
| BottomEnd   | 将弹窗对齐到锚点的底部结束端 |

### ListPopupDefaults 对象

ListPopupDefaults 对象提供弹窗的默认值与定位器。

#### 常量

| 常量名         | 类型 | 说明                                   | 值     |
| -------------- | ---- | -------------------------------------- | ------ |
| MinWidth       | Dp   | 弹窗的默认最小宽度                     | 178.dp |
| MaxWidth       | Dp   | `ListPopupColumn` 使用的最大宽度上限   | 232.dp |
| MinPopupHeight | Dp   | 弹窗测量时占用的最小高度               | 50.dp  |

#### 定位器

| 名称                                                | 类型                  | 说明                                                                 |
| --------------------------------------------------- | --------------------- | -------------------------------------------------------------------- |
| DropdownPositionProvider                            | PopupPositionProvider | 将弹窗锚定在锚点下方（空间不足时上方），用于下拉菜单                 |
| ContextMenuPositionProvider                         | PopupPositionProvider | 将弹窗锚定到锚点的某个角，用于上下文菜单                             |
| dropdownPositionProvider(verticalMargin, horizontalMargin) | PopupPositionProvider | 创建带自定义边距的下拉定位器的工厂函数（默认：垂直 8.dp，水平 0.dp） |
