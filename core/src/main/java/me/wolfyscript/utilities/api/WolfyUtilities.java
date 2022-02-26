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

import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ChatImpl;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.console.Console;
import me.wolfyscript.utilities.api.inventory.BookUtil;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.exceptions.InvalidCacheTypeException;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WolfyUtilities {

    private static final String ENVIRONMENT = System.getProperties().getProperty("com.wolfyscript.env", "PROD");
    private static final Map<String, Boolean> classes = new HashMap<>();

    public static String getENVIRONMENT() {
        return ENVIRONMENT;
    }

    public static boolean isDevEnv() {
        return ENVIRONMENT.equalsIgnoreCase("DEV");
    }

    public static boolean isProdEnv() {
        return ENVIRONMENT.equalsIgnoreCase("PROD");
    }

    public static boolean hasJavaXScripting() {
        return hasClass("javax.script.ScriptEngine");
    }

    public static boolean hasSpigot() {
        return hasClass("org.spigotmc.Metrics");
    }

    public static boolean hasWorldGuard() {
        return hasPlugin("WorldGuard");
    }

    public static boolean hasPlotSquared() {
        return hasPlugin("PlotSquared");
    }

    public static boolean hasLWC() {
        return hasPlugin("LWC");
    }

    public static boolean hasMythicMobs() {
        return hasPlugin("MythicMobs");
    }

    public static boolean hasPlaceHolderAPI() {
        return hasPlugin("PlaceholderAPI");
    }

    public static boolean hasMcMMO() {
        return hasPlugin("mcMMO");
    }

    private final WolfyUtilCore core;
    private final Plugin plugin;
    private String dataBasePrefix;
    private final ConfigAPI configAPI;
    private InventoryAPI<?> inventoryAPI;
    private final LanguageAPI languageAPI;
    private final Chat chat;
    private final Console console;
    private final ItemUtils itemUtils;
    private final Permissions permissions;
    private final BookUtil bookUtil;
    private final MessageAPI messageAPI;
    private final NMSUtil nmsUtil;
    private final boolean initialize;

    WolfyUtilities(WolfyUtilCore core, Plugin plugin, Class<? extends CustomCache> cacheType, boolean initialize) {
        this.plugin = plugin;
        this.core = core;
        this.dataBasePrefix = plugin.getName().toLowerCase(Locale.ROOT) + "_";
        this.configAPI = new ConfigAPI(this);
        this.languageAPI = new LanguageAPI(this);
        this.inventoryAPI = new InventoryAPI<>(this.plugin, this, cacheType);
        this.chat = new ChatImpl(this);
        this.console = new Console(this);
        this.permissions = new Permissions(this);
        this.itemUtils = new ItemUtils(this);
        this.nmsUtil = NMSUtil.create(this);
        this.bookUtil = new BookUtil(this);
        this.messageAPI = new MessageAPI(this);
        this.initialize = initialize;
        if (initialize) {
            initialize();
        }
    }

    WolfyUtilities(WolfyUtilCore core, Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        this(core, plugin, customCacheClass, false);
    }

    WolfyUtilities(WolfyUtilCore core, Plugin plugin, boolean init) {
        this(core, plugin, CustomCache.class, init);
    }

    public final void initialize() {
        Bukkit.getPluginManager().registerEvents(this.inventoryAPI, plugin);
    }

    public Registries getRegistries() {
        return core.getRegistries();
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance from.
     * @return The WolfyUtilities instance for the plugin.
     */
    @Deprecated
    public static WolfyUtilities get(Plugin plugin) {
        return WolfyUtilCore.getInstance().getAPI(plugin);
    }

    @Deprecated
    public static WolfyUtilities get(Plugin plugin, boolean init) {
        return WolfyUtilCore.getInstance().getAPI(plugin, init);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    @Deprecated
    public static WolfyUtilities get(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return WolfyUtilCore.getInstance().getAPI(plugin, customCacheClass);
    }

    /**
     * @param pluginName The name of the plugin to check for
     * @return If the plugin is loaded
     */
    public static boolean hasPlugin(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    /**
     * Check if the specific class exists.
     *
     * @param path The path to the class to check for.
     * @return If the class exists.
     */
    public static boolean hasClass(String path) {
        if (classes.containsKey(path)) {
            return classes.get(path);
        }
        try {
            Class.forName(path);
            classes.put(path, true);
            return true;
        } catch (Exception e) {
            classes.put(path, false);
            return false;
        }
    }

    /**
     * @return The internal WolfyUtilities plugin.
     */
    public static Plugin getWUPlugin() {
        return WolfyUtilCore.getInstance();
    }

    /**
     * @return The {@link WolfyUtilities} instance of WolfyUtilities internal plugin.
     */
    public static WolfyUtilities getWUCore() {
        return WolfyUtilities.get(getWUPlugin());
    }

    /**
     * @return The version of WolfyUtilities internal plugin and therefore also this API version.
     */
    public static String getVersion() {
        return getWUPlugin().getDescription().getVersion();
    }

    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replaceAll("[^0-9]", ""));
    }

    /**
     * Gets the {@link WolfyUtilCore}. This should be used instead of {@link WolfyUtilCore#getInstance()}} whenever possible!
     *
     * @return The core of the plugin.
     */
    public WolfyUtilCore getCore() {
        return core;
    }

    /**
     * @return The {@link LanguageAPI} instance.
     * @see LanguageAPI More information about the Language API
     */
    public LanguageAPI getLanguageAPI() {
        return languageAPI;
    }

    /**
     * @return The {@link ConfigAPI} instance.
     * @see ConfigAPI More information about the Config API.
     */
    public ConfigAPI getConfigAPI() {
        return configAPI;
    }

    /**
     * @return The {@link ChatImpl} instance.
     * @see ChatImpl More information about Chat.
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * @return The {@link Console} instance.
     * @see Console More information about the Console Util.
     */
    public Console getConsole() {
        return console;
    }

    /**
     * @return The {@link Permissions} instance.
     * @see Permissions More information about Permissions
     */
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     * @return The {@link ItemUtils} instance.
     * @see ItemUtils More information about ItemUtils.
     */
    public ItemUtils getItemUtils() {
        return itemUtils;
    }

    /**
     * @return The {@link NMSUtil} instance.
     * @see NMSUtil More information about NMSUtil
     */
    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }

    /**
     * @return The {@link BookUtil} instance.
     * @see BookUtil More information about BookUtil
     */
    public BookUtil getBookUtil() {
        return bookUtil;
    }

    public MessageAPI getMessageAPI() {
        return messageAPI;
    }

    /**
     * This method sets the InventoryAPI.
     * <br>
     * Use this method to set an InventoryAPI instance, that uses a custom cache.
     *
     * @param inventoryAPI The InventoryAPI instance with it's custom cache type.
     * @param <T>          The type of cache that was detected in the instance.
     * @see CustomCache CustomCache which can be extended and used as a custom cache for your GUI.
     * @see InventoryAPI InventoryAPI for more information about it.
     */
    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
        if (initialize) {
            initialize();
        }
    }

    /**
     * You can use this method to get the InventoryAPI, if you don't know type of cache it uses.
     *
     * @return The {@link InventoryAPI} with unknown type.
     * @see InventoryAPI
     */
    public InventoryAPI<?> getInventoryAPI() {
        return getInventoryAPI(inventoryAPI.getCacheInstance().getClass());
    }

    /**
     * This method is used to get the InventoryAPI, that uses the type class as the cache.
     * <br>
     * If there is no active {@link InventoryAPI} instance, then this method will create one with the specified type.
     * <br>
     * If there is an active {@link InventoryAPI} instance, then the specified class must be an instance of the cache, else it will throw a {@link InvalidCacheTypeException}.
     *
     * @param type The class of the custom cache.
     * @param <T>  The type of the cache that was detected in the class.
     * @return The {@link InventoryAPI} with the specified type as it's cache.
     * @throws InvalidCacheTypeException If the type class is not an instance of the actual cache specified in the {@link InventoryAPI}.
     */
    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(Class<T> type) throws InvalidCacheTypeException {
        if (hasInventoryAPI() && type.isInstance(inventoryAPI.getCacheInstance())) {
            return (InventoryAPI<T>) inventoryAPI;
        } else if (!hasInventoryAPI()) {
            InventoryAPI<T> newInventoryAPI = new InventoryAPI<>(plugin, this, type);
            inventoryAPI = newInventoryAPI;
            return newInventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type " + type.getName() + " expected, got " + inventoryAPI.getCacheInstance().getClass().getName() + "!");
    }

    public boolean hasInventoryAPI() {
        return inventoryAPI != null;
    }

    /**
     * @return The plugin that this WolfyUtilities belongs to.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    public String getDataBasePrefix() {
        return dataBasePrefix;
    }

    public void setDataBasePrefix(String dataBasePrefix) {
        this.dataBasePrefix = dataBasePrefix;
    }

    /**
     * This method uses the main {@link me.wolfyscript.utilities.api.config.YamlConfiguration} with the key "config" to check if debug is enabled.
     *
     * @return True if the debug mode is enabled.
     * @see ConfigAPI#registerConfig(YamlConfiguration) More information about registration of configs.
     */
    public boolean hasDebuggingMode() {
        return getConfigAPI().getConfig("config") != null && getConfigAPI().getConfig("config").getBoolean("debug");
    }
}