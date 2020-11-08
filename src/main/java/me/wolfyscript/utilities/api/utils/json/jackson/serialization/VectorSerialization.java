package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.util.Vector;

import java.io.IOException;

public class VectorSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(Vector.class, new Serializer());
        module.addDeserializer(Vector.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<Vector> {

        public Serializer(){
            this(Vector.class);
        }

        protected Serializer(Class<Vector> t) {
            super(t);
        }

        @Override
        public void serialize(Vector value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartArray();
            gen.writeNumber(value.getX());
            gen.writeNumber(value.getY());
            gen.writeNumber(value.getZ());
            gen.writeEndArray();
        }
    }

    public static class Deserializer extends StdDeserializer<Vector> {

        public Deserializer(){
            this(Vector.class);
        }

        protected Deserializer(Class<Vector> t) {
            super(t);
        }

        @Override
        public Vector deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isArray()) {
                ArrayNode arrayNode = (ArrayNode) node;
                return new Vector(arrayNode.get(0).asDouble(0), arrayNode.get(1).asDouble(0), arrayNode.get(2).asDouble(0));
            }
            WUPlugin.getWolfyUtilities().sendConsoleWarning("Error Deserializing Vector! Invalid Vector object!");
            return null;
        }
    }
}
