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

package me.wolfyscript.utilities.api;

import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public abstract class WolfyUtilCore extends JavaPlugin {

    private static WolfyUtilCore instance;

    protected final HashMap<String, WolfyUtilities> wolfyUtilsInstances = new HashMap<>();
    protected final WolfyUtilities api;
    protected final Registries registries;

    protected WolfyUtilCore() {
        super();
        if(instance == null && this.getName().equals("WolfyUtilities")) {
            instance = this;
        } else {
            throw new IllegalArgumentException("This constructor can only be called by WolfyUtilities itself!");
        }
        this.api = get(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.registries = new Registries();
    }

    public static WolfyUtilCore getInstance() {
        return instance;
    }

    public Registries getRegistries() {
        return registries;
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance from.
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities get(Plugin plugin) {
        return get(plugin, false);
    }

    public WolfyUtilities get(Plugin plugin, boolean init) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, init));
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities get(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, customCacheClass));
    }

    public boolean has(Plugin plugin) {
        return wolfyUtilsInstances.containsKey(plugin.getName());
    }

    public List<WolfyUtilities> getAPIList() {
        return List.copyOf(wolfyUtilsInstances.values());
    }


}
