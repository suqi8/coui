---
title: WindowCascadingListPopup
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `COUIPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowCascadingListPopup

`WindowCascadingListPopup` 是支持二级菜单的弹出列表组件，通过 `Dialog` 在窗口层级渲染。与 `OverlayCascadingListPopup` 不同，它不需要 `Scaffold` 或 `COUIPopupHost`。`DropdownItem.children` 非空的项会成为子菜单触发行；级联深度限制为 **2 级**。

<div style="position: relative; height: 410px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=windowCascadingListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.window.WindowCascadingListPopup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## 基本用法

`WindowCascadingListPopup` 与 `OverlayCascadingListPopup` 用法一致，区别仅在于无需 `Scaffold` 宿主。下例展示了两个顶层项：一个扁平排序分组，与一个会展开二级列表的"查看模式"触发行。

```kotlin
var showPopup by remember { mutableStateOf(false) }
var sortIndex by remember { mutableStateOf(0) }
var viewIndex by remember { mutableStateOf(0) }

val sortLabels = listOf("按拍摄日期排序", "按添加日期排序")
val viewLabels = listOf("按日期分组", "紧凑视图")
val entries = listOf(
    DropdownEntry(
        items = sortLabels.mapIndexed { idx, label ->
            DropdownItem(
                text = label,
                selected = sortIndex == idx,
                onClick = { sortIndex = idx },
            )
        },
    ),
    DropdownEntry(
        items = listOf(
            DropdownItem(
                text = "查看模式",
                children = viewLabels.mapIndexed { idx, label ->
                    DropdownItem(
                        text = label,
                        selected = viewIndex == idx,
                        onClick = { viewIndex = idx },
                    )
                },
            ),
        ),
    ),
)

Box {
    TextButton(
        text = "点击显示菜单",
        onClick = { showPopup = true },
    )
    WindowCascadingListPopup(
        show = showPopup,
        entries = entries,
        onDismissRequest = { showPopup = false },
    )
}
```

::: tip 级联深度
级联深度上限为 2。二级菜单中的项不会再处理自己的 `children`；更深层的子树会被静默忽略。
:::

## 子菜单行为

子菜单遵循 COUI 17。紧凑窗口（宽度小于 600dp）下子菜单覆盖在主菜单上：宽度与主菜单一致，首行是父项的副本（中粗标题、着色指示器），起始时正好盖在父项所在行上，随后上移到该行上方 14dp，同时轮廓从该行逐渐展开；主菜单缩小到 0.9、上移以保留 12dp 可见，并把行的不透明度降到 30%。较宽窗口下主菜单缩小到 0.9、行不透明度 70%，子菜单在其旁边缩放出现并重叠 14dp。点击主菜单、点击副本标题行、点击弹窗外部或返回会收起子菜单；在主菜单上拖动 500ms 后也会收起；长按子菜单行 650ms 展开后，同一根手指可以直接滑入子菜单并在某一行上松手选中；紧凑窗口下子菜单的阴影随展开进度淡入；处于主菜单层级时，返回与点击外部通过 `onDismissRequest` 关闭弹窗。预测式返回会预览收起或关闭过程，取消时弹窗恢复。

## 模糊背景

与 ColorOS 17 一致，弹窗默认模糊：绘制 COUI 17 弹窗材质（`ListPopupDefaults.materialBlurSpec`：
160px 模糊、glow overlay 混合，mix `#99CDCDCD` / `#99262626`，blend `#BFEEEEEE` / `#99262626`），
并带阴影描边与跟手光斑。coui-ui 自身不采集背景，模糊由调用处作用域内的 `LocalPopupMaterialBlur`
提供者绘制。在脚手架外层提供一次即可，录制菜单所覆盖的页面：

```kotlin
val backdrop = rememberLayerBackdrop()
val popupBlur = remember(backdrop) {
    MaterialBlurProvider { spec, shape ->
        Modifier.couiMaterialBlur(backdrop, spec.toMaterialBlurStyle(), shape) // coui-blur
    }
}
Box(Modifier.layerBackdrop(backdrop)) {
    CompositionLocalProvider(LocalPopupMaterialBlur provides popupBlur) {
        Page() // 从这里打开的菜单都会模糊
    }
}
```

`toMaterialBlurStyle()` 将 `MaterialBlurSpec` 逐字段映射为 coui-blur 的 `MaterialBlurStyle`（参见示例应用
`utils/PanelBlur.kt`）。没有提供者或不支持 runtime shader 时，弹窗保持不透明的 `backgroundColor`，
即 COUI 的非模糊背景。对话框与底部弹窗会为其内容关闭该提供者。传入 `materialBlur = false`
可让单个弹窗不模糊。窗口版本把同一份页面录制绘制到自己的窗口中；在 Android 上不使用系统 blur-behind，因为它模糊的是窗口背后的整个屏幕，而不是菜单的圆角矩形区域。

## 属性

### WindowCascadingListPopup

| 属性名                | 类型                        | 说明                                                                | 默认值                                     |
| --------------------- | --------------------------- | ------------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | 是否显示弹窗                                                        | -                                          |
| entries               | List\<DropdownEntry>        | 由分割线分组的下拉条目；顶层中 `children` 非空的项会成为子菜单触发行 | -                                          |
| onDismissRequest      | () -> Unit                  | 用户请求关闭（点击外部、触发返回等）时调用                          | -                                          |
| popupModifier         | Modifier                    | 应用于弹窗主体的修饰符                                              | Modifier                                   |
| onDismissFinished     | (() -> Unit)?               | 退出动画结束后调用                                                  | null                                       |
| popupPositionProvider | PopupPositionProvider       | 主弹窗相对锚点的定位策略                                            | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | 主弹窗的对齐方式                                                    | PopupPositionProvider.Align.End            |
| enableWindowDim       | Boolean                     | 弹窗显示时是否变暗背景                                              | false                                       |
| maxHeight             | Dp?                         | 任一面的最大高度。null 时由安全区限制                               | null                                       |
| minWidth              | Dp                          | 弹窗的最小宽度                                                      | ListPopupDefaults.MinWidth                 |
| dropdownColors        | DropdownColors              | 每一行使用的颜色配置                                                | DropdownDefaults.dropdownColors()          |
| collapseOnSelection   | Boolean                     | 为 true 时，选中任何叶子项后即关闭弹窗                              | true                                       |
| backgroundModifier    | Modifier                    | 作用于弹窗表面内部、位于行之下，叠加在默认材质模糊之上 | Modifier |
| backgroundColor       | Color                       | 弹窗不模糊时的表面填充色（couiColorBackgroundTop，即 COUI 的非模糊背景） | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | 使用 `LocalPopupMaterialBlur` 提供的 COUI 17 弹窗材质模糊弹窗背后的内容；false 时保持不透明的 `backgroundColor` | true |
| materialEffect        | Boolean                     | 弹窗模糊时绘制阴影描边与跟手光斑 | true |

### DropdownEntry

| 属性名  | 类型                | 说明                                                                              | 默认值 | 是否必须 |
| ------- | ------------------- | --------------------------------------------------------------------------------- | ------ | -------- |
| items   | List\<DropdownItem> | 此分组中显示的条目                                                                | -      | 是       |
| enabled | Boolean             | 此分组是否启用。为 false 时禁用整组条目；为 true 时仍会遵循每个条目的 enabled 状态 | true   | 否       |
| title | String?             | 可选的不可点击分组标题，渲染在各项之上（12sp 中等字重、次级标签色、最多 2 行） | null   | 否       |
| horizontal    | Boolean             | 是否作为 COUI 17 横向条目行（最多 5 个纯图标或 3 个图标+文字），仅弹窗模式，后接分组分割线 | false | 否 |

### DropdownItem

| 属性名   | 类型                              | 说明                                                                          | 默认值 | 是否必须 |
| -------- | --------------------------------- | ----------------------------------------------------------------------------- | ------ | -------- |
| text     | String                            | 选项显示的文本                                                                | -      | 是       |
| enabled  | Boolean                           | 选项是否可点击，禁用时置灰                                                    | true   | 否       |
| selected | Boolean                           | 选项是否处于选中状态                                                          | false  | 否       |
| onClick  | (() -> Unit)?                     | 点击选项时触发的回调。当 `children` 非空时被忽略（点击会改为展开二级菜单）    | null   | 否       |
| icon     | @Composable ((Modifier) -> Unit)? | 显示在选项文本前的图标                                                        | null   | 否       |
| summary  | String?                           | 显示在选项文本下方的摘要文本                                                  | null   | 否       |
| children | List\<DropdownItem>?              | 可选的子菜单项；仅级联变体会将其渲染为二级菜单（最多两级）                    | null   | 否       |
| hint | @Composable (() -> Unit)?         | 可选的尾部提示槽（徽标、红点、短计数），显示在选中指示图标之前，最大宽度 40dp。行被禁用时整体隐藏 | null   | 否       |
| alert | Boolean                           | 是否为警示（危险）项；其标题使用错误色 | false  | 否       |

### DropdownColors

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
| submenuIndicatorColor  | Color | 子菜单收起时指示器的颜色                |

### PopupPositionProvider.Align

| 值          | 说明                         |
| ----------- | ---------------------------- |
| Start       | 将弹窗对齐到锚点的起始端     |
| End         | 将弹窗对齐到锚点的结束端     |
| TopStart    | 将弹窗对齐到锚点的顶部起始端 |
| TopEnd      | 将弹窗对齐到锚点的顶部结束端 |
| BottomStart | 将弹窗对齐到锚点的底部起始端 |
| BottomEnd   | 将弹窗对齐到锚点的底部结束端 |
