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
import me.wolfyscript.utilities.compatibility.plugins.PlaceholderAPIIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionCommand extends Action<ActionDataPlayer> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("command");

    private final PlaceholderAPIIntegration papi;
    private List<String> playerCommands;
    private List<String> consoleCommands;

    public ActionCommand() {
        super(KEY, ActionDataPlayer.class);
        papi = WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().getIntegration(PlaceholderAPIIntegration.NAME, PlaceholderAPIIntegration.class);
    }

    @Override
    public void execute(WolfyUtilCore core, ActionDataPlayer data) {
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
