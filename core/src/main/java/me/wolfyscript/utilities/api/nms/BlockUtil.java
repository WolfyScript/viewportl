package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public abstract class BlockUtil extends UtilComponent {

    protected BlockUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Get a more detailed API to edit the NMS BrewingStand values
     *
     * @param brewingStand The Bukkit BrewingStand Block
     * @return The NMSBrewingStand API
     */
    public abstract NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand);

}
