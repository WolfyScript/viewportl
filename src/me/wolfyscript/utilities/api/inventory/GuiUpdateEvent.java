package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GuiHandler guiHandler;
    private InventoryAPI inventoryAPI;
    private WolfyUtilities wolfyUtilities;
    private Player player;
    private Inventory inventory;
    private GuiWindow guiWindow;

    public GuiUpdateEvent(GuiHandler guiHandler, GuiWindow guiWindow){
        this.guiHandler = guiHandler;
        this.inventoryAPI = guiHandler.getApi().getInventoryAPI();
        this.wolfyUtilities = guiHandler.getApi();
        this.player = guiHandler.getPlayer();
        this.guiWindow = guiWindow;
        if(!guiWindow.hasCachedInventory(guiHandler)){
            if(guiWindow.getInventoryType() == null){
                this.inventory = Bukkit.createInventory(null, guiWindow.getSize(), guiWindow.getInventoryName());
            }else{
                this.inventory = Bukkit.createInventory(null, guiWindow.getInventoryType(), guiWindow.getInventoryName());
            }
        }else{
            this.inventory = guiWindow.getInventory(guiHandler);
        }
    }

    public boolean verify(GuiWindow guiWindow){
        return guiWindow.equals(this.guiWindow);
    }

    public ItemStack getItem(String action) {
        return getItem(guiWindow.getItemKey(), action);
    }

    public ItemStack getItem(String key, String action){
        return guiHandler.getItem(key, action).clone();
    }

    public ItemStack getGeneralItem(String id) {
        return guiHandler.getItem("none", id);
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public Player getPlayer() {
        return player;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public GuiWindow getGuiWindow() {
        return guiWindow;
    }

    public InventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    public ItemStack getItem(int slot){
        return getInventory().getItem(slot);
    }

    public ItemStack getItem(String action, String keyToReplace, String value){
        return getItem(guiWindow.getItemKey(), action, keyToReplace, value);
    }

    public ItemStack getItem(String key, String action, String keyToReplace, String value){
        ItemStack item = getItem(key, action);
        ItemMeta posXmeta = item.getItemMeta();
        posXmeta.setDisplayName(posXmeta.getDisplayName().replace(keyToReplace, value));
        item.setItemMeta(posXmeta);
        return item;
    }

    public void setItem(int slot, ItemStack itemStack){
        getInventory().setItem(slot, itemStack);
    }

    public void setItem(int slot, String action){
        setItem(slot, action, false);
    }

    public void setItem(int slot, String key, String action){
        setItem(slot, getItem(key, action));
    }

    public void setItem(int slot, String action, boolean generalItem){
        setItem(slot, generalItem ? getGeneralItem(action) : getItem(action));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Inventory createInventory(InventoryHolder owner, int size){
         return Bukkit.createInventory(owner, size, guiWindow.getInventoryName());
    }

    public Inventory createInventory(InventoryHolder owner, InventoryType type){
        return Bukkit.createInventory(owner, type, guiWindow.getInventoryName());
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
