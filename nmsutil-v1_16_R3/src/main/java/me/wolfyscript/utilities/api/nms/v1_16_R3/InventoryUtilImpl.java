package me.wolfyscript.utilities.api.nms.v1_16_R3;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_16_R3.inventory.util.GUIInventoryCreator;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import net.minecraft.server.v1_16_R3.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.event.inventory.InventoryType;

import java.util.Locale;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, type);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, type, title);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, size);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, size, title);
    }

    @Override
    public final void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            Item item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                net.minecraft.server.v1_16_R3.CreativeModeTab creativeModeTab = item.q();
                if (creativeModeTab != null) {
                    CreativeModeTab category = CreativeModeTab.valueOf(creativeModeTab.b().toUpperCase(Locale.ROOT));
                    category.registerMaterial(material);
                }
            }

        }
    }
}
