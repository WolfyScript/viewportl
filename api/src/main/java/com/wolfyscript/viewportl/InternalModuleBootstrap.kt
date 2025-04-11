package com.wolfyscript.viewportl

import com.wolfyscript.scafall.loader.module.ImplementationModuleBootstrap
import com.wolfyscript.scafall.loader.module.Module

class InternalModuleBootstrap : ImplementationModuleBootstrap<Viewportl> {

    override fun bridgeApplied(): Boolean {
        return ViewportlProvider.registered()
    }

    override fun applyBridge(module: Module<Viewportl>) {
        if (module.javaClass.name.startsWith("com.wolfyscript.viewportl")) {
            ViewportlProvider.register(module.bridge)
        }
    }

}