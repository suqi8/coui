# CardListGroup

`CardListGroup` 按 COUI 偏好分组的方式组织 [Card](./card.md) 里堆叠的行：为每一行分配卡片列表位置（圆角边缘上
2dp 的首 / 尾内边距），并绘制行间分隔线。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CardListGroup
```

## 基本用法

```kotlin
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    CardListGroup {
        item { SwitchPreference(title = "WLAN", checked = wifi, onCheckedChange = { wifi = it }) }
        item { ArrowPreference(title = "蓝牙", onClick = {}) }
        item { ArrowPreference(title = "移动网络", onClick = {}) }
    }
}
```

## 位置

第一行为 `CardListPosition.Head`，最后一行为 `Tail`，中间为 `Middle`，只有一行时为 `Full`
（`COUICardListHelper.getPositionInGroup`）。各行从 `LocalCardListPosition` 读取位置作为 `cardListPosition`
的默认值；显式传入 `cardListPosition` 的行以显式值为准。位置按实际存在的行计算，条件行（`if (...) item { }`）也能正确编号。

## 分隔线

分组会绘制 COUI 行间分隔线（`COUIPreferenceItemDecoration`）：

- 画在每个基于 `BasicComponent` 的 `Head` / `Middle` 行下方（所有偏好项都基于它）。其他组件的行不画，与 COUI
  只为偏好项画线一致。
- 起点对齐该行标题（有起始图标时越过图标），终点距卡片边缘 16dp（`DividerDefaults.CardInset`）。颜色
  `DividerDefaults.DividerColor`，粗细 `DividerDefaults.Thickness` 取整到像素、至少 1px。
- 该行或下一行 `selected` 时隐藏；按压不隐藏。

分组内的行之间不要再手工插入 `HorizontalDivider`。

```kotlin
Card {
    CardListGroup(showDividers = true) {
        options.forEachIndexed { index, option ->
            item(key = option, showDivider = true) {
                BasicComponent(
                    title = option,
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                )
            }
        }
    }
}
```

## 属性

### CardListGroup 属性

| 属性名 | 类型 | 说明 | 默认值 | 是否必须 |
| ------ | ---- | ---- | ------ | -------- |
| showDividers | Boolean | 分组是否绘制行间分隔线（COUIPreferenceFragment `mEnableInternalDivider`） | true | 否 |
| content | CardListGroupScope.() -> Unit | 用 `item` 声明各行 | - | 是 |

### CardListGroupScope.item

| 参数 | 类型 | 说明 | 默认值 |
| ---- | ---- | ---- | ------ |
| key | Any? | 稳定 key，前面的行变化时保留本行状态 | null |
| showDivider | Boolean | 是否绘制本行下方的分隔线（COUI `couiShowDivider`） | true |
| content | @Composable () -> Unit | 该行内容 | - |

## 说明

- 每个 `item` 只放一行；它打开的对话框可以放在同一个 `item` 中。
- 每一行会包在一个普通 `Column` 里。
- 对话框、底部面板和弹出菜单会把其内容的 `LocalCardListPosition` 重置为 `None`，其中的行既不获得分组内边距，也不向分组上报。
