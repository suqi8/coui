# 从 COUI 1.1.0 迁移到 COUI 17（2.0.0）

本文根据 OShin 的实际迁移整理：OShin 用到了 50 个文件、73 个 COUI 符号，迁移分支是 `coui17-migration`。全文分三部分：怎么引入私有构建，哪些默认值变了会出问题，以及怎样把应用自己手写的部分换成 COUI 17 组件。

---

## 1. 引入私有构建

COUI 17 走 `coui17` 分支，**不发布到 Maven Central，也不推送到公开仓库 `suqi8/coui`**，构建产物只随应用自己的私有仓库分发。

### 1.1 发布

在 COUI 仓库（`coui17` 分支）执行：

```bash
T=""
for m in coui-core coui-ui coui-preference coui-shader coui-blur coui-squircle coui-icons coui-navigation3-ui coui-nav; do
  T="$T :$m:publishKotlinMultiplatformPublicationToLocalRepository :$m:publishAndroidPublicationToLocalRepository"
done
./gradlew $T -Palpha=NN
```

- **只发布两种 publication**：`kotlinMultiplatform` 提供根 `.module` 元数据，`android` 提供 AAR。Android 应用只解析这两份，其他平台的 variant 文件不需要，体积约 12MB。整套 9 个模块都要发布：`coui-ui` 以 `api` 依赖 `coui-core`/`coui-squircle`，`coui-blur` 以 `api` 依赖 `coui-shader`，缺一个就解析失败。
- **版本号用 `2.0.0-alphaNN`**（`BuildConfig.LIBRARY_VERSION = "2.0.0"`），必须高于 Central 上的 `1.1.0`。如果私有版本更低，一旦某条依赖链从 Central 带进 1.1.0，Gradle 会按冲突解析选中 1.1.0。
- **不要带 `-Prelease` 发布到 mavenLocal**：那样版本号就是 `1.1.0`，会遮住 Central 的正式版，事后分不清用的是哪一份。
- 本机发布约 13 分钟，期间编译全部 KMP target 的元数据。

### 1.2 应用侧

把各模块 `build/repository/local/.` 合并复制到应用仓库的 `third_party/coui-maven/`，然后在 `settings.gradle.kts` 里声明：

```kotlin
dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { maven(rootDir.resolve("third_party/coui-maven")) }
            filter { includeGroup("io.github.suqi8.coui.kmp") }
        }
        google()
        mavenCentral()
        // ...
    }
}
```

- `exclusiveContent` 把这个 group 锁定在本目录：既不会去 Central 探测，也不会被其他仓库同名的 artifact 抢先解析。
- 产物提交进私有仓库后，CI（GitHub Actions）不用任何凭据就能构建。
- 被否掉的做法：
  - **composite build**：COUI 用 Kotlin 2.4.10 / AGP 9.3.1，OShin 用 2.3.21 / 9.2.1，两边插件冲突。
  - **GitHub Packages / Central**：会公开。
- **Kotlin 版本**：用 Kotlin 2.3 编译、依赖 Kotlin 2.4 编出的库是可以的，编译器能读高一个小版本的元数据，1.1.0 就是这样被消费的。

---

## 2. 能编译、但运行或视觉会出错的地方

COUI 17 **没有删改公开签名**，只新增了带默认值的参数，所以迁移后能直接编译。真正的风险在于**默认值变了**，尤其是应用把 COUI 的 Defaults 当常量引用的地方。

| 引用 | 1.1.0 | COUI 17 | 后果 |
|---|---|---|---|
| `ButtonDefaults.CornerRadius` | 22.dp | **`Dp.Unspecified`**（意思是按半高画胶囊） | 传给自写的 `ContinuousRoundedRectangle` / `RoundedCornerShape` 时半径是 NaN |
| `ButtonDefaults.MinWidth` | 58.dp | **132.dp** | 自写按钮被撑宽 |
| `ButtonDefaults.InsideMargin` | 竖向 0 | 竖向 8dp | 高度变化 |
| `CardDefaults.CornerRadius` | 12.dp | **24.dp**（weight 2.5） | 包 45dp 图标的 Card 几乎被裁成圆形 |

**检查方法**：在应用里搜 `Defaults\.\w+`，凡是作为自写组件参数默认值的引用都要过一遍。要保持旧外观就写死旧值，要跟随 COUI 17 就改用 COUI 组件本身。

OShin 的两处：
- `MainAbout` 的本地 `Button`：写死成 22/58/44dp 加左右 12dp 内边距。
- `AppList` 包图标的 `Card`：显式传 `cornerRadius = 13.dp`，与图标的彩色阴影一致。

---

## 3. 材质模糊：怎么接线

COUI 17 的弹窗、底部面板、下拉菜单、悬浮底栏和顶栏凝光，都要模糊「后面的页面」。coui-ui 自己录不到页面，必须由应用提供一份**页面录制**（coui-blur 的 `LayerBackdrop`）。可以照抄 OShin 的 `ui/components/PageMaterial.kt`，它是 example `utils/PanelBlur.kt` 的精简版。

```kotlin
val pageBackdrop = rememberPageBackdrop()          // RuntimeShader 不可用时返回 null
Scaffold(topBar = { ... }) { padding ->
    Box(Modifier.layerBackdrop(pageBackdrop)) {    // 录制挂在「内容」容器上
        ProvidePageMaterial(pageBackdrop) {        // LocalPageBackdrop + LocalPopupMaterialBlur
            content(padding)
        }
    }
}
```

规则：
1. **录制不能包含采样它的组件**，否则会采样到自己。弹窗、`Overlay*` 面板和下拉菜单都渲染在**根 Scaffold 的 popup host** 里，不在录制范围内，所以可以安全使用页面录制。页内按钮则不行，`LocalMaterialBackdrop` 只能用在叠在录制之上的兄弟节点，例如悬浮底栏。
2. **根 Scaffold 是谁，就用谁的录制。** `OverlayDialog` 默认 `renderInRootScaffold = true`，会渲染到最外层 Scaffold。OShin 的三个主页面各有一个 `popupHost = {}` 的内层 Scaffold，它们的弹窗实际出现在 `MainScreen` 的根 Scaffold 上。因此 `ProvidePageMaterial` 放在 `MainScreen`、录制整个 pager。详情页没有外层 Scaffold，`FunPageScaffold` 自己就是根。
3. **CompositionLocal 在调用处读取**：弹窗和菜单读的是调用它们的那一处的 local，所以 provider 包住「会调用弹窗的内容」就够了，不必包住整个 Scaffold。COUI 会在弹窗和面板内部把这两个 local 重置为 null，所以面板里再打开的菜单是不透明的。
4. **`Window*` 版本（`WindowBottomSheet`/`WindowDialog`）在独立 window 里渲染，采样不到主窗口的录制。** 想要模糊就改用 `Overlay*`。OShin 的应用选择面板因此从 `WindowBottomSheet` 换成了 `OverlayBottomSheet`。
5. 录制回调里要先 `drawRect(surface)` 再 `drawContent()`，否则内容的透明区域会被模糊成透明黑。
6. COUI 原生的模糊是 opt-in 的；我们默认开启，只有不支持 RuntimeShader 时才退回不透明底色。

各组件的接法：

| 组件 | 写法 |
|---|---|
| 弹窗 | `OverlayDialog(backgroundModifier = Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.dialog(isDark)), backgroundColor = Transparent, materialStroke = true)`。打开 `materialStroke` 后，`DialogButtonBar` 会自动切成材质按钮 |
| 底部面板 | 同上，用 `MaterialBlurDefaults.bottomSheet`（400px） |
| 下拉菜单 / 列表弹出 | 只要提供了 `LocalPopupMaterialBlur` 就默认模糊，不需要改调用处 |
| 顶栏 | `TopAppBar(color = Transparent, showDivider = false, glow = { TopAppBarGlow(backdrop, scrollBehavior.glowScrollOffsetPx(), isDark, surface, enabled = true) })`。凝光滚过 30dp 完全显现；需要自定义显现进度时，把进度换算成 `AlphaScrollThreshold × progress` 传入（见 OShin 的关于页）。打开凝光后不再需要 `BlurredBar`/`textureBlur` 那一套 |
| 悬浮底栏 | `FloatingNavigationBar(color = Transparent, backgroundModifier = provider.materialBackdrop(FloatingNavigationBarDefaults.backdropSpec(isDark), capsuleShape))`，provider 来自 `MaterialBackdropProvider { spec, shape -> Modifier.couiViewBlur(...) }` |

---

## 4. 把手写部分换成 COUI 17 组件

### 4.1 弹窗按钮
- COUI 17 的 `DialogButtonBar` 要**铺满面板宽度**：单元格自带 16dp 边距、按钮间距 8dp、填充胶囊、没有分隔线。所以它不能放在带 24dp 内边距的内容里面。
  - OShin 的做法：给 `FunDialog` 加一个 `buttons` 槽，排在带边距的内容之后，自身不加边距。
- 排布规则，照搬 `COUIButtonBarLayout`：
  - **只有两个按钮且文案放得下时才横排**，其余情况一律竖排，竖排顺序是 neutral / positive / negative。
  - neutral 按钮用 error 色，适合放「清除」。
  - 主操作用 `DialogButtonBarAction(recommended = true)`，画成主色实心胶囊。
- 只有一个「确定」按钮时传 `negative = null`。

### 4.2 列表与卡片
- 卡片：24dp，Oplus 超椭圆 weight 2.5（`COUIRoundCorner.Normal.XL` + `Modifier.squircleSurface(color, radius, COUIRoundCorner.Normal.Weight)`）。分段卡片用四角版本的 `squircleSurface`，只在首尾给圆角。
- 列表行默认值跟随 `BasicComponentDefaults`：
  - 内边距 16/10dp
  - 标题颜色 `onBackground`
  - 摘要颜色 `onSurfaceSecondary`（`#8A000000`），原来是 `onSurfaceVariantSummary`
- 应用里手写的「摘要 / 次要文字」颜色也统一换成 `onSurfaceSecondary`。
- 分组小标题用 `SmallTitleDefaults.InsideMargin` + `onBackgroundVariant`。
- 分隔线不要再写 `thickness = 0.5.dp`：COUI 默认是 0.33dp，按整像素取整、至少 1px。0.5dp 在 3.5x 屏上会取整成 2px，比 COUI 粗。

### 4.3 开关行
- 不要再写 `BasicComponent + Switch(onCheckedChange = null)`。这样拿不到 COUI 17 的两项行为：
  - 按住开关时整行一起出按压态（`COUISwitchTouchEventDelegate`）；
  - 拇指可以拖动。
- 改用 coui-preference 的 `SwitchPreference`，触感也交给它：
  - 点开关、拖动、点整行都会触发开关震动（`COUISwitchPreference.onClick → setPerformFeedBack(true)`）；
  - 应用自己不要再补 haptic，否则点开关会震两次。

### 4.4 搜索栏
`outsideEndAction` 里的取消按钮改用 `CancelButton(text, onClick)`。它是跟随搜索框材质和展开动画的 40dp 胶囊，手写的 `Text + clickable` 会和玻璃胶囊搜索框对不上。

### 4.5 底部导航
- COUI 17 默认用悬浮底栏 `FloatingNavigationBar` / `FloatingNavigationBarItem`。
- 显隐交给它的 `visible` 参数（COUI 的弹簧动画），**不要再套 `AnimatedVisibility`**：外层的淡出或裁剪会切掉悬浮栏的阴影。
- OShin 保留了 kyant 液态玻璃底栏作为可选项，由「关闭底部栏玻璃效果」开关切换。

### 4.6 不需要改、但外观会变的部分
以下部分没有改动，迁移后外观会自动变化：
- 主色 #0080FF；正文 16sp，行高更高。
- `IconButton` 默认带 40dp 材质圆盘，不想要就传 `material = false`。
- 弹窗圆角 45dp，底部面板圆角 45dp。
- Switch 宽 44dp；Slider 布局高度多出 8dp。
- `TopTips` 单行时是胶囊；`TabRowWithContour` 没有阴影，分段宽度按标签宽度分配。

---

## 5. 迁移检查清单

1. 发布 `2.0.0-alphaNN`，复制到 `third_party/coui-maven`，修改版本号，编译。
2. 搜 `Defaults\.` 作为自写组件默认值的地方（§2）。
3. 给每个根 Scaffold 接上页面录制和 `ProvidePageMaterial`；顶栏改用凝光（§3）。
4. 手写的弹窗按钮行改成 `buttons` 槽 + `DialogButtonBar`，主操作标 `recommended`（§4.1）。
5. 卡片 24dp 超椭圆，摘要颜色、小标题、分隔线改用默认值（§4.2）。
6. 开关行换成 `SwitchPreference`，搜索取消换成 `CancelButton`，底栏换成悬浮底栏（§4.3–4.5）。
7. 编译，装到真机上逐页检查。

## 6. 这次迁移顺带修复的库问题

- `SwitchPreference` 点整行时没有震动，与 COUI 不一致。已修复（`fix: give SwitchPreference row clicks the switch haptic`），从 `2.0.0-alpha02` 起生效。
