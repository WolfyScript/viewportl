package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticleEffectSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(ParticleEffect.class, new Serializer());
        module.addDeserializer(ParticleEffect.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<ParticleEffect> {

        public Serializer(){
            this(ParticleEffect.class);
        }

        protected Serializer(Class<ParticleEffect> t) {
            super(t);
        }

        @Override
        public void serialize(ParticleEffect particleEffect, JsonGenerator gen, SerializerProvider provider) throws IOException {
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
        }
    }

    public static class Deserializer extends StdDeserializer<ParticleEffect> {

        public Deserializer(){
            this(ParticleEffect.class);
        }

        protected Deserializer(Class<ParticleEffect> t) {
            super(t);
        }

        @Override
        public ParticleEffect deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
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
                node.get("description").elements().forEachRemaining(value -> description.add(WolfyUtilities.translateColorCodes(value.asText())));
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
        }
    }



}
