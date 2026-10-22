# Button

`Button` 是 COUI 中的基础交互组件，用于触发操作或事件。提供了多种风格选择，包括主要按钮、次要按钮和文本按钮。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=button" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Button
```

## 基本用法

Button 组件可以用于触发操作或事件：

```kotlin
Button(
    onClick = { /* 处理点击事件 */ }
) {
    Text("按钮")
}
```

## 按钮类型

COUI 提供了多种类型的按钮，适用于不同的场景和重要程度：

### 主要按钮（Primary Button）

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColorsPrimary()
) {
    Text("主要按钮")
}
```

### 次要按钮（Secondary Button）

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColors()
) {
    Text("次要按钮")
}
```

### 文本按钮（Text Button）

```kotlin
TextButton(
    text = "文本按钮",
    onClick = { /* 处理点击事件 */ }
)
```

### 透明按钮（Transparent Button）

对应 COUI 的 `Widget.COUI.Button.Small.ButtonNew.Translate`：浅色与深色模式下都是半透明白色填充（`#33FFFFFF`）配白色文字，用于叠在图片或彩色背景上的按钮。

```kotlin
TextButton(
    text = "透明按钮",
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.textButtonColorsTransparent()
)
```

### 无边框文本按钮（Borderless Text Button）

无填充、主题色文字。

```kotlin
TextButton(
    text = "无边框按钮",
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.textButtonColorsBorderless()
)
```

### 小尺寸档（Small Size Tier）

将 `ButtonDefaults` 中的 `Small` 系列常量与 `ButtonDefaults.textStyleSmall()`（14sp 中粗）搭配使用，即可得到 COUI 小尺寸档（`Widget.COUI.Button.Small`）：

```kotlin
TextButton(
    text = "小按钮",
    onClick = { /* 处理点击事件 */ },
    cornerRadius = ButtonDefaults.CornerRadiusSmall,
    minWidth = ButtonDefaults.MinWidthSmall,
    minHeight = ButtonDefaults.MinHeightSmall,
    insideMargin = ButtonDefaults.InsideMarginSmall,
    textStyle = ButtonDefaults.textStyleSmall()
)
```

## 按压反馈

按压反馈遵循 COUI 17 `COUIButton`（`COUITouchMotionAnimator` + `COUIMaskEffectDrawable`）：

- **缩放目标**由实测 dp 尺寸推导（`PressCalc`）：面积通道 `0.85 + B(t) * 0.12`（`t` 为面积在 20² 到 150² 间的映射）；宽度超过高度两倍的按钮再叠加长宽比通道 `0.85 + B((ar - 2) / 13) * 0.15` 取平均（`B = cubic-bezier(0, 0, 0, 1)`）。296 x 44dp 按钮缩到约 0.966，132 x 44dp 约 0.920，52 x 28dp 约 0.886。
- **弹簧**：宽度超过高度两倍的按钮用 candy 组（按下 k 438.65 / ζ 1，抬手 k 157.91 / ζ 0.5）；紧凑与圆形按钮用 jelly 组（按下 k 631.65 / ζ 1，抬手 k 157.91 / ζ 0.4）。欠阻尼的抬手带来轻微回弹。
- **按压着色**：`couiColorPress`（浅色 `#1F000000` / 深色 `#33FFFFFF`），弹簧 k 438.65 / ζ 1，至少显现到 70% 才开始淡出。绘制材质层时关闭（`COUIButton.applyBlurEffect` 关闭蒙版的按压态）。
- **悬停着色**：`couiColorHover`（浅色 `#14000000` / 深色 `#26FFFFFF`），同一弹簧。
- **拖动形变**（`COUIRubberAnimatorUnit` + `COUIDeformAnimatorUnit`）：越过 5px 死区后按钮按橡皮筋曲线 `(1 - 1 / (0.05·|d| / 28dp + 1)) · 28dp` 跟手平移，并沿拖动方向拉伸、垂直方向收窄（面积守恒，`1 + band(|d|, 0.55, 150dp) / 150dp · (max - 1)`，小控件 `max` = 1.2，大或长的控件更小）。两者都用 response 0.15 / bounce 0.15 弹簧。手指移出按钮时按压缩放保持不变。
- **松手**：平移和拉伸以 response 0.42（面积超过 100dp² 时 0.45）/ bounce 0.65 弹回，并带入手指速度。被滚动父容器接管的手势按零速度松手处理。禁用按钮时所有形变弹回。

## 材质层

COUI 17 的所有 `ButtonNew` 样式都跟随系统材质开关（`useMaterialEffect = followSystem`），而 ColorOS 17 真机上这些开关是打开的，因此 `Button` 默认绘制材质层：描边光 + 内阴影（`COUIMaterialStrokeEffect` `CONTENT_CAPSULE_1/2/3`）与触摸聚光（`COUISpotLightEffect` `OPAQUE_MEDIUM_1/2`、`TRANSPARENT_MEDIUM_1`）。`ButtonMaterial.Auto` 按填充色选择预设（主色、透明按钮色，其余为次要；无边框按钮不绘制）。传入 `ButtonMaterial.None` 可关闭。二者需要运行时着色器（Android 13+ 及所有 Skia 平台），其他环境下跳过，此时保留按压着色。COUI 用来替换实色填充的框架背景模糊需要背景采样，见[背景模糊](#背景模糊)。焦散阴影不绘制：它由 Oplus `RenderNode` 原生渲染，而且只在应用主动开启时才出现。

```kotlin
TextButton(
    text = "材质按钮",
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.textButtonColorsPrimary(),
    material = ButtonMaterial.Primary
)
```

### 背景模糊

系统材质模糊开启时，COUI 17 按钮完全不画实色填充：`COUIButton.onDraw` 跳过填充，由 `setViewBlurEffect` 对按钮背后的内容做滤镜。各类型的参数如下：

- 主按钮：10px，填充色的 alpha 改为 229/230。
- 次要按钮：25px，叠 `#CCFFFFFF` / `#33FFFFFF`。
- 透明按钮：25px，`#3F333333` overlay + `#3FFFFFFF` luminosity。

coui-ui 自身无法采集背景，所以只有两个条件同时满足时 `Button` 才绘制这层模糊：通过 `LocalMaterialBackdrop` 提供了 `MaterialBackdropProvider`，且运行时着色器可用。这时模糊替代填充，画在触摸形变层内，会随按压一起缩放、形变，悬停着色叠在模糊之上。没有 provider 时按钮保留不透明填充，这正是 COUI 的非模糊回退（次要按钮 `#EBEBEB` / `#353535`）。传入 `materialBackdrop = false` 可强制保留填充。

```kotlin
val backdrop = rememberLayerBackdrop()
Box {
    Image(painter, null, Modifier.matchParentSize().layerBackdrop(backdrop))
    val provider = remember(backdrop) {
        MaterialBackdropProvider { spec, shape ->
            Modifier.couiViewBlur(
                backdrop,
                ViewBlurStyle(spec.blurRadiusPx, spec.layer1, spec.mode1, spec.layer2, spec.mode2),
                shape,
            )
        }
    }
    CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
        TextButton(text = "模糊按钮", onClick = {})
    }
}
```

- provider 的背景源不能录制到按钮本身。provider 要包在录制层之上的内容外层；把按钮包含在内的整页 `layerBackdrop` 不能用作背景源。
- 对话框、底部抽屉和弹出菜单会为其内容提供 `null`，所以面板上的按钮保留填充，不会去采样面板背后的页面。COUI 自己的对话框按钮走 `drawableMaterialEffect` 路径，同样从不模糊。
- 按压缩放也作用于采样到的背景，背景会随按钮最多缩小约 8%。在 10–25px 的模糊下几乎看不出来。
- 不绘制焦散阴影。COUI 只在调用 `setCausticShadow(6|7)` 时才开启它，默认是关闭的。

## 组件状态

### 禁用状态

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    enabled = false
) {
    Text("禁用按钮")
}
```

## 属性

### Button 属性

| 属性名            | 类型                            | 说明                     | 默认值                        | 是否必须 |
| ----------------- | ------------------------------- | ------------------------ | ----------------------------- | -------- |
| onClick           | () -> Unit                      | 点击按钮时触发的回调     | -                             | 是       |
| modifier          | Modifier                        | 应用于按钮的修饰符       | Modifier                      | 否       |
| enabled           | Boolean                         | 按钮是否可点击           | true                          | 否       |
| pressScaleEnabled | Boolean                         | 按压时按钮是否缩放（对应 COUIButton 的 `scaleEnable`）。填满容器单元格的按钮（如对话框按钮栏）应设为 `false`，此时按压反馈只有铺满整格的按压着色 | true | 否       |
| cornerRadius      | Dp                              | 按钮圆角半径；`Dp.Unspecified` 按实测高度的一半取圆角（COUI 胶囊，权重 1.0 软胶囊），显式值绘制权重 3.0 超椭圆 | ButtonDefaults.CornerRadius   | 否       |
| minWidth          | Dp                              | 按钮最小宽度             | ButtonDefaults.MinWidth       | 否       |
| minHeight         | Dp                              | 按钮最小高度             | ButtonDefaults.MinHeight      | 否       |
| colors            | ButtonColors                    | 按钮颜色配置             | ButtonDefaults.buttonColors() | 否       |
| insideMargin      | PaddingValues                   | 按钮内部边距             | ButtonDefaults.InsideMargin   | 否       |
| material          | ButtonMaterial                  | COUI 17 材质层（描边光、内阴影、触摸聚光）；绘制时关闭按压着色 | ButtonMaterial.Auto | 否       |
| materialBackdrop  | Boolean                         | 材质是否可用背景模糊替代填充；仅在有 `LocalMaterialBackdrop` provider 时绘制 | true | 否       |
| interactionSource | MutableInteractionSource?       | 按钮的交互源             | null                          | 否       |
| indication        | Indication?                     | 点击交互的反馈效果；默认为 `null`，因为 COUI 按压反馈（缩放 + 按压着色）已内置于按钮 | null | 否       |
| content           | @Composable RowScope.() -> Unit | 按钮内容区域的可组合函数 | -                             | 是       |

### TextButton 属性

| 属性名            | 类型                      | 说明                 | 默认值                            | 是否必须 |
| ----------------- | ------------------------- | -------------------- | --------------------------------- | -------- |
| text              | String                    | 按钮显示的文本       | -                                 | 是       |
| onClick           | () -> Unit                | 点击按钮时触发的回调 | -                                 | 是       |
| modifier          | Modifier                  | 应用于按钮的修饰符   | Modifier                          | 否       |
| enabled           | Boolean                   | 按钮是否可点击       | true                              | 否       |
| pressScaleEnabled | Boolean                   | 按压时按钮是否缩放（对应 COUIButton 的 `scaleEnable`）。填满容器单元格的按钮（如对话框按钮栏）应设为 `false` | true | 否       |
| colors            | TextButtonColors          | 文本按钮颜色配置     | ButtonDefaults.textButtonColors() | 否       |
| cornerRadius      | Dp                        | 按钮圆角半径；`Dp.Unspecified` 为随高度的胶囊 | ButtonDefaults.CornerRadius       | 否       |
| minWidth          | Dp                        | 按钮最小宽度         | ButtonDefaults.MinWidth           | 否       |
| minHeight         | Dp                        | 按钮最小高度         | ButtonDefaults.MinHeight          | 否       |
| insideMargin      | PaddingValues             | 按钮内部边距         | ButtonDefaults.InsideMargin       | 否       |
| textStyle         | TextStyle                 | 标签文字样式（配合 `Small` 系列常量与 `ButtonDefaults.textStyleSmall()` 可得到 COUI 小尺寸档） | COUITheme.textStyles.button | 否       |
| maxLines          | Int                       | 标签最大行数；COUIButton 为单行、末尾省略 | 1 | 否       |
| material          | ButtonMaterial            | COUI 17 材质层，见 `Button` | ButtonMaterial.Auto | 否       |
| materialBackdrop  | Boolean                   | 材质是否可用背景模糊替代填充，见 `Button` | true | 否       |
| interactionSource | MutableInteractionSource? | 按钮的交互源         | null                              | 否       |
| indication        | Indication?               | 点击交互的反馈效果；默认为 `null`，因为 COUI 按压反馈（缩放 + 按压着色）已内置于按钮 | null | 否       |

### ButtonDefaults 对象

ButtonDefaults 对象提供了按钮组件的默认值和颜色配置。

#### 常量

| 常量名       | 类型          | 说明           | 默认值               |
| ------------ | ------------- | -------------- | -------------------- |
| PressedScale | Float         | 旧版 COUI 16 按压终点缩放；仅为源码兼容保留，已不再使用（COUI 17 目标值由尺寸推导） | 0.92f |
| PressedBrightness | Float    | 旧版 COUIButton 的 `brightness` 属性值；仅为源码兼容保留，已不再使用 | 0.8f |
| MinWidth     | Dp            | 按钮的最小宽度（COUI `coui_btn_large_width_min`） | 132.dp |
| MinHeight    | Dp            | 按钮的最小高度 | 44.dp                |
| CornerRadius | Dp            | 按钮的圆角半径；未指定 = 按实测高度一半的胶囊 | Dp.Unspecified |
| InsideMargin | PaddingValues | 按钮的内部边距（COUI `Widget.COUI.Button.Large`） | PaddingValues(horizontal = 12.dp, vertical = 8.dp) |
| MinWidthSmall | Dp           | 小尺寸档的最小宽度（COUI Widget.COUI.Button.Small） | 52.dp |
| MinHeightSmall | Dp          | 小尺寸档的最小高度 | 28.dp |
| CornerRadiusSmall | Dp       | 小尺寸档的圆角半径（随高度的胶囊） | Dp.Unspecified |
| InsideMarginSmall | PaddingValues | 小尺寸档的内部边距 | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |

#### 方法

| 方法名                    | 类型             | 说明                       |
| ------------------------- | ---------------- | -------------------------- |
| buttonColors()            | ButtonColors     | 创建次要按钮的颜色配置     |
| buttonColorsPrimary()     | ButtonColors     | 创建主要按钮的颜色配置     |
| textButtonColors()        | TextButtonColors | 创建次要文本按钮的颜色配置 |
| textButtonColorsPrimary() | TextButtonColors | 创建主要文本按钮的颜色配置 |
| textStyleSmall()          | TextStyle        | 小尺寸档的标签样式（14sp 中粗） |
| buttonColorsTransparent() | ButtonColors     | 创建透明按钮的颜色配置（半透明白色填充、白色文字） |
| textButtonColorsTransparent() | TextButtonColors | 创建透明文本按钮的颜色配置 |
| textButtonColorsBorderless() | TextButtonColors | 创建无边框 / 文字按钮的颜色配置(透明填充、主题色文字) |

次级按钮颜色取自配色方案的 `secondaryButton`（浅色 `#EBEBEB` / 深色 `#353535`：COUI `couiColorContainer8` 合成在 `couiColorBackgroundElevated` 上）、`disabledSecondaryButton` 与 `disabledOnSecondaryButton`。

### ButtonMaterial

| 取值        | 说明 |
| ----------- | ---- |
| Auto        | 按填充色匹配预设：主色为 `Primary`，透明按钮色为 `Transparent`，完全透明填充不绘制，其余为 `Secondary`（默认） |
| None        | 不绘制材质层（`useMaterialEffect = forceDisable`） |
| Primary     | `CONTENT_CAPSULE_1` 描边 + `TYPE_OPAQUE_MEDIUM_1` 聚光 |
| Secondary   | `CONTENT_CAPSULE_2` 描边 + `TYPE_OPAQUE_MEDIUM_2` 聚光 |
| Transparent | `CONTENT_CAPSULE_3` 描边 + `TYPE_TRANSPARENT_MEDIUM_1` 聚光 |

## 进阶用法

### 带图标按钮

```kotlin
Button(
    onClick = { /* 处理点击事件 */ }
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("带图标按钮")
}
```

### 自定义样式按钮

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColors(
        color = Color.Red.copy(alpha = 0.7f)
    ),
    cornerRadius = 8.dp
) {
    Text("自定义按钮")
}
```

### 加载状态按钮

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // 模拟操作
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("提交")
}
```
