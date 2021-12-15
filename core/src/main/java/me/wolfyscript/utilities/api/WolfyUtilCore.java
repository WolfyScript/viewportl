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

import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.compatibility.CompatibilityManager;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class is the actual core of the plugin (This class is being extended by the plugin instance).<br>
 * <p>
 * It provides access to internal functionality like {@link Registries}, {@link CompatibilityManager}, and of course the creation of the API instance.<br>
 * <p>
 * To get an instance of the API ({@link WolfyUtilities}) for your plugin you need one of the following methods. <br>
 * <ul>
 *     <li>{@link #get(Plugin)} - Simple method to get your instance. Only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #get(Plugin, boolean)} - Specify if it should init Event Listeners. Can be used inside the onLoad(), or plugin constructor, if set to false; Else only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #get(Plugin, Class)} - Specify the type of your {@link CustomCache}. Can be used inside the onLoad(), or plugin constructor.</li>
 * </ul>
 * </p>
 */
public abstract class WolfyUtilCore extends JavaPlugin {

    //Static reference to the instance of this class.
    private static WolfyUtilCore instance;

    protected Reflections reflections;
    protected final Map<String, WolfyUtilities> wolfyUtilsInstances = new HashMap<>();
    protected final WolfyUtilities api;
    protected final Registries registries;

    protected WolfyUtilCore() {
        super();
        if (instance == null && this.getName().equals("WolfyUtilities") && getClass().getPackageName().equals("me.wolfyscript.utilities.main")) {
            instance = this;
        } else {
            throw new IllegalArgumentException("This constructor can only be called by WolfyUtilities itself!");
        }
        this.api = get(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.registries = new Registries(this);
        this.reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("me.wolfyscript")
                .addClassLoaders(getClassLoader())
                .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.Resources));
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilities} instance!</strong>
     *
     * @return The instance of the core.
     */
    public static WolfyUtilCore getInstance() {
        return instance;
    }

    /**
     * Gets the {@link Registries} object, that contains all info about available registries.
     *
     * @return The {@link Registries} object, to access registries.
     */
    public Registries getRegistries() {
        return registries;
    }

    /**
     * Gets the {@link CompatibilityManager}, that manages the plugins compatibility features.
     *
     * @return The {@link CompatibilityManager}.
     */
    public abstract CompatibilityManager getCompatibilityManager();

    /**
     * Gets the {@link Reflections} instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    public Reflections getReflections() {
        return reflections;
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance for.
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities get(Plugin plugin) {
        return get(plugin, false);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.<br>
     * In case init is enabled it will directly initialize the event listeners and possibly other things.<br>
     * <b>In case you disable init you need to run {@link WolfyUtilities#initialize()} inside your onEnable()!</b>
     *
     * @param plugin The plugin to get the instance for.
     * @param init   If it should directly initialize the APIs' events, etc. (They must be initialized later via {@link WolfyUtilities#initialize()})
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities get(Plugin plugin, boolean init) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, init));
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.<br>
     * <b>You need to run {@link WolfyUtilities#initialize()} inside your onEnable() </b> to register required events!
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities get(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, customCacheClass));
    }

    /**
     * Checks if the specified plugin has an API instance associated with it.
     *
     * @param plugin The plugin to check.
     * @return True in case the API is available; false otherwise.
     */
    public boolean has(Plugin plugin) {
        return wolfyUtilsInstances.containsKey(plugin.getName());
    }

    /**
     * Returns an unmodifiable List of all available {@link WolfyUtilities} instances.
     *
     * @return A list containing all the created API instances.
     */
    public List<WolfyUtilities> getAPIList() {
        return List.copyOf(wolfyUtilsInstances.values());
    }

    /**
     * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
     *
     * @param parser an {@link APIReference.Parser} instance.
     * @see me.wolfyscript.utilities.api.inventory.custom_items.CustomItem#registerAPIReferenceParser(APIReference.Parser)
     */
    public abstract void registerAPIReference(APIReference.Parser<?> parser);


}
