package me.wolfyscript.utilities.api.custom_items.custom_data;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.util.HashMap;
import java.util.Map;

public class ParticleData extends CustomData {

    private HashMap<ParticleEffect.Action, NamespacedKey> particleEffects;

    public ParticleData() {
        super("particle_data");
        particleEffects = new HashMap<>();
    }

    public HashMap<ParticleEffect.Action, NamespacedKey> getParticleEffects() {
        return particleEffects;
    }

    private void setParticleEffects(HashMap<ParticleEffect.Action, NamespacedKey> particleEffects) {
        this.particleEffects = particleEffects;
    }

    public NamespacedKey getParticleEffect(ParticleEffect.Action action) {
        return particleEffects.get(action);
    }

    @Override
    public CustomData getDefaultCopy() {
        return new ParticleData();
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<ParticleEffect.Action, NamespacedKey> entry : particleEffects.entrySet()) {
            map.put(entry.getKey().name(), entry.getValue().toString());
        }
        return map;
    }

    @Override
    public CustomData fromMap(Map<String, Object> map) {
        ParticleData particleData = new ParticleData();
        HashMap<ParticleEffect.Action, NamespacedKey> effects = new HashMap<>();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String value = (String) entry.getValue();
            effects.put(ParticleEffect.Action.valueOf(entry.getKey()), new NamespacedKey(value.split(":")[0], value.split(":")[1]));
        }
        particleData.setParticleEffects(effects);
        return particleData;
    }
}
