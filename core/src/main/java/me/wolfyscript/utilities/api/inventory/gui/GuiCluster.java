package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashMap;

public abstract class GuiCluster<C extends CustomCache> {

    protected final WolfyUtilities wolfyUtilities;
    protected final InventoryAPI<C> inventoryAPI;
    private String id;
    private final HashMap<String, Button<C>> buttons;
    private final HashMap<String, GuiWindow<C>> guiWindows;

    private NamespacedKey entry;

    public GuiCluster(InventoryAPI<C> inventoryAPI, String id) {
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
        button.init(id, wolfyUtilities);
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

    void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    HashMap<String, Button<C>> getButtons() {
        return buttons;
    }

    HashMap<String, GuiWindow<C>> getGuiWindows() {
        return guiWindows;
    }
}
