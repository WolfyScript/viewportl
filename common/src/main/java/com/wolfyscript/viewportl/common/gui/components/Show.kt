package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.common.gui.reactivity.TriggerImpl
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.Show
import com.wolfyscript.viewportl.gui.components.ShowProperties
import com.wolfyscript.viewportl.gui.reactivity.createMemo

internal fun setupShow(properties: ShowProperties) {
    val runtime = properties.scope.runtime.into()
    val reactiveSource = runtime.reactiveSource

    // Create signals to control condition
    val show = ShowImpl(properties.scope.parent?.component, runtime.viewportl)

    // add component to graph
    val id = (properties.scope as ComponentScopeImpl).setComponent(show)

    // Keep track if condition result has changed
    val condition: Boolean by reactiveSource.createMemo(false) { properties.condition() }

    reactiveSource.createEffect {
        // Update component when condition changes
        val owner = reactiveSource.createTrigger()
        if (condition) {
            reactiveSource.runWithObserver((owner as TriggerImpl).id) {
                properties.content(properties.scope)
            }
        } else {
            reactiveSource.runWithObserver((owner as TriggerImpl).id) {
                properties.fallback(properties.scope)
            }
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
    parent: NativeComponent?,
    viewportl: Viewportl,
) : AbstractNativeComponentImpl<Show>("", viewportl, parent), Show