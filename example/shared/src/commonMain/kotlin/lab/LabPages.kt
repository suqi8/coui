// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package lab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.NavigationItem
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_banner
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_bg_effect
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_bg_effect_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_liquid_glass
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_liquid_glass_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_multi_scaffold
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_multi_scaffold_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_navigation_test
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_navigation_test_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_title
import lab.effect.BgEffectBackground
import lab.liquid.IosLiquidGlassNavigationBar
import navigation3.Route
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.AppPage
import ui.AppPageList
import ui.AppPageScaffold
import utils.LocalPageBackdrop
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
        LabEntry("liquid_glass", Res.string.lab_liquid_glass, Res.string.lab_liquid_glass_summary) { LiquidGlassBarLabPage() },
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

/**
 * The iOS-like liquid glass navigation bar that used to be a shell bar style.
 */
@Composable
fun LiquidGlassBarLabPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    var selected by remember { mutableIntStateOf(0) }
    val items = remember {
        listOf(
            NavigationItem("Photos", COUIIcons.Photos),
            NavigationItem("Music", COUIIcons.Music),
            NavigationItem("Contacts", COUIIcons.Contacts),
            NavigationItem("Settings", COUIIcons.Settings),
        )
    }
    AppPageScaffold(
        title = stringResource(Res.string.lab_liquid_glass),
        modifier = modifier,
        onBack = { navigator.pop() },
        bottomBar = {
            val backdrop = LocalPageBackdrop.current
            Box(Modifier.padding(bottom = 16.dp)) {
                IosLiquidGlassNavigationBar(
                    items = items,
                    selectedIndex = selected,
                    onItemClick = { selected = it },
                    backdrop = backdrop,
                    isBlurActive = backdrop != null,
                )
            }
        },
    ) { scope ->
        AppPageList(scope = scope) {
            item(key = "image") {
                Image(
                    painter = painterResource(Res.drawable.blur_test_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
            }
            item(key = "rows") {
                Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CardListGroup {
                        repeat(30) { index ->
                            item { BasicComponent(title = "${items[selected].label} ${index + 1}") }
                        }
                    }
                }
            }
        }
    }
}
