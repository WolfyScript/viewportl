package me.wolfyscript.utilities.api.nms.v1_16_R3;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_16_R3.inventory.util.GUIInventoryCreator;
import org.bukkit.event.inventory.InventoryType;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, guiHandler, type);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, guiHandler, type, title);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, guiHandler, size);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, guiHandler, size, title);
    }
}