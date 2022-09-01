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

package me.wolfyscript.utilities.util.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.json.jackson.function.Deserialize;
import me.wolfyscript.utilities.util.json.jackson.function.Serialize;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JacksonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectWriter getObjectWriter(boolean prettyPrinting) {
        return objectMapper.writer(prettyPrinting ? new DefaultPrettyPrinter() : null);
    }

    public static void registerModule(Module module) {
        objectMapper.registerModule(module);
    }

    public static <T> void addSerializer(SimpleModule module, Class<T> type, @NotNull Serialize<T> serialize) {
        module.addSerializer(type, new StdSerializer<>(type) {
            @Override
            public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                serialize.serialize(t, jsonGenerator, serializerProvider);
            }
        });
    }

    public static <T> void addDeserializer(SimpleModule module, Class<T> type, @NotNull Deserialize<T> deserialize) {
        module.addDeserializer(type, new StdDeserializer<>(type) {
            @Override
            public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return deserialize.deserialize(jsonParser, deserializationContext);
            }
        });
    }

    public static <T> void addSerializerAndDeserializer(SimpleModule module, Class<T> t, @NotNull Serialize<T> serialize, @NotNull Deserialize<T> deserialize) {
        addSerializer(module, t, serialize);
        addDeserializer(module, t, deserialize);
    }

}
