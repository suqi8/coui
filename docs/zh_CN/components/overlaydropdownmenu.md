---
title: OverlayDropdownMenu
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 中使用以提供 `COUIPopupHost`
  - 未在 `Scaffold` 中使用将导致弹出内容无法渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayDropdownMenu

`OverlayDropdownMenu` 是基于 `BasicComponent` 的封装，点击后会展开 `OverlayDropdownPopup`。与 `OverlayDropdownPreference` 不同，它不再持有单一的选中索引——选中状态完全保存在每个 `DropdownItem` 的 `selected` 与 `onClick` 上。适用于动作菜单、多选菜单，或弹出项之间不构成单选互斥关系的场景。

<div style="position: relative; height: 410px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlayDropdownMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 使用前提
此组件依赖 `Scaffold` 提供的 `COUIPopupHost` 以显示弹出内容。必须在 `Scaffold` 中使用，否则弹出内容无法正常渲染。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.menu.OverlayDropdownMenu
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## 基本用法

传入单个 `DropdownEntry` 即可显示一个基础下拉菜单行：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val entry = DropdownEntry(
    items = listOf("Option 1", "Option 2", "Option 3").mapIndexed { index, text ->
        DropdownItem(
            text = text,
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
        )
    }
)

Scaffold {
    OverlayDropdownMenu(
        title = "Dropdown Menu",
        entry = entry
    )
}
```

## 分组菜单

传入 `List<DropdownEntry>` 即可显示由分割线隔开的多个分组。`collapseOnSelection` 默认为 `entries.size <= 1`，多分组场景下选中后弹出框保持打开以便连续选择。

```kotlin
var sizeIndex by remember { mutableStateOf(0) }
var colorIndex by remember { mutableStateOf(0) }
val entries = listOf(
    DropdownEntry(
        items = listOf("Small", "Medium").mapIndexed { index, text ->
            DropdownItem(text = text, selected = sizeIndex == index, onClick = { sizeIndex = index })
        }
    ),
    DropdownEntry(
        items = listOf("Red", "Green", "Blue").mapIndexed { index, text ->
            DropdownItem(text = text, selected = colorIndex == index, onClick = { colorIndex = index })
        }
    )
)

Scaffold {
    OverlayDropdownMenu(
        title = "Grouped Menu",
        entries = entries,
        collapseOnSelection = false
    )
}
```

## 多选

选中状态保存在 `DropdownItem.selected` 上，可以同时选中多个条目，在每个条目的 `onClick` 中切换选中状态即可。

```kotlin
var selected by remember { mutableStateOf(setOf("A1", "B2")) }
val entries = listOf(
    DropdownEntry(
        items = listOf("A1", "A2").map { text ->
            DropdownItem(
                text = text,
                selected = text in selected,
                onClick = {
                    selected = if (text in selected) selected - text else selected + text
                }
            )
        }
    ),
    DropdownEntry(
        items = listOf("B1", "B2", "B3").map { text ->
            DropdownItem(
                text = text,
                selected = text in selected,
                onClick = {
                    selected = if (text in selected) selected - text else selected + text
                }
            )
        }
    )
)

Scaffold {
    OverlayDropdownMenu(
        title = "Multi Select Menu",
        entries = entries,
        collapseOnSelection = false
    )
}
```

## 带图标与摘要的选项

每个 `DropdownItem` 都可以在文本前显示图标，并在文本下方显示一行摘要。`icon` lambda 会收到一个预设尺寸的 `Modifier`，应将其应用到图标组件上。

```kotlin
val entry = DropdownEntry(
    items = listOf(
        DropdownItem(
            text = "Rename",
            summary = "Change the display name",
            icon = { modifier ->
                Icon(
                    modifier = modifier,
                    imageVector = COUIIcons.Rename,
                    contentDescription = null,
                )
            },
            onClick = { /* handle action */ },
        ),
        DropdownItem(text = "Delete", onClick = { /* handle action */ }),
    )
)

Scaffold {
    OverlayDropdownMenu(
        title = "Item Icons",
        entry = entry
    )
}
```

## 监听展开状态

```kotlin
var expanded by remember { mutableStateOf(false) }
val entry = DropdownEntry(
    items = listOf("Option 1", "Option 2", "Option 3").map { DropdownItem(text = it) }
)

Scaffold {
    OverlayDropdownMenu(
        title = "Observe Expanded",
        summary = if (expanded) "Expanded" else "Collapsed",
        entry = entry,
        onExpandedChange = { expanded = it }
    )
}
```

## 带提示槽的选项

`hint` 槽渲染在标题区块与选中指示图标之间，最大宽度 40dp，适合放红点、计数徽标或极短标签。与
ColorOS 一致，行被禁用时提示槽整体隐藏。

```kotlin
val entry = DropdownEntry(
    items = listOf(
        DropdownItem(text = "Inbox", hint = { Badge(count = 12) }),
        DropdownItem(text = "Updates", hint = { Badge() }),
        // 该行被禁用，因此徽标不会显示。
        DropdownItem(text = "Archive", hint = { Badge(count = 3) }, enabled = false),
    )
)

Scaffold {
    OverlayDropdownMenu(title = "Hints", entry = entry)
}
```

## 分组标题

`DropdownEntry` 可声明 `title`，渲染为该分组各项之上的不可点击标题行（12sp 中等字重、次级标签
色、最多 2 行）。分组标题与已有的分组分割线可以共存。

```kotlin
val entries = listOf(
    DropdownEntry(
        title = "Sort by",
        items = listOf("Name", "Date modified").map { DropdownItem(text = it) }
    ),
    DropdownEntry(
        title = "Order",
        items = listOf("Ascending", "Descending").map { DropdownItem(text = it) }
    )
)

Scaffold {
    OverlayDropdownMenu(title = "Group Headers", entries = entries)
}
```

## 警示项

设置 `alert = true` 可将某项标记为危险操作，其标题使用错误色而非常规标签色；被禁用的警示项仍回退
到禁用色。

```kotlin
val entry = DropdownEntry(
    items = listOf(
        DropdownItem(text = "Rename"),
        DropdownItem(text = "Delete", alert = true),
    )
)

Scaffold {
    OverlayDropdownMenu(title = "Alert Item", entry = entry)
}
```

## 组件状态

### 禁用状态

```kotlin
OverlayDropdownMenu(
    title = "Disabled Menu",
    summary = "This menu is currently unavailable",
    entry = DropdownEntry(items = listOf(DropdownItem(text = "Option 1"))),
    enabled = false
)
```

当所有 `DropdownEntry` 都不包含任何条目时，菜单也会被隐式禁用。

### 禁用部分选项

通过 `DropdownItem.enabled` 可以禁用单个选项，通过 `DropdownEntry.enabled` 可以禁用整个分组。禁用的行会置灰并忽略点击。

```kotlin
val entries = listOf(
    DropdownEntry(
        items = listOf(
            DropdownItem(text = "Available option"),
            DropdownItem(text = "Unavailable option", enabled = false),
        )
    ),
    DropdownEntry(
        items = listOf(DropdownItem(text = "Whole group disabled")),
        enabled = false
    )
)

Scaffold {
    OverlayDropdownMenu(
        title = "Partially Disabled",
        entries = entries
    )
}
```

## 属性

### OverlayDropdownMenu 属性（Entries 重载）

| 属性名               | 类型                      | 说明                                                                                        | 默认值                                | 是否必须 |
| -------------------- | ------------------------- | ------------------------------------------------------------------------------------------- | ------------------------------------- | -------- |
| entries              | List\<DropdownEntry>      | 由分割线隔开的下拉选项分组                                                                  | -                                     | 是       |
| title                | String                    | 菜单行的标题                                                                                | -                                     | 是       |
| modifier             | Modifier                  | 应用于组件的修饰符                                                                          | Modifier                              | 否       |
| titleColor           | BasicComponentColors      | 标题文本的颜色配置                                                                          | BasicComponentDefaults.titleColor()   | 否       |
| summary              | String?                   | 菜单的摘要说明                                                                              | null                                  | 否       |
| summaryColor         | BasicComponentColors      | 摘要文本的颜色配置                                                                          | BasicComponentDefaults.summaryColor() | 否       |
| dropdownColors       | DropdownColors            | 下拉选项的颜色配置                                                                          | DropdownDefaults.dropdownColors()     | 否       |
| startAction          | @Composable (() -> Unit)? | 左侧显示的自定义内容                                                                        | null                                  | 否       |
| bottomAction         | @Composable (() -> Unit)? | 底部显示的自定义内容                                                                        | null                                  | 否       |
| insideMargin         | PaddingValues             | 组件内部内容的边距                                                                          | BasicComponentDefaults.InsideMargin   | 否       |
| maxHeight            | Dp?                       | 下拉菜单的最大高度                                                                          | null                                  | 否       |
| enabled              | Boolean                   | 组件是否可交互                                                                              | true                                  | 否       |
| renderInRootScaffold | Boolean                   | 是否在根（最外层）Scaffold 中渲染弹窗。为 true 时，弹窗覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染并进行位置补偿 | true | 否 |
| collapseOnSelection  | Boolean                   | 每次选中后是否关闭弹出框                                                                    | entries.size <= 1                     | 否       |
| onExpandedChange     | ((Boolean) -> Unit)?      | 展开状态变化时的回调                                                                        | null                                  | 否       |

### Entry 重载属性

| 属性名              | 类型          | 说明                 | 默认值 | 是否必须 |
| ------------------- | ------------- | -------------------- | ------ | -------- |
| entry               | DropdownEntry | 单个下拉选项分组     | -      | 是       |
| collapseOnSelection | Boolean       | 选中后是否关闭弹出框 | true   | 否       |

其余参数与上方 entries 重载完全一致。

### DropdownEntry 属性

| 属性名  | 类型                | 说明                                                                              | 默认值 | 是否必须 |
| ------- | ------------------- | --------------------------------------------------------------------------------- | ------ | -------- |
| items   | List\<DropdownItem> | 此分组中显示的条目                                                                | -      | 是       |
| enabled | Boolean             | 此分组是否启用。为 false 时禁用整组条目；为 true 时仍会遵循每个条目的 enabled 状态 | true   | 否       |
| title | String?             | 可选的不可点击分组标题，渲染在各项之上（12sp 中等字重、次级标签色、最多 2 行） | null   | 否       |

### DropdownItem 属性

| 属性名   | 类型                              | 说明                         | 默认值 | 是否必须 |
| -------- | --------------------------------- | ---------------------------- | ------ | -------- |
| text     | String                            | 选项显示的文本               | -      | 是       |
| enabled  | Boolean                           | 选项是否可点击，禁用时置灰   | true   | 否       |
| selected | Boolean                           | 选项是否处于选中状态         | false  | 否       |
| onClick  | (() -> Unit)?                     | 点击选项时触发的回调         | null   | 否       |
| icon     | @Composable ((Modifier) -> Unit)? | 显示在选项文本前的图标       | null   | 否       |
| summary  | String?                           | 显示在选项文本下方的摘要文本 | null   | 否       |
| children | List\<DropdownItem>?              | 可选的子菜单项；仅级联变体   | null   | 否       |
| hint | @Composable (() -> Unit)?         | 可选的尾部提示槽（徽标、红点、短计数），显示在选中指示图标之前，最大宽度 40dp。行被禁用时整体隐藏 | null   | 否       |
| alert | Boolean                           | 是否为警示（危险）项；其标题使用错误色 | false  | 否       |

### DropdownColors 属性

| 属性名                 | 类型  | 说明             |
| ---------------------- | ----- | ---------------- |
| contentColor           | Color | 选项标题颜色     |
| summaryColor           | Color | 选项摘要颜色     |
| containerColor         | Color | 选项背景颜色     |
| selectedContentColor   | Color | 选中项标题颜色   |
| selectedSummaryColor   | Color | 选中项摘要颜色   |
| selectedContainerColor | Color | 选中项背景颜色   |
| selectedIndicatorColor | Color | 选中指示图标颜色 |
| disabledContentColor | Color | 禁用项标题颜色 |
| alertContentColor | Color | 警示项标题颜色 |
| headerColor | Color | 分组标题行的标题颜色 |
