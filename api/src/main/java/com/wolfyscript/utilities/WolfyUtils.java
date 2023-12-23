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

package com.wolfyscript.utilities;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.wolfyscript.utilities.chat.Chat;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.config.jackson.MapperUtil;
import com.wolfyscript.utilities.registry.Registries;
import java.util.logging.Logger;
import com.wolfyscript.utilities.language.LanguageAPI;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Represents a single API instance that is bound to a plugin or mod.
 *
 */
@JsonIncludeProperties()
public abstract class WolfyUtils {

    protected MapperUtil mapperUtil;

    protected WolfyUtils() {
        this.mapperUtil = new MapperUtil(this);
    }

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

    public abstract Logger getLogger();

    public abstract LanguageAPI getLanguageAPI();

    public abstract Chat getChat();

    public abstract Identifiers getIdentifiers();

    public abstract GuiAPIManager getGUIManager();

    public Registries getRegistries() {
        return getCore().getRegistries();
    }

    public MapperUtil getJacksonMapperUtil() {
        return mapperUtil;
    }

    public abstract void exportResource(String resourcePath, File destination, boolean replace);

    public abstract void exportResources(String resourceName, File dir, boolean replace, Pattern filePattern);
}
