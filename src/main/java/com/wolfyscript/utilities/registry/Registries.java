/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.nbt.NBTTagConfig;
import com.wolfyscript.utilities.eval.operator.Operator;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Includes all the Registries inside WolfyUtilities.<br>
 * <br>
 * To use the registries you need to get an instance of this class.<br>
 * You should always try to not use the static method, as it can make the code less maintainable.<br>
 * If it is possible to access the instance of your WU API, then that should be used instead!<br>
 * <br>
 *
 * <strong>Get an instance:</strong>
 * <ul>
 *     <li>(<b>Recommended</b>) via your API instance {@link WolfyUtils#getRegistries()}</li>
 *     <li>via the core {@link WolfyCore#getRegistries()}</li>
 * </ul>
 */
public abstract class Registries {

    protected final WolfyCore core;

    protected final Map<Class<? extends Keyed>, Registry<?>> REGISTRIES_BY_TYPE = new HashMap<>();
    protected final Map<NamespacedKey, Registry<?>> REGISTRIES_BY_KEY = new HashMap<>();

    private final TypeRegistry<ValueProvider<?>> valueProviders;
    private final TypeRegistry<Operator> operators;
    private final TypeRegistry<NBTTagConfig> nbtTagConfigs;

    public Registries(@NotNull WolfyCore core) {
        this.core = core;

        valueProviders = new UniqueTypeRegistrySimple<>(core.getWolfyUtils().getIdentifiers().getSelfNamespaced("value_providers"), this);
        operators = new UniqueTypeRegistrySimple<>(core.getWolfyUtils().getIdentifiers().getSelfNamespaced("operators"), this);
        nbtTagConfigs = new UniqueTypeRegistrySimple<>(core.getWolfyUtils().getIdentifiers().getSelfNamespaced("nbt_configs"), this);
    }

    protected void indexTypedRegistry(@NotNull Registry<?> registry) {
        Preconditions.checkArgument(!REGISTRIES_BY_KEY.containsKey(registry.getKey()), "A registry with the key \"" + registry.getKey() + "\" already exists!");
        REGISTRIES_BY_KEY.put(registry.getKey(), registry);

        //Index them by type if available
        if(registry instanceof RegistrySimple<?> simpleRegistry && simpleRegistry.getType() != null) {
            Preconditions.checkArgument(!REGISTRIES_BY_TYPE.containsKey(simpleRegistry.getType()), "A registry with that type already exists!");
            REGISTRIES_BY_TYPE.put(simpleRegistry.getType(), simpleRegistry);
        }
    }

    /**
     * Gets a Registry by the type it contains.
     * The Registry has to be created with the class of the type (See: {@link RegistrySimple#RegistrySimple(NamespacedKey, Registries, Class)}).
     *
     * @param type The class of the type the registry contains.
     * @param <V> The type the registry contains.
     * @return The registry of the specific type; or null if not available.
     */
    @SuppressWarnings("unchecked")
    public <V extends Keyed> Registry<V> getByType(Class<V> type) {
        return (Registry<V>) REGISTRIES_BY_TYPE.get(type);
    }

    public Registry<?> getByKey(NamespacedKey key) {
        return REGISTRIES_BY_KEY.get(key);
    }

    public <V extends Registry<?>> V getByKeyOfType(NamespacedKey key, Class<V> registryType) {
        var registry = getByKey(key);
        return registryType.cast(registry);
    }

    public WolfyCore getCore() {
        return core;
    }

    public TypeRegistry<ValueProvider<?>> getValueProviders() {
        return valueProviders;
    }

    public TypeRegistry<Operator> getOperators() {
        return operators;
    }

    public TypeRegistry<NBTTagConfig> getNbtTagConfigs() {
        return nbtTagConfigs;
    }

    public abstract TypeRegistry<Component> getGuiComponents();

    public abstract RegistryGUIComponentBuilders getGuiComponentBuilders();
}
