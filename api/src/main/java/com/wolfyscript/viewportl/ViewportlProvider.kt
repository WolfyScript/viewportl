package com.wolfyscript.viewportl

import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.identifier.Key

const val VIEWPORTL_NAMESPACE = "viewportl"

val Scafall.viewportl: Viewportl
    get() {
        return this.platformManager.getImplementationModule(
            Key.scafall(VIEWPORTL_NAMESPACE),
            Viewportl::class.java
        ) ?: throw IllegalStateException("Viewportl not initialized!")
    }

fun Key.Companion.viewportl(value: String): Key = Key.key(VIEWPORTL_NAMESPACE, value)