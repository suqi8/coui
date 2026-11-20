# Page scaffold and blur bridge

Two files to copy into the app (any package; shown as `ui`). They are the same pattern the COUI example app ships, without its app-specific settings. Requires `coui-ui`, `coui-blur` and `coui-icons` (the back arrow `COUIIcons.Back`).

- `CouiBlur.kt` — the page backdrop and the bridges that hand it to coui-ui (top bar glow, dialogs / sheets, popups, buttons and floating bars).
- `CouiPage.kt` — `CouiPageScaffold` (any content), `CouiPage` (a lazy list page) and `isWideWindow()`.

Turn the material blur off app-wide by providing `LocalCouiBlurEnabled provides false` (e.g. from a setting).

## CouiBlur.kt

```kotlin
package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.blur.Backdrop
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.MaterialBlendMode
import io.github.suqi8.coui.kmp.blur.MaterialBlurStyle
import io.github.suqi8.coui.kmp.blur.ViewBlurStyle
import io.github.suqi8.coui.kmp.blur.couiMaterialBlur
import io.github.suqi8.coui.kmp.blur.couiViewBlur
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropProvider
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.theme.COUITheme

/** Whether the COUI 17 material blur is used at all (wire it to a user setting). */
val LocalCouiBlurEnabled = staticCompositionLocalOf { true }

/**
 * The backdrop recording the current page, provided by [CouiPageScaffold] so dialogs, sheets and
 * menus opened from the page can blur it.
 */
val LocalPageBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

/** Returns a backdrop recording a page over the window background, or null when the blur is off. */
@Composable
fun rememberPageBackdrop(): LayerBackdrop? {
    if (!LocalCouiBlurEnabled.current || !isRuntimeShaderSupported()) return null
    val background = COUITheme.colorScheme.backgroundWithCard
    return rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
}

/**
 * Bridges a backdrop to coui-ui's `LocalMaterialBackdrop` (buttons, floating bars, FAB, toolbar).
 * The backdrop must record what is behind those elements, never the elements themselves.
 */
@Composable
fun rememberMaterialBackdropProvider(backdrop: Backdrop?): MaterialBackdropProvider? {
    val active = LocalCouiBlurEnabled.current && backdrop != null && isRuntimeShaderSupported()
    return remember(backdrop, active) {
        if (active && backdrop != null) {
            MaterialBackdropProvider { spec, shape ->
                Modifier.couiViewBlur(backdrop = backdrop, style = spec.toViewBlurStyle(), shape = shape)
            }
        } else {
            null
        }
    }
}

/** Bridges a backdrop to coui-ui's `LocalPopupMaterialBlur` (dropdowns, list popups, context menus). */
@Composable
fun rememberPopupMaterialBlur(backdrop: Backdrop?): MaterialBlurProvider? {
    val active = LocalCouiBlurEnabled.current && backdrop != null && isRuntimeShaderSupported()
    return remember(backdrop, active) {
        if (active && backdrop != null) {
            MaterialBlurProvider { spec, shape ->
                Modifier.couiMaterialBlur(backdrop = backdrop, style = spec.toMaterialBlurStyle(), shape = shape)
            }
        } else {
            null
        }
    }
}

/**
 * The COUI 17 blurred panel of a dialog or bottom sheet: pass [backgroundModifier] and
 * [backgroundColor] to the component's `backgroundModifier` / `backgroundColor`.
 */
@Immutable
class PanelBlur(val backgroundModifier: Modifier, val backgroundColor: Color)

/**
 * Returns the panel blur for the page behind, or null (keep the opaque surface).
 *
 * @param style e.g. `MaterialBlurDefaults::dialog` or `MaterialBlurDefaults::bottomSheet`.
 */
@Composable
fun panelBlur(style: (isDark: Boolean) -> MaterialBlurStyle): PanelBlur? {
    val backdrop = LocalPageBackdrop.current
    if (!LocalCouiBlurEnabled.current || backdrop == null || !isRuntimeShaderSupported()) return null
    return PanelBlur(
        backgroundModifier = Modifier.couiMaterialBlur(
            backdrop = backdrop,
            style = style(COUITheme.colorScheme.isDark),
        ),
        backgroundColor = Color.Transparent,
    )
}

private fun MaterialBackdropSpec.toViewBlurStyle(): ViewBlurStyle = ViewBlurStyle(
    blurRadiusPx = blurRadiusPx,
    layer1 = layer1,
    mode1 = mode1,
    layer2 = layer2,
    mode2 = mode2,
)

private fun MaterialBlurSpec.toMaterialBlurStyle(): MaterialBlurStyle = MaterialBlurStyle(
    blurRadiusPx = blurRadiusPx,
    mixColor = mixColor,
    blendColor = blendColor,
    blendMode = when (blendMode) {
        MaterialBlurBlendMode.Overlay -> MaterialBlendMode.Overlay
        MaterialBlurBlendMode.ColorDodge -> MaterialBlendMode.ColorDodge
        MaterialBlurBlendMode.GlowOverlay -> MaterialBlendMode.GlowOverlay
        MaterialBlurBlendMode.GlowColorDodge -> MaterialBlendMode.GlowColorDodge
    },
)
```

Dialog usage (put it in `CouiPage(overlays = { … })`; `DialogButtonBar`, `DialogButtonBarAction` and `DialogDefaults` are in `io.github.suqi8.coui.kmp.layout`):

```kotlin
@Composable
fun ClearCacheDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog) // MaterialBlurDefaults::bottomSheet for sheets
    OverlayDialog(
        show = show,
        modifier = modifier,
        title = stringResource(Res.string.clear_cache_title),
        summary = stringResource(Res.string.clear_cache_summary),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        materialStroke = blur != null, // COUI 17 edge light on blurred panels
        onDismissRequest = onDismiss,
    ) {
        DialogButtonBar(
            negative = DialogButtonBarAction(stringResource(Res.string.common_cancel), onClick = onDismiss),
            positive = null,
            // A destructive action takes the neutral slot, drawn in the error colour.
            neutral = DialogButtonBarAction(stringResource(Res.string.clear_cache_confirm), onClick = onConfirm),
        )
    }
}
```

`OverlayBottomSheet` takes the same `backgroundModifier` / `backgroundColor` / `materialStroke` (`BottomSheetDefaults.backgroundColor()` as the fallback). `Window*` variants render in their own platform window: do not blur them with the page backdrop (its coordinates are the main window's). Read the component's signature before adding other parameters.

## CouiPage.kt

```kotlin
@file:OptIn(ExperimentalScrollBarApi::class)

package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.FabPosition
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.ScrollBehavior
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.SnackbarHost
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.ToolbarPosition
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.TopAppBarDefaults
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.glowScrollOffsetPx
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.TopAppBarGlow
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.overScrollVertical
import io.github.suqi8.coui.kmp.utils.rememberCOUIFlingBehavior
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic

/** The top bar layouts a [CouiPageScaffold] can use. */
enum class CouiTopBarStyle {
    /** A collapsing large title; with `wide = true` the pinned small bar of wide windows. */
    Large,

    /** Always the pinned small bar. */
    Small,

    /** The ColorOS Settings home bar: 116dp tall, 40sp title, 24dp / 18dp margins. */
    SettingsHome,
}

/** What page content needs to lay out under the page chrome. */
@Stable
class CouiPageContentScope(
    val contentPadding: PaddingValues,
    val scrollBehavior: ScrollBehavior,
    val backdrop: LayerBackdrop?,
)

/** The scroll modifiers every page list uses: scroll-end haptic, COUI over-scroll, top bar nested scroll. */
fun Modifier.couiPageScroll(scope: CouiPageContentScope): Modifier = this
    .scrollEndHaptic()
    .overScrollVertical()
    .nestedScroll(scope.scrollBehavior.nestedScrollConnection)
    .fillMaxHeight()

/** Whether the window is wide enough for the pinned small top bar: 840dp, or 600dp in landscape. */
@Composable
fun isWideWindow(): Boolean {
    val size = LocalWindowInfo.current.containerSize
    return with(LocalDensity.current) {
        val width = size.width.toDp()
        val height = size.height.toDp()
        width >= 840.dp || (width >= 600.dp && height < width)
    }
}

/**
 * A COUI page: top bar with the COUI 17 glow, the page recorded into [LocalPageBackdrop] (so
 * overlays blur it), and bar / FAB / toolbar slots that get the page as their material backdrop.
 */
@Composable
fun CouiPageScaffold(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    topBarStyle: CouiTopBarStyle = CouiTopBarStyle.Large,
    wide: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    topBarBottomContent: @Composable () -> Unit = {},
    topBarContainer: @Composable (topBar: @Composable () -> Unit) -> Unit = { topBar -> topBar() },
    topBarSearchState: TopAppBarSearchState? = null,
    snackbarHostState: SnackbarHostState? = null,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    floatingToolbar: @Composable () -> Unit = {},
    floatingToolbarPosition: ToolbarPosition = ToolbarPosition.BottomCenter,
    content: @Composable (CouiPageContentScope) -> Unit,
) {
    val backdrop = rememberPageBackdrop()
    val scrollBehavior = COUIScrollBehavior()
    val materialBackdrop = rememberMaterialBackdropProvider(backdrop)
    val popupBlur = rememberPopupMaterialBlur(backdrop)
    Scaffold(
        modifier = modifier,
        topBar = {
            // topBarContainer can wrap the bar, e.g. to anchor something to it.
            topBarContainer {
                CouiPageTopBar(
                    title = title,
                    scrollBehavior = scrollBehavior,
                    backdrop = backdrop,
                    subtitle = subtitle,
                    onBack = onBack,
                    style = topBarStyle,
                    wide = wide,
                    actions = actions,
                    searchState = topBarSearchState,
                    bottomContent = topBarBottomContent,
                )
            }
        },
        // The slots sit outside the page recording, so their material can blur the page.
        bottomBar = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { bottomBar() }
        },
        floatingActionButton = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { floatingActionButton() }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingToolbar = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { floatingToolbar() }
        },
        floatingToolbarPosition = floatingToolbarPosition,
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(state = snackbarHostState) },
    ) { innerPadding ->
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        // Read the scaffold padding while the list lays out, not here: reading it during
        // composition makes the list trail the collapsing top bar by one frame.
        val padding = remember(innerPadding, navBottom) { PageContentPadding(innerPadding, navBottom) }
        val scope = remember(padding, scrollBehavior, backdrop) {
            CouiPageContentScope(padding, scrollBehavior, backdrop)
        }
        Box(Modifier.fillMaxSize().then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)) {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalPopupMaterialBlur provides popupBlur,
            ) {
                content(scope)
            }
        }
    }
}

/** The page content padding: the scaffold padding plus a 12dp top gap, read lazily. */
@Stable
private class PageContentPadding(
    private val innerPadding: PaddingValues,
    private val navBottom: Dp,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp = innerPadding.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp = innerPadding.calculateTopPadding() + 12.dp

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp = innerPadding.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp = max(innerPadding.calculateBottomPadding(), navBottom)
}

/**
 * A [CouiPageScaffold] whose content is a lazy list with the page scroll behaviour and a scroll bar.
 *
 * @param wide Use the pinned small bar of wide windows, e.g. `wide = isWideWindow()`.
 * @param overlays Dialogs and bottom sheets of the page. They live here, beside the list, so they
 *   keep the page backdrop and are not disposed when a lazy item scrolls out of view.
 */
@Composable
fun CouiPage(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    topBarStyle: CouiTopBarStyle = CouiTopBarStyle.Large,
    wide: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    topBarBottomContent: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    listState: LazyListState = rememberLazyListState(),
    overlays: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    CouiPageScaffold(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        onBack = onBack,
        topBarStyle = topBarStyle,
        wide = wide,
        actions = actions,
        topBarBottomContent = topBarBottomContent,
        snackbarHostState = snackbarHostState,
    ) { scope ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.couiPageScroll(scope),
                contentPadding = scope.contentPadding,
                // COUI lists fling through SpringOverScroller, longer than Compose's default spline.
                flingBehavior = rememberCOUIFlingBehavior(),
            ) {
                content()
                item(key = "coui_page_tail") { Spacer(Modifier.height(32.dp)) }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(listState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                trackPadding = scope.contentPadding,
            )
            overlays()
        }
    }
}

/** The page top bar, with the COUI 17 condensed-light glow when the page is recorded into [backdrop]. */
@Composable
fun CouiPageTopBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    style: CouiTopBarStyle = CouiTopBarStyle.Large,
    wide: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    searchState: TopAppBarSearchState? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    val colors = COUITheme.colorScheme
    // A transparent bar with a glow suppresses the divider like COUI 17; a null glow keeps a solid bar.
    val barColor = if (backdrop != null) Color.Transparent else colors.backgroundWithCard
    val glow: (@Composable BoxScope.() -> Unit)? = if (backdrop != null) {
        {
            TopAppBarGlow(
                backdrop = backdrop,
                scrollOffsetPx = scrollBehavior.glowScrollOffsetPx(),
                isDark = colors.isDark,
                baseColor = colors.backgroundWithCard,
                enabled = true,
            )
        }
    } else {
        null
    }
    val navigationIcon: @Composable () -> Unit = {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = COUIIcons.Back,
                    contentDescription = null,
                    tint = colors.labelPrimary,
                )
            }
        }
    }
    if (style == CouiTopBarStyle.Small || (wide && style == CouiTopBarStyle.Large)) {
        SmallTopAppBar(
            title = title,
            modifier = modifier,
            subtitle = subtitle,
            color = barColor,
            scrollBehavior = scrollBehavior,
            glow = glow,
            navigationIcon = navigationIcon,
            actions = actions,
            bottomContent = bottomContent,
        )
    } else {
        // The settings home bar keeps the default title morph, so the small title stays pinned.
        val home = style == CouiTopBarStyle.SettingsHome
        TopAppBar(
            title = title,
            modifier = modifier,
            subtitle = subtitle,
            color = barColor,
            scrollBehavior = scrollBehavior,
            expandedHeight = if (home) 116.dp else TopAppBarDefaults.ExpandedHeight,
            largeTitleFontSize = if (home) 40.sp else TopAppBarDefaults.largeTitleFontSize(),
            titlePadding = if (home) 24.dp else Dp.Unspecified,
            largeTitleBottomMargin = if (home) 18.dp else Dp.Unspecified,
            glow = glow,
            navigationIcon = navigationIcon,
            actions = actions,
            searchState = searchState,
            bottomContent = bottomContent,
        )
    }
}
```

Notes:

- A search under the top bar is always linked with the bar (SKILL.md core rule 8):

```kotlin
var searching by rememberSaveable { mutableStateOf(false) }
var query by rememberSaveable { mutableStateOf("") }
val searchState = rememberTopAppBarSearchState(active = searching)
val pageListState = rememberLazyListState() // keeps the page's scroll position behind the search

CouiPageScaffold(
    title = stringResource(Res.string.settings),
    topBarStyle = CouiTopBarStyle.SettingsHome,
    topBarSearchState = searchState,
    topBarBottomContent = {
        TopAppBarSearchBar(
            query = query,
            onQueryChange = { query = it },
            expanded = searching,
            onExpandedChange = { searching = it; if (!it) query = "" },
            cancelText = stringResource(Res.string.cancel),
            label = stringResource(Res.string.search_hint),
        )
    },
) { scope ->
    Box(Modifier.fillMaxSize()) {
        if (searchState.isContentVisible) {
            LazyColumn(
                modifier = Modifier.couiPageScroll(scope).topAppBarSearchContent(searchState),
                state = pageListState,
                contentPadding = scope.contentPadding,
            ) { /* the page */ }
        }
        if (searchState.isResultsVisible) {
            LazyColumn(
                modifier = Modifier.couiPageScroll(scope).topAppBarSearchResults(searchState),
                contentPadding = scope.contentPadding,
            ) { /* the results for query */ }
        }
    }
}
```

  `TopAppBarSearchSlot(inputField = { InputField(expanded = false, …) })` is only for a capsule that opens something else (named argument; the slot already has the 16dp page margin, so never put a `SearchBar` or `TopAppBarSearchBar` in it).
- A main shell with a bottom bar records the pager into its own backdrop and gives the bar `LocalMaterialBackdrop`; each tab page is still a `CouiPageScaffold`. Pass the bar height to the pages so their lists end above it.
- Components that live in page slots (bottom bars, FAB, floating toolbar) go into the scaffold slots, not into the list.
