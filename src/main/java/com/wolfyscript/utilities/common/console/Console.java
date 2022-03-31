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

package com.wolfyscript.utilities.common.console;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wolfyscript.utilities.common.WolfyUtils;
import me.wolfyscript.utilities.api.language.LanguageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Console {

    private final WolfyUtils wolfyUtils;
    private final LanguageAPI languageAPI;
    private final Logger logger;

    public Console(WolfyUtils wolfyUtils, Logger logger) {
        this.wolfyUtils = wolfyUtils;
        this.languageAPI = wolfyUtils.getLanguageAPI();
        this.logger = logger;
    }

    public void log(Level level, String message) {
        logger.log(level, () -> languageAPI.replaceColoredKeys(message));
    }

    public void log(Level level, String message, String... replacements) {
        message = languageAPI.replaceKeys(message);
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("%([A-Z]*?)(_*?)%");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for (int i = 0; i < keys.size(); i++) {
            message = message.replace(keys.get(i), replacements[i]);
        }
        log(level, message);
    }

    public void log(Level level, String message, String[]... replacements) {
        if (replacements != null) {
            message = languageAPI.replaceKeys(message);
            for (String[] replace : replacements) {
                if (replace.length > 1) {
                    message = message.replaceAll(replace[0], replace[1]);
                }
            }
        }
        log(level, message);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void fine(String message) {
        log(Level.FINE, message);
    }

    public void finer(String message) {
        log(Level.FINER, message);
    }

    public void finest(String message) {
        log(Level.FINEST, message);
    }

    public void warn(String message) {
        log(Level.WARNING, message);
    }

    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    @JsonIgnore
    public Logger getLogger() {
        return logger;
    }

}
