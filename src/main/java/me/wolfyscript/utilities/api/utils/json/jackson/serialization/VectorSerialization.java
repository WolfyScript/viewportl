package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Color;
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
            gen.writeStartObject();
            gen.writeNumberField("x", value.getX());
            gen.writeNumberField("y", value.getY());
            gen.writeNumberField("z", value.getZ());
            gen.writeEndObject();
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
            if (node.isObject()) {
                return new Vector(node.get("x").asDouble(0), node.get("y").asDouble(0), node.get("z").asDouble(0));
            }
            WUPlugin.getWolfyUtilities().sendConsoleWarning("Error Deserializing Vector! Invalid Vector object!");
            return null;
        }
    }
}
