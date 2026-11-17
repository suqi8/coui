// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The COUI 17 floating navigation bar with accessories (`COUIBottomFloatingAccessoryNavigationBar`):
 * the [FloatingNavigationBar] capsule of [FloatingNavigationBarItem]s with an optional 56dp
 * [startAccessory] and [endAccessory] (usually a [FloatingNavigationBarAccessory]) 12dp beside it.
 *
 * - **Placement** (`onLayout`): every accessory that is set reserves its 56dp slot and the 12dp gap
 *   whether it is shown or not, so showing or hiding one never moves the capsule. On compact screens
 *   (narrower than 600dp) the bar keeps a 16dp padding on both sides, the start accessory hugs the
 *   start edge, the end accessory the end edge, and the capsule is centred between them
 *   (`layoutPositionsSmallScreen`); on wider ones there is no side padding and accessory, capsule and
 *   accessory are centred as a group (`layoutPositionsMediumScreen`).
 * - **Show / hide** (`AccessorySideExpansionController`): a hidden accessory tucks behind the
 *   capsule's start or end edge (`computeInactiveFakeBounds`). Showing slides it out on a spring
 *   (response 0.55, bounce 0.35) while it scales 0.5 -> 1 on the same spring; its alpha follows how
 *   much of it the capsule still covers (fully opaque until half of it is covered), and the two
 *   shapes merge like metaballs while they touch. Hiding reverses it.
 * - **Capsule**: identical to [FloatingNavigationBar]: slide-to-select, touch motion, the
 *   `TYPE_FRAMEWORK_BOTTOM_BAR` material through [LocalMaterialBackdrop], the
 *   `TYPE_FRAMEWORK_CAPSULE_3` edge light and the item count springs.
 *
 * @param modifier The modifier applied to the bar's container (the full-width area holding the capsule).
 * @param backgroundModifier Applied inside the capsule clip, above [color] and below the items.
 * @param startAccessoryVisible Whether [startAccessory] is shown (`showStartAccessoryView` /
 *   `hideStartAccessoryView`).
 * @param endAccessoryVisible Whether [endAccessory] is shown (`showEndAccessoryView` /
 *   `hideEndAccessoryView`).
 * @param materialEffect Whether the COUI 17 material is drawn on the capsule: the edge lighting, the
 *   touch spotlight, the metaball merge with the accessories and, with a [LocalMaterialBackdrop]
 *   provider, the backdrop blur. Defaults to [LocalCOUIMaterialEffectEnabled].
 * @param showDivider Whether to draw a thin divider ring around the capsule (not part of COUI 17).
 * @param defaultWindowInsetsPadding Whether to keep the bar above the navigation and caption bars.
 * @param color The capsule fill. Defaults to COUI's opaque `?couiColorBar`; replaced by the backdrop
 *   blur when a [LocalMaterialBackdrop] provider is present and [materialEffect] is on.
 * @param cornerRadius The capsule corner radius.
 * @param horizontalOutSidePadding The side padding on compact screens
 *   (coui_bottom_floating_navigation_bar_padding_horizontal).
 * @param shadowElevation The blur radius of the capsule's drop shadow; 0 disables it.
 * @param startAccessory The view at the capsule's start, laid out at its own size and centred on a
 *   56dp slot. `null` reserves no slot.
 * @param endAccessory The view at the capsule's end, laid out at its own size and centred on a 56dp
 *   slot. `null` reserves no slot.
 * @param content The [FloatingNavigationBarItem]s, placed directly inside.
 */
@Composable
fun FloatingAccessoryNavigationBar(
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    color: Color = FloatingNavigationBarDefaults.color(),
    cornerRadius: Dp = FloatingNavigationBarDefaults.CornerRadius,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    startAccessory: (@Composable () -> Unit)? = null,
    endAccessory: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val menuState = remember { FloatingNavigationBarState() }
    val state = remember { FloatingAccessoryBarState(menuState) }
    val isDark = COUITheme.colorScheme.isDark
    val startActive = startAccessory != null && startAccessoryVisible
    val endActive = endAccessory != null && endAccessoryVisible
    // BaseBottomFloatingMenuView: with the material the backdrop blur is the fill.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val fill = if (backdropProvider != null) Color.Transparent else color

    // The capsule bounds and the accessory slots chase their layout targets on the melt-ball
    // springs; a new container width snaps (onConfigurationChanged: mSkipNextLayoutAnimation).
    LaunchedEffect(state) {
        var last: FloatingAccessoryBarTargets? = null
        snapshotFlow { state.targets }.filterNotNull().collect { target ->
            val previous = last
            last = target
            if (previous == null || previous.availableWidth != target.availableWidth) {
                menuState.menuLeft.snapTo(target.menuLeft)
                menuState.menuWidth.snapTo(target.menuWidth)
                state.startLeft.snapTo(target.startLeft)
                state.endLeft.snapTo(target.endLeft)
                menuState.initialized = true
            } else {
                if (previous.menuLeft != target.menuLeft) launch { menuState.menuLeft.animateTo(target.menuLeft, AccessoryBoundsSpring) }
                if (previous.menuWidth != target.menuWidth) launch { menuState.menuWidth.animateTo(target.menuWidth, AccessoryBoundsSpring) }
                if (previous.startLeft != target.startLeft) launch { state.startLeft.animateTo(target.startLeft, AccessoryBoundsSpring) }
                if (previous.endLeft != target.endLeft) launch { state.endLeft.animateTo(target.endLeft, AccessoryBoundsSpring) }
            }
        }
    }
    // doAlphaScaleAnim: the scale springs 0.5 <-> 1 (HIGH_END_SCALE_SPRING, response 0.55, bounce
    // 0.35); an accessory composed already shown starts at full scale (applyAlphaScaleState(_, false)).
    LaunchedEffect(startActive) { state.start.settleScale(startActive) }
    LaunchedEffect(endActive) { state.end.settleScale(endActive) }

    val neckShaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) List(4) { RuntimeShader(SearchBarMetaballShader.Source) } else null
    }
    val neckFilters = if (fill.alpha < 1f) {
        val spec = FloatingNavigationBarDefaults.backdropSpec(isDark)
        listOf(spec.layer1 to spec.mode1, spec.layer2 to spec.mode2)
    } else {
        null
    }

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Bottom))
                } else {
                    Modifier
                },
            )
            .then(
                if (neckShaders != null && (startAccessory != null || endAccessory != null)) {
                    Modifier.drawWithContent {
                        drawContent()
                        val capsule = state.capsuleRect
                        with(SearchBarMetaballShader) {
                            if (startAccessory != null) {
                                drawNeck(
                                    shaders = neckShaders.subList(0, 2),
                                    capsule = capsule,
                                    action = state.accessoryRect(state.start, state.startLeft.value),
                                    alpha = state.accessoryAlpha(state.start, state.startLeft.value),
                                    filters = neckFilters,
                                    fallback = color,
                                )
                            }
                            if (endAccessory != null) {
                                drawNeck(
                                    shaders = neckShaders.subList(2, 4),
                                    capsule = capsule,
                                    action = state.accessoryRect(state.end, state.endLeft.value),
                                    alpha = state.accessoryAlpha(state.end, state.endLeft.value),
                                    filters = neckFilters,
                                    fallback = color,
                                )
                            }
                        }
                    }
                } else {
                    Modifier
                },
            ),
        content = {
            Box(Modifier.layoutId(ACCESSORY_BAR_CAPSULE_ID)) {
                FloatingNavigationBarCapsule(
                    state = menuState,
                    visible = true,
                    materialEffect = materialEffect,
                    showDivider = showDivider,
                    color = fill,
                    cornerRadius = cornerRadius,
                    shadowElevation = shadowElevation,
                    backdropProvider = backdropProvider,
                    backgroundModifier = backgroundModifier,
                    content = content,
                )
            }
            // The tap target of a large icon rising above the capsule (see FloatingNavigationBar).
            Box(
                modifier = Modifier
                    .layoutId(ACCESSORY_BAR_LARGE_ICON_ID)
                    .pointerInput(menuState) {
                        detectTapGestures {
                            menuState.largeIconItem()?.takeIf { it.enabled }?.onClick?.invoke()
                        }
                    },
            )
            if (startAccessory != null) {
                AccessorySlot(
                    side = state.start,
                    left = { state.startLeft.value },
                    state = state,
                    active = startActive,
                    layoutId = ACCESSORY_BAR_START_ID,
                    content = startAccessory,
                )
            }
            if (endAccessory != null) {
                AccessorySlot(
                    side = state.end,
                    left = { state.endLeft.value },
                    state = state,
                    active = endActive,
                    layoutId = ACCESSORY_BAR_END_ID,
                    content = endAccessory,
                )
            }
        },
    ) { measurables, constraints ->
        val total = constraints.maxWidth
        // changePadding: the side padding only applies on small screens.
        val compact = total < FloatingNavigationBarDefaults.CompactWidthBreakpoint.roundToPx()
        val padding = if (compact) horizontalOutSidePadding.roundToPx() else 0
        val left = padding
        val right = max(left, total - padding)
        val top = FloatingNavigationBarDefaults.VerticalOutSidePadding.roundToPx()
        val barHeight = FloatingNavigationBarDefaults.Height.roundToPx()
        val slot = FloatingAccessoryNavigationBarDefaults.AccessorySize.roundToPx()
        val spacing = FloatingNavigationBarDefaults.ExpansionSpacing.roundToPx()
        val hasStart = startAccessory != null
        val hasEnd = endAccessory != null
        // computeSideReserveForConfiguredIcons: configured accessories reserve their slots.
        val reserve = (if (hasStart) slot + spacing else 0) + (if (hasEnd) slot + spacing else 0)

        val capsule = measurables.first { it.layoutId == ACCESSORY_BAR_CAPSULE_ID }
            .measure(Constraints(maxWidth = max(0, right - left - reserve), maxHeight = barHeight))
        val menuWidth = menuState.capsuleTargetWidth
        val positions = floatingAccessoryBarPositions(
            left = left,
            right = right,
            menuWidth = menuWidth,
            slotSize = slot,
            spacing = spacing,
            hasStart = hasStart,
            hasEnd = hasEnd,
            compact = compact,
        )
        // computeInactiveFakeBounds: a hidden accessory sits inside the capsule's start or end edge.
        val startTarget = if (startActive) positions.startLeft else positions.menuLeft
        val endTarget = if (endActive) positions.endLeft else positions.menuLeft + menuWidth - slot
        val target = FloatingAccessoryBarTargets(
            availableWidth = total,
            menuLeft = positions.menuLeft.toFloat(),
            menuWidth = menuWidth.toFloat(),
            startLeft = startTarget.toFloat(),
            endLeft = endTarget.toFloat(),
        )
        if (state.targets != target) state.targets = target

        val initialized = menuState.initialized
        val drawnLeft = if (initialized) menuState.menuLeft.value.roundToInt() else positions.menuLeft
        val rtl = layoutDirection == LayoutDirection.Rtl
        val capsuleX = if (rtl) total - drawnLeft - capsule.width else drawnLeft
        val height = top * 2 + barHeight

        val largeIcon = menuState.largeIconBounds.firstOrNull { it != null }
        val largeIconPlaceable = measurables.first { it.layoutId == ACCESSORY_BAR_LARGE_ICON_ID }.measure(
            if (largeIcon != null) {
                Constraints.fixed(largeIcon.width.roundToInt(), (-largeIcon.top).coerceIn(0f, largeIcon.height).roundToInt())
            } else {
                Constraints.fixed(0, 0)
            },
        )
        val startPlaceable = measurables.firstOrNull { it.layoutId == ACCESSORY_BAR_START_ID }?.measure(Constraints())
        val endPlaceable = measurables.firstOrNull { it.layoutId == ACCESSORY_BAR_END_ID }?.measure(Constraints())

        layout(total, height) {
            capsule.placeRelative(drawnLeft, top)
            if (largeIcon != null) {
                largeIconPlaceable.place((capsuleX + largeIcon.left).roundToInt(), (top + largeIcon.top).roundToInt())
            }
            state.capsuleRect = Rect(capsuleX.toFloat(), top.toFloat(), (capsuleX + capsule.width).toFloat(), (top + capsule.height).toFloat())
            state.availableWidth = total
            state.rtl = rtl
            state.slotTop = top + (barHeight - slot) / 2f
            state.slotSize = slot.toFloat()
            fun placeAccessory(placeable: Placeable?, side: FloatingAccessorySide, slotLeft: Float, active: Boolean) {
                if (placeable == null) return
                side.size = Size(placeable.width.toFloat(), placeable.height.toFloat())
                // A hidden accessory that has settled is INVISIBLE: not placed, no touches.
                if (side.settledHidden(active)) return
                val centerX = slotLeft + slot / 2f
                placeable.placeRelative(
                    (centerX - placeable.width / 2f).roundToInt(),
                    top + (barHeight - placeable.height) / 2,
                )
            }
            placeAccessory(startPlaceable, state.start, if (initialized) state.startLeft.value else startTarget.toFloat(), startActive)
            placeAccessory(endPlaceable, state.end, if (initialized) state.endLeft.value else endTarget.toFloat(), endActive)
        }
    }
}

@Composable
private fun AccessorySlot(
    side: FloatingAccessorySide,
    left: () -> Float,
    state: FloatingAccessoryBarState,
    active: Boolean,
    layoutId: String,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .layoutId(layoutId)
            // Room for the material shadow while the fade renders the view offscreen.
            .graphicsLayerWithOverflow(COUIMaterialEffect.ShadowOverflow) {
                val scale = side.scale.value
                scaleX = scale
                scaleY = scale
                alpha = state.accessoryAlpha(side, left())
            }
            .then(if (active) Modifier else Modifier.clearAndSetSemantics { }),
    ) {
        content()
    }
}

/**
 * A COUI 17 floating navigation bar accessory (`COUIBottomFloatingAccessoryView`): a 56dp circle
 * holding [icon] at its own size, for [FloatingAccessoryNavigationBar]'s start and end slots.
 *
 * It wears the capsule's material: the `TYPE_FRAMEWORK_BOTTOM_BAR` backdrop blur through
 * [LocalMaterialBackdrop] (else the opaque [color], `?couiColorBar`), the `TYPE_FRAMEWORK_CAPSULE_3`
 * edge light, the `TYPE_BOTTOM_NAVIGATION` touch spotlight (the `TYPE_OPAQUE_MEDIUM_2` parameters)
 * and the caustic drop shadow, and it takes the `COUITouchMotionAnimator` press scale, rubber-band
 * drag and deformation. There is no press mask.
 *
 * Changing [icon] cross-fades the old icon out and the new one in (`setIconDrawable(_, true)`):
 * with [materialEffect] the icons also scale 1 -> 0 and 0 -> 1 on a response 0.5, bounce 0.4
 * spring, the new icon fading in at response 0.2 and the old one out at 0.15; without it they only
 * fade (0.15 in, 0.1 out).
 *
 * @param onClick Called when the accessory is clicked.
 * @param icon The icon, drawn at its own size in the centre.
 * @param contentDescription The accessibility label of the accessory.
 * @param modifier The modifier applied to the accessory.
 * @param enabled Whether the accessory reacts to touches.
 * @param materialEffect Whether the COUI 17 material, spotlight and full icon-change effect are drawn.
 *   Defaults to [LocalCOUIMaterialEffectEnabled].
 * @param color The fill without a backdrop (`setAccessoryBackgroundColor`).
 * @param iconTint The tint of [icon]; [Color.Unspecified] draws it untinted.
 * @param shadowElevation The blur radius of the drop shadow; 0 disables it.
 */
@Composable
fun FloatingNavigationBarAccessory(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    color: Color = FloatingNavigationBarDefaults.color(),
    iconTint: Color = FloatingAccessoryNavigationBarDefaults.iconTint(),
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
) {
    val isDark = COUITheme.colorScheme.isDark
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val backdropModifier = if (backdropProvider != null) {
        val spec = FloatingNavigationBarDefaults.backdropSpec(isDark)
        remember(backdropProvider, spec) { backdropProvider.materialBackdrop(spec, CircleShape) }
    } else {
        Modifier
    }
    val fill = if (backdropProvider != null) Color.Transparent else color
    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            Triple(RuntimeShader(ShadowEdgeShader.Source), RuntimeShader(ShadowEdgeShader.Source), RuntimeShader(SpotLightShader.Source))
        } else {
            null
        }
    }
    val spotlight = remember { SpotLightState() }
    val stroke = if (isDark) FloatingNavigationBarStrokeDark else FloatingNavigationBarStrokeLight
    val spotType = SpotLightType.OpaqueMedium2

    // startIconReplaceAnimation: the outgoing and incoming icon layers.
    val icons = remember { FloatingAccessoryIcons(icon) }
    LaunchedEffect(icon) {
        if (icons.current == icon) return@LaunchedEffect
        icons.replace(icon, fullEffect = materialEffect)
    }

    Box(
        modifier = modifier
            .size(FloatingAccessoryNavigationBarDefaults.AccessorySize)
            .couiTouchMotion(interactionSource = interactionSource, enabled = enabled)
            .then(if (shadowElevation > 0.dp) Modifier.dropShadow(shape = CircleShape, shadow = floatingNavigationBarShadow(shadowElevation)) else Modifier)
            .then(backdropModifier)
            .clip(CircleShape)
            .background(fill)
            .then(
                if (shaders != null) {
                    Modifier
                        .drawWithCache {
                            val radius = min(size.width, size.height) / 2f
                            configureFloatingNavigationBarEdge(shaders.first, size.width, size.height, radius, stroke)
                            configureFloatingNavigationBarEdge(shaders.second, size.width, size.height, radius, stroke)
                            splitFrameworkStroke(shadowPass = shaders.first, edgePass = shaders.second)
                            val shadowBrush = shaders.first.asBrush()
                            val edgeBrush = shaders.second.asBrush()
                            onDrawWithContent {
                                // drawTouchEffectDrawable: the spotlight clipped to the circle, below the icon.
                                val glow = spotlight.progress.value
                                if (glow > 0f) {
                                    val light = spotlight.position.value
                                    SpotLightShader.setUniforms(
                                        shader = shaders.third,
                                        lightX = light.x,
                                        lightY = light.y,
                                        radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                                        intensity = spotType.endIntensity(isDark) * glow,
                                    )
                                    drawRect(brush = shaders.third.asBrush(), blendMode = spotType.blendMode(isDark))
                                }
                                drawRect(shadowBrush)
                                drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
                                drawContent()
                            }
                        }
                        .pointerInput(enabled) {
                            if (!enabled) return@pointerInput
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                scope.launch { spotlight.glow(down.position) }
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!change.pressed) break
                                    if (change.position != change.previousPosition) {
                                        val position = change.position
                                        scope.launch { spotlight.follow(position) }
                                    }
                                }
                                scope.launch { spotlight.extinguish() }
                            }
                        }
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = if (shaders != null) null else LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val filter = if (iconTint.isSpecified) ColorFilter.tint(iconTint) else null
        val outgoing = icons.outgoing
        if (outgoing != null) {
            Image(
                imageVector = outgoing,
                contentDescription = null,
                modifier = Modifier.graphicsLayer {
                    alpha = icons.outgoingAlpha.value
                    scaleX = icons.outgoingScale.value
                    scaleY = icons.outgoingScale.value
                },
                colorFilter = filter,
            )
        }
        Image(
            imageVector = icons.current,
            contentDescription = contentDescription,
            modifier = Modifier.graphicsLayer {
                alpha = icons.incomingAlpha.value
                scaleX = icons.incomingScale.value
                scaleY = icons.incomingScale.value
            },
            colorFilter = filter,
        )
    }
}

/** Contains default values used by [FloatingAccessoryNavigationBar] and [FloatingNavigationBarAccessory]. */
object FloatingAccessoryNavigationBarDefaults {
    /** The accessory size: the capsule height (`mAccessorySize` = coui_bottom_floating_navigation_bar_height). */
    val AccessorySize = 56.dp

    /**
     * The accessory icon tint. COUI draws the drawable as the app supplies it; the default matches the
     * selected navigation item, `?couiColorLabelPrimary`.
     */
    @Composable
    fun iconTint(): Color = COUITheme.colorScheme.labelPrimary
}

/** The melt-ball springs (`HIGH_END_TRANSLATION_X_SPRING` / `HIGH_END_SCALE_SPRING`: response 0.55, bounce 0.35). */
private val AccessoryBoundsSpring = TouchMotion.spring(response = 0.55f, bounce = 0.35f, visibilityThreshold = 0.5f)

private const val ACCESSORY_BAR_CAPSULE_ID = "floatingAccessoryBarCapsule"
private const val ACCESSORY_BAR_LARGE_ICON_ID = "floatingAccessoryBarLargeIcon"
private const val ACCESSORY_BAR_START_ID = "floatingAccessoryBarStart"
private const val ACCESSORY_BAR_END_ID = "floatingAccessoryBarEnd"

/** The left-to-right start offsets of the capsule and the two accessory slots, in pixels. */
internal data class FloatingAccessoryBarPositions(val menuLeft: Int, val startLeft: Int, val endLeft: Int)

/**
 * `COUIBottomFloatingAccessoryNavigationBar.layoutPositionsSmallScreen` / `layoutPositionsMediumScreen`
 * before right-to-left mirroring, between the content edges [left] and [right].
 */
internal fun floatingAccessoryBarPositions(
    left: Int,
    right: Int,
    menuWidth: Int,
    slotSize: Int,
    spacing: Int,
    hasStart: Boolean,
    hasEnd: Boolean,
    compact: Boolean,
): FloatingAccessoryBarPositions {
    if (!hasStart && !hasEnd) {
        val menuLeft = left + (right - left - menuWidth) / 2
        return FloatingAccessoryBarPositions(menuLeft, menuLeft, menuLeft + menuWidth - slotSize)
    }
    val reserveStart = if (hasStart) slotSize + spacing else 0
    val reserveEnd = if (hasEnd) slotSize + spacing else 0
    return if (compact) {
        val innerLeft = left + reserveStart
        val innerRight = right - reserveEnd
        val menuLeft = innerLeft + (max(0, innerRight - innerLeft) - menuWidth) / 2
        FloatingAccessoryBarPositions(menuLeft, left, right - slotSize)
    } else {
        val group = menuWidth + reserveStart + reserveEnd
        val groupLeft = left + (right - left - group) / 2
        val menuLeft = groupLeft + reserveStart
        FloatingAccessoryBarPositions(menuLeft, groupLeft, menuLeft + menuWidth + spacing)
    }
}

/** The layout targets the bar's springs chase, in pixels, before right-to-left mirroring. */
internal data class FloatingAccessoryBarTargets(
    val availableWidth: Int,
    val menuLeft: Float,
    val menuWidth: Float,
    val startLeft: Float,
    val endLeft: Float,
)

/** One accessory side (`AccessorySideExpansionController`). */
@Stable
internal class FloatingAccessorySide {
    var size = Size.Zero
    private var started = false
    val scale = Animatable(ACCESSORY_HIDDEN_SCALE, visibilityThreshold = 0.001f)

    suspend fun settleScale(active: Boolean) {
        val target = if (active) 1f else ACCESSORY_HIDDEN_SCALE
        if (!started) {
            started = true
            scale.snapTo(target)
        } else {
            scale.animateTo(target, TouchMotion.spring(response = 0.55f, bounce = 0.35f, visibilityThreshold = 0.001f))
        }
    }

    /** `checkAllAlphaScaleAnimationsEnded`: hidden and no longer animating. */
    fun settledHidden(active: Boolean): Boolean = !active && !scale.isRunning && scale.value <= ACCESSORY_HIDDEN_SCALE + 0.001f
}

/** The state of one [FloatingAccessoryNavigationBar]. */
@Stable
internal class FloatingAccessoryBarState(private val menu: FloatingNavigationBarState) {
    val start = FloatingAccessorySide()
    val end = FloatingAccessorySide()
    val startLeft = Animatable(0f, visibilityThreshold = 0.5f)
    val endLeft = Animatable(0f, visibilityThreshold = 0.5f)
    var targets by mutableStateOf<FloatingAccessoryBarTargets?>(null)

    // Placement results in the bar's coordinates.
    var capsuleRect = Rect.Zero
    var availableWidth = 0
    var rtl = false
    var slotTop = 0f
    var slotSize = 0f

    /** The accessory's scaled bounds in the bar with its slot starting at [slotLeft] (left to right). */
    fun accessoryRect(side: FloatingAccessorySide, slotLeft: Float): Rect {
        val size = side.size
        if (size.width <= 0f || size.height <= 0f) return Rect.Zero
        val centerStart = slotLeft + slotSize / 2f
        val centerX = if (rtl) availableWidth - centerStart else centerStart
        val centerY = slotTop + slotSize / 2f
        val scale = side.scale.value
        val halfWidth = size.width * scale / 2f
        val halfHeight = size.height * scale / 2f
        return Rect(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight)
    }

    /**
     * `updateAccessoryViewAlphaByOverlap`: the accessory's alpha from how much of its scaled bounds
     * the drawn capsule covers (`BottomFloatingOverlapHelper.alphaFromOverlapRatio`, 50%).
     */
    fun accessoryAlpha(side: FloatingAccessorySide, slotLeft: Float): Float {
        val view = accessoryRect(side, slotLeft)
        val area = view.width * view.height
        if (area <= 0f) return 0f
        val drawnLeft = if (menu.initialized) menu.menuLeft.value else (targets?.menuLeft ?: 0f)
        val drawnWidth = if (menu.initialized) menu.menuWidth.value else (targets?.menuWidth ?: 0f)
        val left = if (rtl) availableWidth - drawnLeft - drawnWidth else drawnLeft
        val bounds = Rect(left, capsuleRect.top, left + drawnWidth, capsuleRect.bottom)
        val overlapWidth = max(0f, min(view.right, bounds.right) - max(view.left, bounds.left))
        val overlapHeight = max(0f, min(view.bottom, bounds.bottom) - max(view.top, bounds.top))
        return floatingNavigationExpansionAlpha(min(1f, overlapWidth * overlapHeight / area))
    }
}

/** The icon layers of a [FloatingNavigationBarAccessory] (`mIconDrawable` / `mOutgoingIconDrawable`). */
@Stable
private class FloatingAccessoryIcons(initial: ImageVector) {
    var current by mutableStateOf(initial)
    var outgoing by mutableStateOf<ImageVector?>(null)
    val incomingAlpha = Animatable(1f, visibilityThreshold = 0.001f)
    val incomingScale = Animatable(1f, visibilityThreshold = 0.001f)
    val outgoingAlpha = Animatable(0f, visibilityThreshold = 0.001f)
    val outgoingScale = Animatable(0f, visibilityThreshold = 0.001f)

    suspend fun replace(icon: ImageVector, fullEffect: Boolean) {
        // selectVisibleLayerAsOutgoing: the more visible layer leaves.
        val incomingWeight = incomingAlpha.value * if (fullEffect) incomingScale.value else 1f
        val outgoingWeight = if (outgoing == null) 0f else outgoingAlpha.value * if (fullEffect) outgoingScale.value else 1f
        if (outgoing == null || outgoingWeight < incomingWeight) {
            outgoing = current
            outgoingAlpha.snapTo(incomingAlpha.value)
            outgoingScale.snapTo(if (fullEffect) incomingScale.value else 1f)
        }
        current = icon
        incomingAlpha.snapTo(0f)
        incomingScale.snapTo(if (fullEffect) 0f else 1f)
        val iconScale = TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.001f)
        coroutineScope {
            if (fullEffect) {
                launch { outgoingScale.animateTo(0f, iconScale) }
                launch { incomingScale.animateTo(1f, iconScale) }
            }
            launch {
                outgoingAlpha.animateTo(
                    0f,
                    TouchMotion.spring(response = if (fullEffect) 0.15f else 0.1f, bounce = 0f, visibilityThreshold = 0.001f),
                )
            }
            launch {
                incomingAlpha.animateTo(
                    1f,
                    TouchMotion.spring(response = if (fullEffect) 0.2f else 0.15f, bounce = 0f, visibilityThreshold = 0.001f),
                )
            }
        }
        // tryFinishOutgoingIcon.
        outgoing = null
    }
}

/** `EXPANSION_VIEW_MIN_SCALE`. */
private const val ACCESSORY_HIDDEN_SCALE = 0.5f
