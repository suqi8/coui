# MarqueeText

`MarqueeText` 是放不下时无限滚动、两端渐隐的单行文本，对应 ColorOS 的 `COUIMarqueeTextView`（COUI 顶部提示条的内容视图）。放得下的文本静止显示，不渐隐。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=marqueeText" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.MarqueeText
import io.github.suqi8.coui.kmp.basic.MarqueeTextDefaults
```

## 基本用法

```kotlin
MarqueeText(
    text = "电池优化正在限制多个应用的后台活动，点击查看详情并逐一调整",
    style = COUITheme.textStyles.bodyS,
    modifier = Modifier.fillMaxWidth(),
)
```

## 属性

### MarqueeText 属性

| 属性名                | 类型        | 说明                                   | 默认值                                    | 是否必须 |
| ------------------ | --------- | ------------------------------------ | -------------------------------------- | ---- |
| text               | String    | 显示的文本                                | -                                      | 是    |
| modifier           | Modifier  | 应用于文本的修饰符                            | Modifier                               | 否    |
| enabled            | Boolean   | 是否开启跑马灯；关闭时文本正常换行                    | true                                   | 否    |
| color              | Color     | 文本颜色；Unspecified 时取 `style` 与内容色      | Color.Unspecified                      | 否    |
| style              | TextStyle | 文本样式                                 | LocalTextStyles.current.main           | 否    |
| velocity           | Dp        | 滚动速度，每秒 dp 数                         | MarqueeTextDefaults.Velocity           | 否    |
| initialDelayMillis | Int       | 首次滚动前的延迟                             | MarqueeTextDefaults.InitialDelayMillis | 否    |
| spacing            | Dp        | 文本末尾与下一份拷贝之间的间距                      | MarqueeTextDefaults.Spacing            | 否    |
| startOffset        | Dp        | 滚动前文本距起始边的缩进                         | MarqueeTextDefaults.StartOffset        | 否    |
| fadingEdgeLength   | Dp        | 滚动时两端渐隐的长度                           | MarqueeTextDefaults.FadingEdgeLength   | 否    |

### MarqueeTextDefaults 对象

| 常量名                | 类型  | 说明                                                 | 默认值   |
| ------------------ | --- | -------------------------------------------------- | ----- |
| Velocity           | Dp  | 每秒滚动距离（`coui_top_tips_scroll_speed`）               | 25.dp |
| InitialDelayMillis | Int | 首次滚动前延迟（`DEFAULT_SCROLL_DELAY_DURATION`）           | 1000  |
| Spacing            | Dp  | 拷贝间距（`coui_top_tips_scroll_text_interval`）         | 80.dp |
| StartOffset        | Dp  | 静止时的起始缩进（`coui_top_tips_scroll_text_start_location`） | 10.dp |
| FadingEdgeLength   | Dp  | 每端渐隐长度（`coui_top_tips_fading_edge_size`）           | 26.dp |

## 行为

- **放得下**：测量后能完整显示时静止绘制，不渐隐。
- **滚动**：否则文本从距起始边 10dp 处开始，1 秒后以每秒 25dp 的速度连续滚动（COUI 每帧移动 `25dp / 刷新率`），拷贝间距 80dp，循环之间不停顿。RTL 文本反向滚动。
- **渐隐**：滚动时两端各渐隐 26dp，等同于强度为 1 的 `TextView` 水平渐隐边缘。
- **关闭**：`enabled = false` 时为普通可换行的 `Text`。
