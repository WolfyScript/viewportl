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
import org.bukkit.event.player.PlayerItemHeldEvent;

public class EventPlayerItemHeld extends EventPlayer<DataPlayerEvent<PlayerItemHeldEvent>> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("player/item_held");

    @JsonCreator
    protected EventPlayerItemHeld(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, KEY, (Class<DataPlayerEvent<PlayerItemHeldEvent>>)(Object) DataPlayerEvent.class);
    }

}
