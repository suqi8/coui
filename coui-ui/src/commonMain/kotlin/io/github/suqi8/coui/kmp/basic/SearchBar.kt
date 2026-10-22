// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.hasFocusReassignBug
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.roundToInt

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
 *   expanded, typically a [CancelButton]. It is placed 12dp after the capsule and grows in with the
 *   COUI 17 edit-state spring (scale 0.5 to 1, fading in) while the capsule narrows to make room.
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

    // COUI 17 COUISearchBar: the capsule width (mStateChangedAnimation) and the functional button
    // (mFunBtnSpringAnimation) run on response 0.5 / bounce 0.25 springs, both in
    // StateChangedToEditParamsHigh and StateChangedToNormalParamsHigh. They start together with
    // the same force, so one progress drives both.
    val progress = remember { Animatable(if (expanded) 1f else 0f) }
    LaunchedEffect(expanded) {
        progress.animateTo(targetValue = if (expanded) 1f else 0f, animationSpec = StateChangeSpring)
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = insideMargin.width),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = insideMargin.height),
            ) {
                inputField()
            }
            if (outsideEndAction != null) {
                val actionInMotion by remember { derivedStateOf { progress.value != 0f } }
                if (expanded || actionInMotion) {
                    val actionTextAlpha = remember { { searchBarActionTextAlpha(progress.value) } }
                    Box(
                        modifier = Modifier.layout { measurable, constraints ->
                            // The slot claims (gap + button) * progress, so the capsule beside it
                            // narrows with the spring, overshoot included. The button itself sits
                            // at its edit-state position against the end edge.
                            val placeable = measurable.measure(constraints.copy(minWidth = 0))
                            val fraction = max(progress.value, 0f)
                            val width = ((placeable.width + ActionStartGap.roundToPx()) * fraction).roundToInt()
                            val direction = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
                            layout(width, placeable.height) {
                                placeable.placeRelativeWithLayer(width - placeable.width, 0) {
                                    val p = progress.value
                                    val scale = searchBarActionScale(p)
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = direction * ActionOffsetPx * (1f - p)
                                    alpha = searchBarActionAlpha(p)
                                }
                            }
                        },
                    ) {
                        CompositionLocalProvider(LocalSearchBarActionTextAlpha provides actionTextAlpha) {
                            outsideEndAction()
                        }
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
 * @param material whether the capsule takes the COUI 17 top-bar glass material (filtered
 *   backdrop, shadow, edge lighting, touch spotlight and the `COUIViewTouchAnimator` press scale,
 *   rubber band and deformation; see [COUIMaterialEffect.materialPanel]), as `SearchBarBackgroundView`
 *   does by default. When `false` the capsule is a flat [SearchBarColors.backgroundColor] fill.
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
    material: Boolean = true,
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
    // COUI input text: 15sp regular (Widget.COUI.COUISearchBar inputTextSize =
    // coui_search_view_input_text_size, applied by SearchBarBackgroundView.loadAttr).
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
            // COUI 17 SearchBarBackgroundView: the TOP_BAR material over a transparent background
            // (ToolbarMaterialEffectResolver), ?couiColorBar without it. A collapsing capsule tints
            // toward the divider line, so it drops the material for its flat colour.
            val capsuleMaterial = material && collapse == null
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
                    .then(
                        if (capsuleMaterial) {
                            Modifier.materialPanel(
                                interactionSource = internalInteractionSource,
                                cornerRadius = COUIRoundCorner.Full,
                                motionEnabled = enabled,
                            )
                        } else {
                            Modifier.background(
                                color = collapse?.capsuleColor ?: colors.backgroundColor,
                                shape = capsuleShape,
                            )
                        },
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
     * The horizontal padding of the [CancelButton] label inside its capsule. COUI 17
     * `coui_search_bar_functional_button_text_padding_horizontal`, which `SearchBarFunctionButton`
     * applies to the instant-search button.
     */
    val CancelPaddingHorizontal = 16.dp

    /**
     * The height of the [CancelButton] capsule. COUI 17 `SearchBarFunctionButton` matches the search
     * capsule: `coui_search_bar_normal_background_height`.
     */
    val CancelHeight = 40.dp

    /** The widest a [CancelButton] may grow. COUI `coui_search_bar_functional_button_max_width`. */
    val CancelMaxWidth = 152.dp

    /**
     * The visual circle size of the default trailing (clear) button, matching the 9dp-radius
     * circle of ic_edit_text_delete_search_view.
     */
    val TrailingIconVisualSize = 18.dp

    /**
     * Creates a [SearchBarColors] with the default colors of the [InputField] and [CancelButton],
     * following COUI 17 COUISearchBar: `?couiColorBar` capsule fill (the colour the glass material
     * falls back to), couiColorLabelSecondary hint, couiColorSecondNeutral glyph (baked into
     * `coui_search_view_icon_normal`), couiColorHintNeutral clear circle and the
     * `coui_search_function_button_text_color` (couiColorLabelPrimary) cancel label.
     */
    @Composable
    fun searchBarColors(
        backgroundColor: Color = barColor(),
        labelColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        iconColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        clearIconColor: Color = COUITheme.colorScheme.onSurfaceContainerHigh,
        cancelTextColor: Color = COUITheme.colorScheme.onSurface,
    ): SearchBarColors = remember(backgroundColor, labelColor, iconColor, clearIconColor, cancelTextColor) {
        SearchBarColors(
            backgroundColor = backgroundColor,
            labelColor = labelColor,
            iconColor = iconColor,
            clearIconColor = clearIconColor,
            cancelTextColor = cancelTextColor,
        )
    }

    /**
     * `?couiColorBar` (`coui_color_bar`: `#FAFAFA` light, `#1E1E1E` dark), the fill
     * `ToolbarMaterialEffectResolver.getToolbarBackgroundColor` gives the search capsule and the
     * cancel capsule without the material. The theme has no bar token, so it is picked by the
     * background luminance, the same test the material uses.
     */
    @Composable
    private fun barColor(): Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) BarColorDark else BarColorLight

    private val BarColorLight = Color(0xFFFAFAFA)
    private val BarColorDark = Color(0xFF1E1E1E)
}

/**
 * Colors used by the [InputField] and [CancelButton] of a [SearchBar].
 *
 * @param backgroundColor the capsule fill when the material is off, and the colour a collapsing
 *   capsule starts from.
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
 * On COUI 17 `SearchBarFunctionButton` is its own 40dp capsule: the same top-bar glass material as
 * the search capsule (`?couiColorBar` without it), 16dp horizontal text padding and a 14sp medium
 * couiColorLabelPrimary label. It appears as soon as the bar expands, whether or not a query has
 * been typed, and the class deliberately leaves the click unwired — the host decides. Settings
 * collapses the bar, and collapsing clears the query, so the net effect there is clear + collapse +
 * hide the keyboard.
 *
 * Inside a [SearchBar] `outsideEndAction` the label fades in with the edit-state progress while the
 * capsule reaches full opacity within the first fifth of it, as `COUISearchBar` does for a blurred
 * button.
 *
 * @param onClick the callback invoked when the button is clicked.
 * @param modifier the [Modifier] to be applied to the button.
 * @param text the label of the button.
 * @param enabled whether the button responds to clicks.
 * @param material whether the capsule takes the COUI 17 top-bar glass material (see
 *   [COUIMaterialEffect.materialPanel]). When `false` it is a flat [SearchBarColors.backgroundColor]
 *   capsule.
 * @param colors the [SearchBarColors] resolving the label and fallback fill colors.
 */
@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Cancel",
    enabled: Boolean = true,
    material: Boolean = true,
    colors: SearchBarColors = SearchBarDefaults.searchBarColors(),
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val interactionSource = remember { MutableInteractionSource() }
    val textAlpha = LocalSearchBarActionTextAlpha.current
    // coui_search_function_button_text_color: couiColorLabelPrimary, disabled couiColorLabelTertiary.
    val labelColor = if (enabled) {
        colors.cancelTextColor
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }
    // SearchBarFunctionButton: a smooth round rect at half the height with weight 1.0.
    val capsuleShape = if (isSquircleEnabled()) {
        remember { SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight) }
    } else {
        CircleShape
    }
    // The material draws its own press feedback.
    val indication = if (material) null else LocalIndication.current
    Box(
        modifier = modifier
            .widthIn(max = SearchBarDefaults.CancelMaxWidth)
            .height(SearchBarDefaults.CancelHeight)
            .then(
                if (material) {
                    Modifier.materialPanel(
                        interactionSource = interactionSource,
                        cornerRadius = COUIRoundCorner.Full,
                        motionEnabled = enabled,
                    )
                } else {
                    Modifier.background(color = colors.backgroundColor, shape = capsuleShape)
                },
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = indication,
                onClick = { currentOnClick() },
            )
            .padding(horizontal = SearchBarDefaults.CancelPaddingHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = if (textAlpha == null) {
                Modifier
            } else {
                Modifier.graphicsLayer { alpha = textAlpha() }
            },
            color = labelColor,
            fontSize = SearchBarDefaults.CancelFontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The extra alpha the [CancelButton] label applies inside a [SearchBar] action slot, on top of the
 * slot's own [searchBarActionAlpha]; `null` outside a [SearchBar].
 */
private val LocalSearchBarActionTextAlpha = staticCompositionLocalOf<(() -> Float)?> { null }

/** COUI 17 `StateChangedToEditParamsHigh` / `StateChangedToNormalParamsHigh`: response 0.5, bounce 0.25. */
private val StateChangeSpring = TouchMotion.spring(response = 0.5f, bounce = 0.25f, visibilityThreshold = 0.001f)

/**
 * `COUISearchBar.mFunctionalInitScale`: `functionalButtonInitScale` (to edit) and
 * `functionalButtonEndScale` (to normal) are both 0.5 in the COUI 17 high-end params.
 */
private val ActionInitScale = 0.5f

/** `COUISearchBar.updateFunctionalButtonAlpha`: a blurred button is fully opaque at this progress. */
private val ActionAlphaRemapProgress = 0.2f

/**
 * The COUI 17 starting translation of the action, `mFunctionalButtonOffsetX`: the search background
 * view's end edge minus the button's, read while the background view is laid out at its full width
 * plus 20px (`handleStateChangedAnimation`). The button therefore grows in almost in place, shifted
 * 20px outward at the start. The 60dp `functional_button_offset_distance` is ColorOS 16 only.
 */
private val ActionOffsetPx = 20f

/** Gap between the capsule and the action: COUI `coui_search_bar_functional_button_start_gap` (12dp). */
private val ActionStartGap = 12.dp

/** The action scale at edit-state [progress]: `initScale + (1 - initScale) * progress`. */
internal fun searchBarActionScale(progress: Float): Float = ActionInitScale + (1f - ActionInitScale) * progress

/**
 * The action alpha at edit-state [progress]: `COUISearchBar.remapAlphaProgress(p, 0.2)`, which a
 * blurred functional button uses, so the capsule is solid before the label has faded in.
 */
internal fun searchBarActionAlpha(progress: Float): Float {
    val p = progress.coerceIn(0f, 1f)
    return if (p < ActionAlphaRemapProgress) p / ActionAlphaRemapProgress else 1f
}

/**
 * The extra label alpha that makes the [CancelButton] text, already under [searchBarActionAlpha],
 * end up at `progress` (`setFunctionalButtonTextAlpha`).
 */
internal fun searchBarActionTextAlpha(progress: Float): Float {
    val p = progress.coerceIn(0f, 1f)
    val outer = searchBarActionAlpha(p)
    return if (outer > 0f) p / outer else 0f
}
