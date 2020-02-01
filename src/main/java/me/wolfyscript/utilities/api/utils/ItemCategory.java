package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.main.Main;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    private static HashMap<String, List<Material>> materials = new HashMap<>();

    public static void init() throws NoSuchMethodException {
        Main.getMainUtil().sendConsoleMessage("Loading Item Categories...");
        Class<?> craftMagicNumbersClass = Reflection.getOBC("util.CraftMagicNumbers");
        Class<?> creativeModeTabClass = Reflection.getNMS("CreativeModeTab");
        Class<?> itemClass = Reflection.getNMS("Item");

        Method getItem = craftMagicNumbersClass.getMethod("getItem", Material.class);
        Field getCreativeModeTab = Reflection.findField(itemClass, creativeModeTabClass);
        getCreativeModeTab.setAccessible(true);
        Method creativeModeToString = creativeModeTabClass.getMethod("c");

        if (getItem != null && creativeModeToString != null) {
            try {
                for (Material material : Material.values()) {
                    Object itemObj = getItem.invoke(craftMagicNumbersClass, material);
                    if (itemObj != null) {
                        Object creativeModeTabObj = getCreativeModeTab.get(itemObj);
                        if (creativeModeTabObj != null) {
                            String name = (String) creativeModeToString.invoke(creativeModeTabObj);
                            List<Material> category = materials.getOrDefault(name, new ArrayList<>());
                            category.add(material);
                            //Main.getMainUtil().sendConsoleMessage(" register " + material + " -> " + name);
                            materials.put(name, category);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    ItemCategory() {

    }

    public boolean isValid(Material material){
        List<Material> category = materials.get(toString().toLowerCase(Locale.ROOT));
        if(category == null){
            Main.getMainUtil().sendConsoleWarning("Invalid category: "+toString().toLowerCase(Locale.ROOT));
        }
        return category.contains(material);
    }

    public static boolean isValid(Material material, ItemCategory itemCategory){
        List<Material> category = materials.get(itemCategory.toString().toLowerCase(Locale.ROOT));
        if(category != null){
            return category.contains(material);
        }
        return false;
    }

    public static ItemCategory getCategory(Material material){
        for(Map.Entry<String, List<Material>> entry : materials.entrySet()){
            if(entry.getValue().contains(material)){
                return valueOf(entry.getKey());
            }
        }
        return SEARCH;
    }

    public static HashMap<String, List<Material>> getMaterials() {
        return materials;
    }
}
