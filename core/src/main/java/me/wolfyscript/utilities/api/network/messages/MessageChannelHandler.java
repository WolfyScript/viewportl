package me.wolfyscript.utilities.api.network.messages;

import io.netty.buffer.Unpooled;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageChannelHandler implements PluginMessageListener {

    final Map<Integer, MessageHandler<?>> indexedMessages = new HashMap<>();
    final Map<Class<?>, MessageHandler<?>> types = new HashMap<>();
    private final WolfyUtilities api;
    private final Plugin plugin;
    private final NamespacedKey channelTag;

    public MessageChannelHandler(WolfyUtilities api, NamespacedKey channelTag) {
        this.api = api;
        this.plugin = api.getPlugin();
        this.channelTag = channelTag;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelTag.toString());
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channelTag.toString(), this);
    }

    public <M> MessageHandler<M> registerMessage(int id, Class<M> messageType, BiConsumer<M, MCByteBuf> encoder, Function<MCByteBuf, M> decoder, BiConsumer<M, Player> process) {
        return new MessageHandler<>(id, messageType, this, encoder, decoder, process);
    }

    public <M> void sendTo(Player player, M message) {
        MCByteBuf out = api.getNmsUtil().getNetworkUtil().buffer();
        encode(message, out);
        player.sendPluginMessage(plugin, channelTag.toString(), out.array());
        api.getConsole().info("Send Plugin Message " + message.getClass().getSimpleName() + " to player " + player.getUniqueId() + " !");
    }

    private <M> void encode(M message, MCByteBuf out) {
        MessageHandler<M> messageHandler = (MessageHandler<M>) types.get(message.getClass());
        if (messageHandler != null) {
            out.writeVarInt(messageHandler.getId());
            messageHandler.toBytes(out, message);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(channelTag.toString())) {
            return;
        }
        MCByteBuf byteBuf = api.getNmsUtil().getNetworkUtil().buffer(Unpooled.wrappedBuffer(message));
        int index = byteBuf.readVarInt();
        MessageHandler<?> messageHandler = indexedMessages.get(index);
        if (messageHandler != null) {
            messageHandler.fromBytes(byteBuf, player);
        }

    }

}
