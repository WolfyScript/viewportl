package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.versioning.ServerVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * This abstract class is the plugin of WolfyUtils core. It bootstraps the actual WolfyCore and provides it via the service manager.<br>
 * That way you don't need to use the static getter to get the instance of the core.
 * <p>
 *     With v5 and the Paper Plugin introduction the Core implementation may be different on other server software.
 *     To get the API of the core you need to use the {@link org.bukkit.plugin.ServicesManager} provided by Bukkit.
 *     <pre><code>WolfyCore core = getServer().getServicesManager().load(WolfyCore.class);</code></pre>
 * </p>
 */
public abstract class WolfyCoreCommonBootstrap extends JavaPlugin {

    //Static reference to the instance of this class.
    private static WolfyCoreCommonBootstrap instance;
    private Metrics metrics;
    private final Reflections reflections;

    public WolfyCoreCommonBootstrap() {
        super();
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.reflections = initReflections();
        instance = this;
    }

    private Reflections initReflections() {
        return new Reflections(new ConfigurationBuilder()
                .forPackage("")
                .addUrls(ClasspathHelper.forClassLoader())
                .forPackage("com.wolfyscript", getClassLoader())
                .addClassLoaders(getClassLoader())
                .addScanners(Scanners.values()));
    }

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(WolfyCore.class, getCore(), this, ServicePriority.Highest);
        getCore().load();
    }

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this, 5114);
        getCore().enable();
    }

    @Override
    public void onDisable() {
        getCore().disable();
        getServer().getServicesManager().unregisterAll(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCoreCommonBootstrap getInstance() {
        return instance;
    }

    public abstract WolfyCoreCommon getCore();

    public Reflections getReflections() {
        return reflections;
    }

}
