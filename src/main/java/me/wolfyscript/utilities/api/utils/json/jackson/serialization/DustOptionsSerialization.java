package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.io.IOException;

public class DustOptionsSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(Particle.DustOptions.class, new Serializer());
        module.addDeserializer(Particle.DustOptions.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<Particle.DustOptions> {

        public Serializer(){
            this(Particle.DustOptions.class);
        }

        protected Serializer(Class<Particle.DustOptions> t) {
            super(t);
        }

        @Override
        public void serialize(Particle.DustOptions dustOptions, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("size", dustOptions.getSize());
            gen.writeObjectField("color", dustOptions.getColor());
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<Particle.DustOptions> {

        public Deserializer(){
            this(Particle.DustOptions.class);
        }

        protected Deserializer(Class<Particle.DustOptions> t) {
            super(t);
        }

        @Override
        public Particle.DustOptions deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            p.setCodec(JacksonUtil.getObjectMapper());
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                float size = node.get("size").floatValue();
                Color color = ctxt.readValue(node.get("color").traverse(JacksonUtil.getObjectMapper()), Color.class);
                return new Particle.DustOptions(color, size);
            }
            Main.getMainUtil().sendConsoleWarning("Error Deserializing DustOptions! Invalid DustOptions object!");
            return null;
        }
    }
}
