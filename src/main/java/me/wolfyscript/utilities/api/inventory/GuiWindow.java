package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiWindow implements Listener {

    private String clusterID;
    private String namespace;
    public String itemKey;
    private InventoryAPI inventoryAPI;
    private HashMap<GuiHandler, Inventory> cachedInventories;
    private HashMap<String, Button> buttons = new HashMap<>();


    //Inventory
    private InventoryType inventoryType;
    private int size;

    public GuiWindow(String namespace, InventoryAPI inventoryAPI, int size) {
        this(namespace, namespace, inventoryAPI, null, size);
    }

    public GuiWindow(String namespace, InventoryAPI inventoryAPI, InventoryType inventoryType) {
        this(namespace, namespace, inventoryAPI, inventoryType, 0);
    }

    public GuiWindow(String namespace, String itemKey, InventoryAPI inventoryAPI, InventoryType inventoryType, int size) {
        this.namespace = namespace;
        this.inventoryAPI = inventoryAPI;
        this.itemKey = itemKey;
        this.cachedInventories = new HashMap<>();
        this.inventoryType = inventoryType;
        this.size = size;
        Bukkit.getPluginManager().registerEvents(this, inventoryAPI.getPlugin());
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public WolfyUtilities getAPI() {
        return inventoryAPI.getWolfyUtilities();
    }

    public int getSize() {
        return size;
    }

    //OVERRIDE METHODS

    public void onInit() {

    }

    public boolean onClick(GuiClick guiClick) {
        return true;
    }

    public void update(GuiHandler guiHandler) {
        Bukkit.getScheduler().runTaskLater(inventoryAPI.getPlugin(), () -> {
            if (!guiHandler.isChatEventActive()) {
                GuiUpdateEvent event = new GuiUpdateEvent(guiHandler, this);
                Bukkit.getPluginManager().callEvent(event);
            }
        }, 1);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String newItemKey) {
        itemKey = newItemKey;
    }

    /*
        Register an Button!
        The id of the Button must be unique, else it will override the Button with the same id.
     */
    public void registerButton(Button button) {
        button.init(this);
        buttons.put(button.getId(), button);
    }

    /*
    Gets the Button by it's id.
     */
    public Button getButton(String id) {
        return buttons.get(id);
    }

    /*
    Returns true if the Button is registered!
     */
    public boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    public void reloadInv(GuiHandler guiHandler) {
        guiHandler.reloadInv(guiHandler.getCurrentGuiCluster(), guiHandler.getCurrentInv().getNamespace());
    }

    /*
    Opens the chat, send the player the defined message and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(GuiHandler guiHandler, String msg, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), msg);
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.global_items.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String guiCluster, String msgKey, GuiHandler guiHandler, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), "$inventories."+guiCluster+".global_messages."+msgKey+"$");
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.<guiWindow>.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String msgKey, GuiHandler guiHandler, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), "$inventories."+getClusterID()+"."+getNamespace()+".messages."+msgKey+"$");
    }

    /*
    Opens the chat, send the player the defined action messages and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openActionChat(GuiHandler guiHandler, ClickData clickData, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendActionMessage(guiHandler.getPlayer(), clickData);
    }

    /*
    Sends a message without closing the inventory.
     */
    public void sendMessage(GuiHandler guiHandler, String msgKey){
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), getClusterID(), getNamespace(), msgKey);
    }

    public void sendMessage(Player player, String msgKey){
        inventoryAPI.getWolfyUtilities().sendPlayerMessage(player, getClusterID(), getNamespace(), msgKey);
    }

    public void sendMessage(GuiHandler guiHandler, String msgKey, String[]... replacements){
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), getClusterID(), getNamespace(), msgKey, replacements);
    }

    public void sendMessage(Player player, String msgKey, String[]... replacements){
        inventoryAPI.getWolfyUtilities().sendPlayerMessage(player, getClusterID(), getNamespace(), msgKey, replacements);
    }

    protected String getInventoryName() {
        return WolfyUtilities.translateColorCodes(inventoryAPI.getWolfyUtilities().getLanguageAPI().getActiveLanguage().replaceKeys("$inventories."+ clusterID + "." + namespace + ".gui_name$"));
    }

    public void setClusterID(String clusterID){
        this.clusterID = clusterID;
    }

    public Inventory getInventory(GuiHandler guiHandler) {
        return cachedInventories.get(guiHandler);
    }

    public boolean hasCachedInventory(GuiHandler guiHandler) {
        return cachedInventories.containsKey(guiHandler);
    }

    public void setCachedInventorie(GuiHandler guiHandler, Inventory inventory) {
        cachedInventories.put(guiHandler, inventory);
    }

    public String getClusterID() {
        return clusterID;
    }

    public String getID(){
        return clusterID + ":" + namespace;
    }

    public List<String> getHelpInformation(){
        List<String> values = new ArrayList<>();
        for(String value : getAPI().getLanguageAPI().getActiveLanguage().replaceKey("$inventories."+ clusterID + "." + namespace + ".gui_help$")){
            values.add(WolfyUtilities.translateColorCodes(value));
        }
        return values;
    }
}
