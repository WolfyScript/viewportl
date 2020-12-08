package me.wolfyscript.utilities.api.chat;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {

    private String CONSOLE_PREFIX;
    private String CHAT_PREFIX;

    private final HashMap<UUID, PlayerAction> clickDataMap = new HashMap<>();

    private final WolfyUtilities wolfyUtilities;
    private final LanguageAPI languageAPI;
    private final Plugin plugin;

    public Chat(WolfyUtilities wolfyUtilities){
        this.wolfyUtilities = wolfyUtilities;
        this.languageAPI = wolfyUtilities.getLanguageAPI();
        this.plugin = wolfyUtilities.getPlugin();
        Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);
    }

    public void setCHAT_PREFIX(String CHAT_PREFIX) {
        this.CHAT_PREFIX = CHAT_PREFIX;
    }

    public void setCONSOLE_PREFIX(String CONSOLE_PREFIX) {
        this.CONSOLE_PREFIX = CONSOLE_PREFIX;
    }

    public String getCHAT_PREFIX() {
        return CHAT_PREFIX;
    }

    public String getCONSOLE_PREFIX() {
        return CONSOLE_PREFIX;
    }

    public void sendConsoleMessage(String message) {
        message = CONSOLE_PREFIX + languageAPI.replaceKeys(message);
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleWarning(String message) {
        message = CONSOLE_PREFIX + "[WARN] " + languageAPI.replaceKeys(message);
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public void sendConsoleMessage(String message, String... replacements) {
        message = CONSOLE_PREFIX + languageAPI.replaceKeys(message);
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("%([A-Z]*?)(_*?)%");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for (int i = 0; i < keys.size(); i++) {
            message = message.replace(keys.get(i), replacements[i]);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.convert(message));
    }

    public void sendConsoleMessage(String message, String[]... replacements) {
        if (replacements != null) {
            message = CHAT_PREFIX + languageAPI.replaceColoredKeys(message);
            for (String[] replace : replacements) {
                if (replace.length > 1) {
                    message = message.replaceAll(replace[0], replace[1]);
                }
            }
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.convert(message));
    }

    public void sendPlayerMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(ChatColor.convert(CHAT_PREFIX + languageAPI.replaceKeys(message)));
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
                message = CHAT_PREFIX + languageAPI.replaceColoredKeys(message);
                for (String[] replace : replacements) {
                    if (replace.length > 1) {
                        message = message.replaceAll(replace[0], replace[1]);
                    }
                }
            } else {
                return;
            }
        }
        player.sendMessage(ChatColor.convert(message));
    }

    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(CHAT_PREFIX, player, clickData);
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
                while (clickDataMap.containsKey(id)) {
                    id = UUID.randomUUID();
                }
                PlayerAction playerAction = new PlayerAction(wolfyUtilities, player, data);
                clickDataMap.put(id, playerAction);
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/wua " + id.toString()));
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

    public void sendDebugMessage(String message) {
        if (wolfyUtilities.hasDebuggingMode()) {
            String prefix = org.bukkit.ChatColor.translateAlternateColorCodes('&', "[&4CC&r] ");
            message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
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
                    Bukkit.getServer().getConsoleSender().sendMessage(prefix + result);
                }
            } else {
                message = prefix + message;
                Bukkit.getServer().getConsoleSender().sendMessage(message);
            }
        }
    }

    private class ChatListener implements Listener{

        //TODO: Move the logic to a CommandExecutor
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

        @EventHandler
        public void actionRemoval(PlayerQuitEvent event) {
            clickDataMap.keySet().removeIf(uuid -> clickDataMap.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
        }
    }

}
