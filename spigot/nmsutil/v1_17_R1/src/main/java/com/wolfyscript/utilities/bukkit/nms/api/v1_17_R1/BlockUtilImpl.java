package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1;

import com.wolfyscript.utilities.bukkit.nms.api.BlockUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public class BlockUtilImpl extends BlockUtil {

    BlockUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand) {
        return new com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1.block.NMSBrewingStand(brewingStand);
    }
}
