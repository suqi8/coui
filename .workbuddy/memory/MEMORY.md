# COUI16 项目长期记忆

## 项目定位
- 本仓库 = Miuix 就地演进的 COUI（ColorOS 设计语言）Compose Multiplatform 组件库。
- 规范：`CLAUDE.md`（工程）+ `AGENT.md`（COUI 改造主线/逆向工作流）。
- 设计 token 必须可溯源到真实 ColorOS（资源路径或运行时实测），不臆造。

## 分支策略
- `main`：当前 COUI16（ColorOS 16 设计，蓝 `#0066FF`/`#247CFF`）。
- `coui17`：复刻 COUI17（ColorOS 17 流体设计/凝光视效，蓝 `#0080FF`/`#1A8CFF`），2026-09-28 启动。
- `coui-retheme`：历史换肤分支（已有两个陈旧 stash）。

## 逆向工作区
- `E:\AndroidStudioProjects\jadx\work\<package>\`（input/apktool/jadx/notes.md）。
- COUI16 取证：`com.oplus.uxdesign`、`com.coloros.alarmclock`、`com.android.settings`(16)。
- COUI17 取证：`coui17_settings`（设置 17.0.0）、`coui17_setup`（开机注册 17.0.237）。
- 工具：JADX `E:\jadx-1.5.0\bin\jadx.bat`、Apktool `E:\ApkTool\apktool.jar`（java -jar）。

## COUI 改造用户决策（贯穿）
- 未对齐 COUI 规范的内容不保留——对齐不到就去除，不私自保留/发明。
- 换肤阶段只改值、不重命名主题类/色槽；组件读 `COUITheme.colorScheme.*`，不硬编码 `Color(0x...)`。
- 字体专项：用户决策「字体暂不动」（除 TopBar 标题等明确迁移项）。

## 构建环境
- 必须走代理：`HTTP_PROXY/HTTPS_PROXY=http://127.0.0.1:7897`，gradle 加 `-Dhttp(s).proxyHost=127.0.0.1 -Dhttp(s).proxyPort=7897`。
- Gradle 缓存目录 `D:\DevCache\gradle\caches`；遇 transforms Hash null → 停 daemon + 清 `9.6.1/transforms` 与对应 `modules-2` 依赖目录。
- 自检：`./gradlew :<module>:compileKotlinDesktop spotlessCheck`（先 check，违规才 apply）。
- 提交前必须 `spotlessCheck` 通过；commit 格式 `<scope>: <summary>`（sentence case，≤72 字）。
