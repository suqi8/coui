# CustomSnackbar

`CustomSnackbar` 是外观完全由内容决定的提示条，对应 ColorOS 的 `COUICustomSnackBar`：一个空框架，只为自定义视图加上提示条行为（自动消失、下滑消失、按压反馈、淡出），自身不绘制任何背景。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=customSnackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CustomSnackbar
import io.github.suqi8.coui.kmp.basic.CustomSnackbarDefaults
```

## 基本用法

```kotlin
var visible by remember { mutableStateOf(true) }

Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    if (visible) {
        CustomSnackbar(
            onDismiss = { visible = false },
            touchSlidable = true,
            autoDismissMillis = 4000L,
        ) {
            Card(insideMargin = PaddingValues(16.dp), shadow = CardDefaults.shadow()) {
                Text("已保存到相册")
            }
        }
    }
}
```

## 属性

### CustomSnackbar 属性

| 属性名               | 类型                   | 说明                                                        | 默认值   | 是否必须 |
| -------------------- | ---------------------- | ----------------------------------------------------------- | -------- | -------- |
| onDismiss            | () -> Unit             | 提示条消失后调用（有淡出则在淡出后）；请在此移除它          | -        | 是       |
| modifier             | Modifier               | 应用于提示条的修饰符                                        | Modifier | 否       |
| touchSlidable        | Boolean                | 是否可向下拖动使其消失（`setTouchSlidable`）                | false    | 否       |
| pressFeedback        | Boolean                | 按压时是否缩小（`setPressFeedBack`）                        | false    | 否       |
| dismissWithAnimation | Boolean                | 消失时是否先淡出（`setDismissWithAnim`）                    | true     | 否       |
| autoDismissMillis    | Long?                  | 最后一次触摸后多久自动消失；null 表示不自动消失             | null     | 否       |
| content              | @Composable () -> Unit | 自定义内容，包括其背景                                      | -        | 是       |

另有一个以 `SnackbarData` 代替 `onDismiss` 与 `autoDismissMillis` 的重载，用于 `SnackbarHost` 内：下滑消失会 dismiss 该数据，宿主仍保留自己的超时与动画。

### CustomSnackbarDefaults 对象

| 名称                  | 类型   | 说明                                                  | 值                                 |
| --------------------- | ------ | ----------------------------------------------------- | ---------------------------------- |
| DismissDurationMillis | Int    | 淡出时长（`COUICustomSnackAnimUtil`）                 | 180                                |
| DismissEasing         | Easing | 淡出曲线（`PathInterpolator(0.1, 0, 0.1, 1)`）        | CubicBezierEasing(0.1f, 0f, 0.1f, 1f) |

## 行为

- **自动消失**：每个触摸事件都会重新开始倒计时，因此触摸期间提示条保持显示，在最后一次触摸 `autoDismissMillis` 后消失。
- **下滑**：提示条跟随向下的拖动，且不会移到原位置之上；拖下后松手即消失。COUI 从移动的第一个像素就开始跟随；这里超过触摸阈值才开始，因此点击内部按钮仍然有效，更短的拖动会弹回原位。
- **按压反馈**：使用 COUI 17 触控缩放绘制。
- 没有内置出现动画（COUI 交给 `setShowAnimSet`）；需要时请用 `AnimatedVisibility` 包裹。
- 该类的 ColorOS 构建方法在发布的应用中已被裁剪，上述默认值由本库决定：COUI 的各字段初始均为关闭。

## 进阶用法

### 在 SnackbarHost 中使用

```kotlin
SnackbarHost(snackbarHostState, canSwipeToDismiss = false) { data ->
    CustomSnackbar(data, touchSlidable = true, pressFeedback = true) {
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            insideMargin = PaddingValues(16.dp),
            shadow = CardDefaults.shadow(),
        ) {
            Text(data.visuals.message)
        }
    }
}
```
