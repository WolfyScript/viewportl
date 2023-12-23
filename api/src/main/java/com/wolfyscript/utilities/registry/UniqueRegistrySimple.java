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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;

/**
 * A simple registry, used for basic use cases.
 *
 * @param <V> The type of the value.
 */
public class UniqueRegistrySimple<V extends Keyed> extends AbstractRegistry<BiMap<NamespacedKey, V>, V> {

    public UniqueRegistrySimple(NamespacedKey namespacedKey, Registries registries) {
        super(namespacedKey, HashBiMap.create(), registries);
    }

    public UniqueRegistrySimple(NamespacedKey namespacedKey, Registries registries, Class<V> type) {
        super(namespacedKey, HashBiMap.create(), registries, type);
    }

    @Override
    public NamespacedKey getKey(V value) {
        return map.inverse().get(value);
    }

}
