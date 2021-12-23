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

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This animator draws a particle in a circle around the origin.
 */
public class AnimatorSquare extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("square");

    private final double radius;
    private int pointsPerSide;
    private final Vector angleMultiplier;
    private Direction direction;
    private Function<Double, Vector[]> createVector;

    public AnimatorSquare() {
        this(1);
    }

    public AnimatorSquare(double radius) {
        this(radius, new Vector(0, 0, 0));
    }

    public AnimatorSquare(double radius, Vector angleMultiplier) {
        this(radius, Direction.Y_AXIS, angleMultiplier);
    }

    public AnimatorSquare(double radius, Direction direction, Vector angleMultiplier) {
        this(radius, 5, direction, angleMultiplier);
    }

    public AnimatorSquare(double radius, int pointsPerSide, Direction direction, Vector angleMultiplier) {
        super(KEY);
        this.radius = radius;
        setDirection(direction);
        setPointsPerSide(pointsPerSide);
        this.angleMultiplier = angleMultiplier;
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        var rotation = new Vector(time * angleMultiplier.getX(), time * angleMultiplier.getY(), time * angleMultiplier.getZ());
        var angleRad = new Vector(Math.toRadians(rotation.getX()), Math.toRadians(rotation.getY()), Math.toRadians(rotation.getZ()));
        double xAxisCos = Math.cos(angleRad.getX()); // getting the cos value for the pitch.
        double xAxisSin = Math.sin(angleRad.getX()); // getting the sin value for the pitch.
        double yAxisCos = Math.cos(-angleRad.getY()); // getting the cos value for the yaw.
        double yAxisSin = Math.sin(-angleRad.getY()); // getting the sin value for the yaw.
        double zAxisCos = Math.cos(angleRad.getZ()); // getting the cos value for the roll.
        double zAxisSin = Math.sin(angleRad.getZ()); // getting the sin value for the roll.

        double pointIncrease = radius * 2 / (pointsPerSide - 1);
        for(double i = 0; i <= radius * 2; i += pointIncrease) {
            Vector[] sides = createVector.apply(i);
            for (int v = 0; v < sides.length; v++) {
                var vec = sides[v];
                rotateAroundAxisX(vec, xAxisCos, xAxisSin);
                rotateAroundAxisY(vec, yAxisCos, yAxisSin);
                rotateAroundAxisZ(vec, zAxisCos, zAxisSin);
                origin.add(vec);
                spawnParticle(effect, origin, player);
                origin.subtract(vec);
            }
        }
    }

    @JsonSetter
    public void setPointsPerSide(int pointsPerSide) {
        Preconditions.checkArgument(pointsPerSide > 1, "Points per side must be at least 2!");
        this.pointsPerSide = pointsPerSide;
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

    @Override
    public String toString() {
        return "CircleAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }

    enum Direction {
        X_AXIS, Y_AXIS, Z_AXIS
    }
}
