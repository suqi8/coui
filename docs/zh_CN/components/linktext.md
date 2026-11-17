# LinkText

`LinkText` 以 ColorOS 链接样式显示带内联可点击链接的文本，对应 COUI 的 `COUIClickableSpan`。链接是标准的 Compose `LinkAnnotation`：点击链接只触发该链接的回调，点击其它位置仍会传给可点击的父级。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=linkText" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.appendLink
```

## 基本用法

用 `buildAnnotatedString` 构建文本，并用 `appendLink` 追加链接：

```kotlin
LinkText(
    text = buildAnnotatedString {
        append("Read the ")
        appendLink("User Agreement", onClick = { /* 打开协议 */ })
        append(" and the ")
        appendLink("Privacy Policy", onClick = { /* 打开隐私政策 */ })
    },
    color = COUITheme.colorScheme.labelSecondary,
    style = COUITheme.textStyles.bodyS
)
```

::: tip 链接样式
没有自带 `TextLinkStyles` 的链接都会套用 COUI 链接样式（`coui_clickable_text_color`）：常态为主题强调色，按下时为 30% 透明度的强调色，无下划线，链接背后无高亮。`appendLink` 追加的链接不带样式，因此需用 `LinkText`（或基于它的组件）渲染；普通 `Text` 会给无样式链接套上它自己的下划线样式。
:::

## 为什么用 `LinkAnnotation`

`COUIClickableSpan` 是由 `LinkMovementMethod` 驱动的 `ClickableSpan`。Compose 中的对应物是带 `TextLinkStyles` 的 `LinkAnnotation.Clickable`：只对链接范围做命中测试，向无障碍服务暴露链接，并自带 `pressedStyle`，可直接对应 COUI 的按下颜色。已废弃的 `ClickableText` 则需要手动按偏移做命中测试和按压跟踪。

## 组件状态

### 禁用状态

禁用的 `LinkText` 以禁用色绘制链接并移除其点击处理：

```kotlin
LinkText(
    text = buildAnnotatedString {
        append("A disabled link: ")
        appendLink("Terms of service", onClick = {})
    },
    enabled = false
)
```

## 属性

### LinkText 属性

| 属性名     | 类型            | 说明                                  | 默认值                            | 必须 |
| ---------- | --------------- | ------------------------------------- | --------------------------------- | ---- |
| text       | AnnotatedString | 含链接 `LinkAnnotation` 的文本        | -                                 | 是   |
| modifier   | Modifier        | 组件修饰符                            | Modifier                          | 否   |
| enabled    | Boolean         | 链接是否可交互                        | true                              | 否   |
| color      | Color           | 非链接文本颜色                        | Color.Unspecified                 | 否   |
| linkColors | LinkTextColors  | 未自带样式的链接颜色                  | LinkTextDefaults.linkTextColors() | 否   |
| style      | TextStyle       | 文本样式                              | COUITheme.textStyles.bodyXL         | 否   |
| textAlign  | TextAlign?      | 文本对齐                              | null                              | 否   |
| overflow   | TextOverflow    | 溢出处理                              | TextOverflow.Clip                 | 否   |
| maxLines   | Int             | 最大行数                              | Int.MAX_VALUE                     | 否   |

### appendLink

`AnnotatedString.Builder.appendLink(text: String, onClick: () -> Unit, tag: String = text)` 把 `text` 作为不带样式的 `LinkAnnotation.Clickable` 追加。

### LinkTextDefaults 对象

| 函数 / 属性        | 说明                                                                          |
| ------------------ | ----------------------------------------------------------------------------- |
| linkTextColors()   | 创建 `LinkTextColors`：`color`（主题 `primary`）、`pressedColor`（`color` 的 30% 透明度）、`disabledColor`（主题 `outline`，COUI #26000000 / #26FFFFFF） |
| linkStyles(colors) | COUI 链接的 `TextLinkStyles`，用于显式指定样式的链接                          |

### LinkTextColors 类

| 属性名        | 类型  | 说明           |
| ------------- | ----- | -------------- |
| color         | Color | 常态链接颜色   |
| pressedColor  | Color | 按下时链接颜色 |
| disabledColor | Color | 禁用时链接颜色 |

## 进阶用法

### 在普通 Text 中显式指定样式

```kotlin
val linkStyles = LinkTextDefaults.linkStyles()
Text(
    text = buildAnnotatedString {
        append("Visit the ")
        withLink(LinkAnnotation.Url("https://example.com", styles = linkStyles)) {
            append("website")
        }
    }
)
```
