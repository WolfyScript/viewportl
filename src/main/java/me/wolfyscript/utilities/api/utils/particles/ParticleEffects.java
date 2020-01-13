package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/*
Contains the ParticleEffects
 */
public class ParticleEffects extends JsonConfiguration {

    private static TreeMap<String, ParticleEffect> particleEffects = new TreeMap<>();

    private static LinkedHashMap<UUID, BukkitTask> currentEffects = new LinkedHashMap<>();

    /*
    Gets a copy of the current particle effects map.
    Changes to the returned map won't be apllied to the real one.
     */
    public static TreeMap<String, ParticleEffect> getEffects() {
        return new TreeMap<>(particleEffects);
    }

    /*
    Returns the particleEffect with the specified name, when it exists in the map.
     */
    public static ParticleEffect getEffect(String name) {
        if(name == null){
            return null;
        }
        return particleEffects.get(name);
    }

    /*
    Puts the given values into the map and replaces the existing Particle Effect.
     */
    public static void addOrReplaceEffect(String name, ParticleEffect particle) {
        particleEffects.put(name, particle);
    }

    /*
    Adds the Particle Effect to the map, only when the map doesn't already contains the key
    else it does nothing.
     */
    public static void addEffect(String name, ParticleEffect particle) {
        if (!particleEffects.containsKey(name)) {
            particleEffects.put(name, particle);
        }
    }

    public ParticleEffects(ConfigAPI configAPI) {
        super(configAPI, configAPI.getPlugin().getDataFolder() + File.separator + "particles", "particle_effects", "me/wolfyscript/utilities/api/utils/particles", "particle_effects", false);
    }

    @Override
    public void reload() {
        super.reload(true);
    }

    @Override
    public void save() {
        super.save(true);
    }

    public void loadEffects() {
        for (String name : getKeys()) {
            ParticleEffect particleEffect = get(ParticleEffect.class, name);
            if (particleEffect != null) {
                particleEffect.setReferencePath(this.getConfigFile().getParent());
                particleEffects.put(name, particleEffect);
            }
        }
    }

    public void setEffects() {
        for (Map.Entry<String, ParticleEffect> particleEntry : particleEffects.entrySet()) {
            set(particleEntry.getKey(), particleEntry.getValue());
        }
    }

    public static void stopEffect(UUID uuid) {
        if(uuid != null){
            BukkitTask task = currentEffects.get(uuid);
            if (task != null) {
                task.cancel();
            }
        }
    }

    public static UUID spawnEffectOnBlock(String name, Block block) {
        ParticleEffect particleEffect = getEffect(name);
        if (particleEffect != null) {
            UUID id = UUID.randomUUID();
            while (currentEffects.containsKey(id)) {
                id = UUID.randomUUID();
            }
            BukkitTask cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
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

            }, particleEffect.getCooldown(), particleEffect.getCooldown() + particleEffect.getDuration() + 1);

            currentEffects.put(id, cooldownTask);
            return id;
        }
        return null;
    }

    public static UUID spawnEffectOnLocation(String name, Location location) {
        ParticleEffect particleEffect = getEffect(name);
        if (particleEffect != null) {
            UUID id = UUID.randomUUID();
            while (currentEffects.containsKey(id)) {
                id = UUID.randomUUID();
            }
            BukkitTask cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
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

            }, particleEffect.getCooldown(), particleEffect.getCooldown() + particleEffect.getDuration() + 1);

            currentEffects.put(id, cooldownTask);
            return id;
        }
        return null;
    }

    public static UUID spawnEffectOnPlayer(String name, Player player) {
        ParticleEffect particleEffect = getEffect(name);
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
                if(currentPlayer != null && currentPlayer.isOnline() && currentPlayer.isValid()){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            particleEffect.spawnOnPlayer(currentPlayer, i.get());

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

    public static UUID spawnEffectOnPlayerHand(String name, EquipmentSlot hand, Player player) {
        ParticleEffect particleEffect = getEffect(name);
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
                if(currentPlayer != null && currentPlayer.isOnline() && currentPlayer.isValid()){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            particleEffect.spawnOnPlayerHand(currentPlayer, hand, i.get());

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
}
