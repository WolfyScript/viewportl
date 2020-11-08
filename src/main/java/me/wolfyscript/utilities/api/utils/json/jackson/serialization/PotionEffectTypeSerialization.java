package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectTypeSerialization {

    public static void create(SimpleModule module) {
        module.addSerializer(PotionEffectType.class, new Serializer());
        module.addDeserializer(PotionEffectType.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<PotionEffectType> {

        public Serializer() {
            this(PotionEffectType.class);
        }

        protected Serializer(Class<PotionEffectType> t) {
            super(t);
        }

        @Override
        public void serialize(PotionEffectType potionEffectType, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(potionEffectType.getName());
        }
    }

    public static class Deserializer extends StdDeserializer<PotionEffectType> {

        public Deserializer() {
            this(PotionEffectType.class);
        }

        protected Deserializer(Class<PotionEffectType> t) {
            super(t);
        }

        @Override
        public PotionEffectType deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            return PotionEffectType.getByName(node.asText());
        }
    }


}
