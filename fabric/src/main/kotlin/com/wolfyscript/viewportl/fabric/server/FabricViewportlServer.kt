package com.wolfyscript.viewportl.fabric.server

import com.wolfyscript.viewportl.ViewportlServer
import com.wolfyscript.viewportl.fabric.ViewportlFabric
import com.wolfyscript.viewportl.fabric.server.ui.FabricServerUIFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import net.minecraft.server.MinecraftServer

class FabricViewportlServer(val viewportl: ViewportlFabric, val mcServer: MinecraftServer) : ViewportlServer {

    override val guiFactory: GuiFactory = FabricServerUIFactory()

    override fun onLoad() {


    }

    override fun onUnload() {

    }

}