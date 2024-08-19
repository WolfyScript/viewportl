/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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
package com.wolfyscript.viewportl.spigot.commands

import com.wolfyscript.scafall.spigot.api.into
import com.wolfyscript.scafall.spigot.api.wrappers.wrap
import com.wolfyscript.viewportl.Viewportl
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class InputCommand(private val core: Viewportl) : Command("wui"), PluginIdentifiableCommand {
    init {
        usage = "/wui <input>"
        setDescription("Input for chat input actions")
    }

    override fun getPlugin(): Plugin {
        return core.scafall.corePlugin.into().plugin
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        core.guiManager.getViewManagersFor(sender.uniqueId)
            .map { runtime -> Pair(runtime, runtime.currentMenu) }
            .filter { pair -> pair.second != null && pair.second!!.onTextInput != null }
            .forEach { pair ->
                val runtime = pair.first
                val window = pair.second
                val text = java.lang.String.join(" ", *args).trim { it <= ' ' }
                Bukkit.getScheduler().runTask(core.scafall.corePlugin.into().plugin, Runnable {
                    window!!.onTextInput!!.run(sender.wrap(), runtime, text, args)
                    window.onTextInput = null
                    window.onTextInputTabComplete = null
                    runtime.open()
                })
            }
        return true
    }

    @Throws(IllegalArgumentException::class)
    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
        if (sender is Player) {
            return core.guiManager.getViewManagersFor(sender.uniqueId)
                .map { viewManager -> Pair(viewManager, viewManager.currentMenu) }
                .filter { pair -> pair.second != null && pair.second!!.onTextInputTabComplete != null }
                .findFirst()
                .map { pair ->
                    val runtime = pair.first
                    val window = pair.second
                    window!!.onTextInputTabComplete!!.apply(
                        sender.wrap(),
                        runtime,
                        java.lang.String.join(" ", *args).trim { it <= ' ' },
                        args
                    )
                }.orElse(listOf())
        }
        return super.tabComplete(sender, alias, args)
    }
}
