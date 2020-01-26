package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParticleEffectSerialization implements JsonSerializer<ParticleEffect>, JsonDeserializer<ParticleEffect> {

    @Override
    public ParticleEffect deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            JsonObject object = (JsonObject) jsonElement;

            ParticleEffect resultParticleEffect = new ParticleEffect();

            Material material;
            if (object.has("icon")) {
                material = Material.matchMaterial(object.get("icon").getAsString());
            } else {
                material = Material.FIREWORK_ROCKET;
            }
            resultParticleEffect.setIcon(material);
            resultParticleEffect.setName(object.get("name").getAsString());

            List<String> description = new ArrayList<>();
            JsonArray lore = object.getAsJsonArray("description");
            for (JsonElement line : lore) {
                description.add(WolfyUtilities.translateColorCodes(line.getAsString()));
            }
            resultParticleEffect.setDescription(description);
            if (object.has("particles")) {
                JsonArray jsonArray = object.getAsJsonArray("particles");
                for (JsonElement element : jsonArray) {
                    if (element instanceof JsonObject) {
                        Particle particle = jsonDeserializationContext.deserialize(element, Particle.class);
                        if (particle != null) {
                            resultParticleEffect.addParticle(particle);
                        }
                    }
                }
            }
            if (object.has("count")) {
                resultParticleEffect.setCount(object.getAsJsonPrimitive("count").getAsInt());
            }
            if (object.has("cooldown")) {
                resultParticleEffect.setCooldown(object.getAsJsonPrimitive("cooldown").getAsInt());
            }
            if (object.has("duration")) {
                resultParticleEffect.setDuration(object.getAsJsonPrimitive("duration").getAsInt());
            }
            return resultParticleEffect;
        }
        return null;
    }

    @Override
    public JsonElement serialize(ParticleEffect particleEffect, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("count", particleEffect.getCount());
        jsonObject.addProperty("duration", particleEffect.getDuration());
        jsonObject.addProperty("cooldown", particleEffect.getCooldown());
        jsonObject.addProperty("icon", particleEffect.getIcon().getKey().toString());
        jsonObject.addProperty("name", particleEffect.getName());
        JsonArray description = new JsonArray();
        for (String line : particleEffect.getDescription()) {
            description.add(line);
        }
        jsonObject.add("description", description);

        JsonArray particles = new JsonArray();
        for (Particle particle : particleEffect.getParticles()) {
            particles.add(jsonSerializationContext.serialize(particle, Particle.class));
        }
        jsonObject.add("particles", particles);
        return jsonObject;
    }
}
