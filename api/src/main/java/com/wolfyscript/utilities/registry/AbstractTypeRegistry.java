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
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTypeRegistry<M extends Map<NamespacedKey, Class<? extends V>>, V extends Keyed> implements TypeRegistry<V> {

    protected final NamespacedKey key;
    protected final Registries registries;
    protected final M map;

    public AbstractTypeRegistry(NamespacedKey key, M map, Registries registries) {
        this.key = key;
        this.map = map;
        this.registries = registries;
        registries.indexTypedRegistry(this);
    }

    public AbstractTypeRegistry(NamespacedKey key, Supplier<M> mapSupplier, Registries registries) {
        this(key, mapSupplier.get(), registries);
    }

    @Override
    public @Nullable Class<? extends V> get(@Nullable NamespacedKey key) {
        return map.get(key);
    }

    @Override
    public @Nullable V create(NamespacedKey key) {
        Class<? extends V> clazz = get(key);
        if (clazz != null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void register(NamespacedKey key, V value) {
        register(key, (Class<V>) value.getClass());
    }

    @Override
    public void register(NamespacedKey key, Class<? extends V> value) {
        if (value != null) {
            Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
            Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
            map.put(key, value);
        }
    }

    @Override
    public void register(Class<? extends V> value) {
        KeyedStaticId staticIdAnnot = value.getAnnotation(KeyedStaticId.class);
        Preconditions.checkArgument(staticIdAnnot != null, "Value does not specify static id via the KeyedStaticId annotation!");
        NamespacedKey id = registries.getCore().getWolfyUtils().getIdentifiers().getNamespaced(value);
        register(id, value);
    }

    @Override
    public void register(V value) {
        register(value.key(), (Class<? extends V>) value.getClass());
    }

    @NotNull
    @Override
    public Iterator<Class<? extends V>> iterator() {
        return map.values().iterator();
    }

    @Override
    public Set<NamespacedKey> keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    @Override
    public Collection<Class<? extends V>> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    @Override
    public Set<Map.Entry<NamespacedKey, Class<? extends V>>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

}
