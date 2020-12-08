package me.wolfyscript.utilities.api.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageChannelHandler implements PluginMessageListener {

    private final Plugin plugin;
    private final NamespacedKey channelTag;
    private final Map<Integer, MessageHandler<?>> indexedMessages = new HashMap();
    private final Map<Class<?>, MessageHandler<?>> types = new HashMap<>();

    public MessageChannelHandler(Plugin plugin, NamespacedKey channelTag) {
        this.plugin = plugin;
        this.channelTag = channelTag;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelTag.toString());
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channelTag.toString(), this);
    }

    public <MSG> MessageHandler<MSG> registerMessage(int id, Class<MSG> messageType, BiConsumer<MSG, ByteArrayDataOutput> encoder, Function<ByteArrayDataInput, MSG> decoder, BiConsumer<MSG, Player> process) {
        return new MessageHandler<>(id, messageType, this, encoder, decoder, process);
    }

    public <MSG> void sendTo(Player player, MSG message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        encode(message, out);
        player.sendPluginMessage(plugin, channelTag.toString(), out.toByteArray());
        System.out.println("Send Plugin Message " + message.getClass().getSimpleName() + " to client of player " + player.getUniqueId().toString() + " !");
    }

    private <MSG> void encode(MSG message, ByteArrayDataOutput out) {
        MessageHandler<MSG> messageHandler = (MessageHandler<MSG>) types.get(message.getClass());
        if (messageHandler != null) {
            out.writeByte(messageHandler.getId() & 0xff);
            messageHandler.toBytes(out, message);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        //System.out.println("    Got message from channel: "+channel);
        if (!channel.equals(channelTag.toString())) {
            return;
        }
        //TODO: Receive message from FORGE Client
        /*
        ByteBuf byteBuf = Unpooled.wrappedBuffer(message);
        int index = byteBuf.readUnsignedByte();
        System.out.println("ID: "+index);

         */

        /*
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        MessageHandler<?> messageHandler = indexedMessages.get(index);
        if(messageHandler != null){
            System.out.println("    - "+messageHandler.getId());
            messageHandler.fromBytes(in, player);
        }

         */
    }

    class MessageHandler<MSG> {

        private final Optional<BiConsumer<MSG, ByteArrayDataOutput>> encoder;
        private final Optional<Function<ByteArrayDataInput, MSG>> decoder;
        private final BiConsumer<MSG, Player> process;
        private final Class<MSG> messageType;
        private final int id;

        public MessageHandler(int id, Class<MSG> messageType, MessageChannelHandler messageChannelHandler, BiConsumer<MSG, ByteArrayDataOutput> encoder, Function<ByteArrayDataInput, MSG> decoder, BiConsumer<MSG, Player> process) {
            this.messageType = messageType;
            this.decoder = Optional.ofNullable(decoder);
            this.encoder = Optional.ofNullable(encoder);
            this.process = process;
            this.id = id;
            messageChannelHandler.indexedMessages.put(id, this);
            messageChannelHandler.types.put(messageType, this);
        }

        private void toBytes(ByteArrayDataOutput out, MSG message) {
            encoder.ifPresent(encoder -> encoder.accept(message, out));
        }

        private void fromBytes(ByteArrayDataInput in, Player player) {
            decoder.map(d -> d.apply(in)).ifPresent(msg -> process.accept(msg, player));
        }

        public int getId() {
            return id;
        }
    }


}
