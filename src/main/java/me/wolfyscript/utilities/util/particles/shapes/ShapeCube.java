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

package me.wolfyscript.utilities.util.particles.shapes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class ShapeCube extends Shape {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("cube");

    private final double radius;
    private int pointsPerSide;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeCube() {
        this(1, 3);
    }

    public ShapeCube(double radius, int pointsPerSide) {
        super(KEY);
        this.radius = radius;
        this.pointsPerSide = pointsPerSide;
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        double pointIncrease = radius * 2 / (pointsPerSide - 1);
        var corner1 = new Vector(radius, radius, radius);
        var corner2 = new Vector(-radius, -radius, -radius);

        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x+=pointIncrease) {
            for (double y = minY; y <= maxY; y+=pointIncrease) {
                for (double z = minZ; z <= maxZ; z+=pointIncrease) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        drawVector.accept(new Vector(x, y, z));
                    }
                }
            }
        }
    }

    @JsonGetter
    public double getRadius() {
        return radius;
    }

    @JsonSetter
    private void setPointsPerSide(int pointsPerSide) {
        Preconditions.checkArgument(pointsPerSide > 1, "Points per side must be at least 2!");
        this.pointsPerSide = pointsPerSide;
    }

    @JsonGetter
    public int getPointsPerSide() {
        return pointsPerSide;
    }

}
