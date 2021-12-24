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

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This animator draws a particle shape with the given direction and rotation.
 */
public class AnimatorShape extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("shape");

    private final Shape shape;

    public AnimatorShape() {
        this(new ShapeCircle());
    }

    public AnimatorShape(Shape shape) {
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
