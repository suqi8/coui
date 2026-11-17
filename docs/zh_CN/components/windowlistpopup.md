---
title: WindowListPopup
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `COUIPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowListPopup

`WindowListPopup` 是一个基于 `Dialog` 在窗口层级渲染的弹出列表组件。与 `OverlayListPopup` 不同，它不需要 `Scaffold` 或 `COUIPopupHost`。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=windowListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.window.WindowListPopup
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
```

## 基本用法

WindowListPopup 组件可用于在没有 `Scaffold` 的情况下创建下拉菜单：

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("选项 1", "选项 2", "选项 3")

Box {
    TextButton(
        text = "点击显示菜单",
        onClick = { showPopup = true }
    )
    WindowListPopup(
        show = showPopup,
        alignment = PopupPositionProvider.Align.Start,
        onDismissRequest = { showPopup = false }
    ) {
        val dismiss = LocalDismissState.current
        ListPopupColumn {
            items.forEachIndexed { index, string ->
                DropdownImpl(
                    text = string,
                    optionSize = items.size,
                    isSelected = selectedIndex == index,
                    index = index,
                    onSelectedIndexChange = {
                        selectedIndex = index
                        dismiss?.invoke()
                    }
                )
            }
        }
    }
}
```

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
可让单个弹窗不模糊。窗口版本把同一份页面录制绘制到自己的窗口中；在 Android 上不使用系统 blur-behind，因为它模糊的是窗口背后的整个屏幕，而不是菜单的圆角矩形区域。

阴影描边与跟手光斑（`materialEffect`）在不透明表面上同样绘制，参数不变。COUI 只在弹窗模糊后才绘制它们；
本库保留它们，使无法模糊的菜单也有光效。二者都是以 source-over 叠在表面上的白光，与 ColorOS 17 的实际观感一致：COUI 的 OVERLAY 光斑落在面板自身透明的画布上、位于窗口模糊之上。它们在模糊与深色表面上可见，在浅色模式的纯白填充上则看不出。光斑在手指按下处亮起，随手指在各行之间移动（包括拖动选择），松手时一边扩散一边熄灭。

## 属性

### WindowListPopup 属性

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
| backgroundModifier    | Modifier                    | 作用于弹窗表面内部、位于行之下，叠加在默认材质模糊之上 | Modifier |
| backgroundColor       | Color                       | 弹窗不模糊时的表面填充色（couiColorBackgroundTop，即 COUI 的非模糊背景） | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | 使用 `LocalPopupMaterialBlur` 提供的 COUI 17 弹窗材质模糊弹窗背后的内容；false 时保持不透明的 `backgroundColor` | `LocalCOUIMaterialEffectEnabled.current` |
| materialEffect        | Boolean                     | 绘制阴影描边与跟手光斑（模糊与否均绘制） | `LocalCOUIMaterialEffectEnabled.current` |
| content               | @Composable () -> Unit      | 要在弹窗内显示的内容                 | -                                          |

### ListPopupColumn 属性

| 属性名  | 类型                   | 说明                   | 默认值 |
| ------- | ---------------------- | ---------------------- | ------ |
| content | @Composable () -> Unit | 要在列内显示的列表内容 | -      |

### DropdownImpl

`DropdownImpl` 可作为 `ListPopupColumn` 内的标准选项行使用。设置 `enabled = false` 可以禁用某一行；禁用行不可点击，并使用禁用文本颜色。

```kotlin
DropdownImpl(
    text = "禁用选项",
    optionSize = items.size,
    isSelected = false,
    index = 1,
    enabled = false,
    onSelectedIndexChange = {}
)
```

| 属性名                | 类型          | 说明               | 默认值                            |
| --------------------- | ------------- | ------------------ | --------------------------------- |
| text                  | String        | 选项显示文本       | -                                 |
| optionSize            | Int           | 选项总数           | -                                 |
| isSelected            | Boolean       | 此选项是否被选中   | -                                 |
| index                 | Int           | 此选项的索引       | -                                 |
| dropdownColors        | DropdownColors | 选项颜色配置      | DropdownDefaults.dropdownColors() |
| enabled               | Boolean       | 此选项是否可点击   | true                              |
| dialogMode            | Boolean       | 使用对话框样式的行高与内边距，而非弹窗样式 | false             |
| onSelectedIndexChange | (Int) -> Unit | 点击此选项时的回调 | -                                 |

`DropdownImpl` 还提供接收完整 `DropdownItem`（代替 `text`）的重载，用于显示带图标或摘要的行。它额外接受 `hasSubmenu`（渲染尾部上下箭头指示器）和 `submenuExpanded`（子菜单展开时指示器变为主色）；`enabled` 默认为 `item.enabled`。两个重载都接受 `reserveIconSlot`：COUI 17 只要列表中有任一条目带图标，就让每一行都保留 20dp + 12dp 的图标位，所以自己拼行时请给每一行传 `items.any { it.icon != null }`。`DropdownItem.stateIcon` 在 24dp 尾部状态位绘制自定义图标（`PopupListItem.setStateIcon`），着色与选中对勾一致。

```kotlin
DropdownImpl(
    item = DropdownItem(
        text = "Copy",
        summary = "Copy to clipboard",
    ),
    optionSize = items.size,
    isSelected = false,
    index = 0,
    onSelectedIndexChange = { /* handle click */ }
)
```

### PopupPositionProvider.Align

| 值          | 说明                         |
| ----------- | ---------------------------- |
| Start       | 将弹窗对齐到锚点的起始端     |
| End         | 将弹窗对齐到锚点的结束端     |
| TopStart    | 将弹窗对齐到锚点的顶部起始端 |
| TopEnd      | 将弹窗对齐到锚点的顶部结束端 |
| BottomStart | 将弹窗对齐到锚点的底部起始端 |
| BottomEnd   | 将弹窗对齐到锚点的底部结束端 |

### LocalDismissState

提供一个 `(() -> Unit)?` 函数，用于从内容内部关闭当前弹窗。这是所有弹出组件提供的统一关闭状态。

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "关闭",
    onClick = { dismiss?.invoke() }
)
```
