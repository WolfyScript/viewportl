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

package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.registry.RegistrySimple;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Tags<T extends Keyed> extends RegistrySimple<CustomTag<T>> {

    @Deprecated
    public Tags() {
        super(new NamespacedKey(WolfyUtilCore.getInstance(), "custom_tags"), WolfyUtilCore.getInstance().getRegistries());
    }

    public Tags(Registries registries) {
        super(new NamespacedKey(registries.getCore(), "custom_tags"), registries);
    }

    @Nullable
    public CustomTag<T> getTag(NamespacedKey namespacedKey) {
        return get(namespacedKey);
    }

}
