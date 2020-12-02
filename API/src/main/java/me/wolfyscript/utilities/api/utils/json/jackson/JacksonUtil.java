package me.wolfyscript.utilities.api.utils.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.utils.json.jackson.function.Deserialize;
import me.wolfyscript.utilities.api.utils.json.jackson.function.Serialize;
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

    public static <T> void addSerializer(SimpleModule module, Class<T> t, @NotNull Serialize<T> serialize) {
        module.addSerializer(t, new StdSerializer<T>(t) {
            @Override
            public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                serialize.serialize(t, jsonGenerator, serializerProvider);
            }
        });
    }

    public static <T> void addDeserializer(SimpleModule module, Class<T> t, @NotNull Deserialize<T> deserialize) {
        module.addDeserializer(t, new StdDeserializer<T>(t) {
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
