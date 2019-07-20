package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class LangConfiguration extends YamlConfiguration {

    public LangConfiguration(ConfigAPI configAPI, String name) {
        this(configAPI, "lang",  name);
    }

    public LangConfiguration(ConfigAPI configAPI, String defaultPath, String name) {
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
