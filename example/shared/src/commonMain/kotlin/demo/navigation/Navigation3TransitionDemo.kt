// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.LocalNavTransitionSettings
import app.LocalNavigator
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_block_input
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_block_input_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_corner_clip
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_corner_clip_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_depth
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_dim_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_push
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_push_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_swipe_edge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_swipe_edge_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation3_transition_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import navigation3.Route
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

/**
 * The Navigation 3 transition effects of the root `NavDisplay`. The switches edit the app-wide
 * [app.NavTransitionSettings], so they take effect on the next push or pop anywhere in the app.
 */
internal val Navigation3TransitionDemo = Demo(
    id = "navigation3_transition",
    apiName = "NavDisplay",
    title = Res.string.demo_navigation3_transition_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            val settings = LocalNavTransitionSettings.current
            val navigator = LocalNavigator.current
            PlaygroundPreview {
                Text(
                    text = stringResource(Res.string.demo_navigation3_transition_hint),
                    style = COUITheme.textStyles.bodyM,
                    color = COUITheme.colorScheme.labelSecondary,
                )
                Text(
                    text = stringResource(Res.string.demo_navigation3_transition_depth, navigator.backStackSize()),
                    modifier = Modifier.padding(top = 8.dp),
                    style = COUITheme.textStyles.bodyM,
                )
            }
            PlaygroundControls {
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation3_transition_corner_clip),
                        summary = stringResource(Res.string.demo_navigation3_transition_corner_clip_summary),
                        checked = settings.enableCornerClip,
                        onCheckedChange = { settings.enableCornerClip = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation3_transition_dim),
                        summary = stringResource(Res.string.demo_navigation3_transition_dim_summary),
                        checked = settings.enableDim,
                        onCheckedChange = { settings.enableDim = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation3_transition_block_input),
                        summary = stringResource(Res.string.demo_navigation3_transition_block_input_summary),
                        checked = settings.blockInputDuringTransition,
                        onCheckedChange = { settings.blockInputDuringTransition = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation3_transition_swipe_edge),
                        summary = stringResource(Res.string.demo_navigation3_transition_swipe_edge_summary),
                        checked = settings.popDirectionFollowsSwipeEdge,
                        onCheckedChange = { settings.popDirectionFollowsSwipeEdge = it },
                    )
                }
                item {
                    ArrowPreference(
                        title = stringResource(Res.string.demo_navigation3_transition_push),
                        summary = stringResource(Res.string.demo_navigation3_transition_push_summary),
                        onClick = { navigator.push(Route.Navigation(Random.nextLong().toString())) },
                    )
                }
            }
        }
    },
    keywords = listOf("NavDisplay"),
    searchTerms = Res.string.demo_navigation3_transition_search_terms,
)
