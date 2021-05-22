package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiCluster<C extends CustomCache> {

    protected final WolfyUtilities wolfyUtilities;
    protected final InventoryAPI<C> inventoryAPI;
    private String id;
    private final Map<String, Button<C>> buttons;
    private final Map<String, GuiWindow<C>> guiWindows;

    private NamespacedKey entry;

    protected GuiCluster(InventoryAPI<C> inventoryAPI, String id) {
        this.inventoryAPI = inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtilities();
        this.id = id;
        this.buttons = new HashMap<>();
        this.guiWindows = new HashMap<>();
        this.entry = null;
    }

    /**
     * This method is called when the cluster is initialized.
     */
    public abstract void onInit();

    public NamespacedKey getEntry() {
        return entry;
    }

    protected void setEntry(NamespacedKey entry) {
        this.entry = entry;
    }

    protected void registerButton(Button<C> button) {
        button.init(this);
        buttons.putIfAbsent(button.getId(), button);
    }

    public Button<C> getButton(String id) {
        return buttons.get(id);
    }

    protected void registerGuiWindow(GuiWindow<C> guiWindow) {
        if (this.entry == null) {
            this.entry = guiWindow.getNamespacedKey();
        }
        guiWindow.onInit();
        guiWindows.put(guiWindow.getNamespacedKey().getKey(), guiWindow);
    }

    public GuiWindow<C> getGuiWindow(String id) {
        return guiWindows.get(id);
    }

    /**
     * @return The {@link WolfyUtilities} this cluster belongs to.
     */
    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    Map<String, Button<C>> getButtons() {
        return buttons;
    }

    Map<String, GuiWindow<C>> getGuiWindows() {
        return guiWindows;
    }
}
