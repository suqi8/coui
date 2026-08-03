# COUI 16 缺失组件清单（对照盘点）

> 盘点日期：2026-07-26。对照基线：`coui-ui/src/commonMain/.../basic/`（32 个文件）+ `overlay/`、`window/`、`layout/` + `coui-preference/`（19 个文件）。
> COUI 全集来源（逆向工作区 `E:\AndroidStudioProjects\jadx\work\`，下文用缩写）：
>
> | 缩写 | 路径 | 说明 |
> | :--- | :--- | :--- |
> | `S` | `E:\AndroidStudioProjects\jadx\work\com.android.settings` | 主证据源，`sources\com\coui\appcompat\` 共 76 个子包 / 323 个 COUI 类；`apktool\res\` 已解出 |
> | `A` | `E:\AndroidStudioProjects\jadx\work\com.coloros.alarmclock` | 补充 calendar / banner / navigationrail；`apktool\res\` 已解出 |
> | `T` | `E:\AndroidStudioProjects\jadx\work\com.heytap.themestore` | 补充 emptyview / sidenavigation / sidepane / slideview / bottomfloatingtoolbar / COUIBanner；**无 apktool**（需要 res 证据时先解包） |
> | `G` | `E:\AndroidStudioProjects\jadx\work\com.oplus.games` | 补充 banner / completeview / aboutpage；`apktool\res\` 已解出 |
>
> 类路径统一相对 `<缩写>\sources\com\coui\appcompat\`（themestore/games/alarmclock 为 `<缩写>\jadx\sources\com\coui\appcompat\`）；layout 相对 `<缩写>\apktool\res\layout\`。
> 本文件只做盘点，不承诺 API 设计；实现时按 `AGENT.md` §6 逆向校准工作流补齐 token。

---

## 一、缺失组件清单（按优先级排序）

优先级定义：**高** = 设置页 / 常规 app 高频出现；**中** = 特定场景常见；**低** = 小众或 Compose 已有廉价替代。

### 高优先级

| COUI 类名 | 功能 | 优先级 | ground truth 入口 | 建议落点 |
| :--- | :--- | :--- | :--- | :--- |
| `COUIEditText`（line / 错误态 / 计数 / 密码 / 快删多形态） | 输入框全形态，详见 [第二节](#二textfield-输入框多形态差距专项) | 高 | `S`: `edittext\COUIEditText.java`、`edittext\COUIErrorEditTextHelper.java`（错误态+抖动）、`edittext\COUIEditFastDeleteWatcher.java`（快删）、`edittext\COUICheckBoxPwd.java`（密码钮）；attr `couiBackgroundMode`（none/line/rectangle/noLine，`apktool\res\values\attrs.xml` L732）；drawable `coui_edittext_default_background.xml`、`coui_edittext_warning_background.xml`、`coui_edittext_password_icon.xml` | `basic/TextField.kt` 扩展参数（backgroundMode / isError / counter / trailing 预制件） |
| `COUIInputView`（+`COUICardSingleInputView` / `COUICardMultiInputView`） | 「标题 + 输入框 + 错误提示 + 计数 + 密码钮」组合输入卡，COUI 表单标准形态 | 高 | `S`: `edittext\COUIInputView.java`、`edittext\COUICardSingleInputView.java`、`edittext\COUICardMultiInputView.java`；layout `coui_input_view.xml`、`coui_single_input_card_view.xml`、`coui_multi_input_card_view.xml` | `basic/`（新文件 `InputView.kt`，内部复用 TextField） |
| `COUILoadingButton` | 按钮加载态（文字→三点加载动画，禁点） | 高 | `S`: `button\COUILoadingButton.java`（继承 COUIButton，内绘 dot 动画） | `basic/Button.kt` 增加 loading 形态或新 `LoadingButton.kt` |
| `COUIHintRedDot` | 红点 / 数字角标（Tab、导航、设置项右侧），带出现/消失弹性动画 | 高 | `S`: `reddot\COUIHintRedDot.java`、`reddot\COUIHintRedDotHelper.java`、`reddot\COUIRedDotDrawable.java`、`reddot\COUIRedDotFrameLayout.java`；attrs `couiHintRedDot*`（`attrs.xml`） | `basic/`（新 `Badge.kt`，供 NavigationBar/TabRow 复用） |
| `COUIDatePicker` / `COUITimePicker` / `COUITimeLimitPicker` / `COUILunarDatePicker` | 日期选择（年月日）、带日期的时间选择、时分选择、农历选择；仓库目前只有 NumberPicker 无任何日期时间组合件 | 高 | `S`: `picker\COUIDatePicker.java`、`picker\COUITimePicker.java`、`picker\COUITimeLimitPicker.java`、`picker\COUILunarDatePicker.java`；layout `coui_date_picker.xml`、`coui_time_limit_picker.xml`、`coui_lunar_date_picker.xml` | `basic/`（`DatePicker.kt` / `TimePicker.kt`，组合已有 NumberPicker） |
| `COUIDefaultTopTips`（+`COUIMarqueeTopTips` / `COUICustomTopTips`） | 页面顶部提示条卡片：图标 + 文案（可跑马灯）+ 关闭/操作按钮，设置页顶部提醒高频 | 高 | `S`: `tips\def\COUIDefaultTopTips.java`、`tips\def\COUIDefaultTopTipsView.java`、`tips\marquee\COUIMarqueeTopTips.java`；layout `coui_default_toptips.xml` | `basic/`（新 `TopTips.kt`） |
| `COUIRotatingDialogBuilder`（+水平进度对话框） | 加载中对话框（转圈 + 文案）与带横向进度条对话框 | 高 | `S`: `dialog\COUIRotatingDialogBuilder.java`；layout `coui_progress_dialog_rotating.xml`、`coui_progress_dialog_horizontal.xml`、`coui_cancelable_progress_dialog_*.xml` | `overlay/` + `window/`（Dialog 变体，复用 ProgressIndicator） |
| `COUISecurityAlertDialogBuilder` | 安全提示对话框：声明文案 + 「不再提醒」勾选 + 链接高亮 | 高 | `S`: `dialog\COUISecurityAlertDialogBuilder.java`；layout `coui_security_alert_dialog_statement_or_checkbox.xml` | `overlay/` + `window/`（Dialog 变体） |
| `COUISwitchLoadingPreference`（+`COUIStatusSwitchingPreference`） | 开关加载态设置项：点击后 Switch 位置转圈（WLAN/蓝牙标准交互） | 高 | `S`: `preference\COUISwitchLoadingPreference.java`、`preference\COUIStatusSwitchingPreference.java`；layout `coui_preference_widget_switchload.xml`、`coui_preference_widget_status_switching.xml` | `coui-preference/preference/`（SwitchPreference 扩展 loading 态） |
| `COUIMarkPreference`（+`COUIMarkWithDividerPreference`） | 单选「对勾」列表项（选中项右/左侧打勾，无圆圈），COUI 单选列表标准形态 | 高 | `S`: `preference\COUIMarkPreference.java`、`preference\COUIMarkWithDividerPreference.java`；layout `coui_preference_widget_mark.xml`、`coui_mark_with_divider_preference.xml` | `coui-preference/preference/`（新 `MarkPreference.kt`） |
| `COUIInputPreference` / `COUIEditTextPreference` | 设置项内嵌输入框 / 点击弹输入对话框 | 高 | `S`: `preference\COUIInputPreference.java`、`preference\COUIEditTextPreference.java`、`preference\COUIEditTextPreferenceDialogFragment.java`；layout `coui_input_preference.xml`、`coui_preference_dialog_edittext.xml` | `coui-preference/preference/` |
| `COUIListPreference` / `COUIMultiSelectListPreference` / `COUIActivityDialogPreference` | 点击弹「底部面板/对话框列表」的单选、多选设置项（区别于已有的下拉 DropdownPreference） | 高 | `S`: `preference\COUIListPreference.java`、`preference\COUIListPreferenceDialogFragment.java`、`preference\COUIMultiSelectListPreference.java`；panel 列表 `panel\COUIListBottomSheetDialog.java`、layout `coui_list_bottom_sheet_dialog_layout.xml` | `coui-preference/preference/`（复用 overlay BottomSheet） |
| `COUIChip` / `COUIChipGroup` | 胶囊筛选标签（可选中、可关闭、组内单/多选），搜索与筛选页高频 | 高 | `S`: `chip\COUIChip.java`、`chip\COUIChipDrawable.java`、`chip\COUIChipGroup.java`、`chip\COUICheckableGroup.java`；attrs `couiChip*`（`attrs.xml`） | `basic/`（新 `Chip.kt`） |
| `COUIFullPageStatement`（+`COUIUserStatementDialog` / `COUIIndividualStatementDialog`） | 全页隐私声明（标题 + 滚动正文 + 底部同意/退出双钮）与底部声明面板，App 首启合规必备 | 高 | `S`: `statement\COUIFullPageStatement.java`、`statement\COUIUserStatementDialog.java`；layout `coui_full_page_statement.xml`、`coui_component_full_page_statement_with_protocol.xml`、`coui_component_statement_content_item.xml` | `basic/`（页面级）+ `overlay/`（面板形态） |
| `COUIRecommendedPreference` | 页面底部「相关设置 / 其他人还搜索」推荐卡（圆角卡内多行链接） | 高 | `S`: `preference\COUIRecommendedPreference.java`、`preference\COUIRecommendedDrawable.java` | `coui-preference/preference/` |
| `COUIButtonPreference` / `COUICardButtonPreference` | 设置列表中的整行按钮项（居中文字按钮 / 卡片按钮） | 高 | `S`: `preference\COUIButtonPreference.java`、`card\COUICardButtonPreference.java`；layout `coui_preference_widget_button.xml`、`coui_component_card_button_preference.xml` | `coui-preference/preference/` |

### 中优先级

| COUI 类名 | 功能 | 优先级 | ground truth 入口 | 建议落点 |
| :--- | :--- | :--- | :--- | :--- |
| `COUICodeInputView` | 验证码输入框（N 个方格、自动跳格、可密文） | 中 | `S`: `edittext\COUICodeInputView.java`、`edittext\COUICodeInputHelper.java`；layout `coui_phone_code_layout.xml` | `basic/`（`CodeInput.kt`） |
| `COUIToolTips` | 带箭头气泡提示（纯文字 / 带图标 / 带图片三种样式，可指向任意锚点） | 中 | `S`: `tooltips\COUIToolTips.java`、`tooltips\COUIDefaultBubbleStyleImpl.java`、`COUIIconBubbleStyleImpl.java`、`COUIImageBubbleStyleImpl.java`；layout `coui_tool_tips_layout.xml`、`coui_tool_tips_icon_style_layout.xml`、`coui_tool_tips_image_*_style_layout.xml` | `basic/`（依托 Popup 定位） |
| `COUIPageIndicator`（/`COUIPageIndicator2`） | 轮播/引导页圆点指示器（蠕虫动画、可点击） | 中 | `S`: `indicator\COUIPageIndicator.java`、`indicator\COUIPageIndicator2.java`；layout `coui_page_indicator_dot_layout.xml`；`A`: `banner\COUIPageIndicatorKit.java` | `basic/`（`PageIndicator.kt`） |
| `COUISearchViewAnimate` / `COUISearchBar` 磁吸形态 | 搜索栏进入/退出动画形态：点击后上浮进 TitleBar、键盘联动（我们 SearchBar 只有静态形态） | 中 | `S`: `searchview\COUISearchViewAnimate.java`、`searchview\COUISearchBar.java`、`searchview\COUIHintAnimationLayout.java`（hint 轮播）；layout `coui_search_view_animate_layout.xml` | `basic/SearchBar.kt` 增强 |
| `COUINotificationSnackBar` | 通知样式 Snackbar（图标 + 标题 + 副文本 + 操作，浮窗观感） | 中 | `S`: `snackbar\COUINotificationSnackBar.java`；layout `coui_notification_snack_bar_item.xml`、`coui_notification_snack_bar_show_layout.xml` | `basic/Snackbar.kt` 变体 |
| `COUIFloatingButton` | 可展开 FAB（speed-dial：主钮展开若干带标签子钮，我们 FAB 仅单钮） | 中 | `S`: `floatingactionbutton\COUIFloatingButton.java`、`COUIFloatingButtonItem.java`、`COUIFloatingButtonLabel.java`；layout `coui_floating_button_item_label.xml` | `basic/FloatingActionButton.kt` 扩展 |
| `COUIInstallLoadProgress`（+`COUILoadInstallProgressPreference`） | 按钮式下载/安装进度（按钮即进度条，文字随进度变色） | 中 | `S`: `progressbar\COUIInstallLoadProgress.java`、`progressbar\COUILoadProgress.java`；layout `coui_preference_load_progress.xml` | `basic/`（`InstallProgressButton.kt`） |
| `COUITouchSearchView` | 侧边字母索引条（触摸弹出首字母气泡，联系人/应用列表） | 中 | `S`: `touchsearchview\COUITouchSearchView.java`；layout `coui_touchsearch_poppup_firstkey.xml`、`coui_touchsearch_popup_content_item.xml` | `basic/` |
| `COUISearchHistoryView`（+`COUIFlowLayout`） | 搜索历史流式标签面板（自动换行、展开/收起、删除） | 中 | `S`: `searchhistory\COUISearchHistoryView.java`、`searchhistory\COUIFlowLayout.java`；layout `coui_component_item_search_history.xml` | `basic/`（FlowRow 组合） |
| `COUIExpandableRecyclerView` | 可展开分组列表（组展开/收起动画，设置「更多」分组） | 中 | `S`: `expandable\COUIExpandableRecyclerView.java`、`expandable\COUIExpandableRecyclerAdapter.java` | `coui-preference/`（展开分组容器） |
| `COUICalendarDayPickerView`（日历） | 月历视图选择器（月翻页 + 年列表），任务书所称 `COUICalendarPicker` 实为此家族 | 中 | `A`: `calendar\COUICalendarDayPickerView.java`、`COUICalendarDayViewPager.java`、`COUICalendarYearView.java`、`COUIDateMonthView.java`；layout `A\apktool\res\layout\coui_calendar_picker_material.xml`、`coui_calendar_picker_month_item_material.xml` | `basic/`（`CalendarPicker.kt`） |
| `COUIBanner` | 轮播 Banner（自动轮播 + 页码指示点，圆角卡片） | 中 | `T`: `banner\COUIBanner.java`、`banner\COUIBannerRecyclerView.java`（`G` 同有）；layout `A/G\apktool\res\layout\coui_banner_content_layout.xml` | `basic/`（依托 Pager + PageIndicator） |
| `COUIEmptyStateView` | 空状态页（插画/动画 + 主副文案 + 操作按钮） | 中 | `T`: `emptyview\COUIEmptyStateView.java`、`emptyview\EmptyStateAnimView.java`（themestore 未解 apktool，需要 res 时先解包） | `basic/`（`EmptyState.kt`） |
| `COUILockPatternView` | 九宫格图案解锁（错误红显、路径动画） | 中 | `S`: `lockview\COUILockPatternView.java`、`lockview\COUILockPatternUtils.java` | `basic/` |
| `COUINumericKeyboard`（+`COUISimpleLock`） | 安全数字键盘（圆形按键、按压动效）与 PIN 圆点显示 | 中 | `S`: `lockview\COUINumericKeyboard.java`、`lockview\COUISimpleLock.java` | `basic/` |
| `COUICardInstructionPreference` / `COUICardEntrancePreference` | 页首图文说明卡（插图轮播 + 描述）/ 宫格入口卡 | 中 | `S`: `card\COUICardInstructionPreference.java`、`card\COUICardEntrancePreference.java`、`card\BaseCardInstructionAdapter.java` | `coui-preference/preference/` |
| `COUISlideView` | 列表项侧滑菜单/侧滑删除（含删除动画） | 中 | `T`: `slideview\COUISlideView.java`、`COUISlideMenuItem.java`、`COUISlideDeleteAnimation.java` | `basic/`（`SwipeItem.kt`） |
| `COUITagView` | 小型标签角标（如「新」「BETA」，文字底色块） | 中 | `S`: `tagview\COUITagView.java`、`tagview\COUITagBackgroundView.java` | `basic/`（可并入 Badge） |
| `COUIButtonBarLayout` 推荐按钮档 | 对话框按钮栏的「推荐按钮」形态：某一按钮提升为高亮填充主按钮（44dp 高、自带内外边距、隐藏所有分割线），且其存在会强制整栏纵排。**自动纵排本体已实现**（见第六节已实施），此处仅缺推荐按钮这一档；现有 `DialogButtonBar` 始终按 `mRecommendButtonId == NO_RECOMMEND_ID` 行事 | 中 | `S`: `buttonBar\COUIButtonBarLayout.java`（`applyRecommendLayout` L81、`applyRecommendStyle` L109、`setRecommendButtonId` L568）；dimens `coui_bottom_alert_dialog_button_recommend_height` 44dp、`coui_alert_dialog_button_horizontal_padding_with_recommend`、`coui_bottom_alert_dialog_vertical_button_margin_nonrecommend` 4dp | `layout/DialogButtonBar.kt` 增强 |
| `COUIStepperPreference` | Stepper 设置项包装（Stepper 本体已有） | 中 | `S`: `preference\COUIStepperPreference.java`；layout `coui_preference_widget_stepper_view.xml` | `coui-preference/preference/` |
| `COUIMarqueeTextView` | 跑马灯文本（自动滚动、渐隐边缘） | 中 | `S`: `tips\COUIMarqueeTextView.java` | `basic/Text.kt` 变体或 modifier |
| `COUICompleteStateView` | 操作完成状态页（对勾动画 + 文案） | 中 | `G`: `completeview\COUICompleteStateView.java` | `basic/`（可与 EmptyState 同族） |
| `COUIAppInfoPreference` | 「关于页」应用信息头（图标 + 名称 + 版本） | 中 | `G`: `aboutpage\COUIAppInfoPreference.java` | `coui-preference/preference/` |

### 低优先级

| COUI 类名 | 功能 | 优先级 | ground truth 入口 | 建议落点 |
| :--- | :--- | :--- | :--- | :--- |
| `COUILockScreenPwdInputLayout` / `COUILockScreenPwdInputView` | 锁屏密码输入组合（输入框 + 确认圆钮，内阴影/光效） | 低 | `S`: `input\COUILockScreenPwdInputLayout.java`、`input\COUILockScreenPwdInputView.java`；layout `coui_input_lock_screen_pwd_layout.xml`、`coui_input_lock_screen_pwd_view.xml`；drawable `coui_input_lock_screen_pwd_next_bg.xml` | `basic/`（如做锁屏场景再上） |
| `COUIRotateView` | 展开/收起旋转箭头（180° 旋转动画的 ImageView） | 低 | `S`: `rotateview\COUIRotateView.java` | Compose `rotate` 动画即可，随展开列表一并做 |
| `COUIRoundImageView` | 圆角/圆形图片（带描边） | 低 | `S`: `imageview\COUIRoundImageView.java` | Compose `clip` 即可，不建议单独建组件 |
| `COUIContextMenu` / `COUIIsolatedPopupListWindow` | 指针位置上下文菜单（长按/右键处弹出，区别于锚点下拉） | 低 | `S`: `poplist\COUIContextMenu.java`、`poplist\COUIIsolatedPopupListWindow.java` | `coui-preference/popup/` 增强 |
| `COUISidePaneLayout` / `COUISideNavigationBar` | 平板/折叠屏侧栏分栏布局、侧导航 | 低 | `T`: `sidepane\COUISidePaneLayout.java`、`sidenavigation\COUISideNavigationBar.java` | 大屏适配专项再评估 |
| `COUIListDetailView` / `COUIPercentWidth*` / `COUIGridLayout` | 响应式栅格与 list-detail 分栏容器 | 低 | `S`: `grid\COUIListDetailView.java`、`grid\COUIPercentWidthLinearLayout.java`、`grid\COUIResponsiveUtils.java` | `layout/`（响应式规则可先做 util） |
| `COUIChipGroupEditText` | 输入框内嵌 Chip（收件人式输入） | 低 | `S`: `chip\COUIChipGroupEditText.java` | Chip 落地后再评估 |
| `COUILottieLoadingView` | 系统默认的旋转加载指示器（Lottie 承载，但动画本体只是一个椭圆 + 一条 trim path） | **已做** | `S`: `progressbar\COUILottieLoadingView.java`；资源 `apktool\assets\coui_rotating_loading.json` / `_night`；`Theme.COUI` 的 `couiRotatingSpinnerJsonName`（`values\styles.xml` L11476 / L11678）；dimens `coui_lottie_loading_view_small/large_*` | **本轮已落地为 `basic/ProgressIndicator.kt` 的 `RotatingProgressIndicator`**。原判「不建议做（依赖 Lottie，平台受限）」有误：该资源只是一个 26×26 椭圆 + 单条 trim path，可用一个 `drawArc` 完整复刻，**不需要 Lottie 依赖**，且它正是 `Theme.COUI` 绑定的系统默认加载指示器 |
| `COUICompProgressIndicator` | 加载指示器 + 文案组合件 | 低 | `S`: `progressbar\COUICompProgressIndicator.java` | `RotatingProgressIndicator` + Text 组合即可（注意它承载的是**裸圆弧旋转器**而非圆环+圆点形态，且对尺寸有 `coui_loading_max_large_width/height` = 40dp 的上限校验，超出会打 warning） |
| `COUISlideSelectPreference` | 点击后在当前值处弹出对齐选择列表 | 低 | `S`: `preference\COUISlideSelectPreference.java`；layout `coui_preference_widget_select.xml` | 已有 SpinnerPreference 近似覆盖，观感差异再校准 |
| `COUILoadingPreferenceCategory` | 分组标题右侧带加载圈（扫描中…） | 低 | `S`: `preference\COUILoadingPreferenceCategory.java`；layout `coui_preference_category_widget_layout_textbutton.xml`（同族 textbutton 变体） | `basic/SmallTitle.kt` 扩展 |
| `COUIPagerHeaderPreference` / `COUIPagerFooterPreference` / `COUIBottomPreference` / `COUISpannablePreference` | 列表首尾留白、富文本说明等杂项 preference | 低 | `S`: `preference\COUIPagerHeaderPreference.java` 等 | `coui-preference/`（按需） |
| `COUICheckBoxWithDividerPreference` / `COUISwitchWithDividerPreference` | 带竖分隔线的复合行（左区点击进入、右区独立开关） | 低 | `S`: `preference\COUISwitchWithDividerPreference.java`、`COUICheckBoxWithDividerPreference.java` | `coui-preference/preference/` |
| `COUIOpenSourceStatement` | 开源声明长文页 | 低 | `G`: `opensource\COUIOpenSourceStatementAdapter*.java` | 不建议单独做 |
| `COUIBottomFloatingToolbar` | 文本选择浮动工具条（横排操作 + 溢出菜单） | 低 | `T`: `bottomfloatingtoolbar\COUIBottomFloatingToolbar.java` 等 | 已有 `basic/FloatingToolbar.kt` 近似覆盖，按需校准 |

---

## 二、TextField 输入框多形态差距专项

我们现状：`basic/TextField.kt` 只有「圆角矩形背景 + 浮动 label + leading/trailing 槽位」一种形态（`TextFieldDefaults.CornerRadius`，rect 模式）。COUI 的 `couiBackgroundMode` attr 枚举为 `none(0) / line(1) / rectangle(2) / noLine(3)`（`S\apktool\res\values\attrs.xml` L732-737）。逐形态对照：

| 形态 | COUI 实现证据 | 我们现状 | 结论 |
| :--- | :--- | :--- | :--- |
| rect 模式（圆角矩形背景） | `couiBackgroundMode=rectangle`；drawable `coui_edittext_default_background.xml` | TextField 默认即此 | 已覆盖 |
| line 模式（下划线，聚焦下划线变色加粗动画） | `couiBackgroundMode=line`；`COUIEditText` 内部 line paint 逻辑 | 无 | **缺失**：TextField 增加 `backgroundMode` 参数 |
| none / noLine 模式（无背景裸输入） | `couiBackgroundMode=none/noLine` | 可用透明色勉强模拟，无显式 API | **缺失**（低成本，随 backgroundMode 一并做） |
| 错误态（红描边/红下划线 + 内容抖动动画 + 错误文案变色） | `edittext\COUIErrorEditTextHelper.java`（含 shake 动画）；drawable `coui_edittext_warning_background.xml` | 无 `isError` 参数 | **缺失**：`isError` + 抖动动画 + `errorColor` 色槽 |
| 字数统计（右下 `当前/上限`，超限变红） | `COUIEditText` maxCount 逻辑与 `COUIInputView` 计数行 | 无 | **缺失** |
| 密码可见切换（眼睛钮，密文/明文切换） | `edittext\COUICheckBoxPwd.java`；drawable `coui_edittext_password_icon.xml` | 需调用方自己塞 trailingIcon | **缺失**（提供预制 trailing 组件） |
| 快速删除（聚焦且非空时显示 ×，一键清空） | `edittext\COUIEditFastDeleteWatcher.java` | 无 | **缺失**（预制 trailing 组件） |
| 组合输入 `COUIInputView`（上方标题/必填星号 + 输入 + 错误行 + 计数 + 密码钮） | `edittext\COUIInputView.java`；layout `coui_input_view.xml` | 无 | **缺失**（新组件，见第一节高优先级） |
| 卡片输入 `COUICardSingleInputView` / `COUICardMultiInputView` | layout `coui_single_input_card_view.xml` / `coui_multi_input_card_view.xml` | 无 | **缺失** |
| 验证码 `COUICodeInputView`（N 格自动跳格） | `edittext\COUICodeInputView.java`；layout `coui_phone_code_layout.xml` | 无 | **缺失**（新组件） |
| 多行滚动 `COUIScrolledEditText` | `edittext\COUIScrolledEditText.java` | `lineLimits` + `scrollState` 已覆盖 | 已覆盖 |
| Chip 输入 `COUIChipGroupEditText` | `chip\COUIChipGroupEditText.java` | 无 | 缺失（低优先级） |

> 注意：Miuix TextField 的「浮动 label」是 MIUI 特性；COUI 输入框无浮动 label，用 hint + `COUIInputView` 的上方独立标题。COUI 化时需评估默认关闭浮动动画。

---

## 三、点名核查结果（任务书指名类）

| 类名 | 是否存在 | 位置 / 说明 |
| :--- | :--- | :--- |
| `COUIChip` | 存在 | `S`: `chip\COUIChip.java` |
| `COUIToolTips` | 存在 | `S`: `tooltips\COUIToolTips.java` |
| `COUIBanner` | 存在（Settings 未捆绑） | `T`/`G`: `banner\COUIBanner.java`；`A`/`G` 有 layout `coui_banner_content_layout.xml` |
| `COUIPageIndicator` | 存在 | `S`: `indicator\COUIPageIndicator.java`（另有 `COUIPageIndicator2`、`A` 的 `COUIPageIndicatorKit`） |
| `COUIEmptyStateView` | 存在（仅 themestore） | `T`: `emptyview\COUIEmptyStateView.java`（T 无 apktool，取 res 需先解包） |
| `COUILoadingButton` | 存在 | `S`: `button\COUILoadingButton.java` |
| `COUIButtonGroup` | **类名不存在** | 对应物为 `buttonBar\COUIButtonBarLayout.java` + `button\SingleButtonWrap.java` / `SimpleButtonGroupCtrl.java` |
| `COUISegmentButton` | 存在（名为 Layout） | `S`: `segmentbutton\COUISegmentButtonLayout.java`；**已由 `basic/TabRow.kt` 的 `TabRowWithContour` 覆盖**（其 KDoc 即对标此类） |
| `COUIBottomTips` | **类名不存在** | tips 家族只有 TopTips（`COUIDefaultTopTips` 等）；底部浮条另有 `T` 的 `bottomfloatingtoolbar` |
| `COUIEditText` 多形态 | 存在 | 见第二节 |
| `COUIInputView` | 存在 | `S`: `edittext\COUIInputView.java` |
| `COUICodeInputView` | 存在 | `S`: `edittext\COUICodeInputView.java` |
| `COUILockPatternView` | 存在 | `S`: `lockview\COUILockPatternView.java` |
| `COUINumericKeyboard` | 存在 | `S`: `lockview\COUINumericKeyboard.java` |
| `COUIFullPageStatement` | 存在 | `S`: `statement\COUIFullPageStatement.java` |
| `COUIStepperView` | 存在，**已实现** | `S`: `stepper\COUIStepperView.java` → 本仓库 `basic/Stepper.kt` |
| `COUICalendarPicker` | 家族存在（无此确切类名） | `A`: `calendar\COUICalendarDayPickerView.java` 等 + `coui_calendar_picker_*.xml` |
| `COUITimeLimitPicker` | 存在 | `S`: `picker\COUITimeLimitPicker.java` |
| `COUIMarkPreference` | 存在 | `S`: `preference\COUIMarkPreference.java` |

---

## 四、已覆盖对照（无需新建，仅后续观感校准）

| COUI 家族 | 本仓库对应 | 备注 |
| :--- | :--- | :--- |
| `button\COUIButton` | `basic/Button.kt` / `TextButton` | 缺 loading 形态（见高优先级） |
| `couiswitch\COUISwitch` | `basic/Switch.kt` | |
| `checkbox\COUICheckBox`（三态） | `basic/Checkbox.kt` | 已含 `Indeterminate` 三态（对标 `coui_btn_part_check` 资产） |
| `seekbar\COUISeekBar` / `COUISectionSeekBar` / `COUIVerticalSeekBar` | `basic/Slider.kt`（`steps`/`keyPoints`）+ `VerticalSlider` | |
| `progressbar\COUIHorizontalProgressBar` / `COUICircularProgressBar` / `COUILoadingView` | `basic/ProgressIndicator.kt`（Linear/Circular/Infinite） | 缺 `COUIInstallLoadProgress`（见中优先级） |
| `stepper\COUIStepperView` | `basic/Stepper.kt` | 已按 COUI 对齐 |
| `picker\COUINumberPicker` | `basic/NumberPicker.kt` | 日期/时间组合件缺失（见高优先级） |
| `tablayout\COUITabLayout` / `segmentbutton\COUISegmentButtonLayout` | `basic/TabRow.kt` / `TabRowWithContour` | TabRow 缺可滚动 Tab + Tab 红点，随 Badge 一并补 |
| `toolbar\COUIToolbar` | `basic/TopAppBar.kt` | 溢出 action menu 依赖 ListPopup 组合 |
| `bottomnavigation\COUINavigationView` / `navigationrail\COUINavigationRailView` | `basic/NavigationBar.kt` / `NavigationRail.kt` | 角标待 Badge |
| `snackbar\COUISnackBar` | `basic/Snackbar.kt`（含 action） | 缺通知样式变体（见中优先级） |
| `cardview\COUICardView` / `cardlist\COUICardListHelper` | `basic/Card.kt` + `BasicComponent` 分组 | 分组圆角/按压态已具备，观感差异走换肤 |
| `dialog\COUIAlertDialogBuilder` | `overlay/OverlayDialog.kt` + `window/WindowDialog.kt` | 缺 rotating 变体（见高优先级）；security 变体已有 `SecurityDialogLayout.kt`；按钮栏纵横自适应已由 `layout/DialogButtonBar.kt` 覆盖（仅缺推荐按钮档，见中优先级） |
| `panel\COUIBottomSheetDialog` | `overlay/OverlayBottomSheet.kt` + `window/WindowBottomSheet.kt` | 列表面板变体随 ListPreference 做 |
| `poplist\COUIPopupListWindow` / `COUIPopupMenu`（含级联子菜单） | `basic/ListPopup.kt`、`Dropdown.kt` + `coui-preference` menu/popup 全家 | 指针位置 ContextMenu 低优先级 |
| `scrollbar\COUIScrollBar` | `basic/ScrollBar.kt` | |
| `searchview\COUISearchView` | `basic/SearchBar.kt` | 动画磁吸形态见中优先级 |
| `preference\COUISwitchPreference` / `COUICheckBoxPreference` / `COUIJumpPreference` / `COUIMenuPreference` / `COUIPreferenceCategory` / `COUISliderPreference 类` | `coui-preference` 的 SwitchPreference / CheckboxPreference / ArrowPreference / Dropdown&SpinnerPreference / `basic/SmallTitle.kt` / SliderPreference | |
| `floatingactionbutton\COUIFloatingButton`（单钮部分） | `basic/FloatingActionButton.kt` | 展开 speed-dial 见中优先级 |
| `scrollview` / `scroll`（弹性过滚动） | `utils/Overscroll.kt` 等 | 行为族，无需组件 |
| `viewpager\COUIViewPager2` | Compose `Pager` | 无需自建 |
| `rotateview` / `imageview` / `textview` / `tintimageview` | Compose 原语 | 无需自建 |

---

## 五、统计口径备注

- `S\sources\com\coui\appcompat\` 共 76 个子包、323 个 `COUI*` 类；其中约三分之一为纯 util/动效/兼容层（`animation`、`math`、`log`、`contextutil`、`darkmode`、`state`、`rippleutil`、`springchain`、`uiutil`、`version` 等），不计入组件缺口。
- `com\support\` 下 40 个模块 R 类（`animation`…`viewpager`）与 appcompat 子包一一对应，用于确认模块划分，未发现 appcompat 之外的额外控件族。
- Settings 未捆绑而其他 app 捆绑的家族：`banner`（A/T/G）、`calendar`（A）、`emptyview`（T）、`navigationrail`（A/T）、`sidenavigation`/`sidepane`/`slideview`/`bottomfloatingtoolbar`（T）、`aboutpage`/`completeview`/`opensource`（G）、`seekbar` 增强（T/G）。
- 实现任一组件前，按 `AGENT.md` §6 用对应 app 的 `apktool\res\`（dimens/colors/styles）+ 真机截图校准 token，本清单的入口只用于定位证据，不代替校准。

---

## 六、状态 / 变体缺口（2026-07-26 横向盘点）

> 对照方法：逐个 Read 本仓库 `basic/` + `coui-preference/` 公开 API 的参数集，对照 `S\apktool\res\values\styles.xml` 的 `Widget.COUI.*` 样式变体、`res\color\` selector 与 `res\layout\` 证据。只列「状态/变体」维度，整组件缺口见第一节。
> 标注：✅ = 本轮已实施；📝 = 仅记录（组件级改动，另行立项）。

### 已实施（✅，2026-07-26）

| 组件 | 补齐内容 | COUI 证据 | 落点 |
| :--- | :--- | :--- | :--- |
| Button | Small 尺寸档常量：`MinWidthSmall` 52dp / `MinHeightSmall` 28dp / `CornerRadiusSmall` 14dp（随高度胶囊）/ `InsideMarginSmall` 12dp/4dp；`TextButton` 新增 `textStyle` 参数（小档配 14sp） | `Widget.COUI.Button.Small`（styles.xml L13263）：`coui_btn_small_width_min` 52dp、`coui_btn_small_height_min` 28dp、padding 12/4、`coui_btn_small_text_size` 14sp、drawableRadius -1（胶囊） | `basic/Button.kt` |
| ProgressIndicator | large 档常量：`LargeCircularProgressIndicatorSize` 40dp / `...StrokeWidth` 5dp；`LargeInfiniteProgressIndicatorSize` 26dp / `...StrokeWidth` 3.33dp | `coui_circular_progress_large_length` 40dp、`coui_circular_progress_large_stroke_width` 5dp、`coui_loading_view_large_width/height` 26dp、`coui_circle_loading_large_strokewidth` 3.33dp | `basic/ProgressIndicator.kt` |
| Snackbar | `Snackbar` 新增 `icon` 前置图标槽（30dp 盒 + 16dp 纵向净空 + 16dp 图文间距），经 `SnackbarHost` 的 `content` 插槽使用 | `coui_snack_bar_item.xml` 的 `iv_snack_bar_icon`；`coui_snack_bar_icon_width/height` 30dp、`coui_snack_bar_icon_margin_top_horizontal` 16dp、`coui_snack_bar_child_margin_horizontal_start` 16dp | `basic/Snackbar.kt` |
| DialogButtonBar（`COUIButtonBarLayout`） | 新增公开 `DialogButtonBar` + `DialogButtonBarAction` + `DialogButtonBarDefaults`，实现**自动纵排**：按 `onMeasure` 判定「文案放得下 且 恰好 2 个按钮 且 无推荐按钮」才横排，否则整栏翻纵排；文案用 `TextMeasurer` 单行测量，可用宽度按 `needSetButVertical` 公式（栏宽夹到 392dp 后按按钮数均分、减分割线、再减两侧 24dp）。纵排度量：52dp 最小高（最底部 64dp = +12dp）、14dp 垂直内边距（顶部无内容时 +6dp、最底部 +12dp）、栏上边距 16dp（>1 按钮时）、0.33dp 分割线内缩 24dp；顺序按 `resortButton` 反转（纵排 neutral/positive/negative，横排 negative/neutral/positive）；单独 negative 按钮回退横排度量（58dp）。已接入 `SecurityDialogLayout`、示例 app、两个 docs demo | `S`: `buttonBar\COUIButtonBarLayout.java`（`onMeasure` L530、`needSetButVertical` L206、`setButtonsVertical` L371、`resetVerButsPadding` L243、`resortButton` L320）；dimens `coui_dialog_max_width` 392dp、`coui_alert_dialog_button_horizontal_padding` 24dp、`coui_alert_dialog_vertical_button_min_height` 52dp、`coui_center_alert_dialog_vertical_button_paddingbottom_vertical_extra` 12dp、`coui_bottom_alert_dialog_vertical_button_padding_vertical_new` 14dp、`..._top_extra_new` 6dp、`..._bottom_extra_new` 12dp、`coui_bottom_alert_dialog_buttonbar_margintop` 16dp、`coui_delete_alert_dialog_divider_height_verticalbutton` 0.33dp、`coui_bottom_alert_dialog_horizontal_button_margin_default` 24dp | `layout/DialogButtonBar.kt`（新文件） |

### 状态完备性结论（逐组件）

| 组件 | 结论 | 备注 |
| :--- | :--- | :--- |
| Button / TextButton / LoadingButton | 基本完备 | primary / secondary / borderless / disabled / loading（LoadingButton，含 loadingText 三点动画）齐；Small 档本轮补齐。COUI **无描边 outline 按钮**（styles 全查无 stroke 变体，无需补）。📝 剩余：Tiny 色系变体（`Large.TinyFull` / `Small.TinySmall` / `.HalfColor` 半色调 / `.Translate` 透明，中性灰按钮 + `couiRoundType=common` 直角圆角 + 专用 `coui_btn_tiny_*_text_color` selector），属颜色/圆角预设族，可后续以 `textButtonColorsTiny*` 工厂低成本补 |
| Switch | 完备 | disabled / loading / disabled+checked（`disabledCheckedThumbColor` / `disabledCheckedTrackColor`）全有；loading 时吞点击与 COUISwitch 一致 |
| Checkbox | 完备 | On / Off / Indeterminate 三态 + disabled 全套颜色 |
| RadioButton | 完备 | enabled/disabled 全套（含 disabledSelectedColor） |
| Slider / VerticalSlider | 大体完备 | disabled / steps（= SectionSeekBar 分段档，其样式仅关 physics）/ keyPoints / 竖向齐。📝 缺 **Intent 档**（`COUIIntentSeekBar`：secondaryProgress 第二进度层 `coui_seekbar_secondary_progress_color` + 4dp 固定轨道），需给 Slider 加 `secondaryValue` 层；📝 `couiSeekBarStartMiddle`（从中点起算的双向滑条）无对应参数 |
| Chip | 选中态完备 | selected / unselected / disabled×2 齐（对应 `chip_checked/unchecked_text_disable_color` selector）。**无下拉箭头 chip**（styles/attrs 全查无 arrow，无需补）。📝 缺关闭 X 形态：`Widget.COUI.Chip.Suggestion`（28dp 高，closeIcon 12dp，touch bounds 24dp）与 Input 形态（32dp 高，close 14dp，touch bounds 28dp，`coui_chip_input_style_*`）——涉及第二触摸目标，组件级；📝 Icon 档（`coui_chip_icon_style_chip_icon_size` 20dp）随之评估；ChipGroup 单/多选容器见第一节 |
| Badge | 完备 | dot / 数字（1..999 三档宽度动画）/ 1000+ 省略号 / stroke 描边形态 / BadgeBox 锚定全有 |
| ProgressIndicator | 大体完备 | 定值/不定值/large 档齐（本轮补 large）。**本轮补齐 `RotatingProgressIndicator`**：ColorOS 系统默认的不确定态旋转指示器（无背景环的裸圆弧、平头端点、1250ms 周期内转两整圈、弧长 273.6°↔50.4° 脉动），移植自 `coui_rotating_loading.json`，用单个 `drawArc` 实现、不依赖 Lottie。注意 COUI 的加载指示器**有两种形态并存**：强调色「圆环 + 环绕圆点」（刷新中状态用，见 `InfiniteProgressIndicator`）与中性色裸圆弧旋转器（`Theme.COUI` 的 `couiRotatingSpinnerJsonName` 默认值）。📝 COUICircularProgressBar 另有 **error / pause 图标态**（`coui_circular_progress_error_*` / `pause_icon_*` dimens），下载场景用，随 InstallLoadProgress 一并评估 |
| Stepper / NumberPicker / DatePicker / TimePicker | 完备 | 均有 `enabled` + disabled 颜色（Stepper 到边界还会单侧禁用）；NumberPicker disabled 吞手势 |
| Snackbar | 大体完备 | action / dismiss / 单行大圆角自适应齐；图标槽本轮补齐。📝 通知样式 `COUINotificationSnackBar` 见第一节中优先级 |
| TopTips | 大体完备 | 关闭型 / 按钮型互斥 + 图标 + 折行降级齐。📝 缺跑马灯 `COUIMarqueeTopTips`（依赖 MarqueeTextView，见第一节）与全自定义 `COUICustomTopTips` 插槽形态 |
| preference 系 | 完备（一处例外） | 全部 preference 均带 `enabled` 且经 `BasicComponent` 将 title/summary 变灰、widget（Switch/Checkbox/箭头/对勾）走各自 disabled 色。例外：`RecommendedPreference` 无 `enabled` 参数（COUI 原件为链接卡，disabled 场景罕见，暂记录） |
| Dialog / BottomSheet | 形态缺口 | Loading（rotating）/ Security 双宿主（overlay+window）齐；列表单选/多选由 List/MultiSelectListPreference 覆盖。📝 缺**居中警示形态**：`AlertDialogBuildStyle.Center` 家族 + `coui_center_alert_dialog_layout`（含 `.Tiny` / `.Rotate` 子形态），我们 Dialog 仅底部锚定，需 Dialog 增加 center 布局形态；📝 缺横向进度对话框 `coui_progress_dialog_horizontal.xml` |

### 本轮结论摘要

- 确凿缺失且已实施：Button Small 尺寸档、ProgressIndicator large 档、Snackbar 图标槽（3 项，均为小改）。
- 记录待立项（组件级）：Chip 关闭 X 形态、Slider Intent 档（secondaryProgress）、Dialog 居中警示形态、TopTips 跑马灯、Button Tiny 色系、CircularProgress error/pause 态、RecommendedPreference enabled。
- 证实无需补：Button outline 描边变体、Chip 下拉箭头变体（COUI 均无此物）；Switch「disabled+checked」视觉已有专用色槽。
