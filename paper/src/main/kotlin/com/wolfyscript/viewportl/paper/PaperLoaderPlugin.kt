package com.wolfyscript.viewportl.paper

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import org.bukkit.plugin.java.JavaPlugin

class PaperLoaderPlugin : JavaPlugin() {

    val viewportl = ScafallProvider.get().platformManager.registerModule(
        Key.scafall("viewportl")
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