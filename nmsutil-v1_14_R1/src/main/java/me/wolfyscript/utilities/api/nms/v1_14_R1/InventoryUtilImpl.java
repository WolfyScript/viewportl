package me.wolfyscript.utilities.api.nms.v1_14_R1;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_14_R1.inventory.util.GUIInventoryCreator;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import net.minecraft.server.v1_14_R1.IRecipe;
import net.minecraft.server.v1_14_R1.Item;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Locale;
import java.util.Optional;

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
    public void setCurrentRecipe(Inventory inventory, NamespacedKey recipe) {
        CraftInventory craftInventory = (CraftInventory) inventory;
        Optional<? extends IRecipe<?>> recipeOptional = MinecraftServer.getServer().getCraftingManager().a(CraftNamespacedKey.toMinecraft(recipe));
        craftInventory.getInventory().setCurrentRecipe(recipeOptional.orElse(null));
    }

    @Override
    public void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            Item item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                net.minecraft.server.v1_14_R1.CreativeModeTab creativeModeTab = item.p();
                if (creativeModeTab != null) {
                    CreativeModeTab category = CreativeModeTab.valueOf(creativeModeTab.c().toUpperCase(Locale.ROOT));
                    category.registerMaterial(material);
                }
            }

        }
    }
}
