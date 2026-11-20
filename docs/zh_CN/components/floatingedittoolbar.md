# FloatingEditToolbar

`FloatingEditToolbar` 是 COUI 17 的悬浮编辑栏 (`COUIBottomFloatingEditToolbar`): 列表进入多选后显示在底部的编辑操作胶囊。每个 `FloatingEditToolbarItem` 由图标和文字组成; 编辑栏负责排布条目、分发点击, 并从下方滑入滑出。

编辑栏按自身宽度响应式排布。宽度小于 600dp 时为 56dp 高的胶囊, 条目为 24dp 图标在上、10dp 文字在下; 600dp 及以上为 44dp 高的胶囊, 条目为图标在左、14dp 文字在右 (最多两行)。放不下时所有条目等比收窄; 没有溢出菜单。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingEditToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbar
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarItem
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarDefaults
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarColors
```

## 基本用法

```kotlin
var selecting by remember { mutableStateOf(false) }

Scaffold(
    bottomBar = {
        FloatingEditToolbar(visible = selecting) {
            FloatingEditToolbarItem(onClick = { /* 分享 */ }, icon = COUIIcons.Share, label = "分享")
            FloatingEditToolbarItem(onClick = { /* 复制 */ }, icon = COUIIcons.Copy, label = "复制")
            FloatingEditToolbarItem(onClick = { /* 删除 */ }, icon = COUIIcons.Delete, label = "删除")
        }
    }
) { paddingValues ->
    // 内容
}
```

- 手指按下与抬起落在同一条目上、且中途没有滑出该条目超过触摸阈值 (`View.onTouchEvent`) 时触发该条目点击。胶囊整体带 COUI 按压缩放、橡皮筋跟手与形变以及触摸光斑; 条目本身没有按压蒙层。所有条目都禁用时胶囊不再给触摸反馈。
- `visible = false` 时胶囊以弹簧 (response 0.38, bounce 0.25) 滑出到编辑栏底边以下, 不做淡出。隐藏后仍占位, 但不接收触摸。

## 属性

### FloatingEditToolbar 属性

| 属性名                     | 类型                   | 说明                                                                 | 默认值                                      | 必需 |
| -------------------------- | ---------------------- | -------------------------------------------------------------------- | ------------------------------------------- | ---- |
| modifier                   | Modifier               | 应用于整宽容器的修饰符                                               | Modifier                                    | 否   |
| backgroundModifier         | Modifier               | 作用于胶囊裁剪内部, 位于 `color` 之上、条目之下                      | Modifier                                    | 否   |
| visible                    | Boolean                | 是否显示胶囊; 变化时滑入 / 滑出                                      | true                                        | 否   |
| materialEffect             | Boolean                | 绘制 COUI 17 边缘光、光斑, 以及 (存在 `LocalMaterialBackdrop` 时) 背景模糊 | `LocalCOUIMaterialEffectEnabled.current` | 否   |
| defaultWindowInsetsPadding | Boolean                | 是否让胶囊避开导航栏与标题栏                                         | true                                        | 否   |
| color                      | Color                  | 胶囊填充色; 存在背景模糊提供者时被模糊取代                           | FloatingEditToolbarDefaults.color()         | 否   |
| shadowElevation            | Dp                     | 投影模糊半径; 0 关闭投影                                             | FloatingEditToolbarDefaults.ShadowElevation | 否   |
| content                    | @Composable () -> Unit | 若干 `FloatingEditToolbarItem`                                       | -                                           | 是   |

### FloatingEditToolbarItem 属性

| 属性名   | 类型                      | 说明                                                        | 默认值                                   | 必需 |
| -------- | ------------------------- | ----------------------------------------------------------- | ---------------------------------------- | ---- |
| onClick  | () -> Unit                | 点击条目时回调                                              | -                                        | 是   |
| icon     | ImageVector               | 条目图标, 以内容色着色                                      | -                                        | 是   |
| label    | String                    | 条目文字, 同时作为无障碍标签                                | -                                        | 是   |
| modifier | Modifier                  | 应用于条目的修饰符                                          | Modifier                                 | 否   |
| enabled  | Boolean                   | 是否可用; 禁用时忽略点击并隐藏角标                          | true                                     | 否   |
| badge    | Int?                      | `null` 无角标, 0 为红点, 正数为数字角标                     | null                                     | 否   |
| colors   | FloatingEditToolbarColors | 内容颜色                                                    | FloatingEditToolbarDefaults.itemColors() | 否   |

### FloatingEditToolbarDefaults 对象

| 属性名                            | 类型 | 说明                                       | 值     |
| --------------------------------- | ---- | ------------------------------------------ | ------ |
| CornerRadius                      | Dp   | 全圆角柔和胶囊                             | COUIRoundCorner.Full |
| MinHeight                         | Dp   | 胶囊居中所在的最小高度                     | 88.dp  |
| CompactWidthBreakpoint            | Dp   | 低于该宽度使用紧凑 (图标在上) 样式         | 600.dp |
| ExpandedWidthBreakpoint           | Dp   | 达到该宽度使用大屏两侧边距                 | 840.dp |
| HorizontalOutSidePaddingCompact   | Dp   | 紧凑宽度两侧边距                           | 16.dp  |
| HorizontalOutSidePaddingMedium    | Dp   | 中等宽度两侧边距                           | 24.dp  |
| HorizontalOutSidePaddingExpanded  | Dp   | 大屏宽度两侧边距                           | 40.dp  |
| HeightCompact                     | Dp   | 紧凑样式胶囊高度                           | 56.dp  |
| HeightMediumLarge                 | Dp   | 宽样式胶囊高度                             | 44.dp  |
| InnerPaddingVertical              | Dp   | 紧凑样式胶囊内边距                         | 12.dp  |
| InnerPaddingHorizontal            | Dp   | 宽样式胶囊内边距                           | 10.dp  |
| ItemSpacing                       | Dp   | 条目间距                                   | 8.dp   |
| ItemMaxWidthVertical              | Dp   | 紧凑样式条目宽度                           | 70.dp  |
| ItemMaxWidthHorizontal            | Dp   | 宽样式条目宽度                             | 98.dp  |
| SingleItemWidthVertical           | Dp   | 紧凑样式单条目宽度                         | 148.dp |
| SingleItemWidthHorizontal         | Dp   | 宽样式单条目宽度                           | 152.dp |
| ItemHeightVertical                | Dp   | 紧凑样式条目高度                           | 48.dp  |
| ItemHeightHorizontal              | Dp   | 宽样式条目高度                             | 40.dp  |
| IconSize                          | Dp   | 图标尺寸                                   | 24.dp  |
| IconTopPadding                    | Dp   | 紧凑样式图标距条目顶部                     | 4.dp   |
| LabelTopSpacing                   | Dp   | 紧凑样式图标与文字间距                     | 2.dp   |
| IconLabelSpacingHorizontal        | Dp   | 宽样式图标与文字间距                       | 4.dp   |
| LabelTextSizeVertical             | Dp   | 紧凑样式文字大小 (不随字体缩放)            | 10.dp  |
| LabelTextSizeHorizontal           | Dp   | 宽样式文字大小 (不随字体缩放)              | 14.dp  |
| ShadowElevation                   | Dp   | 焦散投影模糊半径                           | 24.dp  |

| 函数名               | 返回类型                  | 说明                                                         |
| -------------------- | ------------------------- | ------------------------------------------------------------ |
| color()              | Color                     | 不透明的 `?couiColorBar` 填充                                |
| backdropSpec(isDark) | MaterialBackdropSpec      | COUI 17 底栏材质 `TYPE_FRAMEWORK_BOTTOM_BAR`                 |
| itemColors()         | FloatingEditToolbarColors | 可用 `?couiColorLabelPrimary`, 禁用 `?couiColorLabelTertiary` |

### FloatingEditToolbarColors 属性

| 属性名               | 类型  | 说明                     |
| -------------------- | ----- | ------------------------ |
| contentColor         | Color | 可用条目的图标与文字颜色 |
| disabledContentColor | Color | 禁用条目的图标与文字颜色 |

## 进阶用法

### 禁用条目与角标

```kotlin
FloatingEditToolbar {
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Share, label = "分享", badge = 0)
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Copy, label = "复制", enabled = false)
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Delete, label = "删除", badge = 3)
}
```

### 材质模糊

在编辑栏外层提供 `LocalMaterialBackdrop` (例如来自 coui-blur): 胶囊会用 25px 底栏背景模糊代替 `color`。模糊绘制在胶囊自身图层内、边缘光之下, 因此深浅色下边缘光都叠加在模糊之上。传 `materialEffect = false` 得到普通胶囊。
