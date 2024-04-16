package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStorageMoveEvent extends Event implements BlockStorageEvent {

    private static final HandlerList handlers = new HandlerList();

    private final BlockStorage blockStorage;
    private final BlockStorage previousBlockStorage;

    public BlockStorageMoveEvent(BlockStorage blockStorage, @Nullable BlockStorage previousBlockStorage) {
        this.blockStorage = blockStorage;
        this.previousBlockStorage = previousBlockStorage;
    }


    @NotNull
    @Override
    public BlockStorage getStorage() {
        return blockStorage;
    }

    public Optional<BlockStorage> getPreviousStore() {
        return Optional.ofNullable(previousBlockStorage);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
