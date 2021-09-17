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
