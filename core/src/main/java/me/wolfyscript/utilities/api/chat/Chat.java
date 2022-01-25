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

import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Chat {
    
    protected Chat() {
        
    }

    public abstract void setChatPrefix(Component chatPrefix);

    public abstract Component getChatPrefix();

    public abstract MiniMessage getMiniMessage();

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    @Deprecated
    public abstract void sendMessage(Player player, String message);

    public abstract void sendMessage(Player player, Component component);

    public abstract void sendMessage(Player player, boolean prefix, Component component);

    @Deprecated
    public abstract void sendMessages(Player player, String... messages);

    public abstract void sendMessages(Player player, Component... components);

    public abstract void sendMessages(Player player, boolean prefix, Component... components);

    public abstract void sendMessage(Player player, String message, Pair<String, String>... replacements);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param clusterID
     * @param msgKey
     */
    @Deprecated
    public abstract void sendKey(Player player, String clusterID, String msgKey);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param guiCluster
     * @param msgKey
     */
    public abstract void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey);

    public abstract void sendKey(Player player, NamespacedKey windowKey, String msgKey);

    public abstract void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, List<Template> templates);

    public abstract void sendKey(Player player, NamespacedKey windowKey, String msgKey, List<Template> templates);

    @Deprecated
    public abstract void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, Pair<String, String>... replacements);

    @Deprecated
    public abstract void sendKey(Player player, NamespacedKey namespacedKey, String msgKey, Pair<String, String>... replacements);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param templates The placeholders and values in the message.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key, List<Template> templates);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param templates The placeholders and values in the message.
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key, boolean translateLegacyColor, List<Template> templates);

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
    public abstract ClickEvent executable(Player player, boolean discard, ClickAction action);

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
    public abstract void sendActionMessage(Player player, ClickData... clickData);

    @Deprecated
    public abstract TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData);

    static void removeClickData(UUID uuid) {
        ChatImpl.removeClickData(uuid);
    }

    static PlayerAction getClickData(UUID uuid) {
        return ChatImpl.getClickData(uuid);
    }

    /**
     * @deprecated Replaced by {@link #getChatPrefix()}
     * @return The chat prefix as a String.
     */
    @Deprecated
    public abstract String getInGamePrefix();

    /**
     * @deprecated Replaced by {@link #setChatPrefix(Component)}
     * @param inGamePrefix The chat prefix.
     */
    @Deprecated
    public abstract void setInGamePrefix(String inGamePrefix);

    /**
     * @deprecated Replaced by {@link #getInGamePrefix()}
     */
    @Deprecated(forRemoval = true)
    public String getIN_GAME_PREFIX() { return getInGamePrefix(); }

    /**
     * @deprecated Replaced by {@link #setInGamePrefix(String)}
     */
    @Deprecated(forRemoval = true)
    public void setIN_GAME_PREFIX(String inGamePrefix) { setInGamePrefix(inGamePrefix); }

    /**
     * @deprecated Due to logger changes it is no longer used and required!
     */
    @Deprecated(forRemoval = true)
    public abstract String getConsolePrefix();

    /**
     * @deprecated Due to logger changes it is no longer used and required!
     */
    @Deprecated(forRemoval = true)
    public void setConsolePrefix(String consolePrefix) {}

    /**
     * @deprecated Due to logger changes it is no longer used and required!
     */
    @Deprecated(forRemoval = true)
    public String getCONSOLE_PREFIX() { return getConsolePrefix(); }

    /**
     * @deprecated Due to logger changes it is no longer used and required!
     */
    @Deprecated(forRemoval = true)
    public void setCONSOLE_PREFIX(String consolePrefix) {}

    /**
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#info(String)}!
     */
    @Deprecated(forRemoval = true)
    public abstract void sendConsoleMessage(String message);

    /**
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#log(Level, String, String...)}!
     */
    @Deprecated(forRemoval = true)
    public abstract void sendConsoleMessage(String message, String... replacements);

    /**
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#log(Level, String, String[]...)}!
     */
    @Deprecated(forRemoval = true)
    public abstract void sendConsoleMessage(String message, String[]... replacements);

    /**
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#warn(String)}!
     */
    @Deprecated(forRemoval = true)
    public abstract void sendConsoleWarning(String message);

    /**
     * @deprecated Replaced by {@link me.wolfyscript.utilities.api.console.Console#debug(String)}!
     */
    @Deprecated(forRemoval = true)
    public abstract void sendDebugMessage(String message);
}
