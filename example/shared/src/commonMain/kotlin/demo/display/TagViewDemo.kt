// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.TagView
import io.github.suqi8.coui.kmp.basic.TagViewColors
import io.github.suqi8.coui.kmp.basic.TagViewDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_dark_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_beta
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_corner
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_new
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_label_outlined
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_shape
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_shape_capsule
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_shape_corner
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_style_filled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_style_orange
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_style_outlined
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_trailing_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_variant_inline
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tag_view_variant_styles
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

internal val TagViewDemo = Demo(
    id = "tag_view",
    apiName = "TagView",
    title = Res.string.demo_tag_view_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { tagViewPage() },
    keywords = listOf("tag", "label", "标记"),
)

private const val STYLE_FILLED = 0
private const val STYLE_OUTLINED = 1
private const val STYLE_ORANGE = 2

private const val SHAPE_DEFAULT = 0
private const val SHAPE_CORNER = 1

/** The large label size of the original demo. */
private val LargeTextSize = 12.dp

/** The inside margin that goes with [LargeTextSize]. */
private val LargeInsideMargin = PaddingValues(horizontal = 6.dp, vertical = 2.dp)

private fun LazyListScope.tagViewPage() {
    playground {
        var style by rememberSaveable { mutableIntStateOf(STYLE_FILLED) }
        var shape by rememberSaveable { mutableIntStateOf(SHAPE_DEFAULT) }
        var large by rememberSaveable { mutableStateOf(false) }
        var trailingIcon by rememberSaveable { mutableStateOf(false) }
        var longText by rememberSaveable { mutableStateOf(false) }

        PlaygroundPreview {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(Res.string.demo_tag_view_dark_mode))
                TagView(
                    text = stringResource(if (longText) Res.string.demo_tag_view_label_long else Res.string.demo_tag_view_label_new),
                    shape = when (shape) {
                        SHAPE_DEFAULT -> TagViewDefaults.Shape
                        SHAPE_CORNER -> RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
                        else -> CircleShape
                    },
                    colors = tagStyleColors(style),
                    strokeWidth = if (style == STYLE_OUTLINED) 1.dp else TagViewDefaults.StrokeWidth,
                    textSize = if (large) LargeTextSize else TagViewDefaults.TextSize,
                    insideMargin = if (large) LargeInsideMargin else TagViewDefaults.InsideMargin,
                    trailingIcon = if (trailingIcon) {
                        { TagIcon(large = large) }
                    } else {
                        null
                    },
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "colors",
                    items = listOf(
                        stringResource(Res.string.demo_tag_view_style_filled),
                        stringResource(Res.string.demo_tag_view_style_outlined),
                        stringResource(Res.string.demo_tag_view_style_orange),
                    ),
                    selectedIndex = style,
                    onSelectedIndexChange = { style = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_tag_view_shape),
                    summary = "shape",
                    items = listOf(
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.demo_tag_view_shape_corner),
                        stringResource(Res.string.demo_tag_view_shape_capsule),
                    ),
                    selectedIndex = shape,
                    onSelectedIndexChange = { shape = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_size),
                    summary = "textSize",
                    items = listOf(
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.common_large),
                    ),
                    selectedIndex = if (large) 1 else 0,
                    onSelectedIndexChange = { large = it == 1 },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_tag_view_trailing_icon),
                    summary = "trailingIcon",
                    checked = trailingIcon,
                    onCheckedChange = { trailingIcon = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_long_text),
                    checked = longText,
                    onCheckedChange = { longText = it },
                )
            }
        }
    }
    variant(key = "inline", title = Res.string.demo_tag_view_variant_inline) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(Res.string.demo_tag_view_dark_mode))
            TagView(text = stringResource(Res.string.demo_tag_view_label_new))
            TagView(
                text = stringResource(Res.string.demo_tag_view_label_beta),
                colors = tagStyleColors(STYLE_ORANGE),
            )
        }
    }
    variant(key = "styles", title = Res.string.demo_tag_view_variant_styles) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TagView(
                    text = stringResource(Res.string.demo_tag_view_label_outlined),
                    colors = tagStyleColors(STYLE_OUTLINED),
                    strokeWidth = 1.dp,
                )
                TagView(
                    text = stringResource(Res.string.demo_tag_view_label_corner),
                    shape = RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                )
                TagView(
                    text = stringResource(Res.string.demo_tag_view_label_large),
                    textSize = LargeTextSize,
                    insideMargin = LargeInsideMargin,
                    trailingIcon = { TagIcon(large = true) },
                )
            }
        }
    }
}

/** The tag colours of a playground style. */
@Composable
private fun tagStyleColors(style: Int): TagViewColors = when (style) {
    STYLE_OUTLINED -> TagViewDefaults.tagViewColors(
        containerColor = Color.Transparent,
        contentColor = COUITheme.colorScheme.primary,
        strokeColor = COUITheme.colorScheme.primary,
    )

    STYLE_ORANGE -> TagViewDefaults.tagViewColors(containerColor = COUITheme.colorScheme.orange)

    else -> TagViewDefaults.tagViewColors()
}

/** The trailing add icon, sized to the label. */
@Composable
private fun TagIcon(large: Boolean) {
    Icon(
        imageVector = COUIIcons.Add,
        contentDescription = null,
        modifier = Modifier.size(if (large) 12.dp else 10.dp),
    )
}
