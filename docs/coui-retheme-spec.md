<!-- Copyright 2025, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# ColorOS 16 (COUI) Retheme Spec

> miuix → COUI 换肤的唯一权威规范。来源于对 ColorOS 16(OnePlus PJE110, V16.0.0)系统应用的逆向取证:
> `com.oplus.uxdesign`(设计语言主库)、`com.coloros.alarmclock`、运行时实测。逆向产物见 `D:\AndroidStudioProjects\jadx\work\`。
>
> **策略:只改值,不重命名主题类/色槽。** 组件读 `COUITheme.colorScheme.*` / `textStyles.*`,绝不硬编码 `Color(0x...)`。
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
| `surface` | **Scaffold 页面底**、Surface、TopAppBar、NavigationRail、TabRow 底 | `#F0F1F2`(灰底) | `#000000`(纯黑 AMOLED) |
| `surfaceContainer` | **Card**、FloatingToolbar | `#FFFFFF`(白卡) | `#1AFFFFFF`(10% 白) |
| (popup 专用,非色槽) | ListPopup / Dropdown / CascadingPopup 容器已改走 `popupSurfaceColor()`(couiColorSurfaceTop) | `#FFFFFF` | `#333333`(不透明,coui_color_surface_top values-night) |
| `surfaceVariant` | (卡片同义) | `#FFFFFF` | `#1AFFFFFF` |
| `background` | **Dialog / BottomSheet 面板背景**、NavigationBar 底(COUI tab 栏 9-patch 实测 #FAFAFA/#1F1F1F,比页面底更亮) | `#FFFFFF`(白面板,**勿刷灰**) | `#1E1E1E` |

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
| `CardDefaults.CornerRadius` | 16dp | **12dp** | couiRoundCornerM=coui_round_corner_m=12dp(settings 实际消费值;17dp 为 uxdesign 16.1 未消费 token,见 §9.5) | 高 |
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
| ⚠`Component.kt` `heightIn(min=…)` | 56dp | **48dp**(修订,原定 60) | support_preference_min_height=48(设置页 preference 行;coui_list_item_normal_height=60 属通用列表,设置页实测 48+首尾 2,见 §9.1) | 高·修订 |
| `BasicComponentDefaults.InsideMargin` 水平 | 16dp | ✓ | coui_list_item_left/right_padding | 高 |
| `BasicComponentDefaults.InsideMargin` 垂直 | 16dp | **10dp** | support_preference_text_content_padding_top/bottom=10(§9.1) | 高 |
| `TopAppBarDefaults.CollapsedHeight` | 52dp | ✓ | coui_toolbar_height | 高 |
| `TopAppBarDefaults.TitlePadding` | 26dp | **16dp**(手机 compact 档) | coui_appbar_title_expanded_margin_start_compat(COUICollapsingToolbarLayout 响应式选值; 24=medium/40=expanded) | 高 |
| `SmallTitleDefaults.InsideMargin` | (28,8) | **(32,8)**(修订,曾误定 (32,12)) | category 标题实测:水平 32=16 页边+16 卡内,上下各 8(title_margin_end_new=8;12 是 margintop_small 误用),见 §9.3 | 高·修订 |
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
- **批次 6 — 几何(已做 Switch + FloatingToolbar)**:Switch 49×28→36×22(拇指 18dp,行程/偏移/拖拽阈值全由 `SwitchDefaults.Travel` 推导,数学自洽);FloatingToolbar 50→24(`coui_toolbar_menu_bg_radius`)。**未做**:TabRow 12→8(证据不足/低置信,不臆造)、字体梯度(用户决策"暂不动")。

## 8. 未决项(需运行时目测)

1. 深色 surface=`#000000` 纯黑 AMOLED vs miuix 现 `#242424`,确认是否接受纯黑底。
2. `disabledPrimary` 用 30% 真主色 `#4D0066FF`,不照抄资源 `#4D2660F5`(色相偏)。
3. Checkbox/Radio 关闭&禁用为半透明环,与 miuix 实心填充结构不符,低置信。
4. windowDimming 浅色 0.3→0.2,遮罩变浅。
5. FloatingToolbar 50→24 设计分歧;Switch/Slider 几何需重算数学。
6. 字体角色映射不定,Subtitle 字重 COUI 偏 medium 无干净 token。

## 9. 页面设计系统(卡片 / 分割线 / 节奏)— 设置页 ground truth

> 证据源:`E:\AndroidStudioProjects\jadx\work\com.android.settings\`(V16.0.0 内置 COUI support-preference 库反编译)
> 与真机截图 `_shots\wifi.png`(1264px 宽,density=3.5,页边距 56px/3.5=16dp 整除自证)。
> 核心类:`cardlist/COUICardListHelper` + `COUICardListSelectedItemLayout`(卡片分组与圆角/padding)、
> `preference/COUIPreferenceItemDecoration` + `COUIRecyclerView.COUIDividerItemDecoration` + `COUIPreference`(分割线)、
> `COUIPreferenceCategory`(分组标题)。以下所有 dp 值均三方互证(代码消费点 + dimens + 像素实测)。

### 9.1 卡片分组(cardlist)

分组算法(`COUICardListHelper.getPositionInGroup`):以 **category 内可见项** 为一组——单条=FULL、首条=HEAD、末条=TAIL、其余=MIDDLE。

| 规则 | 值 | COUI 来源(代码消费点已核) | 实测 | miuix 落点 |
| :-- | :-- | :-- | :-- | :-- |
| 卡片水平页边距 | **16dp**(tiny 12dp) | coui_preference_card_margin_horizontal(COUICardListSelectedItemLayout.init) | 56px ✓ | example:`Card(Modifier.padding(horizontal = 16.dp))` |
| 卡片圆角 | 名义 **12dp**(见下注) | couiCardRadius attr → couiRoundCornerM=coui_round_corner_m=12dp(COUICardListSelectedItemLayout 构造 L332;settings styles.xml L11804) | wifi 页弧高 30px≈8.6dp ✓ 吻合 12dp squircle | `CardDefaults.CornerRadius=12dp` ✓ 已对齐(§9.5 已定案) |
| 首/尾行额外 padding | **+2dp** | coui_list_card_head_or_tail_padding(setPadding:HEAD 顶+2 / TAIL 底+2 / FULL 上下各+2 / MIDDLE 无) | 首尾行 50px×3.5=175px=48+2 ✓ | 列表卡建议 `Card(insideMargin = PaddingValues(vertical = 2.dp))` 或首末行自加 |
| 行最小高度 | **48dp** | support_preference_min_height(coui_preference.xml minHeight) | 行高 175px=50dp(48+2 首行) ✓ | `BasicComponent heightIn(min=48.dp)` ✓ 已对齐 |
| 行内容水平 padding | 距卡边 **16dp** | support_preference_title_padding_start/end=32(从全宽 item 计,含 16 页边距) | 文本起点 112px=32dp 距屏 ✓ | `BasicComponentDefaults.InsideMargin` 水平 16dp ✓ |
| 行文本区垂直 padding | **10dp** | support_preference_text_content_padding_top/bottom=10 | — | `BasicComponentDefaults.InsideMargin` 垂直 10dp ✓ |
| title↔summary 行间 | **2dp** | support_preference_margin_between_line(coui_preference.xml summary marginTop) | — | BasicComponent summary `padding(top=2.dp)` ✓ |
| 卡片背景 | #FFFFFF / #1AFFFFFF | couiColorCardBackground = coui_color_card_background/_dark | 白卡 ✓ | `surfaceContainer` ✓ §2 |

> 圆角注:卡片圆角在 OS16 上由 `OplusPathAdapter` 平滑曲线绘制(RoundCornerUtil.getSmoothStyleType()==1,平滑权重系统默认 1.7),名义半径取 couiRoundCornerM。卡片矩形为 `[16dp, 0, width-16dp, height]`——item 视图占满全宽,卡面向内绘制,涟漪/选中态(COUIStateEffectDrawable)被裁剪在卡path 内。

### 9.2 行间分割线

**规则(COUIPreference.drawDivider,已核源码):`positionInGroup ∈ {HEAD, MIDDLE}` 时在该行底部画一条——即卡内相邻两行之间恰好一条,末行之后、单行卡(FULL)一律不画。** 开关 `couiShowDivider` 默认 **true**。

| 属性 | 值 | COUI 来源 | 实测(wifi.png) |
| :-- | :-- | :-- | :-- |
| 厚度 | **0.33dp**,绘制时 `max(1px, 0.33dp)` | coui_list_divider_height(COUIDividerItemDecoration.init) | 1px ✓ |
| 绘制方式 | onDrawOver 叠画在行底边上,**不占布局高度** | COUIDividerItemDecoration.onDrawOver | ✓ |
| 颜色 | **#1F000000 / #33FFFFFF** | couiColorDivider = coui_color_divider/_dark | 白卡上 RGB(223)=12% 黑 ✓ = `dividerLine` §2 |
| start inset | **动态对齐 title 文本起点**(getDividerStartAlignView()=mTitleView) | COUIPreferenceItemDecoration.getDividerInsetStart | 无 icon 行:距卡边 16dp ✓;wifi 自定义 icon 行:距卡边 48dp ✓ |
| — 无 icon 行 | 距卡边 **16dp**(=距屏 32dp) | 即 title padding start | 112px ✓ |
| — 标准 icon 行 | 距卡边 **68dp**(=距屏 84dp) | 16+icon36+gap16(coui_preference_icon_margin_right=16,ex_divider_preference_icon_size=36);静态 token coui_list_card_divider_margin_start=84 即此值(本 apk 代码无直接消费,由动态对齐达成) | — |
| end inset | 距卡边 **16dp**(=距屏 32dp),固定 | coui_preference_divider_default_horizontal_padding=32(getDividerEndInset;EndAlignView=null) = coui_list_card_divider_margin_end=32 | 1151px→56px=16dp ✓ |
| RTL | start/end 镜像交换 | onDrawOver z2 分支 | — |
| 按压态 | 按压行上下两条分割线 alpha 过渡(setPressDividerPos/Alpha) | COUIDividerItemDecoration | 未复刻(可选) |

> ⚠ `coui_list_item_divider_left/right_margin=18dp` 在本 apk **无任何代码消费点**(遗留 token),**勿用 18dp**——正确值是距卡边 16dp。
>
> **miuix 落点**:`HorizontalDivider(Modifier.padding(horizontal = DividerDefaults.CardInset))`(CardInset=16dp,已加,KDoc 含 icon 行规则);example 现有手拼写法已符合。末行后不画由使用侧保证(仅在相邻行之间插入)。

### 9.3 分组标题(category)

| 规则 | 值 | COUI 来源 | 实测 |
| :-- | :-- | :-- | :-- |
| category 容器与上一卡间距(topMargin) | **16dp** 默认 | coui_preference_category_margintop_large(top_margin_type=0 默认;small=12/zero=0 attr 可切) | 参与 48.3dp 合成 ✓ |
| 标题字体 | **12sp sans-serif-medium** | couiTextAppearanceSmallButton(Widget.COUI.List.Category.Title) | 字形高 10.9dp ✓ |
| 标题颜色 | #8A000000 / #8AFFFFFF | couiColorSecondNeutral = coui_color_secondary_neutral/_dark | ✓ = `onBackgroundVariant`(SmallTitle 现用)✓ |
| 标题上下 margin | **各 8dp**(minHeight 16dp) | support_preference_category_layout_title_margin_end_new=8(titleType=0 默认,上下同值) | ✓ |
| 标题 start | 距屏 **32dp** = 距卡边 16dp | support_preference_category_layout_title_margin_start_large=32(margin_start_type=1 默认) | 字形沿 117px≈33.4dp ✓ |
| **卡→卡(带标题)合成** | 16 + 8 + 行盒(~16) + 8 ≈ **48dp** | 上述四项相加 | 169px=48.3dp ✓✓ |
| **卡→卡(无标题 category)** | **16dp** | 仅剩容器 topMargin | — |

**miuix 落点**:`SmallTitleDefaults.InsideMargin = PaddingValues(32.dp, 8.dp)`(已改,水平 32=16 页边+16 卡内对齐);16dp 卡间距由卡片间隔提供(example 惯例 `Card(Modifier.padding(bottom = 16.dp))`),合成 卡→标题文本 24dp / 标题文本→卡 8dp,与 COUI 完全一致。

### 9.4 页面节奏

| 位置 | 值 | COUI 来源 | 实测 |
| :-- | :-- | :-- | :-- |
| toolbar 高度 | 52dp(无附加 padding) | toolbar_min_height=52,top/bottom_padding=0 | 卡顶 364px = 状态栏 40 + toolbar 52 + 12 ✓ |
| 页顶:toolbar 底 → 第一张卡 | **12dp** | coui_list_to_ex_top_padding=12(旧 ListView paddingTop / coui_preference_category_layout_tiny paddingTop / 别名 support_preference_category_padding_top;appbar 页由首项 spacer=appbar 高 + 12 达成) | ✓ |
| 页底:最后一张卡 → 内容末 | **32dp** | coui_list_to_ex_bottom_padding=32(旧 ListView paddingBottom;COUIBottomPreference=invisible 32dp 脚垫,别名 support_preference_foot_preference_padding_bottom) | — |

**miuix 落点**:页面滚动容器 `contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)`(example LazyColumn / Scaffold 内容区)。

### 9.5 分歧与备注

1. **卡片圆角 12 vs 17(已定案:12dp)**:settings 内置 COUI 库的真实消费链为 `COUICardListSelectedItemLayout` 构造(L332)`couiCardRadius` attr 缺省取 `couiRoundCornerM`,settings 主题(styles.xml L11804)映射到 `coui_round_corner_m=12dp`;`coui_card_list_os_16_1_radius_17_dp` 仅存在于 uxdesign 16.1 新库、settings apk 内不存在且无消费点。真机 wifi 页像素复核(density 3.5):卡角弧高 ≈30px≈8.6dp,与 12dp squircle 弧高(≈12×0.71≈8.5dp)吻合,17dp 会得到 ≈15dp 弧高、与实测不符。`CardDefaults.CornerRadius` 已 17→12dp 回退。
2. 按压行分割线 alpha 过渡、卡片选中/涟漪态(COUIStateEffectDrawable)未复刻,不影响静态视觉。
3. 首/尾行 +2dp 属**卡内**空间(计入行高 48→50),不是卡外间距;Compose 侧最简落点是列表卡 `insideMargin = PaddingValues(vertical = 2.dp)`,example 待接入。
