package me.wolfyscript.utilities.util.entity;

import me.wolfyscript.utilities.util.particles.ParticleAnimationUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class PlayerUtils {

    private static final HashMap<UUID, HashMap<EquipmentSlot, UUID>> playerItemParticles = new HashMap<>();

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @param player The player object
     * @return The active particle effects on the player
     */
    public static HashMap<EquipmentSlot, UUID> getActiveItemEffects(Player player) {
        playerItemParticles.putIfAbsent(player.getUniqueId(), new HashMap<>());
        return playerItemParticles.get(player.getUniqueId());
    }

    public static boolean hasActiveItemEffects(Player player) {
        return playerItemParticles.containsKey(player.getUniqueId());
    }

    public static boolean hasActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return playerItemParticles.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(equipmentSlot);
    }

    public static UUID getActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return playerItemParticles.getOrDefault(player.getUniqueId(), new HashMap<>()).get(equipmentSlot);
    }

    public static void setActiveParticleEffect(Player player, EquipmentSlot equipmentSlot, UUID uuid) {
        if (hasActiveItemEffects(player, equipmentSlot)) {
            stopActiveParticleEffect(player, equipmentSlot);
        }
        getActiveItemEffects(player).put(equipmentSlot, uuid);
    }

    public static void stopActiveParticleEffect(Player player, EquipmentSlot equipmentSlot) {
        ParticleAnimationUtils.stopEffect(getActiveItemEffects(player, equipmentSlot));
        getActiveItemEffects(player).remove(equipmentSlot);
    }

}
