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

package me.wolfyscript.utilities.main.listeners.custom_item;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;

public class CustomDurabilityListener implements Listener {

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        var customItem = new CustomItem(event.getItem());
        var itemStack = event.getItem();
        if (customItem.hasCustomDurability()) {
            event.setCancelled(true);
            int totalDmg = customItem.getCustomDamage() + event.getDamage();
            if (totalDmg > customItem.getCustomDurability()) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                customItem.setCustomDamage(customItem.getCustomDamage() + event.getDamage());
            }
        }
    }

    @EventHandler
    public void onMend(PlayerItemMendEvent event) {
        var customItem = new CustomItem(event.getItem());
        if (customItem.hasCustomDurability()) {
            int repairAmount = Math.min(event.getExperienceOrb().getExperience() * 2, customItem.getCustomDamage());
            int finalTotalDmg = Math.max(0, customItem.getCustomDamage() - repairAmount);
            Bukkit.getScheduler().runTask(WUPlugin.getInstance(), () -> customItem.setCustomDamage(finalTotalDmg));
        }
    }
}
