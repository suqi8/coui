# SideNavigationBar

`SideNavigationBar` 是面向大屏的响应式侧边导航抽屉，移植自 COUI `COUISideNavigationBar`（一个 `DrawerLayout`）。它按布局尺寸决定：把抽屉停靠在内容旁边并让内容让出空间，或像模态抽屉那样带遮罩滑到内容上方。

与 [NavigationRail](./navigationrail)（固定 72dp 的图标导航列）不同，`SideNavigationBar` 是抽屉容器：抽屉内可放任意内容，通常是页面列表。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=sideNavigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SideNavigationBar
import io.github.suqi8.coui.kmp.basic.rememberSideNavigationBarState
```

## 基本用法

```kotlin
val state = rememberSideNavigationBarState(initialOpen = true)
val scope = rememberCoroutineScope()

SideNavigationBar(
    state = state,
    drawerContent = { PageList() },
) {
    Column {
        IconButton(onClick = { scope.launch { state.toggle() } }) {
            Icon(COUIIcons.Basic.Sidebar, contentDescription = "Navigation")
        }
        PageContent()
    }
}
```

## 抽屉模式

模式由导航栏尺寸所属屏幕档位（`COUIResponsiveUtils`：小屏 = 1、中屏 = 2、大屏 = 3）与内容权重（1，`parentChildHierarchy` 时为 2）比较得出：

| 模式    | 条件                                  | 抽屉宽度                        | 内容                                 |
| ------- | ------------------------------------- | ------------------------------- | ------------------------------------ |
| Docked  | 窗口权重 > 内容权重                   | `min(280dp, width × 0.382)`     | 在抽屉旁收窄，无遮罩                 |
| Overlay | 其余情况（如宽度不足 600dp）          | 300dp                           | 被抽屉和 `coui_color_mask` 遮罩覆盖；点击遮罩（抬起点与按下点的距离在触摸阈值内）收起抽屉 |

两种模式下抽屉末端边缘都跟随一条 0.33dp 的 `couiColorDivider` 分割线。`state.mode` 暴露当前模式，内容可据此只在覆盖模式下选中后收起抽屉。

尺寸档位变化时：用户未在停靠模式下打开的覆盖抽屉会收起；停靠抽屉会恢复用户上次在停靠模式下选择的开合状态（`mImplicitDrawerState`）。

抽屉移动方式与 `DrawerLayout` 归位一致：完整移动 512ms（`ViewDragHelper`：`(distance / range + 1) × 256ms`，上限 600ms），使用五次方缓出插值器。与 COUI 一致，滑动手势被锁定，通过状态开合抽屉。

## 编辑模式

`editMode = true`（`setIsInEditState`）时，停靠抽屉旁的内容以 300ms、系统默认先加速后减速曲线淡入遮罩并不再接收触摸；退出编辑模式或中途改变时，淡变从当前位置反向播放。与遮罩层一样，编辑遮罩随抽屉打开程度缩放。覆盖模式下编辑模式会让遮罩点击不再收起抽屉。`editModeMask = false`（`setHandlerEditModeMask`）时不绘制遮罩。

## 属性

### SideNavigationBar 属性

| 属性名               | 类型                   | 说明                                           | 默认值                                 | 必须 |
| -------------------- | ---------------------- | ---------------------------------------------- | -------------------------------------- | ---- |
| drawerContent        | @Composable () -> Unit | 抽屉内容                                       | -                                      | 是   |
| modifier             | Modifier               | 应用于导航栏的修饰符                           | Modifier                               | 否   |
| state                | SideNavigationBarState | 抽屉状态                                       | rememberSideNavigationBarState()       | 否   |
| parentChildHierarchy | Boolean                | 内容本身是列表-详情分栏（仅大屏停靠）          | false                                  | 否   |
| editMode             | Boolean                | 编辑状态：遮罩停靠模式下的内容                 | false                                  | 否   |
| editModeMask         | Boolean                | 编辑模式是否绘制遮罩                           | true                                   | 否   |
| drawerColor          | Color                  | 抽屉背景                                       | colorScheme.backgroundWithCard                    | 否   |
| maskColor            | Color                  | 遮罩 / 编辑遮罩颜色                            | SideNavigationBarDefaults.maskColor    | 否   |
| dividerColor         | Color                  | 抽屉分割线颜色                                 | DividerDefaults.DividerColor           | 否   |
| content              | @Composable () -> Unit | 主内容                                         | -                                      | 是   |

### SideNavigationBarState

| 成员         | 类型                  | 说明                               |
| ------------ | --------------------- | ---------------------------------- |
| isOpen       | Boolean               | 抽屉是否已打开或正在打开           |
| mode         | SideNavigationBarMode | 当前模式（`Overlay` / `Docked`）   |
| progress     | Float                 | 抽屉偏移，0 到 1                   |
| open()       | suspend               | 打开抽屉                           |
| close()      | suspend               | 收起抽屉                           |
| toggle()     | suspend               | 切换抽屉                           |
| snapTo(open) | suspend               | 无动画地打开或收起                 |

### SideNavigationBarDefaults 对象

| 名称                         | 类型    | 说明                                         | 值                       |
| ---------------------------- | ------- | -------------------------------------------- | ------------------------ |
| OverlayDrawerWidth           | Dp      | `coui_side_navigation_bar_min_width`         | 300.dp                   |
| DockedDrawerMaxWidth         | Dp      | `coui_side_navigation_bar_max_width`         | 280.dp                   |
| DockedDrawerWidthFraction    | Float   | 停靠时占宽度的比例                           | 0.382                    |
| DividerThickness             | Dp      | `coui_side_navigation_drawer_divider_width`  | 0.33.dp                  |
| EDIT_MASK_DURATION_MILLIS    | Int     | 编辑遮罩淡入时长                             | 300                      |
| EditMaskEasing               | Easing  | 编辑遮罩曲线：`AccelerateDecelerateInterpolator`，`cos((t + 1)π) / 2 + 0.5` | - |
| SettleEasing                 | Easing  | `ViewDragHelper` 插值器 `(t - 1)^5 + 1`      | -                        |
| maskColor                    | Color   | `coui_color_mask`（`colorScheme.mask`） | #33000000 / #99000000 |
| drawerMode(width, height, parentChildHierarchy) | SideNavigationBarMode | 某尺寸下的模式 | -          |
| drawerWidth(width, height, mode) | Dp  | 某尺寸下的抽屉宽度                           | -                        |

## 与 COUI 的差异

- 停靠模式下 COUI 把内容收窄交给应用（通过 `onDrawerSlide`）；`SideNavigationBar` 自行为抽屉让出空间。
- 未移植 `COUISideNavigationBar` 的悬停 / 鼠标事件分发和无障碍层级调整。
