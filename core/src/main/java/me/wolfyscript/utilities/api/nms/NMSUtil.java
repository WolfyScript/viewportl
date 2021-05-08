package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Reflection;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;

public abstract class NMSUtil {

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
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".NMSEntry";
            Class<?> numUtilsType = Class.forName(className);
            if (NMSUtil.class.isAssignableFrom(numUtilsType)) {
                Constructor<?> constructor = numUtilsType.getDeclaredConstructor(WolfyUtilities.class);
                constructor.setAccessible(true);
                return ((NMSUtil) constructor.newInstance(wolfyUtilities));
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        //Unsupported version
        return null; // unreachable
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
}
