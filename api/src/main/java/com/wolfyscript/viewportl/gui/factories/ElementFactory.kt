package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.elements.*

interface ElementFactory {

    fun button(properties: ButtonProperties): Button

    fun slot(properties: SlotProperties): StackInputSlot

}