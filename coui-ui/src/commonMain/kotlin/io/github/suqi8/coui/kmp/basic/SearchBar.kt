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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSupported
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
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
    val iconExpand = LocalSearchBarIconExpand.current
    val capsuleInfo = remember { SearchBarCapsuleInfo() }
    val geometry = remember { SearchBarGeometry() }
    val barLayoutDirection = LocalLayoutDirection.current

    // COUI 17 COUISearchBar: the capsule width (mStateChangedAnimation) and the functional button
    // (mFunBtnSpringAnimation) run on response 0.5 / bounce 0.25 springs, both in
    // StateChangedToEditParamsHigh and StateChangedToNormalParamsHigh. They start together with
    // the same force, so one progress drives both. Expanded out of a toolbar icon
    // (showInToolBar), the bar is laid out in its edit state for the whole icon expansion instead.
    val progress = remember { Animatable(if (expanded || iconExpand != null) 1f else 0f) }
    LaunchedEffect(expanded, iconExpand != null) {
        if (iconExpand != null) {
            progress.snapTo(1f)
        } else {
            progress.animateTo(targetValue = if (expanded) 1f else 0f, animationSpec = StateChangeSpring)
        }
    }

    val shaders = remember {
        if (isRuntimeShaderSupported()) List(2) { RuntimeShader(SearchBarMetaballShader.Source) } else null
    }
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val blendSupported = remember { BlendMode.ColorDodge.isSupported() && BlendMode.Luminosity.isSupported() }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = insideMargin.width)
                .onGloballyPositioned { geometry.rowRootX = it.positionInRoot().x }
                .then(
                    if (outsideEndAction != null && shaders != null) {
                        Modifier.drawWithContent {
                            drawSearchBarNeck(
                                geometry = geometry,
                                progress = progress.value,
                                iconExpand = iconExpand,
                                capsuleInfo = capsuleInfo,
                                capsuleInsetPx = insideMargin.height.toPx(),
                                shaders = shaders,
                                filters = if (blendSupported) materialFilters(isDark) else null,
                            )
                            drawContent()
                        }
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onPlaced { geometry.inputRect = Rect(it.positionInParent(), it.size.toSize()) }
                    .then(
                        if (iconExpand == null) {
                            Modifier
                        } else {
                            Modifier.graphicsLayer {
                                // showInToolBar: the background view starts centred on the toolbar
                                // icon (mIconExpandStartX, measured glyph centre to icon centre) and
                                // slides home, blurring most half way.
                                val p = iconExpand.progress()
                                val startX = iconExpandCapsuleStartX(geometry, iconExpand, barLayoutDirection)
                                if (startX.isNaN()) {
                                    alpha = 0f
                                } else {
                                    alpha = 1f
                                    translationX = startX * (1f - p)
                                }
                                val blur = if (capsuleInfo.material) iconExpandCapsuleBlurPx(p) else 0f
                                renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                            }
                        },
                    )
                    .padding(vertical = insideMargin.height),
            ) {
                CompositionLocalProvider(LocalSearchBarCapsuleInfo provides capsuleInfo) {
                    inputField()
                }
            }
            if (outsideEndAction != null) {
                val actionInMotion by remember { derivedStateOf { progress.value != 0f } }
                if (expanded || actionInMotion) {
                    val actionTextAlpha = remember(iconExpand) {
                        if (iconExpand == null) {
                            { searchBarActionFrame(progress.value, blurred = capsuleInfo.material).textAlpha }
                        } else {
                            { 1f }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .onPlaced { geometry.actionSlotOrigin = it.positionInParent() }
                            .layout { measurable, constraints ->
                                // The slot claims (gap + button) * progress, so the capsule beside
                                // it narrows with the spring, overshoot included. The button itself
                                // sits at its edit-state position against the end edge.
                                val placeable = measurable.measure(constraints.copy(minWidth = 0))
                                val fraction = max(progress.value, 0f)
                                val width = ((placeable.width + ActionStartGap.roundToPx()) * fraction).roundToInt()
                                val rtl = layoutDirection == LayoutDirection.Rtl
                                val direction = if (rtl) -1f else 1f
                                // Slot-local bounds; the slot origin shifts them into the row.
                                val buttonX = if (rtl) 0 else width - placeable.width
                                geometry.actionLocalRect = Rect(
                                    left = buttonX.toFloat(),
                                    top = 0f,
                                    right = (buttonX + placeable.width).toFloat(),
                                    bottom = placeable.height.toFloat(),
                                )
                                layout(width, placeable.height) {
                                    placeable.placeRelativeWithLayer(width - placeable.width, 0) {
                                        if (iconExpand == null) {
                                            // COUISearchBar fun-button listener, the same frame
                                            // function both ways: scale, offset, alpha and blur.
                                            val frame = searchBarActionFrame(progress.value, blurred = capsuleInfo.material)
                                            scaleX = frame.scale
                                            scaleY = frame.scale
                                            translationX = direction * frame.translationPx
                                            alpha = frame.alpha
                                            val blur = frame.blurPx
                                            renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                                        } else {
                                            // funBtnAnimateIconExpand: the button flies from the
                                            // toolbar icon centre (mFunctionalButtonStartX).
                                            val p = iconExpand.progress()
                                            val scale = iconExpandActionScale(p)
                                            scaleX = scale
                                            scaleY = scale
                                            val startX = iconExpandActionStartX(geometry, iconExpand)
                                            translationX = if (startX.isNaN()) 0f else startX * (1f - p)
                                            alpha = iconExpandActionAlpha(p)
                                            val blur = if (capsuleInfo.material) iconExpandActionBlurPx(p) else 0f
                                            renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                                        }
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
 * `mIconExpandStartX`: the toolbar icon centre minus the search glyph centre, in root pixels; `NaN`
 * until both are measured.
 */
private fun Density.iconExpandCapsuleStartX(
    geometry: SearchBarGeometry,
    iconExpand: SearchBarIconExpand,
    layoutDirection: LayoutDirection,
): Float {
    val iconX = iconExpand.sourceCenterX()
    val rowX = geometry.rowRootX
    if (iconX.isNaN() || rowX.isNaN() || geometry.inputRect.isEmpty) return Float.NaN
    val glyphInset = SearchIconCenterInset.toPx()
    val glyphX = if (layoutDirection == LayoutDirection.Rtl) {
        rowX + geometry.inputRect.right - glyphInset
    } else {
        rowX + geometry.inputRect.left + glyphInset
    }
    return iconX - glyphX
}

/** `mFunctionalButtonStartX`: the toolbar icon centre minus the button centre, in root pixels. */
private fun iconExpandActionStartX(geometry: SearchBarGeometry, iconExpand: SearchBarIconExpand): Float {
    val iconX = iconExpand.sourceCenterX()
    val rowX = geometry.rowRootX
    if (iconX.isNaN() || rowX.isNaN() || geometry.actionRect.isEmpty) return Float.NaN
    return iconX - (rowX + geometry.actionRect.center.x)
}

/**
 * Draws the metaball neck between the capsule and the action while they move, from the same
 * transforms the two layers apply. Nothing is drawn at rest, where `COUISearchBar`'s 12dp gap keeps
 * the two shapes apart anyway.
 */
private fun DrawScope.drawSearchBarNeck(
    geometry: SearchBarGeometry,
    progress: Float,
    iconExpand: SearchBarIconExpand?,
    capsuleInfo: SearchBarCapsuleInfo,
    capsuleInsetPx: Float,
    shaders: List<RuntimeShader>,
    filters: List<Pair<Color, BlendMode>>?,
) {
    if (!capsuleInfo.material) return
    val input = geometry.inputRect
    val action = geometry.actionRect
    if (input.isEmpty || action.isEmpty) return
    val capsuleTop = input.top + capsuleInsetPx
    val capsuleBottom = input.bottom - capsuleInsetPx
    val capsule: Rect
    val button: Rect
    val alpha: Float
    if (iconExpand == null) {
        if (progress <= 0f || progress >= 1f) return
        capsule = Rect(input.left, capsuleTop, input.right, capsuleBottom)
        val frame = searchBarActionFrame(progress, blurred = true)
        val direction = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
        button = action.scaledAround(frame.scale).translate(direction * frame.translationPx, 0f)
        alpha = frame.alpha
    } else {
        val p = iconExpand.progress()
        if (p <= 0f || p >= 1f) return
        val capsuleStartX = iconExpandCapsuleStartX(geometry, iconExpand, layoutDirection)
        val actionStartX = iconExpandActionStartX(geometry, iconExpand)
        if (capsuleStartX.isNaN() || actionStartX.isNaN()) return
        val width = iconExpandCapsuleWidth(capsuleBottom - capsuleTop, input.width, p)
        val shift = capsuleStartX * (1f - p)
        capsule = if (layoutDirection == LayoutDirection.Rtl) {
            Rect(input.right - width + shift, capsuleTop, input.right + shift, capsuleBottom)
        } else {
            Rect(input.left + shift, capsuleTop, input.left + width + shift, capsuleBottom)
        }
        button = action.scaledAround(iconExpandActionScale(p)).translate(actionStartX * (1f - p), 0f)
        alpha = iconExpandActionAlpha(p)
    }
    with(SearchBarMetaballShader) {
        drawNeck(
            shaders = shaders,
            capsule = capsule,
            action = button,
            alpha = alpha,
            filters = filters,
            fallback = capsuleInfo.fallbackColor,
        )
    }
}

private fun Rect.scaledAround(scale: Float): Rect {
    val c = center
    val halfWidth = width / 2f * scale
    val halfHeight = height / 2f * scale
    return Rect(c.x - halfWidth, c.y - halfHeight, c.x + halfWidth, c.y + halfHeight)
}

/**
 * The top-bar material colour filters [COUIMaterialEffect.materialPanel] draws (light `#66333333`
 * COLOR_DODGE + `#99F5F5F5` LUMINOSITY, dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY), so the neck
 * matches the shapes it joins.
 */
private fun materialFilters(isDark: Boolean): List<Pair<Color, BlendMode>> = if (isDark) {
    listOf(Color(0xCC262626) to BlendMode.SrcOver, Color(0xB37F7F7F) to BlendMode.Overlay)
} else {
    listOf(Color(0x66333333) to BlendMode.ColorDodge, Color(0x99F5F5F5) to BlendMode.Luminosity)
}

/**
 * The search glyph centre's inset from the capsule's start edge:
 * `coui_search_bar_inner_search_icon_start_gap` (4dp) + `coui_search_bar_inner_search_icon_size` / 2
 * (18dp), which the default leading icon's 12dp + 10dp also lands on.
 */
private val SearchIconCenterInset = 22.dp

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
    val iconExpand = LocalSearchBarIconExpand.current
    val capsuleInfo = LocalSearchBarCapsuleInfo.current
    // SearchBarBackgroundView.dispatchTouchEvent decides on ACTION_DOWN whether a gesture drives the
    // capsule's touch animator and spotlight: not when it starts on an inner primary/secondary
    // button, but the quick delete (clear) button is not in that list and still does.
    var innerButtonPressed by remember { mutableStateOf(false) }
    val quickDeleteDown = remember { arrayOfNulls<PointerId>(1) }
    val iconExpandFullWidth = remember { IntArray(1) }

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
                    // Tags its downs in the Initial pass, before the clickable consumes them, so the
                    // capsule keeps treating them as its own (mQuickDeleteButton is not excluded).
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            quickDeleteDown[0] = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).id
                        }
                    }
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
    val capsuleMaterial = material && collapse == null
    val fallbackColor = colors.backgroundColor
    SideEffect {
        capsuleInfo?.material = capsuleMaterial
        capsuleInfo?.fallbackColor = fallbackColor
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
                            // Observes the Main pass after every child, so a down an inner button
                            // (any clickable icon in the capsule other than the clear button) has
                            // consumed switches the touch motion and spotlight off for that gesture.
                            Modifier.pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val onQuickDelete = quickDeleteDown[0] == down.id
                                    quickDeleteDown[0] = null
                                    if (down.isConsumed && !onQuickDelete) {
                                        innerButtonPressed = true
                                        do {
                                            val event = awaitPointerEvent()
                                        } while (event.changes.any { it.pressed })
                                        innerButtonPressed = false
                                    }
                                }
                            }
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (iconExpand == null) {
                            Modifier
                        } else {
                            // showInToolBar: the capsule is clipped to a circle of its height that
                            // widens into the full capsule (updateIconExpandClippingWidth), from the
                            // start edge, while its content keeps the full width.
                            Modifier.layout { measurable, constraints ->
                                val full = constraints.maxWidth
                                iconExpandFullWidth[0] = full
                                val width = if (constraints.hasBoundedWidth) {
                                    iconExpandCapsuleWidth(
                                        height = SearchBarDefaults.InputFieldMinHeight.toPx(),
                                        fullWidth = full.toFloat(),
                                        progress = iconExpand.progress(),
                                    ).roundToInt().coerceAtLeast(0)
                                } else {
                                    full
                                }
                                val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                                layout(full, placeable.height) { placeable.placeRelative(0, 0) }
                            }
                        },
                    )
                    .then(
                        if (capsuleMaterial) {
                            Modifier.materialPanel(
                                interactionSource = internalInteractionSource,
                                cornerRadius = COUIRoundCorner.Full,
                                motionEnabled = enabled,
                                spotLightSuppressed = { innerButtonPressed || iconExpand?.interactive?.invoke() == false },
                                touchMotionSuppressed = { innerButtonPressed || iconExpand?.interactive?.invoke() == false },
                            )
                        } else {
                            Modifier.background(
                                color = collapse?.capsuleColor ?: colors.backgroundColor,
                                shape = capsuleShape,
                            )
                        },
                    )
                    .then(
                        if (iconExpand == null) {
                            Modifier
                        } else {
                            Modifier
                                .clipToBounds()
                                .layout { measurable, constraints ->
                                    val full = iconExpandFullWidth[0]
                                    val placeable = if (constraints.hasBoundedWidth && full > 0) {
                                        measurable.measure(constraints.copy(minWidth = full, maxWidth = full))
                                    } else {
                                        measurable.measure(constraints)
                                    }
                                    layout(constraints.maxWidth, placeable.height) { placeable.placeRelative(0, 0) }
                                }
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
                            .heightIn(min = SearchBarDefaults.InputFieldMinHeight)
                            .then(
                                if (iconExpand == null) {
                                    Modifier
                                } else {
                                    // showInToolBar: the edit text fades in with the expansion.
                                    Modifier.graphicsLayer { alpha = iconExpand.progress().coerceIn(0f, 1f) }
                                },
                            ),
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
    val iconExpand = LocalSearchBarIconExpand.current
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
                        spotLightSuppressed = { iconExpand?.interactive?.invoke() == false },
                        touchMotionSuppressed = { iconExpand?.interactive?.invoke() == false },
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
 * slot's own [SearchBarActionFrame.alpha]; `null` outside a [SearchBar].
 */
private val LocalSearchBarActionTextAlpha = staticCompositionLocalOf<(() -> Float)?> { null }

/** COUI 17 `StateChangedToEditParamsHigh` / `StateChangedToNormalParamsHigh`: response 0.5, bounce 0.25. */
internal val StateChangeSpring = TouchMotion.spring(response = 0.5f, bounce = 0.25f, visibilityThreshold = 0.001f)

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

/**
 * One frame of the functional (cancel) button during the edit-state change, from the
 * `mFunBtnSpringAnimation` update listener in `COUISearchBar`. Expanding and collapsing run the
 * same function on the same spring, the progress rising from 0 to 1 or falling from 1 to 0.
 *
 * @property scale `mFunctionalInitScale + (1 - mFunctionalInitScale) * p`, 0.5 at rest both ways.
 * @property translationPx `mFunctionalButtonOffsetX * (1 - p)`, toward the end edge, raw pixels.
 * @property alpha the button alpha: `remapAlphaProgress(p, 0.2)` for a blurred (material) button,
 *   which is opaque from 20% on, else `p`.
 * @property textAlpha the extra label alpha inside the button: `setFunctionalButtonTextAlpha(p)`
 *   for a blurred button (so the label shows at `alpha * p`), else 1.
 * @property blurPx `updateBlurWithAnimationProgress(button, 1 - p)`: `30 * (1 - p)` raw pixels for a
 *   blurred button, so the opaque capsule dissolves into blur before it fades out.
 */
@Immutable
internal data class SearchBarActionFrame(
    val scale: Float,
    val translationPx: Float,
    val alpha: Float,
    val textAlpha: Float,
    val blurPx: Float,
)

/** The [SearchBarActionFrame] at edit-state [progress]; [blurred] is `isBlurEnable()`. */
internal fun searchBarActionFrame(progress: Float, blurred: Boolean): SearchBarActionFrame {
    val clamped = progress.coerceIn(0f, 1f)
    return SearchBarActionFrame(
        scale = ActionInitScale + (1f - ActionInitScale) * progress,
        translationPx = ActionOffsetPx * (1f - progress),
        alpha = if (blurred) remapAlphaProgress(clamped, ActionAlphaRemapProgress) else clamped,
        textAlpha = if (blurred) clamped else 1f,
        blurPx = if (blurred) max(1f - progress, 0f) * ActionBlurPx else 0f,
    )
}

/** `COUISearchBar.remapAlphaProgress`: `p / limit` below [limit], 1 from there on. */
private fun remapAlphaProgress(progress: Float, limit: Float): Float = if (progress < limit && limit > 0f) progress / limit else 1f

/** `updateBlurWithAnimationProgress`: the blur radius scale, in raw pixels. */
private val ActionBlurPx = 30f
