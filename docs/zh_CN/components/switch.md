# Switch

`Switch` 是 COUI 中的基础切换组件，用于在两种状态之间进行切换。它提供了具有动画效果的交互式开关控件，适用于设置项的启用与禁用场景。

<div style="position: relative; height: 100px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=switch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Switch
```

## 基本用法

Switch 组件可以用于在两种状态间切换：

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## 交互

`Switch` 对齐 COUI 17 的 `COUISwitch`：

- 轨道 44 x 24 dp，滑块直径 18 dp，行程 20 dp。
- 切换时滑块按弹簧运动（response 0.4，bounce 0.3），并随位置拉伸，约在行程三分之一处达到 1.3 倍，轨道颜色随之混合；运动途中再次切换会改变目标而不是从头开始。
- 滑块可拖动：超过触摸阈值后与手指一比一同步移动（COUISpringDragHelper 按手指原始位移移动滑块，限制在轨道内），松手后回到较近的一端，或甩向的一端。拖到另一端结束时会调用 `onCheckedChange`；若调用方保持原状态，滑块弹回。
- 按下与悬停时在轨道上叠加 `COUITheme.colorScheme.press` / `hover`。

## 组件状态

### 禁用状态

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = false
)
```

### 加载状态

当 `isLoading` 为 true 时，滑块缩小消失并被旋转的渐变弧取代，加载期间不响应点击：

```kotlin
var checked by remember { mutableStateOf(true) }
var isLoading by remember { mutableStateOf(true) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    isLoading = isLoading
)
```

## 属性

### Switch 属性

| 属性名          | 类型                 | 说明                     | 默认值                        | 是否必须 |
| --------------- | -------------------- | ------------------------ | ----------------------------- | -------- |
| checked         | Boolean              | 开关是否处于选中状态     | -                             | 是       |
| onCheckedChange | ((Boolean) -> Unit)? | 开关状态变化时的回调函数；为 `null` 时组件不可交互 | -           | 是       |
| modifier        | Modifier             | 应用于开关的修饰符       | Modifier                      | 否       |
| colors          | SwitchColors         | 开关的颜色配置           | SwitchDefaults.switchColors() | 否       |
| enabled         | Boolean              | 开关是否可交互           | true                          | 否       |
| isLoading       | Boolean              | 是否处于 COUI 加载态:滑块缩小消失并被旋转的渐变弧取代,期间不响应点击 | false                | 否       |

### SwitchDefaults 对象

SwitchDefaults 对象提供了 Switch 组件的尺寸和默认颜色配置。

#### 常量

| 常量名           | 类型   | 说明                     | 值      |
| ---------------- | ------ | ------------------------ | ------- |
| TrackWidth       | Dp     | 轨道宽度                 | 44.dp   |
| TrackHeight      | Dp     | 轨道高度                 | 24.dp   |
| ThumbSize        | Dp     | 滑块直径                 | 18.dp   |
| ThumbStartOffset | Dp     | 未选中时滑块的偏移       | 3.dp    |
| ThumbEndOffset   | Dp     | 选中时滑块的偏移         | 23.dp   |
| Travel           | Dp     | 滑块行程                 | 20.dp   |
| ThumbShadow      | Shadow | 启用时滑块下方的投影     | -       |

#### 方法

| 方法名         | 类型         | 说明                   |
| -------------- | ------------ | ---------------------- |
| switchColors() | SwitchColors | 创建开关的默认颜色配置 |

### SwitchColors 类

| 属性名                      | 类型  | 说明                         | 默认值 | 是否必须 |
| --------------------------- | ----- | ---------------------------- | ------ | -------- |
| checkedThumbColor           | Color | 选中状态时滑块的颜色         | `onPrimary`           | 是       |
| uncheckedThumbColor         | Color | 未选中状态时滑块的颜色       | `labelOnColor`         | 是       |
| disabledCheckedThumbColor   | Color | 禁用且选中状态时滑块的颜色   | `disabledSliderThumb` | 是       |
| disabledUncheckedThumbColor | Color | 禁用且未选中状态时滑块的颜色 | `disabledSliderThumb` | 是       |
| checkedTrackColor           | Color | 选中状态时轨道的颜色         | `primary`             | 是       |
| uncheckedTrackColor         | Color | 未选中状态时轨道的颜色       | `dragHandle`          | 是       |
| disabledCheckedTrackColor   | Color | 禁用且选中状态时轨道的颜色   | `disable`     | 是       |
| disabledUncheckedTrackColor | Color | 禁用且未选中状态时轨道的颜色 | `secondaryContainer`  | 是       |

表中默认值为 `switchColors()` 使用的 `COUITheme.colorScheme` 色值；启用动态取色时，两种启用态滑块均使用 `onPrimary`。

## 进阶用法

### 自定义颜色

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    colors = SwitchDefaults.switchColors(
        checkedTrackColor = Color.Red,
        checkedThumbColor = Color.White
    )
)
```

### 结合文本使用

```kotlin
var checked by remember { mutableStateOf(false) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Switch(
        checked = checked,
        onCheckedChange = { checked = it }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = if (checked) "已开启" else "已关闭")
}
```

### 在列表中使用

```kotlin
val options = listOf("飞行模式", "蓝牙", "位置服务")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index])
            Switch(
                checked = checkedStates[index],
                onCheckedChange = { checkedStates[index] = it }
            )
        }
    }
}
```

### 整行列表可点击

```kotlin
data class Option(val text: String, var isSelected: Boolean)
val options = remember {
    mutableStateListOf(
        Option("飞行模式", false),
        Option("蓝牙", true),
        Option("位置服务", false)
    )
}

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index].text)
            Switch(
                checked = options[index].isSelected,
                onCheckedChange = { 
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
            )
        }
    }
}
```