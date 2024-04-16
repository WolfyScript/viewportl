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

package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GuiExampleCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreCommon core;

    public GuiExampleCommand(WolfyCoreCommon core) {
        super("gui_example");
        this.core = core;
        setDescription("Opens the specified example GUI.");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core.getWolfyUtils().getPlugin();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length > 0) {
            // counter
            // ... tbd
            core.getWolfyUtils().getGuiManager().createViewAndOpen(args[0], player.getUniqueId());
        } else {
            core.getWolfyUtils().getGuiManager().createViewAndOpen("example_counter", player.getUniqueId());
        }
        return true;
    }

}
