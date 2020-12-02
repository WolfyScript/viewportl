package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectSerialization {

    private static final String AMPLIFIER = "amplifier";
    private static final String DURATION = "duration";
    private static final String TYPE = "effect";
    private static final String AMBIENT = "ambient";
    private static final String PARTICLES = "has-particles";
    private static final String ICON = "has-icon";

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, PotionEffect.class, (potionEffect, gen, serializerProvider) -> {
            gen.writeStartObject();
            gen.writeNumberField(AMPLIFIER, potionEffect.getAmplifier());
            gen.writeNumberField(DURATION, potionEffect.getDuration());
            gen.writeObjectField(TYPE, potionEffect.getType());
            gen.writeBooleanField(AMBIENT, potionEffect.isAmbient());
            gen.writeBooleanField(PARTICLES, potionEffect.hasParticles());
            gen.writeBooleanField(ICON, potionEffect.hasIcon());
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (!node.has(TYPE)) return null;
            PotionEffectType type = JacksonUtil.getObjectMapper().convertValue(node.path(TYPE), PotionEffectType.class);
            boolean particles = node.path(PARTICLES).asBoolean(true);
            boolean icon = node.path(ICON).asBoolean(particles);
            return type == null ? null : new PotionEffect(type, node.path(DURATION).asInt(), node.path(AMPLIFIER).asInt(), node.path(AMBIENT).asBoolean(false), particles, icon);
        });
    }

}
