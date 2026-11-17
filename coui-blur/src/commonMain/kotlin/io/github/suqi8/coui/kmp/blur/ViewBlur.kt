// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import io.github.suqi8.coui.kmp.blur.internal.BLUR_RADIUS_TO_SIGMA
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A COUI 17 view blur: the parameters `COUIMaterialBlurEffect.setViewBlurEffect(view, radius, c1,
 * m1, c2, m2)` builds into a framework `RenderEffect` chain (`createBlurEffect(r, r, MIRROR)`, then
 * two `createColorFilterEffect(BlendModeColorFilter(c, m))`) and hands to
 * `OplusViewBackgroundRenderEffect`, which filters what lies behind the view.
 *
 * Both layers use Android `BlendModeColorFilter` semantics: the constant colour is the source and
 * the blurred backdrop the destination, so `SrcOver #CCFFFFFF` is 80% white over the blur and
 * `Luminosity #99F5F5F5` keeps the backdrop's hue and saturation while pulling it 60% toward the
 * luminance of `#F5F5F5`. No separate saturation or brightness step is applied.
 *
 * @param blurRadiusPx The blur radius in raw pixels, as COUI passes it (it never converts dp).
 * @param layer1 The first colour blended over the blurred backdrop.
 * @param mode1 How [layer1] is blended.
 * @param layer2 The second colour, blended over the result of [layer1].
 * @param mode2 How [layer2] is blended.
 */
@Immutable
data class ViewBlurStyle(
    val blurRadiusPx: Float,
    val layer1: Color,
    val mode1: BlendMode,
    val layer2: Color,
    val mode2: BlendMode,
)

/**
 * The COUI 17 view blur presets of `COUIMaterialBlurEffect` / `COUIMaterialThemeBlurEffect` used
 * by buttons and the top bar.
 */
object ViewBlurDefaults {

    /**
     * The primary button blur (`COUIButton.applyBlurEffect`, `buttonType = PRIMARY`): radius 10px.
     * Enabled it is `TYPE_CONTENT_PRIMARY_BUTTON`, [themeColor] (`?couiColorContainerTheme`)
     * re-alpha'd to 229 (light) / 230 (dark) SRC_OVER, then `#00FFFFFF` (light, a no-op) /
     * `#19FFFFFF` (dark) OVERLAY. Disabled it is `TYPE_CONTENT_DISABLED_PRIMARY_BUTTON`,
     * [themeColor] at alpha 77 SRC_OVER, then `#19FFFFFF` OVERLAY in both schemes.
     *
     * @param themeColor The button's theme fill.
     * @param isDark Whether the dark scheme is active.
     * @param enabled Whether the button is enabled.
     */
    fun contentPrimaryButton(themeColor: Color, isDark: Boolean, enabled: Boolean = true): ViewBlurStyle = if (enabled) {
        ViewBlurStyle(
            blurRadiusPx = PrimaryButtonBlurRadiusPx,
            layer1 = themeColor.copy(alpha = (if (isDark) 230f else 229f) / 255f),
            mode1 = BlendMode.SrcOver,
            layer2 = if (isDark) Color(0x19FFFFFF) else Color(0x00FFFFFF),
            mode2 = BlendMode.Overlay,
        )
    } else {
        ViewBlurStyle(
            blurRadiusPx = PrimaryButtonBlurRadiusPx,
            layer1 = themeColor.copy(alpha = 77f / 255f),
            mode1 = BlendMode.SrcOver,
            layer2 = Color(0x19FFFFFF),
            mode2 = BlendMode.Overlay,
        )
    }

    /**
     * The secondary button blur (`TYPE_CONTENT_SECONDARY_BUTTON`): radius 25px, `#CCFFFFFF`
     * (light) / `#33FFFFFF` (dark) SRC_OVER, then a no-op `#00FFFFFF` SRC_OVER.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun contentSecondaryButton(isDark: Boolean): ViewBlurStyle = if (isDark) SecondaryDark else SecondaryLight

    /**
     * The transparent button blur (`TYPE_CONTENT_TRANSPARENT_BUTTON`): radius 25px, `#3F333333`
     * OVERLAY, then `#3FFFFFFF` LUMINOSITY, the same in both schemes.
     */
    fun contentTransparentButton(): ViewBlurStyle = Transparent

    /**
     * The top-bar button blur of a `COUIToolbar` material tier (`ToolbarMaterialEffectResolver`):
     * - [TopBarMaterialTier.Default] (`TYPE_FRAMEWORK_TOP_BAR`): radius 0px, light `#66333333`
     *   COLOR_DODGE + `#99F5F5F5` LUMINOSITY, dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY.
     * - [TopBarMaterialTier.Immersive] (`TYPE_FRAMEWORK_TOP_BAR1`): radius 25px, `#1A6E6E6E`
     *   COLOR_DODGE + `#24F1F1F1` LUMINOSITY in both schemes.
     * - [TopBarMaterialTier.DefaultBlur] (`TYPE_FRAMEWORK_TOP_BAR_BLUR`): radius 20px, the colours
     *   of [TopBarMaterialTier.Default].
     *
     * @param tier The toolbar material tier (`supportMaterialType`).
     * @param isDark Whether the dark scheme is active.
     */
    fun topBar(tier: TopBarMaterialTier, isDark: Boolean): ViewBlurStyle = when (tier) {
        TopBarMaterialTier.Default -> if (isDark) TopBarDark else TopBarLight
        TopBarMaterialTier.Immersive -> TopBarImmersive
        TopBarMaterialTier.DefaultBlur -> (if (isDark) TopBarDark else TopBarLight).copy(blurRadiusPx = 20f)
    }

    /**
     * The floating action button blur (`COUIFloatingButton`, `COUIMaterialThemeBlurEffect`): radius
     * 10px. Enabled it is `TYPE_FRAMEWORK_FAB`, [themeColor] (`?couiColorContainerTheme`) re-alpha'd
     * to 229 (light) / 230 (dark) SRC_OVER, then `#1AFFFFFF` (light) / `#19FFFFFF` (dark) OVERLAY.
     * Disabled it is `TYPE_FRAMEWORK_DISABLED_FAB`, [themeColor] at alpha 77 SRC_OVER, then
     * `#19FFFFFF` OVERLAY in both schemes. Unlike [contentPrimaryButton], the light enabled overlay
     * is not a no-op.
     *
     * @param themeColor The button's theme fill.
     * @param isDark Whether the dark scheme is active.
     * @param enabled Whether the button is enabled.
     */
    fun frameworkFab(themeColor: Color, isDark: Boolean, enabled: Boolean = true): ViewBlurStyle = ViewBlurStyle(
        blurRadiusPx = PrimaryButtonBlurRadiusPx,
        layer1 = themeColor.copy(
            alpha = when {
                !enabled -> 77f
                isDark -> 230f
                else -> 229f
            } / 255f,
        ),
        mode1 = BlendMode.SrcOver,
        layer2 = if (enabled && !isDark) Color(0x1AFFFFFF) else Color(0x19FFFFFF),
        mode2 = BlendMode.Overlay,
    )

    /**
     * The floating bottom bar blur (`TYPE_FRAMEWORK_BOTTOM_BAR`, `BaseBottomFloatingMenuView`):
     * radius 25px, light `#B3666666` COLOR_DODGE + `#99F5F5F5` LUMINOSITY, dark `#80666666`
     * OVERLAY + `#B3262626` LUMINOSITY.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun frameworkBottomBar(isDark: Boolean): ViewBlurStyle = if (isDark) BottomBarDark else BottomBarLight

    /**
     * The search bar fill before its material fades in (`TYPE_SEARCH_BAR_NO_MATERIAL`): radius
     * 0px, light `#D6333333` COLOR_DODGE + `#66F5F5F5` LUMINOSITY, dark `#CC262626` SRC_OVER +
     * `#B37F7F7F` OVERLAY. `COUISearchBar` interpolates from this to [topBar] with [interpolate].
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun searchBarNoMaterial(isDark: Boolean): ViewBlurStyle = if (isDark) SearchBarNoMaterialDark else SearchBarNoMaterialLight

    /**
     * The segment button track blur (`TYPE_CONTENT_SEGMENT_BUTTON`, `COUISegmentButtonLayout`):
     * radius 25px, light `#7F333333` COLOR_DODGE + `#99EFEFEF` LUMINOSITY, dark `#7F7F7F7F`
     * OVERLAY + `#99262626` LUMINOSITY.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun contentSegmentButton(isDark: Boolean): ViewBlurStyle = if (isDark) SegmentDark else SegmentLight

    /**
     * The chip tab blur (`COUIChipTab`), which interpolates between the unselected and selected
     * styles with [interpolate]:
     * - `TYPE_CONTENT_CHIP_TAB` / `_SELECTED` (radius 40px): light `#144E4E4E` / `#66A3A1A1`
     *   COLOR_DODGE + `#14606060` / `#99F5F5F5` LUMINOSITY; dark `#33B2B2B2` / `#A6505050`
     *   LUMINOSITY + `#33303030` / `#4CA7A7A7` OVERLAY.
     * - `TYPE_CONTENT_CHIP_TAB_NO_BLUR` / `_SELECTED_NO_BLUR` (radius 0px): light `#9A9B9B9B` /
     *   `#66A3A3A3` COLOR_DODGE + `#9ACECECE` / `#CEF5F5F5` LUMINOSITY; dark `#EE474747` /
     *   `#DA888888` LUMINOSITY + `#CE8D8D8D` / `#80A8A8A8` OVERLAY.
     *
     * @param isDark Whether the dark scheme is active.
     * @param selected Whether the chip is selected.
     * @param blur Whether the blurred variant is used (`COUIChipTab.TYPE_DEFAULT`) rather than the
     *   0px one (`TYPE_DEFAULT_NO_BLUR`).
     */
    fun contentChipTab(isDark: Boolean, selected: Boolean, blur: Boolean = true): ViewBlurStyle = when {
        blur && selected -> if (isDark) ChipTabSelectedDark else ChipTabSelectedLight
        blur -> if (isDark) ChipTabDark else ChipTabLight
        selected -> if (isDark) ChipTabSelectedNoBlurDark else ChipTabSelectedNoBlurLight
        else -> if (isDark) ChipTabNoBlurDark else ChipTabNoBlurLight
    }

    /**
     * `TYPE_NO_EFFECT`: radius 0 and two fully transparent layers. [couiViewBlur] draws nothing for
     * it, as `COUIMaterialBlurEffect.isNoEffect` clears the render effect.
     */
    val NoEffect: ViewBlurStyle = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color.Transparent,
        mode1 = BlendMode.SrcOver,
        layer2 = Color.Transparent,
        mode2 = BlendMode.SrcOver,
    )

    /**
     * The in-between style of `COUIMaterialBlurEffect.setViewBlurEffect(view, startParams,
     * endParams, blendModeParams, progress)`: the radius is lerped linearly, both layer colors with
     * `android.animation.ArgbEvaluator` (alpha linearly, RGB in a gamma-2.2 linear space, rounded to
     * 8 bits), and the blend modes are taken from [blendModes]. At `progress == 0` a [start] without
     * effect, and at `progress == 1` an [end] without effect, yield [NoEffect].
     *
     * @param start The style at progress 0.
     * @param end The style at progress 1.
     * @param progress The interpolation progress; not clamped, like the native call.
     * @param blendModes The style whose blend modes are used (COUI passes the end style).
     */
    fun interpolate(start: ViewBlurStyle, end: ViewBlurStyle, progress: Float, blendModes: ViewBlurStyle = end): ViewBlurStyle {
        if ((progress == 0f && start.isNoEffect()) || (progress == 1f && end.isNoEffect())) return NoEffect
        return ViewBlurStyle(
            blurRadiusPx = start.blurRadiusPx + (end.blurRadiusPx - start.blurRadiusPx) * progress,
            layer1 = argbEvaluate(progress, start.layer1, end.layer1),
            mode1 = blendModes.mode1,
            layer2 = argbEvaluate(progress, start.layer2, end.layer2),
            mode2 = blendModes.mode2,
        )
    }

    /** The primary button (and FAB) blur radius in raw pixels. */
    val PrimaryButtonBlurRadiusPx: Float = 10f

    /** The secondary and transparent button blur radius in raw pixels. */
    val ContentButtonBlurRadiusPx: Float = 25f

    /** The chip tab blur radius in raw pixels. */
    val ChipTabBlurRadiusPx: Float = 40f

    private val BottomBarLight = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0xB3666666),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val BottomBarDark = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0x80666666),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0xB3262626),
        mode2 = BlendMode.Luminosity,
    )

    private val SearchBarNoMaterialLight = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0xD6333333),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x66F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val SearchBarNoMaterialDark = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0xCC262626),
        mode1 = BlendMode.SrcOver,
        layer2 = Color(0xB37F7F7F),
        mode2 = BlendMode.Overlay,
    )

    private val SegmentLight = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0x7F333333),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99EFEFEF),
        mode2 = BlendMode.Luminosity,
    )

    private val SegmentDark = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0x7F7F7F7F),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0x99262626),
        mode2 = BlendMode.Luminosity,
    )

    private val ChipTabSelectedLight = ViewBlurStyle(
        blurRadiusPx = ChipTabBlurRadiusPx,
        layer1 = Color(0x66A3A1A1),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val ChipTabLight = ViewBlurStyle(
        blurRadiusPx = ChipTabBlurRadiusPx,
        layer1 = Color(0x144E4E4E),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x14606060),
        mode2 = BlendMode.Luminosity,
    )

    private val ChipTabSelectedNoBlurLight = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0x66A3A3A3),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0xCEF5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val ChipTabNoBlurLight = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0x9A9B9B9B),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x9ACECECE),
        mode2 = BlendMode.Luminosity,
    )

    private val ChipTabSelectedDark = ViewBlurStyle(
        blurRadiusPx = ChipTabBlurRadiusPx,
        layer1 = Color(0xA6505050),
        mode1 = BlendMode.Luminosity,
        layer2 = Color(0x4CA7A7A7),
        mode2 = BlendMode.Overlay,
    )

    private val ChipTabDark = ViewBlurStyle(
        blurRadiusPx = ChipTabBlurRadiusPx,
        layer1 = Color(0x33B2B2B2),
        mode1 = BlendMode.Luminosity,
        layer2 = Color(0x33303030),
        mode2 = BlendMode.Overlay,
    )

    private val ChipTabSelectedNoBlurDark = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0xDA888888),
        mode1 = BlendMode.Luminosity,
        layer2 = Color(0x80A8A8A8),
        mode2 = BlendMode.Overlay,
    )

    private val ChipTabNoBlurDark = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0xEE474747),
        mode1 = BlendMode.Luminosity,
        layer2 = Color(0xCE8D8D8D),
        mode2 = BlendMode.Overlay,
    )

    private val SecondaryLight = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0xCCFFFFFF),
        mode1 = BlendMode.SrcOver,
        layer2 = Color(0x00FFFFFF),
        mode2 = BlendMode.SrcOver,
    )

    private val SecondaryDark = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0x33FFFFFF),
        mode1 = BlendMode.SrcOver,
        layer2 = Color(0x00FFFFFF),
        mode2 = BlendMode.SrcOver,
    )

    private val Transparent = ViewBlurStyle(
        blurRadiusPx = ContentButtonBlurRadiusPx,
        layer1 = Color(0x3F333333),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0x3FFFFFFF),
        mode2 = BlendMode.Luminosity,
    )

    private val TopBarLight = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0x66333333),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val TopBarDark = ViewBlurStyle(
        blurRadiusPx = 0f,
        layer1 = Color(0xCC262626),
        mode1 = BlendMode.SrcOver,
        layer2 = Color(0xB37F7F7F),
        mode2 = BlendMode.Overlay,
    )

    private val TopBarImmersive = ViewBlurStyle(
        blurRadiusPx = 25f,
        layer1 = Color(0x1A6E6E6E),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x24F1F1F1),
        mode2 = BlendMode.Luminosity,
    )
}

/**
 * The `COUIToolbar` material tiers (`COUIMaterialEffectType`, attr `supportMaterialType`).
 */
enum class TopBarMaterialTier {
    /** `TYPE_DEFAULT` (0), the stock tier: a 0px blur, i.e. a pure colour blend. */
    Default,

    /** `TYPE_IMMERSIVE` (1), for toolbars over imagery: a 25px blur with its own colours. */
    Immersive,

    /** `TYPE_DEFAULT_BLUR` (2): a 20px blur with the [Default] colours. */
    DefaultBlur,
}

/**
 * Converts a COUI raw-pixel view blur radius into the radius [blur] takes, so both produce the
 * same Gaussian: HWUI turns the framework radius into `sigma = 0.57735 * r + 0.5`
 * (`Blur::convertRadiusToSigma`), while [blur] uses `sigma = radius * 0.45`. A radius of 0 stays 0.
 *
 * @param radiusPx The COUI radius in raw pixels.
 */
fun couiViewBlurRadius(radiusPx: Float): Float = if (radiusPx <= 0f) 0f else (HWUI_RADIUS_TO_SIGMA * radiusPx + HWUI_SIGMA_OFFSET) / BLUR_RADIUS_TO_SIGMA

/**
 * Paints a COUI 17 view blur behind this node, the port of `setViewBlurEffect`: the [backdrop]
 * blurred to the framework's Gaussian (see [couiViewBlurRadius]), then [ViewBlurStyle.layer1] and
 * [ViewBlurStyle.layer2] blended over it in order, clipped to [shape]. The node's own content is
 * drawn on top.
 *
 * Draws nothing where runtime shaders are unavailable (Android below 13 and some web backends);
 * callers should then keep the opaque fill COUI falls back to. The [backdrop] must not record the
 * node that draws it.
 *
 * @param backdrop The [Backdrop] providing the content behind the node.
 * @param style The [ViewBlurStyle], e.g. [ViewBlurDefaults.contentSecondaryButton].
 * @param shape The shape the blur is clipped to, the component's outline (COUI clips through
 *   `OplusMaterialCornerParams`).
 * @param enabled Whether the blur is drawn.
 */
fun Modifier.couiViewBlur(
    backdrop: Backdrop,
    style: ViewBlurStyle,
    shape: Shape,
    enabled: Boolean = true,
): Modifier {
    val colors = BlurColors(
        blendColors = listOf(
            BlendColorEntry(style.layer1, style.mode1.toBlurBlendMode()),
            BlendColorEntry(style.layer2, style.mode2.toBlurBlendMode()),
        ),
    )
    val radius = couiViewBlurRadius(style.blurRadiusPx)
    return this.drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = {
            if (radius > 0f) blur(radius)
            blendColors(colors)
        },
        enabled = enabled && !style.isNoEffect(),
    )
}

/**
 * `COUIMaterialBlurEffect.isNoEffect`: a zero radius and two layers whose ARGB value is 0, for which
 * COUI clears the background render effect instead of applying it.
 */
internal fun ViewBlurStyle.isNoEffect(): Boolean = blurRadiusPx == 0f && layer1.toArgb() == 0 && layer2.toArgb() == 0

/**
 * `android.animation.ArgbEvaluator.evaluate` on 8-bit sRGB colors: alpha is lerped linearly, RGB
 * in a gamma-2.2 linear space, and every channel is rounded back to 8 bits.
 */
private fun argbEvaluate(fraction: Float, start: Color, end: Color): Color {
    val s = start.convert(ColorSpaces.Srgb)
    val e = end.convert(ColorSpaces.Srgb)

    fun channel8(v: Float): Float = (v * 255f).roundToInt() / 255f

    fun toLinear(v: Float): Float = channel8(v).pow(SRGB_GAMMA)

    fun lerpRgb(a: Float, b: Float): Float {
        val linear = (toLinear(a) + fraction * (toLinear(b) - toLinear(a))).coerceAtLeast(0f)
        return channel8(linear.pow(1f / SRGB_GAMMA).coerceIn(0f, 1f))
    }

    val alpha = channel8((channel8(s.alpha) + fraction * (channel8(e.alpha) - channel8(s.alpha))).coerceIn(0f, 1f))
    return Color(
        red = lerpRgb(s.red, e.red),
        green = lerpRgb(s.green, e.green),
        blue = lerpRgb(s.blue, e.blue),
        alpha = alpha,
    )
}

/** The gamma `ArgbEvaluator` linearizes sRGB channels with. */
private const val SRGB_GAMMA = 2.2f

/** Maps a Compose [BlendMode] to the matching standard [BlurBlendMode] (both follow SkBlendMode). */
internal fun BlendMode.toBlurBlendMode(): BlurBlendMode = when (this) {
    BlendMode.Clear -> BlurBlendMode.Clear
    BlendMode.Src -> BlurBlendMode.Src
    BlendMode.Dst -> BlurBlendMode.Dst
    BlendMode.SrcOver -> BlurBlendMode.SrcOver
    BlendMode.DstOver -> BlurBlendMode.DstOver
    BlendMode.SrcIn -> BlurBlendMode.SrcIn
    BlendMode.DstIn -> BlurBlendMode.DstIn
    BlendMode.SrcOut -> BlurBlendMode.SrcOut
    BlendMode.DstOut -> BlurBlendMode.DstOut
    BlendMode.SrcAtop -> BlurBlendMode.SrcAtop
    BlendMode.DstAtop -> BlurBlendMode.DstAtop
    BlendMode.Xor -> BlurBlendMode.Xor
    BlendMode.Plus -> BlurBlendMode.Plus
    BlendMode.Modulate -> BlurBlendMode.Modulate
    BlendMode.Screen -> BlurBlendMode.Screen
    BlendMode.Overlay -> BlurBlendMode.Overlay
    BlendMode.Darken -> BlurBlendMode.Darken
    BlendMode.Lighten -> BlurBlendMode.Lighten
    BlendMode.ColorDodge -> BlurBlendMode.ColorDodge
    BlendMode.ColorBurn -> BlurBlendMode.ColorBurn
    BlendMode.Hardlight -> BlurBlendMode.HardLight
    BlendMode.Softlight -> BlurBlendMode.SoftLight
    BlendMode.Difference -> BlurBlendMode.Difference
    BlendMode.Exclusion -> BlurBlendMode.Exclusion
    BlendMode.Multiply -> BlurBlendMode.Multiply
    BlendMode.Hue -> BlurBlendMode.Hue
    BlendMode.Saturation -> BlurBlendMode.Saturation
    BlendMode.Color -> BlurBlendMode.Color
    BlendMode.Luminosity -> BlurBlendMode.Luminosity
    else -> BlurBlendMode.SrcOver
}

/** HWUI `Blur::convertRadiusToSigma` slope, `1 / sqrt(3)`. */
private const val HWUI_RADIUS_TO_SIGMA = 0.57735f

/** HWUI `Blur::convertRadiusToSigma` offset. */
private const val HWUI_SIGMA_OFFSET = 0.5f
