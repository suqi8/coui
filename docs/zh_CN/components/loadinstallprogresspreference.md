# LoadInstallProgressPreference

`LoadInstallProgressPreference` 是行尾带下载 / 安装进度按钮的偏好设置行，对应 ColorOS 的 `COUILoadInstallProgressPreference`。行尾控件即小号 [InstallLoadProgress](./installloadprogress) 胶囊（`Widget.COUI.COUILoadProgress.InstallDownloadSmall`），偏好设置行只负责把标签、状态、进度和颜色传给它。点击胶囊触发 `onProgressClick`，与整行点击相互独立。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=loadInstallProgressPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.preference.LoadInstallProgressPreference
```

## 基本用法

```kotlin
var state by remember { mutableStateOf(InstallLoadState.Idle) }
var progress by remember { mutableFloatStateOf(0f) }

LoadInstallProgressPreference(
    title = "天气",
    summary = "18.6 MB",
    progressText = if (state == InstallLoadState.Loading) "${(progress * 100).toInt()}%" else "安装",
    state = state,
    progress = progress,
    onProgressClick = {
        state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
    }
)
```

状态机由调用方维护：COUI 通过 `OnStateChangeListener` 上报状态变化，对应这里在 `onProgressClick` 中更新 `state`。

## 属性

### LoadInstallProgressPreference 属性

| 属性名            | 类型                            | 说明                                 | 默认值                                                | 必须 |
| ----------------- | ------------------------------- | ------------------------------------ | ----------------------------------------------------- | ---- |
| title             | String                          | 标题                                 | -                                                     | 是   |
| progressText      | String                          | 进度按钮标签                         | -                                                     | 是   |
| onProgressClick   | () -> Unit                      | 点击进度按钮的回调                   | -                                                     | 是   |
| modifier          | Modifier                        | 修饰符                               | Modifier                                              | 否   |
| state             | InstallLoadState                | 进度按钮状态                         | InstallLoadState.Idle                                 | 否   |
| progress          | Float                           | 0~1 的进度（Loading / Paused 时显示）| 0f                                                    | 否   |
| titleColor        | BasicComponentColors            | 标题颜色配置                         | BasicComponentDefaults.titleColor()                   | 否   |
| summary           | String?                         | 摘要                                 | null                                                  | 否   |
| summaryColor      | BasicComponentColors            | 摘要颜色配置                         | BasicComponentDefaults.summaryColor()                 | 否   |
| progressColors    | InstallLoadProgressColors       | 进度按钮颜色                         | InstallLoadProgressDefaults.installLoadProgressColors() | 否   |
| progressTextStyle | TextStyle                       | 进度标签文字样式（14sp）             | COUITheme.textStyles.buttonM                         | 否   |
| startAction       | @Composable (() -> Unit)?       | 起始侧自定义内容                     | null                                                  | 否   |
| endActions        | @Composable RowScope.() -> Unit | 末端自定义内容（位于按钮之前）       | {}                                                    | 否   |
| bottomAction      | @Composable (() -> Unit)?       | 底部自定义内容                       | null                                                  | 否   |
| insideMargin      | PaddingValues                   | 内部边距                             | BasicComponentDefaults.InsideMargin                   | 否   |
| cardListPosition  | CardListPosition                | 在卡片分组中的位置                   | LocalCardListPosition.current                         | 否   |
| onClick           | (() -> Unit)?                   | 点击整行（非按钮）的回调             | null                                                  | 否   |
| holdDownState     | Boolean                         | 是否处于按下状态                     | false                                                 | 否   |
| enabled           | Boolean                         | 行与按钮是否可用                     | true                                                  | 否   |

`LoadInstallProgressPreference` 没有独立的 Defaults 对象，尺寸与颜色取自 [InstallLoadProgressDefaults](./installloadprogress)。

## 进阶用法

### 失败状态

```kotlin
LoadInstallProgressPreference(
    title = "计算器",
    summary = "下载失败",
    progressText = "重试",
    state = InstallLoadState.Failed,
    onProgressClick = { /* 重试 */ }
)
```

### 自定义颜色

```kotlin
LoadInstallProgressPreference(
    title = "游戏",
    progressText = "更新",
    onProgressClick = {},
    progressColors = InstallLoadProgressDefaults.installLoadProgressColors(
        progressColor = COUITheme.colorScheme.error,
        contentColor = COUITheme.colorScheme.error
    )
)
```
