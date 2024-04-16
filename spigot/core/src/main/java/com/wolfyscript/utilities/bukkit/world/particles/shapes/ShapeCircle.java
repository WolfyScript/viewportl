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

package com.wolfyscript.utilities.bukkit.world.particles.shapes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.util.Vector;

public class ShapeCircle extends Shape {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("circle");

    private final double radius;
    private int resolution;
    private Direction direction;
    @JsonIgnore
    private Function<Double, Vector> createVector;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeCircle() {
        this(1, 3, Direction.Y_AXIS);
    }

    public ShapeCircle(double radius, int resolution, Direction direction) {
        super(KEY);
        this.radius = radius;
        this.resolution = resolution;
        this.setDirection(direction);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        for (double i = 0; i <= Math.PI * 2; i += Math.PI / resolution) {
            drawVector.accept(createVector.apply(i));
        }
    }

    @JsonGetter
    public double getRadius() {
        return radius;
    }

    @JsonSetter
    private void setResolution(int resolution) {
        this.resolution = resolution;
    }

    @JsonGetter
    public int getResolution() {
        return resolution;
    }

    @JsonGetter
    public Direction getDirection() {
        return direction;
    }

    @JsonSetter
    private void setDirection(Direction direction) {
        this.direction = direction;
        createVector = switch (direction) {
            case X_AXIS -> (t) -> {
                double y = radius * Math.sin(t);
                double z = radius * Math.cos(t);
                return new Vector(0, y, z);
            };
            case Y_AXIS -> (t) -> {
                double x = radius * Math.cos(t);
                double z = radius * Math.sin(t);
                return new Vector(x, 0, z);
            };
            case Z_AXIS -> (t) -> {
                double x = radius * Math.cos(t);
                double y = radius * Math.sin(t);
                return new Vector(x, y, 0);
            };
        };
    }
}
