package com.wolfyscript.viewportl.fabric

import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.fabric.server.FabricViewportlServer
import com.wolfyscript.viewportl.fabric.server.ui.FabricServerUIFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger

class ViewportlFabric(val logger: Logger) : CommonViewportl() {

    override val guiFactory: GuiFactory
        get() = FabricServerUIFactory() // TODO: separate server and client

    fun initServer(mcServer: MinecraftServer) {
        server = FabricViewportlServer(this, mcServer)
    }
}