package com.wolfyscript.viewportl

import com.wolfyscript.scafall.loader.module.Server
import com.wolfyscript.viewportl.gui.factories.GuiFactory

interface ViewportlServer : Server {

    val guiFactory: GuiFactory

}