package me.wolfyscript.utilities.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.Legacy;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.chat.ChatEvent;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.chat.PlayerAction;
import me.wolfyscript.utilities.main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WolfyUtilities implements Listener {

    private Plugin plugin;

    private String CONSOLE_PREFIX;
    private String CHAT_PREFIX;

    private String dataBasePrefix;

    private ConfigAPI configAPI;
    private InventoryAPI inventoryAPI;
    private LanguageAPI languageAPI;

    private static boolean hasLWC;
    private static boolean hasWorldGuard;
    private static boolean hasPlotSquared;

    private HashMap<UUID, PlayerAction> clickDataMap;

    public WolfyUtilities(Plugin plugin) {
        this.plugin = plugin;
        this.dataBasePrefix = plugin.getName().toLowerCase(Locale.ROOT) + "_";
        Main.registerWolfyUtilities(this);
        clickDataMap = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replace(".", ""));
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

    public InventoryAPI getInventoryAPI() {
        if (!hasInventoryAPI()) {
            inventoryAPI = new InventoryAPI(plugin, this);
        }
        return inventoryAPI;
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
        message = CONSOLE_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        Main.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleWarning(String message) {
        message = CONSOLE_PREFIX + "[WARN] " + getLanguageAPI().getActiveLanguage().replaceKeys(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        Main.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleMessage(String message, String... replacements) {
        message = CONSOLE_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
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
            message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
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
            message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
            message = WolfyUtilities.translateColorCodes(message);
            player.sendMessage(message);
        }
    }

    public void sendPlayerMessage(Player player, String message, String[]... replacements) {
        if (replacements != null) {
            if (player != null) {
                message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
                for (String[] replace : replacements) {
                    if (replace.length > 1) {
                        message = message.replaceAll(replace[0], replace[1]);
                    }
                }
            }else{
                return;
            }
        }
        player.sendMessage(WolfyUtilities.translateColorCodes(message));
    }

    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(CHAT_PREFIX);
        for (int i = 1; i < textComponents.length; i++) {
            ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(WolfyUtilities.translateColorCodes(getLanguageAPI().getActiveLanguage().replaceKeys(data.getMessage())));

            if (data.getClickAction() != null) {
                UUID id = UUID.randomUUID();
                while (clickDataMap.keySet().contains(id)) {
                    id = UUID.randomUUID();
                }
                PlayerAction playerAction = new PlayerAction(this, player, data);
                clickDataMap.put(id, playerAction);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "wu::" + id.toString()));
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
        player.spigot().sendMessage(textComponents);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void actionCommands(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null && event.getMessage().startsWith("wu::")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(event.getMessage().split("::")[1]);
            } catch (IllegalArgumentException expected) {
                return;
            }
            PlayerAction action = clickDataMap.get(uuid);
            if (action == null)
                return;
            event.setMessage("");
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (player.getUniqueId().equals(action.getUuid())) {
                action.run(player);
                if (action.isDiscard()) {
                    clickDataMap.remove(uuid);
                }
            }
        }
    }

    @EventHandler
    public void actionRemoval(PlayerQuitEvent event) {
        clickDataMap.keySet().removeIf(uuid -> clickDataMap.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
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

    static Random random = new Random();

    public static boolean hasVillagePillageUpdate() {
        return hasSpecificUpdate("v1_14_R0");
    }

    public static boolean hasAquaticUpdate() {
        return hasSpecificUpdate("v1_13_R0");
    }

    public static boolean hasCombatUpdate() {
        return hasSpecificUpdate("v1_9_R0");
    }

    public static boolean hasSpecificUpdate(String versionString) {
        String pkgname = Main.getInstance().getServer().getClass().getPackage().getName();
        String localeVersion = "v" + versionString + "_R0";
        localeVersion = localeVersion.replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
        String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
        return Integer.parseInt(version) >= Integer.parseInt(localeVersion);
    }

    public static boolean hasSpigot() {
        return hasClass("org.spigotmc.Metrics");
    }

    public static void setLWC() {
        hasLWC = hasClass("com.griefcraft.lwc.LWC");
    }

    public static void setPlotSquared() {
        hasPlotSquared = hasClass("com.intellectualcrafters.plot.api.PlotAPI");
    }

    public static void setWorldGuard() {
        hasWorldGuard = hasClass("com.sk89q.worldguard.WorldGuard");
    }

    public static boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    public static boolean hasPlotSquared() {
        return hasPlotSquared;
    }

    public static boolean hasLWC() {
        return hasLWC;
    }

    public static boolean hasClass(String path) {
        try {
            Class.forName(path);
            return true;
        } catch (Exception e) {
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

    public static String getCode() {
        Random random = new Random();
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int x = alphabet.length();
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sB.append(alphabet.charAt(random.nextInt(x)));
        }
        return sB.toString();
    }

    public static ItemStack getCustomHead(String value) {
        if (value.startsWith("http://textures")) {
            value = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
        }
        return getSkullByValue(value);
    }

    public static ItemStack getSkullViaURL(String value) {
        return getCustomHead("http://textures.minecraft.net/texture/" + value);
    }

    public static ItemStack getSkullByValue(String value) {
        ItemStack itemStack;
        if (WolfyUtilities.hasAquaticUpdate()) {
            itemStack = new ItemStack(Material.PLAYER_HEAD);
        } else {
            itemStack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 0, (byte) 3);
        }
        if (value != null && !value.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                itemStack.setItemMeta(skullMeta);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    public static SkullMeta getSkullmeta(String value, SkullMeta skullMeta) {
        if (value != null && !value.isEmpty()) {
            String texture = value;
            if (value.startsWith("https://") || value.startsWith("http://")) {
                texture = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                return skullMeta;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return skullMeta;
    }

    private static String getBase64EncodedString(String str) {
        ByteBuf byteBuf = null;
        ByteBuf encodedByteBuf = null;
        String var3;
        try {
            byteBuf = Unpooled.wrappedBuffer(str.getBytes(StandardCharsets.UTF_8));
            encodedByteBuf = Base64.encode(byteBuf);
            var3 = encodedByteBuf.toString(StandardCharsets.UTF_8);
        } finally {
            if (byteBuf != null) {
                byteBuf.release();
                if (encodedByteBuf != null) {
                    encodedByteBuf.release();
                }
            }
        }
        return var3;
    }


    public static String getSkullValue(SkullMeta skullMeta) {
        GameProfile profile = null;
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            try {
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
        }
        if (profile != null) {
            if (!profile.getProperties().get("textures").isEmpty()) {
                for (Property property : profile.getProperties().get("textures")) {
                    if (!property.getValue().isEmpty())
                        return property.getValue();
                }
            }
        }
        return null;
    }


    /*
    Both items Material must be equal to PLAYER_HEAD!

    Returns result with the texture from the input

    Returns result when one of the items is not Player Head
     */
    public static ItemStack migrateSkullTexture(ItemStack input, ItemStack result) {
        if (input.getType().equals(Material.PLAYER_HEAD) && result.getType().equals(Material.PLAYER_HEAD)) {
            SkullMeta inputMeta = (SkullMeta) input.getItemMeta();
            String value = getSkullValue(inputMeta);
            if (value != null && !value.isEmpty()) {
                result.setItemMeta(getSkullmeta(value, (SkullMeta) result.getItemMeta()));
            }
        }
        return result;
    }

    /*
    Result's Material must be equal to PLAYER_HEAD!

    Returns ItemMeta with the texture from the input

     */
    public static ItemMeta migrateSkullTexture(SkullMeta input, ItemStack result) {
        if (result.getType().equals(Material.PLAYER_HEAD)) {
            String value = getSkullValue(input);
            if (value != null && !value.isEmpty()) {
                return getSkullmeta(value, (SkullMeta) result.getItemMeta());
            }
        }
        return result.getItemMeta();
    }

    /*
    Sets a § before every letter!
    example:
        Hello World -> §H§e§l§l§o§ §W§o§r§l§d

    Because of this the String will be invisible in Minecraft!
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
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && b[i + 1] != ' ') {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static Enchantment getEnchantment(String enchantNmn) {
        try {
            if (!WolfyUtilities.hasAquaticUpdate()) {
                return Legacy.getEnchantment(enchantNmn);
            } else {
                return Enchantment.getByKey(NamespacedKey.minecraft(enchantNmn.toLowerCase()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Sound getSound(String sound) {
        return Sound.valueOf(sound);
    }

    public static Sound getSound(String legacy, String notLegacy) {
        if (WolfyUtilities.hasAquaticUpdate()) {
            return Sound.valueOf(notLegacy);
        } else {
            return Sound.valueOf(legacy);
        }
    }

    public static boolean isSkull(ItemStack itemStack) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            if (WolfyUtilities.hasAquaticUpdate()) {
                return true;
            } else return itemStack.getData().getData() == (byte) 3;
        }
        return false;
    }

    public static boolean checkColumn(ArrayList<String> shape, byte column) {
        for (String s : shape) {
            if (s.charAt(column) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static void clearColumn(ArrayList<String> shape, byte column) {
        for (int i = 0; i < shape.size(); i++) {
            shape.set(i, shape.get(i).substring(0, column) + shape.get(i).substring(column + 1));
        }
    }

    public static ArrayList<String> formatShape(String... shape) {
        ArrayList<String> cleared = new ArrayList<>(Arrays.asList(shape));
        List<Byte> columns = new ArrayList<>();
        List<Byte> rows = new ArrayList<>();
        if (shape[0].equals("   ") || shape[2].equals("   ")) {
            if (shape[0].equals("   ")) {
                rows.add((byte) 0);
            }
            if (shape[1].equals("   ")) {
                rows.add((byte) 1);
            }
            if (shape[2].equals("   ")) {
                rows.add((byte) 2);
            }
        }
        if (checkColumn(cleared, (byte) 0) || checkColumn(cleared, (byte) 2)) {
            if (checkColumn(cleared, (byte) 0)) {
                columns.add((byte) 0);
            }
            if (checkColumn(cleared, (byte) 1)) {
                columns.add((byte) 1);
            }
            if (checkColumn(cleared, (byte) 2)) {
                columns.add((byte) 2);
            }
        }
        int index = 0;
        Iterator<String> rowIt = cleared.iterator();
        while (rowIt.hasNext()) {
            rowIt.next();
            if (rows.contains((byte) index)) {
                rowIt.remove();
            }
            index++;
        }
        if (!columns.isEmpty()) {
            Collections.sort(columns);
            Collections.reverse(columns);
            for (byte i : columns) {
                clearColumn(cleared, i);
            }
        }
        return cleared;
    }


}
