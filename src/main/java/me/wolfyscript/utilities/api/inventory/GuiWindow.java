package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GuiWindow implements Listener {

    private String clusterID;
    private final String namespace;
    public String itemKey;
    private final InventoryAPI<?> inventoryAPI;
    private final HashMap<GuiHandler<?>, Inventory> cachedInventories;
    private final HashMap<String, Button> buttons = new HashMap<>();

    private boolean forceSyncUpdate;

    //Inventory
    private final InventoryType inventoryType;
    private final int size;

    public GuiWindow(String namespace, InventoryAPI<?> inventoryAPI, int size) {
        this(namespace, inventoryAPI, size, false);
    }

    public GuiWindow(String namespace, InventoryAPI<?> inventoryAPI, int size, boolean forceSyncUpdate) {
        this(namespace, namespace, inventoryAPI, null, size, forceSyncUpdate);
    }

    public GuiWindow(String namespace, InventoryAPI<?> inventoryAPI, InventoryType inventoryType) {
        this(namespace, inventoryAPI, inventoryType, false);
    }

    public GuiWindow(String namespace, InventoryAPI<?> inventoryAPI, InventoryType inventoryType, boolean forceSyncUpdate) {
        this(namespace, namespace, inventoryAPI, inventoryType, 0, forceSyncUpdate);
    }

    public GuiWindow(String namespace, String itemKey, InventoryAPI<?> inventoryAPI, InventoryType inventoryType, int size) {
        this(namespace, itemKey, inventoryAPI, inventoryType, size, false);
    }

    public GuiWindow(String namespace, String itemKey, InventoryAPI<?> inventoryAPI, InventoryType inventoryType, int size, boolean forceSyncUpdate) {
        this.namespace = namespace;
        this.inventoryAPI = inventoryAPI;
        this.itemKey = itemKey;
        this.cachedInventories = new HashMap<>();
        this.inventoryType = inventoryType;
        this.size = size;
        this.forceSyncUpdate = forceSyncUpdate;
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

    /**
     * This method is called when the inventory is initiated.
     * It's used to register Buttons and optionally other processing on start-up.
     */
    public void onInit() {
    }

    /**
     * This method is called after the {@link GuiUpdateEvent} is called and executed by Bukkit, but it makes
     * it easier to manage the GUI as it doesn't require a inventory verification.
     *
     * @param update
     */
    public void onUpdateSync(GuiUpdate update) {
    }

    /**
     * This method is called after the Bukkit GuiUpdateEvent and {@link #onUpdateSync(GuiUpdate)} are done.
     * It will be run by the scheduler Async, so be careful with using Bukkit methods!
     * Bukkit methods are not Thread safe!
     * <p>
     * If {@link #isForceSyncUpdate()} is enabled then this method is forced to be updated sync too just like {@link #onUpdateSync(GuiUpdate)}!
     *
     * @param update
     */
    public void onUpdateAsync(GuiUpdate update) {
    }

    protected void update(GuiHandler<?> guiHandler) {
        update(guiHandler, false);
    }

    protected void update(GuiHandler<?> guiHandler, boolean openInventory) {
        Bukkit.getScheduler().runTask(guiHandler.getApi().getPlugin(), () -> {
            if (!guiHandler.isChatEventActive()) {
                GuiUpdateEvent event = new GuiUpdateEvent(guiHandler, this);
                Bukkit.getPluginManager().callEvent(event);
                GuiUpdate guiUpdate = event.getGuiUpdate();
                onUpdateSync(guiUpdate);
                if (forceSyncUpdate) {
                    openInventory(guiHandler, event, guiUpdate, openInventory);
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(inventoryAPI.getPlugin(), () -> openInventory(guiHandler, event, guiUpdate, openInventory));
                }
            }
        });
    }

    private void openInventory(GuiHandler<?> guiHandler, GuiUpdateEvent event, GuiUpdate guiUpdate, boolean openInventory) {
        onUpdateAsync(event.getGuiUpdate());
        event.getGuiUpdate().applyChanges();
        setCachedInventorie(guiHandler, guiUpdate.getInventory());
        if (openInventory) {
            Bukkit.getScheduler().runTask(getAPI().getPlugin(), () -> {
                guiHandler.setChangingInv(true);
                guiHandler.getPlayer().openInventory(guiUpdate.getInventory());
                guiHandler.setChangingInv(false);
            });
        }
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

    HashMap<String, Button> getButtons() {
        return buttons;
    }

    /*
        Returns true if the Button is registered!
         */
    public boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    public void reloadInv(GuiHandler<?> guiHandler) {
        guiHandler.reloadInv(guiHandler.getCurrentGuiCluster(), guiHandler.getCurrentInv().getNamespace());
    }

    /*
    Opens the chat, send the player the defined message and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(GuiHandler<?> guiHandler, String msg, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), msg);
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.global_items.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String guiCluster, String msgKey, GuiHandler<?> guiHandler, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), "$inventories." + guiCluster + ".global_messages." + msgKey + "$");
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.<guiWindow>.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String msgKey, GuiHandler<?> guiHandler, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), "$inventories." + getClusterID() + "." + getNamespace() + ".messages." + msgKey + "$");
    }

    /*
    Opens the chat, send the player the defined action messages and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openActionChat(GuiHandler<?> guiHandler, ClickData clickData, ChatInputAction inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().sendActionMessage(guiHandler.getPlayer(), clickData);
    }

    /*
    Sends a message without closing the inventory.
     */
    public void sendMessage(GuiHandler<?> guiHandler, String msgKey) {
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), getClusterID(), getNamespace(), msgKey);
    }

    public void sendMessage(Player player, String msgKey) {
        inventoryAPI.getWolfyUtilities().sendPlayerMessage(player, getClusterID(), getNamespace(), msgKey);
    }

    public void sendMessage(GuiHandler<?> guiHandler, String msgKey, String[]... replacements) {
        guiHandler.getApi().sendPlayerMessage(guiHandler.getPlayer(), getClusterID(), getNamespace(), msgKey, replacements);
    }

    public void sendMessage(Player player, String msgKey, String[]... replacements) {
        inventoryAPI.getWolfyUtilities().sendPlayerMessage(player, getClusterID(), getNamespace(), msgKey, replacements);
    }

    protected String getInventoryName() {
        return WolfyUtilities.translateColorCodes(inventoryAPI.getWolfyUtilities().getLanguageAPI().replaceKeys("$inventories." + clusterID + "." + namespace + ".gui_name$"));
    }

    public void setClusterID(String clusterID) {
        this.clusterID = clusterID;
    }

    public Inventory getInventory(GuiHandler<?> guiHandler) {
        return cachedInventories.get(guiHandler);
    }

    public boolean hasCachedInventory(GuiHandler<?> guiHandler) {
        return cachedInventories.containsKey(guiHandler);
    }

    public void setCachedInventorie(GuiHandler<?> guiHandler, Inventory inventory) {
        cachedInventories.put(guiHandler, inventory);
    }

    public String getClusterID() {
        return clusterID;
    }

    public String getID() {
        return clusterID + ":" + namespace;
    }

    public List<String> getHelpInformation() {
        List<String> values = new ArrayList<>();
        for (String value : getAPI().getLanguageAPI().replaceKey("$inventories." + clusterID + "." + namespace + ".gui_help$")) {
            values.add(WolfyUtilities.translateColorCodes(value));
        }
        return values;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br/>
     * It should be enabled when using {@link me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton}
     * to make sure that no item could be duplicated, because of tick lag!
     *
     * @return
     */
    public boolean isForceSyncUpdate() {
        return forceSyncUpdate;
    }

    /**
     * @param forceSyncUpdate
     */
    public void setForceSyncUpdate(boolean forceSyncUpdate) {
        this.forceSyncUpdate = forceSyncUpdate;
    }
}
