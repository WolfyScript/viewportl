package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.*

interface ElementGroup : Element {
    /**
     * The children of this Element; or an empty Set if there are no children.
     *
     * @return The child Elements of this Element.
     */
    fun childComponents(): Set<Element>

    /**
     * Gets the direct child Element, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Element.
     * @return The child Element; or empty Element.
     */
    fun getChild(id: String?): Optional<out Element?>?

}

data class GroupProperties(
    val scope: ComponentScope,
    val styles: RenderProperties.() -> Unit,
    val content: ComponentScope.() -> Unit
)
