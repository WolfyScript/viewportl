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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This basic animator doesn't actually animate anything. It spawns the effect at the specified location (origin).
 */
public class AnimatorBasic extends Animator {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("basic");

    public AnimatorBasic() {
        super(KEY);
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        timer.increase();
        spawnParticle(effect, origin, player);
    }


}
