package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParticleSerialization implements JsonSerializer<Particle>, JsonDeserializer<Particle> {

    @Override
    public Particle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            JsonObject object = (JsonObject) jsonElement;
            Particle resultParticle = new Particle();
            if (object.has("particle")) {
                String particle = object.getAsJsonPrimitive("particle").getAsString();
                NamespacedKey namespacedKey = null;
                if (particle.contains(":") && particle.split(":").length > 1) {
                    namespacedKey = new NamespacedKey(particle.split(":")[0].toLowerCase(Locale.ROOT).replace(" ", "_"), particle.split(":")[1].toLowerCase(Locale.ROOT).replace(" ", "_"));
                } else {
                    namespacedKey = new NamespacedKey("wolfyutilities", particle);
                }
                if (namespacedKey.getNamespace().equalsIgnoreCase("minecraft")) {
                    org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(namespacedKey.getKey().toUpperCase(Locale.ROOT));
                    resultParticle.setParticle(particleType);
                } else {
                    Particle particle1 = Particles.getParticles().get(namespacedKey);
                    resultParticle = new Particle(particle1);
                }
                resultParticle.setNamespacedKey(namespacedKey);
            }
            if (object.has("icon")) {
                resultParticle.setIcon(Material.matchMaterial(object.get("icon").getAsString()));
            }
            if (object.has("name")) {
                resultParticle.setName(WolfyUtilities.translateColorCodes(object.get("name").getAsString()));
            }
            if (object.has("description")) {
                List<String> description = new ArrayList<>();
                JsonArray lore = object.getAsJsonArray("description");
                for (JsonElement line : lore) {
                    description.add(WolfyUtilities.translateColorCodes(line.getAsString()));
                }
                resultParticle.setDescription(description);
            }
            if (object.has("count")) {
                resultParticle.setCount(object.getAsJsonPrimitive("count").getAsInt());
            }
            if (object.has("extra")) {
                resultParticle.setExtra(object.getAsJsonPrimitive("extra").getAsDouble());
            }
            if (object.has("data")) {
                if (resultParticle.getParticle().equals(org.bukkit.Particle.REDSTONE)) {
                    resultParticle.setData(jsonDeserializationContext.deserialize(object.get("data"), org.bukkit.Particle.DustOptions.class));
                } else {
                    resultParticle.setData(jsonDeserializationContext.deserialize(object.get("data"), resultParticle.getDataClass()));
                }
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
        if (particle.hasSuperParticle()) {
            particleObject.addProperty("particle", particle.getSuperParticle().getNamespacedKey().toString());
        } else {
            particleObject.addProperty("particle", "minecraft:" + particle.getParticle().name().toLowerCase(Locale.ROOT));
        }
        if (particle.hasIcon()) {
            particleObject.addProperty("icon", particle.getIcon().getKey().toString());
        }
        if (particle.hasName()) {
            particleObject.addProperty("name", particle.getName());
        }
        if (particle.hasDescription()) {
            JsonArray description = new JsonArray();
            if (particle.getDescription() != null) {
                for (String line : particle.getDescription()) {
                    description.add(line);
                }
            }
            particleObject.add("description", description);
        }
        if (particle.hasCount()) {
            particleObject.addProperty("count", particle.getCount());
        }
        if (particle.hasExtra()) {
            particleObject.addProperty("extra", particle.getExtra());
        }
        if (particle.hasData()) {
            particleObject.add("data", jsonSerializationContext.serialize(particle.getData(), particle.getDataClass()));
        }
        if (particle.hasRelativeX() || particle.hasRelativeY() || particle.hasRelativeZ()) {
            JsonObject relative = new JsonObject();
            if (particle.hasRelativeX()) {
                relative.addProperty("x", particle.getRelativeX());
            }
            if (particle.hasRelativeY()) {
                relative.addProperty("y", particle.getRelativeY());
            }
            if (particle.hasRelativeZ()) {
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
