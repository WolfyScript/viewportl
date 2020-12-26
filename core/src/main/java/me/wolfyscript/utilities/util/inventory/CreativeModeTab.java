package me.wolfyscript.utilities.util.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains enums for the creative menu tabs.
 * Each enum contains the corresponding Materials of that tab.
 */
public enum CreativeModeTab {

    BREWING,
    BUILDING_BLOCKS,
    DECORATIONS,
    COMBAT,
    TOOLS,
    REDSTONE,
    FOOD,
    TRANSPORTATION,
    MISC,
    SEARCH;

    private static boolean allowRegistry = true;
    protected List<Material> materials;

    CreativeModeTab() {
        this.materials = new ArrayList<>();
    }

    public static void init() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Item Categories...");
        WolfyUtilities.getWUCore().getNmsUtil().getInventoryUtil().initItemCategories();
        allowRegistry = false;
    }

    public static boolean isValid(Material material, CreativeModeTab creativeModeTab) {
        return creativeModeTab.isValid(material);
    }

    public static CreativeModeTab getCategory(Material material) {
        for (CreativeModeTab category : values()) {
            if (category.isValid(material)) return category;
        }
        return SEARCH;
    }

    public boolean isValid(Material material) {
        return materials.contains(material);
    }

    public void registerMaterial(Material material) {
        if (allowRegistry && !materials.contains(material)) {
            materials.add(material);
        }
    }
}
