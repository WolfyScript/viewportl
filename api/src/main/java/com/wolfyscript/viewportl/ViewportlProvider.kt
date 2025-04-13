package com.wolfyscript.viewportl

import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.identifier.Key

val Scafall.viewportl: Viewportl
    get() {
        return this.platformManager.getImplementationModule(Key.key("scafall", "viewportl"), Viewportl::class.java) ?: throw IllegalStateException("Viewportl not initialized!")
    }
