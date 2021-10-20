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

package me.wolfyscript.utilities.main.particles;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.AnimatorBasic;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import org.bukkit.Particle;

public class ParticleEffects {

    public static void load() {
        var testParticleEffect = new ParticleEffect(Particle.FLAME);
        testParticleEffect.setKey(NamespacedKey.wolfyutilties("flame_sphere"));
        testParticleEffect.setTimeSupplier(new TimerLinear(0.5, 70));
        testParticleEffect.setAnimator(new AnimatorBasic());
        Registry.PARTICLE_EFFECTS.register(testParticleEffect);

        /*
        var testParticleAnimation = new ParticleAnimation(Material.IRON_SWORD, "FLAME_CIRLCE", null, 1, 50, testParticleEffect);
        Registry.PARTICLE_ANIMATIONS.register(new NamespacedKey("wolfyutilities", "flame_circle"), testParticleAnimation);
         */

    }

}
