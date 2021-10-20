/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.particles;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ParticleUtils {

    private static final Map<UUID, ParticleAnimation.Scheduler> activeAnimations = new LinkedHashMap<>();

    public static void spawnAnimationOnBlock(NamespacedKey nameSpacedKey, Block block) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawn(block);
        }
    }

    public static void spawnAnimationOnLocation(NamespacedKey nameSpacedKey, Location location) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawn(location);
        }
    }

    public static void spawnAnimationOnEntity(NamespacedKey nameSpacedKey, Entity entity) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawn(entity);
        }
    }

    public static void spawnAnimationOnPlayer(NamespacedKey nameSpacedKey, Player player, EquipmentSlot equipmentSlot) {
        ParticleAnimation animation = Registry.PARTICLE_ANIMATIONS.get(nameSpacedKey);
        if (animation != null) {
            animation.spawn(player, equipmentSlot);
        }
    }

    /**
     * Stops the Effect that is currently active.
     * If the uuid is null or the list doesn't contain it this method does nothing.
     *
     * @param uuid The {@link UUID} of the animation.
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

    static void removeScheduler(UUID uuid) {
        if (uuid != null) {
            ParticleAnimation.Scheduler scheduler = activeAnimations.get(uuid);
            if (scheduler != null) {
                activeAnimations.remove(uuid);
            }
        }
    }

    static UUID addScheduler(ParticleAnimation.Scheduler scheduler) {
        UUID id = UUID.randomUUID();
        while (activeAnimations.containsKey(id)) {
            id = UUID.randomUUID();
        }
        activeAnimations.put(id, scheduler);
        return id;
    }

    /**
     * @return A set containing the {@link UUID}s of the active animations. This Set is unmodifiable!
     */
    public static Set<UUID> getActiveAnimations() {
        return Set.copyOf(activeAnimations.keySet());
    }
}
