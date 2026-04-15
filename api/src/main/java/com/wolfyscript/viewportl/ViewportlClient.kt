package com.wolfyscript.viewportl

import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.viewportl.gui.factories.GuiFactory

interface ViewportlClient : Client {

    val guiFactory: GuiFactory

}