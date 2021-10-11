package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This animator draws a particle in a circle around the origin.
 */
public class AnimatorCircle extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("circle");

    private final int radius;

    public AnimatorCircle() {
        this(1);
    }

    public AnimatorCircle(int radius) {
        super(KEY);
        this.radius = radius;
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        double time = timer.increase();
        double x = radius * Math.cos(time);
        double z = radius * Math.sin(time);
        origin.add(x, 0, z);
        spawnParticle(effect, origin, player);
        origin.subtract(x, 0, z);
    }

    @Override
    public String toString() {
        return "CircleAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }
}
