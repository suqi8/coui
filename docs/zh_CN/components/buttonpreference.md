# ButtonPreference

`ButtonPreference` 是行尾内嵌小按钮的偏好设置行，对应 ColorOS 的 `COUIButtonPreference`。按钮遵循 COUI 小按钮样式（`couiSmallButtonColorStyle` → `Widget.COUI.Button.Small`）：最小 52×28dp 的胶囊，主题强调色填充，14sp 中等字重标签。按钮点击与整行点击相互独立。

## 引入

```kotlin
import com.suqi8.coui.kmp.preference.ButtonPreference
import com.suqi8.coui.kmp.preference.ButtonPreferenceDefaults
```

## 基本用法

```kotlin
ButtonPreference(
    title = "账号",
    summary = "登录以同步数据",
    buttonText = "登录",
    onButtonClick = { /* 处理按钮点击 */ }
)
```

## 行可点击

行点击（`onClick`）与按钮点击（`onButtonClick`）互不影响：

```kotlin
ButtonPreference(
    title = "存储空间",
    summary = "已使用 12.3 GB",
    buttonText = "清理",
    onButtonClick = { /* 清理 */ },
    onClick = { /* 打开存储详情 */ }
)
```

## 组件状态

### 禁用状态

禁用组件会同时禁用整行与内嵌按钮：

```kotlin
ButtonPreference(
    title = "禁用行",
    summary = "按钮与整行均被禁用",
    buttonText = "操作",
    onButtonClick = {},
    enabled = false
)
```

## 属性

### ButtonPreference 属性

| 属性名             | 类型                            | 说明                               | 默认值                                      | 是否必须 |
| ------------------ | ------------------------------- | ---------------------------------- | ------------------------------------------- | -------- |
| title              | String                          | 设置项标题                         | -                                           | 是       |
| buttonText         | String                          | 内嵌按钮的文字                     | -                                           | 是       |
| onButtonClick      | () -> Unit                      | 点击内嵌按钮时的回调               | -                                           | 是       |
| modifier           | Modifier                        | 应用于组件的修饰符                 | Modifier                                    | 否       |
| titleColor         | BasicComponentColors            | 标题文本的颜色配置                 | BasicComponentDefaults.titleColor()         | 否       |
| summary            | String?                         | 设置项摘要                         | null                                        | 否       |
| summaryColor       | BasicComponentColors            | 摘要文本的颜色配置                 | BasicComponentDefaults.summaryColor()       | 否       |
| startAction        | @Composable (() -> Unit)?       | 左侧显示的自定义内容               | null                                        | 否       |
| endActions         | @Composable RowScope.() -> Unit | 右侧显示的自定义内容（按钮前）     | {}                                          | 否       |
| bottomAction       | @Composable (() -> Unit)?       | 底部显示的自定义内容               | null                                        | 否       |
| buttonColors       | ButtonColors                    | 内嵌按钮的颜色配置                 | ButtonPreferenceDefaults.buttonColors()     | 否       |
| buttonMinWidth     | Dp                              | 内嵌按钮的最小宽度                 | ButtonPreferenceDefaults.ButtonMinWidth     | 否       |
| buttonMinHeight    | Dp                              | 内嵌按钮的最小高度                 | ButtonPreferenceDefaults.ButtonMinHeight    | 否       |
| buttonCornerRadius | Dp                              | 内嵌按钮的圆角半径                 | ButtonPreferenceDefaults.ButtonCornerRadius | 否       |
| buttonInsideMargin | PaddingValues                   | 内嵌按钮的内边距                   | ButtonPreferenceDefaults.ButtonInsideMargin | 否       |
| insideMargin       | PaddingValues                   | 组件内部内容的边距                 | BasicComponentDefaults.InsideMargin         | 否       |
| onClick            | (() -> Unit)?                   | 点击整行（非按钮）时的回调         | null                                        | 否       |
| holdDownState      | Boolean                         | 组件是否处于按下状态               | false                                       | 否       |
| enabled            | Boolean                         | 组件是否可交互                     | true                                        | 否       |

### ButtonPreferenceDefaults 对象

| 常量               | 类型          | 默认值                                             | 说明                                             |
| ------------------ | ------------- | -------------------------------------------------- | ------------------------------------------------ |
| ButtonMinWidth     | Dp            | 52.dp                                              | COUI coui_btn_small_width_min                    |
| ButtonMinHeight    | Dp            | 28.dp                                              | COUI coui_btn_small_height_min                   |
| ButtonCornerRadius | Dp            | 14.dp                                              | 胶囊圆角（COUI drawableRadius -1 = 高度 / 2）    |
| ButtonInsideMargin | PaddingValues | PaddingValues(horizontal = 12.dp, vertical = 4.dp) | Widget.COUI.Button.Small 内边距                  |

#### `buttonColors()` 工厂方法

| 参数                 | 类型  | 默认值                                         |
| -------------------- | ----- | ---------------------------------------------- |
| color                | Color | COUITheme.colorScheme.primary                 |
| disabledColor        | Color | COUITheme.colorScheme.disabledPrimaryButton   |
| contentColor         | Color | COUITheme.colorScheme.onPrimary               |
| disabledContentColor | Color | COUITheme.colorScheme.disabledOnPrimaryButton |

## 进阶用法

### 自定义按钮颜色

```kotlin
ButtonPreference(
    title = "移除设备",
    buttonText = "移除",
    onButtonClick = { /* 移除 */ },
    buttonColors = ButtonPreferenceDefaults.buttonColors(
        color = COUITheme.colorScheme.error,
        contentColor = COUITheme.colorScheme.onError
    )
)
```

### 带左侧图标

```kotlin
ButtonPreference(
    title = "蓝牙设备",
    summary = "已配对",
    buttonText = "连接",
    onButtonClick = { /* 连接 */ },
    startAction = {
        Icon(
            imageVector = COUIIcons.Sort,
            contentDescription = null,
            tint = COUITheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 12.dp)
        )
    }
)
```
