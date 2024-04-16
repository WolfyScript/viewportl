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

package com.wolfyscript.utilities.bukkit.compatibility;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.nms.ServerProperties;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class CompatibilityManagerBukkit implements CompatibilityManager {

    private static final Map<String, Boolean> classes = new HashMap<>();
    private final WolfyCoreCommon core;
    private final PluginsBukkit pluginsBukkit;
    private boolean has1_20Features = false;

    public CompatibilityManagerBukkit(WolfyCoreCommon core) {
        this.core = core;
        this.pluginsBukkit = new PluginsBukkit(core);
    }

    public void init() {
        pluginsBukkit.init();
        Properties properties = ServerProperties.get();
        has1_20Features = ServerVersion.getVersion().isAfterOrEq(MinecraftVersion.of(1, 20, 0));
        // If the version is already 1.20 or later, then it has 1.20 features!
        if (!has1_20Features && ServerVersion.getVersion().isAfterOrEq(MinecraftVersion.of(1, 19, 4))) {
            String initialEnabledDataPacks = properties.getProperty("initial-enabled-packs", "vanilla");
            for (String s : initialEnabledDataPacks.split(",")) {
                if (s.equalsIgnoreCase("update_1_20")) {
                    has1_20Features = true;
                    break;
                }
            }
        }
    }

    public boolean has1_20Features() {
        return has1_20Features;
    }

    public Plugins getPlugins() {
        return pluginsBukkit;
    }

    /**
     * Check if the specific class exists.
     *
     * @param path The path to the class to check for.
     * @return If the class exists.
     */
    public static boolean hasClass(String path) {
        if (classes.containsKey(path)) {
            return classes.get(path);
        }
        try {
            Class.forName(path);
            classes.put(path, true);
            return true;
        } catch (Exception e) {
            classes.put(path, false);
            return false;
        }
    }
}
