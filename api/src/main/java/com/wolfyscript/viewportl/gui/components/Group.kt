package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.*

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

}

data class GroupProperties(
    val scope: ComponentScope,
    val styles: RenderProperties.() -> Unit,
    val content: ComponentScope.() -> Unit
)
