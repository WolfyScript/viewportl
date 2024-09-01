package com.wolfyscript.viewportl.spigot.gui.factories

import com.wolfyscript.viewportl.common.gui.components.setupOutlet
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.factories.ComponentFactory
import com.wolfyscript.viewportl.spigot.gui.components.setupButton
import com.wolfyscript.viewportl.spigot.gui.components.setupRouter
import com.wolfyscript.viewportl.spigot.gui.components.setupShow
import com.wolfyscript.viewportl.spigot.gui.components.setupSlot

class ComponentFactoryImpl : ComponentFactory {

    override fun button(properties: ButtonProperties) = setupButton(properties)

    override fun slot(properties: SlotProperties) = setupSlot(properties)

    override fun router(properties: RouterProperties) = setupRouter(properties)

    override fun show(properties: ShowProperties) = setupShow(properties)

    override fun outlet(properties: OutletProperties) {
        setupOutlet(properties)
    }

}