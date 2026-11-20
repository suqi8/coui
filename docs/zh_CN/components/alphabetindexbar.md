# AlphabetIndexBar

`AlphabetIndexBar` 复刻 ColorOS 的侧边字母索引条（`COUITouchSearchView`），常见于联系人和应用列表：在索引条上按住或拖动即可选中字母，选中项高亮，并在索引条旁弹出显示该字母的气泡。

<div style="position: relative; height: 520px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=alphabetIndexBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBar
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBarDefaults
```

## 基本用法

把索引条放在它所索引的列表之上（气泡绘制在其边界之外，COUI 用的是弹窗，因此 `Card` 等会裁剪的父布局需为气泡及其阴影留出空间），并在 `onKeySelected` 中滚动列表：

```kotlin
Box(Modifier.fillMaxSize()) {
    LazyColumn(state = listState) { /* ... */ }
    AlphabetIndexBar(
        onKeySelected = { key ->
            scope.launch { listState.scrollToItem(indexOfSection(key)) }
        },
        availableKeys = sectionKeys,
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight(),
    )
}
```

## 跟随列表

把列表第一个可见项所在的分组作为 `selectedKey` 传入，高亮即可随滚动移动（COUI `updateMoveTouchBarText`）。它只移动高亮，不触发触感、不弹气泡，手指按在索引条上时忽略；被圆点隐藏的字母会高亮该圆点：

```kotlin
val firstSection by remember {
    derivedStateOf { sectionOf(listState.firstVisibleItemIndex) }
}
AlphabetIndexBar(
    onKeySelected = { key -> scope.launch { listState.scrollToItem(indexOfSection(key)) } },
    selectedKey = firstSection,
)
```

## 可选字母与圆点

不在 `availableKeys` 中的字母仍会显示，但不能被选中：按到它时会选中其上方最近的可选字母（COUI 的 `hasValue`）。高度不足以容纳全部字母时，会按 COUITouchSearchView 的圆点等级把连续的字母折叠成 `·`；在圆点上拖动会依次经过它所隐藏的字母。低于最小等级时索引条不绘制任何内容。

## 属性

### AlphabetIndexBar

| 属性名                | 类型                   | 说明                               | 默认值                                            | 是否必须 |
| --------------------- | ---------------------- | ---------------------------------- | ------------------------------------------------- | -------- |
| onKeySelected         | (String) -> Unit       | 每次选中新字母时回调               | -                                                 | 是       |
| modifier              | Modifier               | 应用于索引条的修饰符               | Modifier                                          | 否       |
| selectedKey           | String?                | 未触摸时高亮的字母                 | null                                              | 否       |
| keys                  | List\<String\>         | 自上而下的索引字母                 | AlphabetIndexBarDefaults.Keys                     | 否       |
| availableKeys         | Set\<String\>?         | 可选中的字母，`null` 表示全部      | null                                              | 否       |
| showCollectKey        | Boolean                | 是否以收藏星标开头                 | true                                              | 否       |
| hapticFeedbackEnabled | Boolean                | 切换字母时是否触发触感反馈         | true                                              | 否       |
| colors                | AlphabetIndexBarColors | 索引条与气泡颜色                   | AlphabetIndexBarDefaults.alphabetIndexBarColors() | 否       |

### AlphabetIndexBarDefaults

| 常量            | 类型           | 默认值       | COUI 来源                                                 |
| --------------- | -------------- | ------------ | --------------------------------------------------------- |
| CollectKey      | String         | "*"          | star_character                                            |
| Keys            | List\<String\> | A 到 Z、"#"  | normal_touchsearch_keys                                   |
| Width           | Dp             | 39.dp        | coui_touchsearch_background_width                         |
| RowHeight       | Dp             | 16.dp        | coui_touchsearch_each_item_height 14dp + item_spacing 2dp |
| VerticalPadding | Dp             | 16.dp        | coui_touchsearch_padding_top / _bottom                    |
| CollectIconSize | Dp             | 12.dp        | coui_touchsearch_collect_normal                           |
| KeyTextSize     | Dp             | 12.dp        | coui_touchsearch_key_textsize（dp，不随字体缩放）         |
| DotTextSize     | Dp             | 20.dp        | coui_touchsearch_key_dot_textsize                         |
| BubbleSize      | Dp             | 48.dp        | coui_touchsearch_popup_first_default_width / _height      |
| BubbleGap       | Dp             | 8.5.dp       | popupwin_right_margin 0.5dp + touch_end_gap 8dp           |

### `alphabetIndexBarColors()` 工厂

| 参数               | 类型  | 默认值                                   | COUI 来源                                           |
| ------------------ | ----- | ---------------------------------------- | --------------------------------------------------- |
| keyColor           | Color | COUITheme.colorScheme.labelSecondary | coui_touchsearchview_text_color（couiColorSecondNeutral） |
| selectedKeyColor   | Color | COUITheme.colorScheme.primaryTextOnPopup | 按下态 couiColorPrimaryTextOnPopup                  |
| dotColor           | Color | COUITheme.colorScheme.dragHandle         | coui_touchsearchview_dot_color（couiColorControls） |
| bubbleColor        | Color | COUITheme.colorScheme.backgroundTop      | coui_touch_search_popup_bg（couiColorBackgroundTop） |
| bubbleContentColor | Color | COUITheme.colorScheme.labelPrimary       | couiColorPrimaryNeutral                             |

## 行为

- 字母为 12dp 中粗文字，在 16dp 顶部留白之下逐行居中于 16dp 行高；星标为 12dp 描边图标。空间充足时索引条按全部字母所需高度包裹，空间不足时折叠出圆点。
- 选中的字母、星标或圆点变为 `selectedKeyColor`，松手后保持高亮，与 COUI 一致。
- 48dp 气泡以 20sp 中粗显示字母，字体缩放上限 1.35（`COUIChangeTextUtil.getSuitableFontSize` 第 4 级）（星标为 24dp 图标），与字母行垂直居中，距索引条 8.5dp。气泡在 350ms 内按 COUIMoveEaseInterpolator 从 0.8 放大并淡入，松手一秒后在 300ms 内缩回 0.8 并淡出。
- 切换到另一个字母时触发一次强度随拖动速度变化的触感，与 COUI 的自适应振动一致（`couiAdaptiveVibrator`，默认开启，`performAdaptiveFeedback`）：手指静止时为一半强度，随速度线性增强，到 8000px/s 时为满强度（`VibrateUtils.getVibratorValueWithLimit`，强度 50..100），且两次触感至少间隔 25ms（`filterVibrator`）。设备不支持按强度振动时播放完整的 COUI `GRANULAR_SHORT_VIBRATE_SYNC`（308）触感（`CouiHapticEffect.Strength`）。`selectedKey` 变化不触发触感。每个可选字母也作为无障碍自定义操作提供。
