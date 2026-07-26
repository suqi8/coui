# LoadingButton

内置加载态的文本按钮,对应 ColorOS 的 COUILoadingButton:加载时标签隐藏,三个小圆点以错峰波浪脉动,并吞掉点击以避免动作被重复触发。

## 引入

```kotlin
import com.suqi8.coui.kmp.basic.LoadingButton
import com.suqi8.coui.kmp.basic.LoadingButtonDefaults
```

## 基本用法

```kotlin
var isLoading by remember { mutableStateOf(false) }

LoadingButton(
    text = "Download",
    onClick = { isLoading = true },
    isLoading = isLoading,
    colors = ButtonDefaults.textButtonColorsPrimary(),
)
```

### 带加载文案

```kotlin
LoadingButton(
    text = "Sign in",
    onClick = { isLoading = true },
    isLoading = isLoading,
    loadingText = "Signing in",
)
```

## 属性

### LoadingButton

| 属性              | 类型                      | 说明                                     | 默认值                            | 必需 |
| ----------------- | ------------------------- | ---------------------------------------- | --------------------------------- | ---- |
| text              | String                    | 非加载态显示的标签                       | -                                 | 是   |
| onClick           | () -> Unit                | 点击回调,加载中不会被调用               | -                                 | 是   |
| modifier          | Modifier                  | 应用于按钮的修饰符                       | Modifier                          | 否   |
| isLoading         | Boolean                   | 按钮是否处于加载态                       | false                             | 否   |
| loadingText       | String?                   | 加载时显示在圆点旁的文案                 | null                              | 否   |
| enabled           | Boolean                   | 按钮是否启用                             | true                              | 否   |
| cornerRadius      | Dp                        | 按钮圆角半径                             | ButtonDefaults.CornerRadius       | 否   |
| minWidth          | Dp                        | 按钮最小宽度                             | ButtonDefaults.MinWidth           | 否   |
| minHeight         | Dp                        | 按钮最小高度                             | ButtonDefaults.MinHeight          | 否   |
| colors            | TextButtonColors          | 颜色配置                                 | ButtonDefaults.textButtonColors() | 否   |
| insideMargin      | PaddingValues             | 按钮内部边距                             | ButtonDefaults.InsideMargin       | 否   |
| interactionSource | MutableInteractionSource? | 按钮的交互源                             | null                              | 否   |
| indication        | Indication?               | 按钮的指示效果(COUI 按压反馈已内置)    | null                              | 否   |

### LoadingButtonDefaults

| 常量       | 类型 | 默认值 |
| ---------- | ---- | ------ |
| DotRadius  | Dp   | 1.dp   |
| DotSpacing | Dp   | 2.dp   |

## 行为

- `isLoading` 为 `true` 时标签隐藏但仍参与测量,按钮宽度不会跳变;点击被吞掉,COUI 按压反馈(缩放 + 着色)仍会播放。
- 三个圆点按线性曲线以 20% -> 50% -> 100% -> 50% -> 20% 的透明度脉动;相邻圆点错峰 333ms,整个 1332ms 波浪循环播放,与 COUILoadingButton 的动画器时序一致。
- 设置 `loadingText` 后,加载态显示该文案及其后三个逐点动画的 "." 字形,替代纯圆点指示(COUIButton `isShowLoadingText`)。
- 圆点使用按钮内容色绘制,与 COUILoadingButton 使用标签画笔一致。
