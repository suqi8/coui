# BottomPreference

`BottomPreference` is an invisible placeholder that closes a settings list, mirroring COUI's `COUIBottomPreference` (layout `coui_preference_bottom`). Put it as the last item of a scrolling settings page to keep the last card off the bottom edge.

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=bottomPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.BottomPreference
```

## Basic Usage

```kotlin
LazyColumn {
    // ... cards
    item { BottomPreference() }
}
```

### Custom Height

```kotlin
BottomPreference(placeholderHeight = 48.dp)
```

## Properties

### BottomPreference Properties

| Property Name     | Type     | Description               | Default Value                              | Required |
| ----------------- | -------- | ------------------------- | ------------------------------------------ | -------- |
| modifier          | Modifier | Component modifier        | Modifier                                   | No       |
| placeholderHeight | Dp       | Height of the placeholder | BottomPreferenceDefaults.PlaceholderHeight | No       |

### BottomPreferenceDefaults Object

| Property Name     | Type | Description                                                                      | Value |
| ----------------- | ---- | -------------------------------------------------------------------------------- | ----- |
| PlaceholderHeight | Dp   | Default height (`support_preference_foot_preference_padding_bottom` = `coui_list_to_ex_bottom_padding`) | 32.dp |
