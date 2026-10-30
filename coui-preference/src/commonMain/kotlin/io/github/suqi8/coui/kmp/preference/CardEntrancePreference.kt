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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.PressFeedbackType

/**
 * The layout of a [CardEntranceItem], mirroring COUICardEntrancePreference entranceCardType.
 */
enum class CardEntranceType {
    /**
     * CARD_TYPE_SMALL (coui_component_card_entrance_preference_type_small.xml): a half-width card
     * with the icon on top and the title / summary below it. Two small cards share a row.
     */
    Small,

    /**
     * CARD_TYPE_LARGE (coui_component_card_entrance_preference_type_large.xml): a full-width card
     * with the icon on the start side and the title / summary next to it.
     */
    Large,
}

/**
 * An entry of a [CardEntrancePreference], mirroring one COUICardEntrancePreference.
 *
 * @param title The title of the card.
 * @param summary The summary below the title. Hidden when null (COUI showSummary = false).
 * @param type The [CardEntranceType] of the card.
 * @param icon The icon of the card (COUI android:icon in a COUIRoundImageView).
 * @param statusOn Whether the summary reads as an "on" status, drawn in the accent color
 *   (COUICardEntrancePreference.setSummaryStatus: couiColorPrimaryText instead of couiColorSecondNeutral).
 * @param enabled Whether the card can be clicked.
 * @param onClick The callback when the card is clicked. A `null` value renders an inert card.
 */
@Stable
data class CardEntranceItem(
    val title: String,
    val summary: String? = null,
    val type: CardEntranceType = CardEntranceType.Small,
    val icon: (@Composable () -> Unit)? = null,
    val statusOn: Boolean = false,
    val enabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
)

/**
 * A grid of entrance cards, mirroring a run of COUICardEntrancePreference items laid out by
 * `CardEntranceSpanSizeLookup` + `CardEntranceDecoration`.
 *
 * COUI places entrance preferences in a two-span grid: a [CardEntranceType.Small] card takes one
 * span, a [CardEntranceType.Large] card both. Every card is its own rounded tile
 * (couiColorCardBackground, 12dp small / 16dp large corners) with the `COUIPressFeedbackHelper`
 * card press scale (COUIPressFeedbackJumpPreference), so press feedback is a scale, not a tint.
 * Small cards in one row share its height (GridLayoutManager stretches the row). The two columns
 * are 8dp apart (2 x coui_component_card_entrance_small_horizontal_margin_inner); a row of small
 * cards starts 8dp (coui_component_card_entrance_small_top_margin_other) and a large card 16dp
 * (coui_component_card_entrance_large_top_margin) below the previous row. The first row carries no
 * top margin here, and the 16dp screen-edge margins (…_horizontal_margin_outer) are left to the
 * caller, like any other card block.
 *
 * @param items The entrance cards in display order. Build the list inside a `remember` block to
 *   keep it stable across recompositions.
 * @param modifier The modifier to be applied to the [CardEntrancePreference].
 * @param colors The [CardEntrancePreferenceColors] of the cards.
 */
@Composable
fun CardEntrancePreference(
    items: List<CardEntranceItem>,
    modifier: Modifier = Modifier,
    colors: CardEntrancePreferenceColors = CardEntrancePreferenceDefaults.cardEntrancePreferenceColors(),
) {
    if (items.isEmpty()) return
    val rows = remember(items) { cardEntranceRows(items) }
    Column(modifier = modifier.fillMaxWidth()) {
        rows.forEachIndexed { rowIndex, row ->
            if (rowIndex > 0) {
                val gap = if (row.first().type == CardEntranceType.Large) {
                    CardEntrancePreferenceDefaults.LargeRowSpacing
                } else {
                    CardEntrancePreferenceDefaults.SmallRowSpacing
                }
                Spacer(Modifier.height(gap))
            }
            val first = row.first()
            if (first.type == CardEntranceType.Large) {
                LargeEntranceCard(item = first, colors = colors, modifier = Modifier.fillMaxWidth())
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(CardEntrancePreferenceDefaults.ColumnSpacing),
                ) {
                    row.forEach { item ->
                        SmallEntranceCard(
                            item = item,
                            colors = colors,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                    if (row.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Splits [items] into grid rows the way a two-span GridLayoutManager fills them: small cards pair
 * up, a large card always starts and ends its own row.
 */
private fun cardEntranceRows(items: List<CardEntranceItem>): List<List<CardEntranceItem>> {
    val rows = mutableListOf<List<CardEntranceItem>>()
    var pending: CardEntranceItem? = null
    for (item in items) {
        if (item.type == CardEntranceType.Large) {
            pending?.let { rows += listOf(it) }
            pending = null
            rows += listOf(item)
        } else {
            val waiting = pending
            if (waiting == null) {
                pending = item
            } else {
                rows += listOf(waiting, item)
                pending = null
            }
        }
    }
    pending?.let { rows += listOf(it) }
    return rows
}

@Composable
private fun EntranceCard(
    item: CardEntranceItem,
    cornerRadius: Dp,
    insideMargin: PaddingValues,
    colors: CardEntrancePreferenceColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val onClick = item.onClick
    val clickable = item.enabled && onClick != null
    Card(
        modifier = if (clickable) modifier.semantics { role = Role.Button } else modifier,
        cornerRadius = cornerRadius,
        insideMargin = insideMargin,
        colors = CardDefaults.defaultColors(color = colors.containerColor),
        // COUIPressFeedbackJumpPreference: COUIPressFeedbackHelper(view, CARD_PRESS_FEEDBACK).
        pressFeedbackType = if (clickable) PressFeedbackType.Sink else PressFeedbackType.None,
        onClick = if (clickable) onClick else null,
    ) {
        content()
    }
}

@Composable
private fun SmallEntranceCard(
    item: CardEntranceItem,
    colors: CardEntrancePreferenceColors,
    modifier: Modifier = Modifier,
) {
    EntranceCard(
        item = item,
        cornerRadius = CardEntrancePreferenceDefaults.SmallCornerRadius,
        insideMargin = CardEntrancePreferenceDefaults.SmallInsideMargin,
        colors = colors,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            item.icon?.let { icon ->
                Box(modifier = Modifier.align(Alignment.Start)) { icon() }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    modifier = Modifier.padding(top = CardEntrancePreferenceDefaults.SmallTitleTopMargin),
                    style = COUITheme.textStyles.headline2,
                    color = colors.titleColor(item.enabled),
                )
                item.summary?.let { summary ->
                    Text(
                        text = summary,
                        modifier = Modifier.padding(top = CardEntrancePreferenceDefaults.SmallSummaryTopMargin),
                        style = COUITheme.textStyles.footnote2,
                        color = colors.summaryColor(item.enabled, item.statusOn),
                    )
                }
            }
        }
    }
}

@Composable
private fun LargeEntranceCard(
    item: CardEntranceItem,
    colors: CardEntrancePreferenceColors,
    modifier: Modifier = Modifier,
) {
    EntranceCard(
        item = item,
        cornerRadius = CardEntrancePreferenceDefaults.LargeCornerRadius,
        insideMargin = CardEntrancePreferenceDefaults.LargeInsideMargin,
        colors = colors,
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            item.icon?.let { icon ->
                icon()
                Spacer(Modifier.width(CardEntrancePreferenceDefaults.LargeIconSpacing))
            }
            Column {
                Text(
                    text = item.title,
                    style = COUITheme.textStyles.headline2,
                    color = colors.titleColor(item.enabled),
                )
                item.summary?.let { summary ->
                    Text(
                        text = summary,
                        modifier = Modifier.padding(top = CardEntrancePreferenceDefaults.LargeSummaryTopMargin),
                        style = COUITheme.textStyles.footnote2,
                        color = colors.summaryColor(item.enabled, item.statusOn),
                    )
                }
            }
        }
    }
}

object CardEntrancePreferenceDefaults {

    /** The corner radius of a small card (COUI coui_component_card_entrance_small_bg: 12dp). */
    val SmallCornerRadius = 12.dp

    /** The corner radius of a large card (COUI coui_component_card_entrance_large_bg: 16dp). */
    val LargeCornerRadius = 16.dp

    /** The padding inside a small card (COUI type_small layout android:padding 16dp). */
    val SmallInsideMargin = PaddingValues(16.dp)

    /** The padding inside a large card (COUI type_large layout paddingStart 16dp / paddingEnd 12dp / paddingVertical 20dp). */
    val LargeInsideMargin = PaddingValues(start = 16.dp, end = 12.dp, top = 20.dp, bottom = 20.dp)

    /** The gap between the icon and the title of a small card (COUI type_small title marginTop). */
    val SmallTitleTopMargin = 12.dp

    /** The gap between the title and the summary of a small card (COUI type_small summary marginTop). */
    val SmallSummaryTopMargin = 4.dp

    /** The gap between the icon and the texts of a large card (COUI type_large text block marginStart). */
    val LargeIconSpacing = 16.dp

    /** The gap between the title and the summary of a large card (COUI type_large summary marginTop). */
    val LargeSummaryTopMargin = 3.dp

    /**
     * The gap between the two columns
     * (2 x COUI coui_component_card_entrance_small_horizontal_margin_inner).
     */
    val ColumnSpacing = 8.dp

    /** The gap above a row of small cards (COUI coui_component_card_entrance_small_top_margin_other). */
    val SmallRowSpacing = 8.dp

    /** The gap above a large card (COUI coui_component_card_entrance_large_top_margin). */
    val LargeRowSpacing = 16.dp

    /**
     * The default [CardEntrancePreferenceColors].
     *
     * @param containerColor The card fill (COUI couiColorCardBackground).
     * @param titleColor The title color (COUI couiColorPrimaryNeutral).
     * @param summaryColor The summary color (COUI couiColorSecondNeutral).
     * @param statusOnSummaryColor The summary color of a status-on card (COUI couiColorPrimaryText).
     * @param disabledColor The title / summary color of a disabled card.
     */
    @Composable
    fun cardEntrancePreferenceColors(
        containerColor: Color = COUITheme.colorScheme.surfaceContainer,
        titleColor: Color = COUITheme.colorScheme.onSurface,
        summaryColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        statusOnSummaryColor: Color = COUITheme.colorScheme.primary,
        disabledColor: Color = COUITheme.colorScheme.disabledOnSecondaryVariant,
    ): CardEntrancePreferenceColors = remember(containerColor, titleColor, summaryColor, statusOnSummaryColor, disabledColor) {
        CardEntrancePreferenceColors(
            containerColor = containerColor,
            titleColor = titleColor,
            summaryColor = summaryColor,
            statusOnSummaryColor = statusOnSummaryColor,
            disabledColor = disabledColor,
        )
    }
}

/**
 * Colors used by a [CardEntrancePreference].
 *
 * @param containerColor The fill of every card.
 * @param titleColor The color of the titles.
 * @param summaryColor The color of the summaries.
 * @param statusOnSummaryColor The color of the summary of a [CardEntranceItem.statusOn] card.
 * @param disabledColor The title / summary color of a disabled card.
 */
@Immutable
data class CardEntrancePreferenceColors(
    val containerColor: Color,
    val titleColor: Color,
    val summaryColor: Color,
    val statusOnSummaryColor: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun titleColor(enabled: Boolean): Color = if (enabled) titleColor else disabledColor

    @Stable
    internal fun summaryColor(enabled: Boolean, statusOn: Boolean): Color = when {
        !enabled -> disabledColor
        statusOn -> statusOnSummaryColor
        else -> summaryColor
    }
}
