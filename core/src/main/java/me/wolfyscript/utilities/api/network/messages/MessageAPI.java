package me.wolfyscript.utilities.api.network.messages;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MessageAPI {

    private final Map<NamespacedKey, Message> handlersByKey;

    private final WolfyUtilities wolfyUtils;

    public MessageAPI(WolfyUtilities wolfyUtils) {
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
