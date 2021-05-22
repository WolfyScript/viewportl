package me.wolfyscript.utilities.api.config;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ConfigAPI {

    private final Plugin plugin;
    private final WolfyUtilities api;
    private boolean prettyPrinting = false;

    private final HashMap<String, YamlConfiguration> configs;

    private int autoSave = -1;

    public ConfigAPI(WolfyUtilities api) {
        this.api = api;
        this.plugin = api.getPlugin();
        this.configs = new HashMap<>();
    }

    public ConfigAPI(WolfyUtilities api, boolean enableAutoSave, int intervalInMin) {
        this(api);
        setAutoSave(enableAutoSave, intervalInMin);
    }

    public void setAutoSave(boolean enabled, int intervalInMin) {
        if (autoSave == -1 && enabled) {
            runAutoSave(intervalInMin);
        } else if (!enabled) {
            stopAutoSave();
        } else {
            stopAutoSave();
            runAutoSave(intervalInMin);
        }
    }

    public void stopAutoSave() {
        if (plugin.getServer().getScheduler().isCurrentlyRunning(autoSave)) {
            plugin.getServer().getScheduler().cancelTask(autoSave);
        }
    }

    public static void exportFile(Class<?> reference, String resourcePath, String savePath) {
        InputStream ddlStream = reference.getClassLoader().getResourceAsStream(resourcePath);
        if (ddlStream != null) {
            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                byte[] buf = new byte[2048];
                int r;
                while ((r = ddlStream.read(buf)) != -1) {
                    fos.write(buf, 0, r);
                }
                ddlStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runAutoSave(int intervalInMin) {
        autoSave = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (YamlConfiguration configuration : configs.values()) {
                if (configuration != null) {
                    configuration.reloadAuto();
                }
            }
        }, 1200, intervalInMin * 60L * 20L);
    }

    public void registerConfig(YamlConfiguration configuration) {
        configs.put(configuration.getName(), configuration);
    }

    public YamlConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public YamlConfiguration getCoreConfig() {
        if (getConfig("config") != null) {
            return getConfig("config");
        }
        return null;
    }

    /*
    This must be called onDisable().
    So that all configs are saved!
    It can be called from everywhere, but it's not useful.
     */
    public void saveConfigs() {
        for (YamlConfiguration configuration : configs.values()) {
            if (configuration != null) {
                configuration.save();
            }
        }
    }

    public void loadConfigs() {
        for (YamlConfiguration configuration : configs.values()) {
            if (configuration != null) {
                configuration.load();
            }
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public void setPrettyPrinting(boolean prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    public boolean isPrettyPrinting() {
        return prettyPrinting;
    }
}
