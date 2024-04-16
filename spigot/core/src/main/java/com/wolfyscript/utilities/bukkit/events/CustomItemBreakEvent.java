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

package com.wolfyscript.utilities.bukkit.events;

import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageBreakEvent;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;

public class CustomItemBreakEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private int exp;
    protected Block block;
    private final Player player;
    private boolean dropItems;
    private boolean cancel;
    private CustomItem customItem;

    public CustomItemBreakEvent(CustomItem customItem, BlockStorageBreakEvent event){
        this.player = event.getPlayer();
        this.dropItems = true;
        this.exp = 0;
        this.block = event.getBlock();
        this.customItem = customItem;
    }

    public CustomItemBreakEvent(int exp, Block block, Player player, boolean dropItems, boolean cancel, CustomItem customItem) {
        this.exp = exp;
        this.block = block;
        this.player = player;
        this.dropItems = dropItems;
        this.cancel = cancel;
        this.customItem = customItem;
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    public void setCustomItem(CustomItem customItem) {
        this.customItem = customItem;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public final Block getBlock() {
        return this.block;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
