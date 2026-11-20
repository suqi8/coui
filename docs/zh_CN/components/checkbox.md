# Checkbox

`Checkbox` 是 COUI 中的基础选择组件，支持三种状态：选中、未选中和半选中。它提供了具有动画效果的交互式选择控件，适用于多选场景和配置项的启用与禁用。

<div style="position: relative; height: 100px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=checkbox" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Checkbox
import androidx.compose.ui.state.ToggleableState
```

## 基本用法

Checkbox 组件可以用于创建可选择的选项：

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
)
```

## 组件状态

### 禁用状态

```kotlin
Checkbox(
    state = ToggleableState.Off,
    onClick = null,
    enabled = false
)
```

### 半选中状态

半选中状态通常用于父复选框中，表示子复选框仅部分被选中。处于半选中时，显示一条横线而非对勾。

```kotlin
var parentState by remember { mutableStateOf(ToggleableState.Indeterminate) }

// 循环切换：Off → Indeterminate → On → Off
Checkbox(
    state = parentState,
    onClick = {
        parentState = when (parentState) {
            ToggleableState.Off -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Off
        }
    }
)
```

## 属性

### Checkbox 属性

| 属性名    | 类型              | 说明                                              | 默认值                            | 是否必须 |
| --------- | ----------------- | ------------------------------------------------- | --------------------------------- | -------- |
| state     | ToggleableState   | 复选框当前状态（On、Off 或 Indeterminate）        | -                                 | 是       |
| onClick   | (() -> Unit)?     | 点击时的回调；为 `null` 时组件不可交互            | -                                 | 是       |
| modifier  | Modifier          | 应用于复选框的修饰符                              | Modifier                          | 否       |
| colors    | CheckboxColors    | 复选框的颜色配置                                  | CheckboxDefaults.checkboxColors() | 否       |
| enabled   | Boolean           | 复选框是否可交互                                  | true                              | 否       |

### CheckboxDefaults 对象

CheckboxDefaults 对象提供了 Checkbox 组件的默认颜色配置。

#### 方法

| 方法名                | 类型           | 说明                                                              |
| --------------------- | -------------- | ----------------------------------------------------------------- |
| checkboxColors()      | CheckboxColors | 创建复选框的默认颜色配置                                          |
| shapeCheckboxColors() | CheckboxColors | 叠在图片上使用的 COUI Shape 复选框配色（`couiShapeCheckBoxStyle`） |

### CheckboxColors 类

| 属性名                           | 类型  | 说明                         |
| -------------------------------- | ----- | ---------------------------- |
| checkedForegroundColor           | Color | 选中状态时前景色（对勾颜色）         |
| disabledCheckedForegroundColor   | Color | 禁用且选中状态时前景色               |
| checkedBackgroundColor           | Color | 选中状态时背景色                     |
| uncheckedBackgroundColor         | Color | 未选中状态时外框颜色                 |
| disabledCheckedBackgroundColor   | Color | 禁用且选中状态时背景色               |
| disabledUncheckedBackgroundColor | Color | 禁用且未选中状态时外框颜色           |
| uncheckedFillColor               | Color | 未选中状态时外框内的填充（默认透明） |
| disabledUncheckedFillColor       | Color | 禁用且未选中状态时外框内的填充       |

> 半选中状态与选中状态共用同一套颜色。

默认颜色对齐 COUI 17（`coui_btn_check_*.xml`）：未选中外框为 `labelTertiary`（#42000000 / #4DFFFFFF），禁用时为 `quarternaryNeutral`（#14000000 / #26FFFFFF）；选中填充为 `primary`，禁用时为 `disable`；对勾为白色，禁用时为 `disabledOnPrimaryButton`（#8AFFFFFF / #29FFFFFF）。

### 按压反馈

与 `COUICheckBox` 一致，按压时方框不缩小。可交互的复选框（`onClick != null`）会在 24dp 方框背后绘制直径 40dp 的圆形 `press` 高亮（#1F000000 / #33FFFFFF），节奏与平台水波纹一致；同一圆内还有 COUI `COUIMaskRippleDrawable` 的 `hover` 悬停层与键盘焦点（`focus`）层，以 response 0.3 弹簧淡入淡出。若外层行负责点击（如 `CheckboxPreference`），请传入 `onClick = null`，只由行显示按压态。与 `COUICheckBox` 一致，独立复选框不播放振动。

状态切换重放 COUI 的 AVD：填充淡入淡出，对勾逐笔绘出或擦除，半选横线从左端伸出或缩回（伸出时是缓动裁剪与线性路径变形的交集，几乎匀速），方框、对勾与横线各自按自己的缓动做 1 -> 1.08 -> 1 的弹跳（半选与选中之间对勾不缩放）。只有启用时状态切换才播放 COUI 动画；禁用时直接跳到新状态，半选与选中之间除外（COUI 禁用时也为其播放动画）。静止的半选状态按 COUI 静态资源绘制：方框比其他状态低 0.666dp，横线 1.6dp 粗（禁用时 1.5dp）。

## 进阶用法

### Shape 复选框

叠在图片或彩色背景上时，使用 COUI Shape 复选框（`Widget.COUI.CompoundButton.COUICheckBox.Shape`）：未选中为 40% 黑色填充加 40% 白色外框（禁用时为 8% 黑色填充加 30% 白色外框），深浅色主题相同；选中与半选状态不变。

```kotlin
Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.shapeCheckboxColors()
)
```

### 自定义颜色

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.checkboxColors(
        checkedBackgroundColor = Color.Red,
        checkedForegroundColor = Color.White
    )
)
```

### 父子复选框（半选中模式）

```kotlin
val childStates = remember { mutableStateListOf(false, true, false) }
val parentState by remember {
    derivedStateOf {
        when {
            childStates.all { it } -> ToggleableState.On
            childStates.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }
}

Column {
    Checkbox(
        state = parentState,
        onClick = {
            val newValue = parentState != ToggleableState.On
            childStates.indices.forEach { childStates[it] = newValue }
        }
    )
    childStates.forEachIndexed { index, checked ->
        Checkbox(
            state = ToggleableState(checked),
            onClick = { childStates[index] = !childStates[index] }
        )
    }
}
```

### 结合文本使用

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Checkbox(
        state = state,
        onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "接受用户协议与隐私政策")
}
```

### 在列表中使用

```kotlin
val options = listOf("选项 A", "选项 B", "选项 C")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                state = ToggleableState(checkedStates[index]),
                onClick = { checkedStates[index] = !checkedStates[index] }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = options[index])
        }
    }
}
```
