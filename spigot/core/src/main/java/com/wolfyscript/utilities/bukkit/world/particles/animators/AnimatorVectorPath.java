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

package com.wolfyscript.utilities.bukkit.world.particles.animators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.math.MathUtil;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.Shape;
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This animator draws a particle shape with the given direction and rotation.
 */
public class AnimatorVectorPath extends Animator {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("vector_path");

    private final Shape shape;
    private final Map<Double, Vector> path;
    private final boolean rotateToDirection;

    @JsonCreator
    public AnimatorVectorPath(@JsonProperty("shape") @NotNull Shape shape) {
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
        double previousTime = timer.getTime();
        double time = timer.increase();
        Vector vector = getVector(time);
        if (rotateToDirection) {
            //Calculate the direction vector
            Vector prevVec = origin.toVector().add(getVector(previousTime));
            Vector direction = prevVec.subtract(origin.toVector().add(vector)).normalize(); //get the direction vector of the previous vector to the current vector.
            if (!Double.isNaN(direction.getX()) && !Double.isNaN(direction.getY()) && !Double.isNaN(direction.getZ())) {
                Location dir = new Location(origin.getWorld(), 0, 0, 0).setDirection(direction); //Create a location, so it calculates the angles for us :)
                //Calculate the rotation cos & sin
                var angleRad = new Vector(Math.toRadians(dir.getPitch()), Math.toRadians(dir.getYaw()), 0);
                double xAxisCos = Math.cos(angleRad.getX()); // getting the cos value for the pitch.
                double xAxisSin = Math.sin(angleRad.getX()); // getting the sin value for the pitch.
                double yAxisCos = Math.cos(-angleRad.getY()); // getting the cos value for the yaw.
                double yAxisSin = Math.sin(-angleRad.getY()); // getting the sin value for the yaw.

                origin.add(vector);
                shape.drawVectors(time, vec -> {
                    MathUtil.rotateAroundAxisX(vec, xAxisCos, xAxisSin);
                    MathUtil.rotateAroundAxisY(vec, yAxisCos, yAxisSin);
                    origin.add(vec);
                    spawnParticle(effect, origin, player);
                    origin.subtract(vec);
                });
                origin.subtract(vector);
                return;
            }
        }
        origin.add(vector); //Add vector to origin
        shape.drawVectors(time, vec -> {
            origin.add(vec);
            spawnParticle(effect, origin, player);
            origin.subtract(vec);
        });
        origin.subtract(vector);
    }

    @NotNull
    private Vector getVector(double time) {
        Iterator<Map.Entry<Double, Vector>> iterator = path.entrySet().iterator();
        Vector vector = new Vector();
        while(iterator.hasNext()) {
            Map.Entry<Double, Vector> entry = iterator.next();
            double activationTime = entry.getKey();
            if (activationTime > time) {
                continue;
            }
            vector = entry.getValue();
        }
        return vector;
    }

    @Override
    public String toString() {
        return "AnimatorVectorPath{" +
                "shape=" + shape +
                ", path=" + path +
                '}';
    }
}
