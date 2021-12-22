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
public class AnimatorCircleComplex extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("circle_complex");

    private final double radius;
    private final int circlePoints;
    private final Vector angleMultiplier;
    private Direction direction;
    private Function<Double, Vector> createVector;

    public AnimatorCircleComplex() {
        this(1);
    }

    public AnimatorCircleComplex(double radius) {
        this(radius, new Vector(0, 0, 0));
    }

    public AnimatorCircleComplex(double radius, Vector angleMultiplier) {
        this(radius, Direction.Y_AXIS, angleMultiplier);
    }

    public AnimatorCircleComplex(double radius, Direction direction, Vector angleMultiplier) {
        this(radius, 5, direction, angleMultiplier);
    }

    public AnimatorCircleComplex(double radius, int circlePoints, Direction direction, Vector angleMultiplier) {
        super(KEY);
        this.radius = radius;
        setDirection(direction);
        this.circlePoints = circlePoints;
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

        for (double i = 0; i <= Math.PI * 2; i += Math.PI / circlePoints) {
            var vec = createVector.apply(i);
            rotateAroundAxisX(vec, xAxisCos, xAxisSin);
            rotateAroundAxisY(vec, yAxisCos, yAxisSin);
            rotateAroundAxisZ(vec, zAxisCos, zAxisSin);

            origin.add(vec);
            spawnParticle(effect, origin, player);
            origin.subtract(vec);
        }
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
