package me.wolfyscript.utilities.util.particles;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
Contains the ParticleEffects
 */
public class ParticleUtils {

    private static final Map<UUID, ParticleAnimation.Scheduler> activeAnimations = new LinkedHashMap<>();

    public static void spawnAnimationOnBlock(NamespacedKey nameSpacedKey, Block block) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawnOnBlock(block);
        }
    }

    public static void spawnAnimationOnLocation(NamespacedKey nameSpacedKey, Location location) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawnOnLocation(location);
        }
    }

    public static void spawnAnimationOnEntity(NamespacedKey nameSpacedKey, Entity entity) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawnOnEntity(entity);
        }
    }

    public static void spawnAnimationOnPlayer(NamespacedKey nameSpacedKey, Player player, EquipmentSlot equipmentSlot) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawnOnPlayer(player, equipmentSlot);
        }
    }

    /**
     * Stops the Effect that is currently active.
     * If the uuid is null or the list doesn't contain it this method does nothing.
     *
     * @param uuid
     */
    public static void stopAnimation(UUID uuid) {
        if (uuid != null) {
            ParticleAnimation.Scheduler scheduler = activeAnimations.get(uuid);
            if (scheduler != null) {
                scheduler.stop();
                activeAnimations.remove(uuid);
            }
        }
    }

    public static void removeScheduler(UUID uuid) {
        if (uuid != null) {
            ParticleAnimation.Scheduler scheduler = activeAnimations.get(uuid);
            if (scheduler != null) {
                activeAnimations.remove(uuid);
            }
        }
    }

    public static UUID addScheduler(ParticleAnimation.Scheduler scheduler) {
        UUID id = UUID.randomUUID();
        while (activeAnimations.containsKey(id)) {
            id = UUID.randomUUID();
        }
        activeAnimations.put(id, scheduler);
        return id;
    }

    public static Set<UUID> getActiveAnimations() {
        return activeAnimations.keySet();
    }
}
