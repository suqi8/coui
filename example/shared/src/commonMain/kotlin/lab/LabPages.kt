// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package lab

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_banner
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_bg_effect
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_bg_effect_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_multi_scaffold
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_multi_scaffold_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_navigation_test
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_navigation_test_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_title
import lab.effect.BgEffectBackground
import navigation3.Route
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.AppPage
import ui.AppPageList
import ui.AppPageScaffold
import kotlin.random.Random

/**
 * A lab page: a legacy (non-COUI) effect or a test page kept from the Miuix days.
 *
 * @property id The id used by [Route.LabPage].
 * @property route Where the lab row navigates; defaults to [Route.LabPage] with [id].
 */
@Stable
class LabEntry(
    val id: String,
    val title: StringResource,
    val summary: StringResource,
    val route: () -> Route = { Route.LabPage(id) },
    val content: @Composable () -> Unit,
)

/**
 * The lab pages. They stay out of the component catalog and its search.
 */
object LabRegistry {
    val all: List<LabEntry> = listOf(
        LabEntry("blur", Res.string.lab_blur, Res.string.lab_blur_summary) { BlurLabPage() },
        LabEntry("bg_effect", Res.string.lab_bg_effect, Res.string.lab_bg_effect_summary) { BgEffectLabPage() },
        LabEntry(
            id = "navigation_test",
            title = Res.string.lab_navigation_test,
            summary = Res.string.lab_navigation_test_summary,
            route = { Route.Navigation(Random.nextLong().toString()) },
        ) {},
        LabEntry("multi_scaffold", Res.string.lab_multi_scaffold, Res.string.lab_multi_scaffold_summary) { MultiScaffoldTestPage() },
    )

    fun byId(id: String): LabEntry? = all.find { it.id == id }
}

/**
 * The lab index page.
 */
@Composable
fun LabPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    AppPage(
        title = stringResource(Res.string.lab_title),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        item(key = "banner") {
            TopTips(
                text = stringResource(Res.string.lab_banner),
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
            )
        }
        item(key = "entries") {
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    LabRegistry.all.forEach { entry ->
                        item(key = entry.id) {
                            ArrowPreference(
                                title = stringResource(entry.title),
                                summary = stringResource(entry.summary),
                                onClick = { navigator.push(entry.route()) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The texture and foreground blur experiments of coui-blur's highlight APIs.
 */
@Composable
fun BlurLabPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    AppPage(
        title = stringResource(Res.string.lab_blur),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        blurSection()
    }
}

/**
 * The HyperOS OS2 / OS3 shader background, full screen, with its options.
 */
@Composable
fun BgEffectLabPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    var isOs3Effect by rememberSaveable { mutableStateOf(true) }
    var dynamicBackground by rememberSaveable { mutableStateOf(isRuntimeShaderSupported()) }
    var fullSize by rememberSaveable { mutableStateOf(true) }
    AppPageScaffold(
        title = stringResource(Res.string.lab_bg_effect),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) { scope ->
        BgEffectBackground(
            dynamicBackground = dynamicBackground,
            isOs3Effect = isOs3Effect,
            isFullSize = fullSize,
            modifier = Modifier.fillMaxSize(),
        ) {
            AppPageList(scope = scope) {
                item(key = "options") {
                    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                        CardListGroup {
                            item {
                                OverlayDropdownPreference(
                                    title = "Effect variant",
                                    items = listOf("OS2", "OS3"),
                                    selectedIndex = if (isOs3Effect) 1 else 0,
                                    onSelectedIndexChange = { isOs3Effect = it == 1 },
                                )
                            }
                            item {
                                SwitchPreference(
                                    title = "Dynamic background",
                                    checked = dynamicBackground,
                                    onCheckedChange = { dynamicBackground = it },
                                )
                            }
                            item {
                                SwitchPreference(
                                    title = "Full screen background",
                                    checked = fullSize,
                                    onCheckedChange = { fullSize = it },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
