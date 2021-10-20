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

package me.wolfyscript.utilities.util.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import me.wolfyscript.utilities.api.WolfyUtilities;
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
        var file = new File(PlayerUtils.STORE_FOLDER + File.separator + uuid.toString() + ".store");
        var data = new PlayerStore();
        if (file.exists()) {
            try (var gzip = new GZIPInputStream(new FileInputStream(file))) {
                data = JacksonUtil.getObjectMapper().readValue(gzip, PlayerStore.class);
            } catch (MismatchedInputException ex) {
                WolfyUtilities.getWUCore().getConsole().severe("Error loading player store for " + uuid+" -> Reset store!");
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
        try (var gzip = new GZIPOutputStream(new FileOutputStream(PlayerUtils.STORE_FOLDER + File.separator + uuid.toString() + ".store"))) {
            JacksonUtil.getObjectWriter(false).writeValue(gzip, this);
            gzip.flush();
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
            var store = new PlayerStore();
            node.path("data").fields().forEachRemaining(entry -> {
                var key = NamespacedKey.of(entry.getKey());
                CustomPlayerData.Provider<?> provider = CustomPlayerData.providers.get(key);
                if (provider != null) {
                    store.data.put(key, provider.loadData(entry.getValue(), deserializationContext));
                }
            });
            return store;
        }
    }
}
