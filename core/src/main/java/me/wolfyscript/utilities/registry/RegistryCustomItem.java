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

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistryCustomItem extends RegistrySimple<CustomItem> {

    RegistryCustomItem() {
    }

    RegistryCustomItem(Registries registries, Class<CustomItem> type) {
        super(registries, type);
    }

    public List<String> getNamespaces() {
        return this.map.keySet().stream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
    }

    /**
     * Get all the items of the specific namespace.
     *
     * @param namespace the namespace you want to get the items from
     * @return A list of all the items of the specific namespace
     */
    public List<CustomItem> get(String namespace) {
        return this.map.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * @param namespacedKey NamespacedKey of the item
     * @return true if there is an CustomItem for the NamespacedKey
     */
    public boolean has(NamespacedKey namespacedKey) {
        return this.map.containsKey(namespacedKey);
    }

    /**
     * Removes the CustomItem from the registry.
     * However, this won't delete the config if one exists!
     * If a config exists the item will be reloaded on the next restart.
     *
     * @param namespacedKey The NamespacedKey of the CustomItem
     */
    public void remove(NamespacedKey namespacedKey) {
        this.map.remove(namespacedKey);
    }

    /**
     * Add a CustomItem to the registry or update a existing one and sets the NamespacedKey in the CustomItem object.
     * <br>
     * If the registry already contains a value for the NamespacedKey then the value will be updated with the new one.
     * <br>
     * <b>
     * If the CustomItem is linked with a {@link WolfyUtilitiesRef}, which NamespacedKey is the same as the passed in NamespacedKey, the CustomItem will neither be added or updated!
     * <br>
     * This is to prevent a infinite loop where a reference tries to call itself when it tries to get the values from it's parent item.
     * </b>
     *
     * @param namespacedKey The NamespacedKey the CustomItem will be saved under.
     * @param item          The CustomItem to add or update.
     */
    @Override
    public void register(NamespacedKey namespacedKey, CustomItem item) {
        if (item == null || (item.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) item.getApiReference()).getNamespacedKey().equals(namespacedKey))) {
            return;
        }
        this.map.put(namespacedKey, item);
        item.setNamespacedKey(namespacedKey);
    }
}
