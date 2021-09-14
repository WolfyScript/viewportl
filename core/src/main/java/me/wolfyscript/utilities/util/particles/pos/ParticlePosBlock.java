package me.wolfyscript.utilities.util.particles.pos;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class ParticlePosBlock extends ParticlePos {

    private final Block block;

    public ParticlePosBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public Location getLocation() {
        return block.getLocation();
    }
}
