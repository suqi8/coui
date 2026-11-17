// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package page

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.about_third_party
import org.jetbrains.compose.resources.stringResource
import ui.AppPage
import utils.Library
import utils.SimpleJsonParser

/**
 * The third-party licenses, read from the AboutLibraries export.
 */
@Composable
fun LicensePage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    val libraries by produceState<List<Library>?>(initialValue = null) {
        value = runCatching {
            val jsonString = Res.readBytes("files/aboutlibraries.json").decodeToString()
            SimpleJsonParser(jsonString).parseLibs().libraries
        }.getOrNull()
    }

    AppPage(
        title = stringResource(Res.string.about_third_party),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        libraries?.let { libs ->
            items(libs, key = { it.uniqueId }) { library ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                ) {
                    CardListGroup {
                        item {
                            ArrowPreference(
                                title = library.name,
                                summary = "${library.artifactVersion}, ${library.licenses.firstOrNull()}",
                                onClick = { library.website?.let { uriHandler.openUri(it) } },
                            )
                        }
                    }
                }
            }
        }
    }
}
