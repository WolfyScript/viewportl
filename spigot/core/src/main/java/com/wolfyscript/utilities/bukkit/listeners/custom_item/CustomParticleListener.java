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

package com.wolfyscript.utilities.bukkit.listeners.custom_item;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerStorage;
import com.wolfyscript.utilities.bukkit.world.entity.PlayerUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CustomParticleListener implements Listener {

    private final WolfyCoreCommon core;

    public CustomParticleListener(WolfyCoreCommon core) {
        this.core = core;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        PlayerStorage playerStorage = core.persistentStorage.getOrCreatePlayerStorage(event.getPlayer());
        playerStorage.computeIfAbsent(PlayerParticleEffectData.class, type -> new PlayerParticleEffectData());
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        var player = event.getPlayer();
        var playerInventory = player.getInventory();
        var newItem = playerInventory.getItem(event.getNewSlot());
        var item = CustomItem.getByItemStack(newItem);
        PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        if (item != null) {
            item.getParticleContent().spawn(player, EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        var player = event.getPlayer();
        PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.OFF_HAND);
        var mainHand = CustomItem.getByItemStack(event.getMainHandItem());
        if (mainHand != null) {
            mainHand.getParticleContent().spawn(player, EquipmentSlot.HAND);
        }
        var offHand = CustomItem.getByItemStack(event.getOffHandItem());
        if (offHand != null) {
            offHand.getParticleContent().spawn(player, EquipmentSlot.OFF_HAND);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var currentItem = player.getInventory().getItemInMainHand();

        if (currentItem.getType().equals(Material.AIR) || currentItem.getAmount() <= 0) {
            PlayerUtils.stopActiveParticleEffect(player, EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {

    }

}
