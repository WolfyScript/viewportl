package me.wolfyscript.utilities.api;

import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.BookUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.particles.ParticleEffects;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.exceptions.InvalidCacheTypeException;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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

    private final NMSUtil nmsUtil;
    private HashMap<me.wolfyscript.utilities.util.NamespacedKey, ParticleEffects> particleEffects;

    private static void register(WolfyUtilities wolfyUtilities) {
        if (!has(wolfyUtilities.getPlugin())) {
            wolfyUtilitiesList.put(wolfyUtilities.getPlugin(), wolfyUtilities);
        }
    }

    public static boolean has(Plugin plugin) {
        return wolfyUtilitiesList.containsKey(plugin);
    }

    public static WolfyUtilities get(Plugin plugin) {
        if (!has(plugin)) {
            new WolfyUtilities(plugin);
        }
        return wolfyUtilitiesList.get(plugin);
    }

    /**
     *
     * @return if the minecraft version is 1.16 or higher
     */
    public static boolean hasNetherUpdate() {
        return hasSpecificUpdate(116);
    }

    /**
     *
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

    public static boolean hasPlugin(String pluginName){
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

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

    //Not tested yet!!
    public static void sendParticles(Player player, String particle, boolean biggerRadius, float x, float y, float z, float xOffset, float yOffset, float zOffset, int count, float particledata, int... data) {
        try {
            Object enumParticles = Reflection.getNMS("EnumParticle").getField(particle).get(null);
            Constructor<?> particleConstructor = Reflection.getNMS("PacketPlayOutWorldParticles").getConstructor(
                    Reflection.getNMS("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
            Object packet = particleConstructor.newInstance(enumParticles, biggerRadius, x, y, z, xOffset, yOffset, zOffset, particledata, count, data);
            sendPacket(player, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", Reflection.getNMS("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Enchantment getEnchantment(String enchantNmn) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(enchantNmn.toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public WolfyUtilities(Plugin plugin) {
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

    public static Plugin getWUPlugin() {
        return Bukkit.getPluginManager().getPlugin("WolfyUtilities");
    }

    public static WolfyUtilities getWUCore() {
        return WolfyUtilities.get(getWUPlugin());
    }

    public static String getVersion() {
        return getWUPlugin().getDescription().getVersion();
    }

    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replaceAll("[^0-9]", ""));
    }

    public LanguageAPI getLanguageAPI() {
        return languageAPI;
    }

    public ConfigAPI getConfigAPI() {
        return configAPI;
    }

    public Chat getChat() {
        return chat;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public ItemUtils getItemUtils() {
        return itemUtils;
    }

    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }

    public BookUtil getBookUtil() {
        return bookUtil;
    }

    public boolean isConfigEnabled() {
        return languageAPI != null;
    }

    public InventoryAPI<?> getInventoryAPI() {
        return getInventoryAPI(inventoryAPI.getNewCacheInstance().getClass());
    }

    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
    }

    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(Class<T> type) {
        if (hasInventoryAPI() && type.isInstance(inventoryAPI.getNewCacheInstance())) {
            return (InventoryAPI<T>) inventoryAPI;
        } else if (!hasInventoryAPI()) {
            InventoryAPI<T> newInventoryAPI = new InventoryAPI<>(plugin, this, type);
            inventoryAPI = newInventoryAPI;
            return newInventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type " + type.getName() + " expected, got " + inventoryAPI.getNewCacheInstance().getClass().getName() + "!");
    }

    public boolean hasInventoryAPI() {
        return inventoryAPI != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getDataBasePrefix() {
        return dataBasePrefix;
    }

    public void setDataBasePrefix(String dataBasePrefix) {
        this.dataBasePrefix = dataBasePrefix;
    }

    public boolean hasDebuggingMode() {
        if (getConfigAPI().getConfig("main_config") instanceof Config) {
            return ((Config) getConfigAPI().getConfig("main_config")).getBoolean("debug");
        }
        return false;
    }
}