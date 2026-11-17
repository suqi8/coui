// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import LocalNavigator
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import navigation3.Route
import kotlin.random.Random

fun LazyListScope.otherPageSection() {
    item(key = "other") {
        val navigator = LocalNavigator.current
        SmallTitle(text = "Other")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp),
        ) {
            CardListGroup {
                item {
                    ArrowPreference(
                        title = "PullToRefresh Test",
                        summary = "Navigate to a PullToRefresh Page",
                        onClick = {
                            navigator.push(Route.PullToRefresh)
                        },
                    )
                }
                item {
                    ArrowPreference(
                        title = "Navigation test",
                        summary = "Navigate to a Navigation Page",
                        onClick = { navigator.push(Route.Navigation(Random.nextLong().toString())) },
                    )
                }
                item {
                    ArrowPreference(
                        title = "MultiScaffold Test",
                        summary = "Navigate to a MultiScaffold Page",
                        onClick = { navigator.push(Route.MultiScaffold) },
                    )
                }
                item {
                    ArrowPreference(
                        title = "Toolbar Search Test",
                        summary = "A search bar that expands out of the toolbar search icon",
                        onClick = { navigator.push(Route.ToolbarSearch) },
                    )
                }
                item {
                    ArrowPreference(
                        title = "COUI 17 Settings Top Bar",
                        summary = "116dp large title at 40sp, capsule search, condensed-light glow",
                        onClick = { navigator.push(Route.SettingsHomeTopBar) },
                    )
                }
            }
        }
    }
}
