package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.plugin.Plugin;

public class MainConfig extends Config {

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
