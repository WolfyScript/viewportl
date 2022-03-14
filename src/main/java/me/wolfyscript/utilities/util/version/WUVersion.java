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

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class WUVersion implements Comparable<WUVersion> {

    private final int major;
    private final int minor;
    private final int api;
    private final int patch;

    private WUVersion(int major, int minor, int api, int patch) {
        this.major = major;
        this.minor = minor;
        this.api = api;
        this.patch = patch;
    }

    public static WUVersion parse(String version) {
        if(version.contains("-")) {
            version = version.split("-")[0];
        }
        String[] versionParts = version.split("\\.");
        int[] numbers = new int[4];
        if (versionParts.length > 0) {
            for (int i = 0; i < Math.min(numbers.length, versionParts.length); ++i) {
                numbers[i] = Integer.parseInt(versionParts[i].trim());
            }
            return of(numbers[0], numbers[1], numbers[2], numbers[3]);
        }
        throw new IllegalStateException("Couldn't parse MC version: " + version);
    }

    public static WUVersion of(int major, int minor, int api, int patch) {
        return new WUVersion(major, minor, api, patch);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getApi() {
        return api;
    }

    public int getPatch() {
        return patch;
    }

    public String getVersion() {
        return String.format("%s.%s.%s.%s", major, minor, api, patch);
    }

    public boolean isAfter(WUVersion other) {
        return compareTo(other) > 0;
    }

    public boolean isAfterOrEq(WUVersion other) {
        return compareTo(other) >= 0;
    }

    public boolean isBefore(WUVersion other) {
        return compareTo(other) < 0;
    }

    public boolean isBeforeOrEq(WUVersion other) {
        return compareTo(other) <= 0;
    }

    public boolean isBetween(WUVersion o1, WUVersion o2) {
        return isAfterOrEq(o1) && isBeforeOrEq(o2) || isBeforeOrEq(o1) && isAfterOrEq(o2);
    }

    @Override
    public int compareTo(@NotNull WUVersion that) {
        return Comparator.nullsFirst(Comparator.comparingInt(WUVersion::getMajor).thenComparingInt(WUVersion::getMinor).thenComparingInt(WUVersion::getApi).thenComparingInt(WUVersion::getPatch)).compare(this, that);
    }
}
