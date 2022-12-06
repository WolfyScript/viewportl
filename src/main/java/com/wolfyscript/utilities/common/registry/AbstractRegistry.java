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

package com.wolfyscript.utilities.common.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRegistry<M extends Map<NamespacedKey, V>, V extends Keyed> implements Registry<V> {

    protected final NamespacedKey namespacedKey;
    protected final Registries registries;
    protected final M map;
    protected final Class<V> type;

    public AbstractRegistry(NamespacedKey namespacedKey, M map, Registries registries) {
        this(namespacedKey, map, registries, null);
    }

    public AbstractRegistry(NamespacedKey namespacedKey, Supplier<M> mapSupplier, Registries registries) {
        this(namespacedKey, mapSupplier.get(), registries, null);
    }

    public AbstractRegistry(NamespacedKey namespacedKey, Supplier<M> mapSupplier, Registries registries, Class<V> type) {
        this(namespacedKey, mapSupplier.get(), registries, type);
    }

    public AbstractRegistry(NamespacedKey namespacedKey, M map, Registries registries, Class<V> type) {
        this.map = map;
        this.namespacedKey = namespacedKey;
        this.type = type;
        this.registries = registries;
        registries.indexTypedRegistry(this);
    }

    public Class<V> getType() {
        return type;
    }

    private boolean isTypeOf(Class<?> type) {
        return this.type != null && this.type.equals(type);
    }

    @Override
    public @Nullable V get(@Nullable NamespacedKey key) {
        return map.get(key);
    }

    @Override
    public void register(NamespacedKey namespacedKey, V value) {
        if (value != null) {
            Preconditions.checkState(!this.map.containsKey(namespacedKey), "namespaced key '%s' already has an associated value!", namespacedKey);
            map.put(namespacedKey, value);
        }
    }

    @Override
    public void register(V value) {
        register(value.getNamespacedKey(), value);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return map.values().iterator();
    }

    @Override
    public Set<NamespacedKey> keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    @Override
    public Set<Map.Entry<NamespacedKey, V>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

}
