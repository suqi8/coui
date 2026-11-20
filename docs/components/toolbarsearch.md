# ToolbarSearch

`ToolbarSearch` puts a [SearchBar](./searchbar) in the toolbar and expands it out of the toolbar's
search icon, after COUI 17 `COUISearchBar.showInToolBar` / `hideInToolBar`.

<div style="position: relative; height: 400px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=toolbarSearch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.basic.toolbarSearchEntry
import io.github.suqi8.coui.kmp.basic.toolbarSearchItem
```

## Behaviour

With the glass material on (the default `InputField(material = true)`), the capsule starts as a
circle on the search icon, slides home while it widens, its text fades in and it blurs most half
way; the cancel button flies out of the icon from 0.6 scale and merges with the capsule like a
metaball. Meanwhile the toolbar's own icons fade and shrink to 0.8 and its title fades (the toolbar
background stays). The search glyph sits 2dp from the capsule start
(`coui_search_bar_icon_expand_search_icon_start_gap`), so it starts centred in that circle.

While the bar folds back it ignores touches; tapping the search icon then expands it again, and a
touch in the last 5% of the collapse first completes the collapse at once. Until the expansion has
settled, touching the capsule or the cancel button lights the spotlight but starts no press scale,
rubber band or deformation.

The expansion runs on a response 0.55 / bounce 0.2 spring (0.5 / 0.2 back), the toolbar icons on
0.4 / 0.3 (0.5 / 0.2 back, growing from 0.5).

With a flat capsule (`InputField(material = false)`) COUI skips the expansion
(`mFadeInFadeOutAnimation`): the whole bar fades in where it rests, while every toolbar child, the
search icon included, fades and shrinks on the same spring.

## Basic Usage

```kotlin
val searchState = rememberToolbarSearchState()
var query by remember { mutableStateOf("") }

Box {
    ToolbarSearchAnchor(state = searchState) {
        val titleColor = COUITheme.colorScheme.labelPrimary
        TopAppBar(
            title = "Title",
            titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
            navigationIcon = {
                IconButton(modifier = Modifier.toolbarSearchItem(searchState), onClick = { /* back */ }) { /* ... */ }
            },
            actions = {
                IconButton(
                    modifier = Modifier.toolbarSearchEntry(searchState),
                    onClick = { searchState.expand() },
                ) {
                    Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "Search")
                }
            },
        )
    }
    ToolbarFloatingSearchBar(
        state = searchState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) {
        SearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = searchState.expanded,
                    onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
                )
            },
            outsideEndAction = {
                CancelButton(onClick = { searchState.collapse() })
            },
            expanded = searchState.expanded,
            onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
        ) {}
    }
}
```

## API

| API | Description |
| --- | --- |
| `rememberToolbarSearchState(initiallyExpanded)` / `ToolbarSearchState` | Expanded state with `expand()` / `collapse()` |
| `ToolbarSearchState.expanded` | Whether the bar is expanded in the toolbar |
| `ToolbarSearchState.toolbarTitleAlpha` | Alpha to apply to the toolbar title colour while the bar is out |
| `ToolbarSearchAnchor(state, modifier, content)` | Hosts the toolbar content (the toolbar itself does not fade) |
| `Modifier.toolbarSearchEntry(state)` | Marks the toolbar search icon the bar grows out of; hides it while the bar is out |
| `Modifier.toolbarSearchItem(state)` | Fades and shrinks another toolbar icon along with the expansion |
| `ToolbarFloatingSearchBar(state, modifier, content)` | Hosts the `SearchBar` over the toolbar row; empty while the bar is folded away |
