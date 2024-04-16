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
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.util.Vector;

public class ShapeSquare extends Shape {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("square");

    private final double radius;
    private int pointsPerSide;
    private Direction direction;
    @JsonIgnore
    private Function<Double, Vector[]> createVector;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeSquare() {
        this(1, 3, Direction.Y_AXIS);
    }

    public ShapeSquare(double radius, int pointsPerSide, Direction direction) {
        super(KEY);
        this.radius = radius;
        this.pointsPerSide = pointsPerSide;
        this.setDirection(direction);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        double pointIncrease = radius * 2 / (pointsPerSide - 1);
        for(double i = 0; i <= radius * 2; i += pointIncrease) {
            Vector[] sides = createVector.apply(i);
            for (Vector side : sides) {
                drawVector.accept(side);
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

    @JsonGetter
    public Direction getDirection() {
        return direction;
    }

    @JsonSetter
    private void setDirection(Direction direction) {
        this.direction = direction;
        createVector = switch (direction) {
            case X_AXIS -> (t) -> new Vector[] {
                    new Vector(0, radius - t, radius),
                    new Vector(0, radius - t,  -radius),
                    new Vector(0, radius, radius - t),
                    new Vector(0, -radius, radius - t)
            };
            case Y_AXIS -> (t) -> new Vector[] {
                    new Vector(radius - t, 0, radius),
                    new Vector(radius - t, 0, -radius),
                    new Vector(radius, 0, radius - t),
                    new Vector(-radius, 0, radius - t)
            };
            case Z_AXIS -> (t) -> new Vector[] {
                    new Vector(radius - t, radius, 0),
                    new Vector(radius - t, -radius, 0),
                    new Vector(radius, radius - t, 0),
                    new Vector(-radius, radius - t, 0)
            };
        };
    }
}
