package me.wolfyscript.utilities.util.world;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WorldUtils {

    private static WorldCustomItemStore worldCustomItemStore;

    public static WorldCustomItemStore getWorldCustomItemStore() {
        return worldCustomItemStore;
    }

    /**
     * Loads the store from the file.
     */
    public static void save() {
        try {
            FileOutputStream fos = new FileOutputStream(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "world_custom_item.storage");
            GZIPOutputStream gzip = new GZIPOutputStream(fos);
            JacksonUtil.getObjectWriter(false).writeValue(gzip, worldCustomItemStore);
            gzip.flush();
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the stored Custom Item into a file.
     */
    public static void load() {
        File file = new File(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "world_custom_item.storage");
        if (file.exists()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                GZIPInputStream gzip = new GZIPInputStream(fin);
                worldCustomItemStore = JacksonUtil.getObjectMapper().readValue(gzip, WorldCustomItemStore.class);
                gzip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Load from the old file if the new one doesn't exist!
            loadOld();
            //And save it to make sure that it is saved in the new format.
            save();
        }
    }

    @Deprecated
    private static void loadOld() {
        File file = new File(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "stored_block_items.dat");
        WorldCustomItemStore worldCustomItemStore = new WorldCustomItemStore();
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    HashMap<String, String> loadMap = (HashMap<String, String>) object;
                    loadMap.forEach((key, value) -> {
                        Location location = stringToLocation(key);
                        if (location != null) {
                            CustomItem customItem = Registry.CUSTOM_ITEMS.get(NamespacedKey.getByString(value));
                            if (customItem != null) {
                                worldCustomItemStore.setStore(location, new BlockCustomItemStore(customItem, null));
                            }
                        }
                    });
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try {
            UUID uuid = UUID.fromString(args[0]);
            World world = Bukkit.getWorld(uuid);
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Couldn't find world " + args[0]);
        }
        return null;
    }

}
