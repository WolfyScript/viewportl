package me.wolfyscript.utilities.util.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private static boolean register = true;
    @JsonIgnore
    protected List<Material> materials;

    public static boolean isValid(Material material, CreativeModeTab creativeModeTab) {
        return creativeModeTab.isValid(material);
    }

    public static CreativeModeTab getCategory(Material material) {
        for (CreativeModeTab tab : values()) {
            if (tab.isValid(material)) return tab;
        }
        return SEARCH;
    }

    CreativeModeTab() {
        this.materials = new ArrayList<>();
    }

    public static void init() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Creative Mode Tabs");
        WolfyUtilities.getWUCore().getNmsUtil().getInventoryUtil().initItemCategories();
        register = false;
    }

    public boolean isValid(Material material) {
        return materials.contains(material);
    }

    public void registerMaterial(Material material) {
        if (register && !materials.contains(material)) {
            materials.add(material);
        }
    }
}
