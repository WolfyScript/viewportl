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

package com.wolfyscript.viewportl.spigot

import com.wolfyscript.scafall.PluginWrapper
import com.wolfyscript.scafall.spigot.api.into
import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.common.gui.GuiAPIManagerImpl
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.GroupImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.common.registry.CommonViewportlRegistries
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.spigot.commands.InputCommand
import com.wolfyscript.viewportl.spigot.gui.factories.GuiFactoryImpl
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent

class SpigotViewportl(private val plugin: PluginWrapper) : CommonViewportl(), Listener {

    override lateinit var guiManager: GuiAPIManager
    override lateinit var guiFactory: GuiFactory
    override lateinit var registries: ViewportlRegistries

    fun init() {
        guiManager = GuiAPIManagerImpl(this)
        guiFactory = GuiFactoryImpl()
        registries = CommonViewportlRegistries(this)

        // Register GUI things
        val guiComponentBuilders = registries.guiComponents
        guiComponentBuilders.register(ButtonImpl::class.java)
        guiComponentBuilders.register(SlotImpl::class.java)
        guiComponentBuilders.register(GroupImpl::class.java)
    }

    fun enable() {
        registerListeners()
        registerCommands()
    }

    fun unload() {

    }

    private fun registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, plugin.into().plugin)
    }

    private fun registerCommands() {
        registerDynamicCommands(
            InputCommand(this)
        )
    }

    private fun registerDynamicCommands(vararg cmds: Command?) {
        var commandMap: CommandMap? = null
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            commandMap = commandMapField[Bukkit.getServer()] as CommandMap
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        if (commandMap == null) {
            scafall.logger.error("Failed to register Commands: Failed to access CommandMap!")
            return
        }
        for (cmd in cmds) {
            commandMap.register("viewportl", cmd!!)
        }
    }

    @EventHandler
    private fun onViewportlDisabled(event: PluginDisableEvent) {

    }

}