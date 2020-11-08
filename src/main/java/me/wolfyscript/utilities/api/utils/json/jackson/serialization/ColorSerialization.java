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

import java.io.IOException;

public class ColorSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(Color.class, new Serializer());
        module.addDeserializer(Color.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<Color> {

        public Serializer(){
            this(Color.class);
        }

        protected Serializer(Class<Color> t) {
            super(t);
        }

        @Override
        public void serialize(Color value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("red", value.getRed());
            gen.writeNumberField("green", value.getGreen());
            gen.writeNumberField("blue", value.getBlue());
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<Color> {

        public Deserializer(){
            this(Color.class);
        }

        protected Deserializer(Class<Color> t) {
            super(t);
        }

        @Override
        public Color deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                int red = node.get("red").asInt();
                int green = node.get("green").asInt();
                int blue = node.get("blue").asInt();
                return Color.fromBGR(blue, green, red);
            }
            WUPlugin.getWolfyUtilities().sendConsoleWarning("Error Deserializing Color! Invalid Color object!");
            return null;
        }
    }
}
