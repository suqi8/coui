# SpannablePreference

`SpannablePreference` 是摘要中含内联可点击链接的设置项，对应 COUI 的 `COUISpannablePreference`（开启了 `couiEnalbeClickSpan` 的 `COUIPreference`）。摘要由 [LinkText](./linktext) 绘制：点击链接只触发该链接回调，点击其它位置触发行的 `onClick`。

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=spannablePreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.SpannablePreference
```

## 基本用法

```kotlin
SpannablePreference(
    title = "Location services",
    summary = buildAnnotatedString {
        append("Apps use your location to show nearby results. ")
        appendLink("Learn more", onClick = { /* 打开帮助页 */ })
    },
    onClick = { /* 打开定位页 */ }
)
```

不传 `onClick` 时行不可点击，但其中的链接仍可交互。

## 组件状态

### 禁用状态

禁用的行会把标题、摘要和链接置灰，链接不再响应：

```kotlin
SpannablePreference(
    title = "Disabled",
    summary = buildAnnotatedString {
        append("The link of a disabled row is greyed out: ")
        appendLink("Privacy policy", onClick = {})
    },
    enabled = false
)
```

## 属性

### SpannablePreference 属性

| 属性名        | 类型                               | 说明                           | 默认值                                | 必须 |
| ------------- | ---------------------------------- | ------------------------------ | ------------------------------------- | ---- |
| title         | String                             | 设置项标题                     | -                                     | 是   |
| summary       | AnnotatedString                    | 含链接的摘要                   | -                                     | 是   |
| modifier      | Modifier                           | 组件修饰符                     | Modifier                              | 否   |
| titleColor    | BasicComponentColors               | 标题文本颜色配置               | BasicComponentDefaults.titleColor()   | 否   |
| summaryColor  | BasicComponentColors               | 摘要非链接文本颜色配置         | BasicComponentDefaults.summaryColor() | 否   |
| linkColors    | LinkTextColors                     | 摘要链接颜色                   | LinkTextDefaults.linkTextColors()     | 否   |
| startAction   | @Composable (() -> Unit)?          | 自定义左侧内容                 | null                                  | 否   |
| endActions    | @Composable (RowScope.() -> Unit)? | 自定义右侧内容                 | null                                  | 否   |
| bottomAction  | @Composable (() -> Unit)?          | 自定义底部内容                 | null                                  | 否   |
| insideMargin  | PaddingValues                      | 组件内部内容边距               | BasicComponentDefaults.InsideMargin   | 否   |
| cardListPosition | CardListPosition | 行在卡片分组中的位置（[CardListGroup](./card.md#cardlistgroup)），圆角外缘获得额外内边距 | LocalCardListPosition.current（在 CardListGroup 外为 None） | 否 |
| onClick       | (() -> Unit)?                      | 行点击回调（链接以外区域）     | null                                  | 否   |
| holdDownState | Boolean                            | 组件是否处于按下状态           | false                                 | 否   |
| enabled       | Boolean                            | 组件是否可交互                 | true                                  | 否   |

`LinkTextColors` 与 `LinkTextDefaults` 见 [LinkText](./linktext)。
