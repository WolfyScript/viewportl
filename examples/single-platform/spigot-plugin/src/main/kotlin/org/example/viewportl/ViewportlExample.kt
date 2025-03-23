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
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.spigot.init
import com.wolfyscript.viewportl.spigot.instance
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin
import org.example.viewportl.commands.GuiExampleCommand
import org.example.viewportl.common.gui.CounterExampleKotlin
import org.example.viewportl.common.gui.NestedRoutingExampleKotlin
import org.example.viewportl.common.gui.StackEditorExampleKotlin
import org.example.viewportl.common.gui.StackSlotsExampleKotlin
import java.util.*

class ViewportlExample : JavaPlugin() {

    override fun onEnable() {
        Viewportl.init(this) // Init the viewportl instance (& Scafall if not yet initiated!)

        // From this point onward you can get the instances via
        val viewportl = Viewportl.instance
        val scafall = ScafallProvider.get()

        // then register the guis
        val manager = viewportl.guiManager
        CounterExampleKotlin.register(manager)
        NestedRoutingExampleKotlin.register(manager)
        StackEditorExampleKotlin.register(manager)
        StackSlotsExampleKotlin.register(manager)

        registerCommands()
    }

    private fun registerCommands() {
        registerDynamicCommands(
            GuiExampleCommand(Viewportl.instance)
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