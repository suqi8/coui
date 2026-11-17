// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_create
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_filler_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_albums
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_profile
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberMaterialBackdropProvider
import io.github.suqi8.coui.kmp.icon.extended.Image as AlbumsIcon

/** The destinations of the navigation bar demos: icons, in order. */
internal val NavigationDemoIcons: List<ImageVector> = listOf(
    COUIIcons.Photos,
    COUIIcons.Music,
    COUIIcons.Add,
    COUIIcons.AlbumsIcon,
    COUIIcons.Contacts,
)

/** The destinations of the navigation bar demos: labels, matching [NavigationDemoIcons]. */
internal val NavigationDemoLabels: List<StringResource> = listOf(
    Res.string.sample_photos,
    Res.string.sample_music,
    Res.string.demo_navigation_create,
    Res.string.sample_albums,
    Res.string.sample_profile,
)

/** The item count choices of the navigation bar demos, as dropdown labels. */
internal val NavigationDemoCountOptions = listOf("2", "3", "4", "5")

/** The smallest item count in [NavigationDemoCountOptions]. */
internal val NavigationDemoMinCount = 2

/**
 * Lays [content] over the blur test picture when [enabled]. The picture is recorded into a
 * backdrop handed to [content] as [LocalMaterialBackdrop], so the COUI 17 materials inside it blur
 * the picture instead of keeping their opaque fill.
 *
 * @param enabled Whether to draw the picture and provide its backdrop.
 * @param modifier The modifier for the box.
 * @param content The previewed component.
 */
@Composable
internal fun ImageBackdropBox(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        if (enabled) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(backdrop),
                contentScale = ContentScale.Crop,
            )
        }
        CompositionLocalProvider(LocalMaterialBackdrop provides if (enabled) provider else null) {
            content()
        }
    }
}

/**
 * Adds page content for the full-screen bar demos to scroll under the bars: a picture, so the
 * bar materials have something colourful to blur, and a run of rows.
 *
 * @param rows The number of rows.
 */
internal fun LazyListScope.navigationFillerContent(rows: Int = 24) {
    variant(key = "navigation_filler", title = Res.string.demo_navigation_filler_title) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )
        repeat(rows) { index ->
            BasicComponent(
                title = stringResource(Res.string.sample_item, index + 1),
                summary = stringResource(Res.string.sample_summary),
            )
        }
    }
}
