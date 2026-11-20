# 升级指南

## 2.0.0 → 2.1.0

### 扩展图标改为 ColorOS 17 的图标

`coui-icons` 中的图标现在是 ColorOS 17 自己绘制的图标，取自 ColorOS 系统应用。它们只有一种粗细，因此移除了
`COUIIcons.Light`、`Normal`、`Regular`、`Medium` 和 `Demibold`，改用 `COUIIcons.<Name>`；图标名称不变。

```kotlin
// 之前
Icon(imageVector = COUIIcons.Light.Settings, contentDescription = "Settings")

// 之后
Icon(imageVector = COUIIcons.Settings, contentDescription = "Settings")
```

新图标的形状有变化，升级后请检查布局中的图标尺寸。

### 动效与触摸对齐 COUI 17

所有组件的动效又和 COUI 17 源码逐项对照了一遍。大部分改动无需你调整代码；下面列出的是会改变现有行为、或新增 API 的部分。

**行为变化**

- 列表行、卡片和按钮在手指按下时就显示按压反馈（滚动列表里也是），松手时保留按下的动量。`couiPressScale(enabled = false)`
  改为弹簧回弹，不再瞬间复位。
- 弹出菜单在整个按住滑动过程中都会跟手形变，内部列表开始滚动后也不中断，并从朝向锚点的一侧伸展。弹出菜单行标题为 15sp（`couiTextBodyL`）。
- 可折叠顶栏用弹簧跟随滚动，吸附也改用弹簧（`snapAnimationSpec` 默认值改为 `TopAppBarDefaults.SnapAnimationSpec`）。
  `COUIScrollBehavior(followScroll = false)` 可恢复 1:1 跟手；`TopAppBarState.displayedHeightOffset` 是顶栏实际绘制的位置。
- 越界状态下松手只回弹；速度超过 6000px/s 的快速滑动仍会把速度带过边缘。
- `NavDisplay`（coui-navigation3-ui）与 `NavTransitions.COUIDefault`（coui-nav）的默认翻页改为 COUI 曲线 350ms，被覆盖页平移
  30%，预测性返回用弹簧收尾。coui-nav 的 `NavDisplayEffects.cornerClipRadius` 默认跟随屏幕圆角。
- `allowDismiss = false` 时，底部面板可以跟手下拉到 82dp 的收起态，遮罩随之变暗，与 COUI 一致。
- `ListPreference` / `MultiSelectListPreference` 改为弹出 COUI 选择对话框，不再是底部面板；行末改为跳转箭头，打开时不再振动。
  `ListPreferenceDefaults.CheckIconSize`、`ButtonBarTopPadding`、`ButtonBarBottomPadding` 不再被读取。
- `SwitchLoadingPreference`：按在开关上即点击整行（开关不再单独点按或拖动），`checked` 翻转时才振动。`CheckboxPreference`
  在手指按下时振动。
- Snackbar 撑满可用宽度：手机上为窗口两侧各留 16dp，宽屏上为 6 列栅格。
- 拖动 `Switch` 时滑块 1:1 跟手。
- `searchBarCollapseGeometry` 的胶囊加宽量改为 16dp（原为 12dp）。
- 日历选择器的箭头直接切换，不再旋转，与 COUI 的 rotate view 一致。
- 禁用态的跳转箭头和下拉箭头改用 `labelQuaternary`。
- `NavigationRailItem` 改用 COUI 导航栏配色（`NavigationRailDefaults.itemColors()`），移除
  `NavigationRailDefaults.SelectedPressedAlpha`、`UnselectedPressedAlpha`、`UnselectedAlpha`。

**新增 API**

- `CircularPressIndication(radius, color, hoverColor, focusColor)` 改为公开，另有使用主题色的
  `rememberCircularPressIndication(...)`。
- `COUITouchInteraction` 与 `Modifier.couiTouchFeedback(interactionSource, enabled)`：在手指按下时上报触摸。
- `couiSpring(response, bounce, threshold)` 与 `COUISpringEasing(response, bounce)`。
- `COUIFlingBehavior` / `rememberCOUIFlingBehavior()`：COUI 17 列表惯性滚动（`SpringOverScroller`）。
- `RotatingProgressIndicator`、`CompProgressIndicator`、`LoadingPreferenceCategory` 新增 `paused`；
  `ProgressIndicatorDefaults.smallRotatingProgressIndicatorColor()`；`InstallLoadProgress(hapticFeedbackEnabled)`。
- `NavigationBar(targetState) { state -> }` 为整组菜单切换加动画；`NavigationRailItem(badge, colors)` 与
  `NavigationRailItemColors`；`FloatingAccessoryNavigationBar(visible)`——`visible` 位于 `backgroundModifier`
  之后，请按名称传参。
- `ChipTabContent`（COUI 胶囊标签切页转场）与 `ChipGroup(animationsEnabled)`。
- 浮动工具栏的项可传 `key`；`ExpandableFloatingActionButton(collapseOnItemClick = true)` 点按项后收起菜单。
- `IntentSlider(holdToSpeedUp, onDragHorizontalOffsetChange)`。
- `OverlayContextMenu` / `WindowContextMenu(anchorInteractionSource)`。
- `ArrowPreference(selected, startBadge, endBadgeCount, endIcon)`。
- `SidePaneLayout(onMaskClick)`。
- coui-nav：`COUIPageMotion`、`COUIPageEasing`、`COUI_PAGE_DURATION_MILLIS`、`COUI_COVERED_SHIFT_FRACTION`、
  `COUI_GESTURE_COVERED_SHIFT_FRACTION`。

## 1.1.0 → 2.0.0

2.0.0 把 COUI 从 ColorOS 16 设计（COUI 1.x）升级到 ColorOS 17（COUI 17）。如果是从 Miuix 迁移过来，请先看[从 Miuix 迁移](./migration.md)。

### 弹出菜单默认模糊

列表弹窗、级联菜单与下拉 / Spinner 弹窗现在默认使用 COUI 17 弹窗材质（160px 模糊、glow overlay
混合、阴影描边、跟手光斑），即 ColorOS 17 的观感；COUI 自身的弹窗模糊需要应用主动开启。模糊由新增的
`LocalPopupMaterialBlur` 提供者（`MaterialBlurProvider`、`MaterialBlurSpec`）绘制；未提供时弹窗保持
不透明表面，因此在提供之前外观不变。

- `OverlayListPopup`、`WindowListPopup`、`OverlayCascadingListPopup`、`WindowCascadingListPopup`、
  `OverlayDropdownPopup` 与 `WindowDropdownPopup` 新增 `materialBlur` 参数（默认 `true`），传 `false`
  即可关闭。基于它们的菜单与偏好项跟随提供者；在其外层提供 `LocalPopupMaterialBlur provides null` 即可关闭。
- `materialEffect` 默认改为 `true`，且只在模糊表面上绘制（默认模糊，或半透明 `backgroundColor` 配合自定义的
  模糊 `backgroundModifier`）。
- `backgroundColor` 是非模糊时的填充色；使用默认模糊的弹窗会忽略它。
- 对话框与底部弹窗会为其内容重置提供者，在面板上打开的菜单保持不透明。

### TabRow 改为滑动标签栏

普通 `TabRow` 过去绘制的是去掉容器的分段按钮，COUI 并没有这种形态。现在它是 ColorOS 的滑动标签栏（`COUITabLayout`）：48dp 高的文字标签行，底部 2dp 轨道，选中标签下方是 2dp 下划线指示器。`TabRowWithContour` 保持不变，仍是分段按钮。

代码仍能编译，但外观和行为有变化：

- 默认高度为 48dp（原为 40dp），有标签同时带图标和文字时为 72dp。
- `colors` 默认取自 `TabRowDefaults.underlineColors()`：`backgroundColor` 是轨道，`selectedBackgroundColor` 是指示器。只有想在新布局上沿用分段配色时，才需要显式传 `TabRowDefaults.tabRowColors()`。
- `cornerRadius` 现在决定 8dp 按压遮罩的圆角，不再是胶囊指示器；`minWidth` 默认 0dp，`maxWidth` 默认为行宽的 70%。
- `itemSpacing` 表示标签之间的最小间距；与新增的 `horizontalMargin` 一样，不指定时随 COUI 屏幕尺寸变化。
- 按压遮罩来自 `LocalIndication`，传 `indication = null` 可去掉。
- 标签放不下时两端渐隐，并带 COUI 的橡皮筋越界回弹。

新增：`enabled`、`pagerState`（拖动 Pager 时标签行跟随），以及接收 `TabRowItem` 的 `TabRow(items = ...)` 重载，可设置图标、红点和禁用状态。需要旧外观时，请改用 `backgroundColor` 透明的 `TabRowWithContour`。

### Tooltip 改为 COUI 17 tooltip

Miuix 版 tooltip（无箭头、Material 风格的 `PlainTooltip` / `RichTooltip`）已替换为 `COUIToolTips` 的移植。
新的入口是 `TooltipBox(state, tooltip)`，配合 COUI 的三种气泡样式之一：`Tooltip`（带关闭图标的强调色气泡，
或 `mode = TooltipMode.Info` 的白色气泡）、`IconTooltip` 和 `ImageTooltip`。每个气泡都会画出指向锚点的箭头，
从箭头处以弹簧动画长出，并按 COUI 规则定位（默认在上方，距窗口边缘 16dp）。

旧 API 已移除：`TooltipBox(positionProvider, …)`、`TooltipBox(text, …)`、`RichTooltipBox`、
`PlainTooltip`、`RichTooltip`、`RichTooltipColors`、`TooltipDefaults.rememberTooltipPositionProvider`、
`TooltipDefaults.caretShape()` / `caretSize`，以及 `PlainTooltip*` / `RichTooltip*` 常量。迁移方式：

```kotlin
// 之前
TooltipBox(text = "Edit") { anchor() }

// 之后
val state = rememberTooltipState()
TooltipBox(
    state = state,
    tooltip = { Tooltip(text = "Edit", mode = TooltipMode.Info) },
    enableUserInput = true, // 保留原来的长按 / 悬停触发
) { anchor() }
```

`RichTooltipBox(title, text, actionText)` 对应 `IconTooltip(title = …, text = …, dismissText = …)` 或 `ImageTooltip`。

行为变化：

- `rememberTooltipState()` 现在默认 `isPersistent = true`：COUI tooltip 没有超时。需要自动消失时传 `isPersistent = false`。
- 新 `TooltipBox` 默认 `enableUserInput = false`（COUI 以编程方式显示 tooltip）且 `dismissOnTouchOutside = false`；
  如需保留旧的长按 / 点击外部关闭，请显式设置。
- 新气泡放进已弃用的 `TooltipBox(positionProvider, …)` 仍能渲染，但不画箭头。
- 锚定在 `TopAppBar`、`SmallTopAppBar` 或 `NavigationBar` 内的 tooltip 使用 COUI 工具栏 / 导航栏间距；锚点离开组合、滚出视野或尺寸变为 0 时，tooltip 直接消失，不播放退场动画。

### 兼容 API 已移除

| 已移除 | 替代 |
| :----- | :--- |
| `SpinnerEntry`、`SpinnerColors`、`SpinnerDefaults`、`SpinnerItemImpl` | `DropdownItem`、`DropdownColors`、`DropdownDefaults`、`DropdownImpl` |
| `DropdownItem(icon, title, summary)` 构造函数 | `DropdownItem(text = …, icon = …, summary = …)` |
| `DropdownImpl(isFirst, isLast)`、`DropdownHeaderItem(isFirst)` | 去掉这些参数；弹窗各行内边距一致 |
| `DropdownDefaults.FirstLastVerticalPadding` | `DropdownDefaults.MiddleVerticalPadding` |
| `DropdownDefaults.PopupHeaderItemMinHeight` | 无；分组标题行没有最小高度 |
| `ButtonDefaults.PressedScale` / `PressedBrightness`、`FloatingActionButtonDefaults.PressedScale` | 无；按压缩放由按钮尺寸决定 |
| `SquircleDefaults.Extension` / `ExtensionMin` / `ExtensionMax` | `SquircleDefaults.Weight` / `WeightMin` / `WeightMax` |

### 色槽与文本样式改用官方命名

`Colors` 与 `TextStyles` 的槽位改用 COUI 官方属性名（`couiColorXxx` → `xxx`，`couiTextXxx` → `xxx`）。与官方槽位同值的旧槽并入官方槽位，
无人使用的旧槽直接删除，且不保留弃用别名，请按下表改名。用位置参数调用 `Colors(...)` 构建的配色需要改成命名参数。
`TextStyles(...)` 与 `defaultTextStyles(...)` 只接受官方字阶，全部带默认值。

| 旧色槽 | 新色槽 |
| :----- | :----- |
| `onBackground`、`onSurface`、`onSurfaceContainer`、`onSecondaryVariant` | `labelPrimary` |
| `onBackgroundVariant`、`onSurfaceSecondary` | `labelSecondary` |
| `onSurfaceVariantSummary`、`onSurfaceContainerVariant` | `labelSecondaryVariant` |
| `onSurfaceVariantActions`、`onSurfaceContainerHigh`、`sliderKeyPoint` | `labelTertiary` |
| `disabledOnSurface`、`disabledOnSecondaryVariant` | `disabledNeutral` |
| `disabledPrimary`、`disabledPrimaryButton`、`disabledPrimarySlider` | `disable` |
| `onSecondary`、`sliderKeyPointForeground` | `labelOnColor` |
| `onTertiaryContainer` | `primaryText` |
| `tertiaryContainer` | `secondary` |
| `surface` | `backgroundWithCard` |
| `background` | 对话框、底部面板、弹出菜单等浮在页面上的面板用 `surface`，其余用 `backgroundElevated` |
| `surfaceContainer` | `cardBackground` |
| `surfaceContainerHigh` | `cardPressed` |
| `dividerLine` | `divider` |
| `windowDimming` | `mask` |
| `textFieldError` | `errorTextBg` |
| `primaryVariant` | 已删除，改用 `primary` |
| `primaryContainer` | 已删除，改用 `containerTheme` |
| `onPrimaryVariant`、`onPrimaryContainer` | 已删除，改用 `labelOnColor` |
| `surfaceVariant` | 已删除，改用 `card` |
| `errorContainer`、`onErrorContainer`、`disabledOnPrimary`、`disabledSecondary`、`disabledOnSecondary`、`onSecondaryContainer`、`secondaryContainerVariant`、`onSecondaryContainerVariant`、`tertiaryContainerVariant`、`onSurfaceContainerHighest`、`secondary`（灰色 `#E5E5E5` / `#757575`） | 已删除 |

| 旧文本样式 | 新文本样式 |
| :--------- | :--------- |
| `main`、`body1` | `bodyXL` |
| `paragraph` | `bodyL` |
| `body2` | `bodyM` |
| `button` | `buttonL` |
| `footnote1` | `bodyS` |
| `footnote2` | `bodyXS` |
| `headline1`、`headline2` | `headlineXS` |
| `subtitle` | `buttonM` |
| `title1` | `displayS` |
| `title2` | `displayXS` |
| `title3` | `headlineM` |
| `title4` | `headlineS` |

以下名称的含义或数值变了，代码仍能编译，但观感会不同：

| 槽位 | 之前 | 现在 |
| :--- | :--- | :--- |
| `background` | 面板白，`#FFFFFF` / `#1E1E1E` | `couiColorBackground`，`#FFFFFF` / `#000000` |
| `surface` | 灰色页面底色，`#F0F1F2` / `#000000` | `couiColorSurface`，面板色 `#FFFFFF` / `#1E1E1E` |
| `secondary` | 不透明灰，`#E5E5E5` / `#757575` | `couiColorSecondary`，主题色 15% / 25% |
| `bodyL`（原 `paragraph`） | 16sp | 15sp（`couiTextBodyL`） |
| `headlineS`（原 `title4`） | 字重 500 | 字重 550（`couiTextHeadlineS`） |

### 顶栏与渐进模糊

编译不兼容的变更：

- 移除 `TopAppBarDefaults.TitlePadding`、`NavigationIconPadding` 与 `ActionIconPadding`。`TopAppBar` 与
  `SmallTopAppBar` 的 `titlePadding`、`navigationIconPadding`、`actionIconPadding` 参数现在默认 `Dp.Unspecified`，
  随顶栏宽度取值（`TopAppBarDefaults.horizontalPadding()`）：`HorizontalPaddingCompact`（16dp）、
  `HorizontalPaddingMedium`（24dp）或 `HorizontalPaddingExpanded`（40dp）。原来用旧常量的地方，改传其中之一或自己的值。
- `glow` 改为可空类型 `(@Composable BoxScope.() -> Unit)?`，默认 `null`。

行为变化：

- `SmallTopAppBar` 标题改为 18dp Medium（原为 24dp SemiBold）；副标题常驻的 `TopAppBar` 折叠后标题也是 18dp。
- 中、大屏窗口上顶栏两侧边距为 24dp / 40dp，不再固定 16dp。
- 只要 `glow` 非空就隐藏分割线。之前用 `glow = {}` 表示「不要 glow」的，请改传 `null` 或去掉该参数。不支持着色器时，
  glow 带会绘制不透明底色。
- `ProgressiveBlur` 的衰减曲线由 `pow(smoothstep(raw), curve)` 改为 `pow(raw, curve)`，与 ColorOS 17 渲染器一致。
  这会影响所有渐进模糊，包括顶栏 glow：默认 `curve = 1f` 时，半径在过渡带内线性变化，不再是 S 形。

新增：`SmallTopAppBar(centerTitle = …)`、`IconButton(redDot = …)` 与 `ToolbarTextButton`，见
[TopAppBar](../components/topappbar.md) 与 [IconButton](../components/iconbutton.md)。

### 导航栏与浮动栏

编译不兼容的变更：

- `NavigationRail` 的 `content` 由 `@Composable ColumnScope.() -> Unit` 改为 `@Composable () -> Unit`。
  用到 `ColumnScope` 修饰符（`Modifier.weight`、`Modifier.align`）的条目需要自己包一层 `Column`。
- `NavigationRailDefaults.LabelFontSize` 由 `10.sp` 改为 `10.dp`（`Dp` 类型，标签不再随字体缩放）。

行为变化：

- 窗口宽度不小于 600dp（`NavigationBarDefaults.WideLayoutBreakpoint`）时，`IconAndText` 模式的 `NavigationBar`
  条目改为图标在 14dp 标签旁边、间距 8dp，整行距两侧 44dp（横屏同样适用）。
- `FloatingToolbar`：`shadowElevation` 4dp → 24dp（`FloatingToolbarDefaults.ShadowElevation`），
  `FloatingToolbarDefaults.OutSidePadding` (12dp, 8dp) → (16dp, 16dp)，`defaultColor()` 改为 `colorScheme.bar`，
  与浮动导航栏一致（原为卡片背景色）。
- 窗口宽度不小于 600dp 时 `Banner` 显示为列表形态（`BannerType.List`）。想在所有尺寸保留翻页形态，传
  `type = BannerType.Pager`。

新增：`FloatingToolbar(items = …)` 与 `FloatingToolbarItem`、`Banner` 的 `listItemWidth` / `listItemSpacing` /
`scalePages`，以及 `NavigationRailDefaults.MinItemSpacing`，见 [FloatingToolbar](../components/floatingtoolbar.md)、
[FloatingNavigationBar](../components/floatingnavigationbar.md) 与 [Banner](../components/banner.md)。

### 对话框

行为变化：

- `DialogDefaults.MaxWidth` 392dp → 360dp（可见面板宽度）。
- 加载对话框：`LoadingDialogDefaults.CornerRadius` 9dp → 45dp（与对话框圆角一致），`SpinnerSize` 26dp → 36dp，
  `textColor()` 改为 `labelSecondary`，`spinnerColor()` 返回 `ProgressIndicatorDefaults.rotatingProgressIndicatorColor()`。
  卡片宽 152dp，带取消按钮时 180dp。
- 安全对话框：`SecurityDialogDefaults.CheckboxText` 改为 "Don't ask me again"（原为 "Don't remind me again"），
  点击外部不再关闭对话框，也不再调用 `onCancel`。
- 只有标题、没有正文 / 自定义内容 / 列表的对话框，标题下方多出 16dp。
- 对话框按钮文字最多换成两行（`DialogButtonBarDefaults.LabelMaxLines`）。

新增：`OverlayDialog` / `WindowDialog` 的 `customContent`、加载对话框的 `title` 与 `cancelAction`、安全对话框的
`onCheckedChange`，以及列表、单选、多选、进度、输入对话框，见 [ListDialog](../components/listdialog.md)、
[ProgressDialog](../components/progressdialog.md) 与 [InputDialog](../components/inputdialog.md)。

### 底部面板与声明页

编译不兼容的变更：

- `FullPageStatement` 的 `content` 由 `String` 改为 `AnnotatedString`：

```kotlin
// 之前
FullPageStatement(title = title, content = text, /* … */)

// 之后
FullPageStatement(title = title, content = AnnotatedString(text), /* … */)
```

行为变化：

- `BottomSheetDefaults.insideMargin` 改为 `DpSize(0.dp, 0.dp)`（原为水平 24dp）：COUI 面板自身不加内边距，由内容自己留白。
  想保留旧内边距就传 `insideMargin = DpSize(24.dp, 0.dp)`。列表与多选列表偏好项的面板仍是 24dp
  （`ListPreferenceDefaults.PanelInsideMargin`）。
- 拖动条默认隐藏，需要时传 `showDragHandle = true`。
- 窗口宽度不小于 600dp（`BottomSheetDefaults.LargeScreenWidthThreshold`）时，面板居中浮起、距底部 64dp、最宽 540dp
  （`PanelMaxWidth`），以淡入加缩放出现，而不是从底部滑入。
- 没有标题和操作按钮的面板不再预留 42dp 的标题行。
- 下拉超过面板一半高度、或以超过 500px/s 的速度下滑即关闭（原为 150dp 或 800dp/s）。
- `FullPageStatement` 不再强制 `fillMaxSize`，声明正文超过 248dp（`statementMaxHeight`）后滚动，滚动渐隐为 32dp（原为 46dp）。

新增：底部面板的 `showDragHandle`、`handlePanel`、`showInMaxHeight`、`dismissOnClickOutside`、`draggable` 与
`enablePullUp`，以及用户声明 / 个人信息声明对话框，见 [OverlayBottomSheet](../components/overlaybottomsheet.md)、
[UserStatementDialog](../components/userstatementdialog.md) 与
[IndividualStatementDialog](../components/individualstatementdialog.md)。

### 列表行与偏好项

行为变化：

- `CardListGroup` 自行绘制行分割线：画在每个首行或中间的 `BasicComponent` 行下方，从标题起、到卡片边缘前 16dp 止，
  与 `selected` 行相邻时隐藏。请删掉你在行之间手动放的 `HorizontalDivider`，或用 `CardListGroup(showDividers = false)`
  / `item(showDivider = false)` 关闭。
- `BasicComponent` 起始内容与标题之间、标题与末尾内容之间的间距由 8dp 改为 16dp。请去掉你给 `startAction` 图标额外加的 end 内边距。
- 24 / 32 / 36 / 50dp 的起始内容使用 COUI 的上下边距，超过两行的行把它顶部对齐。标题与末尾内容争抢宽度时，先量标题，
  末尾内容至少保留 80dp。
- `CheckboxPreference` 默认 `checkboxLocation = CheckboxLocation.End`（原为 `Start`）。
- `SliderPreference` 默认 `hapticEffect = SliderDefaults.SliderHapticEffect.Step`（原为 `Edge`）、
  `showKeyPoints = true`（原为 `false`）。
- `RecommendedPreference` 改用 `container4`：深色背景为 `#1AFFFFFF`（原为 `#14FFFFFF`）。

新增：`BasicComponent` 的 `selected`、`startBadge`、`endBadgeCount`、`endIcon`，`SwipeActionItem(menuStyle = …)`，
`SmallTitleDefaults.InsideMarginSmall`，以及 `MarkWithDividerPreference`、`SlideSelectPreference`、`PreferenceCategory`
组件，见 [CardListGroup](../components/cardlistgroup.md) 与 [BasicComponent](../components/basiccomponent.md)。

### 输入框

编译不兼容的变更：

- 移除 `TextFieldColors.backgroundColor` 以及 `TextFieldDefaults.textFieldColors()` 的 `backgroundColor` 参数：COUI
  输入框没有填充。`TextFieldColors` 构造函数新增 `counterErrorColor` 与 `disabledIconColor`。
- 移除 `InputViewDefaults.MultiCountSpacing`；多行卡片固定 152dp（`InputViewDefaults.MultiInputHeight`）。

行为变化：

- `Line` 与 `Rectangle` 模式的输入框最小高度为 62dp（`TextFieldDefaults.minHeight(mode)`）。需要紧凑的输入框（例如放在对话框里）
  时传 `minHeight = 0.dp`。
- `Rectangle` 模式聚焦时描边保持 0.33dp（原为 1dp），只变颜色。
- 浮动标签按 COUI 布局：`Line` 模式在文字上方预留 10dp + 收起后的标签高度 + 5dp；`Rectangle` 模式的边框从收起标签的一半高度开始，
  标签嵌在 4dp 的缺口里。禁用状态的标签使用 `disabledBorderColor`。
- `TextField` 新增 `initialPasswordVisible`（默认 `false`，即掩码）。`InputView` 与 `InputPreference` 传 `true`，
  与 `COUIInputView` 一样，密码初始以明文显示。
- `InputPreference` 默认 `showClearButton = true`（原为 `false`），`InputView` / `InputPreference` 的 `textFieldColors`
  默认改为 `InputViewDefaults.textFieldColors()`。
- `CodeTextField` 的格子随窗口宽度缩放（`min(窗口宽度, 360dp) / 360dp`），不再随自身宽度缩放。

### 搜索、索引与锁屏

编译不兼容的变更：

- 移除 `InputField(trailingIcon = …)`，改用 `innerPrimaryButton` / `innerSecondaryButton`。
- `SearchBarDefaults.TrailingIconStartPadding` / `TrailingIconEndPadding` / `TrailingIconTouchTargetSize` /
  `TrailingIconVisualSize` → `EditTextEndGap`、`InnerButtonGap`、`InnerButtonSize` 与 `ClearButtonVisualSize`。
- `SearchBarColors` 构造函数新增 `disabledLabelColor`、`disabledIconColor` 与 `disabledCancelTextColor`。
- `SearchHistoryDefaults.InsideMargin` → `SearchHistoryDefaults.insideMargin()`（窗口宽度不小于 600dp 时水平 20dp）。
- `LockScreenPasswordInputColors` 新增 `eyeColor`。
- `LockPatternDisplayMode` 新增 `FingerprintMatch` 与 `FingerprintNoMatch`；对它穷举的 `when` 需要补上新分支。

行为变化：

- `SearchBarDefaults.InsideMargin` 的宽度改为未指定，水平内边距随窗口变化（`responsiveHorizontalPadding`）。
- 收起搜索栏会清空搜索词。
- `SearchHistory` 点击清除按钮时立即调用 `onClear`，随后面板在 300ms 内淡出，期间仍显示旧记录。
- `NumericKeyboard` 默认没有删除键（`rightKey = null`），需要时传 `rightKey = NumericKeyboardDefaults.deleteKey()`。
  自定义侧键图标默认不着色，需 `NumericKeyboardSideKey(tintIcon = true)`。深色模式下数字为白色，侧键无背景。
- 隐身模式下 `LockPatternView` 连错误图案也不再绘制。

新增：`SearchBar` 的 `type`（`SearchBarType.NonInstant`）、`navigationButton` 与外侧按钮，`AlphabetIndexBar(selectedKey = …)`，
见 [SearchBar](../components/searchbar.md) 与 [ToolbarSearch](../components/toolbarsearch.md)。

### 选择器与状态页

编译不兼容的变更：

- `DatePicker(yearRange = …)` → `minDate` / `maxDate`，`DatePickerDefaults.YearRange` → `MinDate` / `MaxDate`：

```kotlin
// 之前
DatePicker(value, onValueChange, yearRange = 2000..2030)

// 之后
DatePicker(value, onValueChange, minDate = DateValue(2000, 1, 1), maxDate = DateValue(2030, 12, 31))
```

- 文案常量改为读取当前语言区域的 `@Composable` 函数：

| 已移除 | 替代 |
| :----- | :--- |
| `DatePickerDefaults.YearLabel` / `MonthLabel` / `DayLabel` | `yearLabel()` / `monthLabel()` / `dayLabel()` |
| `TimePickerDefaults.AmLabel` / `PmLabel` | `amLabel()` / `pmLabel()` |
| `DateTimePickerDefaults.DateLabel` / `TodayLabel` | `dateLabel()` / `todayLabel()` |
| `CalendarPickerDefaults.MonthYearLabel` / `WeekdayLabel` | `monthYearLabel()` / `weekdayLabel()` |

- `NumberPickerDefaults.colors()` 与 `NumberPickerColors` 去掉了 `disabledSelectedTextColor` 与 `disabledUnselectedTextColor`。

行为变化：

- `NumberPicker` 默认 `wrapAround = true`（原为 `false`），需要有边界的滚轮时传 `false`。modifier 没给宽度时，
  滚轮宽 80dp（`NumberPickerDefaults.MinWidth`），不再撑满。
- `TimePicker` 与 `DateTimePicker` 的 `amPmFirst` 跟随语言区域（原分别为 `false` 与 `true`）。
- `LunarDatePickerDefaults.MinDate` 改为 1910-03-10（原为 1910-01-01）。
- `EmptyState` 插图默认 120 × 100dp（原为 280 × 200dp），`Medium` 尺寸不再缩放到 0.6。
- `CompleteState` 默认 `type = CompleteStateType.None`（原为 `Success`）。

### 滑块、进度与过冲回弹

编译不兼容的变更：

- `VerticalSlider` 去掉了 `width` 与 `effect`：它现在是 COUI 的 4dp 轨道，拖动时变宽，最小宽度 36dp。请给它一个高度。
  新增的 `text` / `textColor` 在顶部绘制标签。
- `ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize` / `StrokeWidth` →
  `MediumCircularProgressIndicatorSize`（30dp）/ `MediumCircularProgressIndicatorStrokeWidth`（3dp）。
- `InstallLoadStyle` 新增 `Circle`；对它穷举的 `when` 需要补上新分支。

行为变化：

- `Slider`、`VerticalSlider`、`IntentSlider` 与 `SliderPreference` 默认 `hapticEffect = SliderDefaults.SliderHapticEffect.Step`
  （原为 `Edge`；`RangeSlider` 不变），`Slider` 默认 `showKeyPoints = true`（原为 `false`）。
- `CircularProgressIndicator` 默认值：有 `progress` 时 40dp / 5dp（原为 30dp / 3dp），四周各多 4dp 布局内边距；
  没有 `progress` 时 18dp / 2.67dp，使用主题强调色（原为 30dp / 3dp）。
- 过冲回弹按 COUI 实现：越过边缘的拖动只让内容移动手指位移的 `0.4 * (1 - |过冲量| / 窗口尺寸)`，松手后按
  `SpringOverScroller` 的 RK4 弹簧回弹。默认的 `COUIOverscrollEffect` 会播放滚动到边缘的震动（307），传
  `edgeHapticEnabled = false` 可关闭。
- `Modifier.scrollEndHaptic()` 默认改为 COUI 滚动到边缘的震动（307），`hapticFeedbackType` 改为可空（原默认 `TextHandleMove`）。

新增：`CompProgressIndicator`、`CircularProgressIndicator` 的 `state` / `type`、`Slider(startFromMiddle = …)` 与 `Circle`
安装样式，见 [ProgressIndicator](../components/progressindicator.md)、[Slider](../components/slider.md) 与
[ScrollBar](../components/scrollbar.md)。

### 按钮与选择控件

编译不兼容的变更：

- `CheckboxDefaults.checkboxColors()` 去掉了 `uncheckedForegroundColor` 与 `disabledUncheckedForegroundColor`；
  `CheckboxColors` 构造函数同样去掉这两项，并新增 `uncheckedFillColor` / `disabledUncheckedFillColor`。

行为变化：

- `SegmentButton` 的固定宽度会被限制在 COUI 栅格内（小屏 4 栏、中屏 6 栏、大屏 8 栏），控件在给定宽度内居中。传
  `responsiveWidthEnabled = false` 恢复旧宽度。
- `SegmentButton` 的条目与 `Stepper` 的按钮可通过键盘聚焦（Enter / 方向键），`Button`、`Switch`、`FloatingActionButton`、
  `SegmentButton` 与 `Stepper` 会绘制 COUI 焦点环。

新增：`DescTextButton`、`ButtonDefaults` 宽度常量、`Chip` 的 `showRedDot` / `onClose` / `count`、`SuggestionChip`、`IconChip`、
`ChipGroup`、`CheckboxDefaults.shapeCheckboxColors()`、`Badge(text)` 与 `visible`，以及 FAB 的 `visible`、迷你尺寸、
secondary / translate 预设与 `rememberFloatingActionButtonScrollState()`，见 [Button](../components/button.md)、
[Chip](../components/chip.md)、[ChipGroup](../components/chipgroup.md) 与
[FloatingActionButton](../components/floatingactionbutton.md)。

### TopTips、Snackbar 与菜单

编译不兼容的变更：

- `TopTipsDefaults.ContentPadding`（12dp）→ `TopTipsDefaults.HorizontalPadding`（16dp）。
- `TopTipsColors` 新增 `titleColor` 与 `actionContentColor`，`actionColor` 改为操作按钮的填充色。调用构造函数请用命名参数。
- `NotificationSnackbar(title, actionLabel, …)` 的 `actionLabel` 改为非空 `String`。`NotificationSnackbar(data)` 重载仍接受
  没有按钮文字的 `SnackbarData`，此时卡片不显示按钮。

行为变化：

- `TopTips`：正文 13sp（`bodyS`，原为 14sp）；操作项改为实心小按钮（原为强调色文字），并可与关闭按钮同时出现；容器改用
  `container4`（深色 `#1AFFFFFF`，原为 `#14FFFFFF`）；关闭按钮是 `labelTertiary` 圆底白叉；显示与隐藏以高度动画过渡。
- 导航弹出菜单（`OverlayNavigationPopupMenu` / `WindowNavigationPopupMenu`）的行使用 12dp / 16dp 内边距与 16sp 粗体标题，
  不再标记已选中项。
- `EditTextPreference` 与下拉弹窗的对话框模式改为把输入框 / 列表作为对话框的自定义内容：摘要下方 4dp（原为 16dp），
  输入框下方 8dp，只有标题时不再额外多出 16dp。

新增：`TopTips` 的 `title`（配 36dp 图标）与 `marquee`、`DropdownImpl(reserveIconSlot = …)` 与 `DropdownItem.stateIcon`，见
[TopTips](../components/toptips.md) 与 [Snackbar](../components/snackbar.md)。

### 材质、模糊与触感

编译不兼容的变更：

- `CouiHapticEffect` 新增 `LongVibrate`、`KeyboardTouch`、`ShortTripleVibrate`、`SteppingShort`、`EdgeShort` 与
  `EdgeSqueeze`。对它穷举的 `when` 会编译失败：

```kotlin
// 之后
when (effect) {
    CouiHapticEffect.Switch -> …
    CouiHapticEffect.Strength -> …
    CouiHapticEffect.ScrollEdge -> …
    else -> … // 新增的触感类型
}
```

- `MaterialBlurDefaults.popup(isDark, hdr = false)` 与 `Modifier.couiMaterialBlur(…, noiseCoefficient)` 新增了带默认值的参数。
  源码仍能编译，但基于旧版 coui-blur 编译的库需要重新编译。

行为变化：

- 新增的全局开关 `LocalCOUIMaterialEffectEnabled`（默认 `true`）成为以下组件材质参数的默认值：`FloatingNavigationBar`、
  `FloatingToolbar`、`FloatingEditToolbar`、`FloatingAccessoryNavigationBar`、`ChipTab`、`Button` 系列（`materialBackdrop`、
  `ButtonMaterial.Auto`）、`FloatingActionButton`（`material`）、`IconButton`、`SearchBar`，以及列表、级联与下拉弹窗
  （`materialBlur`、`materialEffect`）。`SegmentButton` 仍是 `materialEffect = false`。提供 `false` 即可全部关闭；显式传入的参数优先：

```kotlin
CompositionLocalProvider(LocalCOUIMaterialEffectEnabled provides !batterySaver) {
    AppContent()
}
```

- 材质模糊改为对齐系统的 Kawase 滤镜：`couiMaterialBlurRadius()` 把 COUI 半径换算为扩散相同的高斯半径（200px → 约 192.4、
  400px → 约 384.9、160px → 约 154），因此模糊扩散约小 4%，并叠加 ±0.005 的抖动（`MaterialBlurDefaults.NoiseCoefficient`；
  `noiseCoefficient = 0f` 关闭）。按 `fraction` 缩放后的半径取整到像素。
- 组件播放 COUI 触感：滑块逐档 `SteppingShort`、分段 `Strength`、到端 `EdgeShort`；图案锁与简易锁出错 `ShortTripleVibrate`；
  数字键盘 `KeyboardTouch`；字母索引条 `Strength`；滚动到边缘 `ScrollEdge`。

新增：`ViewBlurDefaults` 的 `frameworkFab`、`frameworkBottomBar`、`searchBarNoMaterial`、`contentSegmentButton`、
`contentChipTab` 与 `NoEffect` 预设，`ViewBlurDefaults.interpolate()`，`COUIEaseEasing` 系列缓动，`Modifier.couiFocusStroke`
与 `COUITouchSelectInteraction`，见[模糊](./blur.md)。

### 建议真机确认的观感变化

以下变化无需改代码即可编译，但在预览里容易漏看，建议在真机上过一遍：

- 过冲回弹：新的橡皮筋阻尼与回弹，以及所有使用默认过冲效果的列表在滚动到边缘时的震动（307）。
- 渐进模糊：顶栏 glow 与你自己的 `ProgressiveBlur` 过渡带改为线性的 `pow(raw, curve)` 衰减。
- 材质模糊：对话框、底部面板与弹窗的模糊扩散约小 4%，并带抖动。
- 宽窗口（不小于 600dp）：底部面板居中浮起、最宽 540dp，`Banner` 切换为列表形态。
- `TextField` 浮动标签：新的标签位置与缺口，以及 62dp 最小高度。

### 本版新增

- **与顶栏联动的搜索。** `TopAppBarSearchBar`、`rememberTopAppBarSearchState`、
  `TopAppBar(searchState = …)` 以及 `Modifier.topAppBarSearchContent` / `topAppBarSearchResults`
  实现与 ColorOS 17 设置首页一样的原地搜索：标题淡出，搜索栏带着取消按钮升到顶部，页面淡出，结果从下方进入。
  顶栏下方所有可输入的搜索都用它；`TopAppBarSearchSlot` 只用于点击后跳转别处的胶囊。
  见 [TopAppBar](../components/topappbar.md#原地搜索)。
- **相机控件**，来自 ColorOS 相机：`ExposureBar`、`ScaleBar`、`CameraShutterButton`、
  `CameraSwitchButton`、`CameraModeBar`、`CameraModeTitle`、`CameraLensBar`、`CameraZoomBar`、
  `CameraZoomDial`、`CameraFocusIndicator`、`CameraMenuButton`、`CameraEffectButton`、
  `CameraTopMenu`、`CameraRecordTimer`、`CameraCountdown`、`RollingNumber`、
  `CameraLevelIndicator`、`CameraGridOverlay`、`CameraThumbnail` 和 `CameraHint`。
- **Baseline profile**：所有库模块的 profile 已按 `io.github.suqi8.coui.kmp` 包名重新生成。

## 1.0.0 → 1.1.0

把你依赖的每个 COUI 坐标都改成新版本：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
        }
    }
}
```

这一版按 ColorOS 16 重新校准了大部分组件，一部分依据反编译资源，一部分靠真机实测。其中两处纠正的是从 Miuix 继承下来、一直没核对过的值。**即使你什么都没改，观感也会变化**，升级后请过一遍自己的界面。

### 一处编译不兼容

`InfiniteProgressIndicator` 去掉了 `orbitingDotSize` 参数，以及对应的 `ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize`。这个指示器原来是「一个环 + 一个绕着环跑的小圆点」，那是 Miuix 的设计，COUI 里并不存在——ColorOS 画的是一段裸弧，弧长随旋转伸缩。既然没有点了，也就没有点的尺寸可设。

```kotlin
// 改之前
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp, orbitingDotSize = 4.dp)

// 改之后 —— 去掉这个参数
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp)
```

同一段弧也可以直接用 `RotatingProgressIndicator`，它带 COUI 的两档尺寸（16dp 和 26dp）。

### 文字变高了

`COUITheme.textStyles` 的每一项现在都带上了对应 COUI text appearance 声明的行距倍数，所以文字占的纵向空间比 1.0.0 多。`headline1` 的字号也从 17sp 降到了 **16sp**：真机上量到列表项标题是 21.71dp，按 COUI 的倍数换算正好是 16sp，而不是 Miuix 用的 17sp。

如果你按某个具体字符串的尺寸写死过容器高度，请重新核一下。如果你之前为了补偿「太挤」自己加过 `lineHeight`，请去掉——现在会叠加两次行距。

### 列表行与弹窗有位移

- 行高现在取决于它在卡片组里的位置：独立单行比中间行高 4dp，首行或末行在圆角那一侧高 2dp。不传 `cardListPosition` 则行为不变，所有 preference 组件都新增了这个参数。
- 弹窗按钮栏在文案放不下时会**竖排**，而不是硬挤。竖排档有自己的一套尺寸，而且按钮顺序会反转——取消从最左移到最下。
- 弹窗按钮栏里的按钮按下时不再缩放，按压色铺满整格且是方角而非胶囊。`Button` 和 `TextButton` 为此新增了 `pressScaleEnabled`，默认 `true`，所以你自己的按钮不受影响。

### 下拉菜单的交互变了

扁平下拉菜单按 COUI 自己的手势模型重建过，所以变的不只是像素：

- 菜单短到不需要滚动时，**按住拖动会让高亮跟着手指在行间移动**，松手选中的是手指当下所在那行，而不是最初按下那行。每跨过一行会触发一次震动。菜单一旦可滚动，拖动就变成滚动。
- 从 preference 行打开的下拉菜单会**出现在你点击的位置**，而不是居中对齐那一行。
- 菜单不再压暗背后的内容。`OverlayListPopup`、`WindowListPopup` 以及两个级联弹窗的 `enableWindowDim` 现在默认 `false`，想保留旧行为就显式传 `true`。
- 菜单项之间有了细分割线，按住某项时它相邻的两条会淡出。

### 本版新增

`DialogButtonBar`（配合 `DialogButtonBarAction`）替代手写的弹窗按钮行，竖排逻辑由它处理。下拉菜单项新增了放尾部角标的 `hint` 插槽、用于危险操作的 `alert` 标记，以及 `DropdownEntry.title` 用来做菜单内分组标题。

## 更早的版本

如果你是从 Miuix 迁移而不是从旧版 COUI 升级，请看[从 Miuix 迁移](./migration.md)。
