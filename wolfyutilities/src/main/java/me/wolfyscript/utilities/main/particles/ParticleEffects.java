package me.wolfyscript.utilities.main.particles;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.SphereAnimator;
import org.bukkit.Material;
import org.bukkit.Particle;

public class ParticleEffects {

    public static void load() {
        ParticleEffect testParticleEffect = new ParticleEffect(Particle.FLAME, 10, 0, new SphereAnimator(false, 2));
        ParticleAnimation testParticleAnimation = new ParticleAnimation(Material.IRON_SWORD, "FLAME_CIRLCE", null, 1, 50, testParticleEffect);
        Registry.PARTICLE_EFFECTS.register(new NamespacedKey("wolfyutilities", "flame_circle"), testParticleEffect);
        Registry.PARTICLE_ANIMATIONS.register(new NamespacedKey("wolfyutilities", "flame_circle"), testParticleAnimation);

    }

}
