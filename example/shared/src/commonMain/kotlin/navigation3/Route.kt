// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package navigation3

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Type-safe navigation keys for Navigation3.
 * Each destination is a NavKey (data object/data class) and can be saved/restored in the back stack.
 */
@Serializable
sealed interface Route : NavKey {
    /** The three-tab main shell. */
    @Serializable
    data object Main : Route

    /** A component category page, by [catalog.DemoCategory.id]. */
    @Serializable
    data class Category(val id: String) : Route

    /** A component demo page, by [catalog.Demo.id]. */
    @Serializable
    data class Demo(val id: String) : Route

    @Serializable
    data object Search : Route

    /** A design spec page: `icons`, `colors` or `text_styles`. */
    @Serializable
    data class Design(val page: String) : Route

    @Serializable
    data object About : Route

    @Serializable
    data object License : Route

    @Serializable
    data object Lab : Route

    /** A lab page, by its id in the lab registry. */
    @Serializable
    data class LabPage(val id: String) : Route

    /** A navigation test page; the id only keeps repeated pushes distinct. */
    @Serializable
    data class Navigation(val id: String) : Route
}

/**
 * Registers every [Route] for back stack saving. Add new routes here as well.
 */
val RouteSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Main::class)
        subclass(Route.Category::class)
        subclass(Route.Demo::class)
        subclass(Route.Search::class)
        subclass(Route.Design::class)
        subclass(Route.About::class)
        subclass(Route.License::class)
        subclass(Route.Lab::class)
        subclass(Route.LabPage::class)
        subclass(Route.Navigation::class)
    }
}
