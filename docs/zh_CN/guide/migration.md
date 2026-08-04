# 从 Miuix 迁移

COUI 基于 [Miuix](https://github.com/compose-miuix-ui/miuix) 构建，并保持了同样的整体 API 结构，因此将现有 Miuix 项目迁移到 COUI 基本上只是机械式的替换：更换依赖坐标、更新包名前缀、再重命名少量 `Miuix*` 符号为对应的 `COUI*`。本页将逐步说明每个步骤。

::: tip 提示
迁移后应用可继续使用同一套组件，但视觉风格将从 Xiaomi HyperOS 切换为 ColorOS 设计（颜色、圆角、动效曲线、按压反馈）。详见下方[行为与视觉差异](#_7-行为与视觉差异)。
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
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            // 可选模块，模式相同：
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
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

导航图标自带 `16.dp` 的 `navigationIconPadding`。如果你在 Miuix 下为返回按钮额外加过 start 内边距，请移除，否则图标离屏幕边缘会过宽。

### TabRow

`TabRow` 在视觉上重建为 ColorOS 分段按钮。签名保持兼容，但有细微差异：`itemSpacing` 默认值改为 `0.dp`（原为 `9.dp`；`TabRowWithContour` 原为 `5.dp`），普通 `TabRow` 默认使用透明背景并新增 `indication` 参数，`TabRowWithContour` 新增 `contourPadding` 参数。

### Switch

`Switch` 新增 `isLoading: Boolean = false` — 加载中时滑块显示 COUI 加载指示器且无法切换（对应 ColorOS 异步生效的设置项）。

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

`Colors` 构造器保持了 Miuix 的参数顺序，按位置传参构建的完整自定义配色可原样迁移、继续编译。不过更建议直接删掉自定义配色——`lightColorScheme()` / `darkColorScheme()` 现在返回的就是原生 ColorOS 16 配色，通常正是 COUI 应用想要的效果；一次真实迁移借此删掉了约 220 行自定义颜色。

## 7. 行为与视觉差异

API 保持熟悉的形态，但每个组件都已按 ColorOS 16 重新设计：

- **颜色**：配色方案数值遵循 ColorOS（强调色、容器色与表面色与 HyperOS 不同）；继续使用 `COUITheme.colorScheme.*`，代码会自动获得新数值。
- **形状与尺寸**：圆角、内边距与组件尺寸遵循 ColorOS 规范。
- **动效**：动画曲线与按压反馈（缩放/着色）遵循 COUI 动效设计。
- **动态取色**：仍然通过 `ThemeController` 支持 Monet / 动态取色（`ColorSchemeMode.MonetSystem`、`MonetLight`、`MonetDark`），与之前完全一致 — 参见[主题系统](../guide/theme.md)。

## 8. 新增组件

迁移后还可以使用 Miuix 中不存在的组件：

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
