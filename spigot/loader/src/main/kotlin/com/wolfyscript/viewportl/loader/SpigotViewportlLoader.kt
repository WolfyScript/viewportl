package com.wolfyscript.viewportl.loader

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import org.bukkit.plugin.java.JavaPlugin

class SpigotViewportlLoader : JavaPlugin() {

    init {
        ScafallProvider.get().platformManager.registerImplementationModule(
            Key.key("scafall", "viewportl"),
            Viewportl::class.java,
            classLoader,
            "viewportl-spigot.innerjar",
            "com.wolfyscript.viewportl.spigot.SpigotViewportlModule"
        )
    }

    override fun onLoad() {}

    override fun onEnable() {}

    override fun onDisable() {}

}