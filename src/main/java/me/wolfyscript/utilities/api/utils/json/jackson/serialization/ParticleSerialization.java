package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParticleSerialization {

    public static void create(SimpleModule module) {
        module.addSerializer(Particle.class, new Serializer());
        module.addDeserializer(Particle.class, new Deserializer());
    }

    private static <T> T getValue(T particleValue, T supParticleValue) {
        if (particleValue != null && !particleValue.equals(supParticleValue)){
            return particleValue;
        }
        return null;
    }

    public static class Serializer extends StdSerializer<Particle> {

        public Serializer() {
            this(Particle.class);
        }

        protected Serializer(Class<Particle> t) {
            super(t);
        }

        @Override
        public void serialize(Particle particle, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("particle", particle.hasSuperParticle() ? particle.getSuperParticle().getNamespacedKey().toString() : "minecraft:" + particle.getParticle().name().toLowerCase(Locale.ROOT));
            if (particle.hasSuperParticle()) {
                Particle supParticle = particle.getSuperParticle();
                gen.writeObjectField("icon", getValue(particle.getIcon(), supParticle.getIcon()));
                gen.writeObjectField("name", getValue(particle.getName(), supParticle.getName()));
            }
            if (particle.hasDescription()) {
                gen.writeArrayFieldStart("description");
                if (particle.getDescription() != null) {
                    for (String line : particle.getDescription()) {
                        gen.writeString(line);
                    }
                }
                gen.writeEndArray();
            }
            if (particle.hasCount()) {
                gen.writeNumberField("count", particle.getCount());
            }
            if (particle.hasExtra()) {
                gen.writeNumberField("extra", particle.getExtra());
            }
            if (particle.hasData()) {
                gen.writeObjectField("data", particle.getData());
                //particleObject.add("data", jsonSerializationContext.serialize(particle.getData(), particle.getDataClass()));
            }
            gen.writeObjectField("relative", particle.getRelative());
            gen.writeObjectField("offset", particle.getOffset());
            if (particle.hasScripts()) {
                gen.writeArrayFieldStart("scripts");
                for (String s : particle.getScripts()) {
                    gen.writeString(s);
                }
                gen.writeEndArray();
            }
        }
    }

    public static class Deserializer extends StdDeserializer<Particle> {

        public Deserializer() {
            this(Particle.class);
        }

        protected Deserializer(Class<Particle> t) {
            super(t);
        }

        @Override
        public Particle deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
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
                    }
                    resultParticle.setNamespacedKey(namespacedKey);
                } else {
                    resultParticle = new Particle();
                }
                if (node.has("icon")) {
                    resultParticle.setIcon(Material.matchMaterial(node.get("icon").asText()));
                }
                if (node.has("name")) {
                    resultParticle.setName(WolfyUtilities.translateColorCodes(node.get("name").asText()));
                }
                if (node.has("description")) {
                    List<String> description = new ArrayList<>();
                    node.get("description").elements().forEachRemaining(line -> description.add(WolfyUtilities.translateColorCodes(line.asText())));
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
                    resultParticle.setRelative(JacksonUtil.getObjectMapper().convertValue(node.get("offset"), Vector.class));
                }
                if (node.has("scripts")) {
                    node.get("scripts").elements().forEachRemaining(script -> resultParticle.addScript(script.asText()));
                }
                return resultParticle;
            }
            return null;
        }
    }
}
