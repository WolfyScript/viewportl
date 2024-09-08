package com.wolfyscript.viewportl.sponge

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.GuiAPIManagerImpl
import com.wolfyscript.viewportl.common.registry.CommonViewportlRegistries
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.sponge.gui.factories.GuiFactoryImpl
import org.spongepowered.api.Sponge

class SpongeViewportl : Viewportl {

    override val guiManager: GuiAPIManager = GuiAPIManagerImpl(this)
    override val guiFactory: GuiFactory = GuiFactoryImpl()
    override val registries: ViewportlRegistries = CommonViewportlRegistries(this)

    fun init() {
        val eventManager = Sponge.eventManager()
    }
}