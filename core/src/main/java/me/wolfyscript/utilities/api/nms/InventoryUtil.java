package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.event.inventory.InventoryType;

public abstract class InventoryUtil extends UtilComponent {

    protected InventoryUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title);

    /**
     * This is used for internal initialization of the {@link CreativeModeTab} registry.
     *
     * @deprecated Used for internal initialization. Has no effect if called a second time!
     */
    @Deprecated
    public abstract void initItemCategories();
}
