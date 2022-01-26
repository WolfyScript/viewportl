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

package me.wolfyscript.utilities.api.inventory.custom_items.actions;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class EventPlayerInteract extends EventPlayer<DataPlayerEvent<PlayerInteractEvent>> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("player/interact");

    private List<Action> eventAction = List.of(Action.RIGHT_CLICK_AIR);
    private List<EquipmentSlot> hand = List.of(EquipmentSlot.HAND);
    private List<BlockFace> blockFace = List.of();

    protected EventPlayerInteract() {
        super(KEY, (Class<DataPlayerEvent<PlayerInteractEvent>>)(Object) DataPlayerEvent.class);
    }

    @Override
    public void call(WolfyUtilCore core, DataPlayerEvent<PlayerInteractEvent> data) {
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
