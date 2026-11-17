# DialogButtonBar

`DialogButtonBar` 是 COUI 警告对话框的按钮栏，对标 ColorOS 17 的 `COUIButtonBarLayout`。COUI 17 的按钮栏由**填充胶囊按钮**（`COUILoadingButton`，样式 `COUIAlertDialogBottomButtonSecondary`）组成，取代了旧版无边框的通栏单元格，也不再有分割线。它最核心的行为是**自动纵排**：按钮栏会测量自己的文案，一旦某个文案宽到放不进单元格，整栏就从横排翻成纵排，而不是把文字挤压或截断。

横排与纵排的按钮**顺序不同**，且最底部的单元格承担面板的底部留白。

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
    title = "对话框标题",
    summary = "带 COUI 按钮栏的对话框。",
    onDismissRequest = { showDialog = false },
) {
    DialogButtonBar(
        negative = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
        positive = DialogButtonBarAction(text = "确定", onClick = { showDialog = false }),
    )
}
```

两个短文案会并排显示。文案过长时按钮栏会自动翻成纵排，无需额外配置：

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "暂不", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(
        text = "删除备份以及本地的全部副本",
        onClick = { showDialog = false },
    ),
)
```

也支持第三个中性动作，它以错误色绘制（`COUIAlertDialogBottomButtonSecondary.Last`）。COUI 对三按钮栏**一律**纵排，不论文案多短：

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "保存", onClick = { showDialog = false }),
    neutral = DialogButtonBarAction(text = "放弃", onClick = { showDialog = false }),
)
```

### 推荐按钮

把某个动作设为 `recommended = true` 即得到 COUI 的推荐档（`COUIAlertDialogBuilder.setPositiveButton(text, listener, isRecommend = true)`）。`COUIButtonBarLayout.applyRecommendStyle` 会把该按钮改为主色胶囊——不透明主题色填充 + 白色文字，其余按钮仍为次要按钮。布局与顺序不变。

```kotlin
DialogButtonBar(
    negative = DialogButtonBarAction(text = "暂不", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(
        text = "开启",
        recommended = true,
        onClick = { showDialog = false },
    ),
)
```

## 纵排判定规则

按钮栏复刻 `COUIButtonBarLayout.onMeasure`：**只有**恰好两个按钮且每个文案都放得进单元格时才保持横排。三按钮与单按钮都会走纵排。

单元格宽度来自 `getHorizontalButtonContentWidth`：按钮栏宽度（先钳到 `DialogDefaults.MaxWidth`，即 `coui_dialog_max_width` 392dp）按按钮数均分，再减去按钮两侧内边距以及单元格的外侧与内侧内边距：

```
available = min(392.dp, barWidth) / buttonCount
            - ButtonHorizontalPadding * 2 - CellPaddingEdge - CellPaddingCentral
```

文案宽于 `available` 即触发纵排。文案以单行、按解析后的 `textStyle` 用 `TextMeasurer` 测量，与 COUI 的原始画笔测量一致。

设置 `dynamicLayout = false` 可固定为横排，等同 `COUIButtonBarLayout.setDynamicLayout(false)`。

## 顺序与间距

两档的按钮顺序相反，与 `resortButton` 一致：

| 档位 | 顺序 |
| :--- | :--- |
| 横排 | 否定 → 中性 → 肯定（从左到右） |
| 纵排 | 中性 → 肯定 → 否定（从上到下） |

每个按钮放在带内边距的单元格中（`setHorButtonsParentPadding` / `setVerButtonsParentPadding`）：

| 档位 | 单元格内边距 |
| :--- | :--- |
| 横排 | 第一格 16 / 4 / 4 / 16dp，第二格 4 / 4 / 16 / 16dp（起 / 上 / 止 / 下）：按钮间距 8dp |
| 纵排 | 左右各 16dp，上 4dp，下 4dp（最底部一格为 16dp）：按钮间距 8dp |

按钮高 44dp（纵排文案换行时为 56dp），水平内边距 12dp、垂直 4dp，胶囊形填充。

## 属性

### DialogButtonBar 属性

| 属性          | 类型                   | 说明                                           | 默认值                                        | 必须 |
| :------------ | :--------------------- | :--------------------------------------------- | :-------------------------------------------- | :--- |
| negative      | DialogButtonBarAction? | 否定（取消）动作；为 null 时隐藏               | -                                             | 是   |
| positive      | DialogButtonBarAction? | 肯定（确认）动作；为 null 时隐藏               | -                                             | 是   |
| modifier      | Modifier               | 应用于按钮栏的修饰符                           | Modifier                                      | 否   |
| neutral       | DialogButtonBarAction? | 中性（第三个）动作，以错误色绘制；为 null 时隐藏 | null                                          | 否   |
| dynamicLayout | Boolean                | 是否允许自动纵排；为 false 时固定横排          | true                                          | 否   |
| material      | Boolean                | 按钮是否绘制 COUI 17 材质；默认跟随所在对话框是否绘制材质面板（`materialStroke`） | LocalDialogMaterial.current | 否   |
| colors        | TextButtonColors       | 否定与肯定按钮的颜色                           | `material` 时为 materialButtonColors()，否则 buttonColors() | 否   |
| neutralColors | TextButtonColors       | 中性按钮的颜色                                 | `material` 时为 materialNeutralButtonColors()，否则 neutralButtonColors() | 否   |
| recommendedColors | TextButtonColors   | 推荐按钮的颜色（不论其角色）                   | recommendedButtonColors()                     | 否   |
| textStyle     | TextStyle              | 文案样式，同时用于纵排判定时的文案测量         | COUITheme.textStyles.buttonL                   | 否   |

### DialogButtonBarAction 属性

| 属性    | 类型       | 说明           | 默认值 | 必须 |
| :------ | :--------- | :------------- | :----- | :--- |
| text    | String     | 按钮文案       | -      | 是   |
| enabled | Boolean    | 按钮是否可用   | true   | 否   |
| recommended | Boolean | 是否为推荐动作，绘制为主色胶囊；每个按钮栏最多标记一个 | false | 否 |
| onClick | () -> Unit | 点击按钮时回调 | -      | 是   |

### DialogButtonBarDefaults 对象

横排档的按钮高度与内边距位于 `DialogDefaults.ButtonBarMinHeight`（44.dp）与 `DialogDefaults.ButtonBarInsideMargin`（水平 12dp、垂直 4dp）。

| 属性                            | 类型 | 说明                                             | 值    |
| :------------------------------ | :--- | :----------------------------------------------- | :---- |
| ButtonHorizontalPadding         | Dp   | 按钮的水平内边距（两档通用）                     | 12.dp |
| CellPaddingEdge                 | Dp   | 按钮单元格与面板侧边之间的内边距                 | 16.dp |
| CellPaddingCentral              | Dp   | 横排单元格内侧内边距（两个相加为 8dp 间距）      | 4.dp  |
| CellPaddingTop                  | Dp   | 所有单元格的上内边距；纵排非底部单元格的下内边距 | 4.dp  |
| CellPaddingBottom               | Dp   | 最底部单元格的下内边距（即面板底部留白）         | 16.dp |
| StackedButtonMinHeight          | Dp   | 纵排按钮的最小高度                               | 44.dp |
| StackedButtonMinHeightMultiline | Dp   | 文案换行的纵排按钮最小高度                       | 56.dp |
| LabelMaxLines | Int | 按钮文案最多行数, 超出省略 (`android:maxLines`) | 2 |

| 函数                  | 返回类型         | 说明                                                                 |
| :-------------------- | :--------------- | :------------------------------------------------------------------- |
| buttonColors()        | TextButtonColors | `colorScheme.dialogButton` 填充（`?couiColorContainer8`），`labelPrimary` 文字 |
| neutralButtonColors() | TextButtonColors | `colorScheme.dialogButton` 填充，`error` 文字（禁用时为 30% 透明度的错误色） |
| materialButtonColors() | TextButtonColors | 材质对话框填充：白色 alpha 166（浅色 `#A6FFFFFF`）/ 51（深色 `#33FFFFFF`）；禁用填充保持 `dialogButton` |
| materialNeutralButtonColors() | TextButtonColors | 材质填充 + `neutralButtonColors()` 的 `error` 文字 |
| recommendedButtonColors() | TextButtonColors | 推荐按钮：`primary` 填充（`?couiColorContainerTheme`，材质对话框中也保持不透明），`onPrimary` 文字；禁用时为 `disable` / `disabledOnPrimaryButton` |

## 行为说明

- **最底部单元格承担面板留白。** 它 16dp 的下内边距就是对话框的底部留白，面板自身无需再加底部内边距。
- **按钮有按压缩放。** `COUIAlertDialogBottomButtonSecondary` 保留 `scaleEnable=true`，按下时与其他 COUI 按钮一样缩小，并共享 `Button` 的拖动形变（橡皮筋跟手 + 拉伸）。
- **材质跟随对话框模糊。** `COUIButtonBarLayout.setBlurBackgroundWindow(blur)` 在窗口模糊时把按钮设为 `FOLLOW_SYSTEM`，否则为 `FORCE_DISABLE`。因此在 `materialStroke = true` 的 `OverlayDialog` / `WindowDialog` 中按钮栏会变为材质按钮：`drawableMaterialEffect` 的半透明白色填充、`CONTENT_CAPSULE_5` 描边光 + 内阴影（描边权重 2）、`TYPE_OPAQUE_MEDIUM_2` 跟手聚光，且没有按压着色。传入 `material = false` 可关闭。
- **推荐按钮保持不透明。** COUI 对按钮栏按钮保留 `drawableMaterialEffect = true`，因此材质对话框中推荐按钮仍是不透明主题色填充（从不采样背景），并使用 `CONTENT_CAPSULE_4` 描边光与 `TYPE_OPAQUE_MEDIUM_1` 跟手聚光。未开启 `material` 时则是带按压着色的普通主色胶囊。

