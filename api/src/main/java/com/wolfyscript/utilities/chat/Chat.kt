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

package com.wolfyscript.utilities.chat;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.language.LanguageAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.wolfyscript.utilities.platform.adapters.Player;

import java.util.List;

/**
 * Allows sending messages to players, with the specified prefix, translations, placeholders, etc.<br>
 * Additionally, this class provides a system to create text component click events, that execute specified callbacks.
 *
 * <p>
 * Since 3.16.1, the whole message system uses the adventure api to send chat messages.<br>
 * Therefor, translated and click actions are also part of the component eco system.<br>
 * To get those components see the specific method:<br>
 * - {@link #translated}<br>
 * - {@link #executable(Player, boolean, ClickActionCallback)}
 *
 * </p>
 *
 *
 *
 * <br>
 * (Yes this could be an interface, but for backwards compatibility it must be a class!)
 */
public abstract class Chat {

    protected final WolfyUtils wolfyUtils;

    private Component chatPrefix;
    protected final MiniMessage miniMessage;
    protected final LanguageAPI languageAPI;

    protected Chat(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.languageAPI = wolfyUtils.getLanguageAPI();
        this.chatPrefix = Component.text("[" + wolfyUtils.getName() + "]");
        this.miniMessage = MiniMessage.builder().tags(TagResolver.standard())/*.debug(System.out::println)*/.build();
    }

    /**
     * Sets the prefix for this chat message handler.<br>
     *
     * You are still able to send messages without prefix, if you disable it using the parameter.
     * See {@link #sendMessage(Player, boolean, Component)} or {@link #sendMessages(Player, boolean, Component...)}
     *
     * @param chatPrefix The chat prefix.
     */
    public void setChatPrefix(Component chatPrefix) {
        this.chatPrefix = chatPrefix;
    }

    /**
     * Gets the prefix of this chat message handler.
     *
     * @return The chat prefix.
     */
    public Component getChatPrefix() {
        return chatPrefix;
    }

    /**
     * Gets the {@link MiniMessage} object, that allows you to parse text with formatting similar to html.<br>
     * See <a href="https://docs.adventure.kyori.net/minimessage/">MiniMessage docs</a>
     *
     * @return The MiniMessage object
     */
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

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
     * @param resolvers The custom tag resolvers to use.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key, TagResolver... resolvers);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolver The custom tag resolver to use.
     * @return The component set for the key; empty component if not available.
     */
    public abstract Component translated(String key, TagResolver resolver);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    @Deprecated
    public Component translated(String key, boolean translateLegacyColor) {
        return translated(key);
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolvers The custom tag resolvers to use.
     * @return The component set for the key; empty component if not available.
     */
    @Deprecated
    public Component translated(String key, List<? extends TagResolver> resolvers) {
        return translated(key, resolvers.toArray(new TagResolver[0]));
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolvers The custom tag resolvers to use.
     * @param translateLegacyColor If it should translate legacy '&' color codes.
     * @return The component set for the key; empty component if not available.
     */
    @Deprecated
    public Component translated(String key, boolean translateLegacyColor, List<? extends TagResolver> resolvers) {
        return translated(key, resolvers.toArray(new TagResolver[0]));
    }

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
    public abstract ClickEvent executable(Player player, boolean discard, ClickActionCallback action);

    /**
     * Converts the old placeholder to a new mini-message compatible tag.<br>
     * If the placeholder wasn't already converted, it converts it and returns it.<br>
     * Otherwise, it uses the cached value and returns it.
     *
     * @param oldPlaceholder The old placeholder, that might be incompatible with mini-message.
     * @return The converted mini-message compatible placeholder.
     */
    public abstract String convertOldPlaceholder(String oldPlaceholder);
}
