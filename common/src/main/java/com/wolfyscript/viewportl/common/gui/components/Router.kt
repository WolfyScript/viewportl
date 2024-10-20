/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.common.gui.reactivity.TriggerImpl
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.reactivity.*
import com.wolfyscript.viewportl.gui.router.ActivePath
import com.wolfyscript.viewportl.gui.router.MatchPath
import java.lang.IllegalStateException
import java.util.*

internal fun setupRouter(properties: RouterProperties) {
    val runtime = properties.scope.runtime.into()
    val reactiveSource = runtime.reactiveSource

    // Create signals to control routing
    val history: ReadWriteSignal<Deque<ActivePath>> = reactiveSource.createSignal(ArrayDeque<ActivePath>().apply { add(ActivePath()) })
    val currentPath: ActivePath? by reactiveSource.createMemo(null) { history.get().peek() } // fetch the latest path from the history. Only notify subscribers when it changed!

    val router = RouterImpl(properties.scope.parent?.component, runtime.viewportl)
    // calculate routes
    val routerScope = RouterScopeImpl(runtime, router, history)
    properties.routes(routerScope)

    // add component to graph
    val id = (properties.scope as ComponentScopeImpl).setComponent(router)

    // Keep track if selected route has changed
    val selectedRoute: Route? by reactiveSource.createMemo(null) { currentPath?.let { routerScope.matchRoute(it) } }

    // We don't know which route to choose on setup time, therefor this will basically terminate.
    // Then it will proceed to construct the child components once the effect runs
    // TODO: Outlet support!
    reactiveSource.createEffect {
        if (selectedRoute == null) {
            return@createEffect
        }
        // Update component when route changes
        val routeOwner = reactiveSource.createTrigger()
        with(selectedRoute!!.view) { // this subscribes to the selected route memo
            reactiveSource.runWithObserver((routeOwner as TriggerImpl).id) {
                properties.scope.consume()
            }
        }

        // Clear all child components (the route component)
        reactiveSource.createCleanup {
            val graph = runtime.into().model
            graph.clearNodeChildren(id)
        }
    }

}

class RouteGraph {

    private val root: Node = Node(MatchPath.StaticMatcher(""))

    fun insertRoute(route: Route, startNode: Node = root) {
        var currentNode: Node = startNode
        for (sectionMatcher in route.path.sections) {
            val existingChild = currentNode.findChild(sectionMatcher)
            if (existingChild != null) {
                // This section of the path is already in the tree
                currentNode = existingChild
                continue
            }

            // insert new child node
            val newNode = Node(sectionMatcher)
            currentNode.insertChild(newNode)
            currentNode = newNode
        }

        if (currentNode.route != null) {
            throw IllegalStateException("A route with the path '${route.path}' already exists.")
        }
        currentNode.route = route

        // Also add the sub routes
        for (subRoute in route.routes) {
            insertRoute(subRoute, currentNode) // No need to walk the same path down again, start at the current node
        }
    }

    fun matchRoute(path: ActivePath): Route? {
        var currentNode: Node = root

        for (pathSection in path.pathSections) {
            val childNode = currentNode.matchChild(pathSection)
            if (childNode != null) {
                currentNode = childNode
                continue
            }
            return null
        }

        // Path was successfully matched, though the path may not have a route associated with it
        return currentNode.route
    }

    override fun toString(): String {
        return "RouteGraph(root=$root)"
    }

    class Node(
        val matcher: MatchPath.SectionMatcher,
        var route: Route? = null,
    ) {
        private val children: MutableList<Node> = mutableListOf()

        fun findChild(matcher: MatchPath.SectionMatcher): Node? {
            return children.firstOrNull { node -> node.matcher == matcher }
        }

        fun insertChild(node: Node) {
            children.add(node)
        }

        fun matchChild(section: ActivePath.Section): Node? {
            return children.firstOrNull { node -> node.matcher.matches(section) }
        }

        override fun toString(): String {
            return "{\n Matcher: $matcher, Routes: [\n${children.joinToString(separator = ", ")}] \n}"
        }

    }

}

@StaticNamespacedKey(key = "router")
class RouterImpl(
    parent: NativeComponent?,
    viewportl: Viewportl
) : AbstractNativeComponentImpl<Router>("", viewportl, parent), Router {

    val routeGraph: RouteGraph = RouteGraph()

    override val routes: MutableList<Route> = mutableListOf()

    override fun toString(): String {
        return "Routes: ${routes.joinToString(separator = "\n", prefix = "\n")}"
    }
}

class RouteImpl(
    internal val router: Router,
    override var path: MatchPath,
    override var view: ReceiverConsumer<ComponentScope>
) : Route {

    override val routes: MutableList<Route> = mutableListOf()

    override fun init(outlet: Outlet) {
//        val selectedRoute: Memo<Route> = context.reactiveSource.createMemo {
//            router.currentPath?.let { path ->
//                routes.firstOrNull { route -> route.path.matches(path) }
//            }
//        }
//
//        context.reactiveSource.createEffect {
//            selectedRoute.get()?.let { selectedRoute ->
//            }
//        }

    }

    override fun toString(): String {
        return "path: $path \n sub-routes: ${routes.joinToString(separator = "\n", prefix = "\n")}"
    }
}

class RouterScopeImpl(
    runtime: ViewRuntime<*,*>,
    override val router: Router,
    private val history: ReadWriteSignal<Deque<ActivePath>>
) : RouterScope {

    fun matchRoute(activePath: ActivePath) : Route? {
        return (router as RouterImpl).routeGraph.matchRoute(activePath)
    }

    override fun route(
        path: ReceiverConsumer<MatchPath>,
        subRoutes: ReceiverConsumer<RouteScope>,
        view: ReceiverConsumer<ComponentScope>
    ) {
        val matchPath = MatchPath()
        with(path) { matchPath.consume() }

        val route = RouteImpl(router, matchPath, view)
        (router as RouterImpl).routeGraph.insertRoute(route)

        // Init sub routes
        val routeScope = RouteScopeImpl(route)
        with(subRoutes) {
            routeScope.consume()
        }
    }

    override fun openPrevious() {
        history.update {
            it.pop()
            it
        }
    }

    override fun openRoute(path: ReceiverConsumer<ActivePath>) {
        history.update {
            it.apply {
                val newPath = ActivePath()
                with(path) { newPath.consume() }
                if (newPath != it.peek()) {
                    it.push(newPath)
                }
            }
        }
    }

    override fun openSubRoute(path: ReceiverConsumer<ActivePath>) {
        history.update {
            it.apply {
                val copy = it.peek().copy()
                with(path) { copy.consume() }
                if (copy != it.peek()) {
                    it.push(copy)
                }
            }
        }
    }

}

class RouteScopeImpl(
    override val route: RouteImpl,
) : RouteScope {

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentScope>,
        routeConfig: ReceiverConsumer<Route>
    ) {
        val path = route.path.copy()
        with(pathConfig) { path.consume() }

        (route.router as RouterImpl).routeGraph.insertRoute(RouteImpl(route.router, path, viewConfig))
    }

}
