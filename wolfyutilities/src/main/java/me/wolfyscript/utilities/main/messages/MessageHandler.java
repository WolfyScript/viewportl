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

package me.wolfyscript.utilities.main.messages;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.main.WUPlugin;

public class MessageHandler {

    private final WUPlugin plugin;
    private final WolfyUtilities wolfyUtils;
    private final MessageAPI messageAPI;

    public MessageHandler(WUPlugin wuPlugin) {
        this.plugin = wuPlugin;
        this.wolfyUtils = wuPlugin.getWolfyUtilities();
        this.messageAPI = this.wolfyUtils.getMessageAPI();
        //init(); //Disabled for now to prevent misuse from clients!
    }

    public void init() {
        messageAPI.register(Messages.CONNECT_INFO);
        messageAPI.register(Messages.CONNECT_REQUEST, (player, wolfyUtils1, buf) -> {
            if (player.hasPermission("wolfyutilities.network.connect")) {
                plugin.getMessageFactory().sendWolfyUtilsInfo(player);
            }
        });

    }

}
