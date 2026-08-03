# Button

`Button` 是 COUI 中的基础交互组件，用于触发操作或事件。提供了多种风格选择，包括主要按钮、次要按钮和文本按钮。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
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

### 无边框文本按钮（Borderless Text Button）

对应 COUI 的 `Widget.COUI.Button.Large.Borderless` / `Translate`：无填充、主题色文字。

```kotlin
TextButton(
    text = "无边框按钮",
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.textButtonColorsBorderless()
)
```

### 小尺寸档（Small Size Tier）

将 `ButtonDefaults` 中的 `Small` 系列常量与 14sp 文字样式搭配使用，即可得到 COUI 小尺寸档（`Widget.COUI.Button.Small`）：

```kotlin
TextButton(
    text = "小按钮",
    onClick = { /* 处理点击事件 */ },
    cornerRadius = ButtonDefaults.CornerRadiusSmall,
    minWidth = ButtonDefaults.MinWidthSmall,
    minHeight = ButtonDefaults.MinHeightSmall,
    insideMargin = ButtonDefaults.InsideMarginSmall,
    textStyle = COUITheme.textStyles.button.copy(fontSize = 14.sp)
)
```

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
| cornerRadius      | Dp                              | 按钮圆角半径             | ButtonDefaults.CornerRadius   | 否       |
| minWidth          | Dp                              | 按钮最小宽度             | ButtonDefaults.MinWidth       | 否       |
| minHeight         | Dp                              | 按钮最小高度             | ButtonDefaults.MinHeight      | 否       |
| colors            | ButtonColors                    | 按钮颜色配置             | ButtonDefaults.buttonColors() | 否       |
| insideMargin      | PaddingValues                   | 按钮内部边距             | ButtonDefaults.InsideMargin   | 否       |
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
| cornerRadius      | Dp                        | 按钮圆角半径         | ButtonDefaults.CornerRadius       | 否       |
| minWidth          | Dp                        | 按钮最小宽度         | ButtonDefaults.MinWidth           | 否       |
| minHeight         | Dp                        | 按钮最小高度         | ButtonDefaults.MinHeight          | 否       |
| insideMargin      | PaddingValues             | 按钮内部边距         | ButtonDefaults.InsideMargin       | 否       |
| textStyle         | TextStyle                 | 标签文字样式（配合 `Small` 系列常量与 14sp 样式可得到 COUI 小尺寸档） | COUITheme.textStyles.button | 否       |
| interactionSource | MutableInteractionSource? | 按钮的交互源         | null                              | 否       |
| indication        | Indication?               | 点击交互的反馈效果；默认为 `null`，因为 COUI 按压反馈（缩放 + 按压着色）已内置于按钮 | null | 否       |

### ButtonDefaults 对象

ButtonDefaults 对象提供了按钮组件的默认值和颜色配置。

#### 常量

| 常量名       | 类型          | 说明           | 默认值               |
| ------------ | ------------- | -------------- | -------------------- |
| PressedScale | Float         | 按下时按钮收缩到的最小比例（48 x 48dp 以内的表面） | 0.92f |
| PressedBrightness | Float    | 旧版 COUIButton 的 `brightness` 属性值；仅为源码兼容保留，已不再使用 | 0.8f |
| MinWidth     | Dp            | 按钮的最小宽度 | 58.dp                |
| MinHeight    | Dp            | 按钮的最小高度 | 44.dp                |
| CornerRadius | Dp            | 按钮的圆角半径 | 22.dp                |
| InsideMargin | PaddingValues | 按钮的内部边距 | PaddingValues(horizontal = 12.dp, vertical = 0.dp) |
| MinWidthSmall | Dp           | 小尺寸档的最小宽度（COUI Widget.COUI.Button.Small） | 52.dp |
| MinHeightSmall | Dp          | 小尺寸档的最小高度 | 28.dp |
| CornerRadiusSmall | Dp       | 小尺寸档的圆角半径（随高度的胶囊） | 14.dp |
| InsideMarginSmall | PaddingValues | 小尺寸档的内部边距 | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |

#### 方法

| 方法名                    | 类型             | 说明                       |
| ------------------------- | ---------------- | -------------------------- |
| buttonColors()            | ButtonColors     | 创建次要按钮的颜色配置     |
| buttonColorsPrimary()     | ButtonColors     | 创建主要按钮的颜色配置     |
| textButtonColors()        | TextButtonColors | 创建次要文本按钮的颜色配置 |
| textButtonColorsPrimary() | TextButtonColors | 创建主要文本按钮的颜色配置 |
| textButtonColorsBorderless() | TextButtonColors | 创建无边框 / 文字按钮的颜色配置(透明填充、主题色文字) |

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
