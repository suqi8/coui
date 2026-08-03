# DialogButtonBar

`DialogButtonBar` 是 COUI 警告对话框的按钮栏，对标 ColorOS 的 `COUIButtonBarLayout`。它最核心的行为是**自动纵排**：按钮栏会测量自己的文案，一旦某个文案宽到放不进单元格，整栏就从横排翻成纵排，而不是把文字挤压或截断。

横排与纵排不只是方向不同——两档的最小高度、内边距、分割线粗细与内缩都不一样，而且按钮顺序**完全相反**。

## 导入

```kotlin
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogButtonBarDefaults
```

## 基础用法

按角色而非位置传入动作——由按钮栏决定每个动作摆在哪：

```kotlin
OverlayDialog(
    show = showDialog,
    title = "Dialog Title",
    summary = "A dialog with a COUI button bar.",
    onDismissRequest = { showDialog = false },
) {
    DialogButtonBar(
        negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
        positive = DialogButtonBarAction(text = "Confirm", onClick = { showDialog = false }),
    )
}
```

两个短文案会并排显示。长文案会自动把整栏翻成纵排，无需任何额外配置：

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Not Now", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(
        text = "Delete Backup And Local Copies",
        onClick = { showDialog = false },
    ),
)
```

也支持第三个中性动作。COUI 对三按钮栏**一律**纵排，无论文案多短：

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "Save", onClick = { showDialog = false }),
    neutral = DialogButtonBarAction(text = "Discard", onClick = { showDialog = false }),
)
```

## 纵排判定规则

按钮栏严格复刻 `COUIButtonBarLayout.onMeasure`。**只有同时满足以下三条**才保持横排：

1. 所有文案都放得下，且
2. 恰好有两个按钮，且
3. 没有推荐按钮（本实现永远如此，见[未实现部分](#未实现部分)）。

其余任何情况都纵排。特别地，三按钮栏与单按钮栏都走纵排路径。

单元格宽度来自 `needSetButVertical`：栏宽（先夹到 `DialogDefaults.MaxWidth`，即 `coui_dialog_max_width` 392dp）减去分割线后按按钮数均分，每格再减去自己的两侧内边距：

```
available = (min(392.dp, barWidth) - (buttonCount - 1) * StackedDividerThickness) / buttonCount
            - ButtonHorizontalPadding * 2
```

任一文案宽于 `available` 即触发翻转。文案用 `TextMeasurer` 按解析后的 `textStyle` 单行测量，对应 COUI 的原始 paint 测量。

::: tip
该公式里减掉的是**纵排**档的 0.33dp 分割线厚度，即使这一栏最终画的是更粗的 1dp 横排分割线也一样。这是 `COUIButtonBarLayout` 的原始行为，此处如实复刻。
:::

传 `dynamicLayout = false` 可锁定横排，等同于 `COUIButtonBarLayout.setDynamicLayout(false)`。

## 顺序与分割线

按钮顺序在两档之间反转，对应 `resortButton`：

| 档位 | 顺序 |
| :--- | :--- |
| 横排 | negative → neutral → positive（从左到右） |
| 纵排 | neutral → positive → negative（从上到下） |

所以否定（取消）动作横排时在**最左**，纵排时在**最下**。

分割线也不同：

| 档位 | 粗细 | 内缩 |
| :--- | :--- | :--- |
| 横排 | 1dp（`DialogDefaults.ButtonBarDividerThickness`） | 上 17dp / 下 21dp |
| 纵排 | 0.33dp（`DialogButtonBarDefaults.StackedDividerThickness`） | 左右各 24dp |

相邻的两个存在按钮之间会画一条分割线。传 `showDivider = false` 可全部隐藏（对应 `buttonBarShowDivider` 属性）。

## 属性

### DialogButtonBar 属性

| 属性            | 类型                   | 说明                                             | 默认值                                      | 必需 |
| :-------------- | :--------------------- | :----------------------------------------------- | :------------------------------------------ | :--- |
| negative        | DialogButtonBarAction? | 否定（取消）动作，为 null 时隐藏                 | -                                           | 是   |
| positive        | DialogButtonBarAction? | 肯定（确认）动作，为 null 时隐藏                 | -                                           | 是   |
| modifier        | Modifier               | 应用于按钮栏的修饰符                             | Modifier                                    | 否   |
| neutral         | DialogButtonBarAction? | 中性（第三个）动作，为 null 时隐藏               | null                                        | 否   |
| dynamicLayout   | Boolean                | 是否允许自动纵排，false 则锁定横排               | true                                        | 否   |
| showDivider     | Boolean                | 是否显示按钮之间的分割线                         | true                                        | 否   |
| hasContentAbove | Boolean                | 栏上方是否有标题、正文或自定义面板               | true                                        | 否   |
| colors          | TextButtonColors       | 按钮配色                                         | ButtonDefaults.textButtonColorsBorderless() | 否   |
| dividerColor    | Color                  | 分割线颜色                                       | COUITheme.colorScheme.dividerLine           | 否   |
| textStyle       | TextStyle              | 文案样式，同时用于纵排判定时测量文案             | COUITheme.textStyles.button                 | 否   |

::: warning
`hasContentAbove` 影响间距：只有当栏上方什么都没有时，COUI 才给最上面那个纵排按钮加 6dp 额外上内边距。带标题或正文的普通对话框保持 `true` 即可。
:::

### DialogButtonBarAction 属性

| 属性    | 类型       | 说明             | 默认值 | 必需 |
| :------ | :--------- | :--------------- | :----- | :--- |
| text    | String     | 按钮文案         | -      | 是   |
| enabled | Boolean    | 按钮是否启用     | true   | 否   |
| onClick | () -> Unit | 点击按钮时的回调 | -      | 是   |

### DialogButtonBarDefaults 对象

纵排档的度量以及两档共用的值。横排档自身的度量在 `DialogDefaults.ButtonBar*`。

| 属性                            | 类型 | 说明                                             | 值      |
| :------------------------------ | :--- | :----------------------------------------------- | :------ |
| ButtonHorizontalPadding         | Dp   | 按钮水平内边距，两档通用                         | 24.dp   |
| StackedButtonMinHeight          | Dp   | 纵排按钮最小高度                                 | 52.dp   |
| StackedButtonMinHeightBottom    | Dp   | 最底部纵排按钮的最小高度（52dp + 12dp 额外）     | 64.dp   |
| StackedButtonPaddingVertical    | Dp   | 纵排按钮垂直内边距                               | 14.dp   |
| StackedButtonPaddingTopExtra    | Dp   | 上方无内容时，最顶部纵排按钮的额外上内边距       | 6.dp    |
| StackedButtonPaddingBottomExtra | Dp   | 最底部纵排按钮的额外下内边距（承载面板底部留白） | 12.dp   |
| StackedBarMarginTop             | Dp   | 含多个按钮的纵排栏的上外边距                     | 16.dp   |
| StackedDividerThickness         | Dp   | 纵排按钮之间分割线的厚度                         | 0.33.dp |
| StackedDividerInsetHorizontal   | Dp   | 纵排按钮之间分割线的水平内缩                     | 24.dp   |
| ButtonCornerRadius              | Dp   | 按钮圆角（整格直角矩形）                         | 0.dp    |

## 行为说明

- **底部按钮承载面板留白。** 最底部的纵排按钮拿到 12dp 额外下内边距和 64dp 最小高度，所以对话框面板本身不需要再加底部内边距。
- **多按钮纵排栏有 16dp 上外边距。** 单个纵排按钮没有。
- **单独一个否定按钮回退到横排度量。** 这是纵排里唯一一种 `resetVerButsPadding` 改用 58dp 面板最小高度与横排内边距、而非纵排那套的情况。
- **按钮是直角且无按压缩放。** `COUIAlertDialogBottomButtonNewNormal` 设了 `drawableRadius=0dp`、`scaleEnable=false`、`stateListAnimator=@null`：唯一的按压反馈是整格的着色。

## 未实现部分

**推荐按钮**一档尚未实现。在 ColorOS 里，`COUIButtonBarLayout.setRecommendButtonId` 会把某个按钮提升为高亮填充的主按钮（44dp 高，另有自己的内边距与外边距，且隐藏所有分割线）。本按钮栏始终按 `mRecommendButtonId == NO_RECOMMEND_ID` 行事，也就是标准警告对话框路径。
