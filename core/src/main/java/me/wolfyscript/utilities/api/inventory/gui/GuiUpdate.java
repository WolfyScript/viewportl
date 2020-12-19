package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class GuiUpdate<C extends CustomCache> {

    private final GuiHandler<C> guiHandler;
    private final InventoryAPI<C> inventoryAPI;
    private final WolfyUtilities wolfyUtilities;
    private final Player player;
    private final Inventory inventory;
    private final Inventory queueInventory;
    private final GuiWindow<C> guiWindow;

    public GuiUpdate(GUIInventory<C> inventory, GuiHandler<C> guiHandler, GuiWindow<C> guiWindow) {
        this.guiHandler = guiHandler;
        this.inventoryAPI = guiHandler.getInvAPI();
        this.wolfyUtilities = guiHandler.getApi();
        this.player = guiHandler.getPlayer();
        this.guiWindow = guiWindow;
        this.queueInventory = Bukkit.createInventory(null, 54, "");
        if (inventory != null) {
            this.inventory = inventory;
        } else {
            String guiName = guiWindow.getInventoryName();
            guiName = guiName.replace("%plugin.version%", wolfyUtilities.getPlugin().getDescription().getVersion()).replace("%plugin.author%", wolfyUtilities.getPlugin().getDescription().getAuthors().toString()).replace("%plugin.name%", wolfyUtilities.getPlugin().getDescription().getName());
            if (guiWindow.getInventoryType() == null) {
                this.inventory = wolfyUtilities.getNmsUtil().getInventoryUtil().createGUIInventory(guiHandler, guiWindow, guiWindow.getSize(), guiName);
            } else {
                this.inventory = wolfyUtilities.getNmsUtil().getInventoryUtil().createGUIInventory(guiHandler, guiWindow, guiWindow.getInventoryType(), guiName);
            }
        }
    }

    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }

    public Player getPlayer() {
        return player;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    /**
     * Should only be used for the GuiUpdateEvent, as the GuiWindow is already available in the sync and async methods!
     *
     * @return the GUiWindow this update is executed!
     */
    GuiWindow<C> getGuiWindow() {
        return guiWindow;
    }

    public InventoryAPI<C> getInventoryAPI() {
        return inventoryAPI;
    }

    public ItemStack getItem(int slot) {
        return getInventory().getItem(slot);
    }

    public void setItem(int slot, ItemStack itemStack) {
        queueInventory.setItem(slot, itemStack);
    }

    /*
    Set an locally registered Button.
    Locally means it is registered inside of the GuiWindow!
     */
    public void setButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Tries to add an Locally registered Button. If it doesn't exist then
    it will try to get the button globally registered for this GuiCluster.
     */
    public void setLocalOrGlobalButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button == null) {
            button = inventoryAPI.getButton(guiWindow.getNamespacedKey().getNamespace(), id);
        }
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Sets a Button object to the specific slot.
     */
    public void setButton(int slot, @Nonnull Button<C> button) {
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, button.getId());
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Set an globally registered Button.
    Globally means it is registered via the InventoryAPI and registered in the GuiCluster.
     */
    public void setButton(int slot, String namespace, String key) {
        Button<C> button = inventoryAPI.getButton(namespace, key);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, namespace + ":" + key);
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    private void renderButton(Button<C> button, GuiHandler<C> guiHandler, Player player, int slot, boolean help) {
        try {
            button.prepareRender(guiHandler, player, this.inventory, this.inventory.getItem(slot), slot, help);
            button.render(guiHandler, player, this.queueInventory, slot, guiHandler.isHelpEnabled());
        } catch (IOException e) {
            System.out.println("Error while rendering Button \"" + button.getId() + "\"!");
            e.printStackTrace();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void applyChanges() {
        if (queueInventory.getContents().length > 0) {
            Bukkit.getScheduler().runTask(getInventoryAPI().getPlugin(), () -> inventory.setContents(Arrays.copyOfRange(queueInventory.getContents(), 0, inventory.getSize())));
        }
    }

    void postExecuteButtons(HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event) {
        if (postExecuteBtns != null) {
            postExecuteBtns.forEach((slot, btn) -> {
                try {
                    btn.postExecute(guiHandler, player, inventory, inventory.getItem(slot), slot, event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
