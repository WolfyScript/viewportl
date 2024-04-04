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
package com.wolfyscript.utilities

import com.fasterxml.jackson.databind.ObjectMapper
import com.wolfyscript.utilities.chat.Chat
import com.wolfyscript.utilities.platform.Platform
import com.wolfyscript.utilities.registry.Registries
import org.reflections.Reflections

/**
 * Represents the core instance of the WolfyUtils plugin.
 *
 */
interface WolfyCore {

    companion object {

        protected var INSTANCE: WolfyCore? = null

        internal val instance: WolfyCore
            get() {
                if (INSTANCE == null) throw IllegalStateException("Trying to access WolfyCore before it was initialised!")
                return INSTANCE!!
            }

    }

    val chat: Chat

    fun <M : ObjectMapper?> applyWolfyUtilsJsonMapperModules(mapper: M): M

    val wolfyUtils: WolfyUtils

    val reflections: Reflections

    val registries: Registries

    val platform: Platform

}
