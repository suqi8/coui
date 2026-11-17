// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_buttons_stage_card
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_buttons_stage_color
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_buttons_stage_image
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

/**
 * What a button preview is laid over.
 *
 * @property label The localized name for the parameter dropdown.
 */
internal enum class StageBackground(val label: StringResource) {
    /** The plain card surface. */
    Plain(Res.string.demo_buttons_stage_card),

    /** The theme primary colour, for the transparent button types. */
    Accent(Res.string.demo_buttons_stage_color),

    /**
     * A recorded image provided as [LocalMaterialBackdrop], so the COUI 17 button material
     * replaces the fill with the blurred, tinted image.
     */
    Photo(Res.string.demo_buttons_stage_image),
}

/**
 * A preview stage for buttons over a [StageBackground]. The image stage records `blur_test_bg`
 * into a layer backdrop (a sibling of the content) and provides it to [content] through
 * [LocalMaterialBackdrop].
 *
 * @param background What the content is laid over.
 * @param modifier The modifier for the stage.
 * @param cornerRadius The clip radius of the stage.
 * @param contentPadding The padding around [content].
 * @param content The buttons.
 */
@Composable
internal fun ButtonStage(
    background: StageBackground,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    val isImage = background == StageBackground.Photo
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .then(if (background == StageBackground.Accent) Modifier.background(COUITheme.colorScheme.primary) else Modifier),
    ) {
        if (isImage) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(backdrop),
                contentScale = ContentScale.Crop,
            )
        }
        CompositionLocalProvider(LocalMaterialBackdrop provides if (isImage) provider else LocalMaterialBackdrop.current) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

/**
 * A titled section whose content sits on the page background with the card side margins, not in a
 * clipping card, so floating action button shadows and touch motion are not cut off.
 */
@Composable
internal fun OpenDemoSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        PreferenceCategory(title = title)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}
