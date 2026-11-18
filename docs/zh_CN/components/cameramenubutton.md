# CameraMenuButton

`CameraMenuButton` 是相机底部效果菜单的圆形按钮，移植自相机的 `BottomMenuIcon`：54dp 的 10% 白色圆盘配白色图标，选中后变为橙色强调色圆盘配黑色图标，并带有相机的边缘光。`CameraEffectButton` 是相机悬浮在快门行上方、用于打开美颜、滤镜或虚化面板的 32dp 小入口按钮（`MenuLeftButton` / `MenuRightButton`）。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraMenuButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraEffectButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButtonDefaults
```

## 基本用法

```kotlin
var selected by remember { mutableStateOf(false) }

CameraMenuButton(
    onClick = { selected = !selected },
    icon = bokehIcon,
    contentDescription = "虚化",
    selected = selected,
)
```

圆盘为 54dp，内含 24dp 图标（相机把 20dp 矢量图放大 1.2 倍）。未选中时为 `#1AFFFFFF` 填充、白色图标；选中时为从上到下的哈苏渐变 `#FF6B00` → `#FF932D`、黑色图标。与相机一致，状态切换没有动画。

### 边缘光

与相机一致，圆盘内侧带一圈光边：未选中时为三层白色叠加光，选中时为暖色 `#FFD39E` 叠加光。设置 `edgeLight = false` 可关闭。它需要渲染特效支持（Android 12+ 及所有 Skia 平台），其他情况下会跳过。

### 底部菜单项

传入 `label` 后按钮成为底部菜单项：标签位于圆盘下方 4dp，白色 10dp 中等字重，放不下时滚动显示。

```kotlin
CameraMenuButton(
    onClick = { mode = Mode.Bokeh },
    icon = bokehIcon,
    contentDescription = "虚化",
    selected = mode == Mode.Bokeh,
    label = "虚化",
)
```

### 纯色强调与彩色描边

非哈苏风格时，相机用纯色强调色填充选中的圆盘。把渐变颜色设为未指定即可得到：

```kotlin
CameraMenuButton(
    onClick = {},
    icon = icon,
    contentDescription = null,
    selected = true,
    colors = CameraMenuButtonDefaults.cameraMenuButtonColors(
        selectedGradientTopColor = Color.Unspecified,
        selectedGradientBottomColor = Color.Unspecified,
    ),
)
```

`colorfulBorder = true` 会加上相机在 AI 预设入口上绘制的 1dp 扫描渐变彩环（蓝、紫、红、黄、绿）。

### 效果入口按钮

```kotlin
CameraEffectButton(
    onClick = { filterOn = !filterOn },
    icon = filterIcon,
    contentDescription = "滤镜",
    visible = showEntries,
    selected = filterOn,
    materialBlur = provider, // 录制取景画面，而不是按钮本身
)
```

32dp 圆盘，内含 24dp 图标。传入 `materialBlur` 时背景为相机材质模糊（80dp），否则为 50% 黑色填充。`selected` 时图标在 320ms 内以 (0.3, 0, 0.1, 1) 渐变为强调色 `#FB822A`。改变 `visible` 时，按钮在 200ms 内以 (0.3, 0, 0.1, 1) 淡入并从 80% 放大；隐藏时在 200ms 内以 (0.33, 0, 0.67, 1) 淡出并缩回 80%；隐藏期间仍保留布局位置。

## 属性

### CameraMenuButton 属性

| 属性名             | 类型                   | 说明                         | 默认值                                         | 是否必须 |
| ------------------ | ---------------------- | ---------------------------- | ---------------------------------------------- | -------- |
| onClick            | () -> Unit             | 点击按钮时调用               | -                                              | 是   |
| icon               | ImageVector            | 图标，白色（选中时为黑色）   | -                                              | 是   |
| contentDescription | String?                | 无障碍描述                   | -                                              | 是   |
| modifier           | Modifier               | 应用于按钮的修饰符           | Modifier                                       | 否   |
| selected           | Boolean                | 是否选中                     | false                                          | 否   |
| enabled            | Boolean                | 是否响应点击                 | true                                           | 否   |
| label              | String?                | 圆盘下方的可选标签           | null                                           | 否   |
| colorfulBorder     | Boolean                | 是否绘制扫描渐变彩环         | false                                          | 否   |
| edgeLight          | Boolean                | 是否绘制相机边缘光           | true                                           | 否   |
| size               | Dp                     | 圆盘尺寸                     | CameraMenuButtonDefaults.Size                  | 否   |
| colors             | CameraMenuButtonColors | 按钮颜色                     | CameraMenuButtonDefaults.cameraMenuButtonColors() | 否 |

### CameraEffectButton 属性

| 属性名             | 类型                     | 说明                                 | 默认值                                             | 是否必须 |
| ------------------ | ------------------------ | ------------------------------------ | -------------------------------------------------- | -------- |
| onClick            | () -> Unit               | 点击按钮时调用                       | -                                                  | 是   |
| icon               | ImageVector              | 图标                                 | -                                                  | 是   |
| contentDescription | String?                  | 无障碍描述                           | -                                                  | 是   |
| modifier           | Modifier                 | 应用于按钮的修饰符                   | Modifier                                           | 否   |
| visible            | Boolean                  | 是否显示；变化时带动画               | true                                               | 否   |
| selected           | Boolean                  | 效果是否开启（强调色图标）           | false                                              | 否   |
| enabled            | Boolean                  | 是否响应点击                         | true                                               | 否   |
| materialBlur       | MaterialBlurProvider?    | 在圆盘后绘制材质模糊；为 null 时保留填充 | null                                           | 否   |
| colors             | CameraEffectButtonColors | 按钮颜色                             | CameraEffectButtonDefaults.cameraEffectButtonColors() | 否 |

## 默认值对象

### CameraMenuButtonDefaults

| 属性名              | 类型  | 说明                                 | 值      |
| ------------------- | ----- | ------------------------------------ | ------- |
| Size                | Dp    | 圆盘尺寸                             | 54.dp   |
| CornerRadius        | Dp    | 轮廓圆角（54dp 时为圆形）            | 30.dp   |
| IconSize            | Dp    | 图标尺寸                             | 24.dp   |
| GradientHeight      | Dp    | 选中渐变覆盖的高度                   | 54.dp   |
| ColorfulBorderWidth | Dp    | 彩环宽度                             | 1.dp    |
| LabelTextSize       | Dp    | 标签字号（dp，不随字体缩放）         | 10.dp   |
| LabelSpacing        | Dp    | 圆盘与标签的间距                     | 4.dp    |
| DisabledAlpha       | Float | 禁用时的透明度                       | 0.3f    |

| 函数                     | 返回类型               | 说明                       |
| ------------------------ | ---------------------- | -------------------------- |
| cameraMenuButtonColors() | CameraMenuButtonColors | 固定的相机深底浅色配色     |

### CameraEffectButtonDefaults

| 属性名      | 类型  | 说明                         | 值     |
| ----------- | ----- | ---------------------------- | ------ |
| Size        | Dp    | 按钮尺寸                     | 32.dp  |
| IconSize    | Dp    | 图标尺寸                     | 24.dp  |
| BlurRadius  | Dp    | 材质模糊半径                 | 80.dp  |
| HiddenScale | Float | 显示起始与隐藏结束时的缩放   | 0.8f   |

| 函数                       | 返回类型                 | 说明                       |
| -------------------------- | ------------------------ | -------------------------- |
| cameraEffectButtonColors() | CameraEffectButtonColors | 固定的相机深底浅色配色     |

## 颜色类

### CameraMenuButtonColors

| 属性名                      | 类型  | 说明                         | 默认值    |
| --------------------------- | ----- | ---------------------------- | --------- |
| containerColor              | Color | 未选中时的圆盘               | #1AFFFFFF |
| selectedContainerColor      | Color | 未提供渐变时的选中圆盘       | #FB822A   |
| selectedGradientTopColor    | Color | 选中渐变的顶部颜色           | #FF6B00   |
| selectedGradientBottomColor | Color | 选中渐变的底部颜色           | #FF932D   |
| iconColor                   | Color | 未选中时的图标               | White     |
| selectedIconColor           | Color | 选中时的图标                 | Black     |
| labelColor                  | Color | 标签                         | White     |

### CameraEffectButtonColors

| 属性名            | 类型  | 说明                     | 默认值             |
| ----------------- | ----- | ------------------------ | ------------------ |
| containerColor    | Color | 无材质模糊时的圆盘       | #7F000000          |
| blurMixColor      | Color | 材质模糊混合色           | 15% 灰，60% 透明度 |
| blurBlendColor    | Color | 材质模糊辉光叠加色       | 65% 灰，40% 透明度 |
| iconColor         | Color | 未选中时的图标           | White              |
| selectedIconColor | Color | 选中时的图标             | #FB822A            |
