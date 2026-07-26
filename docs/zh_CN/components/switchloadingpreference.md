# SwitchLoadingPreference

`SwitchLoadingPreference` 是带加载态的开关偏好设置行，对应 ColorOS 的 `COUISwitchLoadingPreference`。外观与 `SwitchPreference` 一致，但当 `isLoading` 为 `true` 时，开关滑块上会显示 COUI 加载转圈动画，且整行与开关都无法被切换。适用于异步生效的设置项（例如切换网络功能）。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.SwitchLoadingPreference
```

## 基本用法

典型流程：用户点击该行，`onCheckedChange` 触发，调用方打开加载态并执行异步任务，完成后更新 `checked` 并关闭加载态。

```kotlin
var checked by remember { mutableStateOf(false) }
var target by remember { mutableStateOf(false) }
var isLoading by remember { mutableStateOf(false) }

LaunchedEffect(isLoading) {
    if (isLoading) {
        delay(1500) // 模拟异步任务
        checked = target
        isLoading = false
    }
}

SwitchLoadingPreference(
    checked = checked,
    onCheckedChange = {
        target = it
        isLoading = true
    },
    title = "异步开关",
    summary = "延迟一段时间后应用更改",
    isLoading = isLoading
)
```

## 组件状态

### 加载状态

加载中时，点击整行和开关都会被忽略（COUISwitch 在加载样式下会吞掉触摸事件）：

```kotlin
SwitchLoadingPreference(
    checked = true,
    onCheckedChange = {},
    title = "加载中的开关",
    isLoading = true
)
```

### 禁用状态

```kotlin
SwitchLoadingPreference(
    checked = true,
    onCheckedChange = {},
    title = "禁用开关",
    enabled = false
)
```

## 属性

### SwitchLoadingPreference 属性

| 属性名          | 类型                            | 说明                                         | 默认值                                | 是否必须 |
| --------------- | ------------------------------- | -------------------------------------------- | ------------------------------------- | -------- |
| checked         | Boolean                         | 开关的选中状态                               | -                                     | 是       |
| onCheckedChange | (Boolean) -> Unit               | 开关状态变化时的回调（加载中不触发）         | -                                     | 是       |
| title           | String                          | 设置项标题                                   | -                                     | 是       |
| modifier        | Modifier                        | 应用于组件的修饰符                           | Modifier                              | 否       |
| isLoading       | Boolean                         | 是否显示加载转圈；加载中整行不可切换         | false                                 | 否       |
| titleColor      | BasicComponentColors            | 标题文本的颜色配置                           | BasicComponentDefaults.titleColor()   | 否       |
| summary         | String?                         | 设置项摘要                                   | null                                  | 否       |
| summaryColor    | BasicComponentColors            | 摘要文本的颜色配置                           | BasicComponentDefaults.summaryColor() | 否       |
| startAction     | @Composable (() -> Unit)?       | 左侧显示的自定义内容                         | null                                  | 否       |
| endActions      | @Composable RowScope.() -> Unit | 右侧显示的自定义内容（开关前）               | {}                                    | 否       |
| bottomAction    | @Composable (() -> Unit)?       | 底部显示的自定义内容                         | null                                  | 否       |
| switchColors    | SwitchColors                    | 开关控件的颜色配置                           | SwitchDefaults.switchColors()         | 否       |
| insideMargin    | PaddingValues                   | 组件内部内容的边距                           | BasicComponentDefaults.InsideMargin   | 否       |
| holdDownState   | Boolean                         | 组件是否处于按下状态                         | false                                 | 否       |
| enabled         | Boolean                         | 组件是否可交互                               | true                                  | 否       |

## 进阶用法

### 配合 ViewModel 的异步切换

```kotlin
SwitchLoadingPreference(
    checked = uiState.wifiEnabled,
    onCheckedChange = { viewModel.setWifiEnabled(it) }, // 执行期间置位 uiState.isApplying
    title = "WiFi",
    summary = "打开以连接到无线网络",
    isLoading = uiState.isApplying
)
```

### 带右侧内容

```kotlin
SwitchLoadingPreference(
    checked = checked,
    onCheckedChange = { /* 开始异步任务 */ },
    title = "同步",
    isLoading = isLoading,
    endActions = {
        Text(
            text = if (isLoading) "应用中…" else "",
            color = COUITheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
)
```
