package com.wolfyscript.viewportl.registry

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.viewportl

object ViewportlRegistryTypes {

    val root = Key.viewportl("root")

    /**
     * Contains the types of available [Elements][Element]
     *
     */
    val guiElementTypes = create<Class<out Element>>("gui/element/type")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.viewportl(registryKey)).reference { ScafallProvider.get().viewportl.registries }
    }

}