package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.*

fun group(runtime: ViewRuntime, styles: RenderProperties.() -> Unit = {}, content: ComponentScope.() -> Unit) = component(runtime) {
    content(this)
}

interface NativeComponentGroup : NativeComponent {
    /**
     * The children of this Component; or an empty Set if there are no children.
     *
     * @return The child Components of this Component.
     */
    fun childComponents(): Set<NativeComponent>

    /**
     * Gets the direct child Component, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Component.
     * @return The child Component; or empty Component.
     */
    fun getChild(id: String?): Optional<out NativeComponent?>?

    fun findNextOutlet() : Outlet? {
        for (childComponent in childComponents()) {
            if (childComponent is Outlet) return childComponent
            if (childComponent is NativeComponentGroup) {
                val outlet = childComponent.findNextOutlet()
                if (outlet != null) return outlet
            }
        }
        return null
    }

}
