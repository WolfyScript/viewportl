package me.wolfyscript.utilities.api.custom_items;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class CustomItemBreakEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private int exp;
    protected Block block;
    private final Player player;
    private boolean dropItems;
    private boolean cancel;
    private CustomItem customItem;

    public CustomItemBreakEvent(CustomItem customItem, BlockBreakEvent event){
        this.player = event.getPlayer();
        this.dropItems = event.isDropItems();
        this.exp = event.getExpToDrop();
        this.block = event.getBlock();
        this.customItem = customItem;
    }

    public CustomItemBreakEvent(int exp, Block block, Player player, boolean dropItems, boolean cancel, CustomItem customItem) {
        this.exp = exp;
        this.block = block;
        this.player = player;
        this.dropItems = dropItems;
        this.cancel = cancel;
        this.customItem = customItem;
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    public void setCustomItem(CustomItem customItem) {
        this.customItem = customItem;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public final Block getBlock() {
        return this.block;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
