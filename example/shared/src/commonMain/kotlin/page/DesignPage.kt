// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package page

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Create
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_design
import io.github.suqi8.coui.kmp.shared.generated.resources.design_colors
import io.github.suqi8.coui.kmp.shared.generated.resources.design_colors_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_styles
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_styles_summary
import io.github.suqi8.coui.kmp.theme.COUITheme
import navigation3.Route
import org.jetbrains.compose.resources.stringResource
import ui.AppPage

/**
 * The page ids of [Route.Design].
 */
object DesignPages {
    const val ICONS = "icons"
    const val COLORS = "colors"
    const val TEXT_STYLES = "text_styles"
}

/**
 * The design tab: entries to the icon, colour and text style references.
 */
@Composable
fun DesignPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    AppPage(
        title = stringResource(Res.string.app_tab_design),
        modifier = modifier,
    ) {
        item(key = "design") {
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    item {
                        DesignRow(
                            title = stringResource(Res.string.design_icons),
                            summary = stringResource(Res.string.design_icons_summary),
                            icon = COUIIcons.Create,
                            onClick = { navigator.push(Route.Design(DesignPages.ICONS)) },
                        )
                    }
                    item {
                        DesignRow(
                            title = stringResource(Res.string.design_colors),
                            summary = stringResource(Res.string.design_colors_summary),
                            icon = COUIIcons.Image,
                            onClick = { navigator.push(Route.Design(DesignPages.COLORS)) },
                        )
                    }
                    item {
                        DesignRow(
                            title = stringResource(Res.string.design_text_styles),
                            summary = stringResource(Res.string.design_text_styles_summary),
                            icon = COUIIcons.Edit,
                            onClick = { navigator.push(Route.Design(DesignPages.TEXT_STYLES)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DesignRow(
    title: String,
    summary: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ArrowPreference(
        title = title,
        summary = summary,
        startAction = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = COUITheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        },
        onClick = onClick,
    )
}
