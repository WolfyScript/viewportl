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

import com.fasterxml.jackson.annotation.JsonIncludeProperties
import com.wolfyscript.scafall.config.jackson.MapperUtil
import com.wolfyscript.utilities.chat.Chat
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.utilities.language.Translations
import java.io.File
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * Represents a single API instance that is bound to a plugin or mod.
 *
 */
@JsonIncludeProperties
abstract class WolfyUtils protected constructor() {
    var jacksonMapperUtil: MapperUtil = MapperUtil(this)

    abstract val core: WolfyCore

    abstract val logger: Logger

    abstract val translations: Translations

    abstract val chat: Chat

    abstract val guiManager: GuiAPIManager

    abstract fun exportResource(resourcePath: String, destination: File, replace: Boolean)

    abstract fun exportResources(resourceName: String, dir: File, replace: Boolean, filePattern: Pattern)

    companion object {
        private val environment: String = System.getProperties().getProperty("com.wolfyscript.env", "PROD")

        val isDevEnv: Boolean
            get() = environment.equals("DEV", ignoreCase = true)

        val isProdEnv: Boolean
            get() = environment.equals("PROD", ignoreCase = true)
    }
}
