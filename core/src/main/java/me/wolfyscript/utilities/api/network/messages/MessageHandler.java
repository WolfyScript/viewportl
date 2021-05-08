package me.wolfyscript.utilities.api.network.messages;

import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <M> Message type. Can be any type of class.
 */
public class MessageHandler<M> {

    private final Optional<BiConsumer<M, MCByteBuf>> encoder;
    private final Optional<Function<MCByteBuf, M>> decoder;
    private final BiConsumer<M, Player> process;
    private final Class<M> messageType;
    private final int id;

    public MessageHandler(int id, Class<M> messageType, MessageChannelHandler messageChannelHandler, BiConsumer<M, MCByteBuf> encoder, Function<MCByteBuf, M> decoder, BiConsumer<M, Player> process) {
        this.messageType = messageType;
        this.decoder = Optional.ofNullable(decoder);
        this.encoder = Optional.ofNullable(encoder);
        this.process = process;
        this.id = id;
        messageChannelHandler.indexedMessages.put(id, this);
        messageChannelHandler.types.put(messageType, this);
    }

    void toBytes(MCByteBuf out, M message) {
        encoder.ifPresent(encoder -> encoder.accept(message, out));
    }

    void fromBytes(MCByteBuf in, Player player) {
        decoder.map(d -> d.apply(in)).ifPresent(msg -> process.accept(msg, player));
    }

    public int getId() {
        return id;
    }
}
