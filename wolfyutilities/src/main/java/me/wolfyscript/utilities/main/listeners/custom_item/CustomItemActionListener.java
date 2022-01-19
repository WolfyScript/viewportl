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

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.DataPlayerEvent;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerInteract;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomItemActionListener implements Listener {

    private final WolfyUtilCore core;
    private final RegistryCustomItem customItems;

    public CustomItemActionListener(WolfyUtilCore core) {
        this.core = core;
        this.customItems = core.getRegistries().getCustomItems();
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        var item = customItems.getByItemStack(event.getItem());
        item.ifPresent(customItem -> {
            customItem.getActionSettings().callEvent(EventPlayerInteract.KEY, new DataPlayerEvent<>(event, event.getPlayer(), customItem));
        });
    }

}