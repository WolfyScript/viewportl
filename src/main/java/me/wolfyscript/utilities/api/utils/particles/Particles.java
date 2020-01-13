package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/*
Contains the Particle classes.
 */
public class Particles extends JsonConfiguration{

    private static TreeMap<String, Particle> particles = new TreeMap<>();

    /*
    Gets a copy of the current particles map.
    Changes to the returned map won't be apllied to the real one.
     */
    public static TreeMap<String, Particle> getParticles() {
        return new TreeMap<>(particles);
    }

    /*
    Returns the particle with the specified name, when it exists in the map.
     */
    public static Particle getParticle(String name){
        return particles.get(name);
    }

    /*
    Puts the given values into the map and replaces the existing Particle.
     */
    public static void addOrReplaceParticle(String name, Particle particle){
        particles.put(name, particle);
    }

    /*
    Adds the Particle to the map, only when the map doesn't already contains the key
    else it does nothing.
     */
    public static void addParticle(String name, Particle particle){
        if(!particles.containsKey(name)){
            particles.put(name, particle);
        }
    }

    public Particles(ConfigAPI configAPI) {
        super(configAPI, configAPI.getPlugin().getDataFolder() + File.separator + "particles", "particles", "me/wolfyscript/utilities/api/utils/particles", "particles", false);
    }

    @Override
    public void reload() {
        super.reload(true);
    }

    @Override
    public void save() {
        super.save(true);
    }

    public void loadParticles(){
        for(String particleName : getKeys()){
            Particle particle = get(Particle.class, particleName);
            if(particle != null){
                particle.setName(particleName);
                particles.put(particleName, particle);
            }
        }
    }

    public void setParticles(){
        for(Map.Entry<String, Particle> particleEntry : particles.entrySet()){
            set(particleEntry.getKey(), particleEntry.getValue());
        }
    }
}
