package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleData extends LinkedHashMap<ParticleEffect.Action, NamespacedKey> {

    public NamespacedKey getParticleEffect(ParticleEffect.Action action) {
        return get(action);
    }

    public void addParticleEffect(ParticleEffect.Action action, NamespacedKey namespacedKey){
        put(action, namespacedKey);
    }
}
