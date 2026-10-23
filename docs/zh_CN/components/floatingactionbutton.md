# FloatingActionButton

`FloatingActionButton` 是 COUI 中的悬浮按钮组件，通常用于展示页面中最重要或最常用的操作。它通常悬浮在界面上方，具有突出的视觉效果，便于用户快速访问。

此组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingActionButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
```

## 基本用法

FloatingActionButton 组件可以用于触发重要的操作：

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ }
) {
    Icon(
        imageVector = COUIIcons.Add,
        contentDescription = "添加"
    )
}
```

## 组件状态

### 禁用状态

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ },
    enabled = false
) {
    Icon(
        imageVector = COUIIcons.Add,
        contentDescription = "添加"
    )
}
```

## 属性

### FloatingActionButton 属性

| 属性名                 | 类型                      | 说明                                         | 默认值                                                    | 是否必须 |
| ---------------------- | ------------------------- | -------------------------------------------- | --------------------------------------------------------- | -------- |
| onClick                | () -> Unit                | 点击按钮时触发的回调                         | -                                                         | 是       |
| modifier               | Modifier                  | 应用于按钮的修饰符                           | Modifier                                                  | 否       |
| enabled                | Boolean                   | 按钮是否启用                                 | true                                                      | 否       |
| material               | Boolean                   | 是否绘制 COUI 17 材质(边缘光、背景模糊)     | true                                                      | 否       |
| hapticFeedbackEnabled  | Boolean                   | 按下时是否播放按压震动(OPLUS 302)          | true                                                      | 否       |
| shape                  | Shape                     | 按钮的形状                                   | CircleShape                                               | 否       |
| containerColor         | Color                     | 按钮的背景颜色                               | COUITheme.colorScheme.primary                             | 否       |
| disabledContainerColor | Color                     | 禁用时按钮的背景颜色                         | FloatingActionButtonDefaults.disabledContainerColor()     | 否       |
| contentColor           | Color                     | 提供给内容的 `LocalContentColor`             | COUITheme.colorScheme.onPrimary                           | 否       |
| shadowElevation        | Dp                        | OPlus 阴影高度,0.dp 时不绘制阴影            | FloatingActionButtonDefaults.ShadowElevation              | 否       |
| shadowColor            | Color                     | 阴影光晕的颜色                               | containerColor 取 FloatingActionButtonDefaults.ShadowAlpha | 否      |
| minWidth               | Dp                        | 按钮的最小宽度                               | FloatingActionButtonDefaults.MinWidth                     | 否       |
| minHeight              | Dp                        | 按钮的最小高度                               | FloatingActionButtonDefaults.MinHeight                    | 否       |
| interactionSource      | MutableInteractionSource? | 按钮的交互源                                 | null                                                      | 否       |
| content                | @Composable () -> Unit    | 按钮内容区域的可组合函数                     | -                                                         | 是       |

### FloatingActionButtonDefaults 对象

FloatingActionButtonDefaults 对象提供了 FloatingActionButton 组件的默认值。

#### 常量

| 常量名            | 类型  | 说明                                               | 默认值     |
| ----------------- | ----- | -------------------------------------------------- | ---------- |
| MinWidth          | Dp    | 按钮最小宽度                                       | 56.dp      |
| MinHeight         | Dp    | 按钮最小高度                                       | 56.dp      |
| ShadowElevation   | Dp    | OPlus 阴影高度(coui_float_btn_shadow_elevation)  | 60.dp      |
| ShadowAlpha       | Float | 阴影颜色的透明度(coui_shadow_color_float_btn)    | 86f / 255f |
| ShadowLightY      | Dp    | 阴影光源的 y 坐标,位于窗口顶部之上               | -800.dp    |
| ShadowLightZ      | Dp    | 阴影光源的高度                                     | 10000.dp   |
| ShadowLightRadius | Dp    | 阴影光源的半径                                     | 2000.dp    |
| PressedScale      | Float | 已弃用,不再读取                                   | 0.92f      |

#### 函数

| 函数名                    | 说明                                                            |
| ------------------------- | --------------------------------------------------------------- |
| disabledContainerColor()  | `couiColorDisable` 叠加在禁用底色上:浅色 #B2D9FF,深色 #3E6083 |
| shadowPenumbra(elevation) | 给定高度下点光源阴影的模糊半径(60dp 时约 12dp)                |

## 行为

- **按压**:COUI 17 触摸动效作用于整个按钮,包括按尺寸计算的按压缩放(56dp 约为 0.91)和欠阻尼回弹、橡皮筋拖拽跟随以及拖拽形变。按下时播放 OPLUS 302 震动。
- **阴影**:COUI 请求的 OPlus 光源阴影,是按钮自身颜色、透明度 86/255 的柔和光晕。模糊半径等于 10000dp 高处、半径 2000dp 的光源形成的半影(约 12dp)。阴影向下偏移 `elevation × (centerY + 800dp) / (10000dp − elevation)`,按钮在窗口中越靠下,阴影偏移越大。
- **材质**:`material = true`(ColorOS 17 默认)时,填充之上绘制 `TYPE_FRAMEWORK_CIRCLE_2` 边缘光和内阴影,并关闭按压蒙层。若存在 `LocalMaterialBackdrop` 提供者,填充会被 `TYPE_FRAMEWORK_FAB` 背景模糊取代:模糊 10px,叠加透明度 229(浅色)/ 230(深色)的填充色,再叠加白色 OVERLAY。`material = false` 时,不透明填充在按下时显示 `couiColorPress` 蒙层。边缘光需要运行时着色器(Android 13+ 及所有 Skia 平台)。
- **禁用**:填充变为 `disabledContainerColor`,忽略触摸、震动和触摸动效,材质切换到禁用预设。

## 进阶用法

### 自定义颜色

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ },
    containerColor = Color.Red
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites",
        tint = Color.White
    )
}
```

### 扩展的悬浮按钮

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ },
    shape = RoundedCornerShape(16.dp),
    minWidth = 120.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = COUIIcons.Add,
            contentDescription = "添加",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("添加", color = Color.White)
    }
}
```

### 绑定到脚手架的悬浮按钮

```kotlin
Scaffold(
    floatingActionButton = {
        FloatingActionButton(
            onClick = { /* 处理点击事件 */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "添加"
            )
        }
    },
    floatingActionButtonPosition = FabPosition.End
) { paddingValues ->
    // 内容区域需要考虑 padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // ...
    }
}
```

### 带动画效果的悬浮按钮

```kotlin
var expanded by remember { mutableStateOf(false) }
val animatedSize by animateDpAsState(
    targetValue = if (expanded) 65.dp else 60.dp,
    label = "FAB 尺寸动画",
)

FloatingActionButton(
    onClick = { expanded = !expanded },
    minWidth = animatedSize,
    minHeight = animatedSize
) {
    Icon(
        imageVector = if (expanded) COUIIcons.Remove else COUIIcons.Add,
        contentDescription = if (expanded) "移除" else "添加",
        tint = Color.White
    )
}
```
