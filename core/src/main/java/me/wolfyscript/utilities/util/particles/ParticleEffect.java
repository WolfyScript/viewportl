package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
Contains the location, offset, ParticleEffects, etc.
 */
@JsonSerialize(using = ParticleEffect.Serializer.class)
@JsonDeserialize(using = ParticleEffect.Deserializer.class)
public class ParticleEffect {

    private NamespacedKey namespacedKey;
    private NamespacedKey superParticle;
    private org.bukkit.Particle particle;

    private String name;
    private List<String> description;
    private Material icon;
    private Class<?> dataClass;
    private Object data;
    private Vector relative, offset;
    private Integer count;
    private Double speed;
    private Animator animator;

    public ParticleEffect(ParticleEffect particleEffect) {
        this.particle = particleEffect.getParticle();
        this.superParticle = getSuperParticle();
        this.name = particleEffect.getName();
        this.description = particleEffect.getDescription();
        this.icon = particleEffect.getIcon();

        this.dataClass = particleEffect.dataClass;
        this.data = particleEffect.data;
        this.relative = particleEffect.getRelative();
        this.offset = particleEffect.getOffset();
        this.count = particleEffect.getCount();
        this.speed = particleEffect.getSpeed();
        this.animator = particleEffect.animator;
    }

    public ParticleEffect() {
    }

    public ParticleEffect(org.bukkit.Particle particle) {
        this(particle, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, Object data) {
        this(particle, 1, 1.0, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, int count) {
        this(particle, count, 1.0, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, int count, Object data) {
        this(particle, count, 1.0, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, int count, double speed) {
        this(particle, count, speed, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, int count, double speed, Object data) {
        this(particle, new Vector(0, 0, 0), count, speed, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count) {
        this(particle, relative, count, 1.0, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, double speed) {
        this(particle, relative, count, 1.0, speed);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, Object data) {
        this(particle, relative, count, 1.0, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, double speed, Object data) {
        this(particle, relative, count, new Vector(0, 0, 0), speed, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, Vector offset) {
        this(particle, relative, count, offset, 1.0, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, Vector offset, double speed) {
        this(particle, relative, count, offset, speed, null);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, Vector offset, Object data) {
        this(particle, relative, count, offset, 1.0, data);
    }

    public ParticleEffect(org.bukkit.Particle particle, Vector relative, int count, Vector offset, double speed, Object data) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
        this.data = data;
        this.icon = Material.FIREWORK_STAR;
        this.relative = relative;
        this.offset = offset;
        this.count = count;
        this.speed = speed;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public NamespacedKey getSuperParticle() {
        return superParticle;
    }

    public void setSuperParticle(NamespacedKey superParticle) {
        this.superParticle = superParticle;
    }

    public boolean hasSuperParticle() {
        return superParticle != null;
    }

    public org.bukkit.Particle getParticle() {
        return particle;
    }

    public void setParticle(org.bukkit.Particle particle) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public Vector getRelative() {
        return relative;
    }

    public void setRelative(Vector relative) {
        this.relative = relative;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "namespacedKey=" + namespacedKey +
                ", superParticle=" + superParticle +
                ", particle=" + particle +
                ", icon=" + icon +
                ", dataClass=" + dataClass +
                ", data=" + data +
                ", relative=" + relative +
                ", offset=" + offset +
                ", count=" + count +
                ", extra=" + speed +
                ", name='" + name + '\'' +
                ", description=" + description +
                '}';
    }

    public void onLocation(Location location) {
        animator.onLocation(new Animator.Data(this), location);
    }

    public void onBlock(@NotNull Block block) {
        animator.onBlock(new Animator.Data(this), block);
    }

    public void onPlayer(Player player, EquipmentSlot slot) {
        animator.onPlayer(new Animator.Data(this), player, slot);
    }

    public void onEntity(Entity entity) {
        animator.onEntity(new Animator.Data(this), entity);
    }

    static class Serializer extends StdSerializer<ParticleEffect> {

        public Serializer() {
            this(ParticleEffect.class);
        }

        protected Serializer(Class<ParticleEffect> t) {
            super(t);
        }

        private static boolean checkValue(Object particleValue, Object supParticleValue) {
            return particleValue != null && !particleValue.equals(supParticleValue);
        }

        @Override
        public void serialize(ParticleEffect particleEffect, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ParticleEffect supParticleEffect = Registry.PARTICLE_EFFECTS.get(particleEffect.getSuperParticle());
            boolean hasSup = supParticleEffect != null;
            gen.writeStartObject();
            if (supParticleEffect != null) {
                gen.writeStringField("particle", particleEffect.getSuperParticle().toString());
            } else {
                gen.writeStringField("particle", "minecraft:" + particleEffect.getParticle().toString().toLowerCase(Locale.ROOT));
            }

            if (checkValue(particleEffect.getIcon(), hasSup ? supParticleEffect.getIcon() : null)) {
                gen.writeStringField("icon", particleEffect.getIcon().toString());
            }
            if (checkValue(particleEffect.getName(), hasSup ? supParticleEffect.getName() : null)) {
                gen.writeStringField("name", particleEffect.getName());
            }
            if (checkValue(particleEffect.getDescription(), hasSup ? supParticleEffect.getDescription() : null)) {
                gen.writeArrayFieldStart("description");
                List<String> description = particleEffect.getDescription();
                if (description != null) {
                    for (String line : description) {
                        gen.writeString(line);
                    }
                }
                gen.writeEndArray();
            }
            if (checkValue(particleEffect.getCount(), hasSup ? supParticleEffect.getCount() : null)) {
                gen.writeNumberField("count", particleEffect.getCount());
            }
            if (checkValue(particleEffect.getSpeed(), hasSup ? supParticleEffect.getSpeed() : null)) {
                gen.writeNumberField("speed", particleEffect.getSpeed());
            }
            if (checkValue(particleEffect.getData(), hasSup ? supParticleEffect.getData() : null)) {
                gen.writeObjectField("data", particleEffect.getData());
            }
            if (checkValue(particleEffect.getRelative(), hasSup ? supParticleEffect.getRelative() : null)) {
                gen.writeObjectField("relative", particleEffect.getRelative());
            }
            if (checkValue(particleEffect.getOffset(), hasSup ? supParticleEffect.getOffset() : null)) {
                gen.writeObjectField("offset", particleEffect.getOffset());
            }
            if (checkValue(particleEffect.getAnimator(), hasSup ? supParticleEffect.getAnimator() : null)) {
                gen.writeObjectField("animator", particleEffect.getAnimator());
            }
            //particleObject.add("data", jsonSerializationContext.serialize(particle.getData(), particle.getDataClass()));
            gen.writeEndObject();
        }

    }

    static class Deserializer extends StdDeserializer<ParticleEffect> {

        public Deserializer() {
            this(ParticleEffect.class);
        }

        protected Deserializer(Class<ParticleEffect> t) {
            super(t);
        }

        @Override
        public ParticleEffect deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                final ParticleEffect resultParticleEffect;
                if (node.has("particle")) {
                    String particle = node.path("particle").asText();
                    NamespacedKey namespacedKey = particle.contains(":") ? NamespacedKey.getByString(particle) : new NamespacedKey("wolfyutilities", particle);
                    if (namespacedKey.getNamespace().equalsIgnoreCase("minecraft")) {
                        resultParticleEffect = new ParticleEffect();
                        org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(namespacedKey.getKey().toUpperCase(Locale.ROOT));
                        resultParticleEffect.setParticle(particleType);
                    } else {
                        ParticleEffect particleEffect1 = Registry.PARTICLE_EFFECTS.get(namespacedKey);
                        resultParticleEffect = new ParticleEffect(particleEffect1);
                        resultParticleEffect.setSuperParticle(namespacedKey);
                    }
                } else {
                    resultParticleEffect = new ParticleEffect();
                }
                if (node.has("icon")) {
                    resultParticleEffect.setIcon(Material.matchMaterial(node.get("icon").asText()));
                }
                if (node.has("name")) {
                    resultParticleEffect.setName(ChatColor.convert(node.get("name").asText()));
                }
                if (node.has("description")) {
                    List<String> description = new ArrayList<>();
                    node.get("description").elements().forEachRemaining(line -> description.add(ChatColor.convert(line.asText())));
                    resultParticleEffect.setDescription(description);
                }
                if (node.has("count")) {
                    resultParticleEffect.setCount(node.get("count").asInt());
                }
                if (node.has("speed")) {
                    resultParticleEffect.setSpeed(node.get("speed").asDouble());
                }
                if (node.has("data")) {
                    if (resultParticleEffect.getParticle().equals(org.bukkit.Particle.REDSTONE)) {
                        resultParticleEffect.setData(JacksonUtil.getObjectMapper().convertValue(node.get("data"), org.bukkit.Particle.DustOptions.class));
                    } else {
                        resultParticleEffect.setData(JacksonUtil.getObjectMapper().convertValue(node.get("data"), resultParticleEffect.getDataClass()));
                    }
                }
                if (node.has("relative")) {
                    resultParticleEffect.setRelative(JacksonUtil.getObjectMapper().convertValue(node.get("relative"), Vector.class));
                }
                if (node.has("offset")) {
                    resultParticleEffect.setOffset(JacksonUtil.getObjectMapper().convertValue(node.get("offset"), Vector.class));
                }
                if (node.has("animator")) {
                    resultParticleEffect.setAnimator(JacksonUtil.getObjectMapper().convertValue(node.path("animator"), Animator.class));
                }
                return resultParticleEffect;
            }
            return null;
        }
    }
}
