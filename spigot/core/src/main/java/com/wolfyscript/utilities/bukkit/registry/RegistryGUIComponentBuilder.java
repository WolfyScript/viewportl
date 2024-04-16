package com.wolfyscript.utilities.bukkit.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.ComponentBuilder;
import com.wolfyscript.utilities.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.registry.Registries;
import com.wolfyscript.utilities.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.registry.UniqueTypeRegistrySimple;
import java.util.HashMap;
import java.util.Map;

public class RegistryGUIComponentBuilder extends UniqueTypeRegistrySimple<ComponentBuilder<?,?>> implements RegistryGUIComponentBuilders {

    public static final Map<Class<? extends Component>, NamespacedKey> COMPONENT_TO_BUILDER_POINTER = new HashMap<>();
    public static final Map<Class<? extends ComponentBuilder<?,?>>, NamespacedKey> BASE_TYPE_TO_IMPL_POINTER = new HashMap<>();

    public RegistryGUIComponentBuilder(NamespacedKey key, Registries registries) {
        super(key, registries);
    }

    @Override
    public void register(NamespacedKey key, Class<? extends ComponentBuilder<?, ?>> value) {
        ComponentBuilderSettings settings = value.getAnnotation(ComponentBuilderSettings.class);
        Preconditions.checkNotNull(settings, "Missing settings annotation for Builder '%s'! Use the annotation to provide settings: %s", key, ComponentBuilderSettings.class);
        Class<? extends Component> componentType = settings.component();
        Class<? extends ComponentBuilder<?,?>> baseType = settings.base();
        Preconditions.checkState(COMPONENT_TO_BUILDER_POINTER.get(componentType) == null,
                "Failed to register Builder! There can only be one Builder for a single Component Type: '%s' already has builder '%s' associated with it!", componentType.getName(), COMPONENT_TO_BUILDER_POINTER.get(componentType));
        super.register(key, value);
        BASE_TYPE_TO_IMPL_POINTER.put(baseType, key);
        COMPONENT_TO_BUILDER_POINTER.put(componentType, key);
    }

    @Override
    public NamespacedKey getKey(Class<? extends ComponentBuilder<?, ?>> value) {
        NamespacedKey implKey = super.getKey(value);
        if (implKey == null) {
            return BASE_TYPE_TO_IMPL_POINTER.get(value);
        }
        return implKey;
    }

    @Override
    public Class<? extends ComponentBuilder<?,?>> getFor(Class<? extends Component> componentType) {
        Preconditions.checkNotNull(componentType, "Cannot get Builder for null component type!");
        NamespacedKey key = COMPONENT_TO_BUILDER_POINTER.get(componentType);
        if (key == null) return null;
        return get(key);
    }
}
