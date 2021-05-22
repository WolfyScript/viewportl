package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contains all the data that is used in {@link GuiWindow} updates like {@link GuiWindow#onUpdateAsync(GuiUpdate)} or {@link GuiWindow#onUpdateSync(GuiUpdate)}.
 * <br>
 * It is used to render the GUI inventory and place Buttons into place.
 *
 * @param <C> The type of the {@link CustomCache}.
 */
public class GuiUpdate<C extends CustomCache> {

    private final GuiHandler<C> guiHandler;
    private final InventoryAPI<C> inventoryAPI;
    private final WolfyUtilities wolfyUtilities;
    private final Player player;
    private final GUIInventory<C> inventory;
    private final Inventory queueInventory;
    private final GuiWindow<C> guiWindow;

    GuiUpdate(GUIInventory<C> inventory, GuiHandler<C> guiHandler, GuiWindow<C> guiWindow) {
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

    /**
     * @return The {@link GuiHandler} that caused this update.
     */
    public final GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }

    /**
     * @return The player that caused this update.
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link GUIInventory} this update was called from.
     */
    public final GUIInventory<C> getInventory() {
        return inventory;
    }

    /**
     * Directly set an ItemStack to a slot.
     *
     * @param slot      The slot the item should set in.
     * @param itemStack The ItemStack to set.
     */
    public void setItem(int slot, ItemStack itemStack) {
        queueInventory.setItem(slot, itemStack);
    }

    /**
     * Set an locally registered Button from the current {@link GuiWindow}.
     * <br><br>
     * <strong>It is recommended to save IDs of Buttons as constants in their corresponding {@link GuiWindow} to prevent magic values!</strong>
     *
     * @param slot The slot the Button should be rendered in.
     * @param id   The id of the Button.
     */
    public void setButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /**
     * Set a globally Button registered in a {@link GuiCluster}.
     * <br><br>
     * <strong>It is recommended to save {@link NamespacedKey}s of Buttons as constants in their corresponding {@link GuiCluster} to prevent magic values!</strong>
     *
     * @param slot          The slot the Button should be rendered in.
     * @param namespacedKey The NamespacedKey of the button. The namespace is the cluster key and the key is the button id.
     */
    public void setButton(int slot, NamespacedKey namespacedKey) {
        Button<C> button = inventoryAPI.getButton(namespacedKey);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, namespacedKey.toString());
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /**
     * Used for easier access of buttons, but can be quite inefficient if you have the same buttons multiple times.
     *
     * @param slot       The slot the Button should be rendered in.
     * @param clusterKey The cluster key.
     * @param buttonId   The button id.
     * @deprecated You can easily do mistakes using this method. It is recommended to use constants in your {@link GuiCluster} to save the {@link NamespacedKey}s and use {@link #setButton(int, NamespacedKey)} instead!
     */
    @Deprecated
    public void setButton(int slot, String clusterKey, String buttonId) {
        setButton(slot, new NamespacedKey(clusterKey, buttonId));
    }

    /**
     * Directly render a Button into a specific slot.
     *
     * @param slot   The slot the button should be rendered in.
     * @param button The {@link Button} that should be rendered.
     */
    public void setButton(int slot, @Nonnull Button<C> button) {
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, button.getId());
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    /**
     * Tries to add an Locally registered Button. If it doesn't exist then
     * it will try to get the button globally registered for this GuiCluster.
     *
     * @param slot The slot the button should be rendered in.
     * @param id   The id of the button.
     */
    public void setLocalOrGlobalButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button == null) {
            button = inventoryAPI.getButton(new NamespacedKey(guiWindow.getNamespacedKey().getNamespace(), id));
        }
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot, guiHandler.isHelpEnabled());
        }
    }

    private void renderButton(Button<C> button, GuiHandler<C> guiHandler, Player player, int slot, boolean help) {
        try {
            ItemStack itemStack = this.inventory.getItem(slot);
            button.preRender(guiHandler, player, this.inventory, itemStack, slot, help);
            button.render(guiHandler, player, this.inventory, this.queueInventory, itemStack, slot, guiHandler.isHelpEnabled());
        } catch (IOException e) {
            wolfyUtilities.getConsole().severe("Error while rendering Button \"" + button.getId() + "\"!");
            e.printStackTrace();
        }
    }

    final void applyChanges() {
        if (queueInventory.getContents().length > 0) {
            inventory.setContents(Arrays.copyOfRange(queueInventory.getContents(), 0, inventory.getSize()));
        }
    }

    final void postExecuteButtons(HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event) {
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
