# Card

`Card` 是 COUI 中的基础容器组件，用于承载相关内容和操作。它提供了具有 COUI 风格的卡片容器，适用于信息展示、内容分组等场景。支持静态显示和交互式两种模式。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=card" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.utils.PressFeedbackType // 如果使用交互式卡片
```

## 基本用法

Card 组件可以用于包装和组织内容（静态卡片）：

```kotlin
Card {
    Text("这是卡片内容")
}
```

## 属性

### Card 属性


| 属性名            | 类型                               | 说明                     | 默认值                       | 是否必须 | 适用范围 |
| ----------------- | ---------------------------------- | ------------------------ | ---------------------------- | -------- | -------- |
| modifier          | Modifier                           | 应用于卡片的修饰符       | Modifier                     | 否       | 所有     |
| cornerRadius      | Dp                                 | 卡片圆角半径             | CardDefaults.CornerRadius    | 否       | 所有     |
| insideMargin      | PaddingValues                      | 卡片内部边距             | CardDefaults.InsideMargin    | 否       | 所有     |
| colors            | CardColors                         | 卡片颜色配置             | CardDefaults.defaultColors() | 否       | 所有     |
| shadow            | CardShadow?                        | 卡片外的阴影（COUI `COUIShadowCardView`），null 为无阴影 | null | 否 | 所有 |
| pressFeedbackType | PressFeedbackType                  | 按压反馈类型             | PressFeedbackType.Tint       | 否       | 交互式   |
| showIndication    | Boolean                            | 显示点击指示效果         | false                        | 否       | 交互式   |
| holdDownState     | Boolean                            | 卡片是否处于按下状态     | false                        | 否       | 交互式   |
| onClick           | (() -> Unit)?                      | 点击事件回调             | null                         | 否       | 交互式   |
| onLongPress       | (() -> Unit)?                      | 长按事件回调             | null                         | 否       | 交互式   |
| content           | @Composable ColumnScope.() -> Unit | 卡片内容区域的可组合函数 | -                            | 是       | 所有     |

::: warning 注意
部分属性仅在创建可交互的卡片时可用！
:::

### CardDefaults 对象

CardDefaults 对象提供了卡片组件的默认值和颜色配置。

#### 常量

| 常量名       | 类型          | 说明           | 默认值              |
| ------------ | ------------- | -------------- | ------------------- |
| CornerRadius | Dp            | 卡片的圆角半径：ColorOS 17 卡片列表圆角（`COUIRoundCorner.Normal.XL`，COUI `coui_round_corner_xl_radius_normal_16_1`） | 24.dp |
| InsideMargin | PaddingValues | 卡片的内部边距 | PaddingValues(0.dp) |
| ShadowSize   | Dp            | 默认阴影尺寸（`support_shadow_size_level_for_shadow_card`） | 12.dp |
| ShadowColor  | Color         | 默认阴影颜色，只取其 RGB | Color.Black |
| ShadowOffset | Dp            | 默认阴影沿角度方向的偏移（`couiShadowOffset` 缺省 7px） | 2.dp |

#### 方法

| 方法名          | 类型       | 说明           |
| --------------- | ---------- | -------------- |
| defaultColors() | CardColors | 卡片的默认颜色 |
| shadow()        | CardShadow | 创建 `CardShadow`；参数默认取上面的常量，角度 0，不隐藏任何一侧 |

### CardColors 类
| 属性名       | 类型  | 说明                                     |
| ------------ | ----- | ---------------------------------------- |
| color        | Color | 卡片的背景颜色                           |
| contentColor | Color | 卡片的内容颜色                           |
| pressedColor | Color | 按压时背景过渡到的颜色（Tint 反馈使用）  |

### CardShadow 类

对应 `COUIShadowCardView` 的阴影：一个 `MaterialShapeDrawable`，其兼容阴影从轮廓向外扩散 `0.75 x size`，透明度按阴影 RGB 的 68/255 -> 20/255 -> 0 渐变。这里改用拟合该渐变的高斯投影绘制（模糊半径 `0.62 x size`，透明度 136/255），贴合卡片自身的平滑圆角轮廓，并跟随可交互卡片的按压缩放。

| 属性名     | 类型    | 说明                                                        |
| ---------- | ------- | ----------------------------------------------------------- |
| size       | Dp      | 阴影尺寸（`couiShadowSize`），为 0 时不绘制                 |
| color      | Color   | 阴影颜色，忽略其透明度（`couiShadowColor`）                 |
| offset     | Dp      | 阴影沿 `angle` 方向的偏移（`couiShadowOffset`）             |
| angle      | Int     | 偏移方向（度），0 向下、90 向右（`couiShadowAngle`）        |
| hideLeft   | Boolean | 左边缘外不画阴影（`couiHideLeftShadow`）                    |
| hideTop    | Boolean | 上边缘外不画阴影（`couiHideTopShadow`）                     |
| hideRight  | Boolean | 右边缘外不画阴影（`couiHideRightShadow`）                   |
| hideBottom | Boolean | 下边缘外不画阴影（`couiHideBottomShadow`）                  |

### PressFeedbackType 选项

| 选项 | 说明                                                                  |
| ---- | --------------------------------------------------------------------- |
| None | 无按压反馈                                                            |
| Tint | 按压或保持按下时背景过渡到 `pressedColor`（COUI 卡片按压反馈，默认）  |
| Sink | 按压时按 COUI 17 触控缩放下沉：目标缩放（0.85–0.97）由卡片面积与长宽比决定 |
| Tilt | 按压时根据触摸位置倾斜                                                |

## 进阶用法

### 自定义样式卡片

```kotlin
Card(
    cornerRadius = 8.dp,
    insideMargin = PaddingValues(16.dp),
    colors = CardDefaults.defaultColors(
        color = COUITheme.colorScheme.primary
    ),
) {
    Text("自定义样式卡片")
}
```

### 内容丰富的卡片

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    insideMargin = PaddingValues(16.dp)
) {
    Text(
        text = "卡片标题",
        style = COUITheme.textStyles.displayXS
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "这是卡片的详细内容描述，可以包含多行文本信息。"
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            text = "取消",
            onClick = { /* 处理取消事件 */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            text = "确定",
            colors = ButtonDefaults.textButtonColorsPrimary(), // 使用主题颜色
            onClick = { /* 处理确认事件 */ }
        )
    }
}
```

### 带阴影的卡片

```kotlin
Card(
    insideMargin = PaddingValues(16.dp),
    shadow = CardDefaults.shadow(), // 12dp 阴影，向下偏移 2dp
) {
    Text("带阴影的卡片")
}

// 与上方元素相接的卡片：上边缘外不画阴影
Card(shadow = CardDefaults.shadow(hideTop = true)) { /* ... */ }
```

阴影画在卡片边界之外，请留出空间（padding），并避免被裁剪的父布局截断。`COUIShadowCardView` 的分角圆角与描边未移植；隐藏一侧阴影时，相邻的圆角保持圆角形状。

### 列表中的卡片

```kotlin
LazyColumn {
    items(5) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Text("列表项 ${index + 1}")
        }
    }
}
```

### 可交互的卡片

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    // pressFeedbackType 默认为 PressFeedbackType.Tint：按压时背景过渡到
    // CardColors.pressedColor（COUI 卡片按压反馈）
    onClick = { /* 处理点击事件 */ },
    onLongPress = { /* 处理长按事件 */ }
) {
    Text("可交互的卡片")
}
```

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    pressFeedbackType = PressFeedbackType.Sink, // 设置按压反馈为下沉动画效果
    showIndication = true, // 显示点击时的视觉反馈效果
    onClick = { /* 处理点击事件 */ },
) {
    Text("可交互的卡片（下沉）")
}
```

### 卡片组内的行位置

COUI 会给卡片组的圆角外边缘增加 `coui_list_card_head_or_tail_padding`（2dp）。给 `Card` 内堆叠的
preference 行传入 `CardListPosition`，即可让每一行只在真正带圆角的那一侧获得该内边距；也可以交给
[`CardListGroup`](#cardlistgroup) 自动分配。不在分组内的行默认为 `CardListPosition.None`，不增加任何内边距。

| 位置 | 顶部额外内边距 | 底部额外内边距 | 合计 |
| :--- | :------------- | :------------- | :--- |
| `None`（默认） | 0dp | 0dp | +0dp |
| `Head` | 2dp | 0dp | +2dp |
| `Middle` | 0dp | 0dp | +0dp |
| `Tail` | 0dp | 2dp | +2dp |
| `Full`（唯一一行） | 2dp | 2dp | +4dp |

遍历列表时可用 `cardListPositionOf(index, count)` 推导位置。它对应 COUI 的
`COUICardListHelper.getPositionInGroup(int, int)`，当 `count == 1` 时返回 `Full`。

```kotlin
val options = listOf("每天", "仅工作日", "从不")

Card {
    options.forEachIndexed { index, option ->
        if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index },
            cardListPosition = cardListPositionOf(index, options.size),
        )
    }
}
```

### CardListGroup

`CardListGroup` 会自动为各行编号：第一行为 `Head`，最后一行为 `Tail`，中间的行为 `Middle`，只有一行时为
`Full`。用 `item { }` 声明每一行；每一行包在一个普通 `Column` 里输出到 `Card` 的列中。各行组件
（`BasicComponent` 及各 preference 行）从 `LocalCardListPosition` 读取位置，作为 `cardListPosition` 的默认值。

分组还会绘制 COUI 行间分隔线（`COUIPreferenceItemDecoration`）：在每个基于 `BasicComponent` 的 `Head` / `Middle`
行下方，起点对齐该行标题，终点距卡片边缘 16dp。该行或下一行 `selected` 时分隔线隐藏，按压不隐藏。分组内的行之间
不要再手工插入 `HorizontalDivider`。`CardListGroup(showDividers = false)` 关闭分隔线，`item(showDivider = false) { }`
隐藏单行下方的分隔线。详见 [CardListGroup](./cardlistgroup.md)。

```kotlin
import io.github.suqi8.coui.kmp.basic.CardListGroup

Card {
    CardListGroup {
        item {
            SwitchPreference(title = "WLAN", checked = wifi, onCheckedChange = { wifi = it })
        }
        if (wifi) {
            item(key = "networks") {
                ArrowPreference(title = "网络", onClick = {})
            }
        }
        options.forEach { option ->
            item(key = option) {
                MarkPreference(title = option, checked = selected == option, onClick = { selected = option })
            }
        }
    }
}
```

- 每个 `item` 只放一行；它打开的对话框可以放在同一个 `item` 中。
- 位置按实际存在的行计算，因此条件行（`if (...) item { }`）也能得到正确编号。条件行和循环生成的行应传入
  `key`，以便前面的行变化时保留自身状态。
- 不在分组内的行保持 `CardListPosition.None`；对话框、底部面板和弹出菜单会把其内容重置为 `None`。
- 显式传入 `cardListPosition` 的行以显式值为准，分组只提供默认值。
