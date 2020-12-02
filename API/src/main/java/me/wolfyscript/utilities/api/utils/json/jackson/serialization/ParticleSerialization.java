package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.chat.ChatColor;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParticleSerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, Particle.class, (particle, gen, serializerProvider) -> {
            Particle supParticle = Particles.getParticle(particle.getSuperParticle());
            boolean hasSup = supParticle != null;
            gen.writeStartObject();
            if (supParticle != null) {
                gen.writeStringField("particle", particle.getSuperParticle().toString());
            } else {
                gen.writeStringField("particle", "minecraft:" + particle.getParticle().toString().toLowerCase(Locale.ROOT));
            }

            if (checkValue(particle.getIcon(), hasSup ? supParticle.getIcon() : null)) {
                gen.writeStringField("icon", particle.getIcon().toString());
            }
            if (checkValue(particle.getName(), hasSup ? supParticle.getName() : null)) {
                gen.writeStringField("name", particle.getName());
            }
            if (checkValue(particle.getDescription(), hasSup ? supParticle.getDescription() : null)) {
                gen.writeArrayFieldStart("description");
                List<String> description = particle.getDescription();
                if (description != null) {
                    for (String line : description) {
                        gen.writeString(line);
                    }
                }
                gen.writeEndArray();
            }
            if (checkValue(particle.getCount(), hasSup ? supParticle.getCount() : null)) {
                gen.writeNumberField("count", particle.getCount());
            }
            if (checkValue(particle.getExtra(), hasSup ? supParticle.getExtra() : null)) {
                gen.writeNumberField("extra", particle.getExtra());
            }
            if (checkValue(particle.getData(), hasSup ? supParticle.getData() : null)) {
                gen.writeObjectField("data", particle.getData());
            }
            if (checkValue(particle.getRelative(), hasSup ? supParticle.getRelative() : null)) {
                gen.writeObjectField("relative", particle.getRelative());
            }
            if (checkValue(particle.getOffset(), hasSup ? supParticle.getOffset() : null)) {
                gen.writeObjectField("offset", particle.getOffset());
            }
            if (checkValue(particle.getScripts(), hasSup ? supParticle.getScripts() : null)) {
                gen.writeArrayFieldStart("scripts");
                List<String> scripts = particle.getScripts();
                if (scripts != null) {
                    for (String line : scripts) {
                        gen.writeString(line);
                    }
                }
                gen.writeEndArray();
            }
            //particleObject.add("data", jsonSerializationContext.serialize(particle.getData(), particle.getDataClass()));
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                final Particle resultParticle;
                if (node.has("particle")) {
                    String particle = node.get("particle").asText();
                    NamespacedKey namespacedKey = particle.contains(":") ? NamespacedKey.getByString(particle) : new NamespacedKey("wolfyutilities", particle);
                    if (namespacedKey.getNamespace().equalsIgnoreCase("minecraft")) {
                        resultParticle = new Particle();
                        org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(namespacedKey.getKey().toUpperCase(Locale.ROOT));
                        resultParticle.setParticle(particleType);
                    } else {
                        Particle particle1 = Particles.getParticles().get(namespacedKey);
                        resultParticle = new Particle(particle1);
                        resultParticle.setSuperParticle(namespacedKey);
                    }
                } else {
                    resultParticle = new Particle();
                }
                if (node.has("icon")) {
                    resultParticle.setIcon(Material.matchMaterial(node.get("icon").asText()));
                }
                if (node.has("name")) {
                    resultParticle.setName(ChatColor.convert(node.get("name").asText()));
                }
                if (node.has("description")) {
                    List<String> description = new ArrayList<>();
                    node.get("description").elements().forEachRemaining(line -> description.add(ChatColor.convert(line.asText())));
                    resultParticle.setDescription(description);
                }
                if (node.has("count")) {
                    resultParticle.setCount(node.get("count").asInt());
                }
                if (node.has("extra")) {
                    resultParticle.setExtra(node.get("extra").asDouble());
                }
                if (node.has("data")) {
                    if (resultParticle.getParticle().equals(org.bukkit.Particle.REDSTONE)) {
                        resultParticle.setData(JacksonUtil.getObjectMapper().convertValue(node.get("data"), org.bukkit.Particle.DustOptions.class));
                    } else {
                        resultParticle.setData(JacksonUtil.getObjectMapper().convertValue(node.get("data"), resultParticle.getDataClass()));
                    }
                }
                if (node.has("relative")) {
                    resultParticle.setRelative(JacksonUtil.getObjectMapper().convertValue(node.get("relative"), Vector.class));
                }
                if (node.has("offset")) {
                    resultParticle.setOffset(JacksonUtil.getObjectMapper().convertValue(node.get("offset"), Vector.class));
                }
                if (node.has("scripts")) {
                    node.get("scripts").elements().forEachRemaining(script -> resultParticle.addScript(script.asText()));
                }
                return resultParticle;
            }
            return null;
        });
    }

    private static boolean checkValue(Object particleValue, Object supParticleValue) {
        return particleValue != null && !particleValue.equals(supParticleValue);
    }
}
