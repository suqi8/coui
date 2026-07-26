<!-- Copyright 2025, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# COUI 源码核查与修正记录

对照真实 COUI 控件反编译源(`com.coloros.alarmclock` 的 `com.coui.appcompat.*`、`com.android.settings` 的 COUISeekBar)逐项核查 retheme 结果,并修正不一致项。

## 已修正(本轮)

| 项 | 旧 | 新 | COUI 源 |
| :-- | :-- | :-- | :-- |
| Switch TrackWidth | 36dp | **38dp** | COUISwitchStyle switchMinWidth=bar_width=38dp |
| Switch TrackHeight | 22dp | **24dp** | barHeight=bar_height=24dp(24-18=2×circle_padding) |
| Switch ThumbMargin | 2dp | **3dp** | circlePadding=circle_padding=3dp |
| Switch 拖拽切换 | draggable 跟手 | **点按切换**(thumb 不跟手) | COUISwitch.onTouchEvent 无 ACTION_MOVE |
| Switch 按压放大 | thumb 整体 1.127× | **去掉**;改轨道 press/hover 黑叠加(12%/7.8%)+ toggle 时横向 squash scaleX→1.3 | press 为 coui_color_press/hover 叠加 |
| Slider thumb 静止半径 | ×0.72(7.2dp) | **×0.6(6dp)** | coui_seekbar_thumb_radius=6dp |
| Slider thumb 按压半径 | ×1.127(8.11dp) | **×1.3333(8dp)** | coui_seekbar_thumb_max_radius=8dp |
| Slider 刻点半径(Vert/Slider) | barH/7.5(2.667dp) | **KeyPointRadius 3dp** | coui_section_seekbar_tick_mark_radius=3dp |
| Squircle Extension | 1.25 | **1.2819** | COUIShapePath 128.19/100(卡片域) |
| Button CornerRadius | 21.5dp | **22dp**(=height/2,squircle 路径无 0.5 偏移) | COUIButton.j() height/2 |
| Button 竖向 padding | 13dp | **0**(高度仅靠 minHeight 居中) | Widget.COUI.Button.Large 无竖向 padding |

## 已确认正确(无需改)

Dialog 底角 33dp、BottomSheet 28dp(OS 16.1 权威 dimen);Card 圆角已定案 **12dp**(couiRoundCornerM=coui_round_corner_m,COUICardListSelectedItemLayout L332 实际消费;17dp 为 uxdesign 16.1 未消费 token,真机 wifi 页弧高 30px≈8.6dp 复核吻合 12dp squircle,详见 coui-retheme-spec §9.5);Slider 轨道厚 20dp、thumb 白色、drag+tap-to-jump;RangeSlider 刻点 3dp;List 行高 60dp、横向 16dp;Switch ThumbSize 18dp、Travel 14dp。

## 未采纳(有据)

- **Button MinWidth 58→132dp**:报告标 medium,COUI 多数用 ButtonNew match_parent,58dp 是点击下限,全局改 132 不合理。
- **TextStyles Button 17→16sp + Medium**:用户决策"字体暂不动"。官方确为 16sp/Medium,留待字体专项。
- **Squircle 半径相关 Extension / 每角 3 段 cubic(G2 超椭圆)**:高风险核心绘制重写,1.2819 单值已是卡片域最佳近似,精确化作为独立后续任务。
- **Slider 拖拽背景轨道 20→28dp 放大、触控区 36dp**:进阶保真(动效/命中区),非纯值,留待后续。
- **TabRow 12dp**:低置信、来源不足,不改。

## 重要发现

- COUI 控件真实几何来自**控件自身 styleable/attrs**(如 COUISwitchStyle 的 bar_width=38),不是 `coui_switch_*` 那组 dimen(36/22 是未被控件读取的遗留值)。核查必须读控件类,不能只查 dimens。
- alarmclock 内置的是 **pre-16.1 较旧 COUI**,Card/Panel 解析为旧半径(12/20dp),**不采用**;OS 16.1 权威值取 `*_os_16_1_*` dimen。

## 深度源码审计轮(settings 包 com.coui.appcompat.* 逐类核对,本轮权威)

死 token 案例追加(与"重要发现"同理,均以真实 draw/measure 代码推翻 dimens):
`coui_list_item_normal_height=60`(preference 行真实 minHeight=48)、`coui_navigation_item_height=54`(实测 56)、
`coui_floating_button_close/open_elevation=5/8`(FAB 真实走标准光照回退 12dp)、`coui_snack_bar_radius_single_line`
(真实走 couiRoundCornerXXL attr)、`coui_popup_list_window_margin_top=19/margin_bottom=30`(零消费,真实边界 16/32/16)、
`coui_search_view_corner=18`(旧版 COUISearchViewAnimate;新版 COUISearchBar 恒 height/2 胶囊)、
`coui_textinput_line_padding=11`(死代码;rect 式真值 paddingLeft=16/上下 12,出自 Widget.COUI.EditText.HintAnim.Rectangle)。

行为级修正(值以反编译源码行号为证,详见各组件 KDoc):
- Switch: 关闭态内圆为古老遗迹(两版 onDraw 均不画),thumb 恒白单圆;补 setShadowLayer(8px,0,4px,10% 黑)阴影(3.5x density 换算)。
- Button: brightness=0.8 死代码;真实按压 = couiColorPress 遮罩(亮 12% 黑/暗 20% 白)+ 面积插值 scale(0.92..0.98)+
  临界阻尼 spring 438.65(response 0.3/bounce 0)+ 70% 进度快速点按保护;默认 indication=null(COUIButton 禁 ripple)。
- Slider: 拖拽时背景轨道 20→28dp(×1.4, 183ms COUIEase)、cap 内缩 14dp、thumb spring 987 临界阻尼、thumb 阴影 4dp/2dp offset。
- Checkbox/Radio: 24dp(drawable 内在尺寸)、描边 1.3dp;Radio 选中 = 粗环(r10 盘挖 r6 白心),无中心小点。
- NumberPicker: 3 行可见、无逐项 alpha 渐隐、字号/颜色过渡半行内完成(2x 系数)、Medium 字重。
- NavigationBar: 重做为 tab 型(时钟 app layout_main_bottom_hide_navigation_view.xml 实证 navigationType="tab")。无按压蒙层/ripple(COUINavigationView.inflateMenu 仅 tool 型 setShowPressShadow;COUI 样式无 itemBackground/itemRippleColor);无选中动效(COUINavigationView_NoAnimation: motionDurationLong1=durationLong=0);图标 = 选中态 selector drawable,enter/exitFadeDuration=180ms 交叉淡变,pressed 亦显示选中形态(tab_alarm_selector_color);文字 = coui_navigation_tab_color 瞬时切换:选中 couiColorPrimaryNeutral→onSurfaceContainer,未选中 couiColorSecondNeutral→onSurfaceSecondary;布局 = fl_root 56dp + half_gap 2dp,icon 顶 9dp,标签底锚 7dp(topToBottom: H-9+2),edge padding 12dp,等宽分配;栏底色 coui_tab_navigation_view_bg 9-patch 实测 #FAFAFA/#1F1F1F→colorScheme.background,顶部 divider 0.33dp couiColorDivider。
- TopAppBar: TitlePadding=16(compact 档,COUICollapsingToolbarLayout 响应式);LargeTitleTopPadding=54;折叠时底部 padding 12→0 插值。
- ProgressIndicator: 不定态 = 墙钟 1000ms/圈 + 60..120 度脉动弧(COUILoadingView L197 公式);圆形确定态默认色 couiColorHintNeutral;
  轨道色 = dividerLine;SRC 合成防叠色;水平条方形前沿。
- Snackbar: 单行 24dp/多行 16dp 圆角按行数切换、action=primary 色 14sp Medium、进出场 scale 0.8+alpha 弹簧(322/438/631)、真实投影。
- SearchBar: 背景恒胶囊、输入 16sp regular、hint/图标 couiColorLabelSecondary、清除按钮 36dp 触摸目标+18dp 圆+恒白 X、
  展开动画 450/400/350ms MoveEase + 30dp 位移。
- BottomSheet 拖拽条: 36x4dp 恒定(按压不形变),press 反馈 = 70x20dp 圆角 16 背景 alpha 0-1(200ms COUIEase)。
- Dialog: MaxWidth=392、外边距(16,24)、message 14sp。
- Dropdown/ListPopup: popup 行 min 48(首末 50/单行 52,DefaultAdapter fold 规则)、图标 24、标题 popup=regular/dialog=medium、
  弹窗弹簧 = COUISpringForce(0.35/0.2 进,0.3/0.25 出)换算值;级联副菜单垂直重叠 14dp、高度上限 available-12dp。
  容器背景 = couiPopupWindowBackground → coui_popup_window_background → ?couiColorSurfaceTop(#FFFFFF / 暗 #333333,
  COUIPopupListWindow.createContentView L225-233);阴影 = RoundFrameLayout.setClipMode(OUTLINE_CLIP) → ShadowUtils LV4
  (OPlus)或 elevation 30dp + spot #80000000(RoundFrameLayout L206-215);行文字 selector = 默认 couiColorLabelPrimary /
  选中 couiColorLabelTheme / 禁用 couiColorLabelTertiary,勾选图标 selector = 选中 couiColorLabelTheme / 默认
  couiColorLabelTertiary(coui_popup_list_window_item_tint_selector 等);description 恒 couiColorLabelSecondary
  (DefaultAdapter.setDescription L407,无选中/禁用变体);行背景 = COUIMaskEffectDrawable(透明,无选中底色);
  分组分隔 = 4dp #14000000(无 night 覆盖,明暗同值),默认分隔 = 0.33dp couiColorDivider 水平 inset 16(带图标 52);
  进场 pivotX = anchor.centerX 夹取到菜单 [left,right](PopupMenuDomain.getMainMenuEnterPivotX L66),
  pivotY = 菜单在下取 top / 在上取 bottom;子菜单展开头行 chevron = couiColorLabelTheme(groupState==2 走 STATE_SELECTED)。
  注:settings 16.0 反编译 rfRadius=?couiRoundCornerM=12dp,与既定 16dp(coui_popup_list_window_os_16_1_radius_16_dp,
  OS 16.1)不一致,维持 16dp 决议不回退。
- FAB: 按压 scale 0.92 spring 438.65;ShadowElevation=12dp(support_shadow_size_level_three)。
- Stepper: Spacing=12、IndicatorMinWidth=44、字形 15.2dp/1.6dp 线宽(矢量实测)、18sp Medium。
- BasicComponent: minHeight=48、垂直 padding=10、title-summary 间距 2dp。
- TextField: InsideMargin=(16,12)(HintAnim.Rectangle style)。SmallTitle 垂直=10dp。PullToRefresh: 主题色回退+1000ms 旋转。
- ScrollBar: 按压外缘 inset 5.5→4dp 对称扩展。

## TabRow 专项轮(settings 包 com.coui.appcompat.segmentbutton.COUISegmentButtonLayout,本轮权威)

此前三轮"TabRow 无同形态控件、不改"的结论被推翻:COUI 分段胶囊控件实为 **COUISegmentButtonLayout**
(`com.support.segmentbutton` 模块;settings 内 account_dashboard_preference_main_fragment.xml 与
special_access_chip_group.xml 两处实装,均 40dp + `SegmentButton` style)。据此把 `TabRowWithContour`
对齐为 SegmentButton,普通 `TabRow`(扁平等分形态)确认 **COUI 无对应形态**(另一形态 COUITabLayout/
COUISlidingTabStrip 为文字+下划线指示条),仅共享配色与动效参数、几何保持现状。

- 几何: 容器高 40dp(`SegmentButton` style layout_height;Tiny 变体 32dp/padding 2dp 未采用)、
  容器-滑块 inset=android:padding=4dp、圆角恒为高度一半胶囊(getDrawableRadius=h/2 → 滑块 16dp/容器 20dp)、
  segment 最小宽 coui_segment_min_width=52dp、文字横向 padding coui_segment_btn_padding_horizontal=12dp、
  相邻 segment 无间距(LinearLayout)。
- 颜色: 容器 coui_color_segment_button_background #0F000000/#1FFFFFFF;滑块
  coui_color_segment_button_indicator #FFFFFF/#1AFFFFFF;滑块阴影 coui_segment_button_shadow_color
  #0D000000/#1A000000(radius 1dp、dy 2dp,shadow pass 用 Region.Op.DIFFERENCE 只画滑块外侧);
  选中/未选中文字**同为** couiColorLabelPrimary(#E6000000/#E6FFFFFF),区分靠字重+滑块
  (源码 loadAttr 两者默认相同,且 selectSegmentAt 里颜色动画有同色跳过守卫)。
- 文字: 选中 couiTextButtonM(14sp sans-serif-medium)/未选中 couiTextBodyM(14sp regular),
  切换瞬时换字重、颜色 300ms COUIMoveEaseInterpolator(=PathInterpolator(0.3,0,0.1,1))渐变。
- 动效: 滑块位置/宽度 = COUISpringForce(response 0.3, bounce 0)→ 临界阻尼 spring 438.65
  (与 Button 轮同源);segment 按压 = COUIPressFeedbackHelper 面积插值 scale 0.92..0.98。
- 未移植(结构性): 拖拽跟手换挡(mSwitchDistance=5dp 滞回)、rubber band(overStiffness 0.12/
  coui_segment_over_distance=4dp)、拖拽中滑块按压缩放、拖拽换挡震动(302)、响应式栅格宽
  (4/6/8 格)、文字超长按自然宽排序的重分配算法(Compose 侧保持等宽+LazyRow 滚动适配)。

## 深度轮未采纳/留待专项(有据)

- Checkbox 外形: 真实 COUI 为 18/24 圆角方块(r=4)+对勾,与现圆形结构不同,属组件级重构。
- Radio 选中中心恒白(暗色也白): 需 RadioButtonColors 增前景槽,API 变更。
- BottomSheet maxWidth=640 与顶角 28dp: COUI 真实为响应式栅格 + couiRoundCornerXL=20dp/smooth,大改留专项。
- COUIIndication 按压 10% 亮暗同值 vs couiColorPress 12%/20%: 全局 token 级偏差。
- Slider fling/端点形变/glitter、NumberPicker spline fling+pick 音效、Snackbar 纵排 action/触摸暂停计时: 结构性行为,留专项。
- 折叠标题 18sp、popup summary 12sp、Button/Main 16sp 等字体项: 用户决策"字体暂不动",留字体专项。
