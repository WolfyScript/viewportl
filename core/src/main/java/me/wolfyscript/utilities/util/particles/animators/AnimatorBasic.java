package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This basic animator doesn't actually animate anything. It spawns the effect at the specified location (origin).
 */
public class AnimatorBasic extends Animator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("basic");

    public AnimatorBasic() {
        super(KEY);
    }

    @Override
    public void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player) {
        timer.increase();
        spawnParticle(effect, origin, player);
    }


}
