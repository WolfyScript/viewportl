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

package me.wolfyscript.utilities.util.entity;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtils {

    private PlayerUtils() {
    }

    static final Map<UUID, PlayerStore> indexedStores = new HashMap<>();

    private static final HashMap<UUID, Map<EquipmentSlot, UUID>> playerItemParticles = new HashMap<>();
    static final File STORE_FOLDER = new File(WolfyUtilities.getWUPlugin().getDataFolder(), "players");


    public static boolean hasActiveItemEffects(Player player) {
        return playerItemParticles.containsKey(player.getUniqueId());
    }

    public static boolean hasActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getActiveItemEffects(player).containsKey(equipmentSlot);
    }

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @param player The player object
     * @return The active particle effects on the player
     */
    public static Map<EquipmentSlot, UUID> getActiveItemEffects(Player player) {
        return playerItemParticles.computeIfAbsent(player.getUniqueId(), uuid -> new EnumMap<>(EquipmentSlot.class));
    }

    public static UUID getActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getActiveItemEffects(player).get(equipmentSlot);
    }

    public static void setActiveParticleEffect(Player player, EquipmentSlot equipmentSlot, UUID uuid) {
        stopActiveParticleEffect(player, equipmentSlot);
        getActiveItemEffects(player).put(equipmentSlot, uuid);
    }

    public static void stopActiveParticleEffect(Player player, EquipmentSlot equipmentSlot) {
        ParticleUtils.stopAnimation(getActiveItemEffects(player, equipmentSlot));
        getActiveItemEffects(player).remove(equipmentSlot);
    }

    public static void loadStores() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Player Data");
        if (STORE_FOLDER.exists() || STORE_FOLDER.mkdirs()) {
            for (String s : STORE_FOLDER.list()) {
                var uuid = UUID.fromString(s.replace(".store", ""));
                indexedStores.put(uuid, PlayerStore.load(uuid));
            }
        }
    }

    public static void saveStores() {
        if (STORE_FOLDER.exists() || STORE_FOLDER.mkdirs()) {
            indexedStores.forEach((uuid, playerStore) -> playerStore.save(uuid));
        }
    }

    @NotNull
    public static PlayerStore getStore(@NotNull Player player) {
        return getStore(player.getUniqueId());
    }

    @NotNull
    public static PlayerStore getStore(@NotNull UUID uuid) {
        indexedStores.computeIfAbsent(uuid, key -> {
            var playerStore = new PlayerStore();
            playerStore.save(key);
            return playerStore;
        });
        return indexedStores.get(uuid);
    }

}
