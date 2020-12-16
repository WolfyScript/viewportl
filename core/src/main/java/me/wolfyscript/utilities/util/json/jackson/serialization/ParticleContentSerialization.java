package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.inventory.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.particles.ParticleEffect;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.util.Locale;
import java.util.Map;

public class ParticleContentSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, ParticleContent.class, (particleContent, gen, serializerProvider) -> {
            gen.writeStartObject();
            for (Map.Entry<ParticleEffect.Action, NamespacedKey> entry : particleContent.entrySet()) {
                gen.writeObjectFieldStart(entry.getKey().name().toLowerCase(Locale.ROOT));
                gen.writeStringField("effect", entry.getValue().toString());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                ParticleContent particleContent = new ParticleContent();
                for (ParticleEffect.Action action : ParticleEffect.Action.values()) {
                    String id = action.name().toLowerCase(Locale.ROOT);
                    JsonNode particle = node.get(id);
                    if (particle != null && !particle.isNull() && !particle.isMissingNode() && particle.isObject()) {
                        if (particle.has("effect")) {
                            String effect = particle.get("effect").asText();
                            NamespacedKey namespacedKey;
                            if(effect.split(":").length > 1){
                                namespacedKey = new NamespacedKey(effect.split(":")[0], effect.split(":")[1]);
                            } else {
                                namespacedKey = new NamespacedKey("wolfyutilities", effect);
                            }
                            particleContent.addParticleEffect(action, namespacedKey);
                        }
                    }
                }
                return particleContent;
            }
            return new ParticleContent();
        });
    }
}
