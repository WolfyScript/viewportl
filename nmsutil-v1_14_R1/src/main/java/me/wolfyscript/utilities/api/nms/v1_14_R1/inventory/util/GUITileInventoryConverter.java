package me.wolfyscript.utilities.api.nms.v1_14_R1.inventory.util;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_14_R1.inventory.GUIInventoryBrewer;
import me.wolfyscript.utilities.api.nms.v1_14_R1.inventory.GUIInventoryFurnace;
import me.wolfyscript.utilities.api.nms.v1_14_R1.inventory.GUIInventoryImpl;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUITileInventoryConverter implements GUIInventoryCreator.InventoryConverter {

    public GUITileInventoryConverter() {
    }

    public abstract IInventory getTileEntity();

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type) {
        return this.getInventory(guiHandler, window, this.getTileEntity());
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
        IInventory te = this.getTileEntity();
        if (te instanceof TileEntityLootable) {
            ((TileEntityLootable) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }
        return this.getInventory(guiHandler, window, te);
    }

    public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, IInventory tileEntity) {
        return new GUIInventoryImpl<>(guiHandler, window, tileEntity);
    }

    //---------------------------Sub-Classes------------------------------------

    public static class BlastFurnace extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntityBlastFurnace();
        }
    }

    public static class BrewingStand extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntityBrewingStand();
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
            IInventory tileEntity = this.getTileEntity();
            if (tileEntity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            }
            return this.getInventory(guiHandler, window, tileEntity);
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, IInventory tileEntity) {
            return new GUIInventoryBrewer<>(guiHandler, window, tileEntity);
        }
    }

    public static class Dispenser extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntityDispenser();
        }
    }

    public static class Dropper extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntityDropper();
        }
    }

    public static class Furnace extends GUITileInventoryConverter {

        @Override
        public IInventory getTileEntity() {
            TileEntityFurnace furnace = new TileEntityFurnaceFurnace();
            furnace.setWorld(MinecraftServer.getServer().getWorldServer(DimensionManager.OVERWORLD));
            return furnace;
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
            IInventory tileEntity = this.getTileEntity();
            ((TileEntityFurnace) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return this.getInventory(guiHandler, window, tileEntity);
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, IInventory tileEntity) {
            return new GUIInventoryFurnace<>(guiHandler, window, (TileEntityFurnace) tileEntity);
        }
    }

    public static class Hopper extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntityHopper();
        }
    }

    public static class Lectern extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return (new TileEntityLectern()).inventory;
        }
    }

    public static class Smoker extends GUITileInventoryConverter {
        @Override
        public IInventory getTileEntity() {
            return new TileEntitySmoker();
        }
    }
}
