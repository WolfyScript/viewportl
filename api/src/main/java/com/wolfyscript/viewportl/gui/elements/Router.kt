package com.wolfyscript.viewportl.gui.elements

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.gui.router.ActivePath
import com.wolfyscript.viewportl.gui.router.MatchPath

/**
 * Provides access to the router and allows to create routes.
 *
 */
interface RouterScope {

    val router: Router

    fun initialPath(path: ActivePath.() -> Unit)

    /**
     * Specifies a new route with a given path and associated view.
     *
     * **Note:** Has no effect when used inside the [viewConfig] function. Routes are calculated once on setup and therefore can only be specified at top level!
     */
    fun route(path: ReceiverConsumer<MatchPath>, subRoutes: ReceiverConsumer<RouteScope> = ReceiverConsumer {}, view: ComponentScope.() -> Unit)

    /**
     * Pops the current path from history and opens the previous path in the history
     */
    fun openPrevious()

    /**
     * Opens the specified route originating from the root and pushes it onto the history.
     */
    fun openRoute(path: ReceiverConsumer<ActivePath>)

    /**
     * Opens the specified route originating from the current path and pushes it onto the history.
     */
    fun openSubRoute(path: ReceiverConsumer<ActivePath>)
}

data class RouterProperties(
    val scope: ComponentScope,
    val routes: RouterScope.() -> Unit
)

/**
 * This Element is present in the model graph and holds all the computed routes.
 *
 * The selected route view is rendered as a child of this router element in the graph
 */
interface Router : Element {

    val routes: List<Route>

}

/**
 * Represents a calculated route of a Router.
 *
 * Routes are calculated once on setup. At runtime the path is matched and the component setup function is ran **once** whenever the path matches.
 */
interface Route {

    var path: MatchPath

    var view: ComponentScope.() -> Unit

    val routes: List<Route>

}

interface RouteScope {

    val route: Route

    fun route(pathConfig: ReceiverConsumer<MatchPath>, viewConfig: ComponentScope.() -> Unit, routeConfig: ReceiverConsumer<Route>)

}
