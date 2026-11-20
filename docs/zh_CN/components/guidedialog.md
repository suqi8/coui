# GuideDialog

`GuideDialog` 是 ColorOS 的分页引导对话框 (`COUIGuideDialogBuilder`, `COUIGuideLayoutContentView`): 底部对话框 (大屏居中), 内含可左右滑动的分页, 每页由铺满宽度裁切的插图、标题与描述组成, 下方是页面指示器和「跳过」/「下一页」两个按钮。

「下一页」以 COUI 650ms 弹簧曲线翻页。快速滑动松手后, 以同一条弹簧曲线在 500–1000ms 内停到下一页或上一页, 时长随滑动速度变化（`COUIViewPager2` 的 `FlingScrollConfig`）; 慢速松手则按 RecyclerView 的五次缓动曲线回到最近的一页。页面内容超出分页区域时可滚动, 边缘有 32dp 渐隐。从倒数第二页滚到最后一页时, 下一页按钮逐渐展宽盖住淡出的跳过按钮, 文字同时交叉淡变为「知道了」, 点击后回调 `onStart`。只有一页时只显示「知道了」按钮, 不显示指示器。点击面板外部不会关闭; 返回手势回调 `onDismissRequest`。

提供两种形态:

- `OverlayGuideDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowGuideDialog` — 渲染在窗口层级, 无需 `Scaffold`。

::: danger 前提条件
`OverlayGuideDialog` 依赖 `Scaffold` 提供的 `COUIPopupHost` 渲染弹窗内容。没有 `Scaffold` 时请使用 `WindowGuideDialog`。
:::

<div style="position: relative; height: 560px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=guideDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowGuideDialog

import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.layout.GuideDialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogColors
```

## 基本用法

```kotlin
var showGuide by remember { mutableStateOf(false) }
val pages = listOf(
    GuideDialogPage(title = "滑动切换", description = "在底部横条上左右滑动切换应用。", image = painterResource(Res.drawable.guide_1)),
    GuideDialogPage(title = "快捷回复", description = "在通知中直接回复。", image = painterResource(Res.drawable.guide_2)),
)

Scaffold {
    TextButton(text = "显示引导", onClick = { showGuide = true })

    OverlayGuideDialog(
        show = showGuide,
        pages = pages,
        onStart = { showGuide = false },
        onSkip = { showGuide = false },
        onDismissRequest = { showGuide = false },
    )
}
```

- 页面缺少 `image`, 或 `title` / `description` 为空白时, 对应部分不显示。
- `onSkip = null` (默认) 时「跳过」回调 `onStart`。

## 属性

### OverlayGuideDialog / WindowGuideDialog 属性

| 属性名               | 类型                   | 说明                                                      | 默认值                                   | 必需 |
| -------------------- | ---------------------- | --------------------------------------------------------- | ---------------------------------------- | ---- |
| show                 | Boolean                | 是否显示对话框                                            | -                                        | 是   |
| pages                | List\<GuideDialogPage> | 引导页列表                                                | -                                        | 是   |
| onStart              | () -> Unit             | 点击「知道了」时回调                                      | -                                        | 是   |
| modifier             | Modifier               | 应用于对话框面板的修饰符                                  | Modifier                                 | 否   |
| backgroundModifier   | Modifier               | 作用于面板圆角裁剪内部、内容之下 (用于模糊背景)           | Modifier                                 | 否   |
| onSkip               | (() -> Unit)?          | 点击跳过时回调; 为 null 时回落到 `onStart`                | null                                     | 否   |
| onNext               | ((Int) -> Unit)?       | 「下一页」翻页时回调, 参数为目标页                        | null                                     | 否   |
| onDismissRequest     | (() -> Unit)?          | 返回手势时回调 (点击外部被忽略)                           | null                                     | 否   |
| skipText             | String                 | 跳过按钮文字                                              | GuideDialogDefaults.SkipText             | 否   |
| nextText             | String                 | 下一页按钮文字                                            | GuideDialogDefaults.NextText             | 否   |
| startText            | String                 | 开始按钮文字                                              | GuideDialogDefaults.StartText            | 否   |
| imageHeight          | Dp                     | 插图高度                                                  | GuideDialogDefaults.ImageHeight          | 否   |
| imagePadding         | PaddingValues          | 每张插图四周的边距                                        | GuideDialogDefaults.ImagePadding         | 否   |
| colors               | GuideDialogColors      | 标题与描述颜色                                            | GuideDialogDefaults.guideDialogColors()  | 否   |
| backgroundColor      | Color                  | 面板填充色                                                | DialogDefaults.backgroundColor()         | 否   |
| enableWindowDim      | Boolean                | 是否压暗对话框背后的窗口                                  | true                                     | 否   |
| materialStroke       | Boolean                | 绘制模糊面板的 COUI 17 材质描边; 按钮也随之切换材质       | false                                    | 否   |
| onDismissFinished    | (() -> Unit)?          | 隐藏动画结束后回调                                        | null                                     | 否   |
| renderInRootScaffold | Boolean                | (仅 Overlay) 是否渲染在根 Scaffold 中                     | true                                     | 否   |

### GuideDialogPage 属性

| 属性名                  | 类型     | 说明                                  | 默认值 |
| ----------------------- | -------- | ------------------------------------- | ------ |
| title                   | String?  | 页面标题 (`?couiTextHeadlineS`)       | null   |
| description             | String?  | 页面描述 (`?couiTextBodyS`)           | null   |
| image                   | Painter? | 插图, 按铺满宽度裁切                  | null   |
| imageContentDescription | String?  | 插图的无障碍描述                      | null   |

### GuideDialogDefaults 对象

| 属性名           | 类型          | 说明                               | 值                  |
| ---------------- | ------------- | ---------------------------------- | ------------------- |
| SkipText         | String        | `coui_guide_dialog_skip_text`      | "Skip"              |
| NextText         | String        | `coui_guide_dialog_next_text`      | "Next"              |
| StartText        | String        | `coui_guide_dialog_known_text`     | "Got it"            |
| ImageHeight      | Dp            | `ImageHeightStyle.DEFAULT`         | 328.dp              |
| ImageHeightSmall | Dp            | `ImageHeightStyle.HEIGHT_240DP`    | 240.dp              |
| ImagePadding     | PaddingValues | 无边距                             | PaddingValues(0.dp) |

| 函数名              | 返回类型          | 说明                                                             |
| ------------------- | ----------------- | ---------------------------------------------------------------- |
| guideDialogColors() | GuideDialogColors | 标题 `?couiColorLabelPrimary`, 描述 `?couiColorLabelSecondary`   |

### GuideDialogColors 属性

| 属性名           | 类型  | 说明         |
| ---------------- | ----- | ------------ |
| titleColor       | Color | 页面标题颜色 |
| descriptionColor | Color | 页面描述颜色 |

## 进阶用法

### 单页引导

```kotlin
OverlayGuideDialog(
    show = showGuide,
    pages = listOf(GuideDialogPage(title = "全新外观", description = "工具栏现在悬浮在内容之上。")),
    onStart = { showGuide = false },
    startText = "好的",
)
```

### 较矮的插图

```kotlin
OverlayGuideDialog(
    show = showGuide,
    pages = pages,
    onStart = { showGuide = false },
    imageHeight = GuideDialogDefaults.ImageHeightSmall,
    imagePadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp),
)
```
