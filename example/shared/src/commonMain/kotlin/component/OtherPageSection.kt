// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import LocalNavigator
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.preference.ArrowPreference
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
            ArrowPreference(
                title = "PullToRefresh Test",
                summary = "Navigate to a PullToRefresh Page",
                onClick = {
                    navigator.push(Route.PullToRefresh)
                },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ArrowPreference(
                title = "Navigation test",
                summary = "Navigate to a Navigation Page",
                onClick = { navigator.push(Route.Navigation(Random.nextLong().toString())) },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ArrowPreference(
                title = "MultiScaffold Test",
                summary = "Navigate to a MultiScaffold Page",
                onClick = { navigator.push(Route.MultiScaffold) },
            )
        }
    }
}
