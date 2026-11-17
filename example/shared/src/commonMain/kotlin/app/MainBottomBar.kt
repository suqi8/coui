// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import org.jetbrains.compose.resources.stringResource
import utils.rememberMaterialBackdropProvider

/**
 * The main shell's COUI 17 floating navigation bar. Its bottom-bar material
 * (TYPE_FRAMEWORK_BOTTOM_BAR, 25px) blurs [backdrop], or falls back to COUI's opaque ?couiColorBar
 * when the blur is off.
 *
 * @param selected The selected tab index.
 * @param onSelect Called with the tab index the user picks.
 * @param backdrop The backdrop recording the pages under the bar.
 * @param modifier The modifier for the bar.
 */
@Composable
fun MainBottomBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalMaterialBackdrop provides rememberMaterialBackdropProvider(backdrop)) {
        FloatingNavigationBar(modifier = modifier) {
            MainTab.entries.forEachIndexed { index, tab ->
                FloatingNavigationBarItem(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    icon = tab.icon,
                    label = stringResource(tab.label),
                )
            }
        }
    }
}
