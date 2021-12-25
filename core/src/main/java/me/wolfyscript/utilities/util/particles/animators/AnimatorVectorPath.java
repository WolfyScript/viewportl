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

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.math.MathUtil;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * This animator draws a particle shape with the given direction and rotation.
 */
public class AnimatorVectorPath extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("vector_path");

    private final Shape shape;
    private final Map<Double, Vector> path;
    private final boolean rotateToDirection;

    /**
     * Only used for Jackson deserialization.
     */
    AnimatorVectorPath() {
        this(new ShapeSquare());
    }

    public AnimatorVectorPath(@NotNull Shape shape) {
        this(shape, Map.of(0d, new Vector()));
    }

    public AnimatorVectorPath(@NotNull Shape shape, @NotNull Map<Double, Vector> path) {
        this(shape, path, true);
    }

    public AnimatorVectorPath(@NotNull Shape shape, @NotNull Map<Double, Vector> path, boolean rotateToDirection) {
        super(KEY);
        Preconditions.checkArgument(path != null && !path.isEmpty(), "Path cannot be null and must contain at least one vector!");
        this.shape = Objects.requireNonNull(shape, "Shape cannot be null!");
        this.path = path;
        this.rotateToDirection = rotateToDirection;
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        Vector vector = null;
        for (Double activationTime : path.keySet()) {
            if (time < activationTime) {
                break;
            }
            vector = path.get(activationTime);
        }
        if (rotateToDirection && vector != null) {
            //Calculate the direction vector
            Vector nextVec = origin.toVector().add(vector); //The next vector to draw
            Vector direction = origin.toVector().subtract(nextVec).normalize(); //get the direction vector of the current vector to the next vector.
            Location dir = new Location(origin.getWorld(), 0, 0, 0).setDirection(direction); //Create a location, so it calculates the angles for us :)

            //Calculate the rotation cos & sin
            var angleRad = new Vector(Math.toRadians(dir.getPitch()), Math.toRadians(dir.getYaw()), 0);
            double xAxisCos = Math.cos(angleRad.getX()); // getting the cos value for the pitch.
            double xAxisSin = Math.sin(angleRad.getX()); // getting the sin value for the pitch.
            double yAxisCos = Math.cos(-angleRad.getY()); // getting the cos value for the yaw.
            double yAxisSin = Math.sin(-angleRad.getY()); // getting the sin value for the yaw.

            shape.drawVectors(time, vec -> {
                MathUtil.rotateAroundAxisX(vec, xAxisCos, xAxisSin);
                MathUtil.rotateAroundAxisY(vec, yAxisCos, yAxisSin);
                origin.add(vec);
                spawnParticle(effect, origin, player);
                origin.subtract(vec);
            });
        } else {
            shape.drawVectors(time, vec -> {
                origin.add(vec);
                spawnParticle(effect, origin, player);
                origin.subtract(vec);
            });
        }
        if (vector != null) {
            origin.add(vector); //Add vector to origin
        }
    }

    @Override
    public String toString() {
        return "AnimatorVectorPath{" +
                "shape=" + shape +
                ", path=" + path +
                '}';
    }
}
