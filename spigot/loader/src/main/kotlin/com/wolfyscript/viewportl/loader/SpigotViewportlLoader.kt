package com.wolfyscript.viewportl.loader

import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.viewportl.Viewportl
import org.bukkit.plugin.java.JavaPlugin

class SpigotViewportlLoader : JavaPlugin() {

    private val module: Module<Viewportl>

    init {
        val bootstrap = ScafallLoader.loadImplementationModule(Viewportl::class.java, "viewportl-spigot.innerjar", "com.wolfyscript.viewportl.Bootstrap")
        module = bootstrap.loadImplementationModule(Viewportl::class.java, "com.wolfyscript.viewportl.spigot.SpigotViewportl", this::class.java, this)
    }

    override fun onLoad() {
        module.onLoad()
    }

    override fun onEnable() {

    }

    override fun onDisable() {
        module.onUnload()
    }

}