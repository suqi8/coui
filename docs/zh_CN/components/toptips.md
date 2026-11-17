# TopTips

页面顶部提示条，对应 COUI 17 的 `COUIDefaultTopTips`（coui_default_toptips.xml）：圆角卡片内含可选的前置图标、可选的 16sp Medium 标题、13sp 文案、可选的实心主色行动按钮和可选的关闭按钮——行动按钮与关闭按钮可以同时出现。文案还可以单行跑马灯滚动。切换可见性会播放 COUI 17 的展开/收起动画（250ms 高度裁剪 + 内容渐隐渐显）。

<div style="position: relative; height: 460px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=topTips" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.basic.TopTipsDefaults
```

## 基本用法

```kotlin
TopTips(
    text = "发现新的安全更新",
    actionText = "更新",
    onAction = { /* 处理行动 */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## 带前置图标的可关闭提示

```kotlin
var visible by remember { mutableStateOf(true) }

TopTips(
    text = "带前置图标的可关闭提示",
    visible = visible,
    startIcon = {
        Icon(
            imageVector = COUIIcons.Basic.Search,
            contentDescription = null,
        )
    },
    onClose = { visible = false },
    modifier = Modifier.fillMaxWidth(),
)
```

## 标题、行动按钮与关闭按钮并存

```kotlin
TopTips(
    title = "备份已暂停",
    text = "连接 WLAN 后继续备份照片",
    startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
    actionText = "重试",
    onAction = { /* 处理行动 */ },
    onClose = { /* 隐藏提示 */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## 跑马灯

```kotlin
TopTips(
    text = "放不进一行的长文案会持续滚动显示",
    marquee = true,
    modifier = Modifier.fillMaxWidth(),
)
```

## 属性

### TopTips

| 属性         | 类型                       | 说明                                     | 默认值                          | 必需 |
| ------------ | -------------------------- | ---------------------------------------- | ------------------------------- | ---- |
| text         | String                     | 提示文案                                 | -                               | 是   |
| modifier     | Modifier                   | 应用于提示条的修饰符                     | Modifier                        | 否   |
| visible      | Boolean                    | 是否可见（切换时播放进出动画）           | true                            | 否   |
| title        | String?                    | 文案上方的可选标题（16sp Medium，最多 2 行） | null                        | 否   |
| startIcon    | (@Composable () -> Unit)?  | 可选前置图标；图标框（24dp，有标题时 36dp）会把尺寸强加给图标 | null      | 否   |
| actionText   | String?                    | 行动按钮文案                             | null                            | 否   |
| onAction     | (() -> Unit)?              | 行动按钮点击回调                         | null                            | 否   |
| onClose      | (() -> Unit)?              | 关闭按钮回调；不为 null 时显示关闭按钮   | null                            | 否   |
| marquee      | Boolean                    | 文案单行跑马灯滚动（`COUIMarqueeTopTips.startRoll`） | false               | 否   |
| cornerRadius | Dp                         | 提示条圆角；未指定 = COUI 17 自动（无标题且单行时为权重 1.0 的胶囊，否则 28dp、权重 3.0，切换时 300ms 动画） | Dp.Unspecified | 否 |
| colors       | TopTipsColors              | 颜色配置                                 | TopTipsDefaults.topTipsColors() | 否   |

### TopTipsDefaults

| 常量              | 类型  | 默认值 | COUI 来源                            |
| ----------------- | ----- | ------ | ------------------------------------ |
| CornerRadius      | Dp    | 28.dp  | coui_toptips_view_radius（多行）     |
| Weight            | Float | 3.0    | `getMultiLineWeight`（weight_16_1_large） |
| SingleLineCornerRadius | Dp | 72.dp | couiRoundCornerFULL（单行胶囊）    |
| SingleLineWeight  | Float | 1.0    | `getSingleLineWeight`（软胶囊）     |
| HorizontalPadding | Dp    | 16.dp  | coui_toptips_view_icon_start_margin / content_end_margin / btn_margin |
| VerticalPadding   | Dp    | 12.dp  | coui_toptips_view_content_top_margin |
| IconSize          | Dp    | 24.dp  | coui_toptips_view_icon_btn_size（无标题时的图标、关闭按钮） |
| TitleIconSize     | Dp    | 36.dp  | coui_toptips_view_title_icon_size    |
| IconSpacing       | Dp    | 8.dp   | coui_toptips_view_content_start_margin |
| TitleIconSpacing  | Dp    | 12.dp  | coui_toptips_view_title_icon_text_gap |
| TextMinHeight     | Dp    | 20.dp  | coui_toptips_view_content_min_height |
| TitleMinHeight    | Dp    | 22.dp  | coui_toptips_view_title_min_height   |
| MarqueeVelocity   | Dp    | 25.dp  | coui_top_tips_scroll_speed（每秒）   |

#### 方法

| 方法名            | 类型          | 说明                                                      |
| ----------------- | ------------- | --------------------------------------------------------- |
| titleTextStyle()  | TextStyle     | 标题文本样式（couiTextHeadlineXS，16sp Medium）           |
| textStyle()       | TextStyle     | 文案文本样式（couiTextBodyS，13sp）                       |
| actionTextStyle() | TextStyle     | 行动按钮文字样式（Widget.COUI.Button.Small，14sp Medium） |
| topTipsColors()   | TopTipsColors | 创建默认颜色配置（见下）                                  |

### `topTipsColors()` 工厂

| 参数               | 类型  | 默认值                                    | COUI 属性                |
| ------------------ | ----- | ----------------------------------------- | ------------------------ |
| containerColor     | Color | 亮色 #0A000000 / 暗色 #1AFFFFFF           | couiColorContainer4      |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary           | couiColorLabelPrimary    |
| contentColor       | Color | COUITheme.colorScheme.labelSecondary  | couiColorLabelSecondary  |
| actionColor        | Color | COUITheme.colorScheme.primary             | couiColorContainerTheme（按钮填充） |
| actionContentColor | Color | COUITheme.colorScheme.onPrimary           | couiColorLabelOnColor（按钮文字） |
| closeButtonColor   | Color | COUITheme.colorScheme.labelTertiary       | couiColorLabelTertiary   |
| closeIconColor     | Color | Color.White                               | ic_embedded_top_tips_close |

## 行为

- 行动按钮是 `Widget.COUI.Button.Small.ButtonNew` 主色胶囊（14sp Medium、内边距 12/4dp、最小 52x28dp），强制关闭材质效果。行动按钮与关闭按钮可同时出现（间距 12dp，关闭按钮距末端 12dp）。
- 无标题时前置图标为 24dp，按钮位于文案之后、与文案垂直居中。只有在有前置图标、且文案在按钮旁需要超过两行时，行动按钮才移到文案下方并与文案起始边对齐（上 8dp、下 12dp），图标与关闭按钮对齐到前两行的中线（`isNeedDefaultMultiTextWithoutTitle`）。
- 有标题时前置图标为 36dp（间距 12dp），按钮在「标题 + 文案」整体上垂直居中。有前置图标且任一文字在按钮旁放不下一行时，行动按钮移到文案下方，关闭按钮与标题顶部对齐（`isNeedDefaultMultiTextWithTitle`）。
- 开启 `marquee` 后文案为单行；放不下时以 25dp/s 连续滚动，副本间距 80dp，两端 26dp 渐隐（`COUIMarqueeTextView`），此时行动按钮不会下移。
- 切换 `visible` 播放 `COUIDefaultTopTips.animateExpand` / `animateCollapse`：卡片从顶边起以 250ms 高度裁剪展开（COUIEaseInterpolator），内容透明度随同一进度变化。动画期间提示条在布局中保持完整高度，收起后移出组合。
