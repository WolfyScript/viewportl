package com.wolfyscript.viewportl.spigot.gui.components

import com.wolfyscript.viewportl.common.gui.components.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.components.ShowImpl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.ShowProperties
import com.wolfyscript.viewportl.gui.reactivity.createMemo

fun setupShow(properties: ShowProperties) {
    val runtime = properties.runtime.into()
    val reactiveSource = runtime.reactiveSource
    val buildContext = runtime.buildContext

    // Create signals to control condition
    val show = ShowImpl(buildContext.currentParent, runtime.viewportl, buildContext)

    // add component to graph
    val id = buildContext.addComponent(show)

    // Keep track if condition result has changed
    val condition: Boolean by reactiveSource.createMemo(false) { properties.condition() }

    reactiveSource.createEffect {
        // Update component when condition changes
        buildContext.enterComponent(show) // Make sure to create the component as a child of the show component
        if (condition) {
            properties.content(ComponentScopeImpl(runtime.into()))
        } else {
            properties.fallback(ComponentScopeImpl(runtime.into()))
        }
        buildContext.exitComponent()

        // Clear all child components (the route component)
        reactiveSource.createCleanup {
            val graph = runtime.into().model
            graph.clearNodeChildren(id)
        }
    }

}