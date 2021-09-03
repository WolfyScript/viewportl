package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AnimatorSphere extends Animator {

    private final int radius;

    public AnimatorSphere() {
        this(1);
    }

    public AnimatorSphere(int radius) {
        super(NamespacedKey.wolfyutilties("sphere"));
        this.radius = radius;
    }

    @Override
    public void draw(double time, ParticleEffect effect, Location origin, @Nullable Player player) {
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
