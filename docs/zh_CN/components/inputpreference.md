# InputPreference

`InputPreference` 是内嵌单行输入框的卡片列表行，对应 ColorOS 的 `COUIInputPreference`。可选标题位于输入框上方；输入框平时不画下划线，仅在获得焦点时显示焦点线（`couiJustShowFocusLine`），卡片分组的最后一行完全不画线。该行没有按压态。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=inputPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.InputPreference
import io.github.suqi8.coui.kmp.preference.InputPreferenceDefaults
```

## 基本用法

```kotlin
var name by remember { mutableStateOf("My Phone") }

Card {
    CardListGroup {
        item {
            InputPreference(
                value = name,
                onValueChange = { name = it },
                title = "Device name",
                hint = "Enter a name"
            )
        }
    }
}
```

无标题时输入框上下内边距均为 13dp；有标题时为上 0dp / 下 11dp。

## 组件状态

### 计数与错误

```kotlin
var hotspot by remember { mutableStateOf("") }

InputPreference(
    value = hotspot,
    onValueChange = { hotspot = it },
    hint = "Hotspot name",
    maxCount = 20,
    errorMessage = if (hotspot.contains(' ')) "Spaces are not allowed" else ""
)
```

### 禁用状态

```kotlin
InputPreference(
    value = "Read only",
    onValueChange = {},
    title = "Serial number",
    enabled = false
)
```

## 属性

### InputPreference 属性

| 属性名               | 类型                  | 说明                                       | 默认值                                           | 是否必须 |
| -------------------- | --------------------- | ------------------------------------------ | ------------------------------------------------ | -------- |
| value                | String                | 输入框文本                                 | -                                                | 是       |
| onValueChange        | (String) -> Unit      | 文本变化回调                               | -                                                | 是       |
| modifier             | Modifier              | 组件修饰符                                 | Modifier                                         | 否       |
| title                | String?               | 输入框上方的标题                           | null                                             | 否       |
| hint                 | String                | 输入框为空时的提示                         | ""                                               | 否       |
| enabled              | Boolean               | 是否可用                                   | true                                             | 否       |
| readOnly             | Boolean               | 是否只读                                   | false                                            | 否       |
| justShowFocusLine    | Boolean               | 只显示焦点线（不画常驻下划线）             | true                                             | 否       |
| maxCount             | Int?                  | 最大字符数，显示 `count/max` 计数          | null                                             | 否       |
| showClearButton      | Boolean               | 获得焦点且非空时显示清除按钮               | false                                            | 否       |
| showPasswordToggle   | Boolean               | 显示密码可见切换                           | false                                            | 否       |
| errorMessage         | String                | 输入框下方的错误文字，空串隐藏             | ""                                               | 否       |
| colors               | InputPreferenceColors | 标题 / 错误文字颜色                        | InputPreferenceDefaults.inputPreferenceColors()  | 否       |
| textFieldColors      | TextFieldColors       | 输入框颜色                                 | TextFieldDefaults.textFieldColors()              | 否       |
| textStyle            | TextStyle             | 输入文字样式                               | InputViewDefaults.textStyle()                    | 否       |
| keyboardOptions      | KeyboardOptions       | 键盘选项                                   | KeyboardOptions.Default                          | 否       |
| keyboardActions      | KeyboardActions       | 键盘动作                                   | KeyboardActions.Default                          | 否       |
| visualTransformation | VisualTransformation  | 输入框视觉变换                             | VisualTransformation.None                        | 否       |
| insideMargin         | PaddingValues         | 卡片边缘到内容的边距                       | InputPreferenceDefaults.InsideMargin             | 否       |
| cardListPosition     | CardListPosition      | 行在卡片分组中的位置；最后一行不画线       | LocalCardListPosition.current                    | 否       |
| interactionSource    | MutableInteractionSource? | 输入框交互源                           | null                                             | 否       |

### InputPreferenceDefaults 对象

| 常量                        | 类型          | 默认值                              | 说明                                                   |
| --------------------------- | ------------- | ----------------------------------- | ------------------------------------------------------ |
| InsideMargin                | PaddingValues | PaddingValues(horizontal = 16.dp)   | 行内边距 32dp 减去卡片外边距 16dp                      |
| TitlePaddingTop             | Dp            | 10.dp                               | coui_input_preference_title_padding_top                |
| TitlePaddingBottom          | Dp            | 4.dp                                | coui_input_preference_title_padding_bottom             |
| TitleMinHeight              | Dp            | 22.dp                               | coui_inputView_title_minheight                         |
| FieldPaddingVertical        | Dp            | 13.dp                               | coui_input_edit_text_no_title_padding_*_inPreference   |
| FieldPaddingBottomWithTitle | Dp            | 11.dp                               | coui_input_edit_error_text_has_title_padding_bottom    |
| ErrorFontSize               | TextUnit      | 10.sp                               | couiTextAppearanceCaption                              |
| ErrorPaddingVertical        | Dp            | 4.dp                                | 错误文字上下内边距                                     |

#### `inputPreferenceColors()` 工厂方法

| 参数               | 类型  | 默认值                                    |
| ------------------ | ----- | ----------------------------------------- |
| titleColor         | Color | COUITheme.colorScheme.onSurface           |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledOnSurface   |
| errorColor         | Color | COUITheme.colorScheme.error               |
