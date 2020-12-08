package me.wolfyscript.utilities.api.nms.v1_16_R1;

import me.wolfyscript.utilities.api.nms.BlockUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.block.INMSBrewingStand;
import me.wolfyscript.utilities.api.nms.v1_16_R1.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public class BlockUtilImpl extends BlockUtil {

    BlockUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public INMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand) {
        return new NMSBrewingStand(brewingStand);
    }
}
