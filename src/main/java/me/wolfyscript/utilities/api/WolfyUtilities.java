package me.wolfyscript.utilities.api;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.chat.ChatEvent;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.chat.PlayerAction;
import me.wolfyscript.utilities.api.utils.exceptions.InvalidCacheTypeException;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import me.wolfyscript.utilities.main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WolfyUtilities implements Listener {

    private static final HashMap<Plugin, WolfyUtilities> wolfyUtilitiesList = new HashMap<>();
    private static CustomItems customItems;
    private static Particles particles;
    private static ParticleEffects particleEffects;

    static Random random = new Random();

    public static CustomItems getCustomItems() {
        return customItems;
    }

    public static void registerAPI(WolfyUtilities wolfyUtilities) {
        if (!hasAPI(wolfyUtilities.getPlugin())) {
            wolfyUtilitiesList.put(wolfyUtilities.getPlugin(), wolfyUtilities);
        }
    }

    private static final HashMap<UUID, PlayerAction> clickDataMap = new HashMap<>();

    public static boolean hasAPI(Plugin plugin) {
        return wolfyUtilitiesList.containsKey(plugin);
    }

    public static WolfyUtilities getOrCreateAPI(Plugin plugin) {
        if (!hasAPI(plugin)) {
            registerAPI(new WolfyUtilities(plugin));
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

    /**
     * @return if the minecraft version is 1.13 or higher
     * @deprecated This plugin no longer supports pre-1.14 versions therefore making this method useless!
     */
    @Deprecated
    public static boolean hasAquaticUpdate() {
        return hasSpecificUpdate(113);
    }

    public static boolean hasSpecificUpdate(String versionString) {
        return Main.getMcUpdateVersionNumber() >= Integer.parseInt(versionString.replace("_", "").replace(".", "").replace("-", ""));
    }

    public static boolean hasSpecificUpdate(int versionNumber) {
        return Main.getMcUpdateVersionNumber() >= versionNumber;
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

    private static final HashMap<String, Boolean> classes = new HashMap<>();

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

    public static boolean hasPermission(CommandSender sender, String permCode) {
        List<String> permissions = Arrays.asList(permCode.split("\\."));
        StringBuilder permission = new StringBuilder();
        if (sender.hasPermission("*")) {
            return true;
        }
        for (String perm : permissions) {
            permission.append(perm);
            if (permissions.indexOf(perm) < permissions.size() - 1) {
                permission.append(".*");
            }
            if (sender.hasPermission(permission.toString())) {
                return true;
            }
            permission.replace(permission.length() - 2, permission.length(), "");
            permission.append(".");
        }
        return false;
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

    /**
     * Sets a § before every letter!
     * example:
     * Hello World -> §H§e§l§l§o§ §W§o§r§l§d
     * <p>
     * Because of this the String will be invisible in Minecraft!
     */
    public static String hideString(String hide) {
        char[] data = new char[hide.length() * 2];
        for (int i = 0; i < data.length; i += 2) {
            data[i] = 167;
            data[i + 1] = hide.charAt(i == 0 ? 0 : i / 2);
        }
        return new String(data);
    }

    public static String unhideString(String unhide) {
        return unhide.replace("§", "");
    }

    public static String translateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&') {
                if (b[i + 1] == '&') {
                    b[i + 1] = '=';
                } else {
                    b[i] = 167;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }
        }
        return new String(b).replace("&=", "&");
    }

    public static Enchantment getEnchantment(String enchantNmn) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(enchantNmn.toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Sound getSound(String sound) {
        return Sound.valueOf(sound);
    }

    @Deprecated
    public static Sound getSound(String legacy, String notLegacy) {
        return Sound.valueOf(notLegacy);
    }

    /**
     * Checks if the column of the recipe shape is blocked by items.
     * When the column is empty it will be removed from the shape ArrayList!
     *
     * @param shape  the shape of the recipe
     * @param column the index of the column
     * @return true if the column is blocked
     */
    public static boolean checkColumn(ArrayList<String> shape, int column) {
        boolean blocked = shape.stream().anyMatch(s -> column < s.length() && s.charAt(column) != ' ');
        if (!blocked) {
            for (int i = 0; i < shape.size(); i++) {
                if(column < shape.get(i).length()){
                    shape.set(i, shape.get(i).substring(0, column) + shape.get(i).substring(column + 1));
                }
            }
        }
        return blocked;
    }

    /**
     * Formats the recipe shape to it's smallest possible size.
     * That means if a recipe that was created inside a 3x3 grid only takes up 2x1
     * this method will shrink the array down from a 3x3 to 2x1 array
     *
     * @param shape the recipe that should be formatted
     * @return the shrunken ArrayList of the recipe shape
     */
    public static ArrayList<String> formatShape(String... shape) {
        ArrayList<String> cleared = new ArrayList<>(Arrays.asList(shape));
        ListIterator<String> rowIterator = cleared.listIterator();
        while (rowIterator.hasNext()) {
            if (!StringUtils.isBlank(rowIterator.next())) {
                break;
            }
            rowIterator.remove();
        }
        while (rowIterator.hasNext()) {
            rowIterator.next();
        }
        while (rowIterator.hasPrevious()) {
            if (!StringUtils.isBlank(rowIterator.previous())) {
                break;
            }
            rowIterator.remove();
        }
        if (!cleared.isEmpty()) {
            boolean columnBlocked = false;
            while (!columnBlocked) {
                if (checkColumn(cleared, 0)) {
                    columnBlocked = true;
                }
            }
            columnBlocked = false;
            int column = cleared.get(0).length() - 1;
            while (!columnBlocked) {
                if (checkColumn(cleared, column)) {
                    columnBlocked = true;
                } else {
                    column--;
                }
            }
        }
        return cleared;
    }

    /**
     * Gets the current registered API for that plugin.
     * Can be null when no API is registered!
     *
     * @param plugin the plugin
     * @return the current API of the plugin
     */
    @Nullable
    public static WolfyUtilities getAPI(Plugin plugin) {
        return wolfyUtilitiesList.get(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void actionCommands(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/wua ")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(event.getMessage().substring("/wua ".length()));
            } catch (IllegalArgumentException expected) {
                return;
            }
            PlayerAction action = clickDataMap.get(uuid);
            Player player = event.getPlayer();
            event.setCancelled(true);
            if (action != null) {
                if (player.getUniqueId().equals(action.getUuid())) {
                    action.run(player);
                    if (action.isDiscard()) {
                        clickDataMap.remove(uuid);
                    }
                }
            } else {
                sendDebugMessage(player.getName() + "&c tried to use a invalid action!");
            }
        }
    }

    /*
        Non Static methods and constructor down below!
     */
    private final Plugin plugin;

    private String CONSOLE_PREFIX;

    private String CHAT_PREFIX;

    private String dataBasePrefix;
    private ConfigAPI configAPI;

    private InventoryAPI inventoryAPI;

    private LanguageAPI languageAPI;

    @EventHandler
    public void actionRemoval(PlayerQuitEvent event) {
        clickDataMap.keySet().removeIf(uuid -> clickDataMap.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
    }

    public WolfyUtilities(Plugin plugin) {
        this.plugin = plugin;
        this.dataBasePrefix = plugin.getName().toLowerCase(Locale.ROOT) + "_";
        registerAPI(this);
        customItems = new CustomItems(plugin);
        inventoryAPI = new InventoryAPI<>(this.plugin, this, CustomCache.class);
    }

    public static String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replaceAll("[^0-9]", ""));
    }

    public LanguageAPI getLanguageAPI() {
        if (!hasLanguageAPI()) {
            languageAPI = new LanguageAPI(this.plugin);
        }
        return languageAPI;
    }

    public boolean isLanguageEnabled() {
        return languageAPI != null;
    }

    public ConfigAPI getConfigAPI() {
        if (!hasConfigAPI()) {
            configAPI = new ConfigAPI(this);
        }
        return configAPI;
    }

    public boolean isConfigEnabled() {
        return languageAPI != null;
    }

    public InventoryAPI<?> getInventoryAPI() {
        return getInventoryAPI(inventoryAPI.craftCustomCache().getClass());
    }

    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
    }

    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(Class<T> type) {
        if (hasInventoryAPI() && type.isInstance(inventoryAPI.craftCustomCache())) {
            return (InventoryAPI<T>) inventoryAPI;
        } else if (!hasInventoryAPI()) {
            inventoryAPI = new InventoryAPI<>(plugin, this, type);
            return inventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type " + type.getName() + " expected, got " + inventoryAPI.craftCustomCache().getClass().getName() + "!");
    }

    public boolean hasInventoryAPI() {
        return inventoryAPI != null;
    }

    public boolean hasLanguageAPI() {
        return languageAPI != null;
    }

    public boolean hasConfigAPI() {
        return configAPI != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setCONSOLE_PREFIX(String CONSOLE_PREFIX) {
        this.CONSOLE_PREFIX = CONSOLE_PREFIX;
    }

    public void setCHAT_PREFIX(String CHAT_PREFIX) {
        this.CHAT_PREFIX = CHAT_PREFIX;
    }

    public String getCHAT_PREFIX() {
        return CHAT_PREFIX;
    }

    public String getCONSOLE_PREFIX() {
        return CONSOLE_PREFIX;
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

    public void sendConsoleMessage(String message) {
        message = CONSOLE_PREFIX + getLanguageAPI().replaceKeys(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        Main.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleWarning(String message) {
        message = CONSOLE_PREFIX + "[WARN] " + getLanguageAPI().replaceKeys(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        Main.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleMessage(String message, String... replacements) {
        message = CONSOLE_PREFIX + getLanguageAPI().replaceKeys(message);
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("%([A-Z]*?)(_*?)%");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for (int i = 0; i < keys.size(); i++) {
            message = message.replace(keys.get(i), replacements[i]);
        }
        plugin.getServer().getConsoleSender().sendMessage(WolfyUtilities.translateColorCodes(message));
    }

    public void sendConsoleMessage(String message, String[]... replacements) {
        if (replacements != null) {
            message = CHAT_PREFIX + getLanguageAPI().replaceColoredKeys(message);
            for (String[] replace : replacements) {
                if (replace.length > 1) {
                    message = message.replaceAll(replace[0], replace[1]);
                }
            }
        }
        plugin.getServer().getConsoleSender().sendMessage(WolfyUtilities.translateColorCodes(message));
    }

    public void sendPlayerMessage(Player player, String message) {
        if (player != null) {
            message = CHAT_PREFIX + getLanguageAPI().replaceKeys(message);
            message = WolfyUtilities.translateColorCodes(message);
            player.sendMessage(message);
        }
    }

    /*
    Sends a global message from an GuiCluster to the player!
     */
    public void sendPlayerMessage(Player player, String guiCluster, String msgKey) {
        sendPlayerMessage(player, "$inventories." + guiCluster + ".global_messages." + msgKey + "$");
    }

    public void sendPlayerMessage(Player player, String guiCluster, String guiWindow, String msgKey) {
        sendPlayerMessage(player, "$inventories." + guiCluster + "." + guiWindow + ".messages." + msgKey + "$");
    }

    public void sendPlayerMessage(Player player, String guiCluster, String msgKey, String[]... replacements) {
        String message = "$inventories." + guiCluster + ".global_messages." + msgKey + "$";
        sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(Player player, String guiCluster, String guiWindow, String msgKey, String[]... replacements) {
        String message = "$inventories." + guiCluster + "." + guiWindow + ".messages." + msgKey + "$";
        sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(Player player, String message, String[]... replacements) {
        if (replacements != null) {
            if (player != null) {
                message = CHAT_PREFIX + getLanguageAPI().replaceColoredKeys(message);
                for (String[] replace : replacements) {
                    if (replace.length > 1) {
                        message = message.replaceAll(replace[0], replace[1]);
                    }
                }
            } else {
                return;
            }
        }
        player.sendMessage(WolfyUtilities.translateColorCodes(message));
    }

    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(CHAT_PREFIX, player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    public void openBook(Player player, String author, String title, boolean editable, ClickData[]... clickDatas) {
        ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (ClickData[] clickData : clickDatas) {
            TextComponent[] textComponents = getActionMessage("", player, clickData);
            bookMeta.spigot().addPage(textComponents);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(Player player, boolean editable, ClickData[]... clickDatas) {
        openBook(player, "WolfyUtilities", "Blank", editable, clickDatas);
    }

    public TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData) {
        TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(prefix);
        for (int i = 1; i < textComponents.length; i++) {
            ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(getLanguageAPI().replaceColoredKeys(data.getMessage()));
            if (data.getClickAction() != null) {
                UUID id = UUID.randomUUID();
                while (clickDataMap.containsKey(id)) {
                    id = UUID.randomUUID();
                }
                PlayerAction playerAction = new PlayerAction(this, player, data);
                clickDataMap.put(id, playerAction);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wua " + id.toString()));
            }
            for (ChatEvent chatEvent : data.getChatEvents()) {
                if (chatEvent instanceof HoverEvent) {
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(((HoverEvent) chatEvent).getAction(), ((HoverEvent) chatEvent).getValue()));
                } else if (chatEvent instanceof me.wolfyscript.utilities.api.utils.chat.ClickEvent) {
                    component.setClickEvent(new ClickEvent(((me.wolfyscript.utilities.api.utils.chat.ClickEvent) chatEvent).getAction(), ((me.wolfyscript.utilities.api.utils.chat.ClickEvent) chatEvent).getValue()));
                }
            }
            textComponents[i] = component;
        }
        return textComponents;
    }

    public void sendDebugMessage(String message) {
        if (hasDebuggingMode()) {
            String prefix = ChatColor.translateAlternateColorCodes('&', "[&4CC&r] ");
            message = ChatColor.translateAlternateColorCodes('&', message);
            List<String> messages = new ArrayList<>();
            if (message.length() > 70) {
                int count = message.length() / 70;
                for (int text = 0; text <= count; text++) {
                    if (text < count) {
                        messages.add(message.substring(text * 70, 70 + 70 * text));
                    } else {
                        messages.add(message.substring(text * 70));
                    }
                }
                for (String result : messages) {
                    Main.getInstance().getServer().getConsoleSender().sendMessage(prefix + result);
                }
            } else {
                message = prefix + message;
                Main.getInstance().getServer().getConsoleSender().sendMessage(message);
            }
        }
    }

    public ItemStack translateItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (getLanguageAPI().getActiveLanguage() != null) {
                        displayName = getLanguageAPI().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
                }
                if (itemMeta.hasLore() && getLanguageAPI().getActiveLanguage() != null) {
                    List<String> newLore = new ArrayList<>();
                    for (String row : itemMeta.getLore()) {
                        if (row.startsWith("[WU]")) {
                            newLore.add(getLanguageAPI().replaceKeys(row.substring("[WU]".length())));
                        } else if (row.startsWith("[WU!]")) {
                            for (String newRow : getLanguageAPI().replaceKey(row.substring("[WU!]".length()))) {
                                newLore.add(WolfyUtilities.translateColorCodes(newRow));
                            }
                        } else {
                            newLore.add(row);
                        }
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }
}