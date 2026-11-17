// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

/**
 * The page surface (`backgroundWithCard`) inside a card, for components whose parts are
 * card-coloured (numeric keyboard keys, lock screen password field) and vanish on a card.
 *
 * @param modifier The modifier for the surface.
 * @param contentPadding The padding around [content].
 * @param horizontalAlignment The horizontal alignment of [content].
 * @param content The content.
 */
@Composable
internal fun PageSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(COUITheme.colorScheme.backgroundWithCard)
            .padding(contentPadding),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

/**
 * A sample lock screen wallpaper behind [content], for the lock screen styles of the keyboard,
 * the PIN dots and the password field.
 *
 * @param brush The wallpaper.
 * @param modifier The modifier for the wallpaper.
 * @param contentPadding The padding around [content].
 * @param content The content.
 */
@Composable
internal fun WallpaperSurface(
    brush: Brush,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 24.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(brush)
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

/**
 * A stage for material (glass) components: with [overImage] the content sits on a photo recorded
 * into a backdrop, which the COUI 17 material of the content blurs; otherwise it sits on the card.
 *
 * @param overImage Whether to draw the photo behind [content].
 * @param modifier The modifier for the stage.
 * @param content The content.
 */
@Composable
internal fun MaterialStage(
    overImage: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(if (overImage) backdrop else null)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        if (overImage) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(backdrop),
                contentScale = ContentScale.Crop,
            )
        }
        CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
            Box(modifier = Modifier.padding(vertical = if (overImage) 16.dp else 0.dp)) {
                content()
            }
        }
    }
}

/**
 * A small secondary caption under a preview, for notes and read-outs.
 *
 * @param text The caption.
 * @param modifier The modifier for the caption.
 */
@Composable
internal fun InputCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = COUITheme.textStyles.bodyXS,
        color = COUITheme.colorScheme.labelSecondaryVariant,
    )
}
