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

package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public abstract class NMSUtil {

    private static final HashMap<String, VersionAdapter> versionAdapters = new HashMap<>();

    static {
        registerAdapter(new VersionAdapter("v1_17_R1"));
        registerAdapter(new VersionAdapter("v1_18_R1"));
    }

    public static void registerAdapter(VersionAdapter adapter) {
        versionAdapters.putIfAbsent(adapter.version, adapter);
    }

    private final WolfyUtilities wolfyUtilities;

    private final Plugin plugin;
    protected BlockUtil blockUtil;
    protected ItemUtil itemUtil;
    protected RecipeUtil recipeUtil;
    protected InventoryUtil inventoryUtil;
    protected NBTUtil nbtUtil;

    protected NetworkUtil networkUtil;

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the WolfyUtilities parameter.
     *
     * @param wolfyUtilities
     */
    protected NMSUtil(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = wolfyUtilities.getPlugin();
    }

    public Plugin getPlugin(){
        return this.plugin;
    }

    /**
     * Creates an instance of the specific NMSUtil of the current Minecraft version.
     *
     * @param wolfyUtilities
     * @return
     */
    public static NMSUtil create(WolfyUtilities wolfyUtilities) {
        String version = Reflection.getVersion();
        try {
            var adapter = versionAdapters.get(version);
            if (adapter != null) {
                version = adapter.getPackageName();
            }
            wolfyUtilities.getConsole().getLogger().info("NMS Version: " + version);
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".NMSEntry";
            Class<?> nmsUtilsType = Class.forName(className);
            if (NMSUtil.class.isAssignableFrom(nmsUtilsType)) {
                Constructor<?> constructor = nmsUtilsType.getDeclaredConstructor(WolfyUtilities.class);
                constructor.setAccessible(true);
                return ((NMSUtil) constructor.newInstance(wolfyUtilities));
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        //Unsupported version
        throw new UnsupportedOperationException("This server version (" + version + ") is not supported!");
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public BlockUtil getBlockUtil() {
        return blockUtil;
    }

    public ItemUtil getItemUtil() {
        return itemUtil;
    }

    public InventoryUtil getInventoryUtil() {
        return inventoryUtil;
    }

    public NBTUtil getNBTUtil() {
        return nbtUtil;
    }

    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }

    public NetworkUtil getNetworkUtil() {
        return networkUtil;
    }

    /**
     * Used to handle new types of NMS versions introduced with spigot 1.17 thanks to the use of Mojang mappings.
     */
    private static class VersionAdapter {

        protected final String version;

        public VersionAdapter(String entryVersion) {
            this.version = entryVersion;
        }

        public String getPackageName() {
            return version + "_P" + ServerVersion.getVersion().getPatch();
        }
    }
}
