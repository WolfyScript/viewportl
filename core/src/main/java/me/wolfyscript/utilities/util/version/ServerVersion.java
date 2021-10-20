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

public class ServerVersion {

    private static WUVersion wolfyUtilitiesVersion;

    public static MinecraftVersion getVersion() {
        return MinecraftVersions.RUNTIME_VERSION;
    }

    private ServerVersion() {
    }

    public static WUVersion getWUVersion() {
        return wolfyUtilitiesVersion;
    }

    public static void setWUVersion(String version) {
        if (wolfyUtilitiesVersion == null) {
            wolfyUtilitiesVersion = WUVersion.parse(version);
        }
    }

    public static boolean isAfter(MinecraftVersion other) {
        return MinecraftVersions.RUNTIME_VERSION.compareTo(other) > 0;
    }

    public static boolean isAfterOrEq(MinecraftVersion other) {
        return MinecraftVersions.RUNTIME_VERSION.compareTo(other) >= 0;
    }

    public static boolean isBefore(MinecraftVersion other) {
        return MinecraftVersions.RUNTIME_VERSION.compareTo(other) < 0;
    }

    public static boolean isBeforeOrEq(MinecraftVersion other) {
        return MinecraftVersions.RUNTIME_VERSION.compareTo(other) <= 0;
    }

    public static boolean isBetween(MinecraftVersion o1, MinecraftVersion o2) {
        return isAfterOrEq(o1) && isBeforeOrEq(o2) || isBeforeOrEq(o1) && isAfterOrEq(o2);
    }

    public static boolean equals(MinecraftVersion other) {
        return MinecraftVersions.RUNTIME_VERSION.getMajor() == other.getMajor() && MinecraftVersions.RUNTIME_VERSION.getMinor() == other.getMinor() && MinecraftVersions.RUNTIME_VERSION.getPatch() == other.getPatch();
    }

}
