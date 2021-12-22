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
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * This animator draws a particle in a circle around the origin.
 */
public class AnimatorCircle extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("circle");

    private final int radius;
    private Vector angleDeg;

    @JsonIgnore
    private Vector angleRad;
    @JsonIgnore
    private double xAxisCos; // getting the cos value for the pitch.
    @JsonIgnore
    private double xAxisSin; // getting the sin value for the pitch.
    @JsonIgnore
    private double yAxisCos; // getting the cos value for the yaw.
    @JsonIgnore
    private double yAxisSin; // getting the sin value for the yaw.
    @JsonIgnore
    private double zAxisCos; // getting the cos value for the roll.
    @JsonIgnore
    private double zAxisSin; // getting the sin value for the roll.

    public AnimatorCircle() {
        this(1);
    }

    public AnimatorCircle(int radius) {
        this(radius, new Vector(0, 0, 0));
    }

    public AnimatorCircle(int radius, Vector angleDeg) {
        super(KEY);
        this.radius = radius;
        setAngleDeg(angleDeg);
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        double x = radius * Math.cos(time);
        double z = radius * Math.sin(time);

        Vector vec = new Vector(x, 0, z);
        rotateAroundAxisX(vec, xAxisCos, xAxisSin);
        rotateAroundAxisY(vec, yAxisCos, yAxisSin);
        rotateAroundAxisZ(vec, zAxisCos, zAxisSin);

        origin.add(vec);
        spawnParticle(effect, origin, player);
        origin.subtract(vec);
    }

    @JsonSetter("angle")
    private void setAngleDeg(Vector angleDeg) {
        this.angleDeg = angleDeg;
        this.angleRad = new Vector(Math.toRadians(this.angleDeg.getX()), Math.toRadians(this.angleDeg.getY()), Math.toRadians(this.angleDeg.getZ()));
        this.xAxisCos = Math.cos(angleRad.getX()); // getting the cos value for the pitch.
        this.xAxisSin = Math.sin(angleRad.getX()); // getting the sin value for the pitch.
        this.yAxisCos = Math.cos(-angleRad.getY()); // getting the cos value for the yaw.
        this.yAxisSin = Math.sin(-angleRad.getY()); // getting the sin value for the yaw.
        this.zAxisCos = Math.cos(angleRad.getZ()); // getting the cos value for the roll.
        this.zAxisSin = Math.sin(angleRad.getZ()); // getting the sin value for the roll.
    }

    @JsonGetter("angle")
    private Vector getAngleDeg() {
        return angleDeg;
    }

    @Override
    public String toString() {
        return "CircleAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }
}
