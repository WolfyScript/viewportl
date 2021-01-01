package me.wolfyscript.utilities.util.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@JsonSerialize(using = PlayerStore.Serializer.class)
@JsonDeserialize(using = PlayerStore.Deserializer.class)
public class PlayerStore {

    private final Map<NamespacedKey, CustomPlayerData> data;

    /**
     * Only used for Json deserialization purposes!
     */
    public PlayerStore() {
        this.data = new HashMap<>();
        for (Map.Entry<NamespacedKey, CustomPlayerData.Provider<?>> entry : CustomPlayerData.providers.entrySet()) {
            data.put(entry.getKey(), entry.getValue().createData());
        }
    }

    static PlayerStore load(UUID uuid) {
        File file = new File(PlayerUtils.STORE_FOLDER + File.separator + uuid.toString() + ".store");
        PlayerStore data = new PlayerStore();
        if (file.exists()) {
            try {
                GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                data = JacksonUtil.getObjectMapper().readValue(gzip, PlayerStore.class);
                gzip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public <D extends CustomPlayerData> D getData(NamespacedKey dataKey, Class<D> dataType) {
        return dataType.cast(data.get(dataKey));
    }

    void save(UUID uuid) {
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(PlayerUtils.STORE_FOLDER + File.separator + uuid.toString() + ".store"));
            JacksonUtil.getObjectWriter(false).writeValue(gzip, this);
            gzip.flush();
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PlayerStore{" +
                "data=" + data +
                '}';
    }

    static class Serializer extends JsonSerializer<PlayerStore> {

        @Override
        public void serialize(PlayerStore playerStore, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectFieldStart("data");
            {
                for (Map.Entry<me.wolfyscript.utilities.util.NamespacedKey, CustomPlayerData> value : playerStore.data.entrySet()) {
                    gen.writeObjectFieldStart(value.getKey().toString());
                    value.getValue().writeToJson(gen, serializerProvider);
                    gen.writeEndObject();
                }
            }
            gen.writeEndObject();
            gen.writeEndObject();
        }
    }

    static class Deserializer extends JsonDeserializer<PlayerStore> {

        @Override
        public PlayerStore deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.readValueAsTree();
            PlayerStore store = new PlayerStore();
            node.path("data").fields().forEachRemaining(entry -> {
                NamespacedKey key = NamespacedKey.getByString(entry.getKey());
                CustomPlayerData.Provider<?> provider = CustomPlayerData.providers.get(key);
                if (provider != null) {
                    store.data.put(key, provider.loadData(entry.getValue(), deserializationContext));
                }
            });
            return store;
        }
    }
}
