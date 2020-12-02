package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class GuiUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GuiUpdate guiUpdate;

    public GuiUpdateEvent(GuiHandler guiHandler, GuiWindow guiWindow) {
        this.guiUpdate = new GuiUpdate(guiHandler, guiWindow);
    }

    public boolean verify(GuiWindow guiWindow) {
        return guiWindow.equals(getGuiWindow());
    }

    public GuiHandler getGuiHandler() {
        return guiUpdate.getGuiHandler();
    }

    public Player getPlayer() {
        return guiUpdate.getPlayer();
    }

    public WolfyUtilities getWolfyUtilities() {
        return guiUpdate.getWolfyUtilities();
    }

    public GuiWindow getGuiWindow() {
        return guiUpdate.getGuiWindow();
    }

    public InventoryAPI getInventoryAPI() {
        return guiUpdate.getInventoryAPI();
    }

    public ItemStack getItem(int slot) {
        return guiUpdate.getItem(slot);
    }

    public void setItem(int slot, ItemStack itemStack) {
        guiUpdate.setItem(slot, itemStack);
    }

    /*
    Set an locally registered Button.
    Locally means it is registered inside of the GuiWindow!
     */
    public void setButton(int slot, String id) {
        guiUpdate.setButton(slot, id);
    }

    /*
    Tries to add an Locally registered Button. If it doesn't exist then
    it will try to get the button globally registered for this GuiCluster.
     */
    public void setLocalOrGlobalButton(int slot, String id){
        guiUpdate.setLocalOrGlobalButton(slot, id);
    }

    /*
    Sets a Button object to the specific slot.
     */
    public void setButton(int slot, @Nonnull Button button) {
        guiUpdate.setButton(slot, button);
    }

    /*
    Set an globally registered Button.
    Globally means it is registered via the InventoryAPI and registered in the GuiCluster.
     */
    public void setButton(int slot, String namespace, String key) {
        guiUpdate.setButton(slot, namespace, key);
    }

    public Inventory getInventory() {
        return guiUpdate.getInventory();
    }

    public Inventory createInventory(InventoryHolder owner, int size) {
        return guiUpdate.createInventory(owner, size);
    }

    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return guiUpdate.createInventory(owner, type);
    }

    public GuiUpdate getGuiUpdate() {
        return guiUpdate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
