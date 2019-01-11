package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.templates.LangConfig;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.Legacy;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static Main instance;

    private static List<WolfyUtilities> wolfyUtilitiesList = new ArrayList<>();

    private static WolfyUtilities mainUtil;

    public void onLoad() {
        instance = this;
        Legacy.init();
    }

    public void onEnable() {
        mainUtil = new WolfyUtilities(instance);
        ConfigAPI configAPI = mainUtil.getConfigAPI();
        LanguageAPI languageAPI = mainUtil.getLanguageAPI();
        InventoryAPI inventoryAPI = mainUtil.getInventoryAPI();

        configAPI.registerConfig(new MainConfig(configAPI));

        languageAPI.setActiveLanguage(new Language("en_US", new LangConfig(configAPI, "me/wolfyscript/utilities/main/configs/lang", "en_US"), configAPI));

    }

    public void onDisable() {
        mainUtil.getConfigAPI().saveConfigs();
    }

    public static Main getInstance() {
        return instance;
    }

    public static void registerWolfyUtilities(WolfyUtilities wolfyUtilities){
        if(!wolfyUtilitiesList.contains(wolfyUtilities)){
            wolfyUtilitiesList.add(wolfyUtilities);
        }
    }

    public static WolfyUtilities getMainUtil() {
        return mainUtil;
    }
}
