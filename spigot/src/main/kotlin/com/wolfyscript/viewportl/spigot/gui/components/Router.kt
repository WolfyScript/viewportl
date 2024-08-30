package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.WindowImpl
import com.wolfyscript.viewportl.common.gui.components.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.components.RouterImpl
import com.wolfyscript.viewportl.common.gui.components.RouterScopeImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.Route
import com.wolfyscript.viewportl.gui.components.RouterProperties
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.router.ActivePath
import java.lang.IllegalStateException
import java.util.*

fun setupRouter(properties: RouterProperties) {
    val runtime = properties.runtime
    val reactiveSource = runtime.into().reactiveSource
    val buildContext = (runtime.currentMenu as? WindowImpl)?.context ?: throw IllegalStateException("Cannot create button outside of window")

    // Create signals to control routing
    val history: ReadWriteSignal<Deque<ActivePath>> = reactiveSource.createSignal(ArrayDeque<ActivePath>().apply {
        add(ActivePath())
    })
    val currentPath: ActivePath? by reactiveSource.createMemo { history.get().peek() }

    val router = RouterImpl(
        null,
        properties.runtime.viewportl,
        buildContext,
        properties.runtime.currentMenu!!,
    )
    // calculate routes
    val routerScope = RouterScopeImpl(runtime, router, buildContext, history)
    properties.routes(routerScope)

    // add component to graph
    val id = runtime.into().modelGraph.addNode(router)

    // Keep track if selected route has changed
    val selectedRoute: Route by reactiveSource.createMemo<Route> {
        currentPath?.let { path ->
            routerScope.routes.firstOrNull { route -> route.path.matches(path) }
        }
    }

    // Update component when route changes
    reactiveSource.createEffect {
        with(selectedRoute.view) {
            ComponentScopeImpl(runtime.into()).consume()
        }

        // TODO: Cleanup
    }

}