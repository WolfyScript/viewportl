package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.Particles;

import java.lang.reflect.Type;
import java.util.Locale;

public class ParticleEffectSerialization implements JsonSerializer<ParticleEffect>, JsonDeserializer<ParticleEffect> {

    @Override
    public ParticleEffect deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            JsonObject object = (JsonObject) jsonElement;
            ParticleEffect resultParticleEffect = new ParticleEffect();
            if (object.has("particles")) {
                JsonArray jsonArray = object.getAsJsonArray("particles");
                for(JsonElement element : jsonArray){
                    if(element instanceof JsonObject){
                        Particle particle = jsonDeserializationContext.deserialize(element, Particle.class);
                        if(particle != null){
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
        JsonArray particles = new JsonArray();
        for(Particle particle : particleEffect.getParticles()){
            particles.add(jsonSerializationContext.serialize(particle, Particle.class));
        }
        jsonObject.add("particles", particles);
        return jsonObject;
    }
}
