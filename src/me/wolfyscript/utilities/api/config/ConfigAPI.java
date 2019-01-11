package me.wolfyscript.utilities.api.config;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ConfigAPI {

    private Plugin plugin;

    private HashMap<String, Config> configs;

    private int autoSave = -1;

    public ConfigAPI(Plugin plugin){
        this.plugin = plugin;
        this.configs = new HashMap<>();
    }

    public ConfigAPI(Plugin plugin, boolean enableAutoSave, int intervalInMin){
        this(plugin);
        setAutoSave(enableAutoSave, intervalInMin);
    }

    public void setAutoSave(boolean enabled, int intervalInMin){
        if(autoSave == -1 && enabled){
            runAutoSave(intervalInMin);
        }else if(!enabled){
            stopAutoSave();
        }else{
            stopAutoSave();
            runAutoSave(intervalInMin);
        }
    }

    public void stopAutoSave(){
        if(plugin.getServer().getScheduler().isCurrentlyRunning(autoSave)){
            plugin.getServer().getScheduler().cancelTask(autoSave);
        }
    }

    private void runAutoSave(int intervalInMin){
        autoSave = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for(Config config : configs.values()){
                config.reloadAuto();
            }
        }, 1200, intervalInMin * 60 * 20);
    }

    public void registerConfig(Config config){
        configs.put(config.getName(), config);
    }

    public Config getConfig(String name){
        return configs.get(name);
    }

    /*
    This must be called onDisable().
    So that all configs are saved!
    It can be called from everywhere, but it's not useful.
     */
    public void saveConfigs(){
        for(Config config : configs.values()){
            config.save();
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
