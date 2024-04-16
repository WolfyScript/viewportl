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

package com.wolfyscript.utilities.bukkit.world.entity;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class PlayerUtils {

    private static final Map<UUID, PlayerStore> cachedStores = new HashMap<>();

    private PlayerUtils() {
    }

    private static Optional<PlayerParticleEffectData> getParticleData(Player player) {
        return ((WolfyCoreCommon) WolfyCore.getInstance()).persistentStorage.getOrCreatePlayerStorage(player).getData(PlayerParticleEffectData.class);
    }

    @Deprecated
    public static boolean hasActiveItemEffects(Player player) {
        return getParticleData(player).isPresent();
    }

    @Deprecated
    public static boolean hasActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getParticleData(player).map(data -> data.hasActiveItemEffects(equipmentSlot)).orElse(false);
    }

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @param player The player object
     * @return The active particle effects on the player
     */
    @Deprecated
    public static Map<EquipmentSlot, UUID> getActiveItemEffects(Player player) {
        return getParticleData(player).map(PlayerParticleEffectData::getActiveItemEffects).orElseGet(() -> new EnumMap<>(EquipmentSlot.class));
    }

    @Deprecated
    public static UUID getActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getActiveItemEffects(player).get(equipmentSlot);
    }

    @Deprecated
    public static void setActiveParticleEffect(Player player, EquipmentSlot equipmentSlot, UUID uuid) {
        getParticleData(player).ifPresent(data -> data.setActiveParticleEffect(equipmentSlot, uuid));
    }

    @Deprecated
    public static void stopActiveParticleEffect(Player player, EquipmentSlot equipmentSlot) {
        getParticleData(player).ifPresent(data -> data.stopActiveParticleEffect(equipmentSlot));
    }

    @Deprecated
    public static void loadStores() {
        // The old system is very broken, so do not load old data to prevent further crashes, etc.!
    }

    /**
     * @deprecated Does nothing anymore! Kept for backwards compatibility!
     */
    @Deprecated
    public static void saveStores() {
        // Does nothing anymore. Kept for backwards compatibility!
    }

    @Deprecated
    @NotNull
    public static PlayerStore getStore(@NotNull Player player) {
        return getStore(player.getUniqueId());
    }

    @Deprecated
    @NotNull
    public static PlayerStore getStore(@NotNull UUID uuid) {
        return cachedStores.computeIfAbsent(uuid, uuid1 -> new PlayerStore()); // Just return a dummy obj, as the old data is no longer available!
    }

}
