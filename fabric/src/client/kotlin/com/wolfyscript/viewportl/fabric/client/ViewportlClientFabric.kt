package com.wolfyscript.viewportl.fabric.client

import com.wolfyscript.viewportl.ViewportlClient
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import net.minecraft.client.Minecraft

class ViewportlClientFabric(val mcClient: Minecraft) : ViewportlClient {

    override val guiFactory: GuiFactory
        get() = TODO()

    override fun onLoad() {

    }

    override fun onUnload() {

    }
}