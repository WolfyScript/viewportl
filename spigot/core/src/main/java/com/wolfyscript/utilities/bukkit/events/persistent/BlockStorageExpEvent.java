package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockExpEvent;
import org.jetbrains.annotations.NotNull;

public class BlockStorageExpEvent extends BlockExpEvent implements BlockStorageEvent {

    private static final HandlerList handlers = new HandlerList();

    protected BlockStorage blockStorage;

    public BlockStorageExpEvent(@NotNull Block block, BlockStorage blockStorage, int exp) {
        super(block, exp);
        this.blockStorage = blockStorage;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public BlockStorage getStorage() {
        return blockStorage;
    }
}
