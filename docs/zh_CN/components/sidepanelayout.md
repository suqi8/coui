# SidePaneLayout

`SidePaneLayout` 在平板、折叠屏和桌面窗口中把侧栏放在主内容旁边。它移植自 COUI `COUISidePaneLayout`：侧栏以 COUI 移动缓动动画滑入滑出，宽布局下内容区随之伸缩，左上角的切换图标负责展开和收起侧栏。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=sidePaneLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SidePaneLayout
import io.github.suqi8.coui.kmp.basic.rememberSidePaneState
```

## 基本用法

```kotlin
val state = rememberSidePaneState()

SidePaneLayout(
    state = state,
    pane = { FolderList() },
) {
    NoteList()
}
```

## 行为

- **分栏模式**（`COUISidePaneUtils.isSplitMode`）：布局宽度不小于 600dp 且窗口高度不小于 480dp 时，侧栏（224dp，`coui_sliding_pane_width`）与内容并排，内容宽度随侧栏变化：`width - paneWidth * progress`。
- **窄布局**：内容保持全宽，被侧栏推开。`coverStyle = true` 时侧栏改为覆盖内容，内容上的 `coui_color_mask` 遮罩随侧栏淡入，点击内容即收起侧栏（`touchContentEnabled`）。
- **动画**：483ms，`COUIMoveEaseInterpolator`（cubic-bezier 0.3, 0, 0.1, 1）。动画被打断时只走剩余比例的时长。
- **切换图标**：48dp，距顶部 45dp、距起始边 12dp（`coui_sliding_icon_layout`）。内容标题行起始处请留出 72dp（`coui_side_pane_layout_toolbar_margin_start`），避免与图标重叠。
- `alwaysShowMask = true` 时任何模式下都在内容上绘制遮罩，并在侧栏打开期间吞掉内容上的触摸，但不收起侧栏。

常见做法是只在窄布局下覆盖：

```kotlin
val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
BoxWithConstraints {
    val split = SidePaneLayoutDefaults.isSplitMode(maxWidth, windowHeight)
    SidePaneLayout(
        state = rememberSidePaneState(initialOpen = split),
        coverStyle = !split,
        pane = { FolderList() },
    ) {
        NoteList()
    }
}
```

## 属性

### SidePaneLayout 属性

| 属性名              | 类型                   | 说明                                         | 默认值                           | 必须 |
| ------------------- | ---------------------- | -------------------------------------------- | -------------------------------- | ---- |
| pane                | @Composable () -> Unit | 侧栏                                         | -                                | 是   |
| modifier            | Modifier               | 应用于布局的修饰符                           | Modifier                         | 否   |
| state               | SidePaneState          | 展开状态                                     | rememberSidePaneState()          | 否   |
| coverStyle          | Boolean                | 侧栏覆盖内容，而不是挤压内容                 | false                            | 否   |
| alwaysShowMask      | Boolean                | 侧栏打开时遮罩内容（并吞掉其触摸）           | false                            | 否   |
| touchContentEnabled | Boolean                | 覆盖样式下点击内容收起侧栏                   | true                             | 否   |
| showToggleIcon      | Boolean                | 是否显示切换图标                             | true                             | 否   |
| paneWidth           | Dp                     | 侧栏宽度                                     | SidePaneLayoutDefaults.PaneWidth | 否   |
| maskColor           | Color                  | 完全展开时的遮罩颜色                         | SidePaneLayoutDefaults.maskColor | 否   |
| toggleIconColor     | Color                  | 切换图标颜色                                 | colorScheme.labelPrimary         | 否   |
| content             | @Composable () -> Unit | 主内容                                       | -                                | 是   |

### SidePaneState

| 成员           | 类型    | 说明                               |
| -------------- | ------- | ---------------------------------- |
| isOpen         | Boolean | 侧栏是否已打开或正在打开           |
| progress       | Float   | 滑动偏移，0（收起）到 1（展开）    |
| isAnimating    | Boolean | 侧栏是否在移动                     |
| open()         | suspend | 以动画展开侧栏                     |
| close()        | suspend | 以动画收起侧栏                     |
| toggle()       | suspend | 切换侧栏，与图标行为一致           |
| snapTo(open)   | suspend | 无动画地展开或收起                 |

`rememberSidePaneState(initialOpen = true)` 创建可保存的状态；与 COUI（`setDefaultShowPane(true)`）一致，侧栏默认展开。

### SidePaneLayoutDefaults 对象

| 名称                      | 类型          | 说明                                         | 值                   |
| ------------------------- | ------------- | -------------------------------------------- | -------------------- |
| ANIMATION_DURATION_MILLIS | Int           | 侧栏动画时长                                 | 483                  |
| AnimationEasing           | Easing        | `COUIMoveEaseInterpolator`                   | (0.3, 0, 0.1, 1)     |
| AnimationSpec             | AnimationSpec | 完整时长的侧栏动画                           | tween(483)           |
| PaneWidth                 | Dp            | `coui_sliding_pane_width`                    | 224.dp               |
| ToggleIconSize            | Dp            | `coui_side_pane_layout_icon_size`            | 48.dp                |
| ToggleIconGlyphSize       | Dp            | 切换按钮内的图标                             | 24.dp                |
| ToggleIconMarginTop       | Dp            | `coui_side_pane_layout_icon_margin_top`      | 45.dp                |
| ToggleIconMarginStart     | Dp            | `coui_side_pane_layout_icon_margin_start`    | 12.dp                |
| maskColor                 | Color         | `coui_color_mask`（`colorScheme.mask`） | #33000000 / #99000000 |
| isSplitMode(width, windowHeight) | Boolean | 侧栏与内容是否并排                       | width ≥ 600dp 且 windowHeight ≥ 480dp |

## 与 COUI 的差异

- 侧栏不能拖动：`COUISidePaneLayout` 只通过受可滑动标记控制的 `ViewDragHelper` 边缘拖动来拖侧栏，Compose 应用可以自行添加手势。
- 分栏判断使用布局宽度（COUI 读取 Activity 的 `screenWidthDp`），且忽略多窗口例外，因此在桌面窗口或文档 iframe 中也能自适应。
