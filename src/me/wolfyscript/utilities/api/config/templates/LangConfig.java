package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.plugin.Plugin;

public class LangConfig extends Config {

    public LangConfig(ConfigAPI configAPI, String name) {
        this(configAPI, "lang",  name);
    }

    public LangConfig(ConfigAPI configAPI, String defaultPath, String name) {
        super(configAPI, defaultPath, configAPI.getPlugin().getDataFolder().getPath()+"/lang", name);
    }

    @Override
    public void onFirstInit() {

    }

    @Override
    public void init() {
        configAPI.registerConfig(this);
    }
}
