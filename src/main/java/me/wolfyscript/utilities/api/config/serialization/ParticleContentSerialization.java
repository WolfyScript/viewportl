package me.wolfyscript.utilities.api.config.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

public class ParticleContentSerialization implements JsonSerializer<ParticleContent>, JsonDeserializer<ParticleContent> {

    @Override
    public ParticleContent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject) {
            ParticleContent particleContent = new ParticleContent();
            JsonObject particles = (JsonObject) jsonElement;
            for (ParticleEffect.Action action : ParticleEffect.Action.values()) {
                String id = action.name().toLowerCase(Locale.ROOT);
                if (particles.has(id) && particles.get(id) instanceof JsonObject) {
                    JsonObject particle = (JsonObject) particles.get(id);
                    if (particle.has("effect")) {
                        String effect = particle.get("effect").getAsString();
                        NamespacedKey namespacedKey;
                        if(effect.split(":").length > 1){
                            namespacedKey = new NamespacedKey(effect.split(":")[0], effect.split(":")[1]);
                        }else{
                            namespacedKey = new NamespacedKey("wolfyutilities", effect);
                        }
                        particleContent.addParticleEffect(action, namespacedKey);
                    }
                }
            }
            return particleContent;
        }
        return new ParticleContent();
    }

    @Override
    public JsonElement serialize(ParticleContent particleContent, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        for (Map.Entry<ParticleEffect.Action, NamespacedKey> entry : particleContent.entrySet()) {
            JsonObject particle = new JsonObject();
            particle.addProperty("effect", entry.getValue().toString());
            result.add(entry.getKey().name().toLowerCase(Locale.ROOT), particle);
        }
        return result;
    }
}
