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

@Deprecated
public class AnimatorSphere extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("sphere");

    private final int radius;

    public AnimatorSphere() {
        this(1);
    }

    public AnimatorSphere(int radius) {
        super(KEY);
        this.radius = radius;
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        for (double i = 0; i <= 2 * Math.PI; i += Math.PI / 40) {
            double x = radius * Math.cos(i) * Math.sin(time);
            double y = radius * Math.cos(time);
            double z = radius * Math.sin(i) * Math.sin(time);
            origin.add(x, y, z);
            spawnParticle(effect, origin, player);
            origin.subtract(x, y, z);
        }
    }

    @Override
    public String toString() {
        return "SphereAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }
}
