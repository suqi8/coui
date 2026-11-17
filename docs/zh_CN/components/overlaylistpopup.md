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
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
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

### 窗口变暗

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    enableWindowDim = true // COUI popups do not dim; opt in here
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

## COUI 17 行为

弹窗遵循 COUI 17 `COUIPopupListWindow`：

- **尺寸**：每行最小 40dp，上下内边距 8dp、左右 20dp，列表上下各留 8dp 内边距。弹窗宽度取最宽的行，限制在 168–256dp（有子菜单行时最小 196dp），高度只显示完整的行。
- **行**：选中行标题仍为 `couiColorLabelPrimary`，尾部显示对勾；子菜单行显示上下箭头指示器。行与行之间没有分割线；分组之间是 12dp 高、带 0.5dp 分割线的间隔，且仅在总项数不少于 3 时出现。
- **横向条目**：`DropdownHorizontalItems`（或 `DropdownEntry(horizontal = true)`）在列表顶部放一行最多 5 个纯图标或 3 个图标+文字的单元格；在弹窗列表内与普通行共享滑动遮罩（17dp / 23dp 圆角）和拖动选择。
- **按压反馈**：整列共享一个圆角遮罩（单行行为胶囊形），在行间滑动；手指拖动时遮罩跟随并在每次换行时轻震，松手选中手指下的行。长按子菜单行 650ms 会展开子菜单。
- **禁用行（与 COUI 不同）**：拖动时，按下的遮罩以同一弹簧继续滑到禁用行上、再滑到下一行，透明度保持满值。COUI 17 会在禁用行上让遮罩原地淡出，并在下一个可用行直接重新出现。禁用行仍然没有震动，在其上松手不会选中任何项。
- **定位**：以锚点（或配合 `preciseClickState` 时的按下位置）水平居中，位于锚点下方 8dp，下方放不下则放在上方，两侧都放不下时放在可用区域顶部；与状态栏保持 16dp、与导航栏或键盘保持 32dp、与左右边缘保持栅格边距（16 / 24 / 40dp）。
- **动效**：弹窗以中心为原点从 0 放大，同时中心沿一条轻微弯曲的路径从锚点飞出并淡入；关闭时沿原路径返回。路径在 Android 上使用 COUI 原始像素常量，其它平台按 density / 3.5 缩放。
- **阴影**：OPlus `SHADOW_LV4` 光源阴影，绘制为随弹窗与光源距离偏移的投射层加一层很淡的环境层。
- **默认不压暗**：COUI 弹窗不会压暗其后的内容。

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
可让单个弹窗不模糊。

阴影描边与跟手光斑（`materialEffect`）在不透明表面上同样绘制，参数不变。COUI 只在弹窗模糊后才绘制它们；
本库保留它们，使无法模糊的菜单也有光效。二者都是以 source-over 叠在表面上的白光，与 ColorOS 17 的实际观感一致：COUI 的 OVERLAY 光斑落在面板自身透明的画布上、位于窗口模糊之上。它们在模糊与深色表面上可见，在浅色模式的纯白填充上则看不出。光斑在手指按下处亮起，随手指在各行之间移动（包括拖动选择），松手时一边扩散一边熄灭。

## 属性

### OverlayListPopup

| 属性名                | 类型                        | 说明                                 | 默认值                                     |
| --------------------- | --------------------------- | ------------------------------------ | ------------------------------------------ |
| show                  | Boolean                     | 是否显示弹窗                         | -                                          |
| popupModifier         | Modifier                    | 应用于弹窗容器的修饰符               | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | 提供弹窗的位置计算逻辑               | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | 指定弹窗相对于锚点的对齐方式         | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | 是否在弹窗显示时使背景变暗           | false                                       |
| onDismissRequest      | (() -> Unit)?               | 当用户请求关闭（例如点击外部）时触发 | null                                       |
| onDismissFinished     | (() -> Unit)?               | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null    |
| maxHeight             | Dp?                         | 弹窗内容的最大高度                   | null                                       |
| minWidth              | Dp                          | 弹窗内容的最小宽度                   | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | 是否在根（最外层）Scaffold 中渲染弹窗。为 true 时，弹窗覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染并进行位置补偿 | true |
| backgroundModifier    | Modifier                    | 作用于弹窗表面内部、位于行之下，叠加在默认材质模糊之上 | Modifier |
| backgroundColor       | Color                       | 弹窗不模糊时的表面填充色（couiColorBackgroundTop，即 COUI 的非模糊背景） | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | 使用 `LocalPopupMaterialBlur` 提供的 COUI 17 弹窗材质模糊弹窗背后的内容；false 时保持不透明的 `backgroundColor` | `LocalCOUIMaterialEffectEnabled.current` |
| materialEffect        | Boolean                     | 绘制阴影描边与跟手光斑（模糊与否均绘制） | `LocalCOUIMaterialEffectEnabled.current` |
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
| reserveIconSlot       | Boolean        | 保留 20dp + 12dp 的前置图标位，使标题与带图标的行对齐 | false     |
| onSelectedIndexChange | (Int) -> Unit  | 点击此选项时的回调 | -                                 |

基于条目的重载接受 `DropdownItem`（可携带 `icon`、`summary` 与自定义尾部 `stateIcon`），并暴露额外的布局标志：

| 属性名                | 类型           | 说明                                                                       | 默认值                            |
| --------------------- | -------------- | -------------------------------------------------------------------------- | --------------------------------- |
| item                  | DropdownItem   | 当前选项的条目                                                             | -                                 |
| optionSize            | Int            | 选项总数                                                                   | -                                 |
| isSelected            | Boolean        | 此选项是否被选中                                                           | -                                 |
| index                 | Int            | 此选项的索引                                                               | -                                 |
| dropdownColors        | DropdownColors | 选项颜色配置                                                               | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | 此选项是否可点击                                                           | item.enabled                      |
| dialogMode            | Boolean        | 是否以对话框模式显示此行                                                   | false                             |
| hasSubmenu            | Boolean        | 为 true 时此行作为子菜单触发行：尾部显示上下箭头指示器而非选中对勾              | false                             |
| submenuExpanded       | Boolean        | 此行打开的子菜单是否正在显示（指示器变为选中色）                           | false                             |
| reserveIconSlot       | Boolean        | 条目没有图标时仍保留前置图标位                                             | false                             |
| onSelectedIndexChange | (Int) -> Unit  | 点击此选项时的回调                                                         | -                                 |

COUI 17 `MixedPopupListAdapter` 只要列表中有任一条目带图标，就让所有竖向行都显示图标位，使标题对齐。自己拼行时，请给同一列表的每一行传 `reserveIconSlot = items.any { it.icon != null }`；下拉菜单与 Spinner 已自动处理。

`DropdownItem.stateIcon` 在 24dp 尾部状态位放一个自定义图标（`PopupListItem.setStateIcon`）。无论是否选中都会显示，并取代选中对勾（子菜单触发行仍显示指示器）；着色与对勾一致：选中时 `selectedIndicatorColor`，否则 `submenuIndicatorColor`（couiColorLabelTertiary），警示条目为 `alertContentColor`。

菜单默认不压暗背后的内容（`enableWindowDim = false`）：`COUIPopupListWindow` 会清空窗口背景，也不添加任何遮罩。

### DropdownGroupDivider 与 DropdownHeaderItem

在 `ListPopupColumn` 中自行排列分组选项时，可以使用下拉菜单在 `DropdownEntry` 分组之间和上方使用的同一组构件。`DropdownGroupDivider` 是 COUI 17 的分组分隔（`MixedPopupListAdapter` 的分割线项）：12dp 高的区带，中间一条 0.5dp 的线，两侧各缩进 20dp。`DropdownHeaderItem` 是不可点击的分组标题行（12sp 中等字重、次要文字色，最多两行），滑动的按压蒙层会从它上方经过。

```kotlin
ListPopupColumn {
    DropdownHeaderItem(text = "排序方式")
    sortOptions.forEachIndexed { index, text ->
        DropdownImpl(text = text, optionSize = sortOptions.size, isSelected = sortIndex == index, index = index, onSelectedIndexChange = { sortIndex = it })
    }
    DropdownGroupDivider()
    DropdownImpl(text = "重置", optionSize = 1, isSelected = false, index = 0, onSelectedIndexChange = { /* 重置 */ })
}
```

| 组件                 | 属性名   | 类型     | 说明             | 默认值                                        |
| -------------------- | -------- | -------- | ---------------- | --------------------------------------------- |
| DropdownGroupDivider | modifier | Modifier | 应用于区带的修饰符 | Modifier                                    |
| DropdownGroupDivider | color    | Color    | 分割线颜色       | DropdownDefaults.PopupGroupDividerColor       |
| DropdownHeaderItem   | text     | String   | 分组标题（必填） | -                                             |
| DropdownHeaderItem   | modifier | Modifier | 应用于该行的修饰符 | Modifier                                    |
| DropdownHeaderItem   | color    | Color    | 标题颜色         | DropdownDefaults.dropdownColors().headerColor |

与 COUI 17 一致，基于 `DropdownEntry` 的下拉菜单只在菜单至少有三个选项时才绘制分组分割线。

### ListPopupContent

`ListPopupContent` 是 `OverlayListPopup` 与 `WindowListPopup` 绘制内容所用的底层弹窗表面。它根据传入的进度 lambda 应用缩放、淡入淡出和重力路径位移，并附带 COUI 17 触摸形变、弹窗投影、28dp 超椭圆圆角表面、`backgroundModifier`，以及（`materialEffect` 为 true 时）阴影描边与触摸光斑。只有在自建带独立动画的弹窗宿主时才需要它；普通菜单请使用上面的列表弹窗组件。

| 属性名                   | 类型                   | 说明                                                   | 默认值                 |
| ------------------------ | ---------------------- | ------------------------------------------------------ | ---------------------- |
| popupContentSize         | IntSize                | 上次上报的内容尺寸，用于跳过重复的尺寸回调             | -                      |
| onPopupContentSizeChange | (IntSize) -> Unit      | 测得的内容尺寸变化时调用                               | -                      |
| fractionProgress         | () -> Float            | 弹窗当前缩放（0 → 1）                                  | -                      |
| alphaProgress            | () -> Float            | 弹窗内容当前透明度（0 → 1）                            | -                      |
| popupLayoutPosition      | PopupLayoutPosition    | 弹窗相对锚点的弹出方向                                 | -                      |
| localTransformOrigin     | TransformOrigin        | 相对内容的缩放原点（COUI 17 以中心缩放）               | -                      |
| modifier                 | Modifier               | 应用于弹窗容器的修饰符                                 | Modifier               |
| backgroundModifier       | Modifier               | 在表面裁剪内、`backgroundColor` 之上应用，例如材质模糊 | Modifier               |
| translation              | () -> Offset           | 当前相对静止位置的偏移（重力路径上的点）               | { Offset.Zero }        |
| backgroundColor          | Color                  | 表面填充色                                             | couiColorBackgroundTop |
| materialEffect           | Boolean                | 是否绘制阴影描边与触摸光斑                             | false                  |
| content                  | @Composable () -> Unit | 弹窗内容                                               | -                      |

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
| MinWidth            | Dp   | 弹窗的默认最小宽度（coui_popup_list_window_min_width）              | 168.dp |
| MinWidthWithSubmenu | Dp   | 含子菜单行时的最小宽度（coui_popup_list_window_width_with_icon）    | 196.dp |
| MaxWidth            | Dp   | `ListPopupColumn` 使用的最大宽度上限                                | 256.dp |
| AnchorGap           | Dp   | 锚点与弹窗之间的间距                                                | 8.dp   |
| TopBarrier          | Dp   | 状态栏下方保留的间距                                                | 16.dp  |
| BottomBarrier       | Dp   | 导航栏或键盘上方保留的间距                                          | 32.dp  |
| HorizontalBarrier   | Dp   | 紧凑窗口的左右间距；参见 `horizontalBarrier(widthDp, heightDp)`     | 16.dp  |
| MinPopupHeight | Dp   | 弹窗测量时占用的最小高度               | 50.dp  |

#### 定位器

| 名称                                                | 类型                  | 说明                                                                 |
| --------------------------------------------------- | --------------------- | -------------------------------------------------------------------- |
| DropdownPositionProvider                            | PopupPositionProvider | 将弹窗锚定在锚点下方（空间不足时上方），用于下拉菜单                 |
| ContextMenuPositionProvider                         | PopupPositionProvider | 将弹窗锚定到锚点的某个角，用于上下文菜单                             |
| dropdownPositionProvider(verticalMargin, horizontalMargin) | PopupPositionProvider | 创建带自定义边距的下拉定位器的工厂函数（默认：垂直 8.dp，水平 0.dp） |
