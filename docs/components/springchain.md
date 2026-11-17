# SpringChain

`SpringChain` is the ColorOS edge spring chain of `COUIGridSpringChain` / `COUISpringChain`: when a list is dragged past its first or last item, the rows fan out from that edge, and on release a chain of springs pulls them back one after another, each row chasing its neighbour. It is a pair of modifiers for `LazyColumn` / `LazyRow` / lazy grids rather than a component.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=springChain" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.effect.rememberSpringChainState
import io.github.suqi8.coui.kmp.effect.springChain
import io.github.suqi8.coui.kmp.effect.springChainItem
```

## Basic Usage

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

## Properties

### rememberSpringChainState Parameters

There are two overloads, taking a `LazyListState` or a `LazyGridState` as the first parameter.

| Parameter Name     | Type                        | Description                                                                  | Default Value                          | Required |
| ------------------ | --------------------------- | ---------------------------------------------------------------------------- | -------------------------------------- | -------- |
| lazyListState / lazyGridState | LazyListState / LazyGridState | State of the list or grid the chain follows                    | -                                      | Yes      |
| mainTension        | Float                       | Origami tension of the control spring at the pulled edge                     | SpringChainDefaults.MainTension        | No       |
| mainFriction       | Float                       | Origami friction of the control spring                                       | SpringChainDefaults.MainFriction       | No       |
| attachmentTension  | Float                       | Origami tension of the springs that follow it                                | SpringChainDefaults.AttachmentTension  | No       |
| attachmentFriction | Float                       | Origami friction of the springs that follow it                               | SpringChainDefaults.AttachmentFriction | No       |
| maxRows            | Int                         | Number of rows from the pulled edge that move                                | SpringChainDefaults.MaxRows            | No       |
| transCalculator    | SpringChainTransCalculator  | Translation step per chain index                                             | SpringChainDefaults.TransCalculator    | No       |
| accumulate         | Boolean                     | Whether a row moves by the sum of the steps up to its index (`enableAutoAcc`) | true                                  | No       |

### Modifiers

| Modifier                               | Applied To          | Description                                                                 |
| -------------------------------------- | ------------------- | --------------------------------------------------------------------------- |
| `Modifier.springChain(state)`          | The lazy list/grid  | Watches touches and the drag at the edges, and runs the rebound             |
| `Modifier.springChainItem(state, row)` | Every item          | Translates the item; `row` is the index in a list, the row (column for a horizontal grid) in a grid, e.g. `index / columns` |

### SpringChainState

| Member              | Type                 | Description                                                             |
| ------------------- | -------------------- | ----------------------------------------------------------------------- |
| translationOf(row)  | (Int) -> Float       | Current translation of a row in pixels; reading it in a layer or draw block follows every chain frame |
| isRunning           | Boolean              | Whether the rebound springs are moving                                  |
| isVertical          | Boolean              | Whether the list scrolls vertically                                     |

### SpringChainDefaults Object

| Name                   | Type                       | Description                                                       | Value |
| ---------------------- | -------------------------- | ----------------------------------------------------------------- | ----- |
| MainTension            | Float                      | `COUIGridSpringChain.MAIN_TENSION`                                | 150   |
| MainFriction           | Float                      | `COUIGridSpringChain.MAIN_FRICTION`                               | 10    |
| AttachmentTension      | Float                      | `COUIGridSpringChain.ATTACHMENT_TENSION`                          | 150   |
| AttachmentFriction     | Float                      | `COUIGridSpringChain.ATTACHMENT_FRICTION`                         | 10    |
| BaseMainTension        | Float                      | Control tension of a bare `COUISpringChain`                       | 40    |
| BaseMainFriction       | Float                      | Control friction of a bare `COUISpringChain`                      | 6     |
| BaseAttachmentTension  | Float                      | Attachment tension of a bare `COUISpringChain`                    | 70    |
| BaseAttachmentFriction | Float                      | Attachment friction of a bare `COUISpringChain`                   | 10    |
| MaxRows                | Int                        | `COUIGridSpringChain.MAX_Y`                                       | 50    |
| TransFactor            | Float                      | Share of the pull per step (`DefaultTransCalculator`)             | 0.1   |
| TransCalculator        | SpringChainTransCalculator | `distance x TransFactor` for every step                           | -     |

Origami values are converted as in Rebound: tension `(t - 30) x 3.62 + 194`, friction `(f - 8) x 3 + 25`, so 150 / 10 is a stiffness of 628.4 with a friction of 31.

### SpringChainTransCalculator

`fun interface SpringChainTransCalculator { fun getTrans(index: Int, distance: Float, edge: SpringChainEdge): Float }` returns the step of chain index `index` (0 at the pulled edge) for the damped pull `distance`. `SpringChainEdge` is `Start` (first row, `BACK_TO_TOP` / `BACK_TO_LEFT`) or `End` (last row, `BACK_TO_BOTTOM` / `BACK_TO_RIGHT`).

## Behavior

- **Pull**: only a touch drag that the list can no longer scroll pulls the chain. Each move closes `1 - |distance| / (2 x viewport)` of the gap between the damped distance and the finger travel (`getFormalDistance`), and the row at chain index `c` moves by the sum of the steps `0..c`: with the default calculator the edge row moves 10% of the pull, the next 20%, and so on. As in COUI, the end edge counts one step more than the start edge.
- **Rebound**: on release the control spring at the edge returns to 0 while every other spring chases its neighbour, which gives the staggered follow. The springs are Rebound springs integrated with its fixed 1ms RK4 step (frame delta capped at 64ms) and come to rest within 0.1px.
- **Interrupt**: touching the list while it rebounds freezes the rows in place; they rebound from there when the finger lifts.
- Flings and mouse wheels that reach the edge leave the chain at rest, as the original only reads touch events.
- The per-frame work runs on preallocated arrays and allocates nothing; item layers redraw without recomposition.
- Not ported: the per-item `skipSpringChainCalc` flag, the `ISpringUpdateListener` callback (use `translationOf`), and the multi-finger `inheritDistance` beyond restarting the pull at the current distance.

## Advanced Usage

### Grid

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

### Custom Step

```kotlin
val chainState = rememberSpringChainState(
    lazyListState = listState,
    transCalculator = { index, distance, _ -> distance * 0.06f },
    mainTension = SpringChainDefaults.BaseMainTension,
    mainFriction = SpringChainDefaults.BaseMainFriction,
)
```
