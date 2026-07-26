// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.nav.core

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.suqi8.coui.kmp.nav.runtime.NavChange
import com.suqi8.coui.kmp.nav.runtime.NavPresentation
import com.suqi8.coui.kmp.nav.runtime.relativeDepth
import com.suqi8.coui.kmp.nav.runtime.roleFor
import com.suqi8.coui.kmp.nav.transition.NavGesture
import com.suqi8.coui.kmp.nav.transition.NavRole
import com.suqi8.coui.kmp.nav.transition.NavSettle
import com.suqi8.coui.kmp.nav.transition.NavTransitionScope

/**
 * A [NavTransitionScope] whose [relativeDepth] (and derived [role]) are **deferred reads** of the
 * live [NavPresentation.animatedTop]. Passing this into a transition that itself reads inside a
 * `graphicsLayer { }` (e.g. [com.suqi8.coui.kmp.nav.transition.navGraphicsTransition]) yields
 * zero-recomposition, per-frame visual updates.
 *
 * @param presentation source of the single driving float.
 * @param entryIndex this entry's index in the back stack (for the depth subtraction).
 * @param isRemoving whether this entry is animating out (affects [role]).
 */
@Stable
internal class LiveNavTransitionScope(
    private val presentation: NavPresentation,
    private val entryIndex: Int,
    private val isRemoving: Boolean,
    override val change: NavChange,
    override val layoutSize: IntSize,
    override val layoutDirection: LayoutDirection,
    override val density: Density,
) : NavTransitionScope {
    override val relativeDepth: Float
        get() = relativeDepth(presentation.animatedTop.value, entryIndex)

    override val role: NavRole
        get() = roleFor(relativeDepth, isRemoving)

    override val gesture: NavGesture? get() = presentation.gesture

    override val settle: NavSettle? get() = presentation.settle

    /** Coarse gesture flag for composition-time branch dispatch (see navDirectionalTransition). */
    internal val gestureActive: Boolean get() = presentation.gestureActive
}
