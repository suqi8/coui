# 升级指南

## 1.0.0 → 1.1.0

把你依赖的每个 COUI 坐标都改成新版本：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
        }
    }
}
```

这一版按 ColorOS 16 重新校准了大部分组件，一部分依据反编译资源，一部分靠真机实测。其中两处纠正的是从 Miuix 继承下来、一直没核对过的值。**即使你什么都没改，观感也会变化**，升级后请过一遍自己的界面。

### 一处编译不兼容

`InfiniteProgressIndicator` 去掉了 `orbitingDotSize` 参数，以及对应的 `ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize`。这个指示器原来是「一个环 + 一个绕着环跑的小圆点」，那是 Miuix 的设计，COUI 里并不存在——ColorOS 画的是一段裸弧，弧长随旋转伸缩。既然没有点了，也就没有点的尺寸可设。

```kotlin
// 改之前
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp, orbitingDotSize = 4.dp)

// 改之后 —— 去掉这个参数
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp)
```

同一段弧也可以直接用 `RotatingProgressIndicator`，它带 COUI 的两档尺寸（16dp 和 26dp）。

### 文字变高了

`COUITheme.textStyles` 的每一项现在都带上了对应 COUI text appearance 声明的行距倍数，所以文字占的纵向空间比 1.0.0 多。`headline1` 的字号也从 17sp 降到了 **16sp**：真机上量到列表项标题是 21.71dp，按 COUI 的倍数换算正好是 16sp，而不是 Miuix 用的 17sp。

如果你按某个具体字符串的尺寸写死过容器高度，请重新核一下。如果你之前为了补偿「太挤」自己加过 `lineHeight`，请去掉——现在会叠加两次行距。

### 列表行与弹窗有位移

- 行高现在取决于它在卡片组里的位置：独立单行比中间行高 4dp，首行或末行在圆角那一侧高 2dp。不传 `cardListPosition` 则行为不变，所有 preference 组件都新增了这个参数。
- 弹窗按钮栏在文案放不下时会**竖排**，而不是硬挤。竖排档有自己的一套尺寸，而且按钮顺序会反转——取消从最左移到最下。
- 弹窗按钮栏里的按钮按下时不再缩放，按压色铺满整格且是方角而非胶囊。`Button` 和 `TextButton` 为此新增了 `pressScaleEnabled`，默认 `true`，所以你自己的按钮不受影响。

### 下拉菜单的交互变了

扁平下拉菜单按 COUI 自己的手势模型重建过，所以变的不只是像素：

- 菜单短到不需要滚动时，**按住拖动会让高亮跟着手指在行间移动**，松手选中的是手指当下所在那行，而不是最初按下那行。每跨过一行会触发一次震动。菜单一旦可滚动，拖动就变成滚动。
- 从 preference 行打开的下拉菜单会**出现在你点击的位置**，而不是居中对齐那一行。
- 菜单不再压暗背后的内容。`OverlayListPopup`、`WindowListPopup` 以及两个级联弹窗的 `enableWindowDim` 现在默认 `false`，想保留旧行为就显式传 `true`。
- 菜单项之间有了细分割线，按住某项时它相邻的两条会淡出。

### 本版新增

`DialogButtonBar`（配合 `DialogButtonBarAction`）替代手写的弹窗按钮行，竖排逻辑由它处理。下拉菜单项新增了放尾部角标的 `hint` 插槽、用于危险操作的 `alert` 标记，以及 `DropdownEntry.title` 用来做菜单内分组标题。

## 更早的版本

如果你是从 Miuix 迁移而不是从旧版 COUI 升级，请看[从 Miuix 迁移](./migration.md)。
