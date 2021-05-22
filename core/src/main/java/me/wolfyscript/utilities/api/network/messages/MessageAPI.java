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

    public MessageAPI register(NamespacedKey channelKey, @Nullable MessageConsumer decoder) {
        if (!handlersByKey.containsKey(channelKey)) {
            handlersByKey.put(channelKey, new Message(channelKey, wolfyUtils, decoder));
        } else {
            throw new IllegalArgumentException(String.format("The Channel %s already contains a associated Encoder and/or Decoder!", channelKey));
        }
        return this;
    }

    public MessageAPI register(NamespacedKey channelKey) {
        return register(channelKey, null);
    }

    public void send(NamespacedKey channelKey, Player player, MCByteBuf buf) {
        Message handler = handlersByKey.get(channelKey);
        if (handler != null) {
            handler.send(player, buf);
        }
    }

    public void send(NamespacedKey channelKey, Player player) {
        send(channelKey, player, wolfyUtils.getNmsUtil().getNetworkUtil().buffer());
    }

}
