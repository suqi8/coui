# FullPageStatement

`FullPageStatement` 是 COUI 中的全屏用户协议 / 隐私声明页组件,通常用于首次启动,对应 ColorOS 的 COUIFullPageStatement(coui_full_page_statement.xml):居中标题、带上下渐隐边缘的可滚动声明正文,以及底部的主色填充大按钮和可选的无底色退出文字按钮。

与 COUI 布局一致,声明正文随文字增高,最高 `statementMaxHeight`(248dp),按钮紧跟其后;页面更高时,多出的空间留在底部。小屏横屏(最小宽度 < 480dp)时,退出与确认改为并排的两个 44dp 按钮。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
```

## 基本用法

```kotlin
FullPageStatement(
    title = "用户协议与隐私政策",
    content = AnnotatedString("欢迎使用!在继续之前,请仔细阅读以下声明…"),
    primaryButtonText = "同意",
    onPrimaryButtonClick = { /* 继续 */ },
    modifier = Modifier.fillMaxSize(),
    secondaryButtonText = "不同意并退出",
    onSecondaryButtonClick = { /* 退出 */ },
)
```

组件尺寸由 `modifier` 决定;作为整页使用时传入 `Modifier.fillMaxSize()`。

## 协议链接

声明正文是 `AnnotatedString`,协议链接用 `LinkAnnotation` 表示。`FullPageStatementDefaults.linkStyles()` 提供 COUI 样式(`couiColorLink`,按下时 alpha 77/255,无下划线):

```kotlin
val linkStyles = FullPageStatementDefaults.linkStyles()
val content = buildAnnotatedString {
    append("请阅读")
    withLink(LinkAnnotation.Clickable("agreement", linkStyles) { openAgreement() }) {
        append("《用户协议》")
    }
    append("后继续。")
}
```

## 属性

### FullPageStatement 属性

| 属性名                 | 类型                     | 说明                                     | 默认值                                           | 是否必须 |
| ---------------------- | ------------------------ | ---------------------------------------- | ------------------------------------------------ | -------- |
| title                  | String                   | 居中标题(16sp,中等字重),超过 80 / 120 / 160dp 时可滚动 | -                              | 是   |
| content                | AnnotatedString          | 可滚动声明正文(13sp,次要色),可带链接  | -                                                | 是   |
| primaryButtonText      | String                   | 主色填充按钮文字                         | -                                                | 是   |
| onPrimaryButtonClick   | () -> Unit               | 主按钮点击回调                           | -                                                | 是   |
| modifier               | Modifier                 | 应用于页面的修饰符                       | Modifier                                         | 否   |
| secondaryButtonText    | String?                  | 退出文字按钮文字,为 null 时不显示       | null                                             | 否   |
| onSecondaryButtonClick | (() -> Unit)?            | 退出文字按钮点击回调                     | null                                             | 否   |
| primaryButtonWidth     | Dp?                      | 主按钮宽度,null 时按 COUI 分档自适应    | null                                             | 否   |
| statementMaxHeight     | Dp                       | 声明正文最大高度                         | FullPageStatementDefaults.StatementMaxHeight     | 否   |
| colors                 | FullPageStatementColors  | 文字颜色配置                             | FullPageStatementDefaults.fullPageStatementColors() | 否 |
| primaryButtonColors    | ButtonColors             | 主按钮颜色                               | ButtonDefaults.buttonColorsPrimary()             | 否   |
| contentPadding         | PaddingValues            | 声明正文四周内边距                       | FullPageStatementDefaults.ContentPadding         | 否   |
| scrollState            | ScrollState              | 正文区域滚动状态                         | rememberScrollState()                            | 否   |

### FullPageStatementDefaults 对象

| 常量                        | 类型          | 默认值                                          | COUI 来源                                            |
| --------------------------- | ------------- | ----------------------------------------------- | ---------------------------------------------------- |
| TitleMarginTop              | Dp            | 12.dp                                           | coui_full_page_statement_text_button_padding         |
| TitleMarginBottom           | Dp            | 12.dp                                           | coui_full_page_statement_content_margin              |
| TitleMarginHorizontal       | Dp            | 24.dp                                           | coui_full_page_statement_text_button_padding_horizontal |
| ContentPadding              | PaddingValues | start 24.dp, top 12.dp, end 24.dp, bottom 14.dp | coui_full_page_statement_padding_*                   |
| ScrollFadeLength            | Dp            | 32.dp                                           | coui_full_page_statement_scroll_fade_length          |
| StatementMaxHeight          | Dp            | 248.dp                                          | coui_full_page_statement_scroll_max_height           |
| TitleMarginShort            | Dp            | 3.dp                                            | values-h200dp 标题边距                               |
| ShortWindowHeight           | Dp            | 300.dp                                          | values-h300dp 分档                                   |
| PrimaryButtonMarginTop      | Dp            | 20.dp                                           | coui_full_page_statement_button_margin_top           |
| PrimaryButtonMarginBottom   | Dp            | 16.dp                                           | coui_full_page_statement_button_margin               |
| PrimaryButtonMarginTopShort | Dp            | 10.dp                                           | values-h200dp button_margin_top                      |
| PrimaryButtonMarginBottomShort | Dp         | 8.dp                                            | values-h200dp button_margin                          |
| SmallLandSmallestWidth      | Dp            | 480.dp                                          | COUIFullPageStatement.isSmallScreen                  |
| LandButtonHeight            | Dp            | 44.dp                                           | coui_full_page_statement_button_button_width_landlong |
| LandButtonMarginHorizontal  | Dp            | 37.dp                                           | coui_full_page_statement_button_margin_horizontal_landlong |
| LandButtonSpacing           | Dp            | 8.dp                                            | 2 × coui_full_page_statement_button_margin_center_landlong |
| LandButtonMarginTop         | Dp            | 16.dp                                           | coui_full_page_statement_button_margin_top_landlong  |
| LandButtonMarginBottom      | Dp            | 24.dp                                           | coui_full_page_statement_button_margin_bottom_landlong |
| SecondaryButtonMarginBottom | Dp            | 24.dp                                           | coui_full_page_statement_exit_button_margin_bottom   |
| PrimaryButtonWidthCompact   | Dp            | 174.dp                                          | coui_full_page_statement_button_width(基础值)      |
| PrimaryButtonWidthMedium    | Dp            | 220.dp                                          | coui_full_page_statement_button_width(values-w300dp) |
| PrimaryButtonWidthExpanded  | Dp            | 280.dp                                          | coui_full_page_statement_button_width(values-w600dp) |
| MediumWidthThreshold        | Dp            | 300.dp                                          | values-w300dp 分档                                   |
| ExpandedWidthThreshold      | Dp            | 600.dp                                          | values-w600dp 分档                                   |

`FullPageStatementDefaults.primaryButtonWidth(availableWidth)` 按给定可用宽度解析 COUI 自适应按钮宽度,`titleMaxHeight(windowHeight)` 给出标题滚动区上限(80dp,窗口高 ≥300dp 时 120dp,≥560dp 时 160dp),`linkStyles(color)` 给出正文链接的 `TextLinkStyles`。

### `fullPageStatementColors()` 工厂

| 参数                 | 类型  | 默认值                                    | COUI 角色                   |
| -------------------- | ----- | ----------------------------------------- | --------------------------- |
| titleColor           | Color | COUITheme.colorScheme.labelPrimary       | couiColorPrimaryNeutral     |
| contentColor         | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral      |
| secondaryButtonColor | Color | COUITheme.colorScheme.primary            | couiColorPrimaryTextOnPopup |

### FullPageStatementColors 类

| 属性名               | 类型  | 说明               |
| -------------------- | ----- | ------------------ |
| titleColor           | Color | 标题颜色           |
| contentColor         | Color | 声明正文颜色       |
| secondaryButtonColor | Color | 退出文字按钮颜色   |

## 行为

- 声明正文可滚动,上下各有 32dp 渐隐边缘,随滚动偏移逐渐显现,对应 COUIMaxHeightScrollView 宿主的 fading edge。
- 窗口高度小于 300dp 时使用 values-h200dp 的紧凑边距(标题 3dp,按钮上 10dp / 下 8dp)。
- 主按钮文字最多两行(COUIFullPageStatement `setMaxLines(2)`),宽度按 COUI 资源分档:174dp,窗口宽 ≥300dp 时 220dp,≥600dp 时 280dp。
- 退出按钮是无底色的主题色纯文字(16sp,中等字重),无按压蒙层,对应 COUI 的 `txt_exit` TextView。

## 进阶用法

### 仅确认按钮的声明页

省略 `secondaryButtonText`(默认为 `null`)即可隐藏退出文字按钮:

```kotlin
FullPageStatement(
    title = "用户协议",
    content = AnnotatedString("欢迎使用!在继续之前,请仔细阅读以下声明…"),
    primaryButtonText = "知道了",
    onPrimaryButtonClick = { /* 继续 */ }
)
```

### 固定主按钮宽度

传入 `primaryButtonWidth` 可覆盖 COUI 自适应分档宽度:

```kotlin
FullPageStatement(
    title = "用户协议",
    content = AnnotatedString("欢迎使用!在继续之前,请仔细阅读以下声明…"),
    primaryButtonText = "同意",
    onPrimaryButtonClick = { /* 继续 */ },
    primaryButtonWidth = 240.dp
)
```

### 自定义颜色

```kotlin
FullPageStatement(
    title = "用户协议",
    content = AnnotatedString("欢迎使用!在继续之前,请仔细阅读以下声明…"),
    primaryButtonText = "同意",
    onPrimaryButtonClick = { /* 继续 */ },
    secondaryButtonText = "不同意并退出",
    onSecondaryButtonClick = { /* 退出 */ },
    colors = FullPageStatementDefaults.fullPageStatementColors(
        contentColor = COUITheme.colorScheme.labelPrimary
    ),
    primaryButtonColors = ButtonDefaults.buttonColors()
)
```
