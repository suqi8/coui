<!-- Copyright 2025, compose-miuix-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# ColorOS 16 (COUI) Retheme Spec

> miuix → COUI 换肤的唯一权威规范。来源于对 ColorOS 16(OnePlus PJE110, V16.0.0)系统应用的逆向取证:
> `com.oplus.uxdesign`(设计语言主库)、`com.coloros.alarmclock`、运行时实测。逆向产物见 `D:\AndroidStudioProjects\jadx\work\`。
>
> **策略:只改值,不重命名主题类/色槽。** 组件读 `MiuixTheme.colorScheme.*` / `textStyles.*`,绝不硬编码 `Color(0x...)`。
> 约定:`#RRGGBB` / `#AARRGGBB`;L=浅色 D=深色;透明度前缀 12%=`1F` 15%=`26` 30%=`4D` 40%=`66`。

## 0. 主色(四方互证)

| | L | D | 来源 |
| :-- | :-- | :-- | :-- |
| **primary** | `#0066FF` | `#247CFF` | `coui_color_blue` / `_dark`(uxdesign + clock + 运行时 `sysui_type_accent_color`) |

miuix 原值:L `#3482FF` / D `#277AF7`(HyperOS 蓝)。

## 1. ⚠ 关键:背景层级是"反相"的(已校验消费点)

ColorOS = **灰底 + 白卡**;miuix 现状 = 白底 + 浅灰面。已确认 miuix 实际消费点:

| 色槽 | 消费者(已 grep 确认) | COUI 目标 L | COUI 目标 D |
| :-- | :-- | :-- | :-- |
| `surface` | **Scaffold 页面底**、Surface、TopAppBar、NavigationBar/Rail、TabRow 底 | `#F0F1F2`(灰底) | `#000000`(纯黑 AMOLED) |
| `surfaceContainer` | **Card**、ListPopup、Dropdown、FloatingToolbar、CascadingPopup | `#FFFFFF`(白卡) | `#1AFFFFFF`(10% 白) |
| `surfaceVariant` | (卡片同义) | `#FFFFFF` | `#1AFFFFFF` |
| `background` | **仅 Dialog / BottomSheet 面板背景** | `#FFFFFF`(白面板,**勿刷灰**) | `#1E1E1E` |

> **校正综合研究的 §8.1**:研究建议把 `background` 也设灰 `#F0F1F2` 是错的——`background` 只被对话框/底部表单当面板背景用,刷灰会让弹窗发灰。`background` 应保持白/elevated。页面"灰底"由 `surface` 承担。

## 2. Colors.kt 全色槽落点表

`lightColorScheme()` @341 / `darkColorScheme()` @451。置信度:高/中/低。标 *保留* 的无良好 COUI 源,维持 miuix 原值。

| slot | L | D | COUI 来源 | 置信 |
| :-- | :-- | :-- | :-- | :-- |
| primary | `#0066FF` | `#247CFF` | coui_color_blue/_dark | 高 |
| onPrimary | `#FFFFFF` | `#FFFFFF` | coui_color_label_on_color | 高 |
| primaryVariant | `#0066FF` | `#247CFF` | 复用 coui_color_blue | 中 |
| onPrimaryVariant | *保留* `#AECDFF` | *保留* `#99C7F1` | 无干净源 | 低·保留 |
| error | `#DB382C` | `#FF6C61` | coui_color_error/_dark | 高 |
| onError | `#FFFFFF` | `#FFFFFF` | 语义白 | 高 |
| errorContainer | `#26EB3B2F` | `#40EB493D` | coui_color_secondary_red/_dark | 中 |
| onErrorContainer | `#DB382C` | `#FF6C61` | coui_color_label_theme_red/_dark | 中 |
| disabledPrimary | `#4D0066FF` | `#4D247CFF` | 30% 真主色(不照抄资源 #2660F5) | 中 |
| disabledOnPrimary | `#FFFFFF` | `#404040` | coui_seekbar_thumb_color_disabled/_dark | 低 |
| disabledPrimaryButton | `#4D0066FF` | `#4D247CFF` | coui_button_main_disable / values-night | 高 |
| disabledOnPrimaryButton | `#8AFFFFFF` | `#29FFFFFF` | coui_button_main_disable_text / values-night | 高 |
| disabledPrimarySlider | `#EBEBEB` | `#262626` | coui_seekbar_progress_color_disabled_*(去蓝调) | 中 |
| primaryContainer | `#0066FF` | `#5C9DFF` | coui_color_primary_on_popup_blue/_dark | 中 |
| onPrimaryContainer | `#FFFFFF` | `#FFFFFF` | coui_color_label_on_color | 高 |
| secondary | `#E5E5E5` | `#757575` | switch_unchecked_bar_light/dark(values-night 确认) | 高 |
| onSecondary | `#FFFFFF` | `#FFFFFF` | switch_outer_circle_color(拇指恒白) | 高 |
| secondaryVariant | `#14000000` | `#26FFFFFF` | coui_button_sub / press_background_dark | 中 |
| onSecondaryVariant | `#E6000000` | `#E6FFFFFF` | coui_color_label_primary/_dark | 中 |
| disabledSecondary | `#F2F2F2` | `#4D757575` | switch_unchecked_bar_disabled_* | 中 |
| disabledOnSecondary | `#42000000` | `#4DFFFFFF` | coui_color_disabled_neutral/_dark | 低 |
| disabledSecondaryVariant | `#14000000` | `#26FFFFFF` | coui_button_sub_disable | 低 |
| disabledOnSecondaryVariant | `#42000000` | `#4DFFFFFF` | coui_color_disabled_neutral/_dark | 低 |
| secondaryContainer | `#14000000` | `#26FFFFFF` | coui_color_label_quaternary/_dark | 低 |
| onSecondaryContainer | `#66000000` | `#66FFFFFF` | coui_color_label_secondary_variant/_dark | 低 |
| secondaryContainerVariant | `#14000000` | `#26FFFFFF` | coui_color_label_quaternary/_dark | 低 |
| onSecondaryContainerVariant | `#66000000` | `#66FFFFFF` | coui_color_label_secondary_variant/_dark | 低 |
| tertiaryContainer | `#260066FF` | `#40247CFF` | coui_color_secondary_blue/_dark | 高 |
| onTertiaryContainer | `#0066FF` | `#5C9DFF` | coui_color_label_theme_blue/_dark | 高 |
| tertiaryContainerVariant | `#260066FF` | `#40247CFF` | coui_color_secondary_blue/_dark | 中 |
| background | `#FFFFFF` | `#1E1E1E` | 见 §1(对话框面板,**勿刷灰**) | 高·校正 |
| onBackground | `#E6000000` | `#E6FFFFFF` | coui_color_label_primary/_dark | 高 |
| onBackgroundVariant | `#8A000000` | `#8AFFFFFF` | coui_color_label_secondary/_dark | 中 |
| surface | `#F0F1F2` | `#000000` | coui_color_background_with_card/_dark(页面灰底) | 高 |
| onSurface | `#E6000000` | `#E6FFFFFF` | coui_color_label_primary/_dark | 高 |
| surfaceVariant | `#FFFFFF` | `#1AFFFFFF` | coui_color_card/_dark | 高 |
| onSurfaceSecondary | `#8A000000` | `#8AFFFFFF` | coui_color_label_secondary/_dark | 高 |
| onSurfaceVariantSummary | `#66000000` | `#66FFFFFF` | coui_color_label_secondary_variant/_dark | 中 |
| onSurfaceVariantActions | `#42000000` | `#4DFFFFFF` | coui_color_label_tertiary/_dark | 中 |
| disabledOnSurface | `#42000000` | `#4DFFFFFF` | coui_color_disabled_neutral/_dark | 高 |
| surfaceContainer | `#FFFFFF` | `#1AFFFFFF` | coui_color_card/_dark(白卡) | 高 |
| onSurfaceContainer | `#E6000000` | `#E6FFFFFF` | coui_color_label_primary/_dark | 高 |
| onSurfaceContainerVariant | `#66000000` | `#66FFFFFF` | coui_color_label_secondary_variant/_dark | 中 |
| surfaceContainerHigh | `#E6E6E6` | `#33FFFFFF` | coui_color_card_pressed/_dark | 中 |
| onSurfaceContainerHigh | `#42000000` | `#4DFFFFFF` | coui_color_label_tertiary/_dark | 低 |
| surfaceContainerHighest | `#FFFFFF` | `#1E1E1E` | coui_color_background_elevated/_dark | 中 |
| onSurfaceContainerHighest | `#E6000000` | `#E6FFFFFF` | coui_color_label_primary/_dark | 中 |
| outline | `#26000000` | `#26FFFFFF` | coui_textinput_stroke_color_default/_dark | 中 |
| dividerLine | `#1F000000` | `#33FFFFFF` | coui_color_divider/_dark | 高 |
| windowDimming | `#33000000` | `#99000000` | coui_color_mask/_dark | 高 |
| sliderKeyPoint | `#F2F2F2` | `#595959` | coui_seekbar_tick_mark_color/_dark | 中 |
| sliderKeyPointForeground | *保留* `#6EB5FF` | *保留* `#5DAAFF` | 无源 | 低·保留 |
| sliderBackground | `#0D000000` | `#1AFFFFFF` | coui_seekbar_background_color_normal/_dark | 高 |

## 3. 形状(圆角)— 所有圆角 L==D(dimens 无 night 变体)

| 落点 | 现值 | 目标 | COUI 来源 | 置信 |
| :-- | :-- | :-- | :-- | :-- |
| `CardDefaults.CornerRadius` | 16dp | **17dp** | coui_card_list_os_16_1_radius_17_dp | 高 |
| `ButtonDefaults.CornerRadius` | 16dp | **21.5dp**(胶囊=44/2) | coui_btn_drawable_radius_large | 高 |
| `TextFieldDefaults.CornerRadius` | 16dp | **10dp** | coui_textinput_corner_radius | 中 |
| `DialogContentLayout.kt` 底角 `coerceAtLeast(32.dp)` | 32dp | **33dp** | coui_dialog_os_16_1_radius_33_dp | 高 |
| `BottomSheetContentLayout.kt` 28dp | 28dp | ✓ 无需改 | coui_panel_os_16_1_radius_28_dp | 高 |
| `ListPopup` 16dp + `CascadingPopupCornerRadius` 16dp | 16dp | ✓ 无需改 | coui_popup_list_window_os_16_1_radius_16_dp | 高 |
| `TabRowDefaults.TabRowWithContourCornerRadius` 8dp | 8dp | ✓ | coui_corner_radius | 中 |
| `TabRowDefaults.TabRowCornerRadius` | 12dp | (可选)8dp | coui_navigation_item_background_radius(clock-only) | 低·flag |
| `FloatingToolbarDefaults.CornerRadius` | 50dp | (分歧)24dp | coui_toolbar_menu_bg_radius — **改前确认** | 中·flag |

**Squircle 连续曲率**:`SquircleDefaults.Extension`(`SquirclePath.kt`,现 `1.1f`,clamp 1f–2f)。COUI `coui_round_corner_*` 三元组(extent/radius 比):M=1.33 / XL=1.25 / XXL=1.26 / L=1.23 / S·XS=1.0(纯圆)。建议 `1.1f → ~1.25f`,或半径相关(≤8dp 用 1.0,≥12dp 用 1.25)。`_weight` 是 COUI 平滑连续度参数,miuix 无 1:1 旋钮,精确复刻需 shader 改动(flag)。

## 4. 尺寸 — L==D

| 落点 | 现值 | 目标 | COUI 来源 | 置信 |
| :-- | :-- | :-- | :-- | :-- |
| `ButtonDefaults.MinHeight` | 40dp | **44dp** | coui_btn_large_height_min | 高 |
| `ButtonDefaults.InsideMargin` 水平 | 16dp | **12dp** | coui_btn_padding_horizontal | 中 |
| `SliderDefaults.MinHeight`(轨道厚) | 28dp | **20dp** | coui_seekbar_progress_height | 高 |
| `SliderDefaults.KeyPointRadius` | 3.855dp | **3.0dp** | coui_section_seekbar_tick_mark_radius | 中 |
| ⚠`Component.kt` `heightIn(min=56.dp)` | 56dp | **60dp** | coui_list_item_normal_height | 高 |
| `BasicComponentDefaults.InsideMargin` 水平 | 16dp | ✓ | coui_list_item_left/right_padding | 高 |
| `BasicComponentDefaults.InsideMargin` 垂直 | 16dp | (可选)10dp | support_preference_text_content_padding | 中·flag |
| `TopAppBarDefaults.CollapsedHeight` | 52dp | ✓ | coui_toolbar_height | 高 |
| `TopAppBarDefaults.TitlePadding` | 26dp | **24dp** | coui_appbar_title_margin_start | 中 |
| `SmallTitleDefaults.InsideMargin` | (28,8) | (32,12) | support_preference_category_* | 中·flag |
| `IconButtonDefaults.MinWidth/Height` | 40dp | **48dp** | coui_toolbar_back_view_tiny_width | 中 |
| `FloatingActionButtonDefaults.MinWidth/Height` | 60dp | **56dp** | coui_floating_button_normal_size | 中 |

## 5. 字体梯度(置信偏低,建议独立可选批次)

| TextStyles val | 现值 | 目标 | COUI 来源 | 置信 |
| :-- | :-- | :-- | :-- | :-- |
| `Button` | 17sp | **16sp** | coui_btn_text_size | 中 |
| `Title1` | 32sp | ✓ | coui_appbar_title_expanded_text_size | 高 |
| `Title4` | 18sp | ✓ | coui_appbar_title_collapsed_text_size | 中 |
| `Main` | 17sp | (可选)16sp | support_preference_title_size — Main 用途广,改前评估 | 低·flag |
| `Body2`/summary | 14sp | (可选)12sp | support_preference_summary_size | 低·flag |
| `Subtitle` | 14sp Bold | 评估 Medium(500) | COUI 偏 medium,无干净 token | 低·flag |

## 6. 关键控件(改 §2 色值即自动生效,无需改组件代码)

Switch / Slider / Checkbox / RadioButton / Button 的所有 accent 态都读 `colorScheme.primary / onPrimary / secondary / disabled* / sliderBackground / sliderKeyPoint`。**纯色换肤改 Colors.kt 即传播**。

几何属可选批次(代码字面量,非 Defaults):
- **Switch** `.size(49,28)` 拇指 `20dp` 偏移 `4..25dp` 拖拽 `coerceIn(-21f,0f)` → COUI 36×22,改尺寸须重算偏移/拖拽数学。纯换肤建议**保留 49×28**。
- **Slider** `MinHeight 28→20`(COUI 标志 20dp 胶囊轨)、`KeyPointRadius 3.855→3`;拇指 `barRadius*0.72f`,MinHeight=20 时得 7.2dp(COUI 6–8dp,达标)。
- **Checkbox** `.requiredSize(26.dp)`(COUI 24,保留);COUI 关闭/禁用为半透明描边环,与 miuix 实心填充结构不同 → 低置信近似,需目测。

## 7. 改造批次(仅现有组件,高影响·低风险优先)

- **批次 0 — Colors.kt 纯值替换**(最高 ROI,零代码风险):primary 家族 + surface/card 家族 + secondary + slider* + 语义色 + 文字 alpha + 分隔线 + 遮罩。一改传播到所有组件。先做、单独提交、目测。
- **批次 1 — Button + Card Defaults**:Card 16→17;Button CornerRadius 16→21.5、MinHeight 40→44、InsideMargin 水平 16→12。
- **批次 2 — Slider 几何**:MinHeight 28→20、KeyPointRadius 3.855→3。
- **批次 3 — Squircle Extension**:1.1f→1.25f(全局形状手感,广泛回归)。
- **批次 4 — 次级 Defaults**:TextField 角 16→10、SmallTitle (28,8)→(32,12)、TopAppBar TitlePadding 26→24、IconButton 40→48、FAB 60→56。
- **批次 5 — 代码字面量(中风险)**:Component heightIn 56→60、Dialog 底角 32→33。
- **批次 6 — 可选/高风险几何(默认不做,需确认)**:Switch 49×28→36×22、FloatingToolbar 50→24、TabRow 12→8、字体梯度整体下调。

## 8. 未决项(需运行时目测)

1. 深色 surface=`#000000` 纯黑 AMOLED vs miuix 现 `#242424`,确认是否接受纯黑底。
2. `disabledPrimary` 用 30% 真主色 `#4D0066FF`,不照抄资源 `#4D2660F5`(色相偏)。
3. Checkbox/Radio 关闭&禁用为半透明环,与 miuix 实心填充结构不符,低置信。
4. windowDimming 浅色 0.3→0.2,遮罩变浅。
5. FloatingToolbar 50→24 设计分歧;Switch/Slider 几何需重算数学。
6. 字体角色映射不定,Subtitle 字重 COUI 偏 medium 无干净 token。
