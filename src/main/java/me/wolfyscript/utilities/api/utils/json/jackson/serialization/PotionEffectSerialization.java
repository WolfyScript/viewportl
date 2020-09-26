package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectSerialization {

    private static final String AMPLIFIER = "amplifier";
    private static final String DURATION = "duration";
    private static final String TYPE = "effect";
    private static final String AMBIENT = "ambient";
    private static final String PARTICLES = "has-particles";
    private static final String ICON = "has-icon";

    public static void create(SimpleModule module) {
        module.addSerializer(PotionEffect.class, new Serializer());
        module.addDeserializer(PotionEffect.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<PotionEffect> {

        public Serializer() {
            this(PotionEffect.class);
        }

        protected Serializer(Class<PotionEffect> t) {
            super(t);
        }

        @Override
        public void serialize(PotionEffect particleEffect, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField(AMPLIFIER, particleEffect.getAmplifier());
            gen.writeNumberField(DURATION, particleEffect.getDuration());
            gen.writeObjectField(TYPE, particleEffect.getType());
            gen.writeBooleanField(AMBIENT, particleEffect.isAmbient());
            gen.writeBooleanField(PARTICLES, particleEffect.hasParticles());
            gen.writeBooleanField(ICON, particleEffect.hasIcon());
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<PotionEffect> {

        public Deserializer() {
            this(PotionEffect.class);
        }

        protected Deserializer(Class<PotionEffect> t) {
            super(t);
        }

        @Override
        public PotionEffect deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (!node.has(TYPE)) return null;
            PotionEffectType type = JacksonUtil.getObjectMapper().convertValue(node.path(TYPE), PotionEffectType.class);
            boolean particles = node.path(PARTICLES).asBoolean(true);
            boolean icon = node.path(ICON).asBoolean(particles);
            return type == null ? null : new PotionEffect(type, node.path(DURATION).asInt(), node.path(AMPLIFIER).asInt(), node.path(AMBIENT).asBoolean(false), particles, icon);
        }
    }


}
