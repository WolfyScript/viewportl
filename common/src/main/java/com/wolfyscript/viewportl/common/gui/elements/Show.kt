package com.wolfyscript.viewportl.common.gui.elements

import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.elements.Show
import com.wolfyscript.viewportl.gui.elements.ShowProperties
import com.wolfyscript.viewportl.gui.reactivity.createMemo

internal fun setupShow(properties: ShowProperties) {
    val runtime = properties.scope.runtime.into()
    val reactiveSource = runtime.reactiveSource

    // Create signals to control condition
    val show = ShowImpl(properties.scope.parent?.element, runtime.viewportl)

    // add component to graph
    val id = (properties.scope as ComponentScopeImpl).setElement(show)

    // Keep track if condition result has changed
    val condition: Boolean by reactiveSource.createMemo(false) { properties.condition() }

    reactiveSource.createEffect {
        // Update component when condition changes
        if (condition) {
            properties.content(properties.scope)
        } else {
            properties.fallback(properties.scope)
        }
        // Clear all child components (the route component)
        reactiveSource.createCleanup {
            val graph = runtime.into().model
            graph.clearNodeChildren(id)
        }
    }

}

@StaticNamespacedKey(key = "show")
class ShowImpl(
    parent: Element?,
    viewportl: Viewportl,
) : AbstractElementImpl<Show>("", viewportl, parent), Show