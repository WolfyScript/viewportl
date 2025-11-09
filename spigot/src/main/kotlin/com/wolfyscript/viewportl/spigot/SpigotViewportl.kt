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

import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.common.gui.ViewportlUIRuntimeImpl
import com.wolfyscript.viewportl.common.registry.CommonViewportlRegistries
import com.wolfyscript.viewportl.gui.ViewportlUIRuntime
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.spigot.gui.factories.SpigotGuiFactory
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class SpigotViewportl(private val plugin: Plugin) : CommonViewportl(), Listener {

    override val guiManager: ViewportlUIRuntime = ViewportlUIRuntimeImpl(this)
    override val guiFactory: GuiFactory = SpigotGuiFactory(plugin)
    override val registries: ViewportlRegistries = CommonViewportlRegistries(this)

    override fun onInit() {

    }

    fun initServer() {
        server = SpigotViewportlServer(this, plugin)
    }

}