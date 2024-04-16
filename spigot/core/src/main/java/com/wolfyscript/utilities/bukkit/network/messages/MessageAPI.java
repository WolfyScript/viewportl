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

package com.wolfyscript.utilities.bukkit.network.messages;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MessageAPI {

    private final Map<NamespacedKey, Message> handlersByKey;

    private final WolfyUtilsBukkit wolfyUtils;

    public MessageAPI(WolfyUtilsBukkit wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.handlersByKey = new HashMap<>();
    }

    /**
     * Registers a specific channel used for plugin messages.<br>
     * A channel must be registered before it can be used, <strong>on the client and server</strong>!<br>
     * <br>
     * A decoder can be used to handle and decode incoming messages. If not required it can be set to null, or use {@link #register(NamespacedKey)} instead.
     * <br>
     * A message can be sent using the {@link #send(NamespacedKey, Player, MCByteBuf)}, or {@link #send(NamespacedKey, Player)}.
     *
     * @param channelKey The unique key of the channel.
     * @param decoder The decoder used to decode incoming messages.
     * @return This message api instance for chaining.
     */
    public MessageAPI register(NamespacedKey channelKey, @Nullable MessageConsumer decoder) {
        if (!handlersByKey.containsKey(channelKey)) {
            handlersByKey.put(channelKey, new Message(channelKey, wolfyUtils, decoder));
        } else {
            throw new IllegalArgumentException(String.format("The Channel %s already contains a associated Encoder and/or Decoder!", channelKey));
        }
        return this;
    }

    /**
     * Registers a specific channel used for plugin messages.<br>
     * A channel must be registered before it can be used, <strong>on the client and server</strong>!<br>
     * <br>
     * This method will only register an outgoing message and ignore any incoming messages.<br>
     * To handle incoming message use this {@link #register(NamespacedKey, MessageConsumer)} instead.<br>
     * <br>
     * A message can be sent using the {@link #send(NamespacedKey, Player, MCByteBuf)}, or {@link #send(NamespacedKey, Player)}.
     *
     * @param channelKey The unique key of the channel.
     * @return This message api instance for chaining.
     */
    public MessageAPI register(NamespacedKey channelKey) {
        return register(channelKey, null);
    }

    /**
     * Sends the data of the {@link MCByteBuf} via the specified channel to the client of the player.
     *
     * @param channelKey The key of the channel.
     * @param player The player to send the message to.
     * @param buf The buffer containing all the data to be sent.
     */
    public void send(NamespacedKey channelKey, Player player, MCByteBuf buf) {
        Message handler = handlersByKey.get(channelKey);
        if (handler != null) {
            handler.send(player, buf);
        }
    }

    /**
     * Sends an empty {@link MCByteBuf} via the specified channel to the client of the player.
     *
     * @param channelKey The key of the channel.
     * @param player The player to send the message to.
     */
    public void send(NamespacedKey channelKey, Player player) {
        send(channelKey, player, wolfyUtils.getNmsUtil().getNetworkUtil().buffer());
    }

}
