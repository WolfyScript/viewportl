package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Reflection;
import org.bukkit.plugin.Plugin;

public abstract class NMSUtil {

    private final WolfyUtilities wolfyUtilities;
    private final Plugin plugin;

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
        /*
        try {
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".VersionedNMS";
            Class<?> versioningType = Class.forName(className);
            if (VersionedNMS.class.isAssignableFrom(versioningType)) {
                return ((VersionedNMS) versioningType.getDeclaredConstructor().newInstance()).createNMSUtils(plugin);
            }
        } catch (ClassNotFoundException ex) {
            // fallthrough, does not exist
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

         */

        try {
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".NMSEntry";
            Class<?> numUtilsType = Class.forName(className);
            if (NMSUtil.class.isAssignableFrom(numUtilsType)) {
                return ((NMSUtil) numUtilsType.getDeclaredConstructor(WolfyUtilities.class).newInstance(wolfyUtilities));
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

    public abstract BlockUtil getBlockUtil();

    public abstract ItemUtil getItemUtil();


}
