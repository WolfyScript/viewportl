package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
Contains the ParticleEffects
 */
public class ParticleEffects extends JsonConfiguration {

    private static Map<NamespacedKey, ParticleEffect> particleEffects = new HashMap<>();

    private static LinkedHashMap<UUID, BukkitTask> currentEffects = new LinkedHashMap<>();
    private String namespace;

    public ParticleEffects(ConfigAPI configAPI) {
        this(configAPI, "", configAPI.getPlugin().getDataFolder().getPath(), "me/wolfyscript/utilities/api/utils/particles/defaults");
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace) {
        this(configAPI, namespace, false);
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace, boolean override) {
        this(configAPI, namespace, configAPI.getPlugin().getDataFolder().getPath(), override);
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace, String path) {
        this(configAPI, namespace, path, false);
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace, String path, boolean override) {
        this(configAPI, namespace, path, "me/wolfyscript/utilities/api/utils/particles/defaults", override);
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace, String path, String defPath) {
        this(configAPI, namespace, path, defPath, false);
    }

    public ParticleEffects(ConfigAPI configAPI, String namespace, String path, String defPath, boolean override) {
        super(configAPI, path + File.separator + (namespace.isEmpty() ? "" : namespace + File.separator) + "particles", "particle_effects", defPath, "particle_effects", override);
        this.namespace = namespace.isEmpty() ? configAPI.getPlugin().getName().toLowerCase(Locale.ROOT).replace(" ", "_") : namespace;
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

    @Override
    public void reload() {
        super.reload(true);
    }

    @Override
    public void save() {
        super.save(true);
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

    /**
     * Loads the effects out of this config into the global Effects List.
     */
    public void loadEffects() {
        for (String key : getKeys()) {
            ParticleEffect particleEffect = get(ParticleEffect.class, key);
            if (particleEffect != null) {
                particleEffect.setReferencePath(this.getConfigFile().getParent());
                particleEffects.put(new NamespacedKey(namespace, key), particleEffect);
            }
        }
    }

    /**
     * Sets the Effects which correspond to the current namespace into this config.
     */
    public void setEffects() {
        for (Map.Entry<NamespacedKey, ParticleEffect> particleEntry : particleEffects.entrySet()) {
            if (particleEntry.getKey().getNamespace().equalsIgnoreCase(namespace)) {
                set(particleEntry.getKey().getKey(), particleEntry.getValue());
            }
        }
    }
}
