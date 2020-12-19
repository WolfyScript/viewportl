package me.wolfyscript.utilities.api.nms.v1_16_R2.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import net.minecraft.server.v1_16_R2.IInventory;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryBrewer;

public class GUIInventoryBrewer<C extends CustomCache> extends CraftInventoryBrewer implements GUIInventory<C> {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryBrewer(GuiHandler<C> guiHandler, GuiWindow<C> window, IInventory inventory) {
        super(inventory);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    @Override
    public GuiWindow<C> getWindow() {
        return window;
    }

    @Override
    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }
}
