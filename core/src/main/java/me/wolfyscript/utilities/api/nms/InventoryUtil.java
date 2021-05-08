package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class InventoryUtil extends UtilComponent {

    protected InventoryUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title);

    /**
     * Sets the current stored recipe of the inventory if it is a crafting inventory.
     *
     * @param inventory A crafting inventory.
     * @param recipe    namespaced key of the recipe
     * @deprecated This is not tested at all and might not even work. Likely to be removed upcoming updates!
     */
    @Deprecated
    public abstract void setCurrentRecipe(Inventory inventory, NamespacedKey recipe);

    /**
     * This is used for internal initialization of the {@link CreativeModeTab} registry.
     *
     * @deprecated Used for internal initialization. Has no effect if called a second time!
     */
    @Deprecated
    public abstract void initItemCategories();
}
