package me.wolfyscript.utilities.main.particles;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.CircleAnimator;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.Collections;

public class ParticleEffects {

    public static void load() {
        ParticleEffect testParticleEffect = new ParticleEffect(Particle.FLAME, 10, 0.00005, new CircleAnimator(false, 1));
        ParticleAnimation testParticleAnimation = new ParticleAnimation(Material.IRON_SWORD, "FLAME_CIRLCE", new ArrayList<>(), Collections.singletonList(testParticleEffect), 1, 3);
        Registry.PARTICLE_EFFECTS.register(new NamespacedKey("wolfyutilities", "flame_circle"), testParticleEffect);
        Registry.PARTICLE_ANIMATIONS.register(new NamespacedKey("wolfyutilities", "flame_circle"), testParticleAnimation);

    }

}
