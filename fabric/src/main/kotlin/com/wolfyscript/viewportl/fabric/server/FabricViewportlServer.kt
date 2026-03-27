package com.wolfyscript.viewportl.fabric.server

import com.wolfyscript.viewportl.ViewportlServer
import com.wolfyscript.viewportl.fabric.ViewportlFabric
import net.minecraft.server.MinecraftServer

class FabricViewportlServer(val viewportl: ViewportlFabric, val mcServer: MinecraftServer) : ViewportlServer {

    override fun onLoad() {


    }

    override fun onUnload() {

    }
}