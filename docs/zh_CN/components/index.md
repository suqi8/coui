# 组件库

COUI 提供了丰富的 UI 组件，严格遵循 OPPO ColorOS 设计语言。每个组件都经过精心设计，确保视觉与交互效果与 ColorOS 原生体验一致。

## 脚手架

| 组件                               | 描述           | 常见用途           |
| ---------------------------------- | -------------- | ------------------ |
| [Scaffold](../components/scaffold) | 应用的基础布局 | 页面结构、内容展示 |

::: warning 注意
Scaffold 组件为跨平台提供了一个合适的弹出窗口的容器。`OverlayDialog`、`OverlayDropdownPreference`、`OverlaySpinnerPreference`、`OverlayListPopup` 等组件都基于此实现弹出窗口，因此都需要被该组件包裹。
:::

## 基础组件

| 组件                                                       | 描述                   | 常见用途             |
| ---------------------------------------------------------- | ---------------------- | -------------------- |
| [Surface](../components/surface)                           | 基础容器组件           | 内容展示、背景容器   |
| [TopAppBar](../components/topappbar)                       | 应用顶部的导航栏       | 页面标题、主要操作   |
| [NavigationBar](../components/navigationbar)               | 底部导航组件           | 主要页面切换         |
| [FloatingNavigationBar](../components/floatingnavigationbar) | 悬浮胶囊底部导航 | 覆盖在内容上的主页面切换 |
| [NavigationPopupMenu](../components/navigationpopupmenu) | 锚定底部导航栏的目的地菜单（含 Overlay 与 Window 两种变体） | 「更多」页面入口 |
ZH after NavigationBar: | [FloatingAccessoryNavigationBar](../components/floatingaccessorynavigationbar) | 带两侧附件的悬浮导航胶囊 | 主要页面切换 + 新建 / 搜索 |
| [NavigationRail](../components/navigationrail)             | 侧边导航组件           | 主要页面切换（大屏）  |
| [SideNavigationBar](../components/sidenavigationbar) | 响应式侧边导航抽屉（停靠 / 覆盖） | 平板与桌面的主页面切换 |
| [SidePaneLayout](../components/sidepanelayout) | 可收起的侧栏与内容分栏 | 大屏的文件夹 / 分类侧栏 |
| [ListDetailLayout](../components/listdetaillayout) | 列表-详情双栏容器 | 设置类主从页面 |
| [TabRow](../components/tabrow)                             | 水平标签页切换栏       | 内容分类浏览         |
| [TabWithSearchLayout](../components/tabwithsearchlayout) | 按百分比栅格响应式排布的标签栏 + 搜索框 | 带搜索的分类页 |
| [SegmentButton](../components/segmentbutton)               | 带滑动指示器的分段控件 | 少量视图或筛选切换   |
| [BreadcrumbBar](../components/breadcrumbbar)               | 水平面包屑导航栏       | 文件路径导航、位置轨迹 |
| [Card](../components/card)                                 | 包含相关信息的容器     | 信息展示、内容分组   |
| [CardListGroup](../components/cardlistgroup) | 卡片列表分组：自动计算行位置并绘制行间分隔线 | 设置页式分组列表 |
ZH after Card: | [PageIndicator](../components/pageindicator) | 带轨迹动画的圆点页面指示器 | 轮播、引导页 |
| [Banner](../components/banner) | 自动轮播的圆角卡片横幅 | 推广位、精选内容 |
| [ResponsiveGrid](../components/responsivegrid) | 响应式栅格与百分比宽度 | 宫格布局、宽屏居中内容 |
| [SpringChain](../components/springchain) | 懒加载列表/网格的边缘弹簧链 | 越界反馈、列表回弹 |
| [BasicComponent](../components/basiccomponent)             | 通用基础组件           | 自定义组件开发       |
| [Button](../components/button)                             | 触发操作的交互元素     | 表单提交、操作确认   |
| [TwoTextButton](../components/twotextbutton) | 两段文字交叉淡变的按钮 | 引导对话框、翻页下一步/开始 |
| [LoadingButton](../components/loadingbutton)               | 内置加载状态的文本按钮 | 异步操作提交         |
| [IconButton](../components/iconbutton)                     | 图标按钮组件           | 辅助操作、工具栏     |
| [Chip](../components/chip)                                 | 可选中的胶囊筛选标签   | 内容筛选、标签选择   |
| [ChipGroup](../components/chipgroup) | 可换行的 Chip 组，支持单选、必选与折叠 | 筛选、标签选择 |
| [ChipTab](../components/chiptab)                           | 胶囊式单选标签         | 页面筛选、分类切换   |
| [Text](../components/text)                                 | 展示各种样式的文字内容 | 标题、正文、描述文本 |
| [LinkText](../components/linktext) | COUI 链接样式的内联可点击链接文本 | 协议链接、“了解更多”提示 |
| [SmallTitle](../components/smalltitle)                     | 小型标题组件           | 辅助标题、分类标识   |
| [PreferenceCategory](../components/preferencecategory) | 分组标题，支持小 / 中 / 大三档、图标、红点与右侧控件 | 设置页分组标题 |
| [TextField](../components/textfield)                       | 接收用户文本输入       | 表单填写、搜索框     |
| [InputView](../components/inputview)                       | 卡片式输入框           | 表单填写、对话框输入 |
| [CodeTextField](../components/codetextfield)               | 分格验证码输入框       | 验证码 / OTP 输入    |
| [LockScreenPasswordInput](../components/lockscreenpasswordinput) | 锁屏密码输入框与圆形确认钮 | 锁屏 / 密码确认 |
| [NumericKeyboard](../components/numerickeyboard) | 圆形按键安全数字键盘 | PIN 输入、锁屏 |
| [SimpleLock](../components/simplelock) | 带填充 / 抖动动画的 PIN 圆点 | PIN 输入进度 |
| [LockPatternView](../components/lockpatternview) | 带错误态的九宫格图案解锁 | 图案解锁 / 设置 |
| [Switch](../components/switch)                             | 双态切换控件           | 设置项开关、功能启用 |
| [Checkbox](../components/checkbox)                         | 多选控件               | 多项选择、条款同意   |
| [RadioButton](../components/radiobutton)                   | 单选控件               | 单项选择、选项切换   |
| [Slider](../components/slider)                             | 调节值的滑动控件       | 音量调节、范围选择   |
| [IntentSlider](../components/intentslider)                 | 带缓冲进度的细长媒体进度条 | 视频/音乐播放进度    |
| [NumberPicker](../components/numberpicker)                 | 垂直滚动数字选择器     | 时间选择、数量选择   |
| [ExposureBar](../components/exposurebar)                   | 相机曝光补偿刻度面板   | 曝光补偿、相机工具   |
| [CameraCountdown](../components/cameracountdown) | 定时拍摄数字、长曝光读数与进度环 | 定时拍摄、夜景、录像停止 |
| [CameraRecordTimer](../components/camerarecordtimer) | 红色底板上的相机录像计时器 | 录像、倒计时 |
| [CameraZoomDial](../components/camerazoomdial) | 相机弧形对数变焦刻度环 | 相机变焦、镜头切换 |
| [CameraTopMenu](../components/cameratopmenu) | 相机顶部设置栏，图标可展开为选项条或分组面板 | 闪光灯、画幅、视频画质设置 |
| [CameraMenuButton](../components/cameramenubutton) | 相机圆形菜单按钮，带强调色选中态与边缘光，另含效果入口按钮 | 相机底部效果菜单、滤镜/虚化入口 |
| [CameraFocusIndicator](../components/camerafocusindicator) | 相机点按对焦框，带竖向曝光太阳滑杆与 AE/AF 锁定 | 点按对焦、曝光调节、相机预览 |
| [CameraShutterButton](../components/camerashutterbutton) | 相机快门（拍照 / 录像 / 录制中）与圆形相机控制按钮 | 相机拍摄、录像控制 |
| [CameraZoomBar](../components/camerazoombar) | 带加减按钮与倍率标签的摇杆式变焦条 | 相机连续变焦 |
| [CameraLensBar](../components/cameralensbar) | 相机倍率圆钮镜头切换条 | 相机界面切换镜头 / 倍率 |
| [CameraModeBar](../components/cameramodebar) | 带选中胶囊的相机模式条及模式标题 | 相机模式切换 |
| [DatePicker](../components/datepicker)                     | 日期滚轮选择器（年/月/日） | 日期选择、表单       |
| [TimePicker](../components/timepicker)                     | 时间滚轮选择器（时/分） | 时间选择、闹钟       |
| [DateTimePicker](../components/datetimepicker)             | 日期 + 时间滚轮选择器   | 提醒、日程           |
| [CalendarPicker](../components/calendarpicker)             | 可翻页的月历网格与日期滚轮 | 日期选择、日程       |
| [LunarDatePicker](../components/lunardatepicker)           | 农历日期滚轮选择器     | 生日、农历日期       |
| [Stepper](../components/stepper)                           | 加减调节整数值的控件   | 数量调整、数值微调   |
| [ProgressIndicator](../components/progressindicator)       | 展示操作进度状态       | 加载中、进度展示     |
| [ScrollBar](../components/scrollbar) | 可滚动容器的 COUI 滚动条 | 长列表、长页面 |
| [InstallLoadProgress](../components/installloadprogress)   | 带进度的下载/安装按钮  | 应用下载、安装操作   |
| [Snackbar](../components/snackbar)                         | 底部临时消息条组件     | 状态提示、操作结果   |
| [CustomSnackbar](../components/customsnackbar) | 为自定义内容提供提示条行为 | 自定义提示、富内容通知 |
| [TopTips](../components/toptips)                           | 页面顶部提示条         | 页面提示、公告       |
| [Tooltip](../components/tooltip)                           | 悬停或长按显示的简短标签 | 图标按钮标签、元素提示 |
| [Badge](../components/badge)                               | 锚点上的小型状态叠加     | 未读数、状态圆点       |
| [TagView](../components/tagview)                           | 「新」「BETA」等小型文字标签     | 功能标签、状态标签       |
| [MarqueeText](../components/marqueetext)                   | 两端渐隐的自动滚动单行文本     | 提示条、单行长提示       |
| [Icon](../components/icon)                                 | 图标展示组件           | 图标按钮、状态指示   |
| [FloatingActionButton](../components/floatingactionbutton) | 悬浮操作按钮           | 主要操作、快捷功能   |
| [FloatingToolbar](../components/floatingtoolbar)           | 悬浮工具栏             | 快捷操作、信息展示   |
| [FloatingEditToolbar](../components/floatingedittoolbar) | 悬浮多选编辑栏（图标 + 文字操作） | 列表多选操作 |
| [Divider](../components/divider)                           | 内容分隔线             | 区块分隔、层次划分   |
| [PullToRefresh](../components/pulltorefresh)               | 下拉触发刷新操作       | 数据更新、页面刷新   |
| [SearchBar](../components/searchbar)                       | 执行搜索的输入框       | 内容搜索、快速查找   |
| [ToolbarSearch](../components/toolbarsearch) | 从工具栏展开的搜索 | 顶栏内的搜索入口 |
| [SearchHistory](../components/searchhistory)               | 可折叠、可清空的搜索历史标签 | 搜索页、最近搜索     |
| [AlphabetIndexBar](../components/alphabetindexbar)         | 带弹出气泡的侧边字母索引条   | 联系人、应用列表     |
| [SwipeActionItem](../components/swipeactionitem)           | 侧滑露出操作的列表项         | 侧滑删除、快捷操作   |
| [ExpandableGroup](../components/expandablegroup)           | 带旋转箭头的可展开分组       | 可折叠的设置分组     |
| [FullPageStatement](../components/fullpagestatement)       | 全屏用户协议声明页     | 首次启动协议、隐私声明 |
| [UserStatementDialog](../components/userstatementdialog) | 底部面板式用户协议对话框 | 首次启动协议 |
| [IndividualStatementDialog](../components/individualstatementdialog) | 逐项隐私授权的声明对话框 | 分项隐私授权 |
| [EmptyState](../components/emptystate)                     | 插画、文案与操作的空页面 | 空列表、无搜索结果 |
| [CompleteState](../components/completestate)               | 成功/失败/等待结果动画 | 操作结果页           |
| [ColorPalette](../components/colorpalette)                 | 网格调色盘（含透明度） | 主题设置、颜色选择   |
| [ColorPicker](../components/colorpicker)                   | 选择颜色的控件         | 主题设置、颜色选择   |
| [ColorSwatchPicker](../components/colorswatchpicker)       | 圆点色板选择器（选中描边环） | 主题设置、颜色选择   |

## 扩展组件

| 组件                                                 | 描述                                                                               | 常见用途               |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------- | ---------------------- |
| [ArrowPreference](../components/arrowpreference)               | 基于 BasicComponent 的带箭头组件                                                   | 指示可点击、导航提示   |
| [SwitchPreference](../components/switchpreference)             | 基于 BasicComponent 的开关组件                                                     | 设置项开关、功能启用   |
| [SwitchLoadingPreference](../components/switchloadingpreference) | 带加载状态的开关设置项                                                             | 异步生效的设置项开关   |
| [SwitchWithDividerPreference](../components/switchwithdividerpreference) | 主区域可点击、分割线后开关独立切换的设置项 | WLAN / 蓝牙式入口 |
| [CheckboxPreference](../components/checkboxpreference)         | 基于 BasicComponent 的复选框组件                                                   | 多项选择、条款同意     |
| [MarkWithDividerPreference](../components/markwithdividerpreference) | 带分隔线与尾部操作的对勾单选行 | 带设置入口的选项行 |
| [SlideSelectPreference](../components/slideselectpreference) | 在值的位置弹出选择列表的行 | 原位选择取值 |
| [CheckboxWithDividerPreference](../components/checkboxwithdividerpreference) | 主区域可点击、分割线后复选框独立切换的设置项 | 带详情页的同步项选择 |
| [RadioButtonPreference](../components/radiobuttonpreference)   | 基于 BasicComponent 的单选组件                                                     | 单项选择、选项切换     |
| [MarkPreference](../components/markpreference)                 | 带选中标记的单选行                                                                 | 单选列表、选项选择     |
| [ButtonPreference](../components/buttonpreference)             | 尾部带小型按钮的设置项                                                             | 设置列表内联操作       |
| [ListPreference](../components/listpreference)                 | 打开底部选择面板的设置项（需在 `Scaffold` 中使用）                                 | 单选 / 多选设置项      |
| [MultiSelectListPreference](../components/multiselectlistpreference) | 打开多选列表的偏好项行 | 选择多个选项 |
| [RecommendedPreference](../components/recommendedpreference)   | 推荐相关设置的圆角卡片                                                             | 相关设置推荐           |
| [InputPreference](../components/inputpreference)               | 内嵌输入框的卡片列表行                                                             | 设备名称、行内文本设置 |
| [EditTextPreference](../components/edittextpreference)         | 点击弹出输入对话框编辑值的设置项（需在 `Scaffold` 中使用）                         | 昵称、短文本设置       |
| [StepperPreference](../components/stepperpreference)           | 带步进器的设置项                                                                   | 数量、小范围数值设置   |
| [CardInstructionPreference](../components/cardinstructionpreference) | 带插图轮播与描述的页首说明卡片                                               | 功能介绍、手势引导     |
| [CardEntrancePreference](../components/cardentrancepreference) | 大 / 小入口卡片宫格                                                                | 设置首页入口           |
| [AppInfoPreference](../components/appinfopreference)           | 关于页应用信息头（图标、名称、版本）                                               | 关于页                 |
| [StatusSwitchingPreference](../components/statusswitchingpreference) | 尾部在状态文本、图标和加载动画间切换的设置项 | 开关状态异步切换 |
| [LoadInstallProgressPreference](../components/loadinstallprogresspreference) | 尾部带下载 / 安装进度按钮的设置项 | 应用下载与更新 |
| [LoadingPreferenceCategory](../components/loadingpreferencecategory) | 尾部带加载动画的分组标题 | 设备扫描、列表加载 |
| [CheckboxPreferenceCategory](../components/checkboxpreferencecategory) | 尾部带三态全选复选框的分组标题 | 批量选择分组 |
| [ActivityDialogPreference](../components/activitydialogpreference) | 在整页中展示选项的列表设置项（需在 `Scaffold` 中使用） | 选项较多的单选设置 |
| [CardButtonPreference](../components/cardbuttonpreference) | 图标加标题的卡片按钮宫格 | 快捷操作、入口宫格 |
| [SpannablePreference](../components/spannablepreference) | 摘要含可点击链接的设置项 | 带“了解更多”的设置项 |
| [PagerHeaderPreference](../components/pagerheaderpreference) | 第一张卡片上方的页首说明 | 设置页介绍 |
| [PagerFooterPreference](../components/pagerfooterpreference) | 可含链接的页尾说明 | 设置页备注、相关链接 |
| [BottomPreference](../components/bottompreference) | 收尾设置列表的不可见占位 | 设置页底部留白 |
| [SliderPreference](../components/sliderpreference)             | 基于 BasicComponent 的滑块组件                                                     | 数值调节、音量/亮度    |
| [RangeSliderPreference](../components/rangesliderpreference)   | 基于 BasicComponent 的范围滑块组件                                                 | 范围选择、价格筛选     |
| [OverlayListPopup](../components/overlaylistpopup)             | 基于 BasicComponent 的列表弹窗组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlayCascadingListPopup](../components/overlaycascadinglistpopup) | 二级级联列表弹窗组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用）               | 子菜单、分组动作面板   |
| [OverlayDropdownPreference](../components/overlaydropdownpreference) | 基于 BasicComponent 的下拉选择器组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlaySpinnerPreference](../components/overlayspinnerpreference) | 基于 BasicComponent 的高级选择器组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 进阶选项选择、功能列表 |
| [OverlayDropdownMenu](../components/overlaydropdownmenu)       | 基于 BasicComponent 的动作菜单组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 动作菜单、多选场景     |
| [OverlayIconDropdownMenu](../components/overlayicondropdownmenu) | 基于 IconButton 的动作菜单组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用）     | 工具栏动作、更多菜单   |
| [OverlayIconCascadingDropdownMenu](../components/overlayiconcascadingdropdownmenu) | 基于 IconButton 的二级级联动作菜单组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 含子菜单的工具栏动作   |
| [OverlayBottomSheet](../components/overlaybottomsheet)         | 基于 BasicComponent 的底部抽屉组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 底部抽屉、附加选项     |
| [OverlayDialog](../components/overlaydialog)                   | 基于 BasicComponent 的对话弹窗组件（使用 COUIPopupUtils，需在 `Scaffold` 中使用） | 提示、确认操作         |
| [LoadingDialog](../components/loadingdialog)                   | 带旋转加载指示的加载对话框（含 Overlay 与 Window 两种变体）                        | 阻塞式加载反馈         |
| [ListDialog](../components/listdialog) | 列表、单选与多选对话框（含 Overlay 与 Window 两种变体） | 从选项中选择 |
| [ProgressDialog](../components/progressdialog) | 水平进度条对话框（含 Overlay 与 Window 两种变体） | 下载、安装进度 |
| [InputDialog](../components/inputdialog) | 单输入框对话框（含 Overlay 与 Window 两种变体） | 重命名、快速输入 |
| [SecurityDialog](../components/securitydialog)                 | 安全声明对话框（含 Overlay 与 Window 两种变体）                                    | 隐私声明、安全确认     |
| [GuideDialog](../components/guidedialog) | 带插图的分页引导对话框（含 Overlay 与 Window 两种变体） | 新功能引导、提示 |
| [DialogButtonBar](../components/dialogbuttonbar)               | 对话框按钮栏，文案放不下时自动翻成纵排                                             | 对话框动作             |
| [WindowListPopup](../components/windowlistpopup)     | 窗口级列表弹窗组件                                                                 | 选项选择、功能列表     |
| [WindowCascadingListPopup](../components/windowcascadinglistpopup) | 窗口级二级级联列表弹窗组件                                                         | 子菜单、分组动作面板   |
| [WindowDropdownPreference](../components/windowdropdownpreference) | 窗口级下拉选择器组件                                                               | 选项选择、功能列表     |
| [WindowSpinnerPreference](../components/windowspinnerpreference) | 窗口级高级选择器组件                                                               | 进阶选项选择、功能列表 |
| [WindowDropdownMenu](../components/windowdropdownmenu) | 窗口级动作菜单组件                                                                 | 动作菜单、多选场景     |
| [WindowIconDropdownMenu](../components/windowicondropdownmenu) | 基于 IconButton 的窗口级动作菜单组件                                               | 工具栏动作、更多菜单   |
| [WindowIconCascadingDropdownMenu](../components/windowiconcascadingdropdownmenu) | 基于 IconButton 的窗口级二级级联动作菜单组件                                       | 含子菜单的工具栏动作   |
| [ContextMenu](../components/contextmenu) | 在长按或右键位置弹出的浮层 / 窗口菜单 | 文件、列表项操作   |
| [WindowBottomSheet](../components/windowbottomsheet) | 窗口级底部抽屉组件                                                                 | 底部抽屉、附加选项     |
| [WindowDialog](../components/windowdialog)           | 窗口级对话框组件                                                                   | 提示、确认操作         |
