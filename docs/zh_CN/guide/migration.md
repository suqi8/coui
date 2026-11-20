# 从 Miuix 迁移

COUI 基于 [Miuix](https://github.com/compose-miuix-ui/miuix) 构建，并保持了同样的整体 API 结构，因此将现有 Miuix 项目迁移到 COUI 基本上只是机械式的替换：更换依赖坐标、更新包名前缀、把少量 `Miuix*` 符号重命名为对应的 `COUI*`，再把色槽与文本样式改成 COUI 官方命名。本页逐步说明迁移到 COUI 2.0（ColorOS 17 / COUI 17 设计）的每个步骤。

::: tip 提示
迁移后应用可继续使用同一套组件，但视觉风格将从 Xiaomi HyperOS 切换为 ColorOS 17 设计（颜色、圆角、动效曲线、按压反馈、材质模糊）。详见下方[行为与视觉差异](#_7-行为与视觉差异)。
:::

## 1. 更新依赖

将 Miuix 的 Maven 坐标（`top.yukonga.miuix.kmp`）替换为 COUI 的坐标（`io.github.suqi8.coui.kmp`）。每个模块都有 1:1 的对应关系：

| Miuix 模块 | COUI 模块 |
| :-- | :-- |
| `top.yukonga.miuix.kmp:miuix-ui` | `io.github.suqi8.coui.kmp:coui-ui` |
| `top.yukonga.miuix.kmp:miuix-preference` | `io.github.suqi8.coui.kmp:coui-preference` |
| `top.yukonga.miuix.kmp:miuix-icons` | `io.github.suqi8.coui.kmp:coui-icons` |
| `top.yukonga.miuix.kmp:miuix-blur` | `io.github.suqi8.coui.kmp:coui-blur` |
| `top.yukonga.miuix.kmp:miuix-squircle` | `io.github.suqi8.coui.kmp:coui-squircle` |
| `top.yukonga.miuix.kmp:miuix-nav` | `io.github.suqi8.coui.kmp:coui-nav` |
| `top.yukonga.miuix.kmp:miuix-shader` | `io.github.suqi8.coui.kmp:coui-shader` |
| `top.yukonga.miuix.kmp:miuix-core` | `io.github.suqi8.coui.kmp:coui-core` |

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:2.1.0")
            // 可选模块，模式相同：
            implementation("io.github.suqi8.coui.kmp:coui-preference:2.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:2.1.0")
        }
    }
}
```

COUI 还额外提供 `io.github.suqi8.coui.kmp:coui-navigation3-ui`，用于集成 androidx Navigation 3 — 参见[Navigation 3](../guide/navigation3.md)。

## 2. 更新包名前缀

所有包从 `top.yukonga.miuix.kmp.*` 迁移到 `io.github.suqi8.coui.kmp.*`，子包结构保持不变（`basic`、`theme`、`utils`、`icon`、`extra`/`preference`、`blur`、`nav` 等）。对源码做一次全局查找替换即可：

```shell
# macOS / Linux（在项目根目录执行）
grep -rl "top.yukonga.miuix.kmp" src | xargs sed -i 's/top\.yukonga\.miuix\.kmp/io.github.suqi8.coui.kmp/g'
```

在 Windows 上，可使用 IDE 的「在文件中替换」（Android Studio / IntelliJ 中为 `Ctrl+Shift+R`），将 `top.yukonga.miuix.kmp` 替换为 `io.github.suqi8.coui.kmp`。

## 3. 重命名的 API

所有 `Miuix*` 前缀的符号都重命名为 `COUI*`，映射完全 1:1：

| Miuix | COUI |
| :-- | :-- |
| `MiuixTheme`（可组合函数 + object） | `COUITheme` |
| `MiuixIndication` | `COUIIndication` |
| `MiuixPopupUtils` | `COUIPopupUtils` |
| `MiuixPopupHost` | `COUIPopupHost` |
| `MiuixScrollBehavior` | `COUIScrollBehavior` |
| `MiuixIcons` | `COUIIcons` |
| `MiuixOverscrollEffect` | `COUIOverscrollEffect` |
| `MiuixOverscrollFactory` | `COUIOverscrollFactory` |
| `NavTransitions.MiuixDefault` | `NavTransitions.COUIDefault` |
| `BadgedBox` | `BadgeBox`（签名也有变化，见下文） |

再做一次 `MiuixTheme` → `COUITheme`、`MiuixIcons` → `COUIIcons` 等查找替换即可覆盖。`folmeSpring` 缓动函数（原位于 `MiuixEasing.kt`，现为 `COUIEasing.kt`）名称不变，无需修改代码。

### 色槽与文本样式

COUI 的 `Colors` 与 `TextStyles` 槽位以 COUI 官方属性命名（`couiColorXxx` → `xxx`，`couiTextXxx` → `xxx`），而 Miuix 用的是类 Material 的名字（`onBackground`、`surfaceContainer`、`main`、`title4` 等）。没有弃用别名，所有 `COUITheme.colorScheme.*` 与 `COUITheme.textStyles.*` 的引用都要改成新名字。多个 Miuix 槽位会并入同一个 COUI 槽位：

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

`background`、`surface` 与 `secondary` 两边都有，但含义不同：在 COUI 中 `background` 是页面底色，`surface` 是对话框、底部面板、弹窗等浮层面板的颜色，`secondary` 是主题色 15%（浅色）/ 25%（深色），不是灰色。先改 `surface` 再改 `background`（`background` 有些地方要改成 `surface`），并逐处确认，不要直接保留原名。

## 4. 有变化的 API

少数组件为 ColorOS 重新构建，签名有所不同。

### Badge

Miuix 的 `Badge` 接受任意内容；COUI 的 `Badge` 改为基于数字并自动选择形态 — `count <= 0` 时显示圆点，正数显示数字胶囊，数字过大时显示省略号形态。`BadgedBox` 重命名为 `BadgeBox`，并新增 `overhang` 参数控制角标超出内容的距离。

```kotlin
// Miuix
BadgedBox(badge = { Badge { Text("8") } }) { Icon(...) }

// COUI
BadgeBox(badge = { Badge(count = 8) }) { Icon(...) }
```

### TextField

COUI 的 `TextField` 新增 `backgroundMode: TextFieldMode` 参数，提供三种 ColorOS 形态 — `Rectangle`（描边圆角矩形）、`Line`（底部横线，默认值）和 `None`（无背景装饰）。相关的默认值变化与新增参数：

- `useLabelAsPlaceholder` 默认值改为 `true`（ColorOS 输入框使用提示文本而非浮动标签；Miuix 默认为 `false`）。
- 新增参数：`justShowFocusLine`、`isError`、`maxCount`（字数统计）、`showClearButton`、`showPasswordToggle`。
- `insideMargin` 和 `textStyle` 的默认值现在随所选形态变化。

如需 ColorOS 的「卡片输入框」（字段上方带独立标题），请使用新组件 [InputView](../components/inputview.md)。

### PressFeedbackType

`PressFeedbackType`（`Card` 等可按压组件使用）新增 `Tint` 值——COUI 的按压蒙层反馈。原有穷举式 `when` 会因此编译失败，需补上新分支（或 `else`）：

```kotlin
when (pressFeedbackType) {
    PressFeedbackType.None -> ...
    PressFeedbackType.Sink -> ...
    PressFeedbackType.Tilt -> ...
    PressFeedbackType.Tint -> ... // COUI 新增
}
```

### 弹窗内容内边距

`OverlayDialog` / `WindowDialog` **有意**不给 `content` 槽加内边距，以便按钮行和分割线能贯通弹窗全宽（与 ColorOS 一致）。如果你的 Miuix 弹窗依赖内置内容边距，请自行包一层——水平 `24.dp` 与 COUI 的标题/摘要边距对齐：

```kotlin
OverlayDialog(show = showDialog, title = "标题", onDismissRequest = { ... }) {
    Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        // 弹窗内容
    }
}
```

弹窗调用点较多时，建议封装一个自己的 `AppDialog` 组合项统一处理，而不是每处都写内边距。

### TopAppBar

导航图标、标题与操作按钮的水平边距随窗口宽度变化：手机 16dp，中等窗口 24dp，展开窗口 40dp（`TopAppBarDefaults.horizontalPadding()`）。如果你在 Miuix 下为返回按钮额外加过 start 内边距，请移除。`SmallTopAppBar` 标题为 18dp Medium。

顶栏下方的搜索框请用 `TopAppBarSearchBar` 配合 `TopAppBar(searchState = …)`：标题淡出、搜索栏升到顶部，与 ColorOS 17 设置首页一致。见[原地搜索](../components/topappbar.md#原地搜索)。

### TabRow

`TabRowWithContour` 在视觉上重建为 ColorOS 分段按钮：`itemSpacing` 默认值改为 `0.dp`（原为 `5.dp`），并新增 `contourPadding` 参数。普通 `TabRow` 改为 ColorOS 滑动标签栏（48dp，文字标签 + 2dp 下划线指示器），颜色取自 `TabRowDefaults.underlineColors()`。使用具名参数时签名保持源码兼容，新增了 `enabled`、`horizontalMargin`、`pagerState` 参数以及支持图标、红点和禁用标签的 `TabRow(items = ...)` 重载，但 `cornerRadius` 现在决定按压遮罩的圆角，`itemSpacing` 表示标签文字间的最小间距（默认随宽度自适应），点击反馈默认为 `LocalIndication.current`。

### Switch

`Switch` 新增 `isLoading: Boolean = false` — 加载中时滑块显示 COUI 加载指示器且无法切换（对应 ColorOS 异步生效的设置项）。

### Tooltip

Miuix 的 `PlainTooltip`、`RichTooltip` 与 `TooltipBox(positionProvider, …)` 已替换为 COUI 17 tooltip：`TooltipBox(state, tooltip)` 配合 `Tooltip`、`IconTooltip` 或 `ImageTooltip`。COUI tooltip 由代码控制显示，默认不会自动消失。对应关系与示例见[升级指南](./upgrading.md#tooltip-改为-coui-17-tooltip)。

### 底部面板

`BottomSheetDefaults.insideMargin` 为 `0.dp`：COUI 面板不加内边距，由内容自己设置（`DpSize(24.dp, 0.dp)` 可恢复 Miuix 的边距）。拖动条默认隐藏，需要时传 `showDragHandle = true`；窗口宽度至少 600dp 时面板居中悬浮，最宽 540dp。

### 下拉与 Spinner 类型

Spinner 类型已并入 Dropdown：`SpinnerEntry`、`SpinnerColors`、`SpinnerDefaults`、`SpinnerItemImpl` 改为 `DropdownItem`、`DropdownColors`、`DropdownDefaults`、`DropdownImpl`，`DropdownItem(icon, title, summary)` 改为 `DropdownItem(text = …, icon = …, summary = …)`。

## 5. 从旧版 Miuix 迁移（`extra` 包）

较早的 Miuix 版本有一个 `top.yukonga.miuix.kmp.extra` 包，内含 `Super*` 前缀组件。Miuix 与 COUI 后来都对其做了拆分；如果你的项目仍在使用 `extra.*`，按下表映射：

| 旧版 Miuix（`extra.*`） | COUI |
| :-- | :-- |
| `SuperDialog` | `overlay.OverlayDialog`（或 `window.WindowDialog`）——`show` 参数由 `MutableState<Boolean>` 改为普通 `Boolean` |
| `SuperCheckbox` | `coui-preference`：`preference.CheckboxPreference` |
| `SuperSwitch` | `coui-preference`：`preference.SwitchPreference` |
| `SuperArrow` | `coui-preference`：`preference.ArrowPreference` |
| `SuperDropdown` | `coui-preference`：`preference.OverlayDropdownPreference` / `WindowDropdownPreference` |
| `SuperSpinner` | `coui-preference`：`preference.OverlaySpinnerPreference` / `WindowSpinnerPreference` |
| `CheckboxLocation` | `coui-preference`：`preference.CheckboxLocation` |
| `WindowBottomSheet` | `window.WindowBottomSheet`（仅包名变化） |

Preference 系组件位于独立的 `coui-preference` 构件中——如果你之前只依赖 `miuix`，记得补上依赖。旧版直接暴露的样式参数（如 `TextField` 的 `backgroundColor`）大多已移入对应的 `Defaults.…Colors(...)` 工厂。

## 6. 自定义配色方案

`Colors` 的色槽改用 COUI 官方命名（`couiColorXxx` → `xxx`），不再沿用 Miuix 的名字，完整的自定义配色需要按[色槽与文本样式](#色槽与文本样式)中的表改成新的参数名。更建议直接删掉自定义配色——`lightColorScheme()` / `darkColorScheme()` 返回的就是原生 ColorOS 17 配色，通常正是 COUI 应用想要的效果；一次真实迁移借此删掉了约 220 行自定义颜色。

## 7. 行为与视觉差异

API 保持熟悉的形态，但每个组件都按 ColorOS 17（COUI 17）重新设计：

- **颜色**：配色方案数值遵循 ColorOS（强调色、容器色与表面色与 HyperOS 不同）；继续使用 `COUITheme.colorScheme.*`，改用新的槽位名即可。
- **形状与尺寸**：圆角、内边距与组件尺寸遵循 ColorOS 规范，很多组件会适配中等和展开窗口（更宽的边距、悬浮的底部面板、图标旁的导航标签）。
- **动效**：动画曲线与按压反馈遵循 COUI 动效设计。列表使用 COUI 过冲回弹：橡皮筋拖拽、弹簧回弹，并在到达边缘时触发振动。
- **材质**：浮动栏、按钮、搜索栏与弹窗绘制 COUI 17 材质（模糊、光晕、阴影描边）。`LocalCOUIMaterialEffectEnabled provides false` 可全局关闭，例如省电模式下。
- **列表行**：`CardListGroup` 自己绘制行间分割线，不要再手动添加。
- **动态取色**：仍然通过 `ThemeController` 支持 Monet / 动态取色（`ColorSchemeMode.MonetSystem`、`MonetLight`、`MonetDark`），与之前完全一致 — 参见[主题系统](../guide/theme.md)。

## 8. 新增组件

迁移后还可以使用 Miuix 中不存在的组件：

- [顶栏原地搜索](../components/topappbar.md#原地搜索) — 与顶栏联动的 ColorOS 17 设置首页搜索。
- [CardListGroup](../components/cardlistgroup.md) — 一张卡片里的分组列表行，带 COUI 分割线。
- [Tooltip](../components/tooltip.md) — 带箭头的 COUI 17 气泡提示。
- [ListDialog](../components/listdialog.md)、[ProgressDialog](../components/progressdialog.md)、[InputDialog](../components/inputdialog.md)、[UserStatementDialog](../components/userstatementdialog.md) 与 [IndividualStatementDialog](../components/individualstatementdialog.md)。
- [FloatingToolbar](../components/floatingtoolbar.md) 与 [ChipGroup](../components/chipgroup.md)。
- 来自 ColorOS 相机的相机控件，如 [CameraShutterButton](../components/camerashutterbutton.md)、[CameraModeBar](../components/cameramodebar.md)、[CameraZoomDial](../components/camerazoomdial.md) 与 [ExposureBar](../components/exposurebar.md)。

- [Chip](../components/chip.md) — 胶囊形可选中的筛选标签。
- [TopTips](../components/toptips.md) — 页面顶部的提示横幅。
- [Stepper](../components/stepper.md) — 加减号整数步进器。
- [InputView](../components/inputview.md) — 带标题与错误提示的 ColorOS 卡片输入框。
- [CodeTextField](../components/codetextfield.md) — 逐位分格的验证码输入框。
- [DatePicker](../components/datepicker.md) — 年/月/日滚轮选择器。
- [TimePicker](../components/timepicker.md) — 时/分（及上下午）滚轮选择器。
- [FullPageStatement](../components/fullpagestatement.md) — 首次启动的用户协议页面。
- [ColorSwatchPicker](../components/colorswatchpicker.md) — 色板式颜色选择器。
- [LoadingButton](../components/loadingbutton.md) — 内置加载圆点的按钮。
- [LoadingDialog](../components/loadingdialog.md) — 旋转加载对话框（Overlay/Window 两种变体）。
- [SecurityDialog](../components/securitydialog.md) — 安全声明对话框（Overlay/Window 两种变体）。
- 新增偏好设置组件：[ButtonPreference](../components/buttonpreference.md)、[ListPreference / MultiSelectListPreference](../components/listpreference.md)、[MarkPreference](../components/markpreference.md)、[RecommendedPreference](../components/recommendedpreference.md)、[SwitchLoadingPreference](../components/switchloadingpreference.md)。

想先看 ColorOS 17 页面应该怎么搭，可以用 [COUI Canvas](./canvas.md) 画一遍，或打开<a href="/coui/example/" target="_blank">网页版示例应用</a>。
