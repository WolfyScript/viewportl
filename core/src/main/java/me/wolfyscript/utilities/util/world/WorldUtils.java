/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.world;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private WorldUtils() {
    }

    private static WorldCustomItemStore worldCustomItemStore;

    public static WorldCustomItemStore getWorldCustomItemStore() {
        return worldCustomItemStore;
    }

    /**
     * Save the stored Custom Item into a file.
     */
    public static void save() {
        try (var fos = new FileOutputStream(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "world_custom_item.store")) {
            var gzip = new GZIPOutputStream(fos);
            JacksonUtil.getObjectWriter(false).writeValue(gzip, worldCustomItemStore);
            gzip.flush();
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the store from the file.
     */
    public static void load() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading stored Custom Items");
        var file = new File(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "world_custom_item.store");
        if (file.exists()) {
            try (var fin = new FileInputStream(file)) {
                var gzip = new GZIPInputStream(fin);
                worldCustomItemStore = JacksonUtil.getObjectMapper().readValue(gzip, WorldCustomItemStore.class);
                if (worldCustomItemStore == null) {
                    WolfyUtilities.getWUPlugin().getLogger().severe("Couldn't load stored CustomItems! Resetting to default!");
                    worldCustomItemStore = new WorldCustomItemStore();
                }
                gzip.close();
            } catch (IOException e) {
                WolfyUtilities.getWUPlugin().getLogger().severe("Couldn't load stored CustomItems! Resetting to default!");
                worldCustomItemStore = new WorldCustomItemStore();
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
        var file = new File(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "stored_block_items.dat");
        worldCustomItemStore = new WorldCustomItemStore();
        if (file.exists()) {
            try (var fis = new FileInputStream(file); BukkitObjectInputStream ois = new BukkitObjectInputStream(fis)) {
                var object = ois.readObject();
                HashMap<String, String> loadMap = (HashMap<String, String>) object;
                loadMap.forEach((key, value) -> {
                    var location = stringToLocation(key);
                    if (location != null) {
                        worldCustomItemStore.setStore(location, new BlockCustomItemStore(NamespacedKey.of(value), null));
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try {
            var uuid = UUID.fromString(args[0]);
            var world = Bukkit.getWorld(uuid);
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Couldn't find world " + args[0]);
        }
        return null;
    }

}
