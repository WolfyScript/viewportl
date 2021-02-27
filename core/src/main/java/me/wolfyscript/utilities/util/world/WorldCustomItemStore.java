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
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonSerialize(using = WorldCustomItemStore.Serializer.class)
@JsonDeserialize(using = WorldCustomItemStore.Deserializer.class)
public class WorldCustomItemStore {

    private final HashMap<Location, BlockCustomItemStore> store = new HashMap<>();

    public WorldCustomItemStore() {
    }

    public void store(Location location, CustomItem customItem) {
        ParticleUtils.stopAnimation(getStoredEffect(location));
        if (customItem.getApiReference() instanceof WolfyUtilitiesRef || customItem.hasNamespacedKey()) {
            store.put(location, new BlockCustomItemStore(customItem, null));
            ParticleUtils.spawnAnimationOnBlock(customItem.getParticleContent().getParticleEffect(ParticleLocation.BLOCK), location.getBlock());
        }
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public void remove(Location location) {
        ParticleUtils.stopAnimation(getStoredEffect(location));
        store.remove(location);
    }

    public boolean isStored(Location location) {
        return store.containsKey(location);
    }

    @Nullable
    public BlockCustomItemStore get(Location location) {
        return store.get(location);
    }

    public CustomItem getCustomItem(Location location) {
        BlockCustomItemStore blockStore = get(location);
        if (blockStore != null) {
            return blockStore.getCustomItem();
        }
        return null;
    }

    /**
     * The current active particle effect on this Location.
     *
     * @param location The location to be checked.
     * @return The uuid of the currently active particle effect.
     */
    @Nullable
    public UUID getStoredEffect(@NotNull Location location) {
        return store.entrySet().stream().filter(entry -> entry.getKey().equals(location) && entry.getValue() != null && entry.getValue().getParticleUUID() != null).map(entry -> entry.getValue().getParticleUUID()).findFirst().orElse(null);
    }

    public boolean hasStoredEffect(Location location) {
        if (isStored(location)) {
            return getStoredEffect(location) != null;
        }
        return false;
    }

    void setStore(Location location, BlockCustomItemStore blockStore) {
        store.put(location, blockStore);
    }

    public void initiateMissingBlockEffects() {
        store.entrySet().stream().filter(entry -> !hasStoredEffect(entry.getKey())).forEach(entry -> {
            CustomItem customItem = entry.getValue().getCustomItem();
            if (customItem != null) {
                store.put(entry.getKey(), new BlockCustomItemStore(customItem, null));
                ParticleUtils.spawnAnimationOnBlock(customItem.getParticleContent().getParticleEffect(ParticleLocation.BLOCK), entry.getKey().getBlock());
            }
        });
    }


    static class Serializer extends StdSerializer<WorldCustomItemStore> {

        public Serializer() {
            this(WorldCustomItemStore.class);
        }

        protected Serializer(Class<WorldCustomItemStore> t) {
            super(t);
        }

        @Override
        public void serialize(WorldCustomItemStore customItem, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartArray();
            for (Map.Entry<Location, BlockCustomItemStore> entry : customItem.store.entrySet()) {
                gen.writeStartObject();
                gen.writeObjectField("loc", entry.getKey());
                gen.writeObjectField("store", entry.getValue());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }

    }

    static class Deserializer extends StdDeserializer<WorldCustomItemStore> {

        public Deserializer() {
            this(WorldCustomItemStore.class);
        }

        protected Deserializer(Class<WorldCustomItemStore> t) {
            super(t);
        }

        @Override
        public WorldCustomItemStore deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            WorldCustomItemStore worldStore = new WorldCustomItemStore();
            JsonNode node = p.readValueAsTree();
            node.elements().forEachRemaining(jsonNode -> {
                Location location = JacksonUtil.getObjectMapper().convertValue(jsonNode.path("loc"), Location.class);
                BlockCustomItemStore blockStore = JacksonUtil.getObjectMapper().convertValue(jsonNode.path("store"), BlockCustomItemStore.class);
                if (blockStore != null) {
                    worldStore.setStore(location, blockStore);
                }
            });
            return worldStore;
        }
    }
}
