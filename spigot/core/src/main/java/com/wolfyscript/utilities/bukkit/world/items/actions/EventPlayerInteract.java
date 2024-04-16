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

package com.wolfyscript.utilities.bukkit.world.items.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.WolfyUtils;
import java.util.List;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EventPlayerInteract extends EventPlayer<DataPlayerEvent<PlayerInteractEvent>> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("player/interact");

    private List<Action> eventAction = List.of(Action.RIGHT_CLICK_AIR);
    private List<EquipmentSlot> hand = List.of(EquipmentSlot.HAND);
    private List<BlockFace> blockFace = List.of();

    @JsonCreator
    protected EventPlayerInteract(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, KEY, (Class<DataPlayerEvent<PlayerInteractEvent>>)(Object) DataPlayerEvent.class);
    }

    @Override
    public void call(WolfyUtils core, DataPlayerEvent<PlayerInteractEvent> data) {
        PlayerInteractEvent event = data.getEvent();
        if ((eventAction.isEmpty() || eventAction.contains(event.getAction())) && (hand.isEmpty() || hand.contains(event.getHand())) && (blockFace.isEmpty() || blockFace.contains(event.getBlockFace()))) {
            super.call(core, data);
        }
    }

    public void setEventAction(List<Action> eventAction) {
        this.eventAction = eventAction;
    }

    public void setHand(List<EquipmentSlot> hand) {
        this.hand = hand;
    }

    public void setBlockFace(List<BlockFace> blockFace) {
        this.blockFace = blockFace;
    }
}
