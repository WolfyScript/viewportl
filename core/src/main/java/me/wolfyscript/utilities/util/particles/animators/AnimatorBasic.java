package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AnimatorBasic extends Animator {

    public AnimatorBasic() {
        super(NamespacedKey.wolfyutilties("basic"));
    }

    @Override
    public void draw(double time, ParticleEffect effect, Location origin, @Nullable Player player) {
        spawnParticle(effect, origin, player);
    }


}
