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
package com.wolfyscript.viewportl.commands

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GuiExampleCommand(private val core: WolfyCoreCommon) : Command("gui_example"), PluginIdentifiableCommand {
    init {
        setDescription("Opens the specified example GUI.")
    }

    override fun getPlugin(): Plugin {
        return core.wolfyUtils.plugin
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        if (args.isNotEmpty()) {
            // counter
            // ... tbd
            core.wolfyUtils.guiManager.createViewAndOpen(args[0], sender.uniqueId)
        } else {
            core.wolfyUtils.guiManager.createViewAndOpen("example_counter", sender.uniqueId)
        }
        return true
    }
}
