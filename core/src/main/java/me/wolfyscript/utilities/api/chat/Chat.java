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
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
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

/**
 * Allows sending messages to players, with the specified prefix, translations, placeholders, etc.<br>
 * Additionally, this class provides a system to create text component click events, that execute specified callbacks.
 *
 * <p>
 * Since 3.16.1, the whole message system uses the adventure api to send chat messages.<br>
 * Therefor, translated and click actions are also part of the component eco system.<br>
 * To get those components see the specific method:<br>
 * - {@link #translated}<br>
 * - {@link #executable(Player, boolean, ClickAction)}
 *
 * </p>
 *
 *
 *
 * <br>
 * (Yes this could be an interface, but for backwards compatibility it must be a class!)
 */
public abstract class Chat {
    
    protected Chat() { /* Only for the implementation */ }

    /**
     * Sets the prefix for this chat message handler.<br>
     *
     * You are still able to send messages without prefix, if you disable it using the parameter.
     * See {@link #sendMessage(Player, boolean, Component)} or {@link #sendMessages(Player, boolean, Component...)}
     *
     * @param chatPrefix The chat prefix.
     */
    public abstract void setChatPrefix(Component chatPrefix);

    /**
     * Gets the prefix of this chat message handler.
     *
     * @return The chat prefix.
     */
    public abstract Component getChatPrefix();

    /**
     * Gets the {@link MiniMessage} object, that allows you to parse text with formatting similar to html.<br>
     * See <a href="https://docs.adventure.kyori.net/minimessage/">MiniMessage docs</a>
     *
     * @return The MiniMessage object
     */
    public abstract MiniMessage getMiniMessage();

    /**
     * Sends a chat component message, with the previously set prefix, to the player.
     *
     * @param player The player to send the message to.
     * @param component The component to send.
     */
    public abstract void sendMessage(Player player, Component component);

    /**
     * Sends a chat component message to the player.<br>
     * The prefix can be disabled, which just sends the component as is.
     *
     * @param player The player to send the message to.
     * @param prefix If the message should have the prefix.
     * @param component The component to send.
     */
    public abstract void sendMessage(Player player, boolean prefix, Component component);

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Use {@link #translated(String)} or {@link #translated(String, boolean)} to translate language keys!
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    @Deprecated
    public abstract void sendMessage(Player player, String message);

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Use {@link #translated(String, List)} or {@link #translated(String, boolean, List)} to translate language keys!
     * @param player The player to send the message to.
     * @param message The message to send.
     * @param replacements The placeholder values to replace in the message.
     */
    @Deprecated
    public abstract void sendMessage(Player player, String message, Pair<String, String>... replacements);

    /**
     * Sends chat component messages to the player.<br>
     * Each message will be composed of the prefix and component.
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    public abstract void sendMessages(Player player, Component... components);

    /**
     * Sends chat component messages to the player.<br>
     * If `prefix` is set to false, then the messages are just composed of the component.<br>
     * Otherwise, it does the same as {@link #sendMessages(Player, Component...)}
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    public abstract void sendMessages(Player player, boolean prefix, Component... components);

    /**
     * Sends messages to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Use {@link #translated(String)} or {@link #translated(String, boolean)} to translate language keys!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param messages The messages to send.
     */
    @Deprecated
    public abstract void sendMessages(Player player, String... messages);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Use {@link #translated(String)} or {@link #translated(String, boolean)} to translate language keys!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param msgKey The key of the messages to send.
     */
    @Deprecated
    public abstract void sendKey(Player player, String clusterID, String msgKey);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param msgKey The key of the messages to send.
     */
    public abstract void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey);

    /**
     * Sends a message of the {@link GuiWindow} to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     *             Consider using the {@link GuiWindow#translatedMsgKey(String)} to get the translated message from the window.
     * @param player The player to send the message to.
     * @param msgKey The key of the messages to send.
     */
    public abstract void sendKey(Player player, NamespacedKey windowKey, String msgKey);

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
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key, boolean translateLegacyColor);

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
     * <p>
     * It will internally link a command with an id to the code to execute.<br>
     * That internal command can only be executed by the player, who received the message.
     * </p>
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
     * @deprecated This was mostly used to run code when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     *
     * @param player The player to send the message to.
     * @param clickData The click data of the message.
     */
    @Deprecated
    public abstract void sendActionMessage(Player player, ClickData... clickData);

    /**
     * Sends the clickable chat messages to the player.<br>
     * It allows you to also include ClickData with executable code.
     *
     * @deprecated This was mostly used to run code when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     *
     * @param player The player to send the message to.
     * @param clickData The click data of the message.
     */
    @Deprecated
    public abstract TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData);

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
