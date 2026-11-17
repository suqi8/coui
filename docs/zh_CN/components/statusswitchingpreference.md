# StatusSwitchingPreference

`StatusSwitchingPreference` 是行尾控件可在状态文本、状态图标和加载动画之间切换的偏好设置行，对应 ColorOS 的 `COUIStatusSwitchingPreference`。典型用法是行尾显示「开启」/「关闭」，在状态切换进行中显示加载动画。状态文本使用 14sp 正文样式和次要标签色，控件与标题保持 16dp 间距（`coui_status_switching_preference_widget_margin_start`）。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=statusSwitchingPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreferenceDefaults
```

## 基本用法

```kotlin
var enabled by remember { mutableStateOf(false) }
var switching by remember { mutableStateOf(false) }

LaunchedEffect(switching) {
    if (switching) {
        delay(1500) // 应用变更
        enabled = !enabled
        switching = false
    }
}

StatusSwitchingPreference(
    title = "个人热点",
    status = if (enabled) "开启" else "关闭",
    switching = switching,
    onClick = { if (!switching) switching = true }
)
```

## 控件选择

COUI 显示的是最后一次 `showText` / `showIcon` / `showAnim` 调用所选的控件。这里改为由参数推导，优先级如下：

| 条件                   | 控件               | COUI 调用    |
| ---------------------- | ------------------ | ------------ |
| `switching = true`     | 旋转加载动画       | `showAnim`   |
| `statusIcon != null`   | 图标               | `showIcon`   |
| `status != null`       | 状态文本           | `showText`   |
| 其他                   | 不显示控件         | （默认）     |

原版 `showAnim` 循环播放调用方传入的 Lottie 资源；本移植改用 COUI 旋转加载动画。

## 属性

### StatusSwitchingPreference 属性

| 属性名           | 类型                      | 说明                               | 默认值                                              | 必须 |
| ---------------- | ------------------------- | ---------------------------------- | --------------------------------------------------- | ---- |
| title            | String                    | 标题                               | -                                                   | 是   |
| modifier         | Modifier                  | 修饰符                             | Modifier                                            | 否   |
| status           | String?                   | 状态文本                           | null                                                | 否   |
| switching        | Boolean                   | 是否正在切换（显示加载动画）       | false                                               | 否   |
| statusIcon       | @Composable (() -> Unit)? | 状态图标，优先于 `status`          | null                                                | 否   |
| titleColor       | BasicComponentColors      | 标题颜色配置                       | BasicComponentDefaults.titleColor()                 | 否   |
| summary          | String?                   | 摘要                               | null                                                | 否   |
| summaryColor     | BasicComponentColors      | 摘要颜色配置                       | BasicComponentDefaults.summaryColor()               | 否   |
| statusColor      | Color                     | 状态文本颜色                       | StatusSwitchingPreferenceDefaults.statusColor()     | 否   |
| indicatorColor   | Color                     | 加载动画颜色                       | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | 否 |
| indicatorSize    | Dp                        | 加载动画尺寸                       | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize | 否 |
| startAction      | @Composable (() -> Unit)? | 起始侧自定义内容                   | null                                                | 否   |
| bottomAction     | @Composable (() -> Unit)? | 底部自定义内容                     | null                                                | 否   |
| insideMargin     | PaddingValues             | 内部边距                           | BasicComponentDefaults.InsideMargin                 | 否   |
| cardListPosition | CardListPosition          | 在卡片分组中的位置                 | LocalCardListPosition.current                       | 否   |
| onClick          | (() -> Unit)?             | 点击整行的回调                     | null                                                | 否   |
| holdDownState    | Boolean                   | 是否处于按下状态                   | false                                               | 否   |
| enabled          | Boolean                   | 是否可交互                         | true                                                | 否   |

### StatusSwitchingPreferenceDefaults 对象

| 名称              | 类型  | 默认值                                   | 说明                                                     |
| ----------------- | ----- | ---------------------------------------- | -------------------------------------------------------- |
| WidgetMarginStart | Dp    | 16.dp                                    | COUI coui_status_switching_preference_widget_margin_start |
| statusColor()     | Color | COUITheme.colorScheme.labelSecondary | COUI ?couiColorLabelSecondary                            |

## 进阶用法

### 状态图标

```kotlin
StatusSwitchingPreference(
    title = "备份",
    statusIcon = {
        Icon(
            imageVector = COUIIcons.Ok,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = COUITheme.colorScheme.primary
        )
    }
)
```
