// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package page

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.LocalNavigator
import catalog.DemoCatalog
import catalog.DemoCategory
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.about_components
import io.github.suqi8.coui.kmp.shared.generated.resources.about_components_count
import io.github.suqi8.coui.kmp.shared.generated.resources.about_design_language
import io.github.suqi8.coui.kmp.shared.generated.resources.about_group_community
import io.github.suqi8.coui.kmp.shared.generated.resources.about_group_details
import io.github.suqi8.coui.kmp.shared.generated.resources.about_group_legal
import io.github.suqi8.coui.kmp.shared.generated.resources.about_license
import io.github.suqi8.coui.kmp.shared.generated.resources.about_name
import io.github.suqi8.coui.kmp.shared.generated.resources.about_platforms
import io.github.suqi8.coui.kmp.shared.generated.resources.about_source
import io.github.suqi8.coui.kmp.shared.generated.resources.about_telegram
import io.github.suqi8.coui.kmp.shared.generated.resources.about_third_party
import io.github.suqi8.coui.kmp.shared.generated.resources.about_title
import io.github.suqi8.coui.kmp.shared.generated.resources.about_version
import io.github.suqi8.coui.kmp.shared.generated.resources.about_version_label
import io.github.suqi8.coui.kmp.shared.generated.resources.about_view_source
import io.github.suqi8.coui.kmp.theme.COUITheme
import misc.VersionInfo
import navigation3.Route
import org.jetbrains.compose.resources.stringResource
import ui.AppPage

private const val SOURCE_URL = "https://github.com/compose-coui-ui/coui"
private const val TELEGRAM_URL = "https://t.me/YuKongA13579"
private const val LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0.txt"
private const val APP_NAME = "COUI for Compose"

// ColorOS 17 Settings, about_device_ota_item: a 240dp card with a 24dp corner, 12dp above and 16dp
// below it; the 60dp logo sits 66dp from the top, the build number (16sp medium, #D9FFFFFF) under
// it and a 40dp Small.ButtonNew.Translate button 32dp further down.
private val HeroHeight = 240.dp
private val HeroCornerRadius = 24.dp
private val HeroLogoTop = 66.dp
private val HeroButtonGap = 32.dp
private val HeroTextColor = Color.White
private val HeroBuildNumberColor = Color(0xD9FFFFFF)

// device_info_square_item_grid_preference: the colourful ratio bar is 10dp tall with a 10dp corner,
// 7dp under the title row.
private val RatioBarHeight = 10.dp
private val RatioBarGap = 2.dp

/**
 * The about page, laid out like the ColorOS 17 Settings "About device" page: the version hero card,
 * the name and component-count rows (the count with a colourful ratio bar like the storage row),
 * then the detail list and the links.
 */
@Composable
fun AboutPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    AppPage(
        title = stringResource(Res.string.about_title),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        item(key = "hero") { AboutHero(onViewSource = { uriHandler.openUri(SOURCE_URL) }) }
        item(key = "summary") {
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CardListGroup {
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.about_name),
                            endActions = { EndValue(APP_NAME) },
                        )
                    }
                    item(showDivider = false) { ComponentCountRow() }
                }
            }
        }
        item(key = "details") {
            PreferenceCategory(title = stringResource(Res.string.about_group_details))
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CardListGroup {
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.about_version_label),
                            endActions = { EndValue("${VersionInfo.VERSION_NAME} (${VersionInfo.VERSION_CODE})") },
                        )
                    }
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.about_design_language),
                            endActions = { EndValue("ColorOS 17 · COUI 17") },
                        )
                    }
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.about_platforms),
                            endActions = { EndValue("Android · iOS · Desktop · Web") },
                        )
                    }
                }
            }
        }
        item(key = "community") {
            PreferenceCategory(title = stringResource(Res.string.about_group_community))
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CardListGroup {
                    item {
                        ArrowPreference(
                            title = stringResource(Res.string.about_source),
                            endActions = { EndValue("GitHub") },
                            onClick = { uriHandler.openUri(SOURCE_URL) },
                        )
                    }
                    item {
                        ArrowPreference(
                            title = stringResource(Res.string.about_telegram),
                            endActions = { EndValue("Telegram") },
                            onClick = { uriHandler.openUri(TELEGRAM_URL) },
                        )
                    }
                }
            }
        }
        item(key = "legal") {
            PreferenceCategory(title = stringResource(Res.string.about_group_legal))
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    item {
                        ArrowPreference(
                            title = stringResource(Res.string.about_license),
                            endActions = { EndValue("Apache-2.0") },
                            onClick = { uriHandler.openUri(LICENSE_URL) },
                        )
                    }
                    item {
                        ArrowPreference(
                            title = stringResource(Res.string.about_third_party),
                            onClick = { navigator.push(Route.License) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * The version hero card. ColorOS plays a brand video behind its logo; this draws a gradient in
 * the theme's accent colours behind the library name.
 */
@Composable
private fun AboutHero(onViewSource: () -> Unit) {
    val colors = COUITheme.colorScheme
    val background = Brush.linearGradient(
        listOf(colors.violet, colors.primary, colors.cyan),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .height(HeroHeight)
            .clip(RoundedCornerShape(HeroCornerRadius))
            .background(background),
    ) {
        // A soft light in the top start corner, like the highlight of the ColorOS brand artwork.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 900f,
                    ),
                ),
        )
        Column(
            // The wordmark's line box adds about 20dp above its glyphs, so the glyphs sit where the
            // ColorOS logo does.
            modifier = Modifier.fillMaxWidth().padding(top = HeroLogoTop - 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "COUI",
                color = HeroTextColor,
                fontSize = 52.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.about_version, VersionInfo.VERSION_NAME, VersionInfo.VERSION_CODE.toString()),
                color = HeroBuildNumberColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            TextButton(
                text = stringResource(Res.string.about_view_source),
                onClick = onViewSource,
                // Less the build number's line spacing below its glyphs.
                modifier = Modifier.padding(top = HeroButtonGap - 12.dp),
                minHeight = 40.dp,
                insideMargin = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.textButtonColorsTransparent(textColor = HeroTextColor),
                textStyle = COUITheme.textStyles.buttonM,
            )
        }
    }
}

/**
 * The component count with a ratio bar of the demos per category, styled like the ColorOS storage
 * row.
 */
@Composable
private fun ComponentCountRow() {
    val colors = COUITheme.colorScheme
    val counts = remember { DemoCategory.entries.map { DemoCatalog.byCategory(it).size } }
    val total = counts.sum()
    val palette = listOf(
        colors.blue, colors.green, colors.orange, colors.purple, colors.cyan, colors.red,
        colors.yellow, colors.violet, colors.mint, colors.azure, colors.orangeVariant,
    )
    val track = colors.fillThin
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.about_components),
                style = COUITheme.textStyles.headlineXS,
                color = colors.labelPrimary,
            )
            Text(
                text = stringResource(Res.string.about_components_count, total),
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                style = COUITheme.textStyles.bodyM,
                color = colors.labelSecondary,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }
        Canvas(
            modifier = Modifier
                .padding(top = 7.dp)
                .fillMaxWidth()
                .height(RatioBarHeight)
                .clip(RoundedCornerShape(RatioBarHeight)),
        ) {
            drawRect(track)
            val gap = RatioBarGap.toPx()
            val usable = size.width - gap * (counts.size - 1)
            var x = 0f
            counts.forEachIndexed { index, count ->
                val width = usable * count / total.coerceAtLeast(1)
                drawRoundRect(
                    color = palette[index % palette.size],
                    topLeft = Offset(x, 0f),
                    size = Size(width, size.height),
                    cornerRadius = CornerRadius.Zero,
                )
                x += width + gap
            }
        }
    }
}

@Composable
private fun EndValue(text: String) {
    Text(
        text = text,
        style = COUITheme.textStyles.bodyM,
        color = COUITheme.colorScheme.labelTertiary,
        textAlign = TextAlign.End,
    )
}
