---
name: coui-app
description: Write app screens with the COUI Compose Multiplatform library (io.github.suqi8.coui.kmp — ColorOS 17 / COUI 17 design) the way ColorOS itself builds them — settings-style pages, grouped cards, preference rows, collapsing top bars with a search capsule, dialogs / bottom sheets / menus with the COUI 17 material blur, floating navigation bars, navigation with system back, bilingual strings. Use whenever code imports io.github.suqi8.coui.kmp, the user wants a ColorOS / COUI / OPPO / OnePlus style screen, or asks to "用 COUI 写", "做成 ColorOS 风格", "加一个设置页", "加个页面" in a project that depends on coui-ui / coui-preference. Also for porting Miuix (top.yukonga.miuix.kmp) or COUI 1.x code to COUI 2.0 ("从 Miuix 迁移", "升级到 COUI 17"). Not for changing the COUI library itself (use the repo's CLAUDE.md / create-component for that).
---

# Writing apps with COUI

COUI is a Compose Multiplatform port of the ColorOS 17 component kit (COUI 17). Screens look right only when they are assembled the way ColorOS assembles them: a page scaffold with a collapsing top bar, content in rounded cards on the window background, rows that are preferences, overlays that blur the page behind them. This skill is the recipe. When something here disagrees with the library's KDoc or source, the source wins — read it before calling an API you have not used yet.

Supported targets: Android, iOS, macOS, Desktop (JVM), Web (Wasm / JS). Put UI in `commonMain`.

If the code still imports `top.yukonga.miuix.kmp` or uses COUI 1.x names (`colorScheme.onBackground`, `textStyles.main`, `PlainTooltip`, …), port it with `references/migration.md` first.

## 1. Setup

Maven Central, group `io.github.suqi8.coui.kmp`; use the latest version from Maven Central (or the version the project already pins).

| Module | Use |
| --- | --- |
| `coui-ui` | Core components, theme, Scaffold, bars, dialogs, haptics |
| `coui-preference` | Preference rows (`SwitchPreference`, `ArrowPreference`, …), dropdown menus, popups |
| `coui-blur` | Backdrops and the COUI 17 material / view blur (Android needs minSdk 33 for the effect) |
| `coui-icons` | Extended icons (`COUIIcons.Back`, `COUIIcons.Settings`, …) |
| `coui-squircle` | Smooth (superellipse) corners |
| `coui-nav` | Navigation runtime with COUI page transitions and gesture back |

Kotlin Multiplatform: `commonMain.dependencies { implementation("io.github.suqi8.coui.kmp:coui-ui:<version>") … }`; Android-only projects use the `-android` artifacts (`coui-ui-android`, …). Strings use Compose Multiplatform resources: apply the `org.jetbrains.compose` plugin with `compose.components.resources`, put XML under `src/commonMain/composeResources/`, and import `Res` from the generated package (`<group>.<module>.generated.resources`, or the one set in `compose.resources { packageOfResClass = … }`).

Theme — the controller is immutable, so rebuild it when an app setting changes:

```kotlin
@Composable
fun App(settings: AppSettings) { // your own state holder, e.g. a class of mutableStateOf fields
    val controller = remember(settings.colorMode) {
        ThemeController(settings.colorMode) // ColorSchemeMode.System, Light, Dark, MonetSystem, MonetLight, MonetDark
    }
    COUITheme(controller = controller) { AppNavigation() }
}
```

A "dark mode" switch on some page writes `settings.colorMode`; it does not hold its own state.

## 2. Core rules

1. **Always inside a COUI `Scaffold`.** Every `Overlay*` component (dialogs, bottom sheets, dropdown preferences, list popups, context menus) renders in the root Scaffold's popup host. Without a Scaffold they do not show. Each page has its own Scaffold; nested Scaffolds delegate to the root.
2. **Theme tokens only.** Colours from `COUITheme.colorScheme.*`, text from `COUITheme.textStyles.*`. No hex colours for UI (brand artwork and sample swatches are the only exceptions — comment why).
3. **Settings layout = category title + card + card list group + preference rows.** Never draw rows, dividers or row paddings by hand (section 4).
4. **Use the library component that matches the ColorOS widget** (`references/components.md`) instead of composing a look-alike from `Box` / `Text`.
5. **Material blur is plumbing.** Overlays and bars only blur when the page is recorded into a backdrop and handed to them (section 6). Keep the opaque fallback for platforms without runtime shaders.
6. **No hard-coded user-facing text**: Compose resources, `values/` + `values-zh/` (section 7).
7. **Haptics**: library widgets fire their own. For custom interactions: `val haptic = rememberCouiHaptic(); haptic(CouiHapticEffect.Switch)`.
8. **A top-bar search is linked with the bar, like the ColorOS Settings search.** A search box under a top bar that the user types into is always `TopAppBarSearchBar` + `rememberTopAppBarSearchState` passed to the bar (`CouiPageScaffold(topBarSearchState = …)`), with the page under `Modifier.topAppBarSearchContent(state)` and the results in their own list under `Modifier.topAppBarSearchResults(state)` (section 3). Never a static `TopAppBarSearchSlot` + `InputField` that filters in place, never a second search row swapped in for the bar, never a separate search page. `TopAppBarSearchSlot` is only for a capsule that opens something else.
9. **Your own composables follow the library conventions**: required params, then `modifier: Modifier = Modifier` (any second `Modifier` param right after it), then defaulted params, trailing content lambda; every public composable that emits UI takes a `modifier` and applies it to its root; a `Defaults` object for dimensions and colour factories; `@Immutable` colour classes.

## 3. A page

Copy the page scaffold from `references/scaffold.md` into the project (`CouiBlur.kt`, `CouiPage.kt`). It wires the collapsing top bar, the COUI 17 top-bar glow, the page backdrop for overlays, the list scroll behaviour, the scroll bar and a snackbar host. A settings-style page:

```kotlin
@Composable
fun DisplaySettingsPage(
    settings: AppSettings,
    onBack: () -> Unit,
    onOpenWallpaper: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var textSize by rememberSaveable { mutableIntStateOf(1) }
    var showClearCache by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    CouiPage(
        title = stringResource(Res.string.display_title),
        modifier = modifier,
        onBack = onBack,
        wide = isWideWindow(),
        snackbarHostState = snackbarHostState,
        // Dialogs and sheets go here, not inside a lazy item: an item scrolled out of view
        // disposes the dialog with it.
        overlays = {
            ClearCacheDialog(
                show = showClearCache,
                onConfirm = {
                    showClearCache = false
                    scope.launch { snackbarHostState.showSnackbar(getString(Res.string.display_cache_cleared)) }
                },
                onDismiss = { showClearCache = false },
            )
        },
    ) {
        item(key = "appearance") {
            PreferenceCategory(title = stringResource(Res.string.display_appearance))
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CardListGroup {
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.display_dark_mode),
                            checked = settings.colorMode == ColorSchemeMode.Dark,
                            onCheckedChange = { settings.colorMode = if (it) ColorSchemeMode.Dark else ColorSchemeMode.System },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            title = stringResource(Res.string.display_text_size),
                            items = listOf(
                                stringResource(Res.string.display_text_size_small),
                                stringResource(Res.string.display_text_size_medium),
                                stringResource(Res.string.display_text_size_large),
                            ),
                            selectedIndex = textSize,
                            onSelectedIndexChange = { textSize = it },
                        )
                    }
                    item { ArrowPreference(title = stringResource(Res.string.display_wallpaper), onClick = onOpenWallpaper) }
                }
            }
        }
        item(key = "storage") {
            PreferenceCategory(title = stringResource(Res.string.display_storage))
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CardListGroup {
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.display_clear_cache),
                            onClick = { showClearCache = true },
                        )
                    }
                }
            }
        }
    }
}
```

Measurements: a card is inset 16dp from the page edges and 16dp from the next card — **add them yourself** with `Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)` on every `Card`, as above. The card corner (24dp in COUI 17), row heights, row insets and dividers are library defaults: do not re-add those.

Category titles: pages with several topics put a `PreferenceCategory` above each card. A top-level page (like the ColorOS Settings home) may leave its cards without titles.

Top bars: collapsing large title on narrow windows, small pinned bar on wide windows (`wide = isWideWindow()` — a helper in `CouiPage.kt`: at least 840dp wide, or 600dp wide in landscape). Pushed pages pass `onBack` to show the back button.

Home / top-level page (ColorOS Settings home): `topBarStyle = CouiTopBarStyle.SettingsHome` (116dp bar, 40sp title, 24dp side and 18dp bottom margins; collapses into the pinned small title) with a search bar below the title. Tapping the search bar searches in place like the settings home (core rule 8 is the short form): `topBarBottomContent = { TopAppBarSearchBar(query, onQueryChange, expanded = searching, onExpandedChange = { searching = it; if (!it) query = "" }, cancelText = stringResource(Res.string.cancel), label = …) }` and `topBarSearchState = rememberTopAppBarSearchState(active = searching)` on `CouiPageScaffold` (not `CouiPage`: the page and its results need their own lists). The title fades away and the search bar rises to the top with its cancel button; put the page list under `Modifier.topAppBarSearchContent(state)` with a hoisted `LazyListState`, and the results list under `if (state.isResultsVisible)` with `Modifier.topAppBarSearchResults(state)`. The search bar leaves the search on system back. Do not navigate to a separate search page, and do not swap the top bar for a second search row.

Icons in rows go in `startAction` (`Modifier.size(24.dp)`, tinted `colorScheme.primary` for entry rows) — no extra padding, the row already spaces it.

## 4. Rows, cards, lists

- `CardListGroup { item { … } }` gives each row its card position (head / middle / tail), the rounded outer corners and the COUI dividers. **Do not add `HorizontalDivider`s** between preference rows and do not set `cardListPosition` yourself inside a group.
- Long dynamic lists that cannot live in one `CardListGroup` (a `LazyColumn` of 500 rows): emit rows as separate lazy items and pass `cardListPosition = cardListPositionOf(index, count)`.
- Value on the right of a row: `endActions = { Text(value, style = COUITheme.textStyles.bodyM, color = COUITheme.colorScheme.labelTertiary) }`.
- Choosing a row:

  | Purpose | Row |
  | --- | --- |
  | on / off | `SwitchPreference` |
  | go to another page | `ArrowPreference` |
  | run an action / open a dialog | `BasicComponent(onClick = …)` (value in `endActions`, no chevron) |
  | pick one of a few | `OverlayDropdownPreference` (or `RadioButtonPreference` rows on their own page) |
  | number / level | `SliderPreference` |
  | text | `InputPreference` / `EditTextPreference` |
  | check-mark list | `MarkPreference` |

- Section header → `PreferenceCategory(title)`; plain caption → `SmallTitle`.
- Empty lists → `EmptyState`; finished flows → `CompleteState`; short feedback → snackbar on the page's `SnackbarHostState` (pass it to `CouiPage(snackbarHostState = …)`); inline notice → `TopTips`.

## 5. Dialogs and sheets

- Put them in `CouiPage(overlays = { … })` (or anywhere inside `CouiPageScaffold`'s content), keep the `show` flag in page state.
- Buttons: `DialogButtonBar(negative = DialogButtonBarAction(cancel) { … }, positive = DialogButtonBarAction(ok) { … })`. A destructive action goes in the `neutral` slot, which COUI draws in the error colour; `recommended = true` makes the primary-filled button. The bar stacks itself when labels do not fit.
- Blur: `panelBlur(MaterialBlurDefaults::dialog)` / `::bottomSheet` from `CouiBlur.kt` → `backgroundModifier`, `backgroundColor`, and `materialStroke = blur != null`. Snippet in `references/scaffold.md`.
- `Window*` variants open a platform window (use outside any Scaffold); do not give them the page blur.

## 6. Material blur (COUI 17)

ColorOS 17 blurs what is behind bars, dialogs, sheets, menus and buttons. In COUI you:

1. Record the page into a backdrop (`rememberLayerBackdrop { drawRect(background); drawContent() }` + `Modifier.layerBackdrop(backdrop)` on the content container).
2. Hand it to whoever sits **outside** that recording: the top bar `glow` (`TopAppBarGlow`, with a transparent bar `color`), dialogs and sheets (`Modifier.couiMaterialBlur`), popups (`LocalPopupMaterialBlur`), buttons / floating bars / FAB / toolbars (`LocalMaterialBackdrop`).
3. Never let an element sample a backdrop that records itself (self-sampling): buttons inside page content cannot use the page backdrop.
4. Gate on `isRuntimeShaderSupported()` and the user's setting; return `null` providers otherwise so components keep COUI's opaque fallback.

`CouiBlur.kt` / `CouiPage.kt` in `references/scaffold.md` do all of this — copy them instead of re-deriving it.

## 7. Strings and languages

- `composeResources/values/strings_*.xml` (English, default) + `values-zh/strings_*.xml` (Simplified Chinese). Split files by feature; keys are global, so prefix them (`display_`, `about_`, …).
- `stringResource()` only inside composition. In coroutines (`showSnackbar`) use `suspend getString()`. In `LazyListScope` builders resolve inside `item { }`.
- Never store display text as state or keys; store ids / indices.
- Escape `\'`, `\"`, `&amp;`. Formats `%1$s` / `%1$d`. For a literal percent sign, format the number in code (`"$value%"`) rather than relying on `%%` handling.
- Library defaults that are English text (`CancelButton(text = "Cancel")`, …) must be passed a localized string.

## 8. Navigation and back

Use `coui-nav` (COUI page transitions, swipe back, system back):

```kotlin
@Serializable
sealed interface Route : NavKey {
    @Serializable data object Settings : Route
    @Serializable data object Display : Route
}

@Composable
fun AppNavigation(settings: AppSettings) {
    val backStack = rememberNavBackStack<Route>(Route.Settings)
    NavDisplay(backStack = backStack, onBack = { backStack.removeLastOrNull() }) {
        entry<Route.Settings> { SettingsPage(onOpenDisplay = { backStack.add(Route.Display) }) }
        entry<Route.Display> {
            DisplaySettingsPage(settings, onBack = { backStack.removeLastOrNull() }, onOpenWallpaper = {})
        }
    }
}
```

Imports: `io.github.suqi8.coui.kmp.nav.core.{NavDisplay, NavKey, rememberNavBackStack}`; routes need `kotlinx.serialization`. `onBack` also receives the system back gesture. Every page in an entry is a full `CouiPage` / `CouiPageScaffold`, so each keeps its own top bar and backdrop. Details: the library guide "coui-nav".

## 9. Before you finish

Run through `references/pitfalls.md`, then check:

- every Overlay component is under a Scaffold, dialogs are in `overlays`, not in lazy items;
- every `Card` has its 16dp page and gap padding; no hex colours, hand-made dividers or padding on row icons;
- all user-facing strings in both languages; no display text as state;
- a search the user types into under a top bar is a `TopAppBarSearchBar` linked with the bar through `rememberTopAppBarSearchState` (core rule 8), not a static slot or a swapped-in row;
- every `OverlayListPopup` / `WindowListPopup` shares a `Box` with the button that opens it, never sits directly in a top bar `actions` row or another spaced `Row`;
- dark mode looks right (`COUITheme.colorScheme.isDark` drives the blur presets);
- the project's formatter / ktlint passes (Compose rules: modifier on public composables and in the right position, no reused content slots, single root emitter, line length).

## References

- `references/scaffold.md` — copyable page scaffold + blur bridge.
- `references/components.md` — ColorOS widget → COUI component map, packages, Overlay vs Window.
- `references/pitfalls.md` — mistakes seen in practice and their fixes.
- `references/migration.md` — Miuix / COUI 1.x → 2.0 renames and API changes.
