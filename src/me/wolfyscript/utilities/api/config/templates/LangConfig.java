package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.YamlConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class LangConfig extends YamlConfig {

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
