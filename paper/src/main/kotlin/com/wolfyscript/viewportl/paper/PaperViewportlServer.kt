package com.wolfyscript.viewportl.paper

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.ViewportlServer
import com.wolfyscript.viewportl.common.commands.ViewportlCommands
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction.InventoryUIListener
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class PaperViewportlServer(val viewportl: Viewportl, val plugin: Plugin) : ViewportlServer {

    override fun onLoad() {
        Bukkit.getPluginManager().registerEvents(InventoryUIListener(), plugin)

        ScafallProvider.get().server?.minecraftServer?.commands?.dispatcher?.let {
            ViewportlCommands.register(it)
        }
    }

    override fun onUnload() {

    }


}