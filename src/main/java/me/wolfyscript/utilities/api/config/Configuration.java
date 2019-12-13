package me.wolfyscript.utilities.api.config;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;

public abstract class Configuration {

    public WolfyUtilities api;
    public ConfigAPI configAPI;
    public Plugin plugin;
    private Type type;
    private String name;

    public Configuration(ConfigAPI configAPI, String name, Type type) {
        this.api = configAPI.getApi();
        this.configAPI = configAPI;
        this.plugin = configAPI.getPlugin();
        this.name = name;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected enum Type {
        YAML, JSON
    }
}
