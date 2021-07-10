package me.wolfyscript.utilities.main.configs;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.main.WUPlugin;

public class WUConfig extends YamlConfiguration {

    public WUConfig(ConfigAPI configAPI, WUPlugin plugin) {
        super(configAPI, plugin.getDataFolder().getPath(), "config", "me/wolfyscript/utilities/main/configs", "config", false);
    }

    public boolean isAPIReferenceEnabled(APIReference.Parser<?> parser) {
        return getBoolean("api_references." + parser.getId(), true);
    }

}
