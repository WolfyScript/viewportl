package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.particles.Particle;
import me.wolfyscript.utilities.api.particles.ParticleEffect;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, ParticleEffect.class, (particleEffect, gen, serializerProvider) -> {
            gen.writeStartObject();
            gen.writeNumberField("count", particleEffect.getCount());
            gen.writeNumberField("duration", particleEffect.getDuration());
            gen.writeNumberField("cooldown", particleEffect.getCooldown());
            gen.writeStringField("icon", particleEffect.getIcon().getKey().toString());
            gen.writeStringField("name", particleEffect.getName());
            gen.writeArrayFieldStart("description");
            for (String line : particleEffect.getDescription()) {
                gen.writeString(line);
            }
            gen.writeEndArray();
            gen.writeArrayFieldStart("particles");
            for (Particle particle : particleEffect.getParticles()) {
                gen.writeObject(particle);
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                ParticleEffect resultParticleEffect = new ParticleEffect();
                Material material;
                if (node.has("icon")) {
                    material = Material.matchMaterial(node.get("icon").asText());
                } else {
                    material = Material.FIREWORK_ROCKET;
                }
                resultParticleEffect.setIcon(material);
                resultParticleEffect.setName(node.get("name").asText());
                List<String> description = new ArrayList<>();
                node.get("description").elements().forEachRemaining(value -> description.add(ChatColor.convert(value.asText())));
                resultParticleEffect.setDescription(description);
                if (node.has("particles")) {
                    node.get("particles").elements().forEachRemaining(jsonNode -> {
                        if(jsonNode.isObject()){
                            Particle particle = JacksonUtil.getObjectMapper().convertValue(jsonNode, Particle.class);
                            if (particle != null) {
                                resultParticleEffect.addParticle(particle);
                            }
                        }
                    });
                }
                if (node.has("count")) {
                    resultParticleEffect.setCount(node.get("count").asInt());
                }
                if (node.has("cooldown")) {
                    resultParticleEffect.setCooldown(node.get("cooldown").asInt());
                }
                if (node.has("duration")) {
                    resultParticleEffect.setDuration(node.get("duration").asInt());
                }
                return resultParticleEffect;
            }
            return null;
        });
    }
}
