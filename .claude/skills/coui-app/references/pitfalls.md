# Pitfalls (each one happened in practice)

## Layout

- **Double search margin.** `TopAppBarSearchSlot` already pads the capsule 16dp from the page edge. Wrapping a whole `SearchBar` in it adds the search bar's own responsive 16dp → 32dp. Put `InputField` directly in the slot.
- **Static search under the top bar.** A `TopAppBarSearchSlot` + `InputField` that filters the list in place, or a second search row swapped in for the bar, does not behave like the ColorOS Settings search: the title stays, there is no cancel button and nothing moves. Use `TopAppBarSearchBar` with `rememberTopAppBarSearchState` (SKILL.md core rule 8). `TopAppBarSearchBar` brings its own 52dp row and 16dp margin: never wrap it in the slot.
- **Search hides the page list for good.** With `if (state.isContentVisible)` around the page list, hoist its `LazyListState`; otherwise leaving the search scrolls the page back to the top.
- **Extra icon gap in rows.** `startAction` is already spaced from the title by the row. `Modifier.padding(end = 16.dp)` on the icon doubles the gap. Use `Modifier.size(24.dp)` only.
- **Hand-made dividers.** `CardListGroup` draws COUI dividers (starting at the title, hidden next to a selected row). `HorizontalDivider` between rows draws a second, wrong one.
- **Cards touching the screen edge.** `Card` has no outer margin. Every card needs `Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)`.
- **Shadows clipped.** `Card` clips (squircle surface). A shadowed child inside a card loses its shadow; put shadowed previews (cards with `shadow`, FABs, floating bars) on the page background.
- **Bottom bar covering the last rows.** Lists under a bottom bar / floating navigation bar need the bar height in their bottom content padding.
- **Settings home title.** With the 116dp settings-home bar, keep the default title morph so the small title stays pinned in the bar when collapsed; `fadeLargeTitleOnCollapse = true` leaves the bar empty.

## Overlays and blur

- **List popup as a sibling in a row.** `OverlayListPopup` / `WindowListPopup` anchor to their parent and, while shown, add a zero-size child to it. Put directly in a top bar `actions` row (spaced 10dp), that child adds a gap that pushes the button sideways until the popup has closed, and the menu anchors to the whole row. Always wrap the button and its popup in their own `Box { IconButton(…); WindowListPopup(…) }`.
- **Dialog inside a lazy item.** An `OverlayDialog` registers itself while it is composed; inside a `LazyColumn` item it disappears when that item scrolls away. Put dialogs and sheets in `CouiPage(overlays = { … })` (or beside the list).
- **Overlay without Scaffold** shows nothing. Every page that opens an `Overlay*` must be inside a `Scaffold`.
- **Pushed pages lose the blur** when only the main shell provides the page backdrop. Each page scaffold must record its own content and provide `LocalPageBackdrop` / `LocalPopupMaterialBlur` (the skill's `CouiPageScaffold` does).
- **Self-sampling.** A button inside the recorded layer must not use the page backdrop as its material — it would blur itself. Bars / FAB / toolbars sit in Scaffold slots outside the recording; in-content material previews need their own recorded image.
- **Window* + page blur.** Window variants live in another window; the page backdrop's coordinates do not match. Leave them opaque.
- **No shader support.** `isRuntimeShaderSupported()` is false on some targets / old Android: providers must return null so COUI's opaque fallback is used.

## Text and state

- **English library defaults.** `CancelButton` defaults to `"Cancel"`; pass a localized `text`. Check other components with a default `text`/`label`.
- **`stringResource` outside composition** (in a `LazyListScope` builder, a `remember {}` initializer, a coroutine) does not compile or goes stale. Builders → inside `item {}`; coroutines → `getString()`.
- **A dark mode switch that changes nothing.** `ThemeController` is immutable; keep the colour mode in app-level state and rebuild the controller with `remember(mode)`.
- **Display text as state.** A `Set<String>` of selected labels breaks when the language changes. Store indices / ids.
- **Search taps with the keyboard open.** The first tap on a result can be eaten by focus clearing; hide the keyboard (`LocalFocusManager.clearFocus()`) when the result list starts scrolling.

## Code style (ktlint + Compose rules)

- `modifier` right after the required parameters; a second `Modifier` parameter (`navigationIconModifier`) right after `modifier`, before other defaulted params. A missing default does not satisfy the rule.
- No `const val PascalCase`; constants are `private val PascalCase` or `const val SCREAMING_CASE`.
- Never call the same content lambda in two branches (`content-slot-reused`); pick the container instead, call `content` once.
- One root emitter per composable that takes a `modifier`; multi-row helpers become `ColumnScope.` extensions.
- `@Immutable` only on classes without lambdas; `@Stable` for classes holding lambdas or snapshot state.
