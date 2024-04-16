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

package com.wolfyscript.utilities.bukkit.chat;

import com.wolfyscript.utilities.WolfyUtils;
import java.util.UUID;

import com.wolfyscript.utilities.platform.adapters.Player;
import com.wolfyscript.utilities.chat.ClickActionCallback;
import net.kyori.adventure.audience.Audience;

/**
 * Caches all the data necessary to create and call callbacks, when the corresponding text component is clicked.
 */
public class PlayerAction {

    private UUID uuid;
    private WolfyUtils api;
    private ClickActionCallback clickAction;
    private final boolean discard;

    PlayerAction(WolfyUtils api, Player player, ClickActionCallback action, boolean discard) {
        this.uuid = player.uuid();
        this.api = api;
        this.clickAction = action;
        this.discard = discard;
    }

    public void run(com.wolfyscript.utilities.platform.adapters.Player player) {
        if (clickAction != null) {
            clickAction.run(api, player);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public WolfyUtils getApi() {
        return api;
    }

    public void setApi(WolfyUtils api) {
        this.api = api;
    }

    public ClickActionCallback getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickActionCallback clickAction) {
        this.clickAction = clickAction;
    }

    public boolean isDiscard() {
        return discard;
    }
}
