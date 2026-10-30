# LockScreenPasswordInput

`LockScreenPasswordInput` 复刻 ColorOS 的锁屏密码输入组合（`COUILockScreenPwdInputLayout` + `COUILockScreenPwdInputView`）：48dp 胶囊形密码输入框（带显隐切换眼睛按钮）与 48dp 圆形下一步按钮。在桌面（锁屏）模式下两者都是玻璃质感：透明、带内阴影与细描边；按下按钮时回弹缩小，点亮自身边缘与输入框末端。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=lockScreenPasswordInput" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputDefaults
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode
```

## 基本用法

开启 `enableInputCount` 后输入长度不超过 `maxCount`，达到 `minCount` 时下一步按钮激活：

```kotlin
var password by remember { mutableStateOf("") }
var error by remember { mutableStateOf("") }

LockScreenPasswordInput(
    value = password,
    onValueChange = { password = it; error = "" },
    onNextClick = { if (!check(it)) error = "密码错误" },
    errorMessage = error,
    enableInputCount = true,
)
```

## 锁屏

```kotlin
LockScreenPasswordInput(
    value = password,
    onValueChange = { password = it },
    onNextClick = { unlock(it) },
    mode = LockScreenPasswordInputMode.Desktop,
)
```

背后没有模糊壁纸时，可为桌面模式加填充：`colors = LockScreenPasswordInputDefaults.colors(LockScreenPasswordInputMode.Desktop).copy(fieldColor = LockScreenPasswordInputDefaults.DesktopFallbackColor, buttonColor = LockScreenPasswordInputDefaults.DesktopFallbackColor)`。

## 属性

### LockScreenPasswordInput

| 属性                   | 类型                          | 说明                         | 默认值                                        | 必须 |
| ---------------------- | ----------------------------- | ---------------------------- | --------------------------------------------- | ---- |
| value                  | String                        | 密码                         | -                                             | 是   |
| onValueChange          | (String) -> Unit              | 密码变化                     | -                                             | 是   |
| onNextClick            | (String) -> Unit              | 点击已激活的下一步按钮       | -                                             | 是   |
| modifier               | Modifier                      | 应用于组件的修饰符           | Modifier                                      | 否   |
| mode                   | LockScreenPasswordInputMode   | `Setting` 或 `Desktop`       | Setting                                       | 否   |
| enabled                | Boolean                       | 输入框是否可输入             | true                                          | 否   |
| label                  | String                        | 为空时的提示                 | ""                                            | 否   |
| errorMessage           | String                        | 输入框下方的错误；空则隐藏   | ""                                            | 否   |
| showPasswordToggle     | Boolean                       | 显示眼睛按钮                 | true                                          | 否   |
| enableInputCount       | Boolean                       | 启用 `minCount` / `maxCount` | false                                         | 否   |
| minCount               | Int                           | 激活下一步按钮所需长度       | 6                                             | 否   |
| maxCount               | Int                           | 最大长度                     | 16                                            | 否   |
| colors                 | LockScreenPasswordInputColors | 颜色                         | LockScreenPasswordInputDefaults.colors(mode)  | 否   |
| keyboardOptions        | KeyboardOptions               | 键盘选项                     | NumberPassword                                | 否   |
| keyboardActions        | KeyboardActions               | 键盘动作                     | KeyboardActions.Default                       | 否   |
| nextContentDescription | String                        | 按钮的无障碍描述             | "Next"                                        | 否   |
| nextButtonInteractionSource | MutableInteractionSource? | 确认按钮的交互源；发送按压即可播放 Desktop 样式的按压光效 | null | 否 |

### LockScreenPasswordInputDefaults

| 常量                   | 类型     | 默认值    | COUI 来源                                          |
| ---------------------- | -------- | --------- | -------------------------------------------------- |
| Height                 | Dp       | 48.dp     | coui_input_lock_screen_pwd_height                  |
| ButtonMargin           | Dp       | 12.dp     | coui_input_lock_screen_pwd_icon_margin             |
| FieldWidth             | Dp       | 268.dp    | coui_input_lock_screen_pwd_setting_width           |
| FieldPadding           | Dp       | 16.dp     | coui_input_lock_screen_pwd_card_padding            |
| FieldPaddingVertical   | Dp       | 10.5.dp   | coui_input_lock_screen_pwd_title_padding_top       |
| IconSize               | Dp       | 24.dp     | coui_input_lock_screen_pwd_next_src                |
| ErrorFontSize          | TextUnit | 10.sp     | couiTextAppearanceCaption                          |
| ErrorPaddingTop        | Dp       | 8.dp      | text_input_error                                   |
| ErrorPaddingHorizontal | Dp       | 32.dp     | text_input_error                                   |
| MinCount               | Int      | 6         | DEFAULT_MIN_INPUT_COUNT                            |
| MaxCount               | Int      | 16        | DEFAULT_MAX_INPUT_COUNT                            |
| DesktopFallbackColor   | Color    | #33FFFFFF | coui_input_lock_screen_pwd_view_bg_color_desktop   |

| 方法         | 说明                                                                                                     |
| ------------ | -------------------------------------------------------------------------------------------------------- |
| colors(mode) | Setting：`surfaceContainer` 输入框与按钮，三级 / 主要箭头色。Desktop：透明，白色文字，#4DFFFFFF / 白色箭头 |

### LockScreenPasswordInputColors

| 属性            | 类型  | 说明                     |
| --------------- | ----- | ------------------------ |
| fieldColor      | Color | 输入框填充               |
| buttonColor     | Color | 下一步按钮填充           |
| textColor       | Color | 密码文字                 |
| hintColor       | Color | 提示文字                 |
| iconColor       | Color | 未激活箭头与眼睛按钮     |
| activeIconColor | Color | 已激活箭头               |
| errorColor      | Color | 错误文字                 |

## 行为

- 错误文字在 217ms 内淡入、283ms 内淡出（`COUIEaseInterpolator`），输入框进入错误态。
- 桌面模式按压：按钮回弹到 COUI 按压缩放；边缘发光，径向光延伸进输入框，均为 response 0.3 的弹簧（外光松开时为 0.6）。仅在按钮激活时有按压效果。
- `minCount` / `maxCount` 组合无效时回退为 6 / 16。
- 下一步按钮位于行尾：从右到左布局中在左侧。
