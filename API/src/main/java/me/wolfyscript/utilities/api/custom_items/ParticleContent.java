package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.util.LinkedHashMap;

public class ParticleContent extends LinkedHashMap<ParticleEffect.Action, NamespacedKey> {

    public NamespacedKey getParticleEffect(ParticleEffect.Action action) {
        return get(action);
    }

    public void addParticleEffect(ParticleEffect.Action action, NamespacedKey namespacedKey) {
        put(action, namespacedKey);
    }
}
