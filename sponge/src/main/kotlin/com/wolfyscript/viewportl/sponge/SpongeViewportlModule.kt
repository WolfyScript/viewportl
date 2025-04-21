package com.wolfyscript.viewportl.sponge

import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.viewportl.Viewportl

class SpongeViewportlModule : Module<Viewportl> {

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