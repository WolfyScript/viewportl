package com.wolfyscript.viewportl.fabric

import com.wolfyscript.viewportl.ViewportlClient
import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.fabric.server.FabricViewportlServer
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger

class ViewportlFabric(val logger: Logger) : CommonViewportl() {

    internal fun initServer(mcServer: MinecraftServer) {
        server = FabricViewportlServer(this, mcServer)
    }

    fun initClient(client: ViewportlClient) {
        this.client = client
    }
}