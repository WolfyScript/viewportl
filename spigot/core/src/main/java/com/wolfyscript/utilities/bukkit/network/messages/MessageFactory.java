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

package com.wolfyscript.utilities.bukkit.network.messages;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;
import org.bukkit.entity.Player;

public class MessageFactory {

    private final WolfyCoreCommon plugin;
    private final WolfyUtilsBukkit wolfyUtils;

    public MessageFactory(WolfyCoreCommon wuPlugin) {
        this.plugin = wuPlugin;
        this.wolfyUtils = wuPlugin.getWolfyUtils();
    }

    public void sendWolfyUtilsInfo(Player player) {
        MCByteBuf buf = wolfyUtils.getNmsUtil().getNetworkUtil().buffer();
        buf.writeBoolean(true);
        buf.writeUtf(plugin.getWolfyUtils().getPlugin().getDescription().getVersion());
        wolfyUtils.getMessageAPI().send(Messages.CONNECT_INFO, player, buf);
    }

}
