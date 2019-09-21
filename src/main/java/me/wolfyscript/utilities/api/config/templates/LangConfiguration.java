package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class LangConfiguration extends Config {

    public LangConfiguration(ConfigAPI configAPI, String name) {
        this(configAPI, name, "lang", name, "yml", false);
    }

    public LangConfiguration(ConfigAPI configAPI, String name, String defaultPath, String defName, String fileType, boolean overwrite) {
        super(configAPI, configAPI.getPlugin().getDataFolder().getPath() + "/lang", name, defaultPath, defName, fileType, overwrite);
    }
}
