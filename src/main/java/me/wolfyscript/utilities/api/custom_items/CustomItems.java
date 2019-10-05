package me.wolfyscript.utilities.api.custom_items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomItems {

    private static HashMap<String, CustomItem> customItems = new HashMap<>();

    public CustomItems(){

    }

    public static List<CustomItem> getCustomItems() {
        return new ArrayList<>(customItems.values());
    }

    public static CustomItem getCustomItem(String key) {
        return getCustomItem(key, true);
    }

    public static CustomItem getCustomItem(String key, boolean replace) {
        if (customItems.containsKey(key) && customItems.get(key) != null) {
            if (replace)
                return customItems.get(key).getRealItem();
            return customItems.get(key).clone();
        }
        return null;
    }

    public static void addCustomItem(CustomItem item) {
        customItems.put(item.getId(), item);
    }

    public static void removeCustomItem(String id) {
        customItems.remove(id);
    }

    public static void removeCustomItem(CustomItem item) {
        customItems.remove(item.getId());
    }

    public static CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }

    public static CustomItem getCustomItem(String key, String name, boolean replace) {
        return getCustomItem(key + ":" + name, replace);
    }

    public static void setCustomItem(ItemConfig itemConfig){
        customItems.put(itemConfig.getId(), new CustomItem(itemConfig));
    }
}
