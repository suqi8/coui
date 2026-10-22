# 颜色系统

本页列出了 COUI 颜色方案中的所有颜色及其默认的浅色/深色数值。数值为 ARGB 十六进制。

## 使用方式

- 在组合函数中通过 `COUITheme.colorScheme.<名称>` 访问。
- 以下展示的颜色适用于非 Monet 模式。

## 所有颜色

| 名称 | 用途 | 浅色 | 深色 |
|------|------|------|------|
| `primary` | 主色 | `#FF0080FF` | `#FF1A8CFF` |
| `onPrimary` | 主色上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `primaryVariant` | 主色变体 | `#FF0080FF` | `#FF1A8CFF` |
| `onPrimaryVariant` | 主色变体上的文本 | `#FFAECDFF` | `#FF99C7F1` |
| `error` | 错误色 | `#FFDB382C` | `#FFFF6C61` |
| `onError` | 错误色上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `errorContainer` | 错误容器 | `#26EB3B2F` | `#40EB493D` |
| `onErrorContainer` | 错误容器上的文本 | `#FFDB382C` | `#FFFF6C61` |
| `disabledPrimary` | 禁用主色（开关/按钮/滑块） | `#4D0080FF` | `#4D1A8CFF` |
| `disabledOnPrimary` | 禁用主色上的文本 | `#FFFFFFFF` | `#FF404040` |
| `disabledPrimaryButton` | 按钮禁用主色 | `#4D0080FF` | `#4D1A8CFF` |
| `disabledOnPrimaryButton` | 按钮禁用主色上的文本 | `#8AFFFFFF` | `#28FFFFFF` |
| `disabledPrimarySlider` | 滑块禁用主色 | `#4D0080FF` | `#4D1A8CFF` |
| `primaryContainer` | 主色容器 | `#FF0080FF` | `#FF1A8CFF` |
| `onPrimaryContainer` | 主色容器上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `secondary` | 次要色 | `#FFE5E5E5` | `#FF757575` |
| `onSecondary` | 次要色上的文本 | `#FFFFFFFF` | `#FFFFFFFF` |
| `secondaryVariant` | 次要色变体 | `#14000000` | `#26FFFFFF` |
| `onSecondaryVariant` | 次要色变体上的文本 | `#E6000000` | `#E6FFFFFF` |
| `disabledSecondary` | 禁用次要色 | `#FFF2F2F2` | `#4D757575` |
| `disabledOnSecondary` | 禁用次要色上的文本 | `#42000000` | `#4DFFFFFF` |
| `disabledSecondaryVariant` | 禁用次要色变体 | `#14000000` | `#1AFFFFFF` |
| `disabledOnSecondaryVariant` | 禁用次要色变体上的文本 | `#42000000` | `#4DFFFFFF` |
| `secondaryContainer` | 次要色容器 | `#14000000` | `#26FFFFFF` |
| `onSecondaryContainer` | 次要色容器上的文本 | `#66000000` | `#66FFFFFF` |
| `secondaryContainerVariant` | 次要色容器变体 | `#14000000` | `#26FFFFFF` |
| `onSecondaryContainerVariant` | 次要色容器变体上的文本 | `#66000000` | `#66FFFFFF` |
| `tertiaryContainer` | 三级色容器 | `#260080FF` | `#401A8CFF` |
| `onTertiaryContainer` | 三级色容器上的文本 | `#FF0080FF` | `#FF5C9DFF` |
| `tertiaryContainerVariant` | 三级色容器变体 | `#260080FF` | `#401A8CFF` |
| `background` | 应用背景 | `#FFFFFFFF` | `#FF1E1E1E` |
| `onBackground` | 背景上的文本 | `#E6000000` | `#E6FFFFFF` |
| `onBackgroundVariant` | 背景上的次级文本 | `#8A000000` | `#8AFFFFFF` |
| `surface` | 表面 | `#FFF0F1F2` | `#FF000000` |
| `onSurface` | 表面上的文本 | `#E6000000` | `#E6FFFFFF` |
| `surfaceVariant` | 表面变体 | `#FFFFFFFF` | `#1AFFFFFF` |
| `onSurfaceSecondary` | 表面上的次要文本 | `#8A000000` | `#8AFFFFFF` |
| `onSurfaceVariantSummary` | 表面变体上的摘要文本 | `#66000000` | `#66FFFFFF` |
| `onSurfaceVariantActions` | 表面变体上的操作文本 | `#42000000` | `#4DFFFFFF` |
| `disabledOnSurface` | 禁用状态下的表面文本 | `#42000000` | `#4DFFFFFF` |
| `surfaceContainer` | 表面容器 | `#FFFFFFFF` | `#1AFFFFFF` |
| `onSurfaceContainer` | 表面容器上的文本 | `#E6000000` | `#E6FFFFFF` |
| `onSurfaceContainerVariant` | 表面容器上的次级文本 | `#66000000` | `#66FFFFFF` |
| `surfaceContainerHigh` | 高对比表面容器 | `#FFE6E6E6` | `#33FFFFFF` |
| `onSurfaceContainerHigh` | 高对比表面容器上的文本 | `#42000000` | `#4DFFFFFF` |
| `surfaceContainerHighest` | 最高对比表面容器 | `#FFFFFFFF` | `#FF1E1E1E` |
| `onSurfaceContainerHighest` | 最高对比表面容器上的文本 | `#E6000000` | `#E6FFFFFF` |
| `outline` | 轮廓/边框 | `#26000000` | `#26FFFFFF` |
| `dividerLine` | 分隔线 | `#1F000000` | `#33FFFFFF` |
| `windowDimming` | 窗口遮罩（对话框/下拉） | `#33000000` | `#99000000` |
| `sliderKeyPoint` | 滑块关键点 | `#FFF2F2F2` | `#FF595959` |
| `sliderKeyPointForeground` | 滑块关键点前景 | `#FF6EB5FF` | `#FF5DAAFF` |
| `sliderBackground` | 滑块背景 | `#1F000000` | `#33FFFFFF` |
| `dialogButton` | 对话框按钮栏按钮填充（COUI `couiColorContainer8`） | `#14000000` | `#1AFFFFFF` |
| `dragHandle` | 底部抽屉拖拽指示器（COUI `couiColorControls`） | `#29000000` | `#40FFFFFF` |
| `secondaryButton` | 次级按钮填充（COUI `couiColorContainer8` 合成在 `couiColorBackgroundElevated` 上） | `#FFEBEBEB` | `#FF353535` |
| `disabledSecondaryButton` | 禁用次级按钮填充（COUI `couiColorContainer8`） | `#14000000` | `#1AFFFFFF` |
| `disabledOnSecondaryButton` | 禁用次级按钮文字 | `#42000000` | `#28FFFFFF` |
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
