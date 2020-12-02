package me.wolfyscript.utilities.api.utils.particles;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/*
Contains the Particle classes.
 */
@JsonSerialize(using = Particles.Serializer.class)
public class Particles {

    private static final LinkedHashMap<NamespacedKey, Particle> particles = new LinkedHashMap<>();

    private final String namespace;
    private final String path;
    private final WolfyUtilities wolfyUtilities;

    public Particles(WolfyUtilities wolfyUtilities){
        this(wolfyUtilities, wolfyUtilities.getPlugin().getName().toLowerCase(Locale.ROOT).replace(" ", "_"));
    }

    public Particles(WolfyUtilities wolfyUtilities, String namespace){
        this(wolfyUtilities, namespace, "");
    }

    public Particles(WolfyUtilities wolfyUtilities, String namespace, String path){
        this.wolfyUtilities = wolfyUtilities;
        this.namespace = namespace;
        this.path = wolfyUtilities.getPlugin().getDataFolder().getPath() + path;
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

    public void save() throws IOException {
        save(true);
    }

    /**
     *
     * @param addNamespace If true the namespace is appended to the path
     * @throws IOException
     */
    public void save(boolean addNamespace) throws IOException {
        File file = new File(path + (addNamespace ? File.separator + namespace : "") + File.separator + "particles", "particles.json");
        file.getParentFile().getParentFile().mkdirs();
        if(file.exists() || file.createNewFile()){
            JacksonUtil.getObjectMapper().writeValue(file, this);
        }
    }

    public void load() throws IOException {
        load(true);
    }

    /**
     *
     * @param addNamespace If true the namespace is appended to the path
     * @throws IOException
     */
    public void load(boolean addNamespace) throws IOException {
        File file = new File(path + (addNamespace ? File.separator + namespace : "") + File.separator + "particles", "particles.json");
        if(file.exists()){
            JsonNode node = JacksonUtil.getObjectMapper().readTree(file);
            node.fields().forEachRemaining(entry -> {
                NamespacedKey namespacedKey = new NamespacedKey(namespace, entry.getKey());
                Particle particle = JacksonUtil.getObjectMapper().convertValue(entry.getValue(), Particle.class);
                if(particle != null) {
                    particle.setWolfyUtilities(wolfyUtilities);
                    particle.setNamespacedKey(namespacedKey);
                    if (namespacedKey.equals(particle.getSuperParticle())) {
                        particle.setSuperParticle(null);
                    }
                    addParticle(namespacedKey, particle);
                }
            });
        }
    }

    public static class Serializer extends StdSerializer<Particles> {

        public Serializer(){
            super(Particles.class);
        }

        protected Serializer(Class<Particles> t) {
            super(t);
        }

        @Override
        public void serialize(Particles value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<NamespacedKey, Particle> entry : particles.entrySet()) {
                if(entry.getKey().getNamespace().equals(value.namespace)){
                    gen.writeObjectField(entry.getKey().getKey(), entry.getValue());
                }
            }
            gen.writeEndObject();
        }
    }
}
