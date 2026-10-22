// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.ui.unit.dp

/**
 * The COUI corner radius steps (`coui_round_corner_*` dimens, the `couiRoundCorner*` theme attributes).
 *
 * The base steps are the radii COUI declares. On ColorOS 17 the smooth-corner code paths swap a base
 * step for a larger radius drawn with a squarer smoothing weight:
 * - [Normal]: card lists (`COUICardListSelectedItemLayout.getInnerRadius`, weight 2.5), which always
 *   use [Normal.XL];
 * - [Large]: `COUIButton` and `COUICardView` (`RoundCornerUtil.getRoundCornerForOS17`, weight 3.0).
 *
 * The smoothing weights themselves are not modelled: they are Oplus framework values without a known
 * mapping onto the squircle extension this library draws with.
 */
object COUIRoundCorner {
    /** COUI coui_round_corner_xs. */
    val XS = 4.dp

    /** COUI coui_round_corner_s. */
    val S = 8.dp

    /** COUI coui_round_corner_m. */
    val M = 12.dp

    /** COUI coui_round_corner_l. */
    val L = 16.dp

    /** COUI coui_round_corner_xl. */
    val XL = 20.dp

    /** COUI coui_round_corner_xxl. */
    val XXL = 24.dp

    /** COUI coui_round_corner_xxxl. */
    val XXXL = 28.dp

    /** COUI coui_round_corner_xxxxl. */
    val XXXXL = 32.dp

    /** The ColorOS 17 card list radii (COUI coui_round_corner_*_radius_normal_16_1). */
    object Normal {
        /** COUI coui_round_corner_xs_radius_normal_16_1. */
        val XS = 5.dp

        /** COUI coui_round_corner_s_radius_normal_16_1. */
        val S = 10.dp

        /** COUI coui_round_corner_m_radius_normal_16_1. */
        val M = 15.dp

        /** COUI coui_round_corner_l_radius_normal_16_1. */
        val L = 19.dp

        /** COUI coui_round_corner_xl_radius_normal_16_1, the radius of every ColorOS 17 card list. */
        val XL = 24.dp

        /** COUI coui_round_corner_xxl_radius_normal_16_1. */
        val XXL = 29.dp

        /** COUI coui_round_corner_xxxl_radius_normal_16_1. */
        val XXXL = 34.dp

        /** COUI coui_round_corner_xxxxl_radius_normal_16_1. */
        val XXXXL = 38.dp
    }

    /** The ColorOS 17 button and card view radii (COUI coui_round_corner_*_radius_large_16_1). */
    object Large {
        /** COUI coui_round_corner_xs_radius_large_16_1. */
        val XS = 5.dp

        /** COUI coui_round_corner_s_radius_large_16_1. */
        val S = 11.dp

        /** COUI coui_round_corner_m_radius_large_16_1. */
        val M = 17.dp

        /** COUI coui_round_corner_l_radius_large_16_1. */
        val L = 23.dp

        /** COUI coui_round_corner_xl_radius_large_16_1. */
        val XL = 28.dp

        /** COUI coui_round_corner_xxl_radius_large_16_1. */
        val XXL = 34.dp

        /** COUI coui_round_corner_xxxl_radius_large_16_1. */
        val XXXL = 39.dp

        /** COUI coui_round_corner_xxxxl_radius_large_16_1. */
        val XXXXL = 45.dp
    }
}
