package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.utils.Reflection;
import org.bukkit.plugin.Plugin;

public abstract class NMSUtil {

    private final Plugin plugin;

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the {@link Plugin} parameter.
     *
     * @param plugin
     */
    protected NMSUtil(Plugin plugin){
        this.plugin = plugin;
    }

    public Plugin getPlugin(){
        return this.plugin;
    }

    /**
     * Creates an instance of the specific NMSUtil of the current Minecraft version.
     *
     * @param plugin
     * @return
     */
    public static NMSUtil create(Plugin plugin) {
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
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".NMSUtilsImpl";
            Class<?> numUtilsType = Class.forName(className);
            if (NMSUtil.class.isAssignableFrom(numUtilsType)) {
                return ((NMSUtil) numUtilsType.getDeclaredConstructor(Plugin.class).newInstance(plugin));
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        //Unsupported version
        return null; // unreachable
    }


}
