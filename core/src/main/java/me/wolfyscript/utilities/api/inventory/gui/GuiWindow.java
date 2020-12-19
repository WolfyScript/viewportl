package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.inventory.gui.events.GuiCloseEvent;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GuiWindow<C extends CustomCache> implements Listener {

    protected final WolfyUtilities wolfyUtilities;
    private final InventoryAPI<C> inventoryAPI;
    private final GuiCluster<C> cluster;
    public String itemKey;
    private final HashMap<String, Button<C>> buttons = new HashMap<>();
    private NamespacedKey namespacedKey;

    private boolean forceSyncUpdate;

    //Inventory
    private final InventoryType inventoryType;
    private final int size;

    public GuiWindow(GuiCluster<C> cluster, String key, int size) {
        this(cluster, key, size, false);
    }

    public GuiWindow(GuiCluster<C> cluster, String key, int size, boolean forceSyncUpdate) {
        this(cluster, key, key, null, size, forceSyncUpdate);
    }

    public GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType) {
        this(cluster, key, inventoryType, false);
    }

    public GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType, boolean forceSyncUpdate) {
        this(cluster, key, key, inventoryType, 0, forceSyncUpdate);
    }

    public GuiWindow(GuiCluster<C> cluster, String key, String itemKey, InventoryType inventoryType, int size) {
        this(cluster, key, itemKey, inventoryType, size, false);
    }

    public GuiWindow(GuiCluster<C> cluster, String key, String itemKey, InventoryType inventoryType, int size, boolean forceSyncUpdate) {
        this.cluster = cluster;
        this.inventoryAPI = cluster.inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtilities();
        this.namespacedKey = new NamespacedKey(cluster.getId(), key);
        this.itemKey = itemKey;
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
     * This method is called each time the gui is updated.
     *
     * @param update
     */
    public void onUpdateSync(GuiUpdate<C> update) {
    }

    /**
     * This method is called after the {@link #onUpdateSync(GuiUpdate)} is done.
     * It will be run by the scheduler Async, so be careful with using Bukkit methods!
     * Bukkit methods are not Thread safe!
     * <p>
     * If {@link #isForceSyncUpdate()} is enabled then this method is forced to be updated sync too and will act just like {@link #onUpdateSync(GuiUpdate)}!
     *
     * @param update
     */
    public void onUpdateAsync(GuiUpdate<C> update) {
    }

    /**
     * This method allows you to execute code when this window is closed.
     * It does not require verification like the GuiCloseEvent.
     * <p>
     * This method can be overridden and you can either call this super method or not.
     * If you decide not to, then the GuiCloseEvent won't be called.
     *
     * @param guiHandler  the gui handler that caused this close event.
     * @param transaction the inventory view of the player.
     * @return true if the gui close should be cancelled.
     */
    public boolean onClose(GuiHandler<C> guiHandler, InventoryView transaction) {
        GuiCloseEvent closeEvent = new GuiCloseEvent(namespacedKey.getNamespace(), this, guiHandler, transaction);
        Bukkit.getPluginManager().callEvent(closeEvent);
        return closeEvent.isCancelled();
    }

    void create(GuiHandler<C> guiHandler) {
        update(null, guiHandler, null, null, true);
    }

    void update(GUIInventory<C> inventory, HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event) {
        update(inventory, inventory.getGuiHandler(), postExecuteBtns, event, false);
    }

    private void update(GUIInventory<C> inventory, GuiHandler<C> guiHandler, HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event, boolean openInventory) {
        Bukkit.getScheduler().runTask(guiHandler.getApi().getPlugin(), () -> {
            GuiUpdate<C> guiUpdate = new GuiUpdate<>(inventory, guiHandler, this);
            guiUpdate.postExecuteButtons(postExecuteBtns, event);
            callUpdate(guiHandler, guiUpdate, openInventory);
        });
    }

    private void callUpdate(GuiHandler<C> guiHandler, GuiUpdate<C> guiUpdate, boolean openInventory) {
        if (!guiHandler.isChatEventActive()) {
            onUpdateSync(guiUpdate);
            Runnable runnable = () -> openInventory(guiHandler, guiUpdate, openInventory);
            if (forceSyncUpdate) {
                runnable.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(inventoryAPI.getPlugin(), runnable);
            }
        }
    }

    private void openInventory(GuiHandler<C> guiHandler, GuiUpdate<C> guiUpdate, boolean openInventory) {
        onUpdateAsync(guiUpdate);
        guiUpdate.applyChanges();
        if (openInventory) {
            Bukkit.getScheduler().runTask(getAPI().getPlugin(), () -> {
                guiHandler.setSwitchWindow(true);
                guiHandler.getPlayer().openInventory(guiUpdate.getInventory());
                guiHandler.setSwitchWindow(false);
            });
        }
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public GuiCluster<C> getCluster() {
        return cluster;
    }

    /*
                Register an Button!
                The id of the Button must be unique, else it will override the Button with the same id.
             */
    public void registerButton(Button<C> button) {
        button.init(this);
        buttons.put(button.getId(), button);
    }

    /*
    Gets the Button by it's id.
     */
    public Button<C> getButton(String id) {
        return buttons.get(id);
    }

    HashMap<String, Button<C>> getButtons() {
        return buttons;
    }

    /*
        Returns true if the Button is registered!
         */
    public boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    public void reloadInv(GuiHandler<C> guiHandler) {
        guiHandler.reloadWindow(guiHandler.getWindow().getNamespacedKey());
    }

    /*
    Opens the chat, send the player the defined message and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(GuiHandler<C> guiHandler, String msg, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendPlayerMessage(guiHandler.getPlayer(), msg);
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.global_items.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String guiCluster, String msgKey, GuiHandler<C> guiHandler, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendPlayerMessage(guiHandler.getPlayer(), "$inventories." + guiCluster + ".global_messages." + msgKey + "$");
    }

    /*
    Opens the chat, send the player the defined message, which is set inside of the language under "inventories.<guiCluster>.<guiWindow>.<msgKey>"
    Then it waits for the player's input.
    When the player sends the message the inputAction method is executed
     */
    public void openChat(String msgKey, GuiHandler<C> guiHandler, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendPlayerMessage(guiHandler.getPlayer(), getNamespacedKey(), msgKey);
    }

    /*
    Opens the chat, send the player the defined action messages and waits for the input of the player.
    When the player sends the message the inputAction method is executed
     */
    public void openActionChat(GuiHandler<C> guiHandler, ClickData clickData, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendActionMessage(guiHandler.getPlayer(), clickData);
    }

    /*
    Sends a message without closing the inventory.
     */
    public void sendMessage(GuiHandler<C> guiHandler, String msgKey) {
        guiHandler.getApi().getChat().sendPlayerMessage(guiHandler.getPlayer(), getNamespacedKey(), msgKey);
    }

    public void sendMessage(Player player, String msgKey) {
        inventoryAPI.getWolfyUtilities().getChat().sendPlayerMessage(player, getNamespacedKey(), msgKey);
    }

    public void sendMessage(GuiHandler<C> guiHandler, String msgKey, Pair<String, String>... replacements) {
        guiHandler.getApi().getChat().sendPlayerMessage(guiHandler.getPlayer(), getNamespacedKey(), msgKey, replacements);
    }

    public void sendMessage(Player player, String msgKey, Pair<String, String>... replacements) {
        inventoryAPI.getWolfyUtilities().getChat().sendPlayerMessage(player, getNamespacedKey(), msgKey, replacements);
    }

    protected String getInventoryName() {
        return ChatColor.convert(inventoryAPI.getWolfyUtilities().getLanguageAPI().replaceKeys("$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_name$"));
    }

    public List<String> getHelpInformation() {
        List<String> values = new ArrayList<>();
        for (String value : getAPI().getLanguageAPI().replaceKey("$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_help$")) {
            values.add(ChatColor.convert(value));
        }
        return values;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br/>
     * It should be enabled when using {@link ItemInputButton}
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
