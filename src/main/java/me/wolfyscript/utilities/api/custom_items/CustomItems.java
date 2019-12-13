package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CustomItems {

    private static HashMap<String, CustomItem> customItems = new HashMap<>();
    private static HashMap<Location, String> storedBlocks = new HashMap<>();

    public CustomItems(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::save, 12000, 12000);
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

    public static void setCustomItem(ItemConfig itemConfig) {
        customItems.put(itemConfig.getId(), new CustomItem(itemConfig));
    }

    //StoredBlocks Methods
    public static boolean isBlockStored(Location location) {
        if (storedBlocks.containsKey(location)) {
            return storedBlocks.containsKey(location);
        } else {
            for (Location location1 : storedBlocks.keySet()) {
                if (location1.equals(location)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public static CustomItem getStoredBlockItem(Location location) {
        for (Map.Entry<Location, String> entry : storedBlocks.entrySet()) {
            if (entry.getKey().equals(location)) {
                return CustomItems.getCustomItem(entry.getValue());
            }
        }
        return null;
    }

    public static void setStoredBlockItem(Location location, CustomItem customItem) {
        storedBlocks.put(location, customItem.getId());
    }

    public static void setStoredBlockItem(Location location, String id) {
        storedBlocks.put(location, id);
    }

    public static void removeStoredBlockItem(Location location) {
        storedBlocks.remove(location);
    }

    private static String locationToString(Location location) {
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(Main.getInstance().getDataFolder() + File.separator + "stored_block_items.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            HashMap<String, String> saveMap = new HashMap<>();
            for (Map.Entry<Location, String> entry : storedBlocks.entrySet()) {
                saveMap.put(locationToString(entry.getKey()), entry.getValue());
            }
            oos.writeObject(saveMap);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "stored_block_items.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    HashMap<String, String> loadMap = (HashMap<String, String>) object;
                    for (Map.Entry<String, String> entry : loadMap.entrySet()) {
                        storedBlocks.put(stringToLocation(entry.getKey()), entry.getValue());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
