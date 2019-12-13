package me.wolfyscript.utilities.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ConfigAPI {

    private Plugin plugin;
    private WolfyUtilities api;
    private boolean prettyPrinting = false;

    private HashMap<String, Configuration> configs;

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

    private void runAutoSave(int intervalInMin) {
        autoSave = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Configuration configuration : configs.values()) {
                if (configuration instanceof YamlConfiguration) {
                    ((YamlConfiguration) configuration).reloadAuto();
                }
            }
        }, 1200, intervalInMin * 60 * 20);
    }

    public void registerConfig(Configuration configuration) {
        configs.put(configuration.getName(), configuration);
    }

    public Configuration getConfig(String name) {
        return configs.get(name);
    }

    public YamlConfiguration getmainConfig() {
        if (getConfig("main_config") instanceof YamlConfiguration) {
            return (YamlConfiguration) getConfig("main_config");
        }
        return null;
    }

    public static void exportFile(Class reference, String resourcePath, String savePath) {
        InputStream ddlStream = reference.getClassLoader().getResourceAsStream(resourcePath);
        File target = new File(savePath);
        try {
            target.createNewFile();
            FileOutputStream fos = new FileOutputStream(savePath);
            byte[] buf = new byte[2048];
            int r;
            while ((r = ddlStream.read(buf)) != -1) {
                fos.write(buf, 0, r);
            }
        } catch (IOException e) {
            //EMPTY
        }
    }

    /*
    This must be called onDisable().
    So that all configs are saved!
    It can be called from everywhere, but it's not useful.
     */
    public void saveConfigs() {
        for (Configuration configuration : configs.values()) {
            if (configuration instanceof FileConfiguration) {
                ((FileConfiguration) configuration).save();
            }
        }
    }

    public void loadConfigs() {
        for (Configuration configuration : configs.values()) {
            if (configuration instanceof FileConfiguration) {
                ((FileConfiguration) configuration).load();
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
