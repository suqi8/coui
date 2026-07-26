// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.basic.COUIScrollBehavior
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.CardDefaults
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.ScrollBehavior
import com.suqi8.coui.kmp.basic.SmallTopAppBar
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.VerticalScrollBar
import com.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import com.suqi8.coui.kmp.blur.BlendColorEntry
import com.suqi8.coui.kmp.blur.BlurBlendMode
import com.suqi8.coui.kmp.blur.BlurDefaults
import com.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import com.suqi8.coui.kmp.blur.layerBackdrop
import com.suqi8.coui.kmp.blur.textureBlur
import com.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import com.suqi8.coui.kmp.overlay.OverlayBottomSheet
import com.suqi8.coui.kmp.preference.ArrowPreference
import com.suqi8.coui.kmp.preference.OverlayDropdownPreference
import com.suqi8.coui.kmp.preference.SwitchPreference
import com.suqi8.coui.kmp.shared.generated.resources.Res
import com.suqi8.coui.kmp.shared.generated.resources.ic_launcher
import com.suqi8.coui.kmp.theme.COUITheme
import component.BackNavigationIcon
import component.blend.ColorBlendToken
import component.effect.BgEffectBackground
import misc.VersionInfo
import navigation3.Route
import org.jetbrains.compose.resources.painterResource
import ui.isInDarkTheme
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop
import androidx.compose.ui.graphics.BlendMode as ComposeBlendMode

@Composable
fun AboutPage(
    padding: PaddingValues,
) {
    val topAppBarScrollBehavior = COUIScrollBehavior()
    val navigator = LocalNavigator.current
    val lazyListState = rememberLazyListState()

    val scrollProgress by remember {
        derivedStateOf {
            when {
                lazyListState.firstVisibleItemIndex > 0 -> 1f

                else -> {
                    val spacer = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == "logoSpacer" }
                    if (spacer != null && spacer.size > 0) {
                        (lazyListState.firstVisibleItemScrollOffset.toFloat() / spacer.size).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                }
            }
        }
    }

    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null && scrollProgress == 1f
    val barColor = if (blurActive) {
        Color.Transparent
    } else {
        if (scrollProgress == 1f) COUITheme.colorScheme.surface else Color.Transparent
    }

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                SmallTopAppBar(
                    title = "About",
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    titleColor = COUITheme.colorScheme.onSurface.copy(
                        alpha = ((scrollProgress - 0.35f) / 0.65f).coerceIn(0f, 1f),
                    ),
                    defaultWindowInsetsPadding = false,
                    navigationIcon = {
                        BackNavigationIcon(
                            onClick = { navigator.pop() },
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            AboutContent(
                padding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                ),
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                lazyListState = lazyListState,
                scrollProgress = scrollProgress,
            )
        }
    }
}

@Composable
private fun AboutContent(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    lazyListState: LazyListState,
    scrollProgress: Float,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val uriHandler = LocalUriHandler.current
    val navigator = LocalNavigator.current

    val backdrop = rememberBlurBackdrop()
    var isOs3Effect by remember { mutableStateOf(true) }
    var showTextureSet by remember { mutableStateOf(false) }
    var blurRadius by remember { mutableFloatStateOf(60f) }
    var noiseCoefficient by remember { mutableFloatStateOf(BlurDefaults.NoiseCoefficient) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    val scrollPadding = pageContentPadding(
        padding,
        padding,
        isWideScreen,
        extraStart = WindowInsets.displayCutout.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr),
        extraEnd = WindowInsets.displayCutout.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr),
    )
    val logoPadding = pageContentPadding(
        padding,
        padding,
        isWideScreen,
        extraTop = 40.dp,
        extraStart = WindowInsets.displayCutout.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr),
        extraEnd = WindowInsets.displayCutout.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr),
    )

    val isInDark = isInDarkTheme()
    val dynamicBackground = remember { mutableStateOf(isRuntimeShaderSupported()) }
    val isFullScreenBackground = remember { mutableStateOf(true) }

    val cardBlend = if (isInDark) ColorBlendToken.Overlay_Thin_Light else ColorBlendToken.Pured_Regular_Light
    val logoBlend = remember(isInDark) {
        if (isInDark) {
            listOf(
                BlendColorEntry(Color(0xe6a1a1a1), BlurBlendMode.ColorDodge),
                BlendColorEntry(Color(0x4de6e6e6), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xff1af500), BlurBlendMode.Lab),
            )
        } else {
            listOf(
                BlendColorEntry(Color(0xcc4a4a4a), BlurBlendMode.ColorBurn),
                BlendColorEntry(Color(0xff4f4f4f), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xff1af200), BlurBlendMode.Lab),
            )
        }
    }

    val density = LocalDensity.current
    var logoHeightDp by remember { mutableStateOf(300.dp) }

    val versionCodeProgress = ((scrollProgress - 0.05f) / 0.15f).coerceIn(0f, 1f)
    val projectNameProgress = ((scrollProgress - 0.20f) / 0.15f).coerceIn(0f, 1f)
    val iconProgress = ((scrollProgress - 0.35f) / 0.15f).coerceIn(0f, 1f)

    BgEffectBackground(
        dynamicBackground = dynamicBackground.value,
        isOs3Effect = isOs3Effect,
        isFullSize = isFullScreenBackground.value,
        modifier = Modifier.fillMaxSize(),
        bgModifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
        alpha = { 1f - scrollProgress },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = logoPadding.calculateTopPadding() + 52.dp,
                    start = logoPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = logoPadding.calculateRightPadding(LayoutDirection.Ltr),
                )
                .onSizeChanged { size ->
                    with(density) { logoHeightDp = size.height.toDp() }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer {
                        clip = true
                        shape = RoundedCornerShape(24.dp)
                        alpha = 1 - iconProgress
                        scaleX = 1 - (iconProgress * 0.05f)
                        scaleY = 1 - (iconProgress * 0.05f)
                    }
                    .background(Color.White),
            ) {
                Image(
                    modifier = Modifier.size(74.dp),
                    painter = painterResource(Res.drawable.ic_launcher),
                    contentDescription = null,
                )
            }
            Text(
                modifier = Modifier.padding(top = 12.dp, bottom = 5.dp)
                    .graphicsLayer {
                        alpha = 1 - projectNameProgress
                        scaleX = 1 - (projectNameProgress * 0.05f)
                        scaleY = 1 - (projectNameProgress * 0.05f)
                    }
                    .then(
                        if (backdrop != null) {
                            Modifier
                                .textureBlur(
                                    backdrop = backdrop,
                                    shape = RoundedCornerShape(16.dp),
                                    blurRadius = 150f,
                                    noiseCoefficient = noiseCoefficient,
                                    colors = BlurDefaults.blurColors(
                                        blendColors = logoBlend,
                                    ),
                                    contentBlendMode = ComposeBlendMode.DstIn,
                                )
                        } else {
                            Modifier
                        },
                    ),
                text = "COUI for Compose",
                color = COUITheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
            )
            Text(
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1 - versionCodeProgress
                        scaleX = 1 - (versionCodeProgress * 0.05f)
                        scaleY = 1 - (versionCodeProgress * 0.05f)
                    },
                color = COUITheme.colorScheme.onSurfaceVariantSummary,
                text = "v" + VersionInfo.VERSION_NAME + " (" + VersionInfo.VERSION_CODE + ")",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }

        // Scrollable content
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().pageScrollModifiers(
                appState.enableScrollEndHaptic,
                appState.showTopAppBar,
                topAppBarScrollBehavior,
            ),
            contentPadding = PaddingValues(
                top = scrollPadding.calculateTopPadding(),
                start = scrollPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = scrollPadding.calculateRightPadding(LayoutDirection.Ltr),
            ),
        ) {
            // Transparent spacer matching logo height
            item(key = "logoSpacer") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(
                            logoHeightDp + 52.dp + logoPadding.calculateTopPadding() - scrollPadding.calculateTopPadding() + 126.dp,
                        )
                        .pointerInput(Unit) {
                            detectTapGestures {
                                showTextureSet = true
                            }
                        },
                    contentAlignment = Alignment.TopCenter,
                    content = { },
                )
            }

            item(key = "about") {
                Column(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .padding(bottom = scrollPadding.calculateBottomPadding()),
                ) {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp)
                            .then(
                                if (backdrop != null) {
                                    Modifier
                                        .textureBlur(
                                            backdrop = backdrop,
                                            shape = RoundedCornerShape(16.dp),
                                            blurRadius = blurRadius,
                                            noiseCoefficient = noiseCoefficient,
                                            colors = BlurDefaults.blurColors(
                                                blendColors = cardBlend,
                                                brightness = brightness,
                                                contrast = contrast,
                                                saturation = saturation,
                                            ),
                                        )
                                } else {
                                    Modifier
                                },
                            ),
                        colors = CardDefaults.defaultColors(
                            if (backdrop != null) Color.Transparent else COUITheme.colorScheme.surfaceContainer,
                            Color.Transparent,
                        ),
                    ) {
                        ArrowPreference(
                            title = "View Source",
                            endActions = {
                                Text(
                                    text = "GitHub",
                                    fontSize = COUITheme.textStyles.body2.fontSize,
                                    color = COUITheme.colorScheme.onSurfaceVariantActions,
                                )
                            },
                            onClick = { uriHandler.openUri("https://github.com/compose-coui-ui/coui") },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        ArrowPreference(
                            title = "Join Group",
                            endActions = {
                                Text(
                                    text = "Telegram",
                                    fontSize = COUITheme.textStyles.body2.fontSize,
                                    color = COUITheme.colorScheme.onSurfaceVariantActions,
                                )
                            },
                            onClick = { uriHandler.openUri("https://t.me/YuKongA13579") },
                        )
                    }
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .then(
                                if (backdrop != null) {
                                    Modifier
                                        .textureBlur(
                                            backdrop = backdrop,
                                            shape = RoundedCornerShape(16.dp),
                                            blurRadius = blurRadius,
                                            noiseCoefficient = noiseCoefficient,
                                            colors = BlurDefaults.blurColors(
                                                blendColors = cardBlend,
                                                brightness = brightness,
                                                contrast = contrast,
                                                saturation = saturation,
                                            ),
                                        )
                                } else {
                                    Modifier
                                },
                            ),
                        colors = CardDefaults.defaultColors(
                            if (backdrop != null) Color.Transparent else COUITheme.colorScheme.surfaceContainer,
                            Color.Transparent,
                        ),
                    ) {
                        ArrowPreference(
                            title = "License",
                            endActions = {
                                Text(
                                    text = "Apache-2.0",
                                    fontSize = COUITheme.textStyles.body2.fontSize,
                                    color = COUITheme.colorScheme.onSurfaceVariantActions,
                                )
                            },
                            onClick = {
                                uriHandler.openUri("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        ArrowPreference(
                            title = "Third Party Licenses",
                            onClick = { navigator.push(Route.License) },
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        VerticalScrollBar(
            adapter = rememberScrollBarAdapter(lazyListState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            trackPadding = scrollPadding,
        )
    }

    OverlayBottomSheet(
        show = showTextureSet,
        title = "Background Effect",
        onDismissRequest = {
            showTextureSet = false
        },
        insideMargin = DpSize(0.dp, 0.dp),
    ) {
        LazyColumn {
            item {
                val effectVariantOptions = listOf("OS2", "OS3")
                OverlayDropdownPreference(
                    title = "Effect Variant",
                    items = effectVariantOptions,
                    selectedIndex = if (isOs3Effect) 1 else 0,
                    onSelectedIndexChange = { isOs3Effect = (it == 1) },
                )

                SwitchPreference(
                    title = "Dynamic Background",
                    checked = dynamicBackground.value,
                    onCheckedChange = {
                        dynamicBackground.value = it
                    },
                )

                SwitchPreference(
                    title = "Full Screen Background",
                    checked = isFullScreenBackground.value,
                    onCheckedChange = {
                        isFullScreenBackground.value = it
                    },
                )
            }
            item { Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) }
        }
    }
}

@Suppress("FunctionName")
@Composable
private fun ValueText(text: String) {
    Text(
        text = text,
        fontSize = COUITheme.textStyles.body2.fontSize,
        color = COUITheme.colorScheme.onSurfaceVariantActions,
    )
}
