package com.wolfyscript.utilities.bukkit.nms.fallback;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.BlockUtil;
import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.ItemUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NBTUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NetworkUtil;
import com.wolfyscript.utilities.bukkit.nms.api.RecipeUtil;
import com.wolfyscript.utilities.versioning.ServerVersion;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Used when no NMSUtil is available for the server Minecraft version.
 */
public class FallbackNMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the WolfyUtilities parameter.
     *
     * @param wolfyUtilities
     */
    public FallbackNMSEntry(WolfyUtilsBukkit wolfyUtilities) {
        super(wolfyUtilities);
        this.inventoryUtil = new FallbackInventoryUtilImpl(this);
    }

    @Override
    public BlockUtil getBlockUtil() {
        throw new NotImplementedException("BlockUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public ItemUtil getItemUtil() {
        throw new NotImplementedException("ItemUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public InventoryUtil getInventoryUtil() {
        return inventoryUtil;
    }

    @Override
    public NBTUtil getNBTUtil() {
        throw new NotImplementedException("NBTUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public RecipeUtil getRecipeUtil() {
        throw new NotImplementedException("RecipeUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public NetworkUtil getNetworkUtil() {
        throw new NotImplementedException("NetworkUtil is not yet implement for " + ServerVersion.getVersion());
    }
}
