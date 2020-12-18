package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.event.inventory.InventoryType;

public abstract class InventoryUtil {

    private final NMSUtil nmsUtil;

    protected InventoryUtil(NMSUtil nmsUtil) {
        this.nmsUtil = nmsUtil;
    }

    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size);

    public abstract <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title);
}
