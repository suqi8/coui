<!-- Copyright 2025, compose-miuix-ui contributors -->
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

Card 17dp、Dialog 底角 33dp、BottomSheet 28dp(均 OS 16.1 权威 dimen);Slider 轨道厚 20dp、thumb 白色、drag+tap-to-jump;RangeSlider 刻点 3dp;List 行高 60dp、横向 16dp;Switch ThumbSize 18dp、Travel 14dp。

## 未采纳(有据)

- **Button MinWidth 58→132dp**:报告标 medium,COUI 多数用 ButtonNew match_parent,58dp 是点击下限,全局改 132 不合理。
- **TextStyles Button 17→16sp + Medium**:用户决策"字体暂不动"。官方确为 16sp/Medium,留待字体专项。
- **Squircle 半径相关 Extension / 每角 3 段 cubic(G2 超椭圆)**:高风险核心绘制重写,1.2819 单值已是卡片域最佳近似,精确化作为独立后续任务。
- **Slider 拖拽背景轨道 20→28dp 放大、触控区 36dp**:进阶保真(动效/命中区),非纯值,留待后续。
- **TabRow 12dp**:低置信、来源不足,不改。

## 重要发现

- COUI 控件真实几何来自**控件自身 styleable/attrs**(如 COUISwitchStyle 的 bar_width=38),不是 `coui_switch_*` 那组 dimen(36/22 是未被控件读取的遗留值)。核查必须读控件类,不能只查 dimens。
- alarmclock 内置的是 **pre-16.1 较旧 COUI**,Card/Panel 解析为旧半径(12/20dp),**不采用**;OS 16.1 权威值取 `*_os_16_1_*` dimen。
