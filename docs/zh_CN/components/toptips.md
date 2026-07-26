# TopTips

页面顶部提示条,对应 ColorOS 的 COUIDefaultTopTips:圆角卡片内含可选的 24dp 前置图标、14sp 文案和一个尾部元素——强调色行动按钮或圆形关闭按钮。切换可见性会播放 COUI 的 250ms 纵向缩放动画。

## 引入

```kotlin
import com.suqi8.coui.kmp.basic.TopTips
import com.suqi8.coui.kmp.basic.TopTipsDefaults
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
            modifier = Modifier.size(24.dp),
        )
    },
    onClose = { visible = false },
    modifier = Modifier.fillMaxWidth(),
)
```

## 属性

### TopTips

| 属性         | 类型                       | 说明                                     | 默认值                          | 必需 |
| ------------ | -------------------------- | ---------------------------------------- | ------------------------------- | ---- |
| text         | String                     | 提示文案                                 | -                               | 是   |
| modifier     | Modifier                   | 应用于提示条的修饰符                     | Modifier                        | 否   |
| visible      | Boolean                    | 是否可见(切换时播放进出动画)          | true                            | 否   |
| startIcon    | (@Composable () -> Unit)?  | 可选前置图标                             | null                            | 否   |
| actionText   | String?                    | 尾部行动按钮文案                         | null                            | 否   |
| onAction     | (() -> Unit)?              | 行动按钮点击回调                         | null                            | 否   |
| onClose      | (() -> Unit)?              | 关闭按钮回调(无 actionText 时显示)    | null                            | 否   |
| cornerRadius | Dp                         | 提示条圆角                               | TopTipsDefaults.CornerRadius    | 否   |
| colors       | TopTipsColors              | 颜色配置                                 | TopTipsDefaults.topTipsColors() | 否   |

### TopTipsDefaults

| 常量            | 类型 | 默认值 | COUI 来源                            |
| --------------- | ---- | ------ | ------------------------------------ |
| CornerRadius    | Dp   | 12.dp  | couiRoundCornerM                     |
| ContentPadding  | Dp   | 12.dp  | coui_toptips_view_btn_margin         |
| VerticalPadding | Dp   | 12.dp  | coui_toptips_view_title_top_margin   |
| IconSize        | Dp   | 24.dp  | coui_toptips_view_icon_btn_size      |
| IconSpacing     | Dp   | 8.dp   | coui_toptips_view_title_start_margin |
| TextMinHeight   | Dp   | 20.dp  | coui_toptips_view_title_min_height   |

### `topTipsColors()` 工厂

| 参数             | 类型  | 默认值                                    | COUI 属性              |
| ---------------- | ----- | ----------------------------------------- | ---------------------- |
| containerColor   | Color | 亮色 #0A000000 / 暗色 #14FFFFFF           | couiColorContainer4    |
| contentColor     | Color | COUITheme.colorScheme.onSurfaceSecondary | couiColorSecondNeutral |
| actionColor      | Color | COUITheme.colorScheme.primary            | couiColorPrimaryText   |
| closeButtonColor | Color | 亮色 #29000000 / 暗色 #40FFFFFF           | couiColorControls      |
| closeIconColor   | Color | Color.White                               | coui_ic_toptips_close  |

## 行为

- 尾部元素互斥,与 COUIDefaultTopTipsView 的按钮类型一致:提供 `actionText` 时显示强调色文字按钮,否则提供 `onClose` 时显示圆形关闭按钮。
- 当单行文案将与行动按钮相撞时,按钮下移到文字下方靠右单独一行(COUIDefaultTopTipsView.isNeedMultiText);此布局中前置图标与第一行文字对齐。
- 切换 `visible` 会播放 COUICustomTopTips 的显示/消失动画:以中心为轴的 250ms 纵向缩放;消失动画结束后提示条移出组合。
- 文案为 14sp(`coui_toptips_view_default_text_size`),行动按钮文案为 14sp Medium。
