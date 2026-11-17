# InstallLoadProgress

`InstallLoadProgress` 是 COUI 中应用商店使用的下载/安装胶囊按钮。它在主色淡染的胶囊上显示文字；下载过程中，主色填充随进度增长，被填充覆盖的文字部分变为白色。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=installLoadProgress" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.InstallLoadProgress
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.InstallLoadStyle
```

## 基本用法

```kotlin
var state by remember { mutableStateOf(InstallLoadState.Idle) }
var progress by remember { mutableFloatStateOf(0f) }

InstallLoadProgress(
    text = if (state == InstallLoadState.Idle) "下载" else "${(progress * 100).toInt()}%",
    onClick = {
        state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
    },
    state = state,
    progress = progress,
)
```

## 状态

| 状态      | 外观                                                                 |
| --------- | -------------------------------------------------------------------- |
| `Idle`    | 小号：淡染胶囊 + 主色文字；大号：主色胶囊 + 白色文字                 |
| `Loading` | 淡染胶囊上叠加宽度为 `progress` 的主色填充，被覆盖的文字变白         |
| `Paused`  | 与 `Loading` 绘制完全相同                                            |
| `Failed`  | 主色胶囊 + 白色文字                                                  |

进度变化走临界阻尼弹簧（COUI `COUISpringForce` bounce 0、response 1.0）。按下时填充色变暗（HSL 亮度 × 0.8），胶囊四边各内缩自身尺寸的 5%，文字缩小到 92%；按下 200ms、抬起 340ms，曲线为 move ease。

## 样式

```kotlin
// 小号（默认）：52 × 28dp，非中文语言环境下加宽 30dp
InstallLoadProgress(text = "获取", onClick = {})

// 大号：220 × 44dp
InstallLoadProgress(text = "下载", onClick = {}, style = InstallLoadStyle.Large)

// 圆形（LOAD_STYLE_CIRCLE）：33dp 图标按钮，text 用作内容描述
InstallLoadProgress(text = "下载", onClick = {}, style = InstallLoadStyle.Circle)
```

圆形样式不绘制文字和进度填充：30dp 圆角方块（圆角 7dp）中是一个下载箭头——空闲时为浅色底、主色箭头；下载中为主色底、白色暂停图标；暂停或失败时为主色底、白色箭头。图标切换时新图标在 340ms 内淡入；按下时方块在 200ms 内缩小到 90% 并变暗。

## 属性

### InstallLoadProgress 属性

| 属性名            | 类型                      | 说明                                         | 默认值                                                  | 是否必须 |
| ----------------- | ------------------------- | -------------------------------------------- | ------------------------------------------------------- | -------- |
| text              | String                    | 按钮文字                                     | -                                                       | 是       |
| onClick           | () -> Unit                | 点击回调                                     | -                                                       | 是       |
| modifier          | Modifier                  | 应用于按钮的修饰符；在此设置的尺寸覆盖样式尺寸 | Modifier                                              | 否       |
| state             | InstallLoadState          | 当前状态                                     | InstallLoadState.Idle                                   | 否       |
| progress          | Float                     | 0 到 1 之间的进度，在下载中/暂停时显示       | 0f                                                      | 否       |
| enabled           | Boolean                   | 是否启用                                     | true                                                    | 否       |
| style             | InstallLoadStyle          | 尺寸样式                                     | InstallLoadStyle.Small                                  | 否       |
| colors            | InstallLoadProgressColors | 颜色配置                                     | InstallLoadProgressDefaults.installLoadProgressColors() | 否       |
| textStyle         | TextStyle                 | 文字样式                                     | COUITheme.textStyles.buttonM                           | 否       |
| interactionSource | MutableInteractionSource? | 交互源                                       | null                                                    | 否       |

### InstallLoadProgressDefaults 对象

#### 常量

| 常量名                         | 类型  | 默认值 | 说明                           |
| ------------------------------ | ----- | ------ | ------------------------------ |
| SmallWidth                     | Dp    | 52.dp  | 小号宽度                       |
| SmallForeignLanguageExtraWidth | Dp    | 30.dp  | 非中文语言环境下小号额外宽度   |
| SmallHeight                    | Dp    | 28.dp  | 小号高度                       |
| LargeWidth                     | Dp    | 220.dp | 大号宽度                       |
| LargeHeight                    | Dp    | 44.dp  | 大号高度                       |
| TextPadding                    | Dp    | 6.dp   | 文字水平内边距                 |
| PressedBrightness              | Float | 0.8f   | 按下时填充色的亮度系数         |
| CircleSize                     | Dp    | 33.dp  | 圆形样式尺寸                   |
| CircleRadius                   | Dp    | 15.dp  | 圆形样式方块的半边长           |
| CircleCornerRadius             | Dp    | 7.dp   | 圆形样式方块的圆角半径         |

#### 方法

| 方法名                        | 类型                      | 说明             |
| ----------------------------- | ------------------------- | ---------------- |
| installLoadProgressColors()   | InstallLoadProgressColors | 创建默认颜色配置 |
| width(style, isChineseLocale) | Dp                        | 样式对应的宽度   |
| height(style)                 | Dp                        | 样式对应的高度   |

### InstallLoadProgressColors 类

| 属性名                 | 类型  | 说明                                   | 默认值（浅色 / 深色）     |
| ---------------------- | ----- | -------------------------------------- | ------------------------- |
| progressColor          | Color | 进度填充色，以及整体填充时的胶囊色     | `#0080FF` / `#1A8CFF`     |
| trackColor             | Color | 进度背后的淡染胶囊色                   | `#260080FF` / `#401A8CFF` |
| disabledColor          | Color | 禁用时所有胶囊层的填充色               | `#14000000` / `#26FFFFFF` |
| contentColor           | Color | 轨道上的文字颜色                       | `#0080FF` / `#1A8CFF`     |
| onProgressContentColor | Color | 进度填充上的文字颜色                   | `#FFFFFF`                 |

## 说明

- 禁用时只把填充色换成 `disabledColor`，文字颜色保持不变，与 COUI 一致。
- 圆形样式（`smallCircleButton`，33dp，带下载/暂停/重试图标）尚未移植。
