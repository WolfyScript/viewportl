package me.wolfyscript.utilities.api.nms.v1_16_R3.inventory.util;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_16_R3.inventory.util.CraftTileInventoryConverter;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUITileInventoryConverter implements GUIInventoryCreator.InventoryConverter {

    public GUITileInventoryConverter() {
    }

    public abstract IInventory getTileEntity();

    public Inventory createInventory(InventoryHolder holder, InventoryType type) {
        return this.getInventory(this.getTileEntity());
    }

    public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
        IInventory te = this.getTileEntity();
        if (te instanceof TileEntityLootable) {
            ((TileEntityLootable) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }

        return this.getInventory(te);
    }

    public Inventory getInventory(IInventory tileEntity) {
        return new CraftInventory(tileEntity);
    }

    public static class BlastFurnace extends CraftTileInventoryConverter {
        public BlastFurnace() {
        }

        public IInventory getTileEntity() {
            return new TileEntityBlastFurnace();
        }
    }

    public static class BrewingStand extends CraftTileInventoryConverter {
        public BrewingStand() {
        }

        public IInventory getTileEntity() {
            return new TileEntityBrewingStand();
        }

        public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
            IInventory tileEntity = this.getTileEntity();
            if (tileEntity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            }

            return this.getInventory(tileEntity);
        }

        public Inventory getInventory(IInventory tileEntity) {
            return new CraftInventoryBrewer(tileEntity);
        }
    }

    public static class Dispenser extends CraftTileInventoryConverter {
        public Dispenser() {
        }

        public IInventory getTileEntity() {
            return new TileEntityDispenser();
        }
    }

    public static class Dropper extends CraftTileInventoryConverter {
        public Dropper() {
        }

        public IInventory getTileEntity() {
            return new TileEntityDropper();
        }
    }

    public static class Furnace extends CraftTileInventoryConverter {
        public Furnace() {
        }

        public IInventory getTileEntity() {
            TileEntityFurnace furnace = new TileEntityFurnaceFurnace();
            furnace.setLocation(MinecraftServer.getServer().getWorldServer(World.OVERWORLD), BlockPosition.ZERO);
            return furnace;
        }

        public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
            IInventory tileEntity = this.getTileEntity();
            ((TileEntityFurnace) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return this.getInventory(tileEntity);
        }

        public Inventory getInventory(IInventory tileEntity) {
            return new CraftInventoryFurnace((TileEntityFurnace) tileEntity);
        }
    }

    public static class Hopper extends CraftTileInventoryConverter {
        public Hopper() {
        }

        public IInventory getTileEntity() {
            return new TileEntityHopper();
        }
    }

    public static class Lectern extends CraftTileInventoryConverter {
        public Lectern() {
        }

        public IInventory getTileEntity() {
            return (new TileEntityLectern()).inventory;
        }
    }

    public static class Smoker extends CraftTileInventoryConverter {
        public Smoker() {
        }

        public IInventory getTileEntity() {
            return new TileEntitySmoker();
        }
    }
}
