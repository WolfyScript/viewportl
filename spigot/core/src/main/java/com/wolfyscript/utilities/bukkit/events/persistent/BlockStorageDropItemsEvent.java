package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.List;
import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStorageDropItemsEvent extends Event implements BlockStorageEvent, Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    protected Block block;
    private final BlockStorage blockStorage;
    private boolean cancel;
    private final BlockState blockState;
    private final List<Item> items;

    public BlockStorageDropItemsEvent(@NotNull Block block, @NotNull BlockState blockState, @NotNull BlockStorage blockStorage, @Nullable Player player, @NotNull List<Item> items) {
        this.player = player;
        this.block = block;
        this.blockStorage = blockStorage;
        this.blockState = blockState;
        this.items = items;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public List<Item> getItems() {
        return items;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public BlockStorage getStorage() {
        return blockStorage;
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
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
