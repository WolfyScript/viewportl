package me.wolfyscript.utilities.util.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;

import java.io.IOException;
import java.util.UUID;

@JsonSerialize(using = BlockCustomItemStore.Serializer.class)
@JsonDeserialize(using = BlockCustomItemStore.Deserializer.class)
public class BlockCustomItemStore {

    private final NamespacedKey customItemKey;
    private UUID particleUUID;

    public BlockCustomItemStore(CustomItem customItem, UUID particleUUID) {
        this.customItemKey = customItem.getNamespacedKey();
        this.particleUUID = particleUUID;
    }

    public BlockCustomItemStore(NamespacedKey customItemKey, UUID particleUUID) {
        this.customItemKey = customItemKey;
        this.particleUUID = particleUUID;
    }

    public NamespacedKey getCustomItemKey() {
        return customItemKey;
    }

    public CustomItem getCustomItem() {
        return Registry.CUSTOM_ITEMS.get(customItemKey);
    }

    public UUID getParticleUUID() {
        return particleUUID;
    }

    public void setParticleUUID(UUID particleUUID) {
        this.particleUUID = particleUUID;
    }


    static class Serializer extends StdSerializer<BlockCustomItemStore> {

        public Serializer() {
            this(BlockCustomItemStore.class);
        }

        protected Serializer(Class<BlockCustomItemStore> t) {
            super(t);
        }

        @Override
        public void serialize(BlockCustomItemStore blockStore, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("key", blockStore.getCustomItemKey().toString());
            gen.writeEndObject();
        }

    }

    static class Deserializer extends StdDeserializer<BlockCustomItemStore> {

        public Deserializer() {
            this(BlockCustomItemStore.class);
        }

        protected Deserializer(Class<BlockCustomItemStore> t) {
            super(t);
        }

        @Override
        public BlockCustomItemStore deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.has("key")) {
                var customItemKey = NamespacedKey.of(node.path("key").asText());
                if (customItemKey != null) {
                    return new BlockCustomItemStore(customItemKey, null);
                }
            }
            return null;
        }
    }
}
