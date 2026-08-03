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
| [NavigationRail](../components/navigationrail)             | 侧边导航组件           | 主要页面切换（大屏）  |
| [TabRow](../components/tabrow)                             | 水平标签页切换栏       | 内容分类浏览         |
| [BreadcrumbBar](../components/breadcrumbbar)               | 水平面包屑导航栏       | 文件路径导航、位置轨迹 |
| [Card](../components/card)                                 | 包含相关信息的容器     | 信息展示、内容分组   |
| [BasicComponent](../components/basiccomponent)             | 通用基础组件           | 自定义组件开发       |
| [Button](../components/button)                             | 触发操作的交互元素     | 表单提交、操作确认   |
| [LoadingButton](../components/loadingbutton)               | 内置加载状态的文本按钮 | 异步操作提交         |
| [IconButton](../components/iconbutton)                     | 图标按钮组件           | 辅助操作、工具栏     |
| [Chip](../components/chip)                                 | 可选中的胶囊筛选标签   | 内容筛选、标签选择   |
| [Text](../components/text)                                 | 展示各种样式的文字内容 | 标题、正文、描述文本 |
| [SmallTitle](../components/smalltitle)                     | 小型标题组件           | 辅助标题、分类标识   |
| [TextField](../components/textfield)                       | 接收用户文本输入       | 表单填写、搜索框     |
| [InputView](../components/inputview)                       | 卡片式输入框           | 表单填写、对话框输入 |
| [CodeTextField](../components/codetextfield)               | 分格验证码输入框       | 验证码 / OTP 输入    |
| [Switch](../components/switch)                             | 双态切换控件           | 设置项开关、功能启用 |
| [Checkbox](../components/checkbox)                         | 多选控件               | 多项选择、条款同意   |
| [RadioButton](../components/radiobutton)                   | 单选控件               | 单项选择、选项切换   |
| [Slider](../components/slider)                             | 调节值的滑动控件       | 音量调节、范围选择   |
| [NumberPicker](../components/numberpicker)                 | 垂直滚动数字选择器     | 时间选择、数量选择   |
| [DatePicker](../components/datepicker)                     | 日期滚轮选择器（年/月/日） | 日期选择、表单       |
| [TimePicker](../components/timepicker)                     | 时间滚轮选择器（时/分） | 时间选择、闹钟       |
| [Stepper](../components/stepper)                           | 加减调节整数值的控件   | 数量调整、数值微调   |
| [ProgressIndicator](../components/progressindicator)       | 展示操作进度状态       | 加载中、进度展示     |
| [Snackbar](../components/snackbar)                         | 底部临时消息条组件     | 状态提示、操作结果   |
| [TopTips](../components/toptips)                           | 页面顶部提示条         | 页面提示、公告       |
| [Tooltip](../components/tooltip)                           | 悬停或长按显示的简短标签 | 图标按钮标签、元素提示 |
| [Badge](../components/badge)                               | 锚点上的小型状态叠加     | 未读数、状态圆点       |
| [Icon](../components/icon)                                 | 图标展示组件           | 图标按钮、状态指示   |
| [FloatingActionButton](../components/floatingactionbutton) | 悬浮操作按钮           | 主要操作、快捷功能   |
| [FloatingToolbar](../components/floatingtoolbar)           | 悬浮工具栏             | 快捷操作、信息展示   |
| [Divider](../components/divider)                           | 内容分隔线             | 区块分隔、层次划分   |
| [PullToRefresh](../components/pulltorefresh)               | 下拉触发刷新操作       | 数据更新、页面刷新   |
| [SearchBar](../components/searchbar)                       | 执行搜索的输入框       | 内容搜索、快速查找   |
| [FullPageStatement](../components/fullpagestatement)       | 全屏用户协议声明页     | 首次启动协议、隐私声明 |
| [ColorPalette](../components/colorpalette)                 | 网格调色盘（含透明度） | 主题设置、颜色选择   |
| [ColorPicker](../components/colorpicker)                   | 选择颜色的控件         | 主题设置、颜色选择   |
| [ColorSwatchPicker](../components/colorswatchpicker)       | 圆点色板选择器（选中描边环） | 主题设置、颜色选择   |

## 扩展组件

| 组件                                                 | 描述                                                                               | 常见用途               |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------- | ---------------------- |
| [ArrowPreference](../components/arrowpreference)               | 基于 BasicComponent 的带箭头组件                                                   | 指示可点击、导航提示   |
| [SwitchPreference](../components/switchpreference)             | 基于 BasicComponent 的开关组件                                                     | 设置项开关、功能启用   |
| [SwitchLoadingPreference](../components/switchloadingpreference) | 带加载状态的开关设置项                                                             | 异步生效的设置项开关   |
| [CheckboxPreference](../components/checkboxpreference)         | 基于 BasicComponent 的复选框组件                                                   | 多项选择、条款同意     |
| [RadioButtonPreference](../components/radiobuttonpreference)   | 基于 BasicComponent 的单选组件                                                     | 单项选择、选项切换     |
| [MarkPreference](../components/markpreference)                 | 带选中标记的单选行                                                                 | 单选列表、选项选择     |
| [ButtonPreference](../components/buttonpreference)             | 尾部带小型按钮的设置项                                                             | 设置列表内联操作       |
| [ListPreference](../components/listpreference)                 | 打开底部选择面板的设置项（需在 `Scaffold` 中使用）                                 | 单选 / 多选设置项      |
| [RecommendedPreference](../components/recommendedpreference)   | 推荐相关设置的圆角卡片                                                             | 相关设置推荐           |
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
| [SecurityDialog](../components/securitydialog)                 | 安全声明对话框（含 Overlay 与 Window 两种变体）                                    | 隐私声明、安全确认     |
| [DialogButtonBar](../components/dialogbuttonbar)               | 对话框按钮栏，文案放不下时自动翻成纵排                                             | 对话框动作             |
| [WindowListPopup](../components/windowlistpopup)     | 窗口级列表弹窗组件                                                                 | 选项选择、功能列表     |
| [WindowCascadingListPopup](../components/windowcascadinglistpopup) | 窗口级二级级联列表弹窗组件                                                         | 子菜单、分组动作面板   |
| [WindowDropdownPreference](../components/windowdropdownpreference) | 窗口级下拉选择器组件                                                               | 选项选择、功能列表     |
| [WindowSpinnerPreference](../components/windowspinnerpreference) | 窗口级高级选择器组件                                                               | 进阶选项选择、功能列表 |
| [WindowDropdownMenu](../components/windowdropdownmenu) | 窗口级动作菜单组件                                                                 | 动作菜单、多选场景     |
| [WindowIconDropdownMenu](../components/windowicondropdownmenu) | 基于 IconButton 的窗口级动作菜单组件                                               | 工具栏动作、更多菜单   |
| [WindowIconCascadingDropdownMenu](../components/windowiconcascadingdropdownmenu) | 基于 IconButton 的窗口级二级级联动作菜单组件                                       | 含子菜单的工具栏动作   |
| [WindowBottomSheet](../components/windowbottomsheet) | 窗口级底部抽屉组件                                                                 | 底部抽屉、附加选项     |
| [WindowDialog](../components/windowdialog)           | 窗口级对话框组件                                                                   | 提示、确认操作         |
