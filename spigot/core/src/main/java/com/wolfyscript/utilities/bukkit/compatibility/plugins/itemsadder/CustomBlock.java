package com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder;

import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public interface CustomBlock {

    Optional<CustomBlock> place(Location location);

    boolean remove();

    BlockData getBaseBlockData();

    Block getBlock();

    boolean isPlaced();

    List<ItemStack> getLoot(boolean includeSelfBlock);

    List<ItemStack> getLoot();

    List<ItemStack> getLoot(ItemStack tool, boolean includeSelfBlock);

    int getOriginalLightLevel();

    void setCurrentLightLevel(int level);

    boolean playBreakEffect();

    boolean playBreakParticles();

    boolean playBreakSound();

    boolean playPlaceSound();
}
