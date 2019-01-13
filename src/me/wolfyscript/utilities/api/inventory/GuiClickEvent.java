package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiClickEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;

    private GuiHandler guiHandler;
    private Player player;
    private WolfyUtilities wolfyUtilities;
    private GuiWindow guiWindow;
    private int clickedSlot;
    private ClickType clickType;
    private Inventory clickedInventory;
    private Inventory inventory;
    private InventoryAction inventoryAction;
    private int rawSlot;
    private ItemStack currentItem;
    private ItemStack cursor;
    private int hotbarButton;
    private InventoryType.SlotType slotType;

    public GuiClickEvent(GuiHandler guiHandler, GuiWindow guiWindow, InventoryClickEvent event){
        this.guiHandler = guiHandler;
        this.player = guiHandler.getPlayer();
        this.wolfyUtilities = guiHandler.getApi();
        this.guiWindow = guiWindow;
        this.clickedSlot = event.getSlot();
        this.clickType = event.getClick();
        this.clickedInventory = event.getClickedInventory();
        this.inventory = event.getInventory();
        this.inventoryAction = event.getAction();
        this.rawSlot = event.getRawSlot();
        this.currentItem = event.getCurrentItem();
        this.cursor = event.getCursor();
        this.hotbarButton = event.getHotbarButton();
        this.slotType = event.getSlotType();
    }

    public boolean verify(GuiWindow guiWindow){
        return guiWindow.equals(this.guiWindow);
    }

    public int getClickedSlot() {
        return clickedSlot;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public Inventory getClickedInventory() {
        return clickedInventory;
    }

    public InventoryAction getInventoryAction() {
        return inventoryAction;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    public int getHotbarButton() {
        return hotbarButton;
    }

    public InventoryType.SlotType getSlotType() {
        return slotType;
    }

    public GuiWindow getGuiWindow() {
        return guiWindow;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
