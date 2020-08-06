package me.wolfyscript.utilities.api.utils.particles;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
Contains the ParticleEffects
 */
@JsonSerialize(using = ParticleEffects.Serializer.class)
public class ParticleEffects {

    private static final Map<NamespacedKey, ParticleEffect> particleEffects = new HashMap<>();

    private static final LinkedHashMap<UUID, BukkitTask> currentEffects = new LinkedHashMap<>();

    private final String namespace;
    private final String path;
    private final Plugin plugin;

    public ParticleEffects(Plugin plugin){
        this(plugin, plugin.getName().toLowerCase(Locale.ROOT).replace(" ", "_"));
    }

    public ParticleEffects(Plugin plugin, String namespace){
        this(plugin, namespace, "");
    }

    public ParticleEffects(Plugin plugin, String namespace, String path){
        this.plugin = plugin;
        this.namespace = namespace;
        this.path = plugin.getDataFolder().getPath() + path;
    }

    /*
    Gets a copy of the current particle effects map.
    Changes to the returned map won't be apllied to the real one.
     */
    public static Map<NamespacedKey, ParticleEffect> getEffects() {
        return new HashMap<>(particleEffects);
    }

    /*
    Returns the particleEffect with the specified name, when it exists in the map.
     */
    public static ParticleEffect getEffect(NamespacedKey nameSpacedKey) {
        return particleEffects.get(nameSpacedKey);
    }

    /*
    Puts the given values into the map and replaces the existing Particle Effect.
     */
    public static void addOrReplaceEffect(NamespacedKey nameSpacedKey, ParticleEffect particle) {
        particleEffects.put(nameSpacedKey, particle);
    }

    /*
    Adds the Particle Effect to the map, only when the map doesn't already contains the key
    else it does nothing.
     */
    public static void addEffect(NamespacedKey nameSpacedKey, ParticleEffect particle) {
        if (!particleEffects.containsKey(nameSpacedKey)) {
            particleEffects.put(nameSpacedKey, particle);
        }
    }

    public void save(String path) throws IOException {
        File file = new File(path + File.separator + namespace + File.separator + "particles", "particle_effects.json");
        file.getParentFile().getParentFile().mkdirs();
        if(file.exists() || file.createNewFile()){
            JacksonUtil.getObjectMapper().writeValue(file, this);
        }
    }

    /**
     * Stops the Effect that is currently active.
     * If the uuid is null or the list doesn't contain it this method does nothing.
     *
     * @param uuid
     */
    public static void stopEffect(UUID uuid) {
        if (uuid != null) {
            BukkitTask task = currentEffects.get(uuid);
            if (task != null) {
                task.cancel();
                currentEffects.remove(uuid);
            }
        }
    }

    public static UUID spawnEffectOnBlock(NamespacedKey nameSpacedKey, Block block) {
        ParticleEffect particleEffect = getEffect(nameSpacedKey);
        if (particleEffect != null) {
            UUID id = UUID.randomUUID();
            while (currentEffects.containsKey(id)) {
                id = UUID.randomUUID();
            }
            BukkitTask cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                AtomicBoolean allow = new AtomicBoolean(true);
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> allow.set(block.getWorld().getNearbyEntities(block.getLocation(), 25, 25, 25, entity -> entity instanceof Player).isEmpty()));
                if (allow.get()) {
                    particleEffect.prepare();
                    AtomicInteger i = new AtomicInteger();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            particleEffect.spawnOnBlock(block, i.get());
                            if (i.get() < particleEffect.getDuration()) {
                                i.getAndIncrement();
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
                }
            }, particleEffect.getCooldown(), particleEffect.getCooldown() + particleEffect.getDuration() + 1);

            currentEffects.put(id, cooldownTask);
            return id;
        }
        return null;
    }

    public static UUID spawnEffectOnLocation(NamespacedKey nameSpacedKey, Location location) {
        ParticleEffect particleEffect = getEffect(nameSpacedKey);
        if (particleEffect != null) {
            UUID id = UUID.randomUUID();
            System.out.println();
            while (currentEffects.containsKey(id)) {
                id = UUID.randomUUID();
            }
            BukkitTask cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                AtomicBoolean allow = new AtomicBoolean(true);
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> allow.set(location.getWorld().getNearbyEntities(location, 25, 25, 25, entity -> entity instanceof Player).isEmpty()));
                if (allow.get()) {
                    particleEffect.prepare();
                    AtomicInteger i = new AtomicInteger();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            particleEffect.spawnOnLocation(location, i.get());
                            if (i.get() < particleEffect.getDuration()) {
                                i.getAndIncrement();
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
                }
            }, particleEffect.getCooldown(), particleEffect.getCooldown() + particleEffect.getDuration() + 1);
            currentEffects.put(id, cooldownTask);
            return id;
        }
        return null;
    }

    public static UUID spawnEffectOnPlayer(NamespacedKey nameSpacedKey, EquipmentSlot slot, Player player) {
        ParticleEffect particleEffect = getEffect(nameSpacedKey);
        if (particleEffect != null) {
            UUID id = UUID.randomUUID();
            while (currentEffects.containsKey(id)) {
                id = UUID.randomUUID();
            }
            UUID playerID = player.getUniqueId();
            BukkitTask cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                particleEffect.prepare();
                Player currentPlayer = Bukkit.getPlayer(playerID);
                AtomicInteger i = new AtomicInteger();
                if (currentPlayer != null && currentPlayer.isOnline() && currentPlayer.isValid()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            particleEffect.spawnOnPlayer(currentPlayer, slot, i.get());

                            if (i.get() < particleEffect.getDuration()) {
                                i.getAndIncrement();
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
                }
            }, particleEffect.getCooldown(), particleEffect.getCooldown() + particleEffect.getDuration() + 1);

            currentEffects.put(id, cooldownTask);
            return id;
        }
        return null;
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
        File file = new File(path + (addNamespace ? File.separator + namespace : "") + File.separator + "particles", "particle_effects.json");
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
        File file = new File(path + (addNamespace ? File.separator + namespace : "") + File.separator + "particles", "particle_effects.json");
        if(file.exists()){
            JsonNode node = JacksonUtil.getObjectMapper().readTree(file);
            node.fields().forEachRemaining(entry -> {
                ParticleEffect particle = JacksonUtil.getObjectMapper().convertValue(entry.getValue(), ParticleEffect.class);
                if(particle != null){
                    particle.setReferencePath(file.getParent());
                    addEffect(new NamespacedKey(namespace, entry.getKey()), particle);
                }
            });
        }
    }

    public static class Serializer extends StdSerializer<ParticleEffects> {

        public Serializer(){
            super(ParticleEffects.class);
        }

        protected Serializer(Class<ParticleEffects> t) {
            super(t);
        }

        @Override
        public void serialize(ParticleEffects value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<NamespacedKey, ParticleEffect> entry : particleEffects.entrySet()) {
                if(entry.getKey().getNamespace().equals(value.namespace)){
                    gen.writeObjectField(entry.getKey().getKey(), entry.getValue());
                }
            }
            gen.writeEndObject();
        }
    }
}
