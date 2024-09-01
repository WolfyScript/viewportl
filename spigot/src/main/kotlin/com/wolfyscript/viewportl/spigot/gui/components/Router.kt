package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.WindowImpl
import com.wolfyscript.viewportl.common.gui.components.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.components.RouterImpl
import com.wolfyscript.viewportl.common.gui.components.RouterScopeImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.common.gui.reactivity.TriggerImpl
import com.wolfyscript.viewportl.gui.components.Route
import com.wolfyscript.viewportl.gui.components.RouterProperties
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.router.ActivePath
import java.util.*

fun setupRouter(properties: RouterProperties) {
    val runtime = properties.runtime.into()
    val reactiveSource = runtime.reactiveSource
    val buildContext = runtime.buildContext

    // Create signals to control routing
    val history: ReadWriteSignal<Deque<ActivePath>> = reactiveSource.createSignal(ArrayDeque<ActivePath>().apply { add(ActivePath()) })
    val currentPath: ActivePath? by reactiveSource.createMemo { history.get().peek() } // fetch the latest path from the history. Only notify subscribers when it changed!

    val router = RouterImpl(buildContext.currentParent, runtime.viewportl, buildContext)
    // calculate routes
    val routerScope = RouterScopeImpl(runtime, router, buildContext, history)
    properties.routes(routerScope)

    // add component to graph
    val id = buildContext.addComponent(router)

    // Keep track if selected route has changed
    val selectedRoute: Route by reactiveSource.createMemo<Route> {
        currentPath?.let { path ->
            routerScope.routes.firstOrNull { route -> route.path.matches(path) }
        }
    }

    // We don't know which route to choose on setup time, therefor this will basically terminate.
    // Then it will proceed to construct the child components once the effect runs
    // TODO: Outlet support!
    reactiveSource.createEffect {
        // Update component when route changes
        val routeOwner = reactiveSource.createTrigger()
        with(selectedRoute.view) { // this subscribes to the selected route memo
            buildContext.enterComponent(router) // Make sure to create the route as a child of the router component
            reactiveSource.runWithObserver((routeOwner as TriggerImpl).id) {
                ComponentScopeImpl(runtime.into()).consume()
            }
            buildContext.exitComponent()
        }

        // Clear all child components (the route component)
        reactiveSource.createCleanup {
            val graph = runtime.into().modelGraph
            graph.clearNode(id)
        }
    }

}