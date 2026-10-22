<!-- Copyright 2026, compose-coui-ui contributors -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# COUI17 复刻工程交接文档

> 撰写日期：2026-10-12　分支：`coui17`（37 个提交，基于 `main`）
> 读者：接手 COUI17 复刻的下一位工程师（或未来的自己）
> 阅读顺序：§1 现状 → §2 取证资产 → §3 已完成 → §4 未完成 → §5 已知坑 → §6 下一步建议

---

## 1. 工程现状一句话

COUI17 复刻的 **token 层（色值/字号/圆角/高度）大部分已对齐但并非 100%**（2026-10 基础框架审计修正了滑块/禁用色、行高口径、字阶、卡片圆角 12→24dp 等；平滑圆角已按 Oplus 超椭圆 n=weight 精确复刻，见 §4.3）、**TopBar 页面层 90%**（含设置主页演示页）、**统一材质化接口 `COUIMaterialEffect` 骨架**；剩余硬骨头是 **material_shader.glsl（21,904 字节，提取自 libhwui.so）的完整 shader 级复刻**——当前 `materialPanel`/`materialGlow` 是简化版，与真机观感仍有肉眼可辨差距。

## 2. 取证资产（全部已落盘，勿重复提取）

| 资产 | 路径 | 内容 |
| :-- | :-- | :-- |
| 设置 17.0.0 | `E:\AndroidStudioProjects\jadx\work\coui17_settings\` | `apktool/`（dimens/colors/styles/layout）+ `jadx/`（`com.coui.appcompat.toolbar.*`、`ToolbarMaterialEffectDelegate`、`AppBarBlurHelper`、`COUICollapsingToolbarLayout`） |
| 开机注册 17.0.237 | `E:\AndroidStudioProjects\jadx\work\coui17_setup\` | 203 个 `coui_` 色值 |
| 材质化框架 jar | `C:\Users\ak004\Downloads\oplus-framework.jar` → 已反编译至 `jadx\work\oplus-framework\` | `OplusMaterialCornerParams/EdgeParams/ShadowParams/BaseParams/Util`（Java 参数层） |
| 渲染器 native | `C:\Users\ak004\Downloads\libhwui.so`（17 MB，aarch64） | **shader 实现层**。已用 Python 解析 ELF，从 `.rodata` 提取完整材质化 shader： |
| **材质化 shader** | `E:\AndroidStudioProjects\jadx\work\oplus-framework\material_shader.glsl`（21,904 字节） | **核心资产**。包含 `sdCapsule`（胶囊 SDF+法线）、`sdRoundRect`（G2 超椭圆，Lp norm）、`main`（8 光源聚光：diffuse+specular+deepGlow）、`sdBezierDistance`/`approxSdSquircle`（贝塞尔 squircle）、`u_shadowArray`/`u_shadowShapeCtxArray`（阴影/描边上下文） |
| TopBar 深度审计 | `jadx\work\coui17_settings\topbar-audit.md` | 顶栏逐类审计记录 |

工具：JADX `E:\jadx-1.5.0\bin\jadx.bat`；Apktool `E:\ApkTool\apktool.jar`；ELF 解析用 Python struct（见会话记录，脚本未落盘）。

## 3. 已完成（✅，可直接信赖）

### 3.1 基础 token 层（已按 17 取证对齐，遗留见 §4.3）
- 主色蓝系 `#0080FF`/`#1A8CFF`、语义绿/橙、文字 alpha 族、`Colors.kt` 全量；2026-10 修正 `sliderBackground`（container12 `#1F000000`/`#33FFFFFF`）、`disabledPrimarySlider`（couiColorDisable）、暗色 `disabledSecondaryVariant` `#1AFFFFFF`、Monet 浅色遮罩 0.2，新增 `press`/`hover`/`focus`。
- 字阶：`TextStyles` 行高 = 字号 × lineSpacingMultiplier × 1.1719（Android 乘字体度量高）；`main` 16sp、`footnote1` 13sp/1.1739、`footnote2` 12sp、字重写进 token。
- 列表：`CardDefaults.CornerRadius` 24dp（`COUIRoundCorner.Normal.XL`）；摘要 13sp + `onSurfaceSecondary`、去掉 2dp 标题摘要间距；右侧值 13sp；分隔线 1px 下限；行按压最小可见进度 0.7；开关行按开关也带整行按压。
- `DialogDefaults.CornerRadius` = 24dp（`coui_dialog_background_corner_radius`）。
- `FloatingToolbarDefaults.CornerRadius` = 20dp。
- `TopAppBarDefaults`：展开高 110dp、折叠标题 24sp、大标题边距 **24dp**（`coui_appbar_title_expanded_margin_start/end`，**勿再用 compat 的 16dp**）、`ActionIconGap` = **4dp**（`coui_toolbar_gap_before_menu`）、divider margin 24→0dp、divider 色 `coui_color_divider`（L `#1F000000`/D `#33FFFFFF`）。

### 3.2 TopBar 页面层
- example 演示页 `SettingsHomeTopBarPage.kt`（155 行）：116dp 展开 + 40sp 大标题 + 胶囊搜索槽 + 返回按钮，Route `SettingsHomeTopBar`，入口在 Other 页。
- **叠层顺序（关键修复）**：`TopAppBarGlow` 必须先画（垫底）、`TopAppBar` 背景透明在上——否则凝光 `matchParentSize` 会盖住标题/搜索/按钮，表现为「上滑内容全消失」（commit `707b4e68`）。
- 凝光 alpha 驱动：**`min(1, scrollPx / 30dp)`**（`AppBarBlurOverlayBehavior.computeOverlayAlpha`），传 `scrollOffsetPx = abs(heightOffset)`，**勿用 `collapsedFraction`**（那是折叠比例，要滚完 116dp 才满，凝光出现太晚）（commit `64262a7e`）。
- 渐变 ramp：浅色 pos[0, 0.4, 1] × alpha[0, 0.7, 1]；深色 pos[0, 1] × alpha[0, 0.4]（`GradientBlurConfig.darkPositions/darkAlphas`，`GRADIENT_DARK_ALPHA=0.4` 是 **alpha 值不是位置**——曾误写成 pos[0,0.6]×alpha[0,1]）。

### 3.3 IconButton（已对齐 17 几何）
- **圆形 48×48dp**（width==height；`coui_toolbar_back_view_tiny_width=48`。`coui_action_menu_item_min_height=40` 只是最小约束，不是渲染尺寸——曾因此被压成 48×40 胶囊）。
- 面板底色 L `#FFF0F0F0`/D `#FF333333`（`ToolbarMaterialEffectDelegate.BLUR_WINDOW_VIEW_BG_COLOR_*`）。
- 6dp elevation 阴影 + 2dp 胶囊描边（edgeAlpha L 0.2/D 0.5，`StrokeEffectParams`）+ 圆形涟漪 12% + 按压缩放 0.92（spring stiffness 438.65）。

### 3.4 统一材质化接口（骨架已立）
`coui-ui/.../effect/COUIMaterialEffect.kt`（246 行）：
- `Modifier.materialPanel(...)` — 按钮/面板统一入口（fill+stroke+shadow+spotlight+caustic+press scale），`IconButton` 已迁移。
- `Modifier.materialGlow(...)` — 顶栏凝光统一入口，`TopAppBarGlow`（coui-blur）已迁移；**`coui-blur` 新增 `implementation(projects.couiUi)` 依赖**（无循环，coui-ui 只依赖 couiCore/couiSquircle）。
- `capsulePath`/`drawSpotlight`/`drawCausticShadow` — **当前为简化近似**，与 §4 的完整 shader 差距在此。

### 3.5 分段控件
`TabRowWithContour`：容器 40dp、滑块 inset 4dp、滑块满高、无阴影（几何已对齐）。

## 4. 未完成（⬜/🚧，按优先级）

### 4.1 【最高优先】material_shader.glsl 完整复刻

> **进展（2026-10-22）**：顶栏按钮的描边与内阴影不来自 `material_shader.glsl`。`ToolbarMaterialEffectDelegate.applyStroke` 调用的是 `OplusMaterialUtil.setEdgeParams/setShadowParams`，它在 Java 侧的对应实现是 `com.coui.appcompat.shadowedge.agsl.ShadowEdge`（`blendEdge` + `blendShadowColor`，uniform 布局都是 `u_edgeArray`/`u_shadowArray`）。这部分已经移植完成：`coui-ui/.../effect/ShadowEdgeShader.kt` 包含 NEW_G2 角型的 SkSL、`toEdgeArray16`/`toShadowArray16` 映射，以及 `CIRCLE_1`/`CAPSULE_1` 预设。`materialPanel` 在支持 RuntimeShader 时（Android 33+ 及全部 Skiko 端）走 shader，否则回退到 Canvas 近似。验证情况：`desktopTest` 用 Skia 离屏渲染，PNG 输出在 `coui-ui/build/shadow-edge/`；真机 PJE110 深色模式实测正常。跟手聚光 `spotlight/agsl/SpotLight.java`（`TYPE_OPAQUE_SMALL_2`）也已移植到 `effect/SpotLightShader.kt`。下面这份 `material_shader.glsl` 在 jadx 里找不到调用方，不再作为复刻目标。

当前 `materialPanel` 是**单光源、无深辉光、无超椭圆**的简化版。完整复刻需实现（全部在 `material_shader.glsl`，行号已标注）：
1. **SDF 数学**：`powFast`（@65-74）、`lpNorm`（Lp 范数）、`sdCapsule`（@16-53，含 smoothMaxSqrt 软接合 + 法线）、`sdRoundRect`（@76-98，G2 超椭圆，`cornerSmooth>2.0` 时启用，否则走 capsule）。
2. **8 光源聚光循环**（`main` @106-216）：每光源 `diffuseTotal`（tintedWhite×atten）+ `specularTotal`（specAtten×specularStrength）+ `deepGlowTotal`（glowColor×atten×baseGlowShape×nDotL）。
3. **辉光形**：`baseGlowShape = core×0.6 + halo×0.4`（core=exp2(-dist×1.5)，halo=exp2(-dist×0.15)）。
4. **合成**：`result = fill + finalGlow − fill×finalGlow`，乘 `mask = 1−smoothstep(−aa, aa, d)`，亮度归一 `dot(rgb, 0.299/0.587/0.114)`。
5. **Squircle**：`sdBezierDistance`/`approxSdSquircle`（@219 起，贝塞尔距离场）。
6. **焦散**：`causticBaseSdr`/`causticBlendedSdr` + `setOutlineCausticShadow*` 六参数（sdfWidth/factor0/factor1/speed/luminanceThreshold/luminanceIntensity）。

落地建议：Compose 端用 `drawWithContent` + 离屏 `Canvas` 逐像素（或 `RuntimeShader` Android 端直接跑 SkSL——**注意桌面端 Skiko 也支持 RuntimeShader，可跨端共用同一份 SkSL 字符串**，这比用 Canvas 逐像素近似更接近原 shader）。按钮只有 48dp，逐像素成本可忽略。

### 4.2 顶栏 Material 三档枚举
DEFAULT 0px / IMMERSIVE 25px / DEFAULT_BLUR 20px（blend 层已并入 Glow，枚举未做）。

### 4.3 其他遗留
- Dialog 底角 34dp（底角独立规格）、Dialog 磨砂 240dp。
- 顶栏搜索框 `SearchViewAnimate` 52dp + `SearchBarStyle_v2`（圆角 40、描边 1、elevation 0）。
- subtitle 折叠字号 18sp（`coui_appbar_title_collapsed_with_subtitle_text_size`，当前 body2 14sp 无折叠变化，且 `hideSubtitleOnCollapse` 默认 true，影响小）。
- title icon（`coui_appbar_title_icon_size` 16dp / margin 12dp）概念未实现。
- 组件级专项：Checkbox/RadioButton `__17` 勾选动画、选中态凝光（详见 `docs/coui17-migration-checklist.md` §三）。`coui_color_ripple_press_background` 在 17 上不消费，已移出。
- 平滑圆角（已完成）：Oplus `addSmoothRoundRect` 的 weight 即 Lamé 超椭圆指数 n（`|x|^n+|y|^n=r^n`，在 r 处与直边相切；17 对话框截图拟合 r=45.1dp、n=3.00）。`coui-squircle` 已改为 `weight` 参数（默认 2.5），shader 用与 libhwui G2 相同的 Lp-norm SDF，Path 用超椭圆三次拟合；调用点按 `COUIRoundCorner.Normal.Weight`=2.5 / `Large.Weight`=3.0 / `CircularWeight`=2.0 传入。权重 <2（2026-10-23 补齐）：按 libhwui `g2RoundRectCommonFill` 的 `superN<2` 分支实现 `mix(sdRoundRect(n=2), sdCapsule, smoothstep(2,1,n))`，`sdCapsule` 半径取短边一半、kMax 26.6 / 带宽 70·60 × r·0.0125、kAbs 1px；与 `OplusPathSmooth` 一致仅在全圆（四角相等且 ≥ 短边一半）时生效，否则按 2.0。shader 逐像素与原生公式差 ≤0.002 alpha，Path 追踪零等值线差 ≤0.001px；软胶囊衔接处比圆弧胶囊内收约 0.020r。胶囊调用点改用 `COUIRoundCorner.CapsuleWeight`=1.0（COUIButton 胶囊、Chip、FloatingToolbar、FloatingNavigationBar、宽>高的 IconButton；圆形 IconButton 保持 2.0）。Tooltip 改 17dp/3.0（`coui_tooltips_os_16_1_radius_17_dp`）；TopTips 单行=72dp 胶囊/1.0、多行=28dp/3.0、切换 300ms 动画（`COUIDefaultTopTips.updateRadius`）；ListPopup 与级联菜单改 28dp/2.5（`RoundFrameLayout.getSmoothRadiusAndWeight`），揭示裁剪与级联展开形变（等半径时）同步。CodeTextField、Badge 数字胶囊、RecommendedPreference 改用 `LegacySquircleShape`（`COUIShapePath.getRoundRectPath` 逐字移植，128.19% 延伸）。遗留：① FloatingNavigationBar 默认 22dp 在 52dp+ 高度下不是全圆（COUI 为高度一半胶囊），故仍按 2.0 画；② 非全圆且未显式传权重的 `addSmoothRoundRect` 在 COUI 用 `default_g2_weight`（默认 2.45），本库调用点各自取 2.0/2.5；③ Badge 描边形态 COUI 外圈直接传未钳位的 72dp（`drawPointWithStroke`，路径自交），本库外圈用半高；④ 级联展开形变的上下不等半径帧仍为圆弧；⑤ SearchBar/Slider 等仍用 `CircleShape` 的胶囊未逐个核对；⑥ NavDisplay 转场裁剪用屏幕圆角，无 COUI 取证，沿用默认 2.5。
- 列表选中态：COUI 17 选中行画 `couiColorFocus` 蒙层并隐藏相邻分隔线（`COUIPreferenceItemDecoration`）；本库只在键盘焦点上画 `focus` 层，`BasicComponent` 无 selected 参数，分隔线也不会自动隐藏。

## 5. 已知坑（血泪记录，勿重蹈）

1. **增量编译内部类错乱**：改完 `coui-ui` 直接跑 demo 会 `NoClassDefFoundError`（Kotlin 内部类/lambda 索引错乱），表现为上滑即崩、内容消失。**修法：停 daemon + 删 `coui-ui/build coui-blur/build coui-preference/build example/*/build` 再全量重编**。
2. **`:example:desktop:run` 带 Hot Reload**（`JvmDevelopmentEntryPoint`），热重载缓存与全量重编冲突也会在 `ThemeColorSpec`/`AppTheme` 处炸 `NoClassDefFoundError`。验证大改动时先清缓存。
3. **凝光盖住内容**：`TopAppBarGlow` 与 `TopAppBar` 平级放 Box 且后画 = 内容全消失。必须凝光垫底 + TopBar `color = Color.Transparent`。
4. **凝光 alpha 驱动**：用 `collapsedFraction` 会滚 116dp 才满；正确是 `abs(heightOffset)/30dp`。
5. **GRADIENT_DARK_ALPHA=0.4 是 alpha 值**，深色 ramp 是 pos[0,1]×alpha[0,0.4]，不是 pos[0,0.6]。
6. **按钮 40dp 是最小约束不是渲染高度**，按钮必须 width==height=48dp 才是圆形（`isCapsule() = width!=height`）。
7. **按钮有两套实现**：旧系统 `COUIMaskRippleDrawable`（纯涟漪无底色）vs 17 材质化 `ToolbarMaterialEffectDelegate`（浮起面板）。**17 顶栏按钮走后者**——曾误判为无底色纯图标。
8. **ktlint 参数顺序**：`@Composable` 扩展函数的可选参数必须排最后（`materialPanel` 因 `interactionSource` 位置被 spotless 打回过，注意 `git status` 里那笔未提交的参数换位就是 spotlessApply 改的，提交前先跑一遍 spotlessCheck）。
9. **构建必走代理**：`HTTP_PROXY/HTTPS_PROXY=http://127.0.0.1:7897` + gradle `-Dhttp(s).proxyHost/Port`。
10. **提交前 `./gradlew :<module>:compileKotlinDesktop spotlessCheck`**；commit 格式 `<scope>: <summary>`。

## 6. 下一步建议（接手后第一周）

1. 跑通 demo：`./gradlew :example:desktop:run` → Other → COUI 17 Settings Top Bar，确认 §3 现状。
2. 提交 `COUIMaterialEffect.kt` 里那笔未提交的 spotless 参数换位。
3. 启动 §4.1：先把 `material_shader.glsl` 的 `sdCapsule`+`main` 聚光循环翻成 SkSL `RuntimeShader`，在 IconButton 上单光源→8 光源逐步对齐；真机截图对比（`adb shell screencap`）逐项校准 `u_shadowArray` 参数。
4. 同步更新 `docs/coui17-migration-checklist.md` 状态列。

---

**附：关键文件清单**

| 文件 | 说明 |
| :-- | :-- |
| `coui-ui/.../effect/COUIMaterialEffect.kt` | 统一材质化接口（246 行，简化版） |
| `coui-ui/.../basic/IconButton.kt` | 圆形 48dp 按钮（119 行，已迁移 materialPanel） |
| `coui-blur/.../blur/TopAppBarGlow.kt` | 顶栏凝光（154 行，已迁移 materialGlow） |
| `example/shared/.../SettingsHomeTopBarPage.kt` | 设置主页演示页（155 行） |
| `jadx/work/oplus-framework/material_shader.glsl` | **完整 shader（21,904 字节，复刻目标）** |
| `docs/coui17-migration-checklist.md` | 组件迁移状态表 |
| `docs/coui16-to-17-migration.md` | 迁移过程日志 |
