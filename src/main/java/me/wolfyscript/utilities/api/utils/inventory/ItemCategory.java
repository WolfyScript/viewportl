package me.wolfyscript.utilities.api.utils.inventory;

import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class contaions enums for the creative menu categories.
 * Each enum contaions the corresponding Materials of that category.
 */
public enum ItemCategory {

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

    protected List<Material> materials;

    ItemCategory() {
        this.materials = new ArrayList<>();
    }

    public static void init() throws NoSuchMethodException {
        WUPlugin.getWolfyUtilities().sendConsoleMessage("Loading Item Categories...");
        Class<?> craftMagicNumbersClass = Reflection.getOBC("util.CraftMagicNumbers");
        Class<?> creativeModeTabClass = Reflection.getNMS("CreativeModeTab");
        Class<?> itemClass = Reflection.getNMS("Item");

        Method getItem = craftMagicNumbersClass.getMethod("getItem", Material.class);
        Field getCreativeModeTab = Reflection.findField(itemClass, creativeModeTabClass);
        getCreativeModeTab.setAccessible(true);
        Method creativeModeToString = null;
        for (Method method : creativeModeTabClass.getMethods()) {
            if (method.getReturnType().equals(String.class)) {
                creativeModeToString = method;
                break;
            }
        }

        if (creativeModeToString == null) {
            WUPlugin.getInstance().getLogger().severe("Error loading Item categories! Can't find the specified Method to get ");
            return;
        }

        if (getItem != null && creativeModeToString != null && creativeModeToString != null) {
            try {
                for (Material material : Material.values()) {
                    Object itemObj = getItem.invoke(craftMagicNumbersClass, material);
                    if (itemObj != null) {
                        Object creativeModeTabObj = getCreativeModeTab.get(itemObj);
                        if (creativeModeTabObj != null) {
                            String name = (String) creativeModeToString.invoke(creativeModeTabObj);
                            ItemCategory category = ItemCategory.valueOf(name.toUpperCase(Locale.ROOT));
                            if (category != null) {
                                category.materials.add(material);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValid(Material material, ItemCategory itemCategory) {
        return itemCategory.isValid(material);
    }

    public static ItemCategory getCategory(Material material) {
        for (ItemCategory category : values()) {
            if (category.isValid(material)) return category;
        }
        return SEARCH;
    }

    public boolean isValid(Material material) {
        return materials.contains(material);
    }
}
