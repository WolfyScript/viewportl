package com.wolfyscript.viewportl.paper

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.ViewportlServer
import com.wolfyscript.viewportl.common.commands.ViewportlCommands
import org.bukkit.plugin.Plugin

class PaperViewportlServer(val viewportl: Viewportl, val plugin: Plugin) : ViewportlServer {

    override fun onLoad() {


        ScafallProvider.get().server?.minecraftServer?.commands?.dispatcher?.let {
            ViewportlCommands.register(it)
        }
    }

    override fun onUnload() {

    }


}