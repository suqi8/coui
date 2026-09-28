<!-- Copyright 2026, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# COUI17 迁移组件清单（对照盘点）

> 目标：把本仓库组件库从 COUI16 升级到 COUI17。
> 证据源：`coui17_settings`（设置 17.0.0）/ `coui17_setup`（开机注册 17.0.237）逆向 + `coui17-retheme-spec.md`。
> 标注：✅ 已迁移 / 🚧 进行中 / ⬜ 未开始 / 📝 组件级专项（另行立项）。
> 迁移顺序：**基础（token/主题层）→ 页面层（TopBar 等壳子）→ 组件级新形态**。

## 一、基础层（token / 主题）— TopBar 前置依赖

| 项 | 现状(16) | 目标(17) | 落点 | 状态 |
| :-- | :-- | :-- | :-- | :-- |
| 主色蓝系 | `#0066FF`/`#247CFF` | `#0080FF`/`#1A8CFF` | `Colors.kt` primary 家族、tertiary 蓝系 | ✅ |
| 背景层级/卡片 | 灰底白卡 | 同 16（不变） | `Colors.kt`（无需改） | ✅ 已是 17 值 |
| 文字族 alpha | label 5 档 | 同 16（不变） | `Colors.kt`（无需改） | ✅ |
| 语义色 error | `#DB382C`/`#FF6C61` | 同 16 | `Colors.kt`（无需改） | ✅ |
| 语义绿/橙 | 旧值 | 绿 L `#00BD13`、橙 night `#FF7700` | `Colors.kt` 语义色 | ✅ demo 色板已同步 |
| 圆角体系 | squircle ext 1.2819 | 分档 weight（s/xs1.5、xl/xxl1.1、l/m0.99） | `coui-squircle` + Defaults | 📝 高风险·flag |
| Dialog 底角 | 33dp | 34dp | `DialogContentLayout.kt` | ⬜ |
| FloatingToolbar 圆角 | 24dp | 20dp（`coui_floating_toolbar_item_circle_radius`） | `FloatingToolbarDefaults.CornerRadius` | ✅ 已改 20 |
| TopAppBar 展开高 | 107dp | 110dp | `TopAppBarDefaults.ExpandedHeight` | ✅ |
| TopAppBar 折叠标题字号 | 18sp | 24sp | `CollapsedTitleTextSize` | ✅ |

## 二、页面层壳子（TopBar 重点）

> 深度漏点对照（jadx 反编译 `com.coui.appcompat.toolbar.*`）已完成，见
> `E:/AndroidStudioProjects/jadx/work/coui17_settings/topbar-audit.md` 与 `coui17-retheme-spec.md` §3.1/§3.2。
> 迁移原则：**本库未对齐 COUI 规范的内容不保留（对齐不到就去除）；模糊/渐变/透明度按 17 权威值迁移**。

| 组件/形态 | COUI17 证据 | 现状 | 状态 |
| :-- | :-- | :-- | :-- |
| **设置主页 TopBar**（大标题折叠） | `homepage_toolbar.xml`：116dp 展开、40sp 标题、折叠 52dp、状态栏融合 | 现有 `TopAppBar` | 🚧 展开高/折叠字号已对齐，116/40sp 待办 |
| TopBar 展开高 | `coui_appbar_title_expanded_height`=110dp | 107dp | ✅ 已改 110 |
| TopBar 折叠标题字号 | `coui_appbar_title_collapsed_text_size`=24dp | 18sp | ✅ 已改 24 |
| 图标按钮尺寸 | 48×40dp（`coui_action_menu_item_min_width/height`） | 48×48 | ✅ 已改 48×40 |
| 文字按钮尺寸 | 60–104×40dp、padding 12（`coui_action_text_menu_item_*`） | `TopAppBarDefaults.TextAction*` 常量 | ✅ 已补常量 |
| 按钮面板底色 | L `#FFF0F0F0`/D `#FF333333`（`ToolbarMaterialEffectDelegate`） | `TopAppBarDefaults.buttonPanelColor()` | ✅ 已补 |
| TopBar 凝光渐变模糊 | Oplus gradient blur 240dp + 凝光 blend 层，滚动驱动，**默认关闭（enabled=false，自选启用）** | `TopAppBarGlow`（coui-blur） | ✅ 已实现 |
| 凝光 alpha 渐变底 | L 0/.7/1 @ pos 0/.4/1，D 0/1 @ pos 0/.6，下→上 | `TopAppBarGlow` | ✅ 已实现 |
| TopBar Material 三档 | DEFAULT 0px / IMMERSIVE 25px / DEFAULT_BLUR 20px + blend 层 | blend 层已并入 Glow | 🚧 blend 已实现，三档枚举待办 |
| 顶栏毛玻璃/凝光叠加 | `AppBarBlurOverlayView`（alpha=min(1,scroll/30dp)） | `TopAppBarGlow`（collapseFraction 驱动） | ✅ 已实现 |
| 对话框模糊半径 | `coui_list_dialog_background_blur_radius`=240dp | Glow 复用该值 | 🚧 Dialog 模糊待办 |
| 顶栏搜索框（胶囊） | `SearchViewAnimate` 52dp + `SearchBarStyle_v2`（圆角 40、描边 1、elevation 0） | 现有搜索组件（16 已迁移浮动搜索栏） | 🚧 16 已部分 |
| 顶栏菜单胶囊 | `coui_toolbar_menu_bg_radius`=24dp、icon 24dp | `IconButtonDefaults.CornerRadius`=24dp | ✅ 已一致 |
| 顶栏分段控件 | 容器 **40dp**、滑块 inset **4dp**、滑块（指示器）**40dp 满高**、**无阴影** | `TabRowWithContour`（40dp、inset 4、指示器满高、阴影已去除） | ✅ 几何已对齐 |

## 三、组件级新形态（专项，另行立项）

| 组件 | COUI17 新形态 | 证据 | 状态 |
| :-- | :-- | :-- | :-- |
| Checkbox | `__17` 勾选过渡动画（shape + 部分选中态） | `coui_checkbox_*__17` 动画族 | 📝 |
| RadioButton | `__17` 勾选过渡动画 | `coui_btn_radio_*__17` | 📝 |
| 控件选中态光晕/渐变 | 凝光视效（lock/keyboard gradient、checked_background_gradient_start） | gradient token | 📝 |
| 按压 ripple 专用态 | `coui_color_ripple_press_background` | press 资源 | 📝 |
| Switch/Slider 动效 | 17 是否调整 spring（待 jadx 校准） | — | 📝 |

## 四、对照方法

- 基础层：只改 `Colors.kt` / `Defaults` 值，light/dark 双套，编译自检。
- 页面层：以 `coui17_settings` 的 layout/style/dimen 为 ground truth，复刻到现有组件或新增。
- 组件级：先 `AGENT.md` §6 逆向取证据（jadx 看消费逻辑），不臆造。
- 每一项迁移后在上方表格标记 ✅，并在 `coui17-retheme-spec.md` 记录 token 来源。
