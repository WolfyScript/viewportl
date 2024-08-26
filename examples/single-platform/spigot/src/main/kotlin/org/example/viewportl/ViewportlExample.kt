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

import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.spigot.initOnSpigot
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.spigot.init
import com.wolfyscript.viewportl.spigot.instance
import com.wolfyscript.viewportl.spigot.unload
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin
import org.example.viewportl.commands.GuiExampleCommand
import org.example.viewportl.guis.CounterExampleKotlin
import org.example.viewportl.guis.StackEditorExampleKotlin
import org.example.viewportl.guis.StackSlotsExampleKotlin
import java.util.*

class ViewportlExample : JavaPlugin() {

    override fun onLoad() {
        Scafall.initOnSpigot(this) // initiate shadowed scafall implementation

        // From this point onward you can get the instances via
        val scafall = ScafallProvider.get()
    }

    override fun onEnable() {
        Viewportl.init() // Init the viewportl instance (uses the scafall core plugin by default)
        // From this point onward you can get the instances via
        val viewportl = Viewportl.instance

        // then register the guis
        val manager = viewportl.guiManager
        CounterExampleKotlin.registerExampleCounter(manager)
        StackEditorExampleKotlin.registerStackEditor(manager)
        StackSlotsExampleKotlin.registerStackSlotsExample(manager)

        registerCommands()
    }

    override fun onDisable() {
        Viewportl.unload() // When the plugin is shutdown, viewportl needs to be gracefully unloaded
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