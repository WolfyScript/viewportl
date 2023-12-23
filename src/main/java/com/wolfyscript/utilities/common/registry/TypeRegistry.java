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

import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import org.jetbrains.annotations.Nullable;

/**
 * This registry allows you to register classes under NamespacedKeys. <br>
 * It is similar to the {@link Registry}, with the difference that it stores the classes of the type. <br>
 * To get a new instance of an entry you must use {@link #create(NamespacedKey)} or create it manually. <br>
 * <p>
 * Main use case of this registry would be to prevent using the {@link Registry} with default objects <br>
 * and prevents unwanted usage of those values, as this registry enforces to create new instances.
 *
 * @param <V> The type of the values.
 */
public interface TypeRegistry<V extends Keyed> extends Registry<Class<? extends V>> {

    /**
     * This method creates a new instance of the specific class, if it is available. <br>
     * If class for the key is not found it will return null. <br>
     * Default implementation looks for the default constructor.
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return A new instance of the class.
     */
    @Nullable
    V create(NamespacedKey key);

    /**
     * Registers the class of the object to the specified {@link NamespacedKey}. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param key   The {@link NamespacedKey} to register it to.
     * @param value The object to register the class from.
     */
    void register(NamespacedKey key, V value);

    /**
     * Registers the class of the object to the {@link NamespacedKey} of the object. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param value The object to register the class from.
     */
    void register(V value);

}
