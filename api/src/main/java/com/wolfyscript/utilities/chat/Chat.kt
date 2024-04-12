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
package com.wolfyscript.utilities.chat

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.platform.adapters.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * Allows sending messages to players, with the specified prefix, translations, placeholders, etc.<br>
 * Additionally, this class provides a system to create text component click events, that execute specified callbacks.
 *
 */
interface Chat {

    val wolfyUtils: WolfyUtils

    /**
     * Gets the [MiniMessage] object, that allows you to parse text with formatting similar to html.<br></br>
     * See [MiniMessage docs](https://docs.adventure.kyori.net/minimessage/)
     *
     * @return The MiniMessage object
     */
    val miniMessage: MiniMessage

    /**
     * The prefix for this chat message handler.<br>
     *
     * You are still able to send messages without prefix, if you disable it using the parameter.
     * See [sendMessage] or [sendMessages]
     *
     */
    var chatPrefix: Component

    /**
     * Sends a chat component message, with the previously set prefix, to the player.
     *
     * @param player The player to send the message to.
     * @param component The component to send.
     */
    fun sendMessage(player: Player, component: Component)

    /**
     * Sends a chat component message to the player.<br></br>
     * The prefix can be disabled, which just sends the component as is.
     *
     * @param player The player to send the message to.
     * @param prefix If the message should have the prefix.
     * @param component The component to send.
     */
    fun sendMessage(player: Player, prefix: Boolean, component: Component)

    /**
     * Sends chat component messages to the player.<br></br>
     * Each message will be composed of the prefix and component.
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    fun sendMessages(player: Player, vararg components: Component)

    /**
     * Sends chat component messages to the player.<br></br>
     * If `prefix` is set to false, then the messages are just composed of the component.<br></br>
     * Otherwise, it does the same as [.sendMessages]
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    fun sendMessages(player: Player, prefix: Boolean, vararg components: Component)

    /**
     * Creates a [Component] of the specified language key.<br></br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @return The component set for the key; empty component if not available.
     */
    fun translated(key: String): Component

    /**
     * Creates a [Component] of the specified language key.<br></br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolvers The custom tag resolvers to use.
     * @return The component set for the key; empty component if not available.
     */
    fun translated(key: String, vararg resolvers: TagResolver): Component

    /**
     * Creates a [Component] of the specified language key.<br></br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolver The custom tag resolver to use.
     * @return The component set for the key; empty component if not available.
     */
    fun translated(key: String, resolver: TagResolver): Component

    /**
     * Creates a ClickEvent, that executes code when clicked.<br></br>
     *
     *
     * It will internally link a command with an id to the code to execute.<br></br>
     * That internal command can only be executed by the player, who received the message.
     *
     *
     * @param player The player the event belongs to.
     * @param discard If it should be discarded after clicked. (Any action is removed, when the player disconnects!)
     * @param action The action to execute on click.
     * @return The ClickEvent with the generated command.
     */
    fun executable(player: Player, discard: Boolean, action: ClickActionCallback): ClickEvent

}
