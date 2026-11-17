# BottomPreference

`BottomPreference` 是收尾设置列表的不可见占位，对应 COUI 的 `COUIBottomPreference`（布局 `coui_preference_bottom`）。放在可滚动设置页的最后一项，让最后一张卡片与底部边缘保持距离。

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=bottomPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.BottomPreference
```

## 基本用法

```kotlin
LazyColumn {
    // ... 卡片
    item { BottomPreference() }
}
```

### 自定义高度

```kotlin
BottomPreference(placeholderHeight = 48.dp)
```

## 属性

### BottomPreference 属性

| 属性名            | 类型     | 说明       | 默认值                                     | 必须 |
| ----------------- | -------- | ---------- | ------------------------------------------ | ---- |
| modifier          | Modifier | 组件修饰符 | Modifier                                   | 否   |
| placeholderHeight | Dp       | 占位高度   | BottomPreferenceDefaults.PlaceholderHeight | 否   |

### BottomPreferenceDefaults 对象

| 属性名            | 类型 | 说明                                                                                     | 值    |
| ----------------- | ---- | ---------------------------------------------------------------------------------------- | ----- |
| PlaceholderHeight | Dp   | 默认高度（`support_preference_foot_preference_padding_bottom` = `coui_list_to_ex_bottom_padding`） | 32.dp |
