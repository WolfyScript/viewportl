package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.util.Vector;

public class VectorSerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, Vector.class, (value, gen, serializerProvider) -> {
            gen.writeStartArray();
            gen.writeNumber(value.getX());
            gen.writeNumber(value.getY());
            gen.writeNumber(value.getZ());
            gen.writeEndArray();
        }, (p, deserializationContext) -> {
            WolfyUtilities api = WolfyUtilities.getWUCore();
            JsonNode node = p.readValueAsTree();
            if (node.isArray()) {
                ArrayNode arrayNode = (ArrayNode) node;
                return new Vector(arrayNode.get(0).asDouble(0), arrayNode.get(1).asDouble(0), arrayNode.get(2).asDouble(0));
            }
            api.getConsole().warn("Error Deserializing Vector! Invalid Vector object!");
            return null;
        });
    }
}
