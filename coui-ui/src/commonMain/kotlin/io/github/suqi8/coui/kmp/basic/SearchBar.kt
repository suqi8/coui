// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.hasFocusReassignBug
import kotlinx.coroutines.delay

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
            // COUISearchBar edit-state transition: background resize, button slide and fade,
            // all with COUIMoveEaseInterpolator.
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
 * @param collapsePercent how far through its scroll-driven collapse the bar is: 1 fully expanded,
 *   0 flattened to a divider line. See [searchBarCollapsePercent] for turning a scroll offset into
 *   this value, and [searchBarCollapseGeometry] for what it drives.
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
    collapsePercent: Float = 1f,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    val currentOnSearch by rememberUpdatedState(onSearch)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    // SearchBarBackgroundView.updateSmoothRoundPath: radius = height / 2 at the explicit weight 1.0,
    // the COUI 17 soft capsule.
    val capsuleShape = if (isSquircleEnabled()) {
        remember { SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight) }
    } else {
        CircleShape
    }

    val actualLeadingIcon = leadingIcon ?: {
        // coui_search_view_icon: 20dp glyph filled with couiColorSecondNeutral.
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
            // Clear button: a 36dp circular touch target holding an 18dp filled circle with a
            // white cross (ic_edit_text_delete_search_view).
            val clearCircleColor = colors.clearIconColor
            Box(
                modifier = Modifier
                    .padding(start = SearchBarDefaults.TrailingIconStartPadding, end = SearchBarDefaults.TrailingIconEndPadding)
                    .size(SearchBarDefaults.TrailingIconTouchTargetSize)
                    .clip(CircleShape)
                    .clickable { currentOnQueryChange("") }
                    .semantics { contentDescription = "Search Cleanup" },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(SearchBarDefaults.TrailingIconVisualSize)) {
                    drawCircle(color = clearCircleColor)
                    // ic_edit_text_delete_search_view cross: 8.2dp span, 1.6dp stroke, always white.
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
    // COUI input text: 16sp regular (couiEditTextTextAppearance).
    val inputTextStyle = COUITheme.textStyles.main
        .copy(fontSize = SearchBarDefaults.InputFieldFontSize)
        .merge(textStyle)
        .copy(color = textColor)

    val cursorBrush = SolidColor(COUITheme.colorScheme.primary)
    // Null while fully expanded, so an unused collapse costs nothing and the modifier chain stays
    // identical to what it was before this parameter existed.
    val dividerColor = COUITheme.colorScheme.dividerLine
    val collapse = if (collapsePercent >= 1f) {
        null
    } else {
        remember(collapsePercent, colors.backgroundColor, dividerColor) {
            searchBarCollapseGeometry(
                percent = collapsePercent,
                normalColor = colors.backgroundColor,
                dividerColor = dividerColor,
            )
        }
    }
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
            // COUISearchBar capsule: coui_search_view_selector_color_normal (translucent, the
            // page background shows through).
            Box(
                modifier = Modifier
                    .then(
                        if (collapse == null) {
                            Modifier
                        } else {
                            // The capsule shrinks and simultaneously widens past its own edges,
                            // tinting to the divider color, so a fully collapsed bar reads as the
                            // list's divider line.
                            Modifier
                                .height(collapse.capsuleHeight)
                                .layout { measurable, constraints ->
                                    val extra = collapse.extraHorizontalWidth.roundToPx()
                                    val placeable = measurable.measure(
                                        constraints.offset(horizontal = extra * 2),
                                    )
                                    // Report the resting width so the row keeps its place; the
                                    // capsule simply draws wider than the space it occupies.
                                    layout(placeable.width - extra * 2, placeable.height) {
                                        placeable.place(-extra, 0)
                                    }
                                }
                        },
                    )
                    .background(
                        color = collapse?.capsuleColor ?: colors.backgroundColor,
                        shape = capsuleShape,
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (collapse == null) {
                                Modifier
                            } else {
                                Modifier.graphicsLayer { alpha = collapse.contentAlpha }
                            },
                        ),
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
                        // Hint text: couiColorLabelSecondary (Widget.COUI.EditText textColorHint).
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

/** Contains default values used by [SearchBar] and [InputField]. */
object SearchBarDefaults {
    /**
     * The default inside margin of the [SearchBar].
     * COUI `coui_search_bar_responsive_horizontal_padding_compat` (16dp) x (52dp - 40dp) / 2.
     */
    val InsideMargin = DpSize(16.dp, 6.dp)

    /**
     * The default minimum height of the [InputField] background capsule.
     * COUI `coui_search_bar_normal_background_height` (40dp).
     */
    val InputFieldMinHeight = 40.dp

    /**
     * The default font size for the [InputField] text and label.
     * COUI `coui_search_view_input_text_size` (15sp, via `inputTextSize` on the search bar style).
     */
    val InputFieldFontSize = 15.sp

    /**
     * The start padding for the default leading icon: the COUI 4dp icon container start gap plus
     * (36dp container - 20dp glyph) / 2.
     */
    val LeadingIconStartPadding = 12.dp

    /**
     * The end padding for the default leading icon: (36dp container - 20dp glyph) / 2, equal to
     * COUI `coui_search_view_hint_padding_start`.
     */
    val LeadingIconEndPadding = 8.dp

    /**
     * The start padding for the default trailing (clear) button touch target.
     * COUI `coui_search_bar_edittext_end_gap` (4dp).
     */
    val TrailingIconStartPadding = 4.dp

    /**
     * The end padding for the default trailing (clear) button touch target.
     * COUI `coui_search_bar_non_instant_search_inner_gap` (4dp).
     */
    val TrailingIconEndPadding = 4.dp

    /**
     * The circular touch target size of the default trailing (clear) button.
     * COUI `coui_search_bar_inner_search_icon_size` (36dp).
     */
    val TrailingIconTouchTargetSize = 36.dp

    /**
     * The font size of the [CancelButton] label. COUI `couiTextAppearanceButton`, the style
     * `initFunctionButton` applies to the functional button.
     */
    val CancelFontSize = 14.sp

    /**
     * The horizontal padding of the [CancelButton] label. COUI
     * `text_ripple_bg_padding_horizontal`, forced by `COUITextViewCompatUtil.setPressRippleDrawable`
     * so the label never sits flush against the window edge.
     */
    val CancelPaddingHorizontal = 12.dp

    /** The vertical padding of the [CancelButton] label. COUI `text_ripple_bg_padding_vertical`. */
    val CancelPaddingVertical = 4.dp

    /** The widest a [CancelButton] may grow. COUI `coui_search_bar_functional_button_max_width`. */
    val CancelMaxWidth = 152.dp

    /**
     * The visual circle size of the default trailing (clear) button, matching the 9dp-radius
     * circle of ic_edit_text_delete_search_view.
     */
    val TrailingIconVisualSize = 18.dp

    /**
     * Creates a [SearchBarColors] with the default colors of the [InputField], following
     * COUISearchBar (`coui_search_view_selector_color_normal` capsule, couiColorLabelSecondary
     * hint, couiColorSecondNeutral glyph, couiColorHintNeutral clear circle).
     */
    @Composable
    fun searchBarColors(
        backgroundColor: Color = COUITheme.colorScheme.secondaryContainer,
        labelColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        iconColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        clearIconColor: Color = COUITheme.colorScheme.onSurfaceContainerHigh,
        // COUI coui_searchview_cancel_button_color resolves to ?couiColorPrimaryText.
        cancelTextColor: Color = COUITheme.colorScheme.primary,
    ): SearchBarColors = remember(backgroundColor, labelColor, iconColor, clearIconColor, cancelTextColor) {
        SearchBarColors(
            backgroundColor = backgroundColor,
            labelColor = labelColor,
            iconColor = iconColor,
            clearIconColor = clearIconColor,
            cancelTextColor = cancelTextColor,
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
 * @param cancelTextColor the color of the [CancelButton] label.
 */
@Immutable
data class SearchBarColors(
    val backgroundColor: Color,
    val labelColor: Color,
    val iconColor: Color,
    val clearIconColor: Color,
    val cancelTextColor: Color,
)

/**
 * The trailing button a COUI search bar shows while expanded.
 *
 * COUI `initFunctionButton` styles it with `couiTextAppearanceButton` and forces the ripple
 * paddings, so the label keeps a 12dp visual inset from the window edge. It appears as soon as the
 * bar expands, whether or not a query has been typed, and the class deliberately leaves the click
 * unwired — the host decides. Settings collapses the bar, and collapsing clears the query, so the
 * net effect there is clear + collapse + hide the keyboard.
 *
 * @param onClick the callback invoked when the button is clicked.
 * @param modifier the [Modifier] to be applied to the button.
 * @param text the label of the button.
 * @param enabled whether the button responds to clicks.
 * @param colors the [SearchBarColors] resolving the label color.
 */
@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Cancel",
    enabled: Boolean = true,
    colors: SearchBarColors = SearchBarDefaults.searchBarColors(),
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val labelColor = if (enabled) {
        colors.cancelTextColor
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }
    Text(
        text = text,
        modifier = modifier
            .widthIn(max = SearchBarDefaults.CancelMaxWidth)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = null,
                onClick = { currentOnClick() },
            )
            .padding(
                horizontal = SearchBarDefaults.CancelPaddingHorizontal,
                vertical = SearchBarDefaults.CancelPaddingVertical,
            ),
        color = labelColor,
        fontSize = SearchBarDefaults.CancelFontSize,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
    )
}

/** COUIMoveEaseInterpolator: PathInterpolator(0.3f, 0f, 0.1f, 1f). */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISearchBar DEFAULT_SEARCH_VIEW_OFFSET/SCALE_CHANGE_DURATION (450ms). */
private val BackgroundResizeDurationMillis = 450

/** COUISearchBar DEFAULT_SEARCH_VIEW_BUTTON_OFFSET_DURATION (400ms). */
private val ActionOffsetDurationMillis = 400

/** COUISearchBar DEFAULT_BUTTON_ALPHA_CHANGE_DURATION (350ms). */
private val ActionAlphaDurationMillis = 350

/** COUI `coui_search_bar_functional_button_offset_distance` (30dp). */
private val ActionSlideDistance = 30.dp

/**
 * Start padding for the outside end action: COUI `coui_search_bar_functional_button_start_gap`
 * (8dp) minus the 16dp input field margin already applied, floored at zero. [CancelButton] brings
 * its own 12dp ripple padding, which is what makes up the rest of the visual gap.
 */
private val ActionExtraStartPadding = 0.dp

/**
 * End padding for the outside end action: `coui_search_bar_functional_button_end_gap_compat` (4dp).
 * [CancelButton]'s own 12dp ripple padding supplies the remaining visual inset.
 */
private val ActionEndPadding = 4.dp
