# Badge

红点角标组件,对应 ColorOS 的 COUIHintRedDot,共三种形态:6dp 纯圆点、随位数变宽的 16dp 高数字胶囊,以及数字达到 1000 及以上时的三点省略号。`BadgeBox` 可将角标锚定在图标右上角,对应 COUIRedDotFrameLayout。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.BadgeBox
import io.github.suqi8.coui.kmp.basic.BadgeDefaults
```

## 基本用法

```kotlin
Badge()                // 纯圆点
Badge(count = 8)       // 数字胶囊
Badge(count = 1000)    // 三点省略号(1000+)
Badge(stroke = true)   // 白色描边,用于彩色背景
```

### 锚定在图标上

```kotlin
BadgeBox(
    badge = { Badge(count = 8) },
    overhang = BadgeDefaults.CountOverhang,
) {
    Icon(
        imageVector = COUIIcons.Settings,
        contentDescription = "Settings",
    )
}
```

## 属性

### Badge

| 属性        | 类型        | 说明                                | 默认值                      | 必需 |
| ----------- | ----------- | ----------------------------------- | --------------------------- | ---- |
| modifier    | Modifier    | 应用于角标的修饰符                  | Modifier                    | 否   |
| count       | Int         | 角标显示的数字,<= 0 时显示纯圆点   | 0                           | 否   |
| stroke      | Boolean     | 是否绘制白色描边                    | false                       | 否   |
| colors      | BadgeColors | 颜色配置                            | BadgeDefaults.badgeColors() | 否   |
| dotDiameter | Dp          | 纯圆点形态的直径                    | BadgeDefaults.DotDiameter   | 否   |
| height      | Dp          | 数字胶囊形态的高度                  | BadgeDefaults.Height        | 否   |

### BadgeBox

| 属性     | 类型       | 说明                             | 默认值                    | 必需 |
| -------- | ---------- | -------------------------------- | ------------------------- | ---- |
| badge    | @Composable () -> Unit | 要锚定的角标         | -                         | 是   |
| modifier | Modifier   | 应用于布局的修饰符               | Modifier                  | 否   |
| overhang | Dp         | 角标超出内容右上角的距离         | BadgeDefaults.DotOverhang | 否   |
| content  | @Composable () -> Unit | 锚定内容,通常为图标 | -                         | 是   |

### BadgeDefaults

| 常量                    | 类型 | 默认值 |
| ----------------------- | ---- | ------ |
| DotDiameter             | Dp   | 6.dp   |
| Height                  | Dp   | 16.dp  |
| SmallWidth              | Dp   | 16.dp  |
| MediumWidth             | Dp   | 20.dp  |
| LargeWidth              | Dp   | 26.dp  |
| TextSize                | Dp   | 10.dp  |
| StrokeWidth             | Dp   | 1.dp   |
| EllipsisDotDiameter     | Dp   | 2.dp   |
| EllipsisSpacing         | Dp   | 2.dp   |
| DotOverhang             | Dp   | 2.dp   |
| CountOverhang           | Dp   | 3.dp   |
| ScaleAnimDurationMillis | Int  | 520    |

### `badgeColors()` 工厂

| 参数           | 类型  | 默认值                                          |
| -------------- | ----- | ----------------------------------------------- |
| containerColor | Color | COUI 专用红(浅色 #EB3B2F,深色 #EB493D)       |
| contentColor   | Color | Color.White                                     |
| strokeColor    | Color | Color.White                                     |

## 行为

- `count <= 0` 显示纯圆点;1..9 为 16dp 胶囊,10..99 为 20dp,100..999 为 26dp;1000 及以上为 20dp 胶囊内的三点省略号(COUI `red_dot_more`)。
- 数字变化的动画与 COUIHintRedDot 一致:胶囊宽度以 517ms 缓动(COUIMoveEaseInterpolator),新旧数字同时以 150ms 交叉淡变。
- 数字文本按密度换算为 10dp(sans-serif medium)渲染,与原版一样不受用户字体缩放影响。
- `stroke = true` 绘制 1dp 白色描边;圆点形态的红色圆核按描边宽度缩小,胶囊形态的红色填充内缩于白色描边之内。
- `BadgeBox` 的 `overhang` 为正时,角标超出内容右上角并相应扩大布局(COUI 矩形锚点);为负时角标内缩于角内(COUI 圆形锚点,如头像)。
- 若需要 COUIHintRedDot 的显示 / 隐藏缩放动画,可用 `AnimatedVisibility` 配合 `scaleIn` / `scaleOut`,时长取 `BadgeDefaults.ScaleAnimDurationMillis`。
