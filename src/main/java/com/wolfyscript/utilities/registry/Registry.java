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

import com.wolfyscript.utilities.NamespacedKey;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * A registry to register objects under specified namespaced keys.
 * This allows for easier management of custom content etc.
 *
 * @param <V>
 */
public interface Registry<V> extends Iterable<V> {

    /**
     * Get the value of the registry by its {@link NamespacedKey}
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return The value of the {@link NamespacedKey}.
     */
    @Nullable
    V get(@Nullable NamespacedKey key);

    /**
     * Receives the key under which the value is registered in this Registry.<br>
     * The default implementation is quite inefficient as it uses the stream filter method
     * to find the matching value.<br>
     * Additionally, it only returns the key of the first match. Therefor this only works reliably if the values are unique too!<br>
     * Some Registry implementations or extensions might have exactly that behaviour.<br>
     * <br>
     * Some implementations are:<br>
     * {@link UniqueTypeRegistrySimple}
     *
     * @param value The value to get the key for.
     * @return The key for the registered value or null if not found.
     */
    default NamespacedKey getKey(V value) {
        return entrySet().stream().filter(entry -> entry.getValue().equals(value)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    /**
     * Register a value with a {@link NamespacedKey} to this registry.
     * You can't override values that are already registered under the same {@link NamespacedKey}!
     *
     * @param key   The {@link NamespacedKey} to register it to.
     * @param value The value to register.
     */
    void register(NamespacedKey key, V value);

    /**
     * Registers a value with its contained {@link NamespacedKey} it gets via the {@link V#getNamespacedKey()} method.
     * @param value The value to register.
     */
    void register(V value);

    Set<NamespacedKey> keySet();

    Collection<V> values();

    Set<Map.Entry<NamespacedKey, V>> entrySet();

    NamespacedKey getKey();

}
