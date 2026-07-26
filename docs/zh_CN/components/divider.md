# Divider

`Divider` 是 COUI 中的基础布局组件，用于在列表和布局中分隔内容。提供了水平分割线和垂直分割线两种形式。

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=divider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.HorizontalDivider // 水平分割线
import io.github.suqi8.coui.kmp.basic.VerticalDivider  // 垂直分割线
```

## 基本用法

### 水平分割线

水平分割线用于分隔垂直排列的内容：

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider()
    Text("下方内容")
}
```

### 垂直分割线

垂直分割线用于分隔水平排列的内容：

```kotlin
Row {
    Text("左侧内容")
    VerticalDivider()
    Text("右侧内容")
}
```

## 属性

### HorizontalDivider 属性

| 属性名    | 类型     | 说明                 | 默认值                       | 是否必须 |
| --------- | -------- | -------------------- | ---------------------------- | -------- |
| modifier  | Modifier | 应用于分割线的修饰符 | Modifier                     | 否       |
| thickness | Dp       | 分割线的厚度         | DividerDefaults.Thickness    | 否       |
| color     | Color    | 分割线的颜色         | DividerDefaults.DividerColor | 否       |

### VerticalDivider 属性

| 属性名    | 类型     | 说明                 | 默认值                       | 是否必须 |
| --------- | -------- | -------------------- | ---------------------------- | -------- |
| modifier  | Modifier | 应用于分割线的修饰符 | Modifier                     | 否       |
| thickness | Dp       | 分割线的厚度         | DividerDefaults.Thickness    | 否       |
| color     | Color    | 分割线的颜色         | DividerDefaults.DividerColor | 否       |

### DividerDefaults 对象

DividerDefaults 对象提供了分割线组件的默认值。

#### 常量

| 常量名       | 类型  | 说明                                     | 默认值                             |
| ------------ | ----- | ---------------------------------------- | ---------------------------------- |
| Thickness    | Dp    | 分割线的默认厚度                         | 0.33.dp                            |
| DividerColor | Color | 分割线的默认颜色                         | COUITheme.colorScheme.dividerLine |
| CardInset    | Dp    | 卡片内行间分割线的推荐水平缩进（COUI 规则） | 16.dp                              |

## 进阶用法

### 自定义厚度的分割线

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider(
        thickness = 2.dp
    )
    Text("下方内容")
}
```

### 自定义颜色的分割线

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider(
        color = Color.Red
    )
    Text("下方内容")
}
```

### 卡片内行间分割线

遵循 COUI 卡片列表规则：仅在卡片内相邻两行之间放置分割线（末行之后不放），两侧缩进
`DividerDefaults.CardInset`；带前置图标的行应加大起始缩进，使分割线与标题文本起点对齐：

```kotlin
Card {
    BasicComponent(title = "第一行")
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = DividerDefaults.CardInset)
    )
    BasicComponent(title = "第二行")
}
```

### 使用垂直分割线分隔按钮

```kotlin
Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Button(onClick = { /* 处理点击事件 */ }) {
        Text("取消")
    }
    VerticalDivider(
        modifier = Modifier.height(24.dp)
    )
    Button(onClick = { /* 处理点击事件 */ }) {
        Text("确认")
    }
}
```
