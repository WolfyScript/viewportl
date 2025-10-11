package com.wolfyscript.viewportl.spigot

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import org.bukkit.plugin.java.JavaPlugin

class PaperLoaderPlugin : JavaPlugin() {

    val viewportl = ScafallProvider.get().platformManager.registerModule(
        Key.key("scafall", "viewportl")
    ) { PaperViewportl(this) }

    override fun onLoad() {
        viewportl.initServer()
    }

    override fun onEnable() {
        viewportl.server?.onLoad()
    }

    override fun onDisable() {
        viewportl.server?.onUnload()
    }

}