package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.ParticleData;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParticleDataSerialization implements JsonSerializer<ParticleData>, JsonDeserializer<ParticleData> {

    @Override
    public ParticleData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            ParticleData particleData = new ParticleData();
            JsonObject particles = (JsonObject) jsonElement;
            for(ParticleEffect.Action action : ParticleEffect.Action.values()){
                String id = action.name().toLowerCase(Locale.ROOT);
                if(particles.has(id) && particles.get(id) instanceof JsonObject){
                    JsonObject particle = (JsonObject) particles.get(id);
                    if(particle.has("effect")){
                        String effect = particle.get("effect").getAsString();
                        NamespacedKey namespacedKey;
                        if(effect.split(":").length > 1){
                            namespacedKey = new NamespacedKey(effect.split(":")[0], effect.split(":")[1]);
                        }else{
                            namespacedKey = new NamespacedKey("wolfyutilities", effect);
                        }
                        particleData.addParticleEffect(action, namespacedKey);
                    }
                }
            }
            return particleData;
        }
        return null;
    }

    @Override
    public JsonElement serialize(ParticleData particleData, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        for(Map.Entry<ParticleEffect.Action, NamespacedKey> entry : particleData.entrySet()){
            JsonObject particle = new JsonObject();
            particle.addProperty("effect", entry.getValue().toString());
            result.add(entry.getKey().name().toLowerCase(Locale.ROOT), particle);
        }
        return result;
    }
}
