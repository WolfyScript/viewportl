package com.wolfyscript.utilities.bukkit.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.viewportl.gui.components.Component;
import com.wolfyscript.viewportl.gui.components.ComponentImplementation;
import com.wolfyscript.utilities.registry.Registries;
import com.wolfyscript.utilities.registry.RegistryGUIComponentTypes;
import com.wolfyscript.utilities.registry.UniqueTypeRegistrySimple;
import java.util.HashMap;
import java.util.Map;

public class RegistryGUIComponentBuilder extends UniqueTypeRegistrySimple<Component> implements RegistryGUIComponentTypes {

    public static final Map<Class<? extends Component>, NamespacedKey> COMPONENT_TO_BUILDER_POINTER = new HashMap<>();
    public static final Map<Class<? extends Component>, NamespacedKey> BASE_TYPE_TO_IMPL_POINTER = new HashMap<>();

    public RegistryGUIComponentBuilder(NamespacedKey key, Registries registries) {
        super(key, registries);
    }

    @Override
    public void register(NamespacedKey key, Class<? extends Component> value) {
        ComponentImplementation settings = value.getAnnotation(ComponentImplementation.class);
        Preconditions.checkNotNull(settings, "Missing ComponentImplementation annotation for '%s'! Requires: %s", key, ComponentImplementation.class);
        Class<? extends Component> baseType = settings.base();
        Preconditions.checkState(BASE_TYPE_TO_IMPL_POINTER.get(baseType) == null,
                "Failed to register Component! There can only be one implementation for a single Component Type: '%s' already has an implementation '%s'!", baseType.getName(), COMPONENT_TO_BUILDER_POINTER.get(baseType));
        super.register(key, value);
        BASE_TYPE_TO_IMPL_POINTER.put(baseType, key);
    }

    @Override
    public NamespacedKey getKey(Class<? extends Component> value) {
        NamespacedKey implKey = super.getKey(value);
        if (implKey == null) {
            return BASE_TYPE_TO_IMPL_POINTER.get(value);
        }
        return implKey;
    }

    @Override
    public Class<? extends Component> getFor(Class<? extends Component> componentType) {
        Preconditions.checkNotNull(componentType, "Cannot get Builder for null component type!");
        NamespacedKey key = COMPONENT_TO_BUILDER_POINTER.get(componentType);
        if (key == null) return null;
        return get(key);
    }
}
