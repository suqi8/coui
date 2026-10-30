# SimpleLock

`SimpleLock` 复刻 ColorOS 的 PIN 圆点栏（`COUISimpleLock`）：四个或六个 10dp 圆点，随输入逐个填充。删除时最后一个圆点淡出，清空时全部淡出，失败时整行左右抖动，已填充的圆点依次掉落。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=simpleLock" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockDefaults
import io.github.suqi8.coui.kmp.basic.SimpleLockType
import io.github.suqi8.coui.kmp.basic.rememberSimpleLockState
```

## 基本用法

```kotlin
var pin by remember { mutableStateOf("") }

SimpleLock(filledCount = pin.length)
```

## 失败动画

```kotlin
val state = rememberSimpleLockState()

SimpleLock(filledCount = pin.length, type = SimpleLockType.Six, state = state)

scope.launch {
    state.playFailure()
    pin = ""
}
```

## 壁纸之上

```kotlin
SimpleLock(
    filledCount = pin.length,
    colors = SimpleLockDefaults.transparentColors(),
    glowEffect = true,
)
```

## 属性

### SimpleLock

| 属性                  | 类型             | 说明                       | 默认值                      | 必须 |
| --------------------- | ---------------- | -------------------------- | --------------------------- | ---- |
| filledCount           | Int              | 已输入的位数               | -                           | 是   |
| modifier              | Modifier         | 应用于圆点栏的修饰符       | Modifier                    | 否   |
| type                  | SimpleLockType   | `Four` 或 `Six` 个圆点     | Four                        | 否   |
| state                 | SimpleLockState  | 动画、`playFailure()`      | rememberSimpleLockState()   | 否   |
| glowEffect            | Boolean          | 新圆点带光晕弹入           | false                       | 否   |
| hapticFeedbackEnabled | Boolean          | 失败时拒绝振动             | true                        | 否   |
| colors                | SimpleLockColors | 已填充与空圆点颜色         | SimpleLockDefaults.colors() | 否   |
| contentDescription    | String?          | 无障碍描述                 | null                        | 否   |

### SimpleLockDefaults

| 常量       | 类型 | 默认值 | COUI 来源                          |
| ---------- | ---- | ------ | ---------------------------------- |
| DotSize    | Dp   | 10.dp  | coui_simple_lock_*_rectangle_icon  |
| DotSpacing | Dp   | 10.dp  | couiRectanglePadding               |
| GlowOffset | Dp   | 25.dp  | coui_glow_ball_offset              |

| 方法                | 说明                                                    |
| ------------------- | ------------------------------------------------------- |
| colors()            | 已填充 #000000 / #E6FFFFFF，空 #19000000 / #66FFFFFF    |
| transparentColors() | 已填充 #FFFFFF，空 #26FFFFFF                            |

### SimpleLockColors

| 属性          | 类型  | 说明       |
| ------------- | ----- | ---------- |
| filledColor   | Color | 已输入圆点 |
| outlinedColor | Color | 空圆点     |

## 行为

- 新输入的数字立即填充圆点；开启 `glowEffect` 时以弹簧弹入（response 0.35、bounce 0.5，最大 1.2 倍），同时光晕闪现后淡去。
- 删除一位时该圆点在 230ms 内淡出（`COUIEaseInterpolator`）；一次减少多位时全部淡出。
- `playFailure()` 持续 800ms：整行按 30、-28、14、-8、4、-3、0px 抖动，已填充的圆点依次下落 250px，并在前 150px 内淡出。结束后圆点为空，请把 `filledCount` 重置为 0。
- 圆点栏高度为 `10dp + 150px`，为下落留出空间。
