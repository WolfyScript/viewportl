package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class GuiItemDragEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private GuiHandler guiHandler;
    private Player player;
    private WolfyUtilities wolfyUtilities;
    private GuiWindow guiWindow;
    private final DragType type;
    private final Map<Integer, ItemStack> addedItems;
    private final Set<Integer> containerSlots;
    private final ItemStack oldCursor;
    private ItemStack newCursor;
    private InventoryView view;


    public GuiItemDragEvent(GuiHandler guiHandler, InventoryDragEvent event) {
        this.guiHandler = guiHandler;
        this.player = guiHandler.getPlayer();
        this.wolfyUtilities = guiHandler.getApi();
        this.guiWindow = guiHandler.getCurrentInv();
        type = event.getType();
        addedItems = event.getNewItems();
        containerSlots = event.getInventorySlots();
        oldCursor = event.getOldCursor();
        view = event.getView();
    }

    public InventoryView getView() {
        return view;
    }

    public boolean verify(GuiWindow guiWindow) {
        return guiWindow.equals(this.guiWindow);
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

    public Map<Integer, ItemStack> getNewItems() {
        return Collections.unmodifiableMap(this.addedItems);
    }

    public Set<Integer> getRawSlots() {
        return this.addedItems.keySet();
    }

    public Set<Integer> getInventorySlots() {
        return this.containerSlots;
    }

    public ItemStack getCursor() {
        return this.newCursor;
    }

    public void setCursor(ItemStack newCursor) {
        this.newCursor = newCursor;
    }

    public ItemStack getOldCursor() {
        return this.oldCursor.clone();
    }

    public DragType getType() {
        return this.type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
