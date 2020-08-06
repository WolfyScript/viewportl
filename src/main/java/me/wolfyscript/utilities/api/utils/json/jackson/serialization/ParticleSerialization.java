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
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParticleSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(Particle.class, new Serializer());
        module.addDeserializer(Particle.class, new Deserializer());
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
            if (particle.hasIcon()) {
                gen.writeStringField("icon", particle.getIcon().getKey().toString());
            }
            if (particle.hasName()) {
                gen.writeStringField("name", particle.getName());
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
            if (particle.hasRelativeX() || particle.hasRelativeY() || particle.hasRelativeZ()) {
                gen.writeObjectFieldStart("relative");
                if (particle.hasRelativeX()) {
                    gen.writeNumberField("x", particle.getRelativeX());
                }
                if (particle.hasRelativeY()) {
                    gen.writeNumberField("y", particle.getRelativeY());
                }
                if (particle.hasRelativeZ()) {
                    gen.writeNumberField("z", particle.getRelativeZ());
                }
                gen.writeEndObject();
            }
            if (particle.hasOffsetX() || particle.hasOffsetY() || particle.hasOffsetZ()) {
                gen.writeObjectFieldStart("offset");
                if (particle.hasOffsetX()) {
                    gen.writeNumberField("x", particle.getOffsetX());
                }
                if (particle.hasOffsetY()) {
                    gen.writeNumberField("y", particle.getOffsetY());
                }
                if (particle.hasOffsetZ()) {
                    gen.writeNumberField("z", particle.getOffsetZ());
                }
            }
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
                    NamespacedKey namespacedKey;
                    if (particle.contains(":") && particle.split(":").length > 1) {
                        namespacedKey = new NamespacedKey(particle.split(":")[0].toLowerCase(Locale.ROOT).replace(" ", "_"), particle.split(":")[1].toLowerCase(Locale.ROOT).replace(" ", "_"));
                    } else {
                        namespacedKey = new NamespacedKey("wolfyutilities", particle);
                    }
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
                    JsonNode relative = node.get("relative");
                    resultParticle.setRelativeX(relative.get("x").asDouble());
                    resultParticle.setRelativeY(relative.get("y").asDouble());
                    resultParticle.setRelativeZ(relative.get("z").asDouble());
                }
                if (node.has("offset")) {
                    JsonNode relative = node.get("offset");
                    resultParticle.setOffsetX(relative.get("x").asDouble());
                    resultParticle.setOffsetY(relative.get("y").asDouble());
                    resultParticle.setOffsetZ(relative.get("z").asDouble());
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
