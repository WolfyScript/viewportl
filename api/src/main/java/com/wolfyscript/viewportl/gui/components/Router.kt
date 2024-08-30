package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal
import com.wolfyscript.viewportl.gui.router.ActivePath
import com.wolfyscript.viewportl.gui.router.MatchPath
import java.util.*

/**
 * A Router decides which child component it should show depending on the current path.
 *
 * ### Navigation
 * Navigation between paths is possible within the [RouterScope]. For that the [RouterScope] may be passed to child components.
 *
 */
fun router(runtime: ViewRuntime, routes: RouterScope.() -> Unit) = component(runtime) {
    runtime.viewportl.guiFactory.componentFactory.router(
        RouterProperties(
            runtime,
            routes
        )
    )
}

/**
 * Provides access to the router and allows to create routes.
 *
 */
interface RouterScope {

    val router: Router

    /**
     * Specifies a new route with a given path and associated view.
     *
     * **Note:** Has no effect when used inside the [viewConfig] function. Routes are calculated once on setup and therefore can only be specified at top level!
     */
    fun route(pathConfig: ReceiverConsumer<MatchPath>, viewConfig: ReceiverConsumer<ComponentScope>)

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
    val runtime: ViewRuntime,
    val routes: RouterScope.() -> Unit
)

/**
 *
 */
interface Router : NativeComponent {

    val routes: List<Route>
    val currentPath: ActivePath?
    val history : ReadWriteSignal<Deque<ActivePath>>

    fun open()

    fun route(pathConfig: ReceiverConsumer<MatchPath>, viewConfig: ReceiverConsumer<ComponentScope>, routeConfig: ReceiverConsumer<Route>? = null)

}

/**
 * Represents a calculated route of a Router.
 *
 * Routes are calculated once on setup. At runtime the path is matched and the component setup function is ran **once** whenever the path matches.
 */
interface Route {

    var path: MatchPath

    var view: ReceiverConsumer<ComponentScope>

    val routes: List<Route>

    fun route(pathConfig: ReceiverConsumer<MatchPath>, viewConfig: ReceiverConsumer<ComponentScope>, routeConfig: ReceiverConsumer<Route>)

    fun init(outlet: Outlet)

}
