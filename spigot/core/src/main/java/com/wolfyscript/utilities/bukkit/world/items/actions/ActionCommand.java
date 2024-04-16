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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.PlaceholderAPIIntegration;
import com.wolfyscript.utilities.WolfyUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionCommand extends Action<DataPlayer> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("player/command");

    @JsonIgnore
    private final PlaceholderAPIIntegration papi;
    private List<String> playerCommands = new ArrayList<>();
    private List<String> consoleCommands = new ArrayList<>();

    @JsonCreator
    public ActionCommand(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, KEY, DataPlayer.class);
        papi = ((WolfyUtilsBukkit) wolfyUtils).getCore().getCompatibilityManager().getPlugins().getIntegration(PlaceholderAPIIntegration.KEY, PlaceholderAPIIntegration.class);
    }

    @Override
    public void execute(WolfyUtils core, DataPlayer data) {
        final Player player = data.getPlayer();
        List<String> resultPlayerCmds = playerCommands;
        List<String> resultConsoleCmds = consoleCommands;
        if (papi != null) {
            resultPlayerCmds = papi.setPlaceholders(player, papi.setBracketPlaceholders(player, playerCommands));
            resultConsoleCmds = papi.setPlaceholders(player, papi.setBracketPlaceholders(player, consoleCommands));
        }
        resultPlayerCmds.forEach(player::performCommand);
        resultConsoleCmds.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
    }

    public List<String> getConsoleCommands() {
        return List.copyOf(consoleCommands);
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    public List<String> getPlayerCommands() {
        return List.copyOf(playerCommands);
    }

    public void setPlayerCommands(List<String> playerCommands) {
        this.playerCommands = playerCommands;
    }
}
