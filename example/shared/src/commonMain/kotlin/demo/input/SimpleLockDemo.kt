// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockDefaults
import io.github.suqi8.coui.kmp.basic.SimpleLockState
import io.github.suqi8.coui.kmp.basic.SimpleLockType
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.rememberSimpleLockState
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_add
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_over_wallpaper
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_clear
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_fail
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_glow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_glow_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_variant_four
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_simple_lock_variant_six
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal val SimpleLockDemo = Demo(
    id = "simple_lock",
    apiName = "SimpleLock",
    title = Res.string.demo_simple_lock_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { simpleLockPage() },
    keywords = listOf("pin", "密码圆点", "COUISimpleLock"),
)

private fun LazyListScope.simpleLockPage() {
    variant(key = "four", title = Res.string.demo_simple_lock_variant_four) {
        SimpleLockSample(type = SimpleLockType.Four)
    }
    variant(key = "six", title = Res.string.demo_simple_lock_variant_six) {
        SimpleLockSample(type = SimpleLockType.Six)
    }
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(SimpleLockType.Four.ordinal) }
        var overWallpaper by rememberSaveable { mutableStateOf(true) }
        var glow by rememberSaveable { mutableStateOf(true) }
        var haptic by rememberSaveable { mutableStateOf(true) }
        val type = SimpleLockType.entries[typeIndex]
        val surface = if (overWallpaper) LockScreenWallpaper else SolidColor(Color.Transparent)

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            WallpaperSurface(brush = surface, contentPadding = PaddingValues(0.dp)) {
                SimpleLockSample(
                    type = type,
                    glowEffect = glow,
                    hapticFeedbackEnabled = haptic,
                    overWallpaper = overWallpaper,
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = SimpleLockType.entries.map { "${it.name} (${it.count})" },
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.common_count),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = overWallpaper,
                    onCheckedChange = { overWallpaper = it },
                    title = stringResource(Res.string.demo_input_over_wallpaper),
                )
            }
            item {
                SwitchPreference(
                    checked = glow,
                    onCheckedChange = { glow = it },
                    title = stringResource(Res.string.demo_simple_lock_glow),
                    summary = stringResource(Res.string.demo_simple_lock_glow_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = haptic,
                    onCheckedChange = { haptic = it },
                    title = stringResource(Res.string.demo_input_haptic),
                )
            }
        }
    }
}

/**
 * A [SimpleLock] of [type] with buttons to add, delete and clear digits and to play the failure.
 * Over a wallpaper it takes the transparent colours.
 */
@Composable
private fun SimpleLockSample(
    type: SimpleLockType,
    modifier: Modifier = Modifier,
    glowEffect: Boolean = false,
    hapticFeedbackEnabled: Boolean = true,
    overWallpaper: Boolean = false,
) {
    var count by remember(type) { mutableIntStateOf(0) }
    val state: SimpleLockState = rememberSimpleLockState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SimpleLock(
            filledCount = count,
            type = type,
            state = state,
            glowEffect = glowEffect,
            hapticFeedbackEnabled = hapticFeedbackEnabled,
            colors = if (overWallpaper) SimpleLockDefaults.transparentColors() else SimpleLockDefaults.colors(),
            contentDescription = stringResource(Res.string.demo_simple_lock_description, count, type.count),
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                text = stringResource(Res.string.common_add),
                onClick = { if (count < type.count) count++ },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.common_delete),
                onClick = { if (count > 0) count-- },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_simple_lock_clear),
                onClick = { count = 0 },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_simple_lock_fail),
                onClick = {
                    scope.launch {
                        state.playFailure()
                        count = 0
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
