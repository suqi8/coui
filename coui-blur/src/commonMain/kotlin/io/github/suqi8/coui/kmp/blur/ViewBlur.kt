// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import io.github.suqi8.coui.kmp.blur.internal.BLUR_RADIUS_TO_SIGMA

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

    /** The primary button (and FAB) blur radius in raw pixels. */
    val PrimaryButtonBlurRadiusPx: Float = 10f

    /** The secondary and transparent button blur radius in raw pixels. */
    val ContentButtonBlurRadiusPx: Float = 25f

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
        enabled = enabled,
    )
}

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
