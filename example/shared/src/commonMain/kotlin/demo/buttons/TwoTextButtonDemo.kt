// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.ButtonMaterial
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TwoTextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButtonDefaults
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_next
import io.github.suqi8.coui.kmp.shared.generated.resources.common_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_press_scale
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_primary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_secondary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_fill
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_wrap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_done
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_get_started
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_page
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_progress_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_two_text_button_variant_onboarding
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

internal val TwoTextButtonDemo = Demo(
    id = "two_text_button",
    apiName = "TwoTextButton",
    title = Res.string.demo_two_text_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { twoTextButtonPage() },
    keywords = listOf("onboarding", "引导", "crossfade"),
)

/** The page count of the onboarding variant. */
private const val ONBOARDING_PAGES = 3

/** The duration of the playground's click animation. */
private const val SWITCH_DURATION_MILLIS = 400

private fun LazyListScope.twoTextButtonPage() {
    playground {
        var progress by rememberSaveable { mutableFloatStateOf(0f) }
        var primary by rememberSaveable { mutableStateOf(true) }
        var fillWidth by rememberSaveable { mutableStateOf(true) }
        var material by rememberSaveable { mutableIntStateOf(ButtonMaterial.Auto.ordinal) }
        var pressScale by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val scope = rememberCoroutineScope()

        PlaygroundPreview {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.demo_two_text_button_progress_hint, "${(progress * 100).toInt()}%"),
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
                TwoTextButton(
                    startText = stringResource(Res.string.common_next),
                    nextText = stringResource(Res.string.demo_two_text_button_get_started),
                    startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(progress) },
                    nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(progress) },
                    onClick = {
                        // Plays the crossfade to the other label.
                        val target = if (progress < 0.5f) 1f else 0f
                        scope.launch {
                            animate(
                                initialValue = progress,
                                targetValue = target,
                                animationSpec = tween(SWITCH_DURATION_MILLIS),
                            ) { value, _ -> progress = value }
                        }
                    },
                    modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier,
                    enabled = enabled,
                    pressScaleEnabled = pressScale,
                    colors = if (primary) ButtonDefaults.buttonColorsPrimary() else ButtonDefaults.buttonColors(),
                    material = ButtonMaterial.entries[material],
                )
            }
        }
        PlaygroundControls {
            item {
                SliderPreference(
                    value = progress,
                    onValueChange = { progress = it },
                    title = stringResource(Res.string.common_progress),
                    summary = "outgoingTextAlpha / incomingTextAlpha",
                    valueText = "${(progress * 100).toInt()}%",
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "colors",
                    items = listOf(
                        stringResource(Res.string.demo_button_type_secondary),
                        stringResource(Res.string.demo_button_type_primary),
                    ),
                    selectedIndex = if (primary) 1 else 0,
                    onSelectedIndexChange = { primary = it == 1 },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_button_width),
                    items = listOf(
                        stringResource(Res.string.demo_button_width_wrap),
                        stringResource(Res.string.demo_button_width_fill),
                    ),
                    selectedIndex = if (fillWidth) 1 else 0,
                    onSelectedIndexChange = { fillWidth = it == 1 },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_material),
                    summary = "material",
                    items = ButtonMaterial.entries.map { it.name },
                    selectedIndex = material,
                    onSelectedIndexChange = { material = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_button_press_scale),
                    summary = "pressScaleEnabled",
                    checked = pressScale,
                    onCheckedChange = { pressScale = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_enabled),
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                )
            }
        }
    }
    // The real use: the label turns from "Next" to "Get started" while the pager moves onto its
    // last page.
    variant(key = "onboarding", title = Res.string.demo_two_text_button_variant_onboarding) {
        val pagerState = rememberPagerState { ONBOARDING_PAGES }
        val scope = rememberCoroutineScope()
        val snackbarHostState = LocalDemoSnackbarHostState.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            ) { page ->
                Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(Res.string.demo_two_text_button_page, page + 1))
                }
            }
            PageIndicator(pagerState = pagerState)
            TwoTextButton(
                startText = stringResource(Res.string.common_next),
                nextText = stringResource(Res.string.demo_two_text_button_get_started),
                startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(pagerState.lastPageProgress()) },
                nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(pagerState.lastPageProgress()) },
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < ONBOARDING_PAGES - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            snackbarHostState.showSnackbar(getString(Res.string.demo_two_text_button_done))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
    }
}

/** How far the pager has moved from the second-to-last page onto the last one, 0..1. */
private fun PagerState.lastPageProgress(): Float = (currentPage + currentPageOffsetFraction - (pageCount - 2)).coerceIn(0f, 1f)
