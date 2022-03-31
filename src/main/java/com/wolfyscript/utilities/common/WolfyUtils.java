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

package com.wolfyscript.utilities.common;

import com.wolfyscript.utilities.common.chat.Chat;
import me.wolfyscript.utilities.api.language.LanguageAPI;

import java.io.File;

/**
 * Represents a single API instance that is bound to a plugin or mod.
 *
 */
public abstract class WolfyUtils {

    private static final String ENVIRONMENT = System.getProperties().getProperty("com.wolfyscript.env", "PROD");

    public static String getENVIRONMENT() {
        return ENVIRONMENT;
    }

    public static boolean isDevEnv() {
        return ENVIRONMENT.equalsIgnoreCase("DEV");
    }

    public static boolean isProdEnv() {
        return ENVIRONMENT.equalsIgnoreCase("PROD");
    }

    public abstract WolfyCore getCore();

    public abstract String getName();

    public abstract File getDataFolder();

    public abstract LanguageAPI getLanguageAPI();

    public abstract Chat getChat();




}
