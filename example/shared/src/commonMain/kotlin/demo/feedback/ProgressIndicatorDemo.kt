// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator
import io.github.suqi8.coui.kmp.basic.CircularProgressState
import io.github.suqi8.coui.kmp.basic.CircularProgressType
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.common_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_animate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_indeterminate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_kind
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_states_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_variant_circular
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_variant_linear
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_variant_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_indicator_variant_states
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `ProgressIndicator`, listed in its category and found by the search. */
internal val ProgressIndicatorDemo = Demo(
    id = "progress_indicator",
    apiName = "ProgressIndicator",
    title = Res.string.demo_progress_indicator_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { ProgressIndicatorPlayground() }
        variant(key = "linear", title = Res.string.demo_progress_indicator_variant_linear) {
            LinearVariantsContent()
        }
        variant(key = "circular", title = Res.string.demo_progress_indicator_variant_circular) {
            CircularVariantsContent()
        }
        variant(key = "loading", title = Res.string.demo_progress_indicator_variant_loading) {
            LoadingVariantsContent()
        }
        variant(key = "states", title = Res.string.demo_progress_indicator_variant_states) {
            CircularStatesContent()
        }
    },
    keywords = listOf("LinearProgressIndicator", "CircularProgressIndicator", "InfiniteProgressIndicator", "RotatingProgressIndicator"),
    searchTerms = Res.string.demo_progress_indicator_search_terms,
)

/** The fixed progress values the variants show; null is indeterminate. */
private val ProgressValues = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f, null)

private val IndicatorKinds = listOf(
    "LinearProgressIndicator",
    "CircularProgressIndicator",
    "InfiniteProgressIndicator",
    "RotatingProgressIndicator",
)

/** Ping-pongs between 0 and 1 once a second. */
@Composable
private fun animatedProgress(): Float {
    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    return value
}

@Composable
private fun ColumnScope.ProgressIndicatorPlayground() {
    var kindIndex by rememberSaveable { mutableIntStateOf(0) }
    var progress by rememberSaveable { mutableFloatStateOf(0.4f) }
    var indeterminate by rememberSaveable { mutableStateOf(false) }
    var animate by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var stateIndex by rememberSaveable { mutableIntStateOf(0) }
    var typeIndex by rememberSaveable { mutableIntStateOf(0) }
    var sizeIndex by rememberSaveable { mutableIntStateOf(2) }

    val determinateKind = kindIndex == 0 || kindIndex == 1

    PlaygroundPreview {
        // Read the animation here, so only the preview recomposes on every frame.
        val value: Float? = when {
            indeterminate -> null
            animate -> animatedProgress()
            else -> progress
        }
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (kindIndex) {
                0 -> LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = value,
                    enabled = enabled,
                )

                1 -> {
                    val (size, stroke) = circularSize(sizeIndex)
                    CircularProgressIndicator(
                        progress = value,
                        state = CircularProgressState.entries[stateIndex],
                        type = CircularProgressType.entries[typeIndex],
                        strokeWidth = stroke,
                        size = size,
                    )
                }

                2 -> if (sizeIndex == 0) {
                    InfiniteProgressIndicator()
                } else {
                    InfiniteProgressIndicator(
                        size = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorSize,
                        strokeWidth = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorStrokeWidth,
                    )
                }

                else -> if (sizeIndex == 0) {
                    RotatingProgressIndicator(
                        size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
                        ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
                        strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth,
                    )
                } else {
                    RotatingProgressIndicator()
                }
            }
        }
    }
    val sizeItems = listOf(
        stringResource(Res.string.common_small),
        stringResource(Res.string.common_medium),
        stringResource(Res.string.common_large),
    )
    val kindTitle = stringResource(Res.string.demo_progress_indicator_kind)
    val progressTitle = stringResource(Res.string.common_progress)
    val indeterminateTitle = stringResource(Res.string.demo_progress_indicator_indeterminate)
    val animateTitle = stringResource(Res.string.demo_progress_indicator_animate)
    val enabledTitle = stringResource(Res.string.common_enabled)
    val stateTitle = stringResource(Res.string.demo_progress_indicator_state)
    val typeTitle = stringResource(Res.string.demo_progress_indicator_type)
    val sizeTitle = stringResource(Res.string.common_size)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = IndicatorKinds,
                selectedIndex = kindIndex,
                title = kindTitle,
                onSelectedIndexChange = { kindIndex = it },
            )
        }
        item {
            SliderPreference(
                value = progress,
                onValueChange = { progress = it },
                title = progressTitle,
                valueText = "${(progress * 100).toInt()}%",
                enabled = determinateKind && !indeterminate && !animate,
            )
        }
        item {
            SwitchPreference(
                checked = indeterminate,
                onCheckedChange = { indeterminate = it },
                title = indeterminateTitle,
                summary = "progress = null",
                enabled = determinateKind,
            )
        }
        item {
            SwitchPreference(
                checked = animate,
                onCheckedChange = { animate = it },
                title = animateTitle,
                enabled = determinateKind && !indeterminate,
            )
        }
        item {
            SwitchPreference(
                checked = enabled,
                onCheckedChange = { enabled = it },
                title = enabledTitle,
                enabled = kindIndex == 0,
            )
        }
        item {
            OverlayDropdownPreference(
                items = CircularProgressState.entries.map { it.name },
                selectedIndex = stateIndex,
                title = stateTitle,
                summary = "CircularProgressState",
                enabled = kindIndex == 1,
                onSelectedIndexChange = { stateIndex = it },
            )
        }
        item {
            OverlayDropdownPreference(
                items = CircularProgressType.entries.map { it.name },
                selectedIndex = typeIndex,
                title = typeTitle,
                summary = "CircularProgressType",
                enabled = kindIndex == 1,
                onSelectedIndexChange = { typeIndex = it },
            )
        }
        item {
            OverlayDropdownPreference(
                items = sizeItems,
                selectedIndex = sizeIndex,
                title = sizeTitle,
                enabled = kindIndex != 0,
                onSelectedIndexChange = { sizeIndex = it },
            )
        }
    }
}

/** The circular indicator size and stroke for a small / medium / large choice. */
private fun circularSize(index: Int): Pair<Dp, Dp> = with(ProgressIndicatorDefaults) {
    when (index) {
        0 -> DefaultLoadingViewSize to DefaultLoadingViewStrokeWidth
        1 -> MediumCircularProgressIndicatorSize to MediumCircularProgressIndicatorStrokeWidth
        else -> LargeCircularProgressIndicatorSize to LargeCircularProgressIndicatorStrokeWidth
    }
}

/** An animated bar, the fixed values, indeterminate and a disabled bar. */
@Composable
private fun LinearVariantsContent() {
    val animated = animatedProgress()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LinearProgressIndicator(progress = animated)
        ProgressValues.forEach { value ->
            LinearProgressIndicator(progress = value)
        }
        LinearProgressIndicator(progress = 0.5f, enabled = false)
    }
}

/** An animated ring, the fixed values and indeterminate. */
@Composable
private fun CircularVariantsContent() {
    val animated = animatedProgress()
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(progress = animated)
        ProgressValues.forEach { value ->
            CircularProgressIndicator(progress = value)
        }
    }
}

/** The infinite and the rotating loading indicators, the latter in both sizes. */
@Composable
private fun LoadingVariantsContent() {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        InfiniteProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
        RotatingProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
        RotatingProgressIndicator(
            modifier = Modifier.align(Alignment.CenterVertically),
            size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
            ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
            strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth,
        )
    }
}

/**
 * The on-image ring cycling Normal, Paused and Error on tap, an error ring, a ring following the
 * theme and a medium animated ring.
 */
@Composable
private fun CircularStatesContent() {
    var state by rememberSaveable { mutableStateOf(CircularProgressState.Normal) }
    val animated = animatedProgress()
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = stringResource(Res.string.demo_progress_indicator_states_hint),
            modifier = Modifier.padding(bottom = 12.dp),
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CircularProgressIndicator(
                progress = 0.6f,
                state = state,
                type = CircularProgressType.OnImage,
                modifier = Modifier.clickable {
                    state = when (state) {
                        CircularProgressState.Normal -> CircularProgressState.Paused
                        CircularProgressState.Paused -> CircularProgressState.Error
                        CircularProgressState.Error -> CircularProgressState.Normal
                    }
                },
            )
            CircularProgressIndicator(progress = 0.6f, state = CircularProgressState.Error)
            CircularProgressIndicator(progress = 0.6f, type = CircularProgressType.FollowTheme)
            CircularProgressIndicator(
                progress = animated,
                size = ProgressIndicatorDefaults.MediumCircularProgressIndicatorSize,
                strokeWidth = ProgressIndicatorDefaults.MediumCircularProgressIndicatorStrokeWidth,
            )
        }
    }
}
