package com.wolfyscript.viewportl.gui.rendering

import com.wolfyscript.viewportl.gui.elements.Element

interface ElementRenderer<C: Element, X: RenderContext> {

    fun render(context: X, component: C)

    /**
     * Checks if this renderer can renderer an [Element] of a specific type and context type.
     *
     * @return true if the [Element] can be rendered by this renderer
     */
    fun canRender(elementType: Class<out Element>, contextType: Class<out RenderContext>): Boolean

}