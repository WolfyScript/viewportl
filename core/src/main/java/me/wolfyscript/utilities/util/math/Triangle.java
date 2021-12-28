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

package me.wolfyscript.utilities.util.math;

import org.bukkit.util.Vector;

public class Triangle {

    private final Vector point1;
    private final Vector point2;
    private final Vector point3;

    public Triangle(Vector point1, Vector point2, Vector point3) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
    }

    public Vector getPoint1() {
        return point1;
    }

    public Vector getPoint2() {
        return point2;
    }

    public Vector getPoint3() {
        return point3;
    }
}
