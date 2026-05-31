# COUI16 AGENT Guide

本文件汇总后续 AI 在本仓库里应遵守的核心规则。本仓库基于 [Miuix](https://github.com/compose-miuix-ui/miuix) 改造，目标是**就地演进成一套 COUI（ColorOS 设计语言）Compose Multiplatform 组件库**。

> 工程规范（构建命令、代码风格、API 约定、性能约束）见 `CLAUDE.md`，那份仍然有效，写 COUI 组件时**必须同时遵守**。本文件只补充「COUI 改造」这条主线：项目定位、设计来源、逆向校准工作流。

## 1. 必读目标

后续 AI 在本仓库里的默认目标不是只分析，而是：

1. 把 Miuix（MIUI/HyperOS 观感）逐步换皮成 COUI（ColorOS 观感）
2. 设计 token（颜色 / 圆角 / 字号 / 控件尺寸 / 间距 / 动效）**必须来自真实 ColorOS**，不靠拍脑袋
3. 信息不足时，先逆向 ColorOS 系统应用取证，再落代码
4. 直接完成代码实现，而不是停在「研究完了」
5. 改完做最小自检（编译 + 至少一个平台跑通）

除非用户明确只要分析，否则不要停在结论阶段。

## 2. 项目定位（已确认）

- 本仓库由 `git clone` 自上游 Miuix（`origin = compose-miuix-ui/miuix`），是改造基底。
- **定位：就地改造成 coui 库。** Miuix 作为基底，不再单独对齐上游；最终可改包名 / Maven 坐标为 coui。
- **改动深度：主题换肤优先（渐进）。** 先调 `theme/` 层默认值让现有组件整体呈现 COUI 观感，之后再按需逐个深挖组件。先不做大规模组件重构，除非某个组件的结构/交互和 ColorOS 差异大到换肤补不平。
- 目标设计系统：**COUI 16 = ColorOS 16 的设计语言**（仓库目录名 `COUI16` 即源于此）。

## 3. 设备与逆向工具环境（已确认）

| 项目 | 值 |
| :--- | :--- |
| 设备 | OnePlus PJE110（一加 13） |
| 系统 | ColorOS 16 / Android 16（`ro.build.version.oplusrom = V16.0.0`，SDK 36） |
| Root | 已 root（KernelSU，`u:r:ksu:s0`）→ 可提取 framework / overlay 资源 |
| ADB | 已连接（`adb devices` 可见），直接用 `adb` |
| JADX | `D:\jadx-1.5.0\bin\jadx.bat` |
| Apktool | `D:\ApkTool\apktool_3.0.1.jar` |
| 逆向工作区 | `D:\AndroidStudioProjects\jadx\work\<target_name>\` |

设备状态可能变化，每次进入逆向前先 `adb devices` 确认连接，再 `adb shell getprop ro.build.version.oplusrom` 确认版本未漂移。

### 关键逆向目标包（已确认存在于设备）

| 包名 | 用途 / 能提取什么 |
| :--- | :--- |
| `com.oplus.uxdesign` | **ColorOS 设计语言资源主库**，COUI 主色 / 强调色 / 设计规范的首选来源 |
| `com.android.settings` | 设置页：列表项、卡片、开关、分组标题、对话框的真实尺寸与配色 |
| `com.coloros.alarmclock` | 时钟：大标题字号、按钮、Tab、滑块、数字选择器的真实样式 |
| `com.android.systemui` | 状态栏 / 通知 / 控制中心：圆角、模糊、Toast/Snackbar 观感 |
| `com.android.launcher` | 桌面 / 文件夹：动效曲线、squircle 圆角参数 |
| `com.heytap.themestore` / `com.oplus.themestore` | 主题商店：强调色色板、动态取色参考 |
| `com.suqi8.coui.uitest` | 用户自建的 COUI 测试 app（包前缀与 OShin `com.suqi8.*` 同源），可作落地验证场 |

COUI 控件资源历史上常以 `couiXxx` / `coloros_xxx` / `oplus_xxx` 命名（颜色、dimen、style）。framework 级 COUI 资源可能在 RRO overlay 里：`com.oplus.framework.res.overlay`、`com.oplus.framework.rro.oneplus`、`com.android.systemui.oplus.res.overlay`（`adb shell cmd overlay list | grep oplus` 查看）。

## 4. Miuix → COUI 的换肤抓手（先看清，再动手）

Miuix 的视觉语言**高度集中在 `theme/` 层**，组件全部走 `MiuixTheme.colorScheme.*` / `MiuixTheme.textStyles.*` 取色取字，从不硬编码颜色 —— 这是换肤的核心利好，改默认值即可全局生效。

| 抓手 | 文件 | 改什么 |
| :--- | :--- | :--- |
| 配色 | `miuix-ui/src/commonMain/kotlin/top/yukonga/miuix/kmp/theme/Colors.kt` | `lightColorScheme()` / `darkColorScheme()` 的默认色值（约 50 个色槽）。当前主色是 HyperOS 蓝 `#3482FF`，COUI 应换成 ColorOS 主色 |
| 字体 | `…/theme/TextStyles.kt` | `Main/Body/Title/...` 的 `fontSize` / `fontWeight` / `lineHeight`。当前主字号 17sp |
| 形状 | 各组件 `ComponentDefaults.CornerRadius` + `miuix-squircle/` | COUI 圆角偏大且是连续曲率（squircle）；本仓库已内置 `miuix-squircle`，校准 squircle 平滑度参数即可 |
| 动效 | `…/anim/` | 缓动曲线 / 时长 |

**取色取字断言（来自真实代码，写组件时照此模式）**：组件如 `basic/Button.kt` 通过 `ButtonDefaults.buttonColors()` 取 `MiuixTheme.colorScheme.secondaryVariant` 等，形状走 `Modifier.squircleSurface(color, cornerRadius)`。所以「换主色」= 改 `Colors.kt` 默认值；「换某控件配色」= 改该组件的 `XxxDefaults.xxxColors()` 默认引用的色槽；**任何情况下都不在组件里写死 `Color(0x...)`**。

> ⚠️ 命名现状：色槽 / 主题类目前仍叫 `MiuixTheme` / `Colors` / `lightColorScheme`。换肤阶段**不重命名**，只改值，避免一次性大改爆炸。重命名为 `CouiTheme` 等留到后续「改包名 / 坐标」专项再做。

## 5. 设计 token 必须先逆向的判定

满足任一条件时，**不要直接硬写色值 / 尺寸**，先逆向取证：

- 需要 COUI 主色 / 强调色 / 语义色（错误红、链接蓝等）的精确值
- 需要某控件（开关、滑块、按钮、卡片、列表项、对话框）的真实圆角 / 尺寸 / 内边距
- 需要 ColorOS 字体梯度（标题 / 正文 / 脚注的 sp 与字重）
- 需要 squircle 平滑度 / 动效曲线参数
- 用户对某个组件「不像 ColorOS」提出具体异议

够明确的微调（如已确认主色后改一处引用）可直接改，不必每次都逆向。

## 6. 设计 token 逆向工作流

### 6.0 资源类逆向优先 Apktool

COUI 设计 token 主要是**资源**（`res/values/colors.xml`、`dimens.xml`、`styles.xml`、`color/*.xml`），不是 dex 逻辑。所以与 OShin（hook 逻辑、优先 JADX/DexKit）不同，**本仓库逆向优先 Apktool 解出 `res/`**，JADX 仅在需要看「某 token 在代码里怎么被消费 / 计算」时辅助。

### 6.1 目录规则

逆向工作区统一放到：

```text
D:\AndroidStudioProjects\jadx\work\<package_name>\
  input\      原始 APK
  jadx\       JADX 输出（看消费逻辑时用）
  apktool\    Apktool 输出（看 res/ token，主战场）
  notes.md    token 提取结论
```

### 6.2 提取已安装系统应用 APK

```powershell
$Package = "com.oplus.uxdesign"
$Target  = "D:\AndroidStudioProjects\jadx\work\$Package"

New-Item -ItemType Directory -Force -Path "$Target\input", "$Target\jadx", "$Target\apktool" | Out-Null

$RemotePaths = adb shell pm path $Package |
  ForEach-Object { $_.Trim() } |
  Where-Object { $_ -like "package:*" } |
  ForEach-Object { $_.Substring(8) }

$Index = 0
foreach ($Remote in $RemotePaths) {
  $LocalName = if ($Index -eq 0) { "$Package.apk" } else { "$Package-$Index.apk" }
  adb pull $Remote "$Target\input\$LocalName"
  $Index++
}
```

### 6.3 Apktool 解资源（主战场）

```powershell
$Package = "com.oplus.uxdesign"
$Target  = "D:\AndroidStudioProjects\jadx\work\$Package"
$BaseApk = "$Target\input\$Package.apk"

if (Test-Path "$Target\apktool") { Remove-Item "$Target\apktool" -Recurse -Force }
java -jar "D:\ApkTool\apktool_3.0.1.jar" d -f "$BaseApk" -o "$Target\apktool"
```

解出后，token 主要看：

- `apktool\res\values\colors.xml` / `values-night\colors.xml` —— 亮/暗色值
- `apktool\res\values\dimens.xml` —— 圆角、控件尺寸、间距
- `apktool\res\values\styles.xml` / `themes.xml` —— 控件样式聚合
- `apktool\res\color\*.xml` —— 状态色（selector）

用 Grep 在 `apktool\res\values\` 里搜 `coui` / `color_primary` / `oplus` / `corner` / `radius` 定位候选。

### 6.4 提取 framework / overlay 资源（需 root）

framework 级 COUI token 在 RRO overlay 或 framework-res：

```powershell
# 列出 oplus overlay，挑目标
adb shell cmd overlay list | Select-String -Pattern "oplus|coui|color"

# 例：拉 framework overlay apk（路径用 pm path 或 dumpsys 查）
$Remote = "/system/.../com.oplus.framework.res.overlay.apk"   # 用 adb shell pm path <overlay包名> 确认真实路径
adb shell su -c "cp $Remote /data/local/tmp/o.apk"
adb pull /data/local/tmp/o.apk "D:\AndroidStudioProjects\jadx\work\oplus_framework_overlay\input\o.apk"
adb shell su -c "rm /data/local/tmp/o.apk"
```

### 6.5 运行时实测（交叉验证，最可信）

静态资源可能被主题 / 动态取色覆盖。拿不准时直接读运行时值：

```powershell
# 当前强调色 / 主题相关 settings（key 名可能随版本变，先 grep 再读）
adb shell settings list system   | Select-String -Pattern "theme|color|accent"
adb shell settings list secure   | Select-String -Pattern "theme|color|monet|accent"

# 截图肉眼/取色校准（截当前 ColorOS 界面）
adb exec-out screencap -p > D:\AndroidStudioProjects\jadx\work\_shots\settings.png
```

截图配合放大取色，是校准「这个蓝到底是哪个蓝」最直接的手段。

### 6.6 逆向时至少记录（写进 notes.md）

- 目标包名 + 版本（`adb shell dumpsys package <pkg> | grep versionName`）
- 取到的 token：资源名 → 值（亮 / 暗）
- 来源文件路径（`apktool\res\values\colors.xml` 等）
- 该 token 在 Miuix 里对应哪个色槽 / 哪个 `Defaults`（落点见 §7）
- 是静态资源值，还是运行时实测值（后者更可信）
- 未确认 / 待校准项

## 7. token → 代码落点映射

提取到 token 后，按下表落到代码（**只改值，不重命名**）：

| COUI token 类型 | Miuix 落点 | 备注 |
| :--- | :--- | :--- |
| 主色 / 强调色 | `Colors.kt` → `primary` / `primaryVariant` / `primaryContainer`（light + dark 两处） | 当前 `#3482FF`，替换为 ColorOS 主色 |
| 语义色（错误等） | `Colors.kt` → `error` / `errorContainer` 等 | light + dark 都要改 |
| 背景 / 表面层级 | `Colors.kt` → `background` / `surface` / `surfaceContainer*` | ColorOS 卡片层级感强，注意 container 梯度 |
| 文字色 | `Colors.kt` → `onBackground` / `onSurface*` | |
| 全局字号 / 字重 | `TextStyles.kt` → `Main` / `Body*` / `Title*` 等私有 `val` | |
| 控件圆角 | 对应组件的 `XxxDefaults.CornerRadius`（如 `ButtonDefaults.CornerRadius`） | 配合 squircle |
| 控件尺寸 / 内边距 | 对应组件的 `XxxDefaults.MinWidth/MinHeight/InsideMargin` | |
| squircle 平滑度 | `miuix-squircle/` 模块参数 | 全局连续曲率 |
| 单控件配色 | 该组件 `XxxDefaults.xxxColors()` 默认引用的色槽 | 不在组件体内写死颜色 |

改完同一改动要顺带检查（见 `CLAUDE.md`「Modifying a Component」）：

1. 文档 `docs/components/` 与 `docs/zh_CN/components/`
2. 文档 demo `docs/demo/`
3. 示例 app `example/shared/src/commonMain/kotlin/component/`

## 8. 默认执行顺序

1. 明确目标：要改哪个视觉面（全局主色？某控件？字体梯度？）
2. 看 Miuix 现状：定位 §4 的抓手文件 / 对应 `Defaults`，确认当前值与取色路径
3. 判定是否需要逆向（§5）：需要就进 §6 取证（优先 Apktool 解 `res/`，必要时运行时实测）
4. 在 `notes.md` 记录 token → 落点结论（§6.6）
5. 改代码（§7，只改值不重命名）
6. 同步文档 / demo / 示例 app（§7 末）
7. 最小自检（§9）

## 9. 最小自检清单

- 改的是 `theme/` 默认值或 `Defaults`，没有在组件体里硬编码 `Color(0x...)`
- light 和 dark **两套**色值都改了（只改一套是最常见遗漏）
- token 有真实来源（资源路径或运行时实测），不是凭空写的
- 命名未被擅自改动（换肤阶段不重命名主题类 / 色槽）
- 关联文档 / demo / 示例 app 已同步（若改了组件 API 或默认值）
- 构建自检默认直接执行：
  - 快速：`./gradlew compileKotlinDesktop`
  - 完整：`./gradlew assemble`
  - 格式：先 `./gradlew spotlessCheck`，有违规才 `./gradlew spotlessApply`
- 至少在一个平台（Android 或 Desktop）肉眼确认观感更接近 ColorOS

## 10. 默认完成标准

当用户说「做 COUI 换肤 / 改成 ColorOS 风格」时，默认完成标准不是「分析完」，而是：

1. token 已有真实 ColorOS 来源（逆向资源或运行时实测）
2. 已落到 `theme/` 默认值或对应 `Defaults`，light/dark 双套
3. 编译通过，至少一个平台跑通并肉眼校准
4. 关联文档 / demo / 示例已同步（若涉及）
5. 关键来源与未校准项已在回复里说明

不要用泛泛「已完成」收尾；说明取到的 token 来源、改了哪些落点、还有哪些待校准。

## 11. 共享约束

- 沿用 Miuix 现有架构与命名，不发明新的模块结构 / 主题机制（换肤阶段尤其如此）。
- 引用真实路径与真实包名，不给泛泛 Android / Compose 建议。
- 设计 token 不臆造，必须可溯源到 ColorOS（资源文件路径或运行时实测）。
- 静态资源与运行时值冲突时，以运行时实测为准（动态取色 / 主题会覆盖静态默认）。
- 逆向工作区统一放 `D:\AndroidStudioProjects\jadx\work\<package_name>\`，结论写进该目录 `notes.md`。
- 写 / 改组件仍遵守 `CLAUDE.md` 的 API 约定、性能约束、`@Immutable`/`@Stable` 规则。
- 与用户用中文交流、用中文输出 Plan；生成的代码（含注释 / KDoc）保持英文。
