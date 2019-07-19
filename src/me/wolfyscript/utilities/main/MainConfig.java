package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.YamlConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class MainConfig extends YamlConfig {

    MainConfig(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/utilities/main/configs", configAPI.getPlugin().getDataFolder().getPath(), "main_config");

    }

    @Override
    public void onFirstInit() {
        set("securityCode",  WolfyUtilities.getCode());
    }

    @Override
    public void init() {

    }
}
