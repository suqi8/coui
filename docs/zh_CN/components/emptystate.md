# EmptyState

`EmptyState` 是空页面占位组件，对应 ColorOS 的 COUIEmptyStateView：插画槽位、标题、副标题和无边框的操作文字按钮自上而下居中排列。高度有界时，整组位于剩余高度的 45% 处（Small 尺寸类型为 50%）。插画在 `Small` 下隐藏，`Medium` 与 `Large` 均为原尺寸（COUI 17 两档缩放均为 1.0）；`Auto` 在高度低于 200dp 时取 Small，宽度低于 320dp 或高度低于 480dp 时取 Medium，否则取 Large。标题最多两行、副标题最多三行，二者宽 280dp，合计超过 92dp 时可滚动。

COUI 用 Lottie 动画或图片填充插画；这里的 `illustration` 是一个可组合槽位，绘制在按尺寸类型换算后的插画区域内。

<div style="position: relative; height: 460px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=emptyState" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.EmptyStateDefaults
import io.github.suqi8.coui.kmp.basic.EmptyStateSizeType
```

## 基本用法

```kotlin
EmptyState(
    title = "暂无内容",
    modifier = Modifier.fillMaxSize(),
    subtitle = "还没有添加任何内容",
    actionText = "刷新",
    onActionClick = { /* 重新加载 */ },
    illustration = { Image(painterResource(Res.drawable.empty), contentDescription = null) }
)
```

## 属性

### EmptyState 属性

| 属性名             | 类型                      | 说明                                   | 默认值                                 | 必须 |
| ------------------ | ------------------------- | -------------------------------------- | -------------------------------------- | ---- |
| title              | String                    | 标题，为空时隐藏                       | -                                      | 是   |
| modifier           | Modifier                  | 应用于组件的修饰符                     | Modifier                               | 否   |
| subtitle           | String?                   | 副标题，为 null 或空时隐藏             | null                                   | 否   |
| actionText         | String?                   | 操作文字按钮的文字，为空时隐藏         | null                                   | 否   |
| onActionClick      | (() -> Unit)?             | 点击操作文字按钮时回调                 | null                                   | 否   |
| sizeType           | EmptyStateSizeType        | 决定插画缩放的尺寸类型                 | EmptyStateSizeType.Auto                | 否   |
| illustrationWidth  | Dp                        | 插画原始宽度                           | EmptyStateDefaults.IllustrationWidth   | 否   |
| illustrationHeight | Dp                        | 插画原始高度                           | EmptyStateDefaults.IllustrationHeight  | 否   |
| colors             | EmptyStateColors          | 颜色配置                               | EmptyStateDefaults.emptyStateColors()  | 否   |
| titleStyle         | TextStyle                 | 标题文字样式                           | COUITheme.textStyles.headlineXS         | 否   |
| subtitleStyle      | TextStyle                 | 副标题文字样式                         | COUITheme.textStyles.bodyXS         | 否   |
| actionStyle        | TextStyle                 | 操作按钮文字样式                       | COUITheme.textStyles.buttonM          | 否   |
| illustration       | (@Composable () -> Unit)? | 插画槽位，Small 时不组合               | null                                   | 否   |

### EmptyStateSizeType 枚举

`Auto`、`Small`、`Medium`、`Large`。

### EmptyStateDefaults 对象

| 属性名                | 类型          | 说明                         | 默认值                                          |
| --------------------- | ------------- | ---------------------------- | ----------------------------------------------- |
| IllustrationWidth     | Dp            | 插画原始宽度                 | 120.dp                                          |
| IllustrationHeight    | Dp            | 插画原始高度                 | 100.dp                                          |
| TextWidth             | Dp            | 标题与副标题宽度             | 280.dp                                          |
| TextMaxHeight         | Dp            | 文字区最大高度（超出滚动）   | 92.dp                                           |
| SubtitleMarginTop     | Dp            | 副标题上边距                 | 2.dp                                            |
| ActionMarginTop       | Dp            | 操作按钮上边距               | 8.dp                                            |
| ActionPadding         | PaddingValues | 操作按钮按压区域内边距       | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |
| HeightThresholdSmall  | Dp            | Auto 低于此高度时取 Small    | 200.dp                                          |
| WidthThresholdMedium  | Dp            | Auto 低于此宽度时取 Medium   | 320.dp                                          |
| HeightThresholdMedium | Dp            | Auto 低于此高度时取 Medium   | 480.dp                                          |

`EmptyStateDefaults.resolveSizeType(sizeType, width, height)` 按给定尺寸解析 `Auto`。

### EmptyStateColors 类

| 属性名        | 类型  | 说明           |
| ------------- | ----- | -------------- |
| titleColor    | Color | 标题颜色       |
| subtitleColor | Color | 副标题颜色     |
| actionColor   | Color | 操作按钮颜色   |
