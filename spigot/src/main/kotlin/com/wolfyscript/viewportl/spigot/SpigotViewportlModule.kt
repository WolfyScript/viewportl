package com.wolfyscript.viewportl.spigot

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.viewportl.Viewportl

class SpigotViewportlModule() : Module<Viewportl> {

    val spigotViewportl: SpigotViewportl = SpigotViewportl(ScafallProvider.get().corePlugin)
    override val bridge: Viewportl
        get() = spigotViewportl

    override fun onLoad() {
        spigotViewportl.init()
    }

    override fun onEnable() {
        spigotViewportl.enable()
    }

    override fun onUnload() {
        spigotViewportl.unload()
    }
}