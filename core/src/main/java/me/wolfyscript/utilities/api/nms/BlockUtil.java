package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public abstract class BlockUtil {

    private final NMSUtil nmsUtil;

    protected BlockUtil(NMSUtil nmsUtil) {
        this.nmsUtil = nmsUtil;
    }

    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }

    /**
     * Get a more detailed API to edit the NMS BrewingStand values
     *
     * @param brewingStand The Bukkit BrewingStand Block
     * @return The NMSBrewingStand API
     */
    public abstract NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand);

}
