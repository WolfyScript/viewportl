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

package com.wolfyscript.utilities.bukkit.world.entity;

import com.wolfyscript.utilities.NamespacedKey;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class PlayerStore {

    private final Map<NamespacedKey, CustomPlayerData> cachedData = new HashMap<>();

    /**
     * Only used for Json deserialization purposes!
     */
    public PlayerStore() { }

    /**
     * @deprecated Changes made to the data is no longer persistent across server restarts!
     */
    @Deprecated
    public <D extends CustomPlayerData> D getData(NamespacedKey dataKey, Class<D> dataType) {
        return dataType.cast(cachedData.computeIfAbsent(dataKey, namespacedKey -> {
            CustomPlayerData.Provider<?> provider = CustomPlayerData.providers.get(namespacedKey);
            return provider != null ? provider.createData() : null;
        }));
    }

    @Override
    public String toString() {
        return "PlayerStore{" +
                "data={}" +
                '}';
    }
}
