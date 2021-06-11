package me.wolfyscript.utilities.api.nms.v1_17_R1.inventory.util;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.EnumMap;
import java.util.Map;

public class GUIInventoryCreator {

    public static final GUIInventoryCreator INSTANCE = new GUIInventoryCreator();
    private final GUICustomInventoryConverter DEFAULT_CONVERTER = new GUICustomInventoryConverter();
    private final Map<InventoryType, InventoryConverter> converterMap = new EnumMap<>(InventoryType.class);

    private GUIInventoryCreator() {
        this.converterMap.put(InventoryType.CHEST, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.DISPENSER, new GUITileInventoryConverter.Dispenser());
        this.converterMap.put(InventoryType.DROPPER, new GUITileInventoryConverter.Dropper());
        this.converterMap.put(InventoryType.FURNACE, new GUITileInventoryConverter.Furnace());
        this.converterMap.put(InventoryType.WORKBENCH, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.ENCHANTING, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.BREWING, new GUITileInventoryConverter.BrewingStand());
        this.converterMap.put(InventoryType.PLAYER, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.MERCHANT, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.ENDER_CHEST, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.ANVIL, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.SMITHING, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.BEACON, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.HOPPER, new GUITileInventoryConverter.Hopper());
        this.converterMap.put(InventoryType.SHULKER_BOX, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.BARREL, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.BLAST_FURNACE, new GUITileInventoryConverter.BlastFurnace());
        this.converterMap.put(InventoryType.LECTERN, new GUITileInventoryConverter.Lectern());
        this.converterMap.put(InventoryType.SMOKER, new GUITileInventoryConverter.Smoker());
        this.converterMap.put(InventoryType.LOOM, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.CARTOGRAPHY, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.GRINDSTONE, this.DEFAULT_CONVERTER);
        this.converterMap.put(InventoryType.STONECUTTER, this.DEFAULT_CONVERTER);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type) {
        Validate.isTrue(this.converterMap.containsKey(type), "Cannot create GUI inventory of type ", type);
        return this.converterMap.get(type).createInventory(guiHandler, window, holder, type);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
        Validate.isTrue(this.converterMap.containsKey(type), "Cannot create GUI inventory of type ", type);
        return this.converterMap.get(type).createInventory(guiHandler, window, holder, type, title);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, int size) {
        return this.DEFAULT_CONVERTER.createInventory(guiHandler, window, holder, size);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, int size, String title) {
        return this.DEFAULT_CONVERTER.createInventory(guiHandler, window, holder, size, title);
    }

    public interface InventoryConverter {

        <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type);

        <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title);
    }
}
