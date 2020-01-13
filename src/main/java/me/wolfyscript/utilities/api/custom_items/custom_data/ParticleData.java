package me.wolfyscript.utilities.api.custom_items.custom_data;

import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.util.HashMap;
import java.util.Map;

public class ParticleData extends CustomData {

    private HashMap<ParticleEffect.Action, String> particleEffects;

    public ParticleData() {
        super("particle_data");
        particleEffects = new HashMap<>();
    }

    private void setParticleEffects(HashMap<ParticleEffect.Action, String> particleEffects) {
        this.particleEffects = particleEffects;
    }

    public HashMap<ParticleEffect.Action, String> getParticleEffects() {
        return particleEffects;
    }

    public String getParticleEffect(ParticleEffect.Action action){
        return particleEffects.get(action);
    }

    @Override
    public CustomData getDefaultCopy() {
        return new ParticleData();
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        for(Map.Entry<ParticleEffect.Action, String> entry : particleEffects.entrySet()){
            map.put(entry.getKey().name(), entry.getValue());
        }
        return map;
    }

    @Override
    public CustomData fromMap(Map<String, Object> map) {
        ParticleData particleData = new ParticleData();
        HashMap<ParticleEffect.Action, String> effects = new HashMap<>();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            effects.put(ParticleEffect.Action.valueOf(entry.getKey()), (String) entry.getValue());
        }
        particleData.setParticleEffects(effects);
        return particleData;
    }
}
