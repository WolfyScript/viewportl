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
import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.spigot.api.into
import com.wolfyscript.scafall.spigot.init
import com.wolfyscript.viewportl.Viewportl
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin

private var viewportlInstance: SpigotViewportl? = null

val Viewportl.Companion.instance: Viewportl
    get() = viewportlInstance ?: throw IllegalStateException("Viewportl is not initialized.")

/**
 * Initiates the Viewportl library and loads all required data.
 *
 * When Scafall wasn't yet initiated this will [init Scafall][Scafall.Companion.init] using the passed [plugin]
 *
 */
fun Viewportl.Companion.init(plugin: JavaPlugin) {
    if (!ScafallProvider.registered()) {
        Scafall.init(plugin)
    }
    init()
}

/**
 * Initiates the Viewportl library and loads all required data.
 *
 * The [plugin] param requires [Scafall] to be initiated by default!
 */
fun Viewportl.Companion.init(plugin: PluginWrapper = ScafallProvider.get().corePlugin) {
    viewportlInstance = SpigotViewportl(plugin)
    viewportlInstance!!.init()

    Bukkit.getPluginManager().registerEvents(UnloadListener(), plugin.into().plugin)
}

private class UnloadListener : Listener {

    @EventHandler
    fun onViewportlDisabled(event: PluginDisableEvent) {
        viewportlInstance = null
    }

}