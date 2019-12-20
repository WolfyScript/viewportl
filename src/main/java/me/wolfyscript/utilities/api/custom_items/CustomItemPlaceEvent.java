package me.wolfyscript.utilities.api.custom_items;

import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomItemPlaceEvent extends Event {

    private CustomItem customItem;

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private boolean canBuild;
    private Block placedAgainst;
    private BlockState replacedBlockState;
    private ItemStack itemInHand;
    protected Player player;
    private EquipmentSlot hand;
    private Block block;

    public CustomItemPlaceEvent(CustomItem customItem, BlockPlaceEvent event) {
        this.block = event.getBlockPlaced();
        this.placedAgainst = event.getBlockAgainst();
        this.itemInHand = event.getItemInHand();
        this.player = event.getPlayer();
        this.replacedBlockState = event.getBlockReplacedState();
        this.canBuild = event.canBuild();
        this.hand = event.getHand();
        this.cancel = event.isCancelled();
        this.customItem = customItem;
    }

    public CustomItemPlaceEvent(CustomItem customItem, @Nonnull Block placedBlock, @Nonnull BlockState replacedBlockState, @Nonnull Block placedAgainst, @Nonnull ItemStack itemInHand, @Nonnull Player thePlayer, boolean canBuild, @Nonnull EquipmentSlot hand) {
        this.block = placedBlock;
        this.placedAgainst = placedAgainst;
        this.itemInHand = itemInHand;
        this.player = thePlayer;
        this.replacedBlockState = replacedBlockState;
        this.canBuild = canBuild;
        this.hand = hand;
        this.cancel = false;
        this.customItem = customItem;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Nonnull
    public final Block getBlock() {
        return this.block;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    @Nonnull
    public Block getBlockPlaced() {
        return this.getBlock();
    }

    @Nonnull
    public BlockState getBlockReplacedState() {
        return this.replacedBlockState;
    }

    @Nonnull
    public Block getBlockAgainst() {
        return this.placedAgainst;
    }

    @Nonnull
    public ItemStack getItemInHand() {
        return this.itemInHand;
    }

    @Nonnull
    public EquipmentSlot getHand() {
        return this.hand;
    }

    public boolean canBuild() {
        return this.canBuild;
    }

    public void setBuild(boolean canBuild) {
        this.canBuild = canBuild;
    }

    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    public void setCustomItem(CustomItem customItem) {
        this.customItem = customItem;
    }
}
