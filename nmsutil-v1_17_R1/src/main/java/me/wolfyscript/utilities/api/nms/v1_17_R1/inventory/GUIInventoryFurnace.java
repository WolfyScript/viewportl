package me.wolfyscript.utilities.api.nms.v1_17_R1.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryFurnace;

public class GUIInventoryFurnace<C extends CustomCache> extends CraftInventoryFurnace implements GUIInventory<C> {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryFurnace(GuiHandler<C> guiHandler, GuiWindow<C> window, FurnaceBlockEntity inventory) {
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
