<!-- Copyright 2026, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# ColorOS 17 (COUI17) Retheme Spec

> COUI16 → COUI17 换肤的权威规范。来源于对 ColorOS 17 系统应用的逆向取证：
> `com.android.settings`(设置 17.0.0)、`com.coloros.bootreg`(开机注册 17.0.237)。
> 逆向产物见 `E:\AndroidStudioProjects\jadx\work\coui17_settings\` / `coui17_setup\`，token 结论 `coui17_settings/notes.md`。
>
> **策略延续 COUI16：只改值，不重命名主题类/色槽。** 组件读 `COUITheme.colorScheme.*` / `textStyles.*`，绝不硬编码 `Color(0x...)`。
> 与 16 的关系：**17 只改少量 token（主要是主色蓝系更通透）+ 新增凝光/柔性反馈/搜索栏等新形态**；背景层级、卡片、文字 alpha、分隔线、遮罩与 16 完全一致。

## 0. COUI17 主色（相对 16 唯一显著变化）

| slot | COUI16 | **COUI17 L** | **COUI17 D** | 来源 |
| :-- | :-- | :-- | :-- | :-- |
| primary | `#0066FF`/`#247CFF` | **`#0080FF`** | **`#1A8CFF`** | `coui_color_blue` / values-night |
| primaryVariant | 同 primary | `#0080FF` | `#1A8CFF` | 复用 |
| primaryContainer | `#0066FF`/`#5C9DFF` | `#0080FF` | `#1A8CFF` | coui_color_label_theme_blue |
| onPrimaryContainer | `#FFFFFF` | `#FFFFFF` | `#FFFFFF` | coui_color_label_on_color |
| disabledPrimary / disabledPrimaryButton | `#4D0066FF`/`#4D247CFF` | `#4D0080FF` | `#4D1A8CFF` | 30% 主色 |
| tertiaryContainer | `#260066FF`/`#40247CFF` | `#260080FF` | `#401A8CFF` | coui_color_secondary_blue |
| onTertiaryContainer | `#0066FF`/`#5C9DFF` | `#0080FF` | `#5C9DFF` | couiColorOnSecondary = ?couiColorPrimaryText = coui_color_primary_text_blue（更正：暗色不是 `#1A8CFF`） |

> **其余色槽（secondary/error/surface/background/文字族/divider/mask/slider 等）与 COUI16 完全一致，见 `coui-retheme-spec.md` §2，逐项沿用。**
> 17 背景层级：页面灰底 `surface` L `#F0F1F2` / D `#000000`；白卡 `surfaceContainer` L `#FFFFFF` / D `#1AFFFFFF`；`background`（面板）L `#FFFFFF` / D `#1E1E1E`。

## 1. COUI17 新设计语言特征（"流体设计 / 凝光视效"）

官方口径：凝光视效（光成为界面指引）+ 流体动效（全局连贯）+ 柔性反馈（控件按压/拖拽/回弹）。资源侧落地：

| 特征 | 资源证据 | 落点（拟） |
| :-- | :-- | :-- |
| **凝光（光效/渐变）** | lock_screen / numeric_keyboard 内外圈 gradient、`coui_color_checked_background_gradient_start` | 控件选中态加光晕/渐变，shader 层 |
| **柔性反馈（按压态）** | `coui_color_ripple_press_background` L `#1F000000`(light) / D `#80FFFFFF`(dark)（另 `_dark=#80FFFFFF` `_light=#1F000000`，夜资源 `#3D3D3D`/`#99B4B4B4`）；`coui_color_press` `#1F000000`/`#33FFFFFF` | 各组件 press indication |
| **hover 态** | `coui_color_hover` `#14000000`/`#26FFFFFF` | hover indication |
| **勾选动画化** | `coui_checkbox_*__17`、`coui_shape_checkbox_*__17`、`coui_btn_radio_*__17` 动画族 | Checkbox/Radio 组件级新形态（专项） |

> 按压/悬停 token 与 16 的 `couiColorPress` 12%/20% 体系同源（`#1F=12%` 黑、`#33=20%` 白）。更正：`coui_color_ripple_press_background` 在 ColorOS 17 上**不会被消费**——`COUIMaskRippleDrawable` 仅 `SDK_INT<34` 用它、`==34` 用 `couiColorPressBackground`、`>=35` 用 `couiColorPress`（`COUIMaskRippleDrawable.java:50-56,184-190`），无需新增 ripple 槽。色板已新增 `press`/`hover`/`focus` 三槽（couiColorPress/Hover/Focus），由 `COUIIndication` 消费。

## 2. 圆角（squircle）体系 —— 基准 radius + weight（17 扩展为四档变体）

`coui_round_corner_*`：基础值 + `_radius`（实际绘制）+ `_radius_normal_16_1` + `_radius_large_16_1` + `_weight`（连续曲率平滑度）。

| 档 | 基础 | 实际 radius | normal_16_1 | large_16_1 | weight |
| :-- | :-- | :-- | :-- | :-- | :-- |
| full | 72 | — | — | — | — |
| xxxl | 28 | 22 | 34 | 39 | — |
| xxl | 24 | 19 | 29 | 34 | 1.1 |
| xl | 20 | 16 | 24 | 28 | 1.1 |
| l | 16 | 13 | 19 | 23 | 0.99 |
| m | 12 | 9 | 15 | 17 | 0.99 |
| s | 8 | 8 | 10 | 11 | 1.5 |
| xs | 4 | 4 | 5 | 5 | 1.5 |

落点（相对 16 的调整）：

| 落点 | 16 | **17 目标** | 来源 |
| :-- | :-- | :-- | :-- |
| `CardDefaults.CornerRadius` | 12dp | **24dp**（更正：`COUICardListSelectedItemLayout.getInnerRadius` 在平滑圆角开启且 OS>37 时直接取 `coui_round_corner_xl_radius_normal_16_1`=24dp、权重 2.5；平滑关闭回退 `couiRoundCornerXL`=20dp。`coui_preference_card_radius`=12 只被 Settings 自有图片组件消费） | 高 |
| `DialogContentLayout` 底角 | 33dp | **34dp**（coui_dialog_os_16_1_radius_33_dp=34） | 中 |
| 对话框主体圆角 | — | 24dp / 旋转 12dp（coui_dialog_background_corner_radius） | 中 |
| Squircle Extension | 1.2819 | **分档 weight**：s/xs=1.5、xl/xxl=1.1、l/m=0.99（精确化，高风险，flag） | 低·flag |

## 3. COUI17 设置主页 TopBar（重点迁移对象）

> **深度漏点对照已做（jadx 反编译 `com.coui.appcompat.toolbar.*`）**，结论存
> `E:/AndroidStudioProjects/jadx/work/coui17_settings/topbar-audit.md`。以下 TopBar 数值为 17 权威值。

### 3.0 用户决策（迁移原则）

1. **本库未对齐 COUI 规范的内容，不保留——对齐不到就去除**，不得私自保留或发明。
2. **TopBar 模糊半径**：用 `coui_list_dialog_background_blur_radius` 类 **240dp** 量级（对话框模糊半径）。注意区分场景：
   - TopBar「凝光」是**渐变模糊**（Oplus 系统级 gradient blur：start 30px→end 0px，speed 3，滚动驱动），不是固定半径。
   - TopBar Material 三档：DEFAULT=0px / IMMERSIVE=25px / DEFAULT_BLUR=20px。
   - **240dp 是对话框/弹窗背景的模糊半径**（`coui_list_dialog_background_blur_radius`），用于 Dialog/ListDialog，不是 TopBar。
   - 跨平台库无法直接用 Oplus 系统级模糊，按平台能力近似：Android 用 RenderEffect blur，其它平台用半透明渐变底。

容器链 `settings_homepage_container_oplus.xml`（`HomePageCoordinatorLayout`）：

- 内容层 `main_content_scrollable_container`（`COUIFloatingScrollingViewBehavior`）
- `search_bg_mask`（`BackgroundMaskStyle`，背景 `@color/search_view_window_mask`，默认 gone）
- **`AppBarBlurOverlayView`**（滚动驱动 alpha，`AppBarBlurOverlayBehavior`）—— 17 顶栏毛玻璃/凝光叠加层
- `stub_toolbar_layout` → `homepage_toolbar.xml`

`homepage_toolbar.xml`：
- `SettingsCollapsableAppBarLayout`（hasDivider=false, elevation=0）
  - `SettingsCollapsingToolbarLayout`（style `SettingsCOUICollapsingToolbarLayoutStyle`，title=settings_label）
    - `COUIToolbar`（paddingLeft/Right=16dp=coui_preference_card_margin_horizontal，style `COUICollapsedToolbarStyle`）
  - `SearchViewAnimate`（高 52dp，elevation=1sp，滚动 `scroll|exitUntilCollapsed|snap`，语音图标 + 取消按钮）

TopBar 关键 style/dimen（17 权威值）：

| 项 | 值 | 来源 |
| :-- | :-- | :-- |
| 设置主页 Collapsing 高 | **116dp** | SettingsCOUICollapsingToolbarLayoutStyle layout_height |
| 展开标题 gravity | start\|bottom，marginBottom 18dp | 同上 |
| 折叠/展开标题字号 | **40sp**（settings 覆写，COUIAppbarTitleStyle 父） | SettingsCollapsingTitleCollapsed/Expanded |
| 通用 appbar 展开高 | 110dp（带搜索 158dp） | coui_appbar_title_expanded_height |
| 大标题 margin top | 54dp | coui_appbar_title_expanded_margin_top |
| 大标题 margin start/end | 24dp（compat16/medium24/expanded40） | 响应式 |
| 大标题字号 | 32dp（sans-serif-semibold，lineSpacing 1.4） | COUIAppbarTitleStyle.Expanded |
| 折叠标题字号 | 24dp | COUIAppbarTitleStyle.Collapsed |
| toolbar 折叠高 | 52dp | coui_appbar_title_toolbar_height |
| 展开分隔线内缩 | 24dp / 折叠 0dp | coui_appbar_divider_*_margin_horizontal |
| 搜索框高 | 52dp | coui_search_view_height |
| 搜索框样式 | 圆角 40dp 胶囊、elevation 0、描边 1dp outlineVariant | SearchBarStyle_v2 |
| 工具栏菜单胶囊 | radius 24dp、icon 24dp | coui_toolbar_menu_bg_radius |
| nav↔title / title↔menu / search↔menu 间距 | 4 / 4 / 8dp | coui_toolbar_gap_* |
| 顶栏分段控件宽 | min 108 / max 207dp | coui_toolbar_segment_button_* |

### 3.1 TopBar 按钮（COUI17 权威，jadx 实证）

| 项 | 值 | 来源 |
| :-- | :-- | :-- |
| 图标按钮尺寸 | **48×40dp**（minW=48 / minH=40） | `coui_action_menu_item_min_width/height`，ToolbarMaterialHelper.applyIconButtonSize |
| 图标按钮 padding | 0dp | coui_toolbar_menu_bg_padding_horizontal |
| 文字按钮尺寸 | minW=60 / maxW=104 / minH=40dp，padding 12dp | coui_action_text_menu_item_min_width、max_width、text_menu_bg_padding |
| 返回键（tiny）宽 | 48dp | coui_toolbar_back_view_tiny_width |
| 菜单图标 | 24dp（最大 48dp 槽） | coui_toolbar_menu_icon_size |
| item 间距 / marginEnd | 0 / 0 | coui_actionbar_menuitemview_item_spacing、item_view_margin_end |
| overflow padding | 9dp | overflow_button_padding_horizontal |
| 菜单胶囊半径 | 24dp | coui_toolbar_menu_bg_radius |
| 按钮面板底色 | L `#FFF0F0F0` / D `#FF333333` / immersive `#26FFFFFF` | ToolbarMaterialEffectDelegate |
| 按钮非模糊叠加 | `#1A000000` / `#33FFFFFF`（于 surfaceWithCard 上） | 同上 |
| 顶栏 padding（左右） | compat 16 / medium 24 / expanded 40（menu 侧 4/12/28） | dimens.xml L4978-4994 |

### 3.2 TopBar 模糊/凝光/渐变/透明度（COUI17 权威，jadx 实证）

**凝光渐变模糊**（AppBarBlurHelper + AppBarBlurParams）：
- Oplus 系统级 `createGradientBlurEffect(startBlurRadius=30px, endBlurRadius=0px, gradientBlurSpeed=3)`，作用于 `AppBarBlurOverlayView`。
- 渐变底色 `GradientBackgroundDrawable`：同一基色 + alpha 渐变，方向**下→上**（bottom=pos0）。
  - 浅色 3 档 positions[0, 0.4, 1] × alphas[0, 0.7, 1]
  - 深色 2 档 positions[0, 1] × alphas[0, 0.4]
  - 基色回退：AppBar 背景 → windowBackground → `couiColorBackgroundWithCard`。
- 滚动驱动：alpha=min(1, scroll/30dp)；0 时隐藏；fraction 同步缩放模糊半径与底色 alpha。
- overlay 额外高度：固定 8dp / 滚动驱动 24dp。

**Material 三档**（COUIMaterialEffectType）：
| 档 | 值 | blurRadius | blend 层1 / 层2 |
| :-- | :-- | :-- | :-- |
| TYPE_DEFAULT | 0 | 0px | L `#66333333`COLOR_DODGE / `#99F5F5F5`LUMINOSITY；D `#CC262626`SRC_OVER / `#B37F7F7F`OVERLAY |
| TYPE_IMMERSIVE | 1 | 25px | `#1A6E6E6E` / `#24F1F1F1`（明暗同参） |
| TYPE_DEFAULT_BLUR | 2 | 20px | 同 DEFAULT |

**普通模糊**：radius 50px，底色 alpha 0.8（深色 0.6）。
**对话框模糊**：`coui_list_dialog_background_blur_radius` = **240dp**（用户确认口径，用于 Dialog/ListDialog 背景）。
**搜索框无模糊兜底**：`coui_search_bar_no_blur_solid` L `#64FFFFFF` / D `#28767676`。
**凝光选中态**：`coui_color_checked_background_gradient_start` = `#40FFFFFF`。
**模糊开启时隐藏分隔线**（setBlurDividerSuppressed）。

### 3.3 分段控件 COUISegmentButtonLayout（17 材质化新增）

- 容器 28dp 高、padding 4（tiny 2）、项 padding 12、overDistance 4、switchDistance 5。
- 容器模糊 `TYPE_CONTENT_SEGMENT_BUTTON`（25px + blend 层 L `#7F333333`/`#99EFEFEF`、D `#7F7F7F7F`/`#99262626`）。
- 滑块 L `#B2FFFFFF` / D `#33FFFFFF`；滑块阴影 offset 2dp、radius 1dp、起 `#0D000000` 止 `#03000000`（D 起 `#14000000`）。
- 底 L `#0F000000` / D `#1FFFFFFF`。

## 4. 迁移批次（顺序：基础 → TopBar）

- **批次 A — Colors.kt 蓝系刷新**（高 ROI，零代码风险）：primary 家族 + tertiary 蓝系 + primaryContainer 改为 17 蓝（`#0080FF`/`#1A8CFF`），light/dark 双套。其余色槽不动。
- **批次 B — TopBar 组件迁移**：复刻设置主页 TopBar（大标题 40sp/116dp 展开 + 折叠 52dp + 状态栏融合 + 搜索框胶囊 52dp + 毛玻璃叠加层），落到 `TopAppBar` / 搜索组件。
- **批次 C — 圆角细化（可选）**：Dialog 底角 33→34、对话框主体 24、squircle 分档 weight（高风险，独立 flag）。
- **批次 D — 柔性反馈/凝光（可选，组件级）**：按压 ripple 槽、checkbox/radio 勾选动画、控件选中态光晕 —— 均为组件级专项，另行立项。

## 5. 待校准项（下一轮，需 jadx 看消费逻辑）

1. `AppBarBlurOverlayView` 的模糊半径/颜色/材质（凝光在设置页顶栏的具体落点）。
2. 动效曲线（spring/damping/ease）17 是否相对 16 调整（res 未含，需看 COUISpringForce/MoveEase 类常量）。
3. 开机注册(`com.coloros.bootreg`)的 17 设计语言资源逐项对照（203 个 coui_ 色值）。
4. checkbox/radio `__17` 勾选动画的具体形态（vector/animator 资源）。
5. `coui_color_ripple_press_background` 暗色 `#3D3D3D` 与 `#99B4B4B4` 的分场景含义（selector）。
