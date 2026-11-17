// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderPlaybackState
import io.github.suqi8.coui.kmp.basic.IntentSliderSize
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_buffering
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_conic_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_conic_light_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_follow_thumb
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_follow_thumb_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_initial_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_loading_drag_return
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_loading_drag_return_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_over_media
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_over_media_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_pause
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_play
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_playback_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_secondary_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_show_thumb
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_state_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_thumb_paused
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_thumb_playing
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_variant_media
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_variant_media_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_variant_playback
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_intent_slider_variant_sizes
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberMaterialBackdropProvider
import kotlin.math.roundToInt

internal val IntentSliderDemo = Demo(
    id = "intent_slider",
    apiName = "IntentSlider",
    title = Res.string.demo_intent_slider_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { intentSliderPage() },
    keywords = listOf("seekbar", "进度条", "media", "播放"),
)

private fun LazyListScope.intentSliderPage() {
    playground {
        var value by rememberSaveable { mutableFloatStateOf(0.35f) }
        var sizeIndex by rememberSaveable { mutableIntStateOf(IntentSliderSize.Default.ordinal) }
        var playbackIndex by rememberSaveable { mutableIntStateOf(IntentSliderPlaybackState.Idle.ordinal) }
        var showThumb by rememberSaveable { mutableStateOf(false) }
        var followThumb by rememberSaveable { mutableStateOf(false) }
        var showSecondary by rememberSaveable { mutableStateOf(true) }
        var loadingDragReturn by rememberSaveable { mutableStateOf(false) }
        var overMedia by rememberSaveable { mutableStateOf(false) }
        var conicLight by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            MediaStage(overMedia = overMedia) {
                IntentSlider(
                    value = value,
                    onValueChange = { value = it },
                    enabled = enabled,
                    showThumb = showThumb,
                    followThumb = followThumb,
                    followSystemTheme = !overMedia,
                    conicLight = conicLight,
                    loadingDragReturn = loadingDragReturn,
                    playbackState = IntentSliderPlaybackState.entries[playbackIndex],
                    secondaryValue = if (showSecondary) 0.7f else null,
                    size = IntentSliderSize.entries[sizeIndex],
                )
            }
            ValueCaption(text = stringResource(Res.string.common_value_format, "${(value * 100).roundToInt()}%"))
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = listOf(
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.common_small),
                        stringResource(Res.string.common_large),
                    ),
                    selectedIndex = sizeIndex,
                    title = stringResource(Res.string.common_size),
                    onSelectedIndexChange = { sizeIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = IntentSliderPlaybackState.entries.map { it.name },
                    selectedIndex = playbackIndex,
                    title = stringResource(Res.string.demo_intent_slider_playback_state),
                    onSelectedIndexChange = { playbackIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = showThumb,
                    onCheckedChange = { showThumb = it },
                    title = stringResource(Res.string.demo_intent_slider_show_thumb),
                )
            }
            item {
                SwitchPreference(
                    checked = followThumb,
                    onCheckedChange = { followThumb = it },
                    title = stringResource(Res.string.demo_intent_slider_follow_thumb),
                    summary = stringResource(Res.string.demo_intent_slider_follow_thumb_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = showSecondary,
                    onCheckedChange = { showSecondary = it },
                    title = stringResource(Res.string.demo_intent_slider_secondary_value),
                )
            }
            item {
                SwitchPreference(
                    checked = loadingDragReturn,
                    onCheckedChange = { loadingDragReturn = it },
                    title = stringResource(Res.string.demo_intent_slider_loading_drag_return),
                    summary = stringResource(Res.string.demo_intent_slider_loading_drag_return_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = overMedia,
                    onCheckedChange = { overMedia = it },
                    title = stringResource(Res.string.demo_intent_slider_over_media),
                    summary = stringResource(Res.string.demo_intent_slider_over_media_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = conicLight,
                    onCheckedChange = { conicLight = it },
                    title = stringResource(Res.string.demo_intent_slider_conic_light),
                    summary = stringResource(Res.string.demo_intent_slider_conic_light_summary),
                    // Like isConicLightProgressStyle, it only applies to the fixed palette.
                    enabled = overMedia,
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "sizes", title = Res.string.demo_intent_slider_variant_sizes) {
        IntentSliderSizes()
    }
    variant(key = "playback", title = Res.string.demo_intent_slider_variant_playback) {
        PausableIntentSlider()
        LoadingIntentSlider()
    }
    variant(key = "media", title = Res.string.demo_intent_slider_variant_media, inCard = false) {
        MediaIntentSlider()
    }
}

/**
 * The preview stage: the plain card, or a photo recorded into a backdrop so the slider over it
 * gets the COUI 17 blur-blend instead of its translucent fill.
 */
@Composable
private fun MediaStage(
    overMedia: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Only the photo is inset; on the card the slider keeps its own horizontal padding.
            .then(if (overMedia) Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)) else Modifier),
    ) {
        if (overMedia) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(backdrop),
                contentScale = ContentScale.Crop,
            )
        }
        CompositionLocalProvider(
            LocalMaterialBackdrop provides if (overMedia) provider else LocalMaterialBackdrop.current,
        ) {
            Box(modifier = Modifier.padding(vertical = if (overMedia) 24.dp else 0.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun IntentSliderRow(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = COUITheme.textStyles.bodyS,
            color = COUITheme.colorScheme.labelSecondary,
        )
        content()
    }
}

@Composable
private fun IntentSliderSizes() {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        IntentSliderRow(label = stringResource(Res.string.common_default)) {
            var value by remember { mutableFloatStateOf(0.3f) }
            IntentSlider(value = value, onValueChange = { value = it }, secondaryValue = 0.6f)
        }
        IntentSliderRow(label = stringResource(Res.string.common_small)) {
            var value by remember { mutableFloatStateOf(0.5f) }
            IntentSlider(value = value, onValueChange = { value = it }, size = IntentSliderSize.Small)
        }
        IntentSliderRow(label = stringResource(Res.string.common_large)) {
            var value by remember { mutableFloatStateOf(0.7f) }
            IntentSlider(value = value, onValueChange = { value = it }, size = IntentSliderSize.Large)
        }
        IntentSliderRow(label = stringResource(Res.string.demo_intent_slider_follow_thumb)) {
            var value by remember { mutableFloatStateOf(0.4f) }
            IntentSlider(value = value, onValueChange = { value = it }, followThumb = true)
        }
        IntentSliderRow(label = stringResource(Res.string.common_disabled)) {
            IntentSlider(value = 0.5f, onValueChange = {}, enabled = false, secondaryValue = 0.8f)
        }
    }
}

@Composable
private fun PausableIntentSlider() {
    var value by remember { mutableFloatStateOf(0.25f) }
    var paused by remember { mutableStateOf(true) }
    IntentSliderRow(
        label = stringResource(
            if (paused) Res.string.demo_intent_slider_thumb_paused else Res.string.demo_intent_slider_thumb_playing,
        ),
    ) {
        IntentSlider(
            value = value,
            onValueChange = { value = it },
            showThumb = true,
            playbackState = if (paused) IntentSliderPlaybackState.Paused else IntentSliderPlaybackState.Playing,
            secondaryValue = 0.5f,
        )
        TextButton(
            text = stringResource(if (paused) Res.string.demo_intent_slider_play else Res.string.demo_intent_slider_pause),
            onClick = { paused = !paused },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun LoadingIntentSlider() {
    var value by remember { mutableFloatStateOf(0f) }
    var buffering by remember { mutableStateOf(false) }
    IntentSliderRow(
        label = stringResource(
            if (buffering) Res.string.demo_intent_slider_buffering else Res.string.demo_intent_slider_initial_loading,
        ),
    ) {
        IntentSlider(
            value = value,
            onValueChange = { value = it },
            loadingDragReturn = true,
            playbackState = if (buffering) IntentSliderPlaybackState.LoadingBuffering else IntentSliderPlaybackState.LoadingInitial,
        )
        TextButton(
            text = stringResource(
                if (buffering) Res.string.demo_intent_slider_initial_loading else Res.string.demo_intent_slider_buffering,
            ),
            onClick = { buffering = !buffering },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * The fixed palette over media content: with a backdrop provider the COUI 17 blur-blend replaces
 * the translucent background fill, and the conic light style trails the progress while playing or
 * dragging.
 */
@Composable
private fun MediaIntentSlider() {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    var value by remember { mutableFloatStateOf(0.35f) }
    var playback by remember { mutableStateOf(IntentSliderPlaybackState.Playing) }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = stringResource(Res.string.demo_intent_slider_variant_media_label),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = COUITheme.textStyles.bodyS,
                    // Fixed white over the photo, like the fixed slider palette.
                    color = Color.White,
                )
                IntentSlider(
                    value = value,
                    onValueChange = { value = it },
                    showThumb = true,
                    followSystemTheme = false,
                    conicLight = true,
                    loadingDragReturn = true,
                    playbackState = playback,
                    secondaryValue = 0.7f,
                    size = IntentSliderSize.Large,
                )
                TextButton(
                    text = stringResource(Res.string.demo_intent_slider_state_format, playback.name),
                    onClick = {
                        val states = IntentSliderPlaybackState.entries
                        playback = states[(playback.ordinal + 1) % states.size]
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}
