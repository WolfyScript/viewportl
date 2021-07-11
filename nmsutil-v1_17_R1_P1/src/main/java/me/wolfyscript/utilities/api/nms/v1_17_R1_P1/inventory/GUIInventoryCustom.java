package me.wolfyscript.utilities.api.nms.v1_17_R1_P1.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class GUIInventoryCustom<C extends CustomCache> extends CraftInventoryCustom implements GUIInventory<C> {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, InventoryType type) {
        super(owner, type);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, InventoryType type, String title) {
        super(owner, type, title);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size) {
        super(owner, size);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size, String title) {
        super(owner, size, title);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    @Override
    public GuiWindow<C> getWindow() {
        return this.window;
    }

    @Override
    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }
}
