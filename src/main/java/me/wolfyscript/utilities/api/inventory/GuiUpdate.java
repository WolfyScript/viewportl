package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

public class GuiUpdate {

    private final GuiHandler<?> guiHandler;
    private final InventoryAPI<?> inventoryAPI;
    private final WolfyUtilities wolfyUtilities;
    private final Player player;
    private final Inventory inventory;
    private final Inventory queueInventory;
    private final GuiWindow guiWindow;

    public GuiUpdate(GuiHandler<?> guiHandler, GuiWindow guiWindow) {
        this.guiHandler = guiHandler;
        this.inventoryAPI = guiHandler.getInvAPI();
        this.wolfyUtilities = guiHandler.getApi();
        this.player = guiHandler.getPlayer();
        this.guiWindow = guiWindow;
        this.queueInventory = Bukkit.createInventory(null, 54, "");
        if (!guiWindow.hasCachedInventory(guiHandler)) {
            String guiName = guiWindow.getInventoryName();
            guiName = guiName.replace("%plugin.version%", wolfyUtilities.getPlugin().getDescription().getVersion()).replace("%plugin.author%", wolfyUtilities.getPlugin().getDescription().getAuthors().toString()).replace("%plugin.name%", wolfyUtilities.getPlugin().getDescription().getName());
            if (guiWindow.getInventoryType() == null) {
                this.inventory = Bukkit.createInventory(null, guiWindow.getSize(), guiName);
            } else {
                this.inventory = Bukkit.createInventory(null, guiWindow.getInventoryType(), guiName);
            }
        } else {
            this.inventory = guiWindow.getInventory(guiHandler);
        }
    }

    public GuiHandler<?> getGuiHandler() {
        return guiHandler;
    }

    public <C extends CustomCache> GuiHandler<C> getGuiHandler(Class<C> customCache) {
        return wolfyUtilities.getInventoryAPI(customCache).getGuiHandler(player);
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
    GuiWindow getGuiWindow() {
        return guiWindow;
    }

    public InventoryAPI<?> getInventoryAPI() {
        return inventoryAPI;
    }

    public <C extends CustomCache> InventoryAPI<C> getInventoryAPI(Class<C> customCache) {
        return wolfyUtilities.getInventoryAPI(customCache);
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
        Button button = guiWindow.getButton(id);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, queueInventory, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Tries to add an Locally registered Button. If it doesn't exist then
    it will try to get the button globally registered for this GuiCluster.
     */
    public void setLocalOrGlobalButton(int slot, String id) {
        Button button = guiWindow.getButton(id);
        if (button == null) {
            button = inventoryAPI.getButton(guiWindow.getClusterID(), id);
        }
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, queueInventory, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Sets a Button object to the specific slot.
     */
    public void setButton(int slot, @Nonnull Button button) {
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, button.getId());
            renderButton(button, guiHandler, player, queueInventory, slot, guiHandler.isHelpEnabled());
        }
    }

    /*
    Set an globally registered Button.
    Globally means it is registered via the InventoryAPI and registered in the GuiCluster.
     */
    public void setButton(int slot, String namespace, String key) {
        Button button = inventoryAPI.getButton(namespace, key);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, namespace + ":" + key);
            renderButton(button, guiHandler, player, queueInventory, slot, guiHandler.isHelpEnabled());
        }
    }

    private void renderButton(Button button, GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        try {
            button.render(guiHandler, player, inventory, slot, guiHandler.isHelpEnabled());
        } catch (IOException e) {
            System.out.println("Error while rendering Button \"" + button.getId() + "\"!");
            e.printStackTrace();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Inventory createInventory(InventoryHolder owner, int size) {
        return Bukkit.createInventory(owner, size, guiWindow.getInventoryName());
    }

    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return Bukkit.createInventory(owner, type, guiWindow.getInventoryName());
    }

    public void applyChanges() {
        Bukkit.getScheduler().runTask(getInventoryAPI().getPlugin(), () -> inventory.setContents(Arrays.copyOfRange(queueInventory.getContents(), 0, inventory.getSize())));
    }

}
