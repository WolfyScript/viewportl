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

package com.wolfyscript.utilities.versioning;

import java.util.Comparator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class MinecraftVersion implements Comparable<MinecraftVersion> {

    private final int major;
    private final int minor;
    private final int patch;

    private MinecraftVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static MinecraftVersion parse(String version) {
        String[] versionParts = version.split("\\.");
        int[] numbers = new int[3];
        if (versionParts.length > 0) {
            for (int i = 0; i < Math.min(numbers.length, versionParts.length); ++i) {
                numbers[i] = Integer.parseInt(versionParts[i].trim());
            }
            return of(numbers[0], numbers[1], numbers[2]);
        }
        throw new IllegalStateException("Couldn't parse MC version: " + version);
    }

    public static MinecraftVersion of(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getVersion() {
        return String.format("%s.%s.%s", major, minor, patch);
    }

    public boolean isAfter(MinecraftVersion other) {
        return compareTo(other) > 0;
    }

    public boolean isAfterOrEq(MinecraftVersion other) {
        return compareTo(other) >= 0;
    }

    public boolean isBefore(MinecraftVersion other) {
        return compareTo(other) < 0;
    }

    public boolean isBeforeOrEq(MinecraftVersion other) {
        return compareTo(other) <= 0;
    }

    public boolean isBetween(MinecraftVersion o1, MinecraftVersion o2) {
        return isAfterOrEq(o1) && isBeforeOrEq(o2) || isBeforeOrEq(o1) && isAfterOrEq(o2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinecraftVersion that = (MinecraftVersion) o;
        return major == that.major && minor == that.minor && patch == that.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public int compareTo(@NotNull MinecraftVersion that) {
        return Comparator.nullsFirst(Comparator.comparingInt(MinecraftVersion::getMajor).thenComparingInt(MinecraftVersion::getMinor).thenComparingInt(MinecraftVersion::getPatch)).compare(this, that);
    }

    @Override
    public String toString() {
        return "MinecraftVersion{" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                '}';
    }
}
