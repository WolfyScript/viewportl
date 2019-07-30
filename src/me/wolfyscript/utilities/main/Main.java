package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.templates.LangConfiguration;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.Legacy;
import me.wolfyscript.utilities.main.listeners.ItemListener;
import me.wolfyscript.utilities.main.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static Main instance;

    private static List<WolfyUtilities> wolfyUtilitiesList = new ArrayList<>();

    private static WolfyUtilities mainUtil;
    private static MainConfiguration mainConfig;

    public void onLoad() {
        instance = this;
        Legacy.init();
    }

    public void onEnable() {
        mainUtil = new WolfyUtilities(instance);
        mainUtil.setCONSOLE_PREFIX("[WU] ");
        mainUtil.setCHAT_PREFIX("§8[§3WU§8] §7");
        WolfyUtilities.setLWC();
        WolfyUtilities.setPlotSquared();
        WolfyUtilities.setWorldGuard();
        ConfigAPI configAPI = mainUtil.getConfigAPI();
        LanguageAPI languageAPI = mainUtil.getLanguageAPI();
        InventoryAPI inventoryAPI = mainUtil.getInventoryAPI();

        mainConfig = new MainConfiguration(configAPI);
        configAPI.registerConfig(mainConfig);
        languageAPI.setActiveLanguage(new Language("en_US", new LangConfiguration(configAPI, "en_US", "me/wolfyscript/utilities/main/configs/lang", "en_US", "yml", false), configAPI));

        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);

        Metrics metrics = new Metrics(this);

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wolfyutils")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                mainUtil.sendPlayerMessage(p, "~*~*~*~*&8[&3&lWolfyUtilities&8]&7~*~*~*~*~");
                mainUtil.sendPlayerMessage(p, "");
                mainUtil.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
                mainUtil.sendPlayerMessage(p, "        ------------------");
                mainUtil.sendPlayerMessage(p, "");
                mainUtil.sendPlayerMessage(p, "             &nVersion:&r&b "+getDescription().getVersion());
                mainUtil.sendPlayerMessage(p, "");
                mainUtil.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
                return true;
            }
        }
        return true;
    }

    public static MainConfiguration getMainConfig() {
        return mainConfig;
    }
}
