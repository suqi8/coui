<!-- Copyright 2026, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# COUI16 → COUI17 迁移文档（临时草稿）

> 本文件为**临时草稿**，记录 COUI16 → COUI17 迁移过程中的所有改动、依据与决策。
> 迁移完成后将整理为正式升级指南（并入 `docs/guide/upgrading.md`）。
> 取证来源：`coui17_settings`(设置 17.0.0) / `coui17_setup`(开机注册 17.0.237)，token 结论见
> `coui17-retheme-spec.md` 与 `E:\AndroidStudioProjects\jadx\work\coui17_settings\notes.md`。

## 变更摘要（持续更新）

| 日期 | 改动 | 文件 | 依据 | 状态 |
| :-- | :-- | :-- | :-- | :-- |
| 2026-09-28 | 新建 `coui17` 分支 | — | 用户要求 | ✅ |
| 2026-09-28 | 解包 COUI17 APK 取证 | `coui17_settings/` `coui17_setup/` | 用户提供的两个 APK | ✅ |
| 2026-09-28 | 写 COUI17 复刻 spec | `docs/coui17-retheme-spec.md` | 取证结论 | ✅ |
| 2026-09-28 | 建迁移组件清单 | `docs/coui17-migration-checklist.md` | 取证结论 | ✅ |
| 2026-09-28 | 蓝系刷新 16→17 | `coui-ui/.../theme/Colors.kt` | coui_color_blue 17=`#0080FF`/`#1A8CFF` | ✅ |
| 2026-09-28 | demo 色板同步 17 蓝/绿 | `example/.../ColorPickerSection.kt` | 17 蓝 `#0080FF`、绿 `#00BD13` | ✅ |
| 2026-09-28 | TopAppBar 展开高 107→110 | `coui-ui/.../basic/TopAppBar.kt` | `coui_appbar_title_expanded_height`=110 | ✅ |
| 2026-09-28 | TopAppBar 折叠标题 18→24sp | `TopAppBar.kt` `CollapsedTitleTextSize` | `coui_appbar_title_collapsed_text_size`=24 | ✅ |
| 2026-09-28 | 编译自检 + spotless 通过 | — | Gradle 缓存修复（清 transforms） | ✅ |

## 一、COUI17 vs COUI16 核心差异（用户/开发者视角）

**结论：17 是一次「主色提亮 + 新形态叠加」的迭代，不是推翻。**

1. **主色蓝系更通透**（唯一显著 token 变化）：
   - Light：`#0066FF` → **`#0080FF`**
   - Dark：`#247CFF` → **`#1A8CFF`**（更亮更透）
   - 联动：disabled/tertiary/primaryContainer/label_theme_blue 同步
2. **背景层级 / 卡片 / 文字 alpha / 分隔线 / 遮罩：完全不变**（灰底白卡、纯黑暗色、label 5 档沿用）。
3. **新设计语言「流体设计 / 凝光视效」**（新形态，非 token 替换）：
   - 凝光：控件选中态光晕/渐变（gradient token）
   - 柔性反馈：按压/拖拽/回弹，新增 ripple 专用按压色
   - 勾选动画化：Checkbox/Radio `__17` 过渡动画族
4. **页面壳子焕新**：设置主页 TopBar（116dp 大标题折叠 + 状态栏融合 + 毛玻璃叠加 + 胶囊搜索框）。

## 二、已落地代码改动（逐项）

### 2.1 `coui-ui/.../theme/Colors.kt` — 蓝系刷新

**lightColorScheme()**：
- `primary` / `primaryVariant` / `primaryContainer`：`0xFF0066FF` → `0xFF0080FF`
- `disabledPrimary` / `disabledPrimaryButton`：`0x4D0066FF` → `0x4D0080FF`
- `tertiaryContainer` / `tertiaryContainerVariant`：`0x260066FF` → `0x260080FF`
- `onTertiaryContainer`：`0xFF0066FF` → `0xFF0080FF`
- 文档注释：`coui_color_blue #0066FF` → `#0080FF`

**darkColorScheme()**：
- `primary` / `primaryVariant` / `primaryContainer`：`0xFF247CFF`/`0xFF5C9DFF` → `0xFF1A8CFF`
- `disabledPrimary` / `disabledPrimaryButton`：`0x4D247CFF` → `0x4D1A8CFF`
- `tertiaryContainer` / `tertiaryContainerVariant`：`0x40247CFF` → `0x401A8CFF`
- `onTertiaryContainer`：`0xFF5C9DFF` → `0xFF1A8CFF`
- 文档注释：`#247CFF` → `#1A8CFF`

> 未改槽位（与 17 一致，无需动）：secondary/error/surface/background/文字族/divider/mask/slider 全部沿用 16。

### 2.2 `example/.../ColorPickerSection.kt` — demo 色板同步

- 蓝 `0xFF0066FF` → `0xFF0080FF`
- 绿 `0xFF24B232` → `0xFF00BD13`（17 提亮绿）

## 三、待办（按迁移顺序）

### 3.1 基础层（TopBar 前置）
- [x] Colors.kt 蓝系刷新（light/dark）
- [ ] 编译自检通过（当前 Gradle 缓存损坏，待修复后验证）
- [ ] spotlessCheck

### 3.2 页面层 — 设置主页 TopBar（重点）
- [ ] 复刻 `homepage_toolbar.xml` 形态：116dp 展开大标题（40sp，settings 覆写）→ 折叠 52dp
- [ ] 状态栏融合 + 内容浮动滚动（`COUIFloatingScrollingViewBehavior`）
- [ ] `AppBarBlurOverlayView` 毛玻璃/凝光叠加层（滚动驱动 alpha，待 jadx 校准模糊半径/材质）
- [ ] 胶囊搜索框（`SearchBarStyle_v2`：圆角 40dp、描边 1dp、elevation 0、高 52dp）

### 3.3 组件级新形态（专项，另行立项）
- [ ] Checkbox/Radio `__17` 勾选动画
- [ ] 控件选中态光晕/渐变（凝光）
- [ ] 按压 ripple 专用态（`coui_color_ripple_press_background`）
- [ ] 圆角分档 weight（s/xs 1.5、xl/xxl 1.1、l/m 0.99）—— 高风险 flag
- [ ] Dialog 底角 33→34（coui_dialog_os_16_1_radius_33_dp=34）

## 四、关键决策记录

1. **语义绿/橙不进 Colors.kt**：Colors 无独立语义色槽（仅 error 系），绿 `#00BD13`/橙 `#FF7700` 仅 demo 色板用，已在 example 同步。
2. **圆角分档 weight 留专项**：squircle 精确化需改 shader，属高风险核心绘制，不并入 token 迁移。
3. **TopBar 迁移依赖基础层**：先完成 token/主题（蓝系），再做页面壳子（逻辑顺序）。
4. **不动 16 已对齐项**：背景层级、卡片、文字、分隔线、Switch/Slider 几何（17 与 16 一致）。

## 五、逆向工作区

- `E:\AndroidStudioProjects\jadx\work\coui17_settings\` — 设置 17.0.0（apktool + notes.md）
- `E:\AndroidStudioProjects\jadx\work\coui17_setup\` — 开机注册 17.0.237（apktool）
- 原始 APK 在各自 `input/` 下
