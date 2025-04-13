package com.wolfyscript.viewportl.sponge

import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.viewportl.Viewportl
import org.spongepowered.plugin.PluginContainer

class SpongeViewportlModule : Module<Viewportl> {

    constructor(pluginContainer: PluginContainer) : super()

    val spongeViewportl: SpongeViewportl = SpongeViewportl()

    override val bridge: Viewportl
        get() = spongeViewportl

    override fun onLoad() {
        spongeViewportl.init()
    }

    override fun onEnable() {
        spongeViewportl.enable()
    }

    override fun onUnload() {
        spongeViewportl.unload()
    }

}