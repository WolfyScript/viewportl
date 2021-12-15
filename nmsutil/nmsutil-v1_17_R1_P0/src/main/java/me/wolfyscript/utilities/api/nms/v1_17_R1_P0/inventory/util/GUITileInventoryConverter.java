package me.wolfyscript.utilities.api.nms.v1_17_R1_P0.inventory.util;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_17_R1_P0.inventory.GUIInventoryBrewer;
import me.wolfyscript.utilities.api.nms.v1_17_R1_P0.inventory.GUIInventoryFurnace;
import me.wolfyscript.utilities.api.nms.v1_17_R1_P0.inventory.GUIInventoryImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUITileInventoryConverter implements GUIInventoryCreator.InventoryConverter {

    public GUITileInventoryConverter() {
    }

    public abstract Container getTileEntity();

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type) {
        return this.getInventory(guiHandler, window, this.getTileEntity());
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
        Container te = this.getTileEntity();
        if (te instanceof RandomizableContainerBlockEntity) {
            ((RandomizableContainerBlockEntity) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }
        return this.getInventory(guiHandler, window, te);
    }

    public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, Container tileEntity) {
        return new GUIInventoryImpl<>(guiHandler, window, tileEntity);
    }

    //---------------------------Sub-Classes------------------------------------

    public static class Furnace extends GUITileInventoryConverter {

        @Override
        public Container getTileEntity() {
            return new FurnaceBlockEntity(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState());
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
            Container tileEntity = this.getTileEntity();
            ((FurnaceBlockEntity) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return this.getInventory(guiHandler, window, tileEntity);
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, Container tileEntity) {
            return new GUIInventoryFurnace<>(guiHandler, window, (FurnaceBlockEntity) tileEntity);
        }
    }

    public static class BlastFurnace extends GUITileInventoryConverter {

        @Override
        public Container getTileEntity() {
            return new BlastFurnaceBlockEntity(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState());
        }

    }

    public static class Smoker extends GUITileInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new SmokerBlockEntity(BlockPos.ZERO, Blocks.SMOKER.defaultBlockState());
        }
    }

    public static class BrewingStand extends GUITileInventoryConverter {

        @Override
        public Container getTileEntity() {
            return new BrewingStandBlockEntity(BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState());
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
            Container tileEntity = this.getTileEntity();
            if (tileEntity instanceof BrewingStandBlockEntity) {
                ((BrewingStandBlockEntity) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            }
            return this.getInventory(guiHandler, window, tileEntity);
        }

        @Override
        public <C extends CustomCache> GUIInventory<C> getInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, Container tileEntity) {
            return new GUIInventoryBrewer<>(guiHandler, window, tileEntity);
        }

    }

    public static class Dispenser extends GUITileInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new DispenserBlockEntity(BlockPos.ZERO, Blocks.DISPENSER.defaultBlockState());
        }

    }

    public static class Dropper extends GUITileInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new DropperBlockEntity(BlockPos.ZERO, Blocks.DROPPER.defaultBlockState());
        }

    }

    public static class Hopper extends GUITileInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new HopperBlockEntity(BlockPos.ZERO, Blocks.HOPPER.defaultBlockState());
        }
    }

    public static class Lectern extends GUITileInventoryConverter {
        @Override
        public Container getTileEntity() {
            return (new LecternBlockEntity(BlockPos.ZERO, Blocks.LECTERN.defaultBlockState())).bookAccess;
        }
    }
}
