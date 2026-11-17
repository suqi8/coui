# 颜色系统

本页列出了 COUI 颜色方案中的所有颜色及其默认的浅色/深色数值。数值为 ARGB 十六进制。

## 使用方式

- 在组合函数中通过 `COUITheme.colorScheme.<名称>` 访问。
- 以下展示的颜色适用于非 Monet 模式。

## 基础与组件色槽

主色与错误色、状态蒙层以及组件级 token。

| 名称 | 用途 | 浅色 | 深色 |
|------|------|------|------|
| `primary` | 主色 | `#FF0080FF` | `#FF1A8CFF` |
| `onPrimary` | 主色上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `error` | 错误色 | `#FFDB382C` | `#FFFF6C61` |
| `onError` | 错误色上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledOnPrimaryButton` | 按钮禁用主色上的文本（COUI `coui_button_main_disable_text`） | `#8AFFFFFF` | `#29FFFFFF` |
| `secondaryVariant` | 次要色变体 | `#14000000` | `#26FFFFFF` |
| `disabledSecondaryVariant` | 禁用次要色变体 | `#14000000` | `#1AFFFFFF` |
| `secondaryContainer` | 次要色容器 | `#14000000` | `#26FFFFFF` |
| `surfaceContainerHighest` | 最高对比表面容器 | `#FFFFFFFF` | `#FF1E1E1E` |
| `outline` | 轮廓/边框 | `#26000000` | `#26FFFFFF` |
| `sliderBackground` | 滑块背景 | `#1F000000` | `#33FFFFFF` |
| `dialogButton` | 对话框按钮栏按钮填充（COUI `couiColorContainer8`） | `#14000000` | `#1AFFFFFF` |
| `dragHandle` | 底部抽屉拖拽指示器（COUI `couiColorControls`） | `#29000000` | `#40FFFFFF` |
| `secondaryButton` | 次级按钮填充（COUI `couiColorContainer8` 合成在 `couiColorBackgroundElevated` 上） | `#FFEBEBEB` | `#FF353535` |
| `disabledSecondaryButton` | 禁用次级按钮填充（COUI `couiColorContainer8`） | `#14000000` | `#1AFFFFFF` |
| `disabledOnSecondaryButton` | 禁用次级按钮文字（COUI `coui_btn_secondary_text_color_disable`） | `#42000000` | `#28FFFFFF` |
| `transparentButton` | 透明按钮填充（COUI `coui_button_translate_color`） | `#33FFFFFF` | `#33FFFFFF` |
| `onTransparentButton` | 透明按钮文字 | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledOnTransparentButton` | 禁用透明按钮文字 | `#4DFFFFFF` | `#4DFFFFFF` |
| `backgroundTop` | 最上层浮层表面，如 Snackbar（COUI `couiColorBackgroundTop`） | `#FFFFFFFF` | `#FF333333` |
| `primaryTextOnPopup` | 浮层上的强调文字，如 Snackbar 操作（COUI `couiColorPrimaryTextOnPopup`） | `#FF0080FF` | `#FF5C9DFF` |
| `press` | 按压态蒙层（COUI `couiColorPress`），由 `COUIIndication` 绘制 | `#1F000000` | `#33FFFFFF` |
| `hover` | 悬停态蒙层（COUI `couiColorHover`），由 `COUIIndication` 绘制 | `#14000000` | `#26FFFFFF` |
| `focus` | 列表行焦点/选中蒙层（COUI `couiColorFocus`），由 `COUIIndication` 绘制 | `#1F0080FF` | `#331A8CFF` |
| `disabledProgress` | 禁用线性进度条的进度色（COUI `coui_progressbar_color_disabled`） | `#FFE6E6E6` | `#FF262626` |
| `disabledProgressBackground` | 禁用线性进度条的轨道色（COUI `coui_progressbar_background_color_disabled`） | `#08000000` | `#33FFFFFF` |
| `disabledSliderThumb` | 禁用 Slider 的滑块与进度内关键点（COUI `coui_seekbar_thumb_disable_color`） | `#8AFFFFFF` | `#29FFFFFF` |
| `disabledSliderKeyPoint` | 禁用 Slider 的进度外关键点（COUI `coui_seekbar_inactive_mark_disable_color`） | `#42000000` | `#29FFFFFF` |
| `sliderThumbShadow` | Slider 滑块阴影（COUI `coui_seekbar_thumb_shadow_color`） | `#1A000000` | `#40000000` |
| `disabledTextFieldBorder` | 禁用 TextField 的边框 / 下划线（COUI `coui_textinput_stroke_color_disabled`） | `#19000000` | `#19FFFFFF` |
| `quarternaryNeutral` | 最浅的中性色，如禁用的 Checkbox / RadioButton 外框（COUI `couiColorQuarternaryNeutral`） | `#14000000` | `#26FFFFFF` |
| `container8` | 半透明容器填充，如未选中的 Chip（COUI `couiColorContainer8`） | `#14000000` | `#1AFFFFFF` |
| `disabledChipText` | 禁用且未选中的 Chip 标签（COUI `chip_unchecked_text_disable_color`） | `#42000000` | `#29FFFFFF` |
| `focusOutline` | 键盘焦点描边，如 Chip 焦点描边（COUI `couiColorFocusOutline`） | `#660080FF` | `#665C9DFF` |

## COUI 语义色

以下槽位以官方名称承载 COUI `Theme.COUI` / `Theme.COUI.Dark` 的颜色属性（`couiColorXxx` → `xxx`）。

| 名称 | COUI 属性 | 浅色 | 深色 |
|------|-----------|------|------|
| `primaryText` | `couiColorPrimaryText`（也是 `couiColorOnSecondary`） | `#FF0080FF` | `#FF5C9DFF` |
| `labelTheme` | `couiColorLabelTheme` | `#FF0080FF` | `#FF1A8CFF` |
| `link` | `couiColorLink` | `#FF0080FF` | `#FF1A8CFF` |
| `disable` | `couiColorDisable` | `#4D0080FF` | `#4D1A8CFF` |
| `containerTheme` | `couiColorContainerTheme` | `#FF0080FF` | `#FF1A8CFF` |
| `containerThemeHalftone` | `couiColorContainerThemeHalftone` | `#260080FF` | `#401A8CFF` |
| `secondary` | `couiColorSecondary`（主题色 15% / 25%） | `#260080FF` | `#401A8CFF` |
| `textColorHighlight` | `android:textColorHighlight`（`?couiColorSecondary`） | `#260080FF` | `#401A8CFF` |
| `errorDisabled` | `couiColorErrorDisabled` | `#4DDB382C` | `#4DFF6C61` |
| `errorTextBg` | `couiColorErrorTextBg` | `#4DE82A18` | `#FFEA3939` |
| `success` | `couiColorSuccess` | `#FF00BD13` | `#FF24B232` |
| `tips` | `couiColorTips` | `#FF0080FF` | `#FF1A8CFF` |
| `additional` | `couiColorAdditional` | `#FF0080FF` | `#FF1A8CFF` |
| `assist` | `couiColorAssist` | `#FF5C93E5` | `#FF5C93E5` |
| `background` | `couiColorBackground` | `#FFFFFFFF` | `#FF000000` |
| `surface` | `couiColorSurface`（对话框、底部面板、弹出菜单面板） | `#FFFFFFFF` | `#FF1E1E1E` |
| `backgroundElevated` | `couiColorBackgroundElevated` | `#FFFFFFFF` | `#FF1E1E1E` |
| `backgroundElevatedWithCard` | `couiColorBackgroundElevatedWithCard` | `#FFF0F1F2` | `#FF1E1E1E` |
| `backgroundWithCard` | `couiColorBackgroundWithCard`（`Scaffold` 等页面底色） | `#FFF0F1F2` | `#FF000000` |
| `surfaceTop` | `couiColorSurfaceTop` | `#FFFFFFFF` | `#FF333333` |
| `surfaceWithCard` | `couiColorSurfaceWithCard` | `#FFF0F1F2` | `#FF1E1E1E` |
| `bar` | `couiColorBar` | `#FFFAFAFA` | `#FF1E1E1E` |
| `bottomBar` | `couiColorBottomBar` | `#FFFAFAFA` | `#FF1E1E1E` |
| `card` | `couiColorCard` | `#FFFFFFFF` | `#1AFFFFFF` |
| `cardBackground` | `couiColorCardBackground` | `#FFFFFFFF` | `#1AFFFFFF` |
| `cardPressed` | `couiColorCardPressed` | `#FFE6E6E6` | `#33FFFFFF` |
| `labelPrimary` | `couiColorLabelPrimary` | `#E6000000` | `#E6FFFFFF` |
| `labelPrimaryVariant` | `couiColorLabelPrimaryVariant` | `#B8000000` | `#B8FFFFFF` |
| `labelSecondary` | `couiColorLabelSecondary` | `#8A000000` | `#8AFFFFFF` |
| `labelSecondaryVariant` | `couiColorLabelSecondaryVariant` | `#66000000` | `#66FFFFFF` |
| `labelTertiary` | `couiColorLabelTertiary` | `#42000000` | `#4DFFFFFF` |
| `labelQuaternary` | `couiColorLabelQuaternary` | `#14000000` | `#26FFFFFF` |
| `labelOnColor` | `couiColorLabelOnColor` | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledNeutral` | `couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |
| `hintNeutral` | `couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| `container4` | `couiColorContainer4` | `#0A000000` | `#1AFFFFFF` |
| `container12` | `couiColorContainer12` | `#1F000000` | `#33FFFFFF` |
| `container16` | `couiColorContainer16` | `#29000000` | `#40FFFFFF` |
| `containerSolid` | `couiColorContainerSolid` | `#FFEBEBEB` | `#FF262626` |
| `fillSolid` | `couiColorFillSolid` | `#FFEBEBEB` | `#FF262626` |
| `fillThin` | `couiColorFillThin` | `#0A000000` | `#14FFFFFF` |
| `controls` | `couiColorControls` | `#29000000` | `#40FFFFFF` |
| `divider` | `couiColorDivider` | `#1F000000` | `#33FFFFFF` |
| `mask` | `couiColorMask` | `#33000000` | `#99000000` |
| `pressBackground` | `couiColorPressBackground` | `#14000000` | `#26FFFFFF` |
| `ripplePressBackground` | `couiColorRipplePressBackground` | `#99B4B4B4` | `#80FFFFFF` |

### 色相色板

每个色相有主色（`couiColor<Hue>`）、`<hue>Variant`（`couiColor<Hue>Variant`）和 `container<Hue>` 浅底
（`couiColorContainer<Hue>`）。色板是固定的：主题色族和 Monet 都不会改变它。

| 色相 | 主色（浅 / 深） | Variant（浅 / 深） | Container（浅 / 深） |
|------|-----------------|--------------------|----------------------|
| `red` | `#EB3B2F` / `#EB493D` | `#E56E65` / `#CC615A` | `#EEDADC` / `#3A3030` |
| `orange` | `#FF7700` / `#F08222` | `#E58F45` / `#CC7F3D` | `#FAE7D6` / `#39332C` |
| `yellow` | `#FFB200` / `#E5A100` | `#E5AF2E` / `#C29427` | `#F6E7C6` / `#373530` |
| `green` | `#00BD13` / `#24B232` | `#4BBD57` / `#43A84E` | `#BADECA` / `#353E36` |
| `mint` | `#00CC88` / `#1AB883` | `#39BD91` / `#32A881` | `#BADEDB` / `#394342` |
| `cyan` | `#1DBACC` / `#25A9B8` | `#39AFBD` / `#329DA8` | `#BEDADD` / `#343E3D` |
| `azure` | `#00A1FF` / `#209DE5` | `#45ABE5` / `#3D98CC` | `#CEE5EB` / `#373F42` |
| `blue` | `#0080FF` / `#1A8CFF` | `#5C93E5` / `#5C93E5` | `#C9DAE4` / `#323739` |
| `violet` | `#5A47FF` / `#7161FF` | `#7E73E5` / `#7E73E5` | `#D6D5EE` / `#3A3A43` |
| `purple` | `#A723FF` / `#AC33FF` | `#B873E5` / `#A466CC` | `#E0D6EB` / `#3A3540` |

## 暗色标志

`Colors.isDark`（也可用 `COUITheme.isDark`）表示当前方案是否为暗色，对应 COUI `isCOUIDarkTheme`。
`lightColorScheme()`、`darkColorScheme()` 和 Monet 方案都会设置它；请用它代替根据颜色亮度推断。
