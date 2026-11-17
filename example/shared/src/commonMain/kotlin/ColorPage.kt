// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.squircle.squircleBorder
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.Colors
import io.github.suqi8.coui.kmp.theme.darkColorScheme
import io.github.suqi8.coui.kmp.theme.lightColorScheme
import io.github.suqi8.coui.kmp.theme.platformDynamicColors
import utils.AdaptiveTopAppBar
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop

private val CamelCaseRegex = Regex("([A-Z])")
private val ColorBlockCornerRadius = 12.dp

@Immutable
private data class ColorBlockData(
    val name: String,
    val displayName: String,
    val surfaceColor: Color,
    val textColor: Color,
)

@Composable
fun ColorPage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val topAppBarScrollBehavior = COUIScrollBehavior()

    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else COUITheme.colorScheme.backgroundWithCard

    val lightColors = remember { lightColorScheme() }
    val darkColors = remember { darkColorScheme() }
    val dynLight = platformDynamicColors(dark = false)
    val dynDark = platformDynamicColors(dark = true)

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Color",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    backdrop = backdrop,
                    blurActive = blurActive,
                )
            }
        },
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()
        val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen, extraTop = 12.dp)
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.pageScrollModifiers(
                    appState.enableScrollEndHaptic,
                    appState.showTopAppBar,
                    topAppBarScrollBehavior,
                ),
                contentPadding = contentPadding,
            ) {
                item(key = "current") {
                    SmallTitle("Current Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(COUITheme.colorScheme)
                    }
                }
                item(key = "light") {
                    SmallTitle("Light Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = lightColors.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(lightColors)
                    }
                }
                item(key = "dynamic_light") {
                    SmallTitle("Dynamic Light Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = dynLight.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(dynLight)
                    }
                }
                item(key = "dark") {
                    SmallTitle("Dark Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = darkColors.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(darkColors)
                    }
                }
                item(key = "dynamic_dark") {
                    SmallTitle("Dynamic Dark Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = dynDark.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(dynDark)
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                trackPadding = contentPadding,
            )
        }
    }
}

@Composable
fun ColorsPreview(colors: Colors) {
    val colorBlocks = remember(colors) {
        val colorList = listOf(
            "primary" to colors.primary,
            "onPrimary" to colors.onPrimary,
            "error" to colors.error,
            "onError" to colors.onError,
            "disabledOnPrimaryButton" to colors.disabledOnPrimaryButton,
            "secondaryVariant" to colors.secondaryVariant,
            "disabledSecondaryVariant" to colors.disabledSecondaryVariant,
            "secondaryContainer" to colors.secondaryContainer,
            "secondary" to colors.secondary,
            "surfaceContainerHighest" to colors.surfaceContainerHighest,
            "outline" to colors.outline,
            "sliderBackground" to colors.sliderBackground,
            "dialogButton" to colors.dialogButton,
            "dragHandle" to colors.dragHandle,
            "secondaryButton" to colors.secondaryButton,
            "disabledSecondaryButton" to colors.disabledSecondaryButton,
            "disabledOnSecondaryButton" to colors.disabledOnSecondaryButton,
            "transparentButton" to colors.transparentButton,
            "onTransparentButton" to colors.onTransparentButton,
            "disabledOnTransparentButton" to colors.disabledOnTransparentButton,
            "backgroundTop" to colors.backgroundTop,
            "primaryTextOnPopup" to colors.primaryTextOnPopup,
            "press" to colors.press,
            "hover" to colors.hover,
            "focus" to colors.focus,
            "disabledProgress" to colors.disabledProgress,
            "disabledProgressBackground" to colors.disabledProgressBackground,
            "disabledSliderThumb" to colors.disabledSliderThumb,
            "disabledSliderKeyPoint" to colors.disabledSliderKeyPoint,
            "sliderThumbShadow" to colors.sliderThumbShadow,
            "disabledTextFieldBorder" to colors.disabledTextFieldBorder,
            "quarternaryNeutral" to colors.quarternaryNeutral,
            "container8" to colors.container8,
            "disabledChipText" to colors.disabledChipText,
            "focusOutline" to colors.focusOutline,
            "primaryText" to colors.primaryText,
            "labelTheme" to colors.labelTheme,
            "link" to colors.link,
            "disable" to colors.disable,
            "containerTheme" to colors.containerTheme,
            "containerThemeHalftone" to colors.containerThemeHalftone,
            "textColorHighlight" to colors.textColorHighlight,
            "errorDisabled" to colors.errorDisabled,
            "errorTextBg" to colors.errorTextBg,
            "success" to colors.success,
            "tips" to colors.tips,
            "additional" to colors.additional,
            "assist" to colors.assist,
            "background" to colors.background,
            "surface" to colors.surface,
            "backgroundElevated" to colors.backgroundElevated,
            "backgroundElevatedWithCard" to colors.backgroundElevatedWithCard,
            "backgroundWithCard" to colors.backgroundWithCard,
            "surfaceTop" to colors.surfaceTop,
            "surfaceWithCard" to colors.surfaceWithCard,
            "bar" to colors.bar,
            "bottomBar" to colors.bottomBar,
            "card" to colors.card,
            "cardBackground" to colors.cardBackground,
            "cardPressed" to colors.cardPressed,
            "labelPrimary" to colors.labelPrimary,
            "labelPrimaryVariant" to colors.labelPrimaryVariant,
            "labelSecondary" to colors.labelSecondary,
            "labelSecondaryVariant" to colors.labelSecondaryVariant,
            "labelTertiary" to colors.labelTertiary,
            "labelQuaternary" to colors.labelQuaternary,
            "labelOnColor" to colors.labelOnColor,
            "disabledNeutral" to colors.disabledNeutral,
            "hintNeutral" to colors.hintNeutral,
            "container4" to colors.container4,
            "container12" to colors.container12,
            "container16" to colors.container16,
            "containerSolid" to colors.containerSolid,
            "fillSolid" to colors.fillSolid,
            "fillThin" to colors.fillThin,
            "controls" to colors.controls,
            "divider" to colors.divider,
            "mask" to colors.mask,
            "pressBackground" to colors.pressBackground,
            "ripplePressBackground" to colors.ripplePressBackground,
            "red" to colors.red,
            "redVariant" to colors.redVariant,
            "containerRed" to colors.containerRed,
            "orange" to colors.orange,
            "orangeVariant" to colors.orangeVariant,
            "containerOrange" to colors.containerOrange,
            "yellow" to colors.yellow,
            "yellowVariant" to colors.yellowVariant,
            "containerYellow" to colors.containerYellow,
            "green" to colors.green,
            "greenVariant" to colors.greenVariant,
            "containerGreen" to colors.containerGreen,
            "mint" to colors.mint,
            "mintVariant" to colors.mintVariant,
            "containerMint" to colors.containerMint,
            "cyan" to colors.cyan,
            "cyanVariant" to colors.cyanVariant,
            "containerCyan" to colors.containerCyan,
            "azure" to colors.azure,
            "azureVariant" to colors.azureVariant,
            "containerAzure" to colors.containerAzure,
            "blue" to colors.blue,
            "blueVariant" to colors.blueVariant,
            "containerBlue" to colors.containerBlue,
            "violet" to colors.violet,
            "violetVariant" to colors.violetVariant,
            "containerViolet" to colors.containerViolet,
            "purple" to colors.purple,
            "purpleVariant" to colors.purpleVariant,
            "containerPurple" to colors.containerPurple,
            "neutralButton" to colors.neutralButton,
        )
        val colorMap = colorList.toMap()
        colorList.map { (name, color) ->
            val (surfaceColor, textColor) = when {
                name.startsWith("on") && colorMap.containsKey(name.removePrefix("on").replaceFirstChar { it.lowercase() }) -> {
                    color to colorMap[name.removePrefix("on").replaceFirstChar { it.lowercase() }]!!
                }

                !name.startsWith("on") && colorMap.containsKey("on" + name.replaceFirstChar { it.uppercase() }) -> {
                    color to colorMap["on" + name.replaceFirstChar { it.uppercase() }]!!
                }

                else -> color to (if (color.luminance() > 0.5) Color.Black else Color.White)
            }
            val displayName = name.replace(CamelCaseRegex, " $1").trim().replaceFirstChar { it.uppercase() }
            ColorBlockData(name, displayName, surfaceColor, textColor)
        }
    }

    Column(
        modifier = Modifier.padding(top = 16.dp),
    ) {
        colorBlocks.chunked(2).forEach { rowColors ->
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                rowColors.forEachIndexed { idx, block ->
                    ColorBlock(
                        displayName = block.displayName,
                        surfaceColor = block.surfaceColor,
                        textColor = block.textColor,
                        modifier = Modifier
                            .weight(1f)
                            .then(if (idx < rowColors.lastIndex) Modifier.padding(end = 16.dp) else Modifier),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorBlock(displayName: String, surfaceColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .squircleBackground(surfaceColor, ColorBlockCornerRadius)
            .squircleBorder(1.dp, textColor, ColorBlockCornerRadius),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = displayName,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp),
        )
    }
}
