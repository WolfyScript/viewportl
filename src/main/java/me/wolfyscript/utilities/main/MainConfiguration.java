package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.utils.EncryptionUtils;

public class MainConfiguration extends YamlConfiguration {

    MainConfiguration(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/utilities/main/configs", configAPI.getPlugin().getDataFolder().getPath(), "main_config");

    }

    @Override
    public void onFirstInit() {
        set("securityCode", EncryptionUtils.getCode());
    }

    @Override
    public void init() {

    }
}
