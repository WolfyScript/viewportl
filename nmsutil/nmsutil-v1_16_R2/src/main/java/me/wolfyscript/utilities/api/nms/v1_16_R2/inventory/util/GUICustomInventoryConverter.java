package me.wolfyscript.utilities.api.nms.v1_16_R2.inventory.util;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_16_R2.inventory.GUIInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class GUICustomInventoryConverter implements GUIInventoryCreator.InventoryConverter {

    public GUICustomInventoryConverter() {
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type) {
        return new GUIInventoryCustom<>(guiHandler, window, holder, type);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
        return new GUIInventoryCustom<>(guiHandler, window, holder, type, title);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size) {
        return new GUIInventoryCustom<>(guiHandler, window, owner, size);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size, String title) {
        return new GUIInventoryCustom<>(guiHandler, window, owner, size, title);
    }
}
