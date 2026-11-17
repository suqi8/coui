// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.CustomSnackbar
import io.github.suqi8.coui.kmp.basic.SnackbarHost
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_auto_dismiss
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_content_body
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_content_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_dismiss_animation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_dismissed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_host_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_press_feedback
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_showing
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_touch_slidable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_custom_snackbar_variant_host
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CustomSnackbar`, listed in its category and found by the search. */
internal val CustomSnackbarDemo = Demo(
    id = "custom_snackbar",
    apiName = "CustomSnackbar",
    title = Res.string.demo_custom_snackbar_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { CustomSnackbarPlayground() }
        variant(key = "host", title = Res.string.demo_custom_snackbar_variant_host) {
            CustomSnackbarHostContent()
        }
    },
    searchTerms = Res.string.demo_custom_snackbar_search_terms,
)

/** The auto-dismiss choices; null keeps the snackbar until it is slid away. */
private val AutoDismissMillis: List<Long?> = listOf(null, 2000L, 4000L, 8000L)

@Composable
private fun ColumnScope.CustomSnackbarPlayground() {
    var visible by rememberSaveable { mutableStateOf(false) }
    var dismissCount by rememberSaveable { mutableIntStateOf(0) }
    var touchSlidable by rememberSaveable { mutableStateOf(true) }
    var pressFeedback by rememberSaveable { mutableStateOf(true) }
    var dismissWithAnimation by rememberSaveable { mutableStateOf(true) }
    var autoDismissIndex by rememberSaveable { mutableIntStateOf(2) }

    PlaygroundPreview {
        val dismissed = stringResource(Res.string.demo_custom_snackbar_dismissed, dismissCount)
        val label = stringResource(if (visible) Res.string.demo_custom_snackbar_showing else Res.string.demo_custom_snackbar_show)
        TextButton(
            text = "$label · $dismissed",
            onClick = { visible = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !visible,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(96.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (visible) {
                CustomSnackbar(
                    onDismiss = {
                        visible = false
                        dismissCount++
                    },
                    touchSlidable = touchSlidable,
                    pressFeedback = pressFeedback,
                    dismissWithAnimation = dismissWithAnimation,
                    autoDismissMillis = AutoDismissMillis[autoDismissIndex],
                ) {
                    SnackbarCard(
                        title = stringResource(Res.string.demo_custom_snackbar_content_title),
                        body = stringResource(Res.string.demo_custom_snackbar_content_body),
                    )
                }
            }
        }
    }
    val autoItems = listOf(stringResource(Res.string.common_off), "2 s", "4 s", "8 s")
    val slideTitle = stringResource(Res.string.demo_custom_snackbar_touch_slidable)
    val pressTitle = stringResource(Res.string.demo_custom_snackbar_press_feedback)
    val fadeTitle = stringResource(Res.string.demo_custom_snackbar_dismiss_animation)
    val autoTitle = stringResource(Res.string.demo_custom_snackbar_auto_dismiss)
    PlaygroundControls {
        item {
            SwitchPreference(
                checked = touchSlidable,
                onCheckedChange = { touchSlidable = it },
                title = slideTitle,
                summary = "touchSlidable",
            )
        }
        item {
            SwitchPreference(
                checked = pressFeedback,
                onCheckedChange = { pressFeedback = it },
                title = pressTitle,
                summary = "pressFeedback",
            )
        }
        item {
            SwitchPreference(
                checked = dismissWithAnimation,
                onCheckedChange = { dismissWithAnimation = it },
                title = fadeTitle,
                summary = "dismissWithAnimation",
            )
        }
        item {
            OverlayDropdownPreference(
                items = autoItems,
                selectedIndex = autoDismissIndex,
                title = autoTitle,
                summary = "autoDismissMillis",
                onSelectedIndexChange = { autoDismissIndex = it },
            )
        }
    }
}

/**
 * The [SnackbarData][io.github.suqi8.coui.kmp.basic.SnackbarData] overload in a local
 * [SnackbarHost]: the host times the snackbar out, sliding it down dismisses the data.
 */
@Composable
private fun CustomSnackbarHostContent() {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextButton(
            text = stringResource(Res.string.demo_custom_snackbar_show),
            onClick = { scope.launch { hostState.showSnackbar(getString(Res.string.demo_custom_snackbar_host_message)) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(120.dp),
        ) {
            SnackbarHost(state = hostState, modifier = Modifier.fillMaxSize(), canSwipeToDismiss = false) { data ->
                CustomSnackbar(data = data, touchSlidable = true, pressFeedback = true) {
                    SnackbarCard(
                        title = data.visuals.message,
                        body = stringResource(Res.string.demo_custom_snackbar_content_body),
                    )
                }
            }
        }
    }
}

/** The custom surface of the demo snackbars: a shadowed card with a title and a body. */
@Composable
private fun SnackbarCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp),
        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
        shadow = CardDefaults.shadow(),
    ) {
        Text(text = title)
        Text(
            text = body,
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
    }
}
