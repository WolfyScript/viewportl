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

package me.wolfyscript.utilities.api.chat;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ChatImpl extends Chat {

    protected static final Map<UUID, PlayerAction> CLICK_DATA_MAP = new HashMap<>();

    private final LegacyComponentSerializer LEGACY_SERIALIZER;
    private final BungeeComponentSerializer BUNGEE_SERIALIZER;

    private Component chatPrefix;

    private final WolfyUtilities wolfyUtilities;
    private final LanguageAPI languageAPI;
    private final Plugin plugin;
    private final MiniMessage miniMessage;

    public ChatImpl(@NotNull WolfyUtilities wolfyUtilities) {
        super();
        this.wolfyUtilities = wolfyUtilities;
        this.languageAPI = wolfyUtilities.getLanguageAPI();
        this.plugin = wolfyUtilities.getPlugin();
        this.chatPrefix = Component.text("[" + plugin.getName() + "]");
        this.miniMessage = MiniMessage.builder().markdown().markdownFlavor(DiscordFlavor.get()).parsingErrorMessageConsumer(strings -> {
            for (String string : strings) {
                wolfyUtilities.getConsole().getLogger().warning(string);
            }
        }).build();
        this.LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
        this.BUNGEE_SERIALIZER = BungeeComponentSerializer.get();
    }

    /**
     * @deprecated Replaced by {@link #getChatPrefix()}
     * @return The chat prefix as a String.
     */
    @Deprecated
    @Override
    public String getInGamePrefix() {
        return BukkitComponentSerializer.legacy().serialize(chatPrefix);
    }

    /**
     * @deprecated Replaced by {@link #setChatPrefix(Component)}
     * @param inGamePrefix The chat prefix.
     */
    @Deprecated
    @Override
    public void setInGamePrefix(String inGamePrefix) {
        this.chatPrefix = BukkitComponentSerializer.legacy().deserialize(inGamePrefix);
    }

    @Override
    public void setChatPrefix(Component chatPrefix) {
        this.chatPrefix = chatPrefix;
    }

    @Override
    public Component getChatPrefix() {
        return chatPrefix;
    }

    @Override
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    @Deprecated
    @Override
    public void sendMessage(Player player, String message) {
        if (player != null) {
            sendMessage(player, true, LEGACY_SERIALIZER.deserialize(ChatColor.convert(languageAPI.replaceKeys(message))));
        }
    }

    @Override
    public void sendMessage(Player player, Component component) {
        if (player != null) {
            player.spigot().sendMessage(BUNGEE_SERIALIZER.serialize(component));
        }
    }

    @Override
    public void sendMessage(Player player, boolean prefix, Component component) {
        if (player != null) {
            if (prefix) {
                component = Component.empty().append(chatPrefix).append(component);
            }
            player.spigot().sendMessage(BUNGEE_SERIALIZER.serialize(component));
        }
    }

    /**
     * Sends multiple messages to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the messages multiple times (Not efficient!) {@link #sendMessages(Player, Component...)} should be used instead!
     * @param player The player to send the message to.
     * @param messages The messages to send.
     */
    @Deprecated
    @Override
    public void sendMessages(Player player, String... messages) {
        if (player != null) {
            for (String message : messages) {
                sendMessage(player, true, LEGACY_SERIALIZER.deserialize(ChatColor.convert(languageAPI.replaceKeys(message))));
            }
        }
    }

    @Override
    public void sendMessages(Player player, Component... components) {
        if (player != null) {
            for (Component component : components) {
                player.spigot().sendMessage(BUNGEE_SERIALIZER.serialize(component));
            }
        }
    }

    @Override
    public void sendMessages(Player player, boolean prefix, Component... components) {
        for (Component component : components) {
            sendMessage(player, prefix, component);
        }
    }

    @SafeVarargs
    @Override
    public final void sendMessage(Player player, String message, Pair<String, String>... replacements) {
        if (player == null) return;
        if (replacements != null) {
            message = getInGamePrefix() + languageAPI.replaceColoredKeys(message);
            for (Pair<String, String> pair : replacements) {
                message = message.replaceAll(pair.getKey(), pair.getValue());
            }
        }
        player.sendMessage(ChatColor.convert(message));
    }

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param clusterID
     * @param msgKey
     */
    @Deprecated
    @Override
    public void sendKey(Player player, String clusterID, String msgKey) {
        sendMessage(player, translated("inventories." + clusterID + ".global_messages." + msgKey, true));
    }

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param guiCluster
     * @param msgKey
     */
    @Override
    public void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey) {
        sendMessage(player, translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey, true));
    }

    @Override
    public void sendKey(Player player, @NotNull NamespacedKey windowKey, String msgKey) {
        sendMessage(player, translated("inventories." + windowKey.getNamespace() + "." + windowKey.getKey() + ".messages." + msgKey, true));
    }

    @Override
    public final void sendKey(Player player, @NotNull GuiCluster<?> guiCluster, String msgKey, List<Template> templates) {
        sendMessage(player, translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey, templates));
    }

    @Override
    public final void sendKey(Player player, NamespacedKey windowKey, String msgKey, List<Template> templates) {
        sendMessage(player, translated("inventories." + windowKey.getNamespace() + "." + windowKey.getKey() + ".messages." + msgKey, templates));
    }

    @Deprecated
    @SafeVarargs
    @Override
    public final void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey, true, getTemplates(replacements)));
    }

    @Deprecated
    @SafeVarargs
    @Override
    public final void sendKey(Player player, NamespacedKey namespacedKey, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, translated("inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".messages." + msgKey, true, getTemplates(replacements)));
    }

    private List<Template> getTemplates(Pair<String, String>[] replacements) {
        return Arrays.stream(replacements).map(pair -> Template.of(pair.getKey(), pair.getValue())).toList();
    }

    @Override
    public Component translated(String key) {
        return languageAPI.getComponent(key);
    }

    @Override
    public Component translated(String key, List<Template> templates) {
        return languageAPI.getComponent(key, templates);
    }

    @Override
    public Component translated(String key, boolean translateLegacyColor, List<Template> templates) {
        return languageAPI.getComponent(key, translateLegacyColor, templates);
    }

    /**
     * Creates a ClickEvent, that executes code when clicked.<br>
     * It will internally link a command with an id to the code to execute.
     * That internal command can only be executed by the player, which the message was sent to.
     *
     * @param player The player the event belongs to.
     * @param discard If it should be discarded after clicked. (Any action is removed, when the player disconnects!)
     * @param action The action to execute on click.
     * @return The ClickEvent with the generated command.
     */
    @Override
    public net.kyori.adventure.text.event.ClickEvent executable(Player player, boolean discard, ClickAction action) {
        Preconditions.checkArgument(action != null, "The click action cannot be null!");
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (CLICK_DATA_MAP.containsKey(id));
        CLICK_DATA_MAP.put(id, new PlayerAction(wolfyUtilities, player, action, discard));
        return net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/wua " + id);
    }

    /**
     * Sends the clickable chat messages to the player.<br>
     * It allows you to also include ClickData with executable code.
     *
     * @deprecated This was mostly used to run code, when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     *
     * @param player The player to send the message to.
     * @param clickData The click data of the message.
     */
    @Deprecated
    @Override
    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(getInGamePrefix(), player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    @Deprecated
    @Override
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

    public static void removeClickData(UUID uuid) {
        CLICK_DATA_MAP.remove(uuid);
    }

    public static PlayerAction getClickData(UUID uuid) {
        return CLICK_DATA_MAP.get(uuid);
    }

    @Override
    public String getConsolePrefix() {
        return "[" + plugin.getName() + "]";
    }

    @Override
    public void sendConsoleMessage(String message) {
        wolfyUtilities.getConsole().info(message);
    }

    @Override
    public void sendConsoleMessage(String message, String... replacements) {
        wolfyUtilities.getConsole().log(Level.INFO, message, replacements);
    }

    @Override
    public void sendConsoleMessage(String message, String[]... replacements) {
        wolfyUtilities.getConsole().log(Level.INFO, message, replacements);
    }

    @Override
    public void sendConsoleWarning(String message) {
        wolfyUtilities.getConsole().warn(message);
    }

    @Override
    public void sendDebugMessage(String message) {
        wolfyUtilities.getConsole().debug(message);
    }

    /**
     * Listener for chat specific features.
     *
     */
    public static class ChatListener implements Listener {

        @EventHandler
        public void actionRemoval(PlayerQuitEvent event) {
            CLICK_DATA_MAP.keySet().removeIf(uuid -> CLICK_DATA_MAP.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
        }
    }

}
