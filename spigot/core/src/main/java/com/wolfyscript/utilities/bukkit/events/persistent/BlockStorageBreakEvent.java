package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockStorageBreakEvent extends Event implements BlockStorageEvent, Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    protected Block block;
    protected BlockStorage blockStorage;
    private boolean cancel;

    public BlockStorageBreakEvent(@NotNull Block theBlock, BlockStorage blockStorage, @NotNull Player player) {
        super();
        this.block = theBlock;
        this.player = player;
        this.blockStorage = blockStorage;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the block involved in this event.
     *
     * @return The Block which block is involved in this event
     */
    @NotNull
    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public BlockStorage getStorage() {
        return blockStorage;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
