// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.PressFeedbackType

/**
 * A card-style button with an icon above a one-line title, mirroring COUICardButtonPreference.
 *
 * The tile is coui_component_card_button_preference.xml: a vertical, horizontally centered stack
 * on coui_component_card_button_bg (couiColorCardBackground, 16dp corners) padded 8dp horizontally
 * and 12dp vertically, with the title 4dp below the icon. The title is 12sp
 * (CARD_BUTTON_TEXT_SIZE_NORMAL) and drops to 10sp (CARD_BUTTON_TEXT_SIZE_SMALL) when it does not
 * fit on one line, then ellipsizes. Pressing scales the tile down (COUIPressFeedbackJumpPreference:
 * COUIPressFeedbackHelper card press feedback), never tints it.
 *
 * Use [CardButtonPreferenceGroup] to lay several tiles out in the COUI CardButtonDecoration grid.
 *
 * @param title The title of the [CardButtonPreference].
 * @param onClick The callback when the tile is clicked. A `null` value renders an inert tile.
 * @param modifier The modifier to be applied to the [CardButtonPreference].
 * @param enabled Whether the tile can be clicked.
 * @param cornerRadius The corner radius of the tile.
 * @param insideMargin The padding inside the tile.
 * @param colors The [CardButtonPreferenceColors] of the tile.
 * @param icon The icon above the title (COUI android:icon).
 */
@Composable
fun CardButtonPreference(
    title: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = CardButtonPreferenceDefaults.CornerRadius,
    insideMargin: PaddingValues = CardButtonPreferenceDefaults.InsideMargin,
    colors: CardButtonPreferenceColors = CardButtonPreferenceDefaults.cardButtonPreferenceColors(),
    icon: (@Composable () -> Unit)? = null,
) {
    val clickable = enabled && onClick != null
    Card(
        modifier = if (clickable) modifier.semantics { role = Role.Button } else modifier,
        cornerRadius = cornerRadius,
        insideMargin = insideMargin,
        colors = CardDefaults.defaultColors(color = colors.containerColor),
        pressFeedbackType = if (clickable) PressFeedbackType.Sink else PressFeedbackType.None,
        onClick = if (clickable) onClick else null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (icon != null) {
                Box(contentAlignment = Alignment.Center) { icon() }
            }
            CardButtonTitle(
                title = title,
                color = colors.titleColor(enabled),
                modifier = if (icon != null) {
                    Modifier.padding(top = CardButtonPreferenceDefaults.TitleTopMargin)
                } else {
                    Modifier
                },
            )
        }
    }
}

/**
 * The one-line tile title. COUICardButtonPreference measures the 12sp text after layout and
 * switches to 10sp when `paint.measureText(text)` exceeds the available width.
 */
@Composable
private fun CardButtonTitle(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    var shrunk by remember(title) { mutableStateOf(false) }
    val baseStyle = COUITheme.textStyles.bodyXS
    val fontSize: TextUnit = if (shrunk) {
        CardButtonPreferenceDefaults.SmallTitleFontSize
    } else {
        CardButtonPreferenceDefaults.TitleFontSize
    }
    Text(
        text = title,
        modifier = modifier.fillMaxWidth(),
        style = baseStyle.copy(
            fontSize = fontSize,
            lineHeight = if (baseStyle.lineHeight.isSpecified) {
                baseStyle.lineHeight * (fontSize.value / baseStyle.fontSize.value)
            } else {
                TextUnit.Unspecified
            },
        ),
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { result ->
            if (!shrunk && result.hasVisualOverflow) shrunk = true
        },
    )
}

/**
 * An entry of a [CardButtonPreferenceGroup], mirroring one COUICardButtonPreference.
 *
 * @param title The title of the tile.
 * @param icon The icon above the title.
 * @param enabled Whether the tile can be clicked.
 * @param onClick The callback when the tile is clicked. A `null` value renders an inert tile.
 */
@Stable
data class CardButtonItem(
    val title: String,
    val icon: (@Composable () -> Unit)? = null,
    val enabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
)

/**
 * A grid of [CardButtonPreference] tiles, mirroring a run of COUICardButtonPreference items laid out
 * by `CardButtonDecoration`.
 *
 * CardButtonDecoration gives every tile 4dp on both sides
 * (coui_component_card_button_horizontal_margin_inner), so columns are 8dp apart, and every row
 * after the first 16dp on top (coui_component_card_button_first_top_margin, which COUI stores as
 * CardMargin.otherRowTop). Tiles in a row share its height. The outer 4dp of the first and last
 * columns plus the 12dp list padding (coui_component_card_button_horizontal_margin_outer) make up the
 * usual 16dp screen margin, which is left to the caller like any other card block. COUI leaves the
 * column count to the host's GridLayoutManager; [columns] defaults to four.
 *
 * @param items The tiles in display order. Build the list inside a `remember` block to keep it
 *   stable across recompositions.
 * @param modifier The modifier to be applied to the [CardButtonPreferenceGroup].
 * @param columns The number of columns.
 * @param colors The [CardButtonPreferenceColors] of the tiles.
 */
@Composable
fun CardButtonPreferenceGroup(
    items: List<CardButtonItem>,
    modifier: Modifier = Modifier,
    columns: Int = CardButtonPreferenceDefaults.Columns,
    colors: CardButtonPreferenceColors = CardButtonPreferenceDefaults.cardButtonPreferenceColors(),
) {
    if (items.isEmpty()) return
    val columnCount = columns.coerceAtLeast(1)
    val rows = remember(items, columnCount) { items.chunked(columnCount) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CardButtonPreferenceDefaults.RowSpacing),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(CardButtonPreferenceDefaults.ColumnSpacing),
            ) {
                row.forEach { item ->
                    CardButtonPreference(
                        title = item.title,
                        onClick = item.onClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        enabled = item.enabled,
                        colors = colors,
                        icon = item.icon,
                    )
                }
                repeat(columnCount - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/** Contains default values used by [CardButtonPreference] and [CardButtonPreferenceGroup]. */
object CardButtonPreferenceDefaults {

    /** The corner radius of a tile (COUI coui_component_card_button_bg corners 16dp). */
    val CornerRadius = 16.dp

    /** The padding inside a tile (COUI card button layout paddingHorizontal 8dp / paddingVertical 12dp). */
    val InsideMargin = PaddingValues(horizontal = 8.dp, vertical = 12.dp)

    /** The gap between the icon and the title (COUI android:id/title layout_marginTop 4dp). */
    val TitleTopMargin = 4.dp

    /** The title size (COUICardButtonPreference.CARD_BUTTON_TEXT_SIZE_NORMAL). */
    val TitleFontSize = 12.sp

    /** The title size when the normal size overflows (COUICardButtonPreference.CARD_BUTTON_TEXT_SIZE_SMALL). */
    val SmallTitleFontSize = 10.sp

    /** The gap between two columns (2 x COUI coui_component_card_button_horizontal_margin_inner). */
    val ColumnSpacing = 8.dp

    /** The gap above every row but the first (COUI coui_component_card_button_first_top_margin). */
    val RowSpacing = 16.dp

    /** The default number of columns of a [CardButtonPreferenceGroup]. */
    val Columns: Int = 4

    /**
     * The default [CardButtonPreferenceColors].
     *
     * @param containerColor The tile fill (COUI couiColorCardBackground).
     * @param titleColor The title color (COUI couiColorPrimaryNeutral).
     * @param disabledTitleColor The title color of a disabled tile.
     */
    @Composable
    fun cardButtonPreferenceColors(
        containerColor: Color = COUITheme.colorScheme.cardBackground,
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledTitleColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): CardButtonPreferenceColors = remember(containerColor, titleColor, disabledTitleColor) {
        CardButtonPreferenceColors(
            containerColor = containerColor,
            titleColor = titleColor,
            disabledTitleColor = disabledTitleColor,
        )
    }
}

/**
 * Colors used by a [CardButtonPreference].
 *
 * @param containerColor The fill of the tile.
 * @param titleColor The color of the title.
 * @param disabledTitleColor The color of the title of a disabled tile.
 */
@Immutable
data class CardButtonPreferenceColors(
    val containerColor: Color,
    val titleColor: Color,
    val disabledTitleColor: Color,
) {
    @Stable
    internal fun titleColor(enabled: Boolean): Color = if (enabled) titleColor else disabledTitleColor
}
