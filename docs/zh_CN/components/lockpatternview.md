# LockPatternView

`LockPatternView` 复刻 ColorOS 的九宫格图案解锁（`COUILockPatternView`）。手指划过圆点时绘制路径；每添加一个圆点，会播放放大到 7 倍并淡出的波纹，其发光内圆淡入，路径端点吸附到圆心。错误图案会变红，并在 1 秒内闪烁消失。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=lockPatternView" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.LockPatternView
import io.github.suqi8.coui.kmp.basic.LockPatternViewDefaults
import io.github.suqi8.coui.kmp.basic.LockPatternDisplayMode
import io.github.suqi8.coui.kmp.basic.rememberLockPatternState
```

## 基本用法

抬手时校验图案；错误时标记为错误并清除（清除会等错误闪烁结束后再执行）：

```kotlin
val state = rememberLockPatternState()

LockPatternView(
    state = state,
    onPatternDetected = { pattern ->
        if (patternToString(pattern) != "14789") {
            state.setDisplayMode(LockPatternDisplayMode.Wrong)
        }
        state.clearPattern()
    },
)
```

## 回放与入场

```kotlin
// 回放图案，每 700ms 一个圆点
state.setPattern(LockPatternDisplayMode.Animate, stringToPattern("14789"))

// COUI 入场：圆点上升 258dp 并淡入，逐个错开 16ms
scope.launch { state.animateEnter() }
```

## 属性

### LockPatternView

| 属性                  | 类型                              | 说明                                   | 默认值                              | 必须 |
| --------------------- | --------------------------------- | -------------------------------------- | ----------------------------------- | ---- |
| onPatternDetected     | (List\<LockPatternCell\>) -> Unit | 抬手时回调图案                         | -                                   | 是   |
| modifier              | Modifier                          | 应用于视图的修饰符（默认 312x280dp）   | Modifier                            | 否   |
| state                 | LockPatternState                  | 图案、显示模式与圆点动画               | rememberLockPatternState()          | 否   |
| enabled               | Boolean                           | 是否接受触摸                           | true                                | 否   |
| inStealthMode         | Boolean                           | 隐藏路径与选中圆点（错误状态仍显示）   | false                               | 否   |
| hapticFeedbackEnabled | Boolean                           | 每个圆点轻振，错误图案拒绝振动         | true                                | 否   |
| colors                | LockPatternViewColors             | 圆点、成功与错误颜色                   | LockPatternViewDefaults.colors()    | 否   |
| onPatternStart        | () -> Unit                        | 开始绘制                               | {}                                  | 否   |
| onPatternCellAdded    | (List\<LockPatternCell\>) -> Unit | 添加了一个圆点                         | {}                                  | 否   |
| onPatternCleared      | () -> Unit                        | 绘制中途被取消                         | {}                                  | 否   |

### LockPatternState

| 成员                         | 说明                                                   |
| ---------------------------- | ------------------------------------------------------ |
| pattern                      | 当前图案的圆点                                         |
| displayMode                  | `Correct`、`Animate` 或 `Wrong`                        |
| setDisplayMode(mode, haptic) | `Wrong` 播放 1 秒错误闪烁（2 个以上圆点时附带拒绝振动） |
| setPattern(mode, pattern)    | 替换图案                                               |
| clearPattern()               | 清除图案；错误闪烁进行中则在其结束后清除               |
| animateEnter()               | 挂起直至入场动画结束                                   |

`patternToString` / `stringToPattern` 在图案与 1 起始的圆点编号之间转换（`COUILockPatternUtils`）。

### LockPatternViewDefaults

| 常量              | 类型 | 默认值 | COUI 来源                                   |
| ----------------- | ---- | ------ | ------------------------------------------- |
| Width             | Dp   | 312.dp | coui_lock_pattern_view_width                |
| Height            | Dp   | 280.dp | coui_lock_pattern_view_height               |
| HorizontalPadding | Dp   | 21.dp  | coui_lock_pattern_view_horizontal_padding   |
| VerticalPadding   | Dp   | 5.dp   | coui_lock_pattern_view_vertical_padding     |
| DotSize           | Dp   | 10.dp  | lock_pattern_dot_size                       |
| PathWidth         | Dp   | 2.dp   | lock_pattern_dot_line_width                 |
| EnterTranslateY   | Dp   | 258.dp | color_lock_pattern_view_max_translate_y     |

| 方法     | 说明                                                                                  |
| -------- | ------------------------------------------------------------------------------------- |
| colors() | 圆点 #4D000000 / #66FFFFFF，成功 #000000 / #FFFFFF，错误 #EA3447，波纹峰值 0.1 / 0.2  |

### LockPatternViewColors

| 属性                | 类型  | 说明                             |
| ------------------- | ----- | -------------------------------- |
| dotColor            | Color | 静止圆点                         |
| successColor        | Color | 绘制中或正确时的路径与选中圆点   |
| errorColor          | Color | 错误图案的路径与选中圆点         |
| outerCircleMaxAlpha | Float | 添加圆点时波纹的峰值透明度       |

## 行为

- 圆点命中区域为格子中间 60%（`mHitFactor`）。直线跨过的中间圆点会被一并添加。
- 波纹：460ms 内缩放 1 -> 7（`COUIInEaseInterpolator`），透明度 0 -> 峰值 -> 0。内圆：230ms 淡入（`COUIEaseInterpolator`）。路径端点：100ms 吸附（fast-out-slow-in）。
- 错误闪烁：路径与选中圆点在 1 秒内按 1、0.35、1、0.15、0.5、0 变化；低于 0.1 后普通圆点重新出现。
- 与 COUI 一致，路径使用成功 / 错误颜色绘制，因此没有单独的路径颜色。
