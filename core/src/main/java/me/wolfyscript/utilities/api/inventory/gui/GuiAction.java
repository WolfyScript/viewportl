package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiAction {

    private final String action;
    private final GuiHandler guiHandler;
    private final Player player;
    private final WolfyUtilities wolfyUtilities;
    private final GuiWindow guiWindow;
    private final int clickedSlot;
    private final ClickType clickType;
    private final Inventory clickedInventory;
    private final InventoryAction inventoryAction;
    private final int rawSlot;
    private final ItemStack currentItem;
    private final ItemStack cursor;
    private final int hotbarButton;
    private final InventoryType.SlotType slotType;

    public GuiAction(String action, GuiHandler guiHandler, GuiWindow guiWindow, InventoryClickEvent event) {
        this.action = action;
        this.guiHandler = guiHandler;
        this.player = guiHandler.getPlayer();
        this.wolfyUtilities = guiHandler.getApi();
        this.guiWindow = guiWindow;
        this.clickedSlot = event.getSlot();
        this.clickType = event.getClick();
        this.clickedInventory = event.getClickedInventory();
        this.inventoryAction = event.getAction();
        this.rawSlot = event.getRawSlot();
        this.currentItem = event.getCurrentItem();
        this.cursor = event.getCursor();
        this.hotbarButton = event.getHotbarButton();
        this.slotType = event.getSlotType();
    }

    public int getClickedSlot() {
        return clickedSlot;
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

    public Player getPlayer() {
        return player;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public String getAction() {
        return action;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }
}
