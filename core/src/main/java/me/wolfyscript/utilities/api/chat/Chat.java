package me.wolfyscript.utilities.api.chat;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Chat {

    public static final Map<UUID, PlayerAction> CLICK_DATA_MAP = new HashMap<>();

    private String consolePrefix;
    private String inGamePrefix;

    private final WolfyUtilities wolfyUtilities;
    private final LanguageAPI languageAPI;
    private final Plugin plugin;

    public Chat(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.languageAPI = wolfyUtilities.getLanguageAPI();
        this.plugin = wolfyUtilities.getPlugin();
        this.consolePrefix = "[" + plugin.getName() + "]";
        this.inGamePrefix = this.consolePrefix;
    }

    public String getInGamePrefix() {
        return inGamePrefix;
    }

    public void setInGamePrefix(String inGamePrefix) {
        this.inGamePrefix = inGamePrefix;
    }

    /**
     * @deprecated Due to logger changes it is no longer used!
     */
    @Deprecated
    public String getConsolePrefix() {
        return consolePrefix;
    }

    /**
     * @deprecated Due to logger changes it is no longer used!
     */
    @Deprecated
    public void setConsolePrefix(String consolePrefix) {
        this.consolePrefix = consolePrefix;
    }

    @Deprecated
    public String getIN_GAME_PREFIX() {
        return inGamePrefix;
    }

    @Deprecated
    public void setIN_GAME_PREFIX(String inGamePrefix) {
        this.inGamePrefix = inGamePrefix;
    }

    @Deprecated
    public String getCONSOLE_PREFIX() {
        return getConsolePrefix();
    }

    @Deprecated
    public void setCONSOLE_PREFIX(String consolePrefix) {
        setConsolePrefix(consolePrefix);
    }

    @Deprecated
    public void sendConsoleMessage(String message) {
        wolfyUtilities.getConsole().info(message);
    }

    @Deprecated
    public void sendConsoleMessage(String message, String... replacements) {
        wolfyUtilities.getConsole().log(Level.INFO, message, replacements);
    }

    @Deprecated
    public void sendConsoleMessage(String message, String[]... replacements) {
        wolfyUtilities.getConsole().log(Level.INFO, message, replacements);
    }

    /**
     * @see me.wolfyscript.utilities.api.console.Console
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#warn(String)}!
     */
    @Deprecated
    public void sendConsoleWarning(String message) {
        wolfyUtilities.getConsole().warn(message);
    }

    public void sendDebugMessage(String message) {
        if (wolfyUtilities.hasDebuggingMode()) {
            wolfyUtilities.getConsole().info(message);
        }
    }

    public void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(ChatColor.convert(inGamePrefix + languageAPI.replaceKeys(message)));
        }
    }

    public void sendMessages(Player player, String... messages) {
        if (player != null) {
            for (String message : messages) {
                player.sendMessage(ChatColor.convert(inGamePrefix + languageAPI.replaceKeys(message)));
            }
        }
    }

    @SafeVarargs
    public final void sendMessage(Player player, String message, Pair<String, String>... replacements) {
        if (player == null) return;
        if (replacements != null) {
            message = inGamePrefix + languageAPI.replaceColoredKeys(message);
            for (Pair<String, String> pair : replacements) {
                message = message.replaceAll(pair.getKey(), pair.getValue());
            }
        }
        player.sendMessage(ChatColor.convert(message));
    }
    /*
    Sends a global message from an GuiCluster to the player!
     */

    public void sendKey(Player player, String clusterID, String msgKey) {
        sendMessage(player, "$inventories." + clusterID + ".global_messages." + msgKey + "$");
    }

    public void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey) {
        sendMessage(player, "$inventories." + guiCluster.getId() + ".global_messages." + msgKey + "$");
    }

    public void sendKey(Player player, NamespacedKey namespacedKey, String msgKey) {
        sendMessage(player, "$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".messages." + msgKey + "$");
    }

    @SafeVarargs
    public final void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, "$inventories." + guiCluster.getId() + ".global_messages." + msgKey + "$", replacements);
    }

    @SafeVarargs
    public final void sendKey(Player player, NamespacedKey namespacedKey, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, "$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".messages." + msgKey + "$", replacements);
    }

    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(inGamePrefix, player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    public TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData) {
        TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(prefix);
        for (int i = 1; i < textComponents.length; i++) {
            ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(languageAPI.replaceColoredKeys(data.getMessage()));
            if (data.getClickAction() != null) {
                UUID id = UUID.randomUUID();
                while (CLICK_DATA_MAP.containsKey(id)) {
                    id = UUID.randomUUID();
                }
                PlayerAction playerAction = new PlayerAction(wolfyUtilities, player, data);
                CLICK_DATA_MAP.put(id, playerAction);
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/wua " + id));
            }
            for (ChatEvent<?, ?> chatEvent : data.getChatEvents()) {
                if (chatEvent instanceof HoverEvent) {
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(((HoverEvent) chatEvent).getAction(), ((HoverEvent) chatEvent).getValue()));
                } else if (chatEvent instanceof me.wolfyscript.utilities.api.chat.ClickEvent) {
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(((me.wolfyscript.utilities.api.chat.ClickEvent) chatEvent).getAction(), ((me.wolfyscript.utilities.api.chat.ClickEvent) chatEvent).getValue()));
                }
            }
            textComponents[i] = component;
        }
        return textComponents;
    }

    public static class ChatListener implements Listener {

        @EventHandler
        public void actionRemoval(PlayerQuitEvent event) {
            CLICK_DATA_MAP.keySet().removeIf(uuid -> CLICK_DATA_MAP.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
        }
    }

}
