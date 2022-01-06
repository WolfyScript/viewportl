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

package me.wolfyscript.utilities.main.resource_loader;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.expansions.ResourceLoaderJson;
import me.wolfyscript.utilities.main.WUPlugin;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ResourceLoaderCustomItems extends ResourceLoaderJson<CustomItem> {

    public ResourceLoaderCustomItems(WUPlugin plugin) {
        super(plugin, new NamespacedKey(plugin, "items/items"), CustomItem.class);
    }

    @Override
    public void register(NamespacedKey namespacedKey, CustomItem value, WolfyUtilCore core) {
        core.getRegistries().getCustomItems().register(namespacedKey, value);
    }

}
