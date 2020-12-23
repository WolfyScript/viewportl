package me.wolfyscript.utilities.api;

import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.inventory.BookUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.exceptions.InvalidCacheTypeException;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;

public class WolfyUtilities implements Listener {

    private static final HashMap<Plugin, WolfyUtilities> wolfyUtilitiesList = new HashMap<>();
    private static final HashMap<String, Boolean> classes = new HashMap<>();

    private static CustomItems customItems;

    public static CustomItems getCustomItems() {
        return customItems;
    }

    private static void register(WolfyUtilities wolfyUtilities) {
        if (!has(wolfyUtilities.getPlugin())) {
            wolfyUtilitiesList.put(wolfyUtilities.getPlugin(), wolfyUtilities);
        }
    }

    private final NMSUtil nmsUtil;

    private WolfyUtilities(Plugin plugin) {
        this.plugin = plugin;
        this.dataBasePrefix = plugin.getName().toLowerCase(Locale.ROOT) + "_";
        register(this);

        customItems = new CustomItems(plugin);

        this.configAPI = new ConfigAPI(this);
        this.languageAPI = new LanguageAPI(this);
        this.inventoryAPI = new InventoryAPI<>(this.plugin, this, CustomCache.class);
        this.chat = new Chat(this);
        this.permissions = new Permissions(this);
        this.itemUtils = new ItemUtils(this);
        this.nmsUtil = NMSUtil.create(this);
        this.bookUtil = new BookUtil(this);
    }

    /**
     * @return if the minecraft version is 1.16 or higher
     */
    public static boolean hasNetherUpdate() {
        return hasSpecificUpdate(116);
    }

    /**
     * @return if the minecraft version is 1.15 or higher
     */
    public static boolean hasBuzzyBeesUpdate() {
        return hasSpecificUpdate(115);
    }

    /**
     * This can be used to make sure that this API is running on a supported Minecraft version, because pre-1.14 MC versions are no longer supported!
     *
     * @return if the minecraft version is 1.14 or higher
     */
    public static boolean hasVillagePillageUpdate() {
        return hasSpecificUpdate(114);
    }

    public static boolean hasSpecificUpdate(String versionString) {
        return Reflection.getVersionNumber() >= Integer.parseInt(versionString.replace("_", "").replace(".", "").replace("-", ""));
    }

    public static boolean hasSpecificUpdate(int versionNumber) {
        return Reflection.getVersionNumber() >= versionNumber;
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

    /**
     * @param plugin The plugin to check for WolfyUtilities
     * @return If the plugin has a registered WolfyUtilities instance associated with it
     */
    public static boolean has(Plugin plugin) {
        return wolfyUtilitiesList.containsKey(plugin);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance from.
     * @return The WolfyUtilities instance for the plugin.
     */
    public static WolfyUtilities get(Plugin plugin) {
        if (!has(plugin)) {
            new WolfyUtilities(plugin);
        }
        return wolfyUtilitiesList.get(plugin);
    }

    //Not tested yet!!

    public static void sendParticles(Player player, String particle, boolean biggerRadius, float x, float y, float z, float xOffset, float yOffset, float zOffset, int count, float particledata, int... data) {
        try {
            Object enumParticles = Reflection.getNMS("EnumParticle").getField(particle).get(null);
            Constructor<?> particleConstructor = Reflection.getNMS("PacketPlayOutWorldParticles").getConstructor(
                    Reflection.getNMS("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
            Object packet = particleConstructor.newInstance(enumParticles, biggerRadius, x, y, z, xOffset, yOffset, zOffset, particledata, count, data);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /*
        Non Static methods and constructor down below!
     */

    private final Plugin plugin;

    private String dataBasePrefix;

    private final ConfigAPI configAPI;

    private InventoryAPI<?> inventoryAPI;

    private final LanguageAPI languageAPI;

    private final Chat chat;
    private final ItemUtils itemUtils;
    private final Permissions permissions;
    private final BookUtil bookUtil;

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
        return Bukkit.getPluginManager().getPlugin("WolfyUtilities");
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
     * @return The {@link Chat} instance.
     * @see Chat More information about Chat.
     */
    public Chat getChat() {
        return chat;
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
     * This method sets the InventoryAPI.
     * <br/>
     * Use this method to set an InventoryAPI instance, that uses a custom cache.
     *
     * @param inventoryAPI The InventoryAPI instance with it's custom cache type.
     * @param <T>          The type of cache that was detected in the instance.
     * @see CustomCache CustomCache which can be extended and used as a custom cache for your GUI.
     * @see InventoryAPI InventoryAPI for more information about it.
     */
    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
    }

    /**
     * This method is used to get the InventoryAPI, that uses the type class as the cache.
     * <br/>
     * If there is no active {@link InventoryAPI} instance, then this method will create one with the specified type.
     * <br/>
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
        if (getConfigAPI().getConfig("config") != null) {
            return getConfigAPI().getConfig("config").getBoolean("debug");
        }
        return false;
    }
}