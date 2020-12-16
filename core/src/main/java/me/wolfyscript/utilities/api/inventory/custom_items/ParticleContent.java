package me.wolfyscript.utilities.api.inventory.custom_items;

import me.wolfyscript.utilities.api.particles.ParticleEffect;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.LinkedHashMap;

public class ParticleContent extends LinkedHashMap<ParticleEffect.Action, NamespacedKey> {

    public NamespacedKey getParticleEffect(ParticleEffect.Action action) {
        return get(action);
    }

    public void addParticleEffect(ParticleEffect.Action action, NamespacedKey namespacedKey) {
        put(action, namespacedKey);
    }
}
