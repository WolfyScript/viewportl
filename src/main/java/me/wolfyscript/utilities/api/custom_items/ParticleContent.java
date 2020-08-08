package me.wolfyscript.utilities.api.custom_items;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.serialization.ParticleContentSerialization;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.util.LinkedHashMap;

@JsonSerialize(using = ParticleContentSerialization.Serializer.class)
@JsonDeserialize(using = ParticleContentSerialization.Deserializer.class)
public class ParticleContent extends LinkedHashMap<ParticleEffect.Action, NamespacedKey> {

    public NamespacedKey getParticleEffect(ParticleEffect.Action action) {
        return get(action);
    }

    public void addParticleEffect(ParticleEffect.Action action, NamespacedKey namespacedKey) {
        put(action, namespacedKey);
    }
}
