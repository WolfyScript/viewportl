package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;

@Deprecated
public class LangConfiguration extends JsonConfiguration {

    public LangConfiguration(ConfigAPI configAPI, String name) {
        this(configAPI, name, "lang", name, false);
    }

    public LangConfiguration(ConfigAPI configAPI, String name, String defaultPath, String defName, boolean overwrite) {
        super(configAPI, configAPI.getPlugin().getDataFolder().getPath() + "/lang", name, defaultPath, defName, overwrite);
    }
}
