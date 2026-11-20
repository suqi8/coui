# SpringChain

`SpringChain` 是 ColorOS `COUIGridSpringChain` / `COUISpringChain` 的边缘弹簧链：列表被拖过首项或末项时，各行从该边缘向外散开；松手后一串弹簧依次把它们拉回，每一行追随相邻的一行。它不是组件，而是一组用于 `LazyColumn` / `LazyRow` / 懒加载网格的修饰符。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=springChain" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.effect.rememberSpringChainState
import io.github.suqi8.coui.kmp.effect.springChain
import io.github.suqi8.coui.kmp.effect.springChainItem
```

## 基本用法

```kotlin
val listState = rememberLazyListState()
val chainState = rememberSpringChainState(listState)

LazyColumn(
    state = listState,
    modifier = Modifier.springChain(chainState),
) {
    items(items.size) { index ->
        Card(modifier = Modifier.springChainItem(chainState, row = index)) {
            Text(items[index])
        }
    }
}
```

## 属性

### rememberSpringChainState 参数

有两个重载，第一个参数分别为 `LazyListState` 或 `LazyGridState`。

| 参数名             | 类型                        | 说明                                                       | 默认值                                 | 是否必须 |
| ------------------ | --------------------------- | ---------------------------------------------------------- | -------------------------------------- | -------- |
| lazyListState / lazyGridState | LazyListState / LazyGridState | 弹簧链跟随的列表或网格状态                    | -                                      | 是       |
| mainTension        | Float                       | 被拉边缘处控制弹簧的 Origami 张力                          | SpringChainDefaults.MainTension        | 否       |
| mainFriction       | Float                       | 控制弹簧的 Origami 摩擦                                    | SpringChainDefaults.MainFriction       | 否       |
| attachmentTension  | Float                       | 跟随弹簧的 Origami 张力                                    | SpringChainDefaults.AttachmentTension  | 否       |
| attachmentFriction | Float                       | 跟随弹簧的 Origami 摩擦                                    | SpringChainDefaults.AttachmentFriction | 否       |
| maxRows            | Int                         | 从被拉边缘起参与移动的行数                                 | SpringChainDefaults.MaxRows            | 否       |
| transCalculator    | SpringChainTransCalculator  | 每个链序号的位移步长                                       | SpringChainDefaults.TransCalculator    | 否       |
| accumulate         | Boolean                     | 某行位移是否为其序号以内各步长之和（`enableAutoAcc`）      | true                                   | 否       |

### 修饰符

| 修饰符                                 | 作用对象        | 说明                                                                 |
| -------------------------------------- | --------------- | -------------------------------------------------------------------- |
| `Modifier.springChain(state)`          | 懒加载列表/网格 | 监听触摸与边缘处的拖动，并驱动回弹                                   |
| `Modifier.springChainItem(state, row)` | 每一项          | 位移该项；`row` 在列表中为下标，在网格中为行号（横向网格为列号），如 `index / columns` |

### SpringChainState

| 成员               | 类型           | 说明                                                       |
| ------------------ | -------------- | ---------------------------------------------------------- |
| translationOf(row) | (Int) -> Float | 某行当前位移（像素）；在 layer 或绘制块中读取会随每帧刷新  |
| isRunning          | Boolean        | 回弹弹簧是否在运动                                         |
| isVertical         | Boolean        | 列表是否纵向滚动                                           |

### SpringChainDefaults 对象

| 名称                   | 类型                       | 说明                                              | 值    |
| ---------------------- | -------------------------- | ------------------------------------------------- | ----- |
| MainTension            | Float                      | `COUIGridSpringChain.MAIN_TENSION`                | 150   |
| MainFriction           | Float                      | `COUIGridSpringChain.MAIN_FRICTION`               | 10    |
| AttachmentTension      | Float                      | `COUIGridSpringChain.ATTACHMENT_TENSION`          | 150   |
| AttachmentFriction     | Float                      | `COUIGridSpringChain.ATTACHMENT_FRICTION`         | 10    |
| BaseMainTension        | Float                      | 单独 `COUISpringChain` 的控制弹簧张力             | 40    |
| BaseMainFriction       | Float                      | 单独 `COUISpringChain` 的控制弹簧摩擦             | 6     |
| BaseAttachmentTension  | Float                      | 单独 `COUISpringChain` 的跟随弹簧张力             | 70    |
| BaseAttachmentFriction | Float                      | 单独 `COUISpringChain` 的跟随弹簧摩擦             | 10    |
| MaxRows                | Int                        | `COUIGridSpringChain.MAX_Y`                       | 50    |
| TransFactor            | Float                      | 每步占拉动距离的比例（`DefaultTransCalculator`）  | 0.1   |
| TransCalculator        | SpringChainTransCalculator | 每步均为 `distance x TransFactor`                 | -     |

Origami 数值按 Rebound 换算：张力 `(t - 30) x 3.62 + 194`，摩擦 `(f - 8) x 3 + 25`，因此 150 / 10 即刚度 628.4、摩擦 31。

### SpringChainTransCalculator

`fun interface SpringChainTransCalculator { fun getTrans(index: Int, distance: Float, edge: SpringChainEdge): Float }` 根据阻尼后的拉动距离 `distance` 返回链序号 `index`（被拉边缘处为 0）的步长。`SpringChainEdge` 为 `Start`（首行，`BACK_TO_TOP` / `BACK_TO_LEFT`）或 `End`（末行，`BACK_TO_BOTTOM` / `BACK_TO_RIGHT`）。

## 行为

- **拉动**：只有列表已无法继续滚动时的触摸拖动才会拉动弹簧链。每次移动把阻尼距离与手指位移之差缩小 `1 - |distance| / (2 x 视口)`（`getFormalDistance`），链序号为 `c` 的行位移为步长 `0..c` 之和：默认计算器下边缘行移动拉动距离的 10%，下一行 20%，依此类推。与 COUI 一致，末端边缘比起始边缘多算一步。`COUIGridSpringChainScrollView` 同时把每次移动交给滚动视图，所以同一次拉动还会让整个列表在展开的各行之下过度滚动：位移为移动量的 `0.4 * (1 - |过度滚动| / 范围)`，范围为窗口的 0.3（`setCustomOverScrollDistFactor(0.3f)`），不触发边缘振动（`setEnableVibrator(false)`），松手后按 `SpringOverScroller` 弹簧回弹。拉动不会滚动外层列表。
- **回弹**：松手后边缘处的控制弹簧回到 0，其余弹簧各自追随相邻弹簧，形成错落跟随。弹簧为 Rebound 弹簧，按其固定 1ms 的 RK4 步长积分（单帧时长上限 64ms），在 0.1px 内静止。
- **打断**：回弹过程中触摸列表会把各行冻结在原处；抬起手指后从该处继续回弹。
- 惯性滑动、鼠标滚轮或鼠标拖动到达边缘时弹簧链保持静止，因为原实现只读取触摸事件。
- 每帧计算只使用预分配数组，不产生分配；各项的 layer 重绘而不触发重组。
- 未移植：单项的 `skipSpringChainCalc` 标记、`ISpringUpdateListener` 回调（请用 `translationOf`），以及多指 `inheritDistance` 中除「以当前距离重新开始拉动」之外的部分。

## 进阶用法

### 网格

```kotlin
val gridState = rememberLazyGridState()
val chainState = rememberSpringChainState(gridState)

LazyVerticalGrid(
    columns = GridCells.Fixed(4),
    state = gridState,
    modifier = Modifier.springChain(chainState),
) {
    items(24) { index ->
        Card(modifier = Modifier.springChainItem(chainState, row = index / 4)) { /* ... */ }
    }
}
```

### 自定义步长

```kotlin
val chainState = rememberSpringChainState(
    lazyListState = listState,
    transCalculator = { index, distance, _ -> distance * 0.06f },
    mainTension = SpringChainDefaults.BaseMainTension,
    mainFriction = SpringChainDefaults.BaseMainFriction,
)
```
