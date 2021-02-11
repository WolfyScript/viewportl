package me.wolfyscript.utilities.api.nms.v1_15_R1;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import org.bukkit.plugin.Plugin;

public class NMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the {@link Plugin} parameter.
     *
     * @param wolfyUtilities
     */
    public NMSEntry(WolfyUtilities wolfyUtilities) {
        super(wolfyUtilities);
        this.blockUtil = new BlockUtilImpl(this);
        this.itemUtil = new ItemUtilImpl(this);
        this.inventoryUtil = new InventoryUtilImpl(this);
        this.nbtUtil = new NBTUtilImpl(this);
    }
}
