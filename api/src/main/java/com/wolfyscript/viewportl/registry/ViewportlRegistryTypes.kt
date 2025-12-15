package com.wolfyscript.viewportl.registry

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.viewportl.viewportl

object ViewportlRegistryTypes {

    val root = Key.viewportl("root")

    /* ********************************************
     * There are no registries used at the moment.
     * This is here for future additions.
     * ********************************************/

    /**
     * Creates a new viewportl specific reference to the registry
     */
    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.viewportl(registryKey)).reference { ScafallProvider.get().viewportl.registries }
    }

}