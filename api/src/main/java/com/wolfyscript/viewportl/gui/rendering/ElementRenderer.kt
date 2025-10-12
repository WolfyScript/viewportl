package com.wolfyscript.viewportl.gui.rendering

import com.wolfyscript.viewportl.gui.elements.Element

interface ElementRenderer<C: Element, X: RenderContext> {

    fun render(context: X, component: C)

}