// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.delay
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.basic.Search
import com.suqi8.coui.kmp.theme.LocalContentColor
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.hasFocusReassignBug

/**
 * A [SearchBar] component with COUI style.
 *
 * @param inputField the input field to input a query in the [SearchBar].
 * @param onExpandedChange the callback to be invoked when the [SearchBar]'s expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to the [SearchBar].
 * @param insideMargin The margin inside the [SearchBar].
 * @param expanded whether the [SearchBar] is expanded and showing search results.
 * @param outsideEndAction the action to be shown at the end side of the [SearchBar] when it is
 *   expanded.
 * @param content the content to be shown when the [SearchBar] is expanded.
 */
@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = SearchBarDefaults.InsideMargin,
    expanded: Boolean = false,
    outsideEndAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    val actionSlideDistancePx = with(LocalDensity.current) { ActionSlideDistance.roundToPx() }
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = insideMargin.height, horizontal = insideMargin.width),
            ) {
                inputField()
            }
            // COUISearchBar edit-state transition: the background resize runs for 450ms
            // (DEFAULT_SEARCH_VIEW_OFFSET/SCALE_CHANGE_DURATION), the functional button slides
            // 30dp (coui_search_bar_functional_button_offset_distance) over 400ms
            // (DEFAULT_SEARCH_VIEW_BUTTON_OFFSET_DURATION) and fades over 350ms
            // (DEFAULT_BUTTON_ALPHA_CHANGE_DURATION), all with COUIMoveEaseInterpolator.
            if (outsideEndAction != null) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandHorizontally(animationSpec = tween(BackgroundResizeDurationMillis, easing = MoveEase)) +
                        slideInHorizontally(
                            animationSpec = tween(ActionOffsetDurationMillis, easing = MoveEase),
                            initialOffsetX = { actionSlideDistancePx },
                        ) +
                        fadeIn(animationSpec = tween(ActionAlphaDurationMillis, easing = MoveEase)),
                    exit = shrinkHorizontally(animationSpec = tween(BackgroundResizeDurationMillis, easing = MoveEase)) +
                        slideOutHorizontally(
                            animationSpec = tween(ActionOffsetDurationMillis, easing = MoveEase),
                            targetOffsetX = { actionSlideDistancePx },
                        ) +
                        fadeOut(animationSpec = tween(ActionAlphaDurationMillis, easing = MoveEase)),
                ) {
                    // COUISearchBar functional button: laid out at backgroundRect.right +
                    // coui_search_bar_functional_button_start_gap (8dp), with a 12dp internal
                    // ripple text padding (text_ripple_bg_padding_horizontal) -> 20dp visual gap
                    // from the capsule. The input field margin already contributes 16dp, so add
                    // 4dp more. End side: coui_search_bar_functional_button_end_gap_compat (4dp)
                    // + 12dp ripple padding -> the text sits 16dp from the container edge.
                    Box(
                        modifier = Modifier.padding(
                            start = ActionExtraStartPadding,
                            end = ActionEndPadding,
                        ),
                    ) {
                        outsideEndAction?.invoke()
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
        ) {
            content()
        }
    }

    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = expanded,
        onBackCompleted = {
            currentOnExpandedChange(false)
        },
    )
}

/**
 * A text field to input a query in a search bar with COUI style.
 *
 * @param query the query text to be shown in the input field.
 * @param onQueryChange the callback to be invoked when the input service updates the query. An
 *   updated text comes as a parameter of the callback.
 * @param onSearch the callback to be invoked when the input service triggers the
 *   [ImeAction.Search] action. The current [query] comes as a parameter of the callback.
 * @param expanded whether the search bar is expanded and showing search results.
 * @param onExpandedChange the callback to be invoked when the search bar's expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to this input field.
 * @param label the label to be shown when the input field is not focused.
 * @param enabled the enabled state of this input field. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param textStyle Style configuration that applies at character level such as color, font etc.
 * @param colors [SearchBarColors] that will be used to resolve the colors used for this input
 *   field in different states. See [SearchBarDefaults.searchBarColors].
 * @param leadingIcon the leading icon to be displayed at the start of the input field.
 * @param trailingIcon the trailing icon to be displayed at the end of the input field.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this input field. You can use this to change the search bar's
 *   appearance or preview the search bar in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 */
@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle? = null,
    colors: SearchBarColors = SearchBarDefaults.searchBarColors(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    val currentOnSearch by rememberUpdatedState(onSearch)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    // COUISearchBarDrawingProxyDrawable.setBackgroundRect always uses a corner radius of
    // rect.height() / 2 (a capsule; smooth round rect on OS16). The fixed 18dp radius
    // (coui_search_view_corner / coui_searchview_corner_rect_bg) belongs to the legacy
    // COUISearchViewAnimate only.
    val capsuleShape = CircleShape

    val actualLeadingIcon = leadingIcon ?: {
        // coui_search_view_icon: 20dp glyph filled with couiColorSecondNeutral
        // (#8A000000 light / #8AFFFFFF dark).
        Icon(
            modifier = Modifier.padding(start = SearchBarDefaults.LeadingIconStartPadding, end = SearchBarDefaults.LeadingIconEndPadding),
            imageVector = COUIIcons.Basic.Search,
            tint = colors.iconColor,
            contentDescription = "Search",
        )
    }

    val actualTrailingIcon = trailingIcon ?: {
        AnimatedVisibility(
            visible = query.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            // Clear button: a 36dp circular touch target (coui_search_bar_inner_search_icon_size)
            // holding an 18dp filled circle with a white cross, matching
            // ic_edit_text_delete_search_view (36dp vector, 9dp-radius circle filled with
            // couiColorHintNeutral #42000000 light / #4DFFFFFF dark).
            val clearCircleColor = colors.clearIconColor
            Box(
                modifier = Modifier
                    .padding(start = SearchBarDefaults.TrailingIconStartPadding, end = SearchBarDefaults.TrailingIconEndPadding)
                    .size(SearchBarDefaults.TrailingIconTouchTargetSize)
                    .clip(capsuleShape)
                    .clickable { currentOnQueryChange("") }
                    .semantics { contentDescription = "Search Cleanup" },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(SearchBarDefaults.TrailingIconVisualSize)) {
                    drawCircle(color = clearCircleColor)
                    // Cross geometry from ic_edit_text_delete_search_view: 8.2dp span,
                    // 1.6dp stroke, butt caps, always white (no night variant in COUI).
                    val half = 4.1.dp.toPx()
                    val stroke = 1.6.dp.toPx()
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - half, center.y - half),
                        end = Offset(center.x + half, center.y + half),
                        strokeWidth = stroke,
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - half, center.y + half),
                        end = Offset(center.x + half, center.y - half),
                        strokeWidth = stroke,
                    )
                }
            }
        }
    }

    val focused = internalInteractionSource.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val textAlpha = remember { Animatable(1f) }

    val textColor = LocalContentColor.current
    // COUI input text: 16sp regular (couiEditTextTextAppearance = couiTextAppearanceHeadline6
    // 16sp with fontFamily overridden to sans-serif-regular).
    val inputTextStyle = COUITheme.textStyles.main
        .copy(fontSize = SearchBarDefaults.InputFieldFontSize)
        .merge(textStyle)
        .copy(color = textColor)

    val cursorBrush = SolidColor(COUITheme.colorScheme.primary)
    val labelText by remember(query, expanded, label) {
        derivedStateOf { if (!(query.isNotEmpty() || expanded)) label else "" }
    }

    // On API 26-27, focus is incorrectly reassigned after clearFocus(), preventing the
    // SearchBar from closing. Workaround: disable the TextField when collapsed and use
    // pointerInput to handle tap-to-expand. https://issuetracker.google.com/issues/433382598
    val workaroundEnabled = !hasFocusReassignBug || expanded
    val expandOnTapModifier = if (workaroundEnabled || !enabled) {
        Modifier
    } else {
        Modifier.pointerInput(Unit) { detectTapGestures { currentOnExpandedChange(true) } }
    }

    BasicTextField(
        value = query,
        onValueChange = currentOnQueryChange,
        modifier = modifier
            .then(expandOnTapModifier)
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) currentOnExpandedChange(true) }
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled && workaroundEnabled,
        singleLine = true,
        textStyle = inputTextStyle,
        cursorBrush = cursorBrush,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { currentOnSearch(query) }),
        interactionSource = internalInteractionSource,
        decorationBox = { innerTextField ->
            // COUISearchBar capsule: coui_search_view_selector_color_normal, a translucent
            // 8% black (#14000000) in light and 10% white (#1AFFFFFF) in dark — NOT a solid
            // gray. The page background shows through it on real devices.
            Box(
                modifier = Modifier
                    .background(
                        color = colors.backgroundColor,
                        shape = capsuleShape,
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actualLeadingIcon()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = SearchBarDefaults.InputFieldMinHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        val mergedLabelStyle = remember(textStyle) {
                            TextStyle(fontSize = SearchBarDefaults.InputFieldFontSize).merge(textStyle)
                        }
                        // Hint text: couiColorLabelSecondary (#8A000000 light / #8AFFFFFF dark),
                        // from Widget.COUI.EditText android:textColorHint.
                        Text(
                            text = labelText,
                            style = mergedLabelStyle,
                            color = colors.labelColor,
                        )
                        Box(modifier = Modifier.graphicsLayer { alpha = textAlpha.value }) {
                            innerTextField()
                        }
                    }
                    actualTrailingIcon()
                }
            }
        },
    )

    LaunchedEffect(expanded) {
        if (expanded) {
            // Explicitly request focus when expanded. On API 26-27, the workaround disables
            // the TextField when collapsed, so the initial tap doesn't grant focus — this
            // ensures the keyboard appears after the TextField becomes enabled again.
            focusRequester.requestFocus()
        } else if (focused) {
            delay(100)
            if (query.isNotEmpty()) {
                textAlpha.animateTo(0f)
                currentOnQueryChange("")
                textAlpha.snapTo(1f)
            }
            focusManager.clearFocus()
        }
    }
}

/** COUIMoveEaseInterpolator: PathInterpolator(0.3f, 0f, 0.1f, 1f). */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISearchBar DEFAULT_SEARCH_VIEW_OFFSET/SCALE_CHANGE_DURATION = 450ms. */
private val BackgroundResizeDurationMillis = 450

/** COUISearchBar DEFAULT_SEARCH_VIEW_BUTTON_OFFSET_DURATION = 400ms. */
private val ActionOffsetDurationMillis = 400

/** COUISearchBar DEFAULT_BUTTON_ALPHA_CHANGE_DURATION = 350ms. */
private val ActionAlphaDurationMillis = 350

/** COUISearchBar coui_search_bar_functional_button_offset_distance = 30dp. */
private val ActionSlideDistance = 30.dp

/**
 * Extra start padding for the outside end action. COUISearchBar lays out the functional button at
 * backgroundRect.right + coui_search_bar_functional_button_start_gap (8dp) and the button text has
 * a 12dp ripple padding (text_ripple_bg_padding_horizontal), giving a 20dp visual gap from the
 * capsule; the input field margin already provides 16dp, so 4dp remains.
 */
private val ActionExtraStartPadding = 4.dp

/**
 * End padding for the outside end action: coui_search_bar_functional_button_end_gap_compat (4dp) +
 * 12dp ripple text padding = the action text sits 16dp from the container edge.
 */
private val ActionEndPadding = 16.dp

/** Contains default values used by [SearchBar] and [InputField]. */
object SearchBarDefaults {
    /**
     * The default inside margin of the [SearchBar].
     * Horizontal: coui_search_bar_responsive_horizontal_padding_compat = 16dp.
     * Vertical: (coui_search_view_height=52dp - coui_search_bar_normal_background_height=40dp) / 2 = 6dp.
     */
    val InsideMargin = DpSize(16.dp, 6.dp)

    /**
     * The default minimum height of the [InputField] background capsule.
     * Matches coui_search_bar_normal_background_height = 40dp.
     */
    val InputFieldMinHeight = 40.dp

    /**
     * The default font size for the [InputField] text and label.
     * Matches coui_search_view_input_text_size = 16sp (couiEditTextTextAppearance is also 16sp).
     */
    val InputFieldFontSize = 16.sp

    /**
     * The start padding for the default leading icon (20dp visual glyph).
     * COUISearchBar places a 36dp icon container (coui_search_bar_inner_search_icon_size) at
     * coui_search_bar_inner_search_icon_start_gap = 4dp from the background edge and centers the
     * 20dp glyph (coui_search_view_icon) inside: 4 + (36 - 20) / 2 = 12dp.
     */
    val LeadingIconStartPadding = 12.dp

    /**
     * The end padding for the default leading icon.
     * The edit frame starts right after the 36dp icon container, so the gap between the 20dp
     * glyph and the text is (36 - 20) / 2 = 8dp (equals coui_search_view_hint_padding_start).
     */
    val LeadingIconEndPadding = 8.dp

    /**
     * The start padding for the default trailing (clear) button touch target.
     * Matches coui_search_bar_edittext_end_gap = 4dp.
     */
    val TrailingIconStartPadding = 4.dp

    /**
     * The end padding for the default trailing (clear) button touch target.
     * Matches coui_search_bar_non_instant_search_inner_gap = 4dp.
     */
    val TrailingIconEndPadding = 4.dp

    /**
     * The circular touch target size of the default trailing (clear) button.
     * Matches coui_search_bar_inner_search_icon_size = 36dp.
     */
    val TrailingIconTouchTargetSize = 36.dp

    /**
     * The visual circle size of the default trailing (clear) button.
     * Matches the 9dp-radius circle of ic_edit_text_delete_search_view (36dp vector).
     */
    val TrailingIconVisualSize = 18.dp

    /**
     * Creates a [SearchBarColors] with the default colors of the [InputField].
     *
     * COUISearchBar references:
     * - backgroundColor: coui_search_view_selector_color_normal = #14000000 light / #1AFFFFFF
     *   dark (translucent, the page background shows through). The secondaryContainer slot is
     *   exact in light; dark (#26FFFFFF) is the closest theme slot.
     * - labelColor (hint): couiColorLabelSecondary = #8A000000 / #8AFFFFFF (exact).
     * - iconColor (search glyph): couiColorSecondNeutral = #8A000000 / #8AFFFFFF (exact).
     * - clearIconColor (clear circle fill): couiColorHintNeutral = #42000000 / #4DFFFFFF (exact).
     */
    @Composable
    fun searchBarColors(
        backgroundColor: Color = COUITheme.colorScheme.secondaryContainer,
        labelColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        iconColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        clearIconColor: Color = COUITheme.colorScheme.onSurfaceContainerHigh,
    ): SearchBarColors = remember(backgroundColor, labelColor, iconColor, clearIconColor) {
        SearchBarColors(
            backgroundColor = backgroundColor,
            labelColor = labelColor,
            iconColor = iconColor,
            clearIconColor = clearIconColor,
        )
    }
}

/**
 * Colors used by the [InputField] of a [SearchBar].
 *
 * @param backgroundColor the color of the capsule background.
 * @param labelColor the color of the label (hint) text.
 * @param iconColor the tint of the default leading search icon.
 * @param clearIconColor the fill color of the default clear button circle.
 */
@Immutable
data class SearchBarColors(
    val backgroundColor: Color,
    val labelColor: Color,
    val iconColor: Color,
    val clearIconColor: Color,
)
