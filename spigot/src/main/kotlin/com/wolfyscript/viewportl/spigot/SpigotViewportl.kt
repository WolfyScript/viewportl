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

import com.fasterxml.jackson.databind.module.SimpleModule
import com.wolfyscript.scafall.spigot.api.into
import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.common.gui.GuiAPIManagerImpl
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.ComponentGroupImpl
import com.wolfyscript.viewportl.common.gui.components.OutletImpl
import com.wolfyscript.viewportl.common.gui.components.StackInputSlotImpl
import com.wolfyscript.viewportl.common.registry.SpigotViewportlRegistries
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.components.ButtonIcon
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.spigot.commands.InputCommand
import com.wolfyscript.viewportl.spigot.gui.GuiFactoryImpl
import com.wolfyscript.viewportl.spigot.gui.interaction.GUIInventoryListener
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap

class SpigotViewportl : CommonViewportl() {

    override val guiManager: GuiAPIManager = GuiAPIManagerImpl(this)
    override val guiFactory: GuiFactory = GuiFactoryImpl()
    override val registries: ViewportlRegistries = SpigotViewportlRegistries(this)

    fun init() {
        val module = SimpleModule()
        // Register implementation types to use for de/serialization
        module.addAbstractTypeMapping(ButtonIcon::class.java, ButtonImpl.DynamicIcon::class.java)
        // TODO: register module

        // Register GUI things
        val guiComponentBuilders = registries.guiComponents
        guiComponentBuilders.register(ButtonImpl::class.java)
        guiComponentBuilders.register(StackInputSlotImpl::class.java)
        guiComponentBuilders.register(ComponentGroupImpl::class.java)
        guiComponentBuilders.register(OutletImpl::class.java)

        registerListeners()
        registerCommands()
    }

    private fun registerListeners() {
        Bukkit.getPluginManager().registerEvents(GUIInventoryListener(), scafall.corePlugin.into().plugin)
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

}