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

package me.wolfyscript.utilities.util.particles.animators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.util.Vector;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ShapeSphere extends Shape {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("sphere");

    private final double radius;
    private int resolution;
    private Direction direction;
    @JsonIgnore
    private BiFunction<Double, Double, Vector> createVector;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeSphere() {
        this(1, 3, Direction.Y_AXIS);
    }

    public ShapeSphere(double radius, int resolution, Direction direction) {
        super(KEY);
        this.radius = radius;
        this.resolution = resolution;
        this.setDirection(direction);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        for (double i = 0; i <= 2 * Math.PI; i += Math.PI / resolution) {
            for (double j = 0; j <= Math.PI * 2; j += Math.PI / resolution) {
                drawVector.accept(createVector.apply(j, i));
            }
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
            case X_AXIS -> (i, j) -> {
                double x = radius * Math.cos(j);
                double y = radius * Math.sin(i) * Math.sin(j);
                double z = radius * Math.cos(i) * Math.sin(j);
                return new Vector(x, y, z);
            };
            case Y_AXIS -> (i, j) -> {
                double x = radius * Math.cos(i) * Math.sin(j);
                double y = radius * Math.cos(j);
                double z = radius * Math.sin(i) * Math.sin(j);
                return new Vector(x, y, z);
            };
            case Z_AXIS -> (i, j) -> {
                double x = radius * Math.cos(i) * Math.sin(j);
                double y = radius * Math.sin(i) * Math.sin(j);
                double z = radius * Math.cos(j);
                return new Vector(x, y, z);
            };
        };
    }
}
