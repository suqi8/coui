// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils

/**
 * A list preference whose choices open on a full page, mirroring COUIActivityDialogPreference.
 *
 * COUIActivityDialogPreference is a COUIListPreference that COUIPreferenceFragment shows through
 * COUIActivityDialogFragment: a full-screen `Theme.COUI.ActivityDialog` window (opaque
 * couiColorBackgroundWithCard) that slides in from the end edge like an activity
 * (coui_open_slide_enter / coui_close_slide_exit, 350ms), with a COUIToolbar (back arrow + dialog
 * title) above a card list of single-choice rows. Tapping a row commits the value and closes the
 * page; the back arrow or the system back gesture closes it without changes.
 *
 * This library has no separate window for it, so the page is rendered as a full-size, undimmed
 * layer through [COUIPopupUtils.PopupLayout] (the same Scaffold-hosted layer the overlay popups use,
 * which also handles back navigation), containing a [SmallTopAppBar] and a [Card] of
 * [RadioButtonPreference] rows with the radio button at the end (coui_preference_listview_item.xml:
 * CheckedTextView with `?android:listChoiceIndicatorSingle` = coui_btn_radio as its end check mark).
 * A [io.github.suqi8.coui.kmp.basic.Scaffold] is therefore required. The preference row itself
 * uses the jump-arrow widget of Preference.COUI.DialogPreference (coui_preference_widget_jump), so
 * it is an [ArrowPreference] with the selected entry as assignment text.
 *
 * @param entries The list of [ListPreferenceEntry] to choose from.
 * @param selectedIndex The index of the selected entry, or -1 when nothing is selected.
 * @param onSelectedIndexChange The callback when an entry is selected on the page.
 * @param title The title of the [ActivityDialogPreference].
 * @param modifier The modifier to be applied to the [ActivityDialogPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [ActivityDialogPreference].
 * @param summaryColor The color of the summary.
 * @param dialogTitle The toolbar title of the page (COUI dialogTitle). Defaults to [title].
 * @param startAction The [Composable] content on the start side of the [ActivityDialogPreference].
 * @param bottomAction The [Composable] content at the bottom of the [ActivityDialogPreference].
 * @param insideMargin The margin inside the [ActivityDialogPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled Whether the [ActivityDialogPreference] is clickable.
 * @param showValue Whether to show the selected entry as trailing assignment text.
 * @param renderInRootScaffold Whether to render the page in the root (outermost) Scaffold.
 */
@Composable
fun ActivityDialogPreference(
    entries: List<ListPreferenceEntry>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dialogTitle: String = title,
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
) {
    val pageShown = remember { mutableStateOf(false) }
    val hasEntries = entries.isNotEmpty()
    val actualEnabled = enabled && hasEntries

    ArrowPreference(
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            val valueText = entries.getOrNull(selectedIndex)?.text
            if (showValue && !valueText.isNullOrBlank()) {
                Text(
                    text = valueText,
                    fontSize = COUITheme.textStyles.bodyS.fontSize,
                    color = if (actualEnabled) {
                        COUITheme.colorScheme.labelSecondary
                    } else {
                        COUITheme.colorScheme.disabledNeutral
                    },
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        onClick = { if (actualEnabled) pageShown.value = true },
        holdDownState = pageShown.value,
        enabled = actualEnabled,
    )

    val layoutDirection = LocalLayoutDirection.current
    val enterTransition = remember(layoutDirection) { activityOpenTransition(layoutDirection) }
    val exitTransition = remember(layoutDirection) { activityCloseTransition(layoutDirection) }
    COUIPopupUtils.PopupLayout(
        visible = pageShown,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        enableWindowDim = false,
        renderInRootScaffold = renderInRootScaffold,
        content = if (hasEntries) {
            {
                ActivityDialogPage(
                    entries = entries,
                    selectedIndex = selectedIndex,
                    onSelectedIndexChange = onSelectedIndexChange,
                    dialogTitle = dialogTitle,
                    pageShown = pageShown,
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun ActivityDialogPage(
    entries: List<ListPreferenceEntry>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    dialogTitle: String,
    pageShown: MutableState<Boolean>,
) {
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val backgroundColor = COUITheme.colorScheme.backgroundWithCard
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        SmallTopAppBar(
            title = dialogTitle,
            color = backgroundColor,
            navigationIcon = {
                IconButton(onClick = { pageShown.value = false }) {
                    Icon(
                        imageVector = BackArrow,
                        contentDescription = null,
                        tint = COUITheme.colorScheme.labelPrimary,
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                .padding(ActivityDialogPreferenceDefaults.ListContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                // CardListGroup draws the item_divider of coui_preference_listview_item.xml below every
                // row but the last (coui_preference_divider_default_horizontal_padding from both list
                // edges, i.e. from the title start to the card inset).
                CardListGroup {
                    entries.forEachIndexed { index, entry ->
                        item(key = index) {
                            RadioButtonPreference(
                                title = entry.text,
                                selected = index == selectedIndex,
                                onClick = {
                                    // COUIActivityDialogFragment: commit the clicked entry, then dismiss.
                                    currentOnSelectedIndexChange(index)
                                    pageShown.value = false
                                },
                                summary = entry.summary,
                                radioButtonLocation = RadioButtonLocation.End,
                                enabled = entry.enabled,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** coui_open_slide_enter: translate from 100% (-100% in RTL) to 0 over 350ms, path (0.3, 0.1, 0.3, 1). */
private fun activityOpenTransition(layoutDirection: LayoutDirection): EnterTransition = slideInHorizontally(
    animationSpec = tween(ActivityDialogPreferenceDefaults.TransitionDurationMillis, easing = OpenSlideEasing),
    initialOffsetX = { if (layoutDirection == LayoutDirection.Rtl) -it else it },
)

/** coui_close_slide_exit: translate from 0 to 100% (-100% in RTL) over 350ms, path (0.25, 0.1, 0.3, 1). */
private fun activityCloseTransition(layoutDirection: LayoutDirection): ExitTransition = slideOutHorizontally(
    animationSpec = tween(ActivityDialogPreferenceDefaults.TransitionDurationMillis, easing = CloseSlideEasing),
    targetOffsetX = { if (layoutDirection == LayoutDirection.Rtl) -it else it },
)

/** coui_open_slide_enter_translate_interpolator. */
private val OpenSlideEasing = CubicBezierEasing(0.3f, 0.1f, 0.3f, 1f)

/** coui_close_slide_exit_translate_interpolator. */
private val CloseSlideEasing = CubicBezierEasing(0.25f, 0.1f, 0.3f, 1f)

/**
 * coui_back_arrow_normal.xml: a 24dp arrow of three 1.4dp round-cap strokes, tinted
 * couiColorPrimaryNeutral and mirrored in RTL (the coui_back_arrow selector is autoMirrored).
 */
private val BackArrow: ImageVector by lazy {
    ImageVector.Builder("CouiBackArrow", 24.dp, 24.dp, 24f, 24f, autoMirror = true).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(10.1f, 4.4f)
            lineTo(2.5f, 12f)
            moveTo(2.5f, 12f)
            lineTo(10.1f, 19.6f)
            moveTo(2.5f, 12f)
            lineTo(21.5f, 12.001f)
        }
    }.build()
}

/** Contains default values used by [ActivityDialogPreference]. */
object ActivityDialogPreferenceDefaults {

    /**
     * The padding around the choice card: 16dp card-list margins (COUIPercentWidthListView
     * paddingType cardListType), 6dp below the toolbar (support_preference_listview_margin_top) and
     * 32dp at the bottom (coui_list_to_ex_bottom_padding).
     */
    val ListContentPadding = PaddingValues(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 32.dp)

    /**
     * The horizontal inset of the dividers between rows, inside the card: COUI
     * coui_preference_divider_default_horizontal_padding (32dp from the list edge) minus the 16dp
     * card margin.
     */
    val DividerHorizontalPadding: Dp = 16.dp

    /** The duration of the slide-in / slide-out page transition (coui_open_slide_enter / coui_close_slide_exit). */
    val TransitionDurationMillis: Int = 350
}
