package com.wolfyscript.utilities.bukkit.registry;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.viewportl.gui.components.Component;
import com.wolfyscript.utilities.registry.Registries;
import com.wolfyscript.utilities.registry.UniqueTypeRegistrySimple;

public class RegistryGUIComponent extends UniqueTypeRegistrySimple<Component> {

    public RegistryGUIComponent(NamespacedKey key, Registries registries) {
        super(key, registries);
    }

}
