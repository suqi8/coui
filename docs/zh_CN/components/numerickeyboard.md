# NumericKeyboard

`NumericKeyboard` 复刻 ColorOS 的安全数字键盘（`COUINumericKeyboard`）：3 x 4 网格的 68dp 圆形数字键，左下与右下各一个侧边键。提供两种按压效果：设置页键盘的波纹效果，以及锁屏的玻璃按键——按下的按键回弹缩小，点亮自身边缘与周围按键。

<div style="position: relative; height: 460px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=numericKeyboard" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NumericKeyboard
import io.github.suqi8.coui.kmp.basic.NumericKeyboardDefaults
import io.github.suqi8.coui.kmp.basic.NumericKeyboardPressEffect
```

## 基本用法

```kotlin
var input by remember { mutableStateOf("") }

NumericKeyboard(
    onNumberClick = { input += it },
    rightKey = NumericKeyboardDefaults.deleteKey(), // 两个角默认都为空
    onRightKeyClick = { input = input.dropLast(1) },
)
```

## 侧边键

侧边键显示图标或粗体文字。首次组合之后再更改侧边键，会淡入或淡出：

```kotlin
NumericKeyboard(
    onNumberClick = { input += it },
    leftKey = if (input.isNotEmpty()) NumericKeyboardDefaults.finishKey() else null,
    rightKey = NumericKeyboardDefaults.deleteKey(),
    onLeftKeyClick = { submit(input) },
    onRightKeyClick = { input = input.dropLast(1) },
)
```

## 锁屏按键

```kotlin
NumericKeyboard(
    onNumberClick = { pin += it },
    pressEffect = NumericKeyboardPressEffect.Light,
    colors = NumericKeyboardDefaults.lockScreenColors(),
)
```

## 属性

### NumericKeyboard

| 属性                  | 类型                       | 说明                               | 默认值                               | 必须 |
| --------------------- | -------------------------- | ---------------------------------- | ------------------------------------ | ---- |
| onNumberClick         | (Int) -> Unit              | 松开按键时回调其数字               | -                                    | 是   |
| modifier              | Modifier                   | 修饰符（默认 248 x 326dp）         | Modifier                             | 否   |
| state                 | NumericKeyboardState       | 按键动画、`animateEnter()`         | rememberNumericKeyboardState()       | 否   |
| enabled               | Boolean                    | 按键是否接受触摸                   | true                                 | 否   |
| hapticFeedbackEnabled | Boolean                    | 按下时键盘振动                     | true                                 | 否   |
| leftKey               | NumericKeyboardSideKey?    | 左下键，null 为空格                | null                                 | 否   |
| rightKey              | NumericKeyboardSideKey?    | 右下键，null 为空格                | null                                 | 否   |
| pressEffect           | NumericKeyboardPressEffect | `Ripple` 或 `Light`                | Ripple                               | 否   |
| keySize               | Dp                         | 按键直径                           | 68.dp                                | 否   |
| colors                | NumericKeyboardColors      | 按键颜色                           | NumericKeyboardDefaults.colors()     | 否   |
| onLeftKeyClick        | () -> Unit                 | 松开左侧键                         | {}                                   | 否   |
| onRightKeyClick       | () -> Unit                 | 松开右侧键                         | {}                                   | 否   |

### NumericKeyboardSideKey

| 属性               | 类型         | 说明                                    | 默认值      |
| ------------------ | ------------ | --------------------------------------- | ----------- |
| icon               | ImageVector? | 图标，默认保留自身颜色，`tintIcon` 时着色 | null        |
| text               | String?      | 粗体文字，无图标时使用                  | null        |
| contentDescription | String?      | 无障碍描述                              | text        |
| textColor          | Color        | 文字颜色；未指定时使用 `wordTextColor`  | Unspecified |
| textSize           | Dp           | 文字大小                                | 16.dp       |
| tintIcon           | Boolean      | 用数字颜色给图标着色（`deleteKey()` 已开启） | false   |

### NumericKeyboardDefaults

| 常量                    | 类型        | 默认值    | COUI 来源                                |
| ----------------------- | ----------- | --------- | ---------------------------------------- |
| Width                   | Dp          | 248.dp    | coui_numeric_keyboard_view_width         |
| Height                  | Dp          | 326.dp    | coui_numeric_keyboard_view_height        |
| KeySize                 | Dp          | 68.dp     | coui_numeric_keyboard_view_size          |
| NumberTextSize          | Dp          | 32.dp     | number_keyboard_number_size              |
| NumberOffsetY           | Dp          | 1.dp      | coui_numeric_keyboard_number_offset_y    |
| SideTextSize            | Dp          | 16.dp     | coui_number_keyboard_finish_text_size    |
| AdditionalPressableArea | Dp          | 32.dp     | coui_additional_pressable_area           |
| LightShaderRadius       | Dp          | 124.dp    | coui_numeric_light_shader_radius         |
| EnterTranslateY         | Dp          | 258.dp    | coui_numeric_keyboard_max_translate_y    |
| DeleteIcon              | ImageVector | 25 x 14dp | ic_coui_number_keyboard_setting_delete   |

| 方法               | 说明                                                                                      |
| ------------------ | ----------------------------------------------------------------------------------------- |
| colors()           | 设置页键盘：黑色 / 白色数字，`cardBackground` / #8C3D3D3D 按键（放在页面 `backgroundWithCard` 上，而非卡片上），侧边键无底色，波纹 6% / 12%，文字 #2AD181   |
| lockScreenColors() | 锁屏：白色数字，按键无填充，白色波纹 12%                                                  |
| deleteKey()        | 删除侧边键                                                                                |
| finishKey()        | “完成”侧边键                                                                              |

### NumericKeyboardColors

| 属性                  | 类型  | 说明                     |
| --------------------- | ----- | ------------------------ |
| numberColor           | Color | 数字与侧边图标           |
| numberBackgroundColor | Color | 数字键填充（波纹效果）   |
| sideBackgroundColor   | Color | 侧边键填充               |
| pressedColor          | Color | 波纹颜色                 |
| circleMaxAlpha        | Float | 波纹峰值透明度           |
| wordTextColor         | Color | 侧边文字默认颜色         |

## 行为

- 手指仍在按键上松开时触发；滑出按键即取消；外层可滚动容器（列表、Pager）接管拖动时同样取消，因此键盘可以放在可滚动页面中。支持多指同时按键。触摸区域向外扩展最多 32dp，且不超过间距的一半。
- 波纹：按下时圆盘在 100ms 内从按键的 47% 放大到 100%；松开时在 160ms 内放大到 117% 并淡出，同时柔光晕在 400ms 内扩散到按键两倍大小并淡出。
- 光效：按下的按键回弹到 COUI 按压缩放；边缘发光（内光），124dp 内的按键接收径向光，均为 response 0.3 的弹簧（外光松开时为 0.6）。每个数字键带两层内阴影与 0.6dp 描边；Android 12 以下不支持图层模糊，内阴影改用模糊遮罩绘制（半径与偏移相同）。
- 数字为 32dp Medium，不随系统字号缩放。
