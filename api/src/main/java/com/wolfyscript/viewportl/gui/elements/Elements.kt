package com.wolfyscript.viewportl.gui.elements

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced
import com.wolfyscript.viewportl.registry.ViewportlRegistryTypes
import com.wolfyscript.viewportl.viewportl

object Elements {

    val button = create<Class<Button>>("button")
    val slot = create<Class<StackInputSlot>>("slot")

    private inline fun <reified T: Class<out Element>> create(key: String) : ValueReference<Class<out Element>, T> {
        return ViewportlRegistryTypes.guiElementTypes.key
            .referenced<Class<out Element>, T>(Key.viewportl(key))
            .reference { ScafallProvider.get().viewportl.registries }
    }
}