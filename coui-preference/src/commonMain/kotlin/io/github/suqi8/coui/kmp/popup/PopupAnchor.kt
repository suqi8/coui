// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.popup

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Locks the hover layer of a list row on while the menu anchored to it is showing, as COUI 17
 * `COUIPopupListWindow.setAnchorHoveredState` does for an `IListSelectedItem` anchor (every
 * preference and card list row): `show` locks the row's `state_hovered`
 * (`COUIStateEffectDrawable.setStateLocked(STATE_HOVERED, true, true, true)`, so the
 * `couiColorHover` layer springs in on the response 0.3 spring of `COUIMaskEffectDrawable`) and
 * `dismiss` unlocks it as soon as the menu starts to leave, not when its exit animation ends.
 *
 * It emits a [HoverInteraction.Enter] into [interactionSource] while [locked] and the matching
 * [HoverInteraction.Exit] once it is not, so the row's indication draws its hover layer. A locked
 * state ignores hover exits in COUI, so when a mouse that hovered the row reports its own exit (the
 * menu now covers the row), the lock enters again; every enter it made is ended on release.
 *
 * @param locked Whether the menu anchored to the row is showing.
 * @param interactionSource The interaction source the row's indication reads.
 */
@Composable
internal fun PopupAnchorHoverLock(
    locked: Boolean,
    interactionSource: MutableInteractionSource,
) {
    val holder = remember(interactionSource) { PopupAnchorHoverHolder() }
    LaunchedEffect(locked, interactionSource) {
        if (!locked) {
            holder.release(interactionSource)
            return@LaunchedEffect
        }
        if (!holder.isHolding) holder.enter(interactionSource)
        interactionSource.interactions.collect { interaction ->
            if (interaction is HoverInteraction.Exit && !holder.owns(interaction.enter)) {
                holder.enter(interactionSource)
            }
        }
    }
    DisposableEffect(interactionSource) {
        onDispose { holder.releaseNow(interactionSource) }
    }
}

/** The [HoverInteraction.Enter]s a [PopupAnchorHoverLock] has emitted and not yet ended. */
private class PopupAnchorHoverHolder {
    private val enters = mutableListOf<HoverInteraction.Enter>()

    val isHolding: Boolean get() = enters.isNotEmpty()

    fun owns(enter: HoverInteraction.Enter): Boolean = enters.any { it === enter }

    suspend fun enter(source: MutableInteractionSource) {
        val enter = HoverInteraction.Enter()
        enters += enter
        source.emit(enter)
    }

    suspend fun release(source: MutableInteractionSource) {
        val ended = enters.toList()
        enters.clear()
        ended.forEach { source.emit(HoverInteraction.Exit(it)) }
    }

    fun releaseNow(source: MutableInteractionSource) {
        enters.forEach { source.tryEmit(HoverInteraction.Exit(it)) }
        enters.clear()
    }
}

/**
 * The click trigger of a click-select menu, after COUI 17 `COUIClickSelectMenu`: while the soft
 * keyboard is showing, a click hides it first (`InputMethodManager.hideSoftInputFromWindow`) and the
 * menu opens `support_menu_click_select_time` later — 100ms, or 240ms on a window at least 840dp tall
 * (`values-h840dp`) — so the menu is placed against the window the keyboard leaves behind instead of
 * jumping once it has gone. Without a keyboard the menu opens at once. A click that lands while an
 * opening is pending is ignored.
 *
 * @param onOpen Opens the menu.
 * @return The action that opens the menu, for the anchor's click.
 */
@Composable
internal fun rememberClickSelectMenuOpener(onOpen: () -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    // Read when the click lands, so they are held as state instead of keying the action.
    val currentOnOpen by rememberUpdatedState(onOpen)
    val currentKeyboardController by rememberUpdatedState(LocalSoftwareKeyboardController.current)
    val currentImeInsets by rememberUpdatedState(WindowInsets.ime)
    val currentDensity by rememberUpdatedState(LocalDensity.current)
    val currentWindowInfo by rememberUpdatedState(LocalWindowInfo.current)
    val pending = remember { ClickSelectPendingOpen() }
    return remember(scope, pending) {
        {
            if (pending.job?.isActive != true) {
                val keyboard = currentKeyboardController
                val imeShowing = currentImeInsets.getBottom(currentDensity) > 0
                if (keyboard != null && imeShowing) {
                    keyboard.hide()
                    val windowHeightDp = currentWindowInfo.containerSize.height / currentDensity.density
                    val delayMillis = if (windowHeightDp >= CLICK_SELECT_TALL_WINDOW_HEIGHT_DP) {
                        CLICK_SELECT_IME_DELAY_TALL_MILLIS
                    } else {
                        CLICK_SELECT_IME_DELAY_MILLIS
                    }
                    pending.job = scope.launch {
                        delay(delayMillis)
                        currentOnOpen()
                    }
                } else {
                    currentOnOpen()
                }
            }
        }
    }
}

/** The opening a [rememberClickSelectMenuOpener] has scheduled behind the keyboard, if any. */
private class ClickSelectPendingOpen {
    var job: Job? = null
}

/**
 * The color of the up-down arrow of a dropdown row (COUI 17 `coui_pop_up_next`): the normal and
 * pressed states draw `couiColorLabelTertiary`, the disabled state `couiColorLabelQuaternary`
 * (`coui_pop_up_next_disabled`).
 */
@Composable
@ReadOnlyComposable
internal fun dropdownArrowColor(enabled: Boolean): Color = if (enabled) {
    COUITheme.colorScheme.labelTertiary
} else {
    COUITheme.colorScheme.labelQuaternary
}

/**
 * The color of the selected value shown on a dropdown row (COUI 17
 * `coui_preference_secondary_text_color` of `TextAppearance.COUI.List.Assignment.End`):
 * `couiColorSecondNeutral` (the [io.github.suqi8.coui.kmp.theme.Colors.labelSecondary] value),
 * `couiColorLabelTertiary` when disabled.
 */
@Composable
@ReadOnlyComposable
internal fun dropdownValueColor(enabled: Boolean): Color = if (enabled) {
    COUITheme.colorScheme.labelSecondary
} else {
    COUITheme.colorScheme.labelTertiary
}

/**
 * The selected value shown before the arrow of a dropdown row: the COUI 17 `assignment` of
 * `coui_preference_withlittlewiget_assignment_in_right.xml` (the `COUIMenuPreference` layout) —
 * `TextAppearance.COUI.List.Assignment.End` (`couiTextAppearanceArticleBody`, 13sp with the body_s
 * line spacing), end aligned, at most two lines, then the 4dp start margin of the arrow
 * (`coui_preference_widget_menu.xml`, `support_preference_image_margin_start`).
 *
 * @param text The selected value.
 * @param color The text color, see [dropdownValueColor].
 */
@Composable
internal fun RowScope.DropdownValueText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(end = DropdownValueEndPadding)
            .align(Alignment.CenterVertically)
            .weight(1f, fill = false),
        style = COUITheme.textStyles.bodyS,
        color = color,
        textAlign = TextAlign.End,
        maxLines = DROPDOWN_VALUE_MAX_LINES,
        overflow = TextOverflow.Ellipsis,
    )
}

/** The line limit of the selected value on a dropdown row (COUI `assignment` maxLines). */
private const val DROPDOWN_VALUE_MAX_LINES = 2

/** The gap between the selected value and the arrow (COUI `support_preference_image_margin_start`). */
private val DropdownValueEndPadding = 4.dp

/** COUI `support_menu_click_select_time` (values). */
private const val CLICK_SELECT_IME_DELAY_MILLIS = 100L

/** COUI `support_menu_click_select_time` (values-h840dp). */
private const val CLICK_SELECT_IME_DELAY_TALL_MILLIS = 240L

/** The window height from which `values-h840dp` applies. */
private const val CLICK_SELECT_TALL_WINDOW_HEIGHT_DP = 840f
