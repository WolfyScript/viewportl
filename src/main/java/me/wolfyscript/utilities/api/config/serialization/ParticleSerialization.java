package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.Particles;

import java.lang.reflect.Type;
import java.util.Locale;

public class ParticleSerialization implements JsonSerializer<Particle>, JsonDeserializer<Particle> {

    @Override
    public Particle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            JsonObject object = (JsonObject) jsonElement;
            Particle resultParticle = new Particle();
            if (object.has("particle")) {
                String particle = object.getAsJsonPrimitive("particle").getAsString();
                if (particle.startsWith("minecraft:")) {
                    org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(particle.substring("minecraft:".length()).toUpperCase(Locale.ROOT));
                    resultParticle.setParticle(particleType);
                } else {
                    resultParticle = new Particle(Particles.getParticles().get(particle.toLowerCase(Locale.ROOT)));
                    resultParticle.setSuperParticle(Particles.getParticles().get(particle.toLowerCase(Locale.ROOT)));
                }
                resultParticle.setName(particle);
            }
            if (object.has("count")) {
                resultParticle.setCount(object.getAsJsonPrimitive("count").getAsInt());
            }
            if (object.has("extra")) {
                resultParticle.setExtra(object.getAsJsonPrimitive("extra").getAsDouble());
            }
            if (object.has("relative")) {
                JsonObject relative = object.getAsJsonObject("relative");
                resultParticle.setRelativeX(relative.get("x").getAsDouble());
                resultParticle.setRelativeY(relative.get("y").getAsDouble());
                resultParticle.setRelativeZ(relative.get("z").getAsDouble());
            }
            if (object.has("offset")) {
                JsonObject relative = object.getAsJsonObject("offset");
                resultParticle.setOffsetX(relative.get("x").getAsDouble());
                resultParticle.setOffsetY(relative.get("y").getAsDouble());
                resultParticle.setOffsetZ(relative.get("z").getAsDouble());
            }
            if(object.has("scripts")){
                JsonArray jsonArray = object.getAsJsonArray("scripts");
                for(JsonElement element : jsonArray){
                    if(element instanceof JsonPrimitive){
                        resultParticle.addScript(element.getAsString());
                    }
                }
            }
            return resultParticle;
        }
        return null;
    }

    @Override
    public JsonElement serialize(Particle particle, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject particleObject = new JsonObject();
        if(particle.hasSuperParticle()){
            particleObject.addProperty("particle", particle.getSuperParticle().getName());
        }else{
            particleObject.addProperty("particle", "minecraft:"+particle.getParticle().name().toLowerCase(Locale.ROOT));
        }
        if(particle.hasCount()){
            particleObject.addProperty("count", particle.getCount());
        }
        if(particle.hasExtra()){
            particleObject.addProperty("extra", particle.getExtra());
        }
        if(particle.hasRelativeX() || particle.hasRelativeY() || particle.hasRelativeZ()){
            JsonObject relative = new JsonObject();
            if(particle.hasRelativeX()){
                relative.addProperty("x", particle.getRelativeX());
            }
            if(particle.hasRelativeY()){
                relative.addProperty("y", particle.getRelativeY());
            }
            if(particle.hasRelativeZ()){
                relative.addProperty("z", particle.getRelativeZ());
            }
            particleObject.add("relative", relative);
        }
        if(particle.hasOffsetX() || particle.hasOffsetY() || particle.hasOffsetZ()){
            JsonObject offset = new JsonObject();
            if(particle.hasOffsetX()){
                offset.addProperty("x", particle.getOffsetX());
            }
            if(particle.hasOffsetY()){
                offset.addProperty("y", particle.getOffsetY());
            }
            if(particle.hasOffsetZ()){
                offset.addProperty("z", particle.getOffsetZ());
            }
            particleObject.add("offset", offset);
        }
        if(particle.hasScripts()){
            JsonArray array = new JsonArray();
            for(String script : particle.getScripts()){
                array.add(script);
            }
            particleObject.add("scripts", array);
        }
        return particleObject;
    }
}
