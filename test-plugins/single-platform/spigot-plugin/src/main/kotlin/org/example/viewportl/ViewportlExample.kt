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

package org.example.viewportl

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin
import org.example.viewportl.commands.GuiExampleCommand
import org.example.viewportl.common.gui.Counter
import org.example.viewportl.common.gui.CounterTextured
import org.example.viewportl.common.gui.NestedRouting
import org.example.viewportl.common.gui.StackEditor
import org.example.viewportl.common.gui.StackSlots
import java.util.*

class ViewportlExample : JavaPlugin() {

    override fun onEnable() {
        val viewportl = ScafallProvider.get().viewportl

        val manager = viewportl.guiManager
        Counter.register(manager)
        CounterTextured.register(manager)
        NestedRouting.register(manager)
        StackEditor.register(manager)
        StackSlots.register(manager)

        registerCommands()
    }

    private fun registerCommands() {
        registerDynamicCommands(
            GuiExampleCommand(this, ScafallProvider.get().viewportl)
        )
    }

    private fun registerDynamicCommands(vararg cmds: Command?) {
        val commandMap: CommandMap? = try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            commandMapField[Bukkit.getServer()] as CommandMap
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
            null
        }
        if (commandMap == null) {
            logger.severe("Failed to register Commands: Failed to access CommandMap!")
            return
        }
        for (cmd in cmds) {
            commandMap.register(name.lowercase(Locale.ROOT), cmd!!)
        }
    }

}