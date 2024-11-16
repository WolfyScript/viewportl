package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.elements.setupButton
import com.wolfyscript.viewportl.common.gui.elements.setupGroup
import com.wolfyscript.viewportl.common.gui.elements.setupRouter
import com.wolfyscript.viewportl.common.gui.elements.setupShow
import com.wolfyscript.viewportl.common.gui.elements.setupSlot
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.factories.ElementFactory

class ElementFactoryImpl : ElementFactory {

    override fun group(properties: GroupProperties) = setupGroup(properties)

    override fun button(properties: ButtonProperties) = setupButton(properties)

    override fun slot(properties: SlotProperties) = setupSlot(properties)

    override fun router(properties: RouterProperties) = setupRouter(properties)

    override fun show(properties: ShowProperties) = setupShow(properties)

}