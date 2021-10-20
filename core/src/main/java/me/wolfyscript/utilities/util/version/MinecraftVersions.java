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

package me.wolfyscript.utilities.util.version;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersions {

    private MinecraftVersions() {
    }

    public static final MinecraftVersion v1_17 = MinecraftVersion.parse("1.17");
    public static final MinecraftVersion v1_16 = MinecraftVersion.parse("1.16");
    public static final MinecraftVersion v1_15 = MinecraftVersion.parse("1.15");
    public static final MinecraftVersion v1_14 = MinecraftVersion.parse("1.14");
    public static final MinecraftVersion v1_13 = MinecraftVersion.parse("1.13");

    static final MinecraftVersion RUNTIME_VERSION;

    static {
        Matcher version = Pattern.compile("\\(MC: ([0-9].*)\\)").matcher(Bukkit.getVersion());
        if (version.find() && version.group(1) != null) {
            RUNTIME_VERSION = MinecraftVersion.parse(version.group(1));
        } else {
            throw new IllegalStateException("Cannot parse version String '" + Bukkit.getVersion() + "'");
        }
    }

}
