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

package me.wolfyscript.utilities.registry;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A simple registry, used for basic use cases.
 *
 * @param <V> The type of the value.
 */
public class RegistrySimple<V extends Keyed> implements Registry<V> {

    private final NamespacedKey namespacedKey;
    protected final Map<NamespacedKey, V> map;
    private final Class<V> type;

    public RegistrySimple(NamespacedKey namespacedKey, Registries registries) {
        this.map = new HashMap<>();
        this.namespacedKey = namespacedKey;
        this.type = null;
        registries.indexTypedRegistry(this);
    }

    public RegistrySimple(NamespacedKey namespacedKey, Registries registries, Class<V> type) {
        this.map = new HashMap<>();
        this.type = type;
        this.namespacedKey = namespacedKey;
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
