package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.utils.NamespacedKey;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/*
Contains the Particle classes.
 */
public class Particles extends JsonConfiguration {

    private static LinkedHashMap<NamespacedKey, Particle> particles = new LinkedHashMap<>();
    private String namespace;

    public Particles(ConfigAPI configAPI) {
        this(configAPI, "", configAPI.getPlugin().getDataFolder().getPath(), "me/wolfyscript/utilities/api/utils/particles/defaults");
    }

    public Particles(ConfigAPI configAPI, String namespace) {
        this(configAPI, namespace, false);
    }

    public Particles(ConfigAPI configAPI, String namespace, boolean override) {
        this(configAPI, namespace, configAPI.getPlugin().getDataFolder().getPath(), override);
    }

    public Particles(ConfigAPI configAPI, String namespace, String path) {
        this(configAPI, namespace, path, false);
    }

    public Particles(ConfigAPI configAPI, String namespace, String path, boolean override) {
        this(configAPI, namespace, path, "me/wolfyscript/utilities/api/utils/particles/defaults", override);
    }

    public Particles(ConfigAPI configAPI, String namespace, String path, String defPath) {
        this(configAPI, namespace, path, defPath, false);
    }

    public Particles(ConfigAPI configAPI, String namespace, String path, String defPath, boolean override) {
        super(configAPI, path + File.separator + (namespace.isEmpty() ? "" : namespace + File.separator) + "particles", "particles", defPath, "particles", override);
        this.namespace = namespace.isEmpty() ? configAPI.getPlugin().getName().toLowerCase(Locale.ROOT).replace(" ", "_") : namespace;
    }

    /*
    Gets a copy of the current particles map.
    Changes to the returned map won't be apllied to the real one.
     */
    public static LinkedHashMap<NamespacedKey, Particle> getParticles() {
        return new LinkedHashMap<>(particles);
    }

    /*
    Returns the particle with the specified name, when it exists in the map.
     */
    public static Particle getParticle(NamespacedKey namespacedKey) {
        return particles.get(namespacedKey);
    }

    /*
    Puts the given values into the map and replaces the existing Particle.
     */
    public static void addOrReplaceParticle(NamespacedKey namespacedKey, Particle particle) {
        particles.put(namespacedKey, particle);
    }

    /*
    Adds the Particle to the map, only when the map doesn't already contains the key
    else it does nothing.
     */
    public static void addParticle(NamespacedKey namespacedKey, Particle particle) {
        if (!particles.containsKey(namespacedKey)) {
            particles.put(namespacedKey, particle);
        }
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
            if(particle != null) {
                NamespacedKey namespacedKey = new NamespacedKey(namespace, particleName);
                particle.setNamespacedKey(namespacedKey);
                particles.put(namespacedKey, particle);
            }
        }
    }

    public void setParticles() {
        for (Map.Entry<NamespacedKey, Particle> particleEntry : particles.entrySet()) {
            if (particleEntry.getKey().getNamespace().equalsIgnoreCase(namespace)) {
                set(particleEntry.getKey().getKey(), particleEntry.getValue());
            }
        }
    }
}
