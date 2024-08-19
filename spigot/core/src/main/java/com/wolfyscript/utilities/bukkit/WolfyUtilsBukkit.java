package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.scafall.wrappers.world.items.Items;
import com.wolfyscript.utilities.bukkit.config.ConfigAPI;
import com.wolfyscript.utilities.bukkit.world.items.BookUtil;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.viewportl.gui.GuiAPIManager;
import com.wolfyscript.viewportl.gui.GuiAPIManagerImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.wolfyscript.utilities.language.Translations;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WolfyUtilsBukkit extends WolfyUtils {

    private final WolfyCoreCommon core;
    private final Plugin plugin;
    private final Permissions permissions;

    private String dataBasePrefix;
    private final ConfigAPI configAPI;
    private final GuiAPIManagerImpl guiAPIManager;

    WolfyUtilsBukkit(WolfyCoreCommon core, Plugin plugin) {
        this.core = core;
        this.plugin = plugin;
        this.permissions = new Permissions(this);
        this.dataBasePrefix = getName().toLowerCase(Locale.ROOT) + "_";
        this.configAPI = new ConfigAPI(this);
        this.guiAPIManager = new GuiAPIManagerImpl(this);
    }

    final void initialize() {
        permissions.initRootPerm(getName().toLowerCase(Locale.ROOT).replace(" ", "_") + ".*");
    }

    @Override
    public WolfyCoreSpigot getCore() {
        return (WolfyCoreSpigot) core;
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @NotNull
    @Override
    public Translations getTranslations() {
        return translations;
    }

    @NotNull
    @Override
    public GuiAPIManager getGuiManager() {
        return guiAPIManager;
    }

    /**
     * @return The {@link ConfigAPI} instance.
     * @see ConfigAPI More information about the Config API.
     */
    public ConfigAPI getConfigAPI() {
        return configAPI;
    }

    /**
     * @return The {@link Permissions} instance.
     * @see Permissions More information about Permissions
     */
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     * @return The plugin that this WolfyUtilities belongs to.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    public String getDataBasePrefix() {
        return dataBasePrefix;
    }

    public void setDataBasePrefix(String dataBasePrefix) {
        this.dataBasePrefix = dataBasePrefix;
    }

    @Override
    public void exportResources(String resourceName, File dir, boolean replace, Pattern filePattern) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        resourceName = resourceName.replace('\\', '/');

        Set<String> paths = getCore().getReflections().getResources(filePattern);
        for (String path : paths) {
            if (!path.startsWith(resourceName)) continue;
            URL url = plugin.getClass().getClassLoader().getResource(path);
            if (url == null) return;

            try {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                InputStream in = connection.getInputStream();
                if (in == null) throw new IllegalArgumentException("The embedded resource '" + url + "' cannot be found in " + plugin.getName());

                File destination = new File(dir, path.replace(resourceName, ""));
                try {
                    if (destination.exists() && !replace) return;
                    destination.getParentFile().mkdirs();
                    destination.createNewFile();
                    OutputStream out = new FileOutputStream(destination);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (IOException ex) {
                    getLogger().log(Level.SEVERE, "Could not save " + destination.getName() + " to " + destination, ex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exportResource(String resourcePath, File destination, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + plugin.getName());
        }

        final File outDir;
        if (destination.isDirectory()) {
            // Destination is a directory, so keep file name
            outDir = destination;
            destination = new File(destination, resourcePath.substring(resourcePath.lastIndexOf('/') + 1));
        } else {
            outDir = new File(destination.getPath().substring(0, Math.max(destination.getPath().lastIndexOf('/'), 0)));
        }

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!destination.exists() || replace) {
                OutputStream out = new FileOutputStream(destination);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + destination.getName() + " to " + destination, ex);
        }
    }

}
