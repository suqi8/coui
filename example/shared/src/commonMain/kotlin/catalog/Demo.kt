// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.AddCircle
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.HorizontalSplit
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Messages
import io.github.suqi8.coui.kmp.icon.extended.Months
import io.github.suqi8.coui.kmp.icon.extended.MoreCircle
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.icon.extended.Tune
import io.github.suqi8.coui.kmp.icon.extended.VerticalSplit
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_group_basic
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_group_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_group_structure
import io.github.suqi8.coui.kmp.shared.generated.resources.category_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.category_buttons_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_dialogs
import io.github.suqi8.coui.kmp.shared.generated.resources.category_dialogs_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_display
import io.github.suqi8.coui.kmp.shared.generated.resources.category_display_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_feedback
import io.github.suqi8.coui.kmp.shared.generated.resources.category_feedback_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_input
import io.github.suqi8.coui.kmp.shared.generated.resources.category_input_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_layout
import io.github.suqi8.coui.kmp.shared.generated.resources.category_layout_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_menus
import io.github.suqi8.coui.kmp.shared.generated.resources.category_menus_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_navigation
import io.github.suqi8.coui.kmp.shared.generated.resources.category_navigation_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_pickers
import io.github.suqi8.coui.kmp.shared.generated.resources.category_pickers_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_preference
import io.github.suqi8.coui.kmp.shared.generated.resources.category_preference_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.category_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.category_selection_summary
import org.jetbrains.compose.resources.StringResource

/**
 * The card groups of the components home page, in display order.
 */
enum class DemoGroup(val title: StringResource) {
    Basic(Res.string.catalog_group_basic),
    Overlay(Res.string.catalog_group_overlay),
    Structure(Res.string.catalog_group_structure),
}

/**
 * A component category: one row on the components home page and one category page.
 *
 * @property id The stable id used by [navigation3.Route.Category].
 */
enum class DemoCategory(
    val id: String,
    val title: StringResource,
    val summary: StringResource,
    val group: DemoGroup,
) {
    Display("display", Res.string.category_display, Res.string.category_display_summary, DemoGroup.Basic),
    Buttons("buttons", Res.string.category_buttons, Res.string.category_buttons_summary, DemoGroup.Basic),
    Selection("selection", Res.string.category_selection, Res.string.category_selection_summary, DemoGroup.Basic),
    Input("input", Res.string.category_input, Res.string.category_input_summary, DemoGroup.Basic),
    Pickers("pickers", Res.string.category_pickers, Res.string.category_pickers_summary, DemoGroup.Basic),
    Dialogs("dialogs", Res.string.category_dialogs, Res.string.category_dialogs_summary, DemoGroup.Overlay),
    Menus("menus", Res.string.category_menus, Res.string.category_menus_summary, DemoGroup.Overlay),
    Feedback("feedback", Res.string.category_feedback, Res.string.category_feedback_summary, DemoGroup.Overlay),
    Navigation("navigation", Res.string.category_navigation, Res.string.category_navigation_summary, DemoGroup.Structure),
    Layout("layout", Res.string.category_layout, Res.string.category_layout_summary, DemoGroup.Structure),
    Preference("preference", Res.string.category_preference, Res.string.category_preference_summary, DemoGroup.Structure),
    ;

    /** The category icon on the home page. */
    val icon: ImageVector
        get() = when (this) {
            Display -> COUIIcons.Image
            Buttons -> COUIIcons.AddCircle
            Selection -> COUIIcons.Ok
            Input -> COUIIcons.Edit
            Pickers -> COUIIcons.Months
            Dialogs -> COUIIcons.Messages
            Menus -> COUIIcons.MoreCircle
            Feedback -> COUIIcons.Info
            Navigation -> COUIIcons.HorizontalSplit
            Layout -> COUIIcons.VerticalSplit
            Preference -> COUIIcons.Tune
        }

    companion object {
        fun byId(id: String): DemoCategory? = entries.find { it.id == id }
    }
}

/**
 * One component demo page.
 *
 * Held in top-level `val`s built once, so instances keep their identity; [Stable] rather than
 * [Immutable] because [page] holds a lambda.
 *
 * @property id The stable id used by [navigation3.Route.Demo] and the search.
 * @property apiName The library API name, never translated, e.g. `FloatingNavigationBar`.
 * @property title The localized component name.
 * @property category The category the demo is listed under.
 * @property page The page content.
 * @property keywords Extra search terms in either language, e.g. aliases.
 * @property isNew Whether the component is new in COUI 17; shown as a badge.
 */
@Stable
class Demo(
    val id: String,
    val apiName: String,
    val title: StringResource,
    val category: DemoCategory,
    val page: DemoPage,
    val keywords: List<String> = emptyList(),
    val isNew: Boolean = false,
)

/**
 * The content of a [Demo].
 */
@Stable
sealed interface DemoPage {
    /**
     * A list of variants hosted by the demo page scaffold: titled sections and a playground. Use
     * `variant` and `playground` from `demo.framework`.
     */
    class Variants(val content: LazyListScope.() -> Unit) : DemoPage

    /**
     * A demo that owns the whole screen, for components that live in page slots (bars, floating
     * buttons and toolbars, pull to refresh). Build it on `ui.AppPageScaffold`. It is always
     * pushed full screen, also on wide windows.
     */
    class FullScreen(val content: @Composable (onBack: () -> Unit) -> Unit) : DemoPage
}
