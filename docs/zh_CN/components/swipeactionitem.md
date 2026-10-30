# SwipeActionItem

`SwipeActionItem` 复刻 ColorOS 的侧滑列表项（`COUISlideView` + `COUISlideMenuItem` + `COUISlideDeleteAnimation`）：列表行向起始方向滑动，露出背后的操作按钮；可选的内置删除操作会播放 COUI 删除动画。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=swipeActionItem" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SwipeAction
import io.github.suqi8.coui.kmp.basic.SwipeActionItem
import io.github.suqi8.coui.kmp.basic.SwipeActionItemDefaults
import io.github.suqi8.coui.kmp.basic.rememberSwipeActionItemState
```

## 基本用法

内容会盖在操作按钮之上滑动，因此需要给内容设置不透明背景：

```kotlin
val state = rememberSwipeActionItemState()
val scope = rememberCoroutineScope()

SwipeActionItem(
    actions = listOf(SwipeAction(label = "Pin", color = COUITheme.colorScheme.primary)),
    onActionClick = { index -> scope.launch { state.close() } },
    state = state,
    onDelete = { rows.remove(row) },
) {
    BasicComponent(
        modifier = Modifier.background(COUITheme.colorScheme.surfaceContainer),
        title = row,
    )
}
```

## 删除

设置 `onDelete` 后，菜单末端会增加一个错误色底的垃圾桶操作（`COUISlideView.setDeleteEnable`）。点击后播放 `COUISlideDeleteAnimation`：红色底填满整行，同时内容滑出（330ms），行高收起（400ms，延迟 10ms）。收起结束时调用 `onDelete`，请在其中从数据里移除该行。请按每行的标识设置 key（`key(id) { ... }` 或懒加载列表的 item key），避免被删除行的状态转移到相邻行。

`fastDeleteEnabled = true` 时，越过菜单 48dp 松手，或越过 30dp 并快速甩动，也会删除该行（`COUISlideView.enableFastDelete`）。

## 从外部关闭

提升状态即可从外部关闭菜单，例如另一行展开时：

```kotlin
scope.launch { state.close() }
```

## 属性

### SwipeActionItem

| 属性名                   | 类型                    | 说明                                   | 默认值                                           | 是否必须 |
| ------------------------ | ----------------------- | -------------------------------------- | ------------------------------------------------ | -------- |
| actions                  | List\<SwipeAction\>     | 按从起始到末端的视觉顺序排列的操作     | -                                                | 是       |
| onActionClick            | (Int) -> Unit           | 以被点击操作的序号回调                 | -                                                | 是       |
| modifier                 | Modifier                | 应用于组件的修饰符                     | Modifier                                         | 否       |
| state                    | SwipeActionItemState    | 展开与删除状态                         | rememberSwipeActionItemState()                   | 否       |
| enabled                  | Boolean                 | 是否可以滑动                           | true                                             | 否       |
| onDelete                 | (() -> Unit)?           | 添加删除操作，删除动画结束后回调       | null                                             | 否       |
| fastDeleteEnabled        | Boolean                 | 是否允许越界滑动删除（需要 `onDelete`） | false                                           | 否       |
| deleteContentDescription | String?                 | 删除操作的无障碍描述                   | null                                             | 否       |
| actionMinWidth           | Dp                      | 每个操作的最小宽度                     | SwipeActionItemDefaults.ActionMinWidth           | 否       |
| colors                   | SwipeActionItemColors   | 菜单颜色                               | SwipeActionItemDefaults.swipeActionItemColors()  | 否       |
| textStyle                | TextStyle               | 操作文字样式                           | SwipeActionItemDefaults.textStyle()              | 否       |
| content                  | @Composable () -> Unit  | 盖在操作之上滑动的行内容               | -                                                | 是       |

### SwipeAction

| 属性名             | 类型          | 说明                                              | 默认值            |
| ------------------ | ------------- | ------------------------------------------------- | ----------------- |
| label              | String?       | 绘制在操作中央的文字                              | null              |
| icon               | ImageVector?  | 绘制在操作中央的图标                              | null              |
| color              | Color         | 自身底色；`Unspecified` 时使用 `colors.containerColor` | Color.Unspecified |
| contentDescription | String?       | 无障碍描述                                        | label             |

### SwipeActionItemState

| 成员         | 类型    | 说明                                                  |
| ------------ | ------- | ----------------------------------------------------- |
| revealOffset | Float   | 内容滑开的距离（像素）                                |
| isOpen       | Boolean | 菜单是否（部分）展开                                  |
| isDeleting   | Boolean | 删除动画是否在进行                                    |
| close()      | suspend | 按 `COUISlideView.shrink` 收起（每像素 3ms，最多 200ms） |
| open()       | suspend | 按松手弹簧展开                                        |
| snapClose()  | suspend | 立即回到收起的静止状态                                |

### SwipeActionItemDefaults

| 常量                | 类型 | 默认值 | COUI 来源                            |
| ------------------- | ---- | ------ | ------------------------------------ |
| ActionMinWidth      | Dp   | 80.dp  | coui_slideview_menuitem_width        |
| ActionTextPadding   | Dp   | 12.dp  | coui_slide_view_text_padding         |
| FastDeleteOverSlide | Dp   | 48.dp  | coui_slideview_over_slide_delete     |
| FastDeleteQuickSlop | Dp   | 30.dp  | coui_slideview_quick_delete          |

| 方法                    | 类型                  | 说明                                       |
| ----------------------- | --------------------- | ------------------------------------------ |
| textStyle()             | TextStyle             | body1，字号 15sp（coui_slide_view_text_size） |
| swipeActionItemColors() | SwipeActionItemColors | 创建菜单颜色                               |

### `swipeActionItemColors()` 工厂

| 参数           | 类型  | 默认值                          | COUI 来源                                   |
| -------------- | ----- | ------------------------------- | ------------------------------------------- |
| containerColor | Color | COUITheme.colorScheme.error     | itemBackgroundColor 默认 couiColorError     |
| contentColor   | Color | COUITheme.colorScheme.onError   | coui_slideview_textcolor #F9F9F9            |

## 行为

- 水平移动超过触摸阈值、且水平位移大于垂直位移的两倍时，行才接管拖动（`COUISlideView.onInterceptTouchEvent`）；以垂直为主的拖动继续滚动父列表。收起状态下朝结束方向的拖动交给父容器，因此可放在水平分页器中。
- 菜单范围内内容 1:1 跟手；越过菜单后按 `delta x (1 - min(越界 / (宽度 - 菜单宽), 1)) x 2 / 5` 阻尼，各操作随之拉开，第 `k` 个（从起始方向数）偏移 `k x 越界 / (数量 + 1)`。
- 松手时，速度超过 1000 px/s 的甩动决定展开或收起；否则露出超过一半菜单即展开。以阻尼 1、刚度 200 的弹簧归位。开启快速删除时只按一半菜单的规则判断。
- 文字宽于 80dp 时，操作宽度扩展为 `文字 + 2 x 12dp`。
- 菜单展开时点击内容会收起菜单。点击非删除操作后菜单保持展开，与 COUI 一致。
- RTL 布局下菜单位于左侧，行向右滑动。
