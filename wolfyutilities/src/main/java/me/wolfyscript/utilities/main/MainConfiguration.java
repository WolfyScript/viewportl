package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.util.EncryptionUtils;

public class MainConfiguration extends Config {

    MainConfiguration(ConfigAPI configAPI) {
        super(configAPI, configAPI.getPlugin().getDataFolder().getPath(), "main_config", "me/wolfyscript/utilities/main/configs", "main_config", "json", false);

    }

    @Override
    public void onFirstInit() {
        set("securityCode", EncryptionUtils.getCode());
    }


}
