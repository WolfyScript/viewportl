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
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.Shape;
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This animator draws a particle shape with the given direction and rotation.
 */
public class AnimatorShape extends Animator {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("shape");

    private final Shape shape;

    @JsonCreator
    public AnimatorShape(@JsonProperty("shape") Shape shape) {
        super(KEY);
        this.shape = shape;
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        shape.drawVectors(time, vec -> {
            origin.add(vec);
            spawnParticle(effect, origin, player);
            origin.subtract(vec);
        });
    }

    @Override
    public String toString() {
        return "AnimatorVectorPath{" +
                "shape=" + shape +
                '}';
    }
}
