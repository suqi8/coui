// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderSize
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

fun LazyListScope.intentSliderSection() {
    item(key = "intentSlider") {
        SmallTitle(text = "IntentSlider")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                IntentSliderRow(label = "Default") {
                    var value by remember { mutableFloatStateOf(0.3f) }
                    IntentSlider(value = value, onValueChange = { value = it }, secondaryValue = 0.6f)
                }
                IntentSliderRow(label = "Small") {
                    var value by remember { mutableFloatStateOf(0.5f) }
                    IntentSlider(value = value, onValueChange = { value = it }, size = IntentSliderSize.Small)
                }
                IntentSliderRow(label = "Large") {
                    var value by remember { mutableFloatStateOf(0.7f) }
                    IntentSlider(value = value, onValueChange = { value = it }, size = IntentSliderSize.Large)
                }
                IntentSliderRow(label = "Follow thumb") {
                    var value by remember { mutableFloatStateOf(0.4f) }
                    IntentSlider(value = value, onValueChange = { value = it }, followThumb = true)
                }
                PausableIntentSlider()
                IntentSliderRow(label = "Disabled") {
                    IntentSlider(value = 0.5f, onValueChange = {}, enabled = false, secondaryValue = 0.8f)
                }
            }
        }
        MediaIntentSlider()
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
            style = COUITheme.textStyles.footnote1,
            color = COUITheme.colorScheme.onSurfaceSecondary,
        )
        content()
    }
}

@Composable
private fun PausableIntentSlider() {
    var value by remember { mutableFloatStateOf(0.25f) }
    var paused by remember { mutableStateOf(true) }
    IntentSliderRow(label = if (paused) "Thumb (paused)" else "Thumb (playing)") {
        IntentSlider(
            value = value,
            onValueChange = { value = it },
            showThumb = true,
            paused = paused,
            secondaryValue = 0.5f,
        )
        TextButton(
            text = if (paused) "Play" else "Pause",
            onClick = { paused = !paused },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * The fixed palette over media content: with a backdrop provider the COUI 17 blur-blend replaces
 * the translucent background fill.
 */
@Composable
private fun MediaIntentSlider() {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    var value by remember { mutableFloatStateOf(0.35f) }
    var paused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
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
                    text = "Over media (fixed colors)",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = COUITheme.textStyles.footnote1,
                    color = Color.White,
                )
                IntentSlider(
                    value = value,
                    onValueChange = { value = it },
                    showThumb = true,
                    paused = paused,
                    followSystemTheme = false,
                    secondaryValue = 0.7f,
                    size = IntentSliderSize.Large,
                )
                TextButton(
                    text = if (paused) "Play" else "Pause",
                    onClick = { paused = !paused },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}
