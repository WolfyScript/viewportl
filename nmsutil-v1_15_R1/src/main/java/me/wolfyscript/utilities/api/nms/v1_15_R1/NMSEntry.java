package me.wolfyscript.utilities.api.nms.v1_15_R1;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.BlockUtil;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import org.bukkit.plugin.Plugin;

public class NMSEntry extends NMSUtil {

    private final BlockUtil blockUtil;
    private final ItemUtil itemUtil;
    private final InventoryUtil inventoryUtil;

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
    }

    @Override
    public BlockUtil getBlockUtil() {
        return blockUtil;
    }

    @Override
    public ItemUtil getItemUtil() {
        return itemUtil;
    }

    @Override
    public InventoryUtil getInventoryUtil() {
        return inventoryUtil;
    }
}
