// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.node.CompositionLocalConsumerModifierNode

/**
 * Plays the COUI scroll-edge vibration (`performHapticFeedback(307)`, `EDGE_LIST_VIBRATE`) from a
 * modifier node, as `COUIRecyclerView` / `COUINestedScrollView.performFeedback` do when a fling
 * runs into an edge or a drag is released while overscrolled. Off ColorOS it falls back to the
 * closest standard haptic, like [CouiHapticEffect.ScrollEdge]. The node must be attached.
 */
internal expect fun CompositionLocalConsumerModifierNode.performScrollEdgeHaptic()
