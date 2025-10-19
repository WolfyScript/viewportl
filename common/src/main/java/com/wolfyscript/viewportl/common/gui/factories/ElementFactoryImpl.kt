package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.elements.*
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.factories.ElementFactory

class ElementFactoryImpl : ElementFactory {

    override fun button(properties: ButtonProperties): Button = setupButton(properties)

    override fun slot(properties: SlotProperties): StackInputSlot = setupSlot(properties)

}