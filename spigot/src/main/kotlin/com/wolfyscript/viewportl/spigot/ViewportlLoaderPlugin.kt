package com.wolfyscript.viewportl.spigot

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import org.bukkit.plugin.java.JavaPlugin

class ViewportlLoaderPlugin : JavaPlugin() {

    val viewportl = ScafallProvider.get().platformManager.registerModule(
        Key.key("scafall", "viewportl")
    ) { SpigotViewportl(this) }

    override fun onLoad() {}

    override fun onEnable() {}

    override fun onDisable() {}

}