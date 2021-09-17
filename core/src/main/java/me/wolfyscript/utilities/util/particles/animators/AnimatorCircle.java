package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
    public void draw(double time, ParticleEffect effect, Location origin, @Nullable Player player) {
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
