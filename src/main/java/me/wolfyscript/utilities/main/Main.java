package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.serialization.*;
import me.wolfyscript.utilities.api.config.templates.LangConfiguration;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ParticleData;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.GsonUtil;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import me.wolfyscript.utilities.api.utils.Legacy;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.ItemListener;
import me.wolfyscript.utilities.main.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;

    private static int mcUpdateVersionNumber;
    private static String mcUpdateVersion;

    private static WolfyUtilities mainUtil;
    private static MainConfiguration mainConfig;

    private static Particles particlesConfig;
    private static ParticleEffects particleEffectsConfig;

    public void onLoad() {
        instance = this;
        String pkgname = Main.getInstance().getServer().getClass().getPackage().getName();
        mcUpdateVersion = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
        mcUpdateVersionNumber = Integer.parseInt(mcUpdateVersion);
        Legacy.init();

        //ItemStack serialization
        GsonUtil.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerialization());
        GsonUtil.registerTypeHierarchyAdapter(Location.class, new LocationSerialization());
        GsonUtil.registerTypeHierarchyAdapter(Particle.class, new ParticleSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ParticleEffect.class, new ParticleEffectSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ParticleData.class, new ParticleDataSerialization());

        //Register custom item data
    }

    public void onEnable() {
        mainUtil = WolfyUtilities.getOrCreateAPI(instance);
        mainUtil.setCONSOLE_PREFIX("[WU] ");
        mainUtil.setCHAT_PREFIX("§8[§3WU§8] §7");

        ConfigAPI configAPI = mainUtil.getConfigAPI();
        LanguageAPI languageAPI = mainUtil.getLanguageAPI();

        mainConfig = new MainConfiguration(configAPI);
        configAPI.registerConfig(mainConfig);
        languageAPI.setActiveLanguage(new Language("en_US", new LangConfiguration(configAPI, "en_US", "me/wolfyscript/utilities/main/configs/lang", "en_US", "yml", false), configAPI));

        WolfyUtilities.getCustomItems().load();
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand());

        Metrics metrics = new Metrics(this);

        try {
            ItemCategory.init();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        getMainUtil().sendConsoleMessage("Registering Item Categories...");
        for (Map.Entry<String, List<Material>> category : ItemCategory.getMaterials().entrySet()) {
            getMainUtil().sendDebugMessage("  " + category.getKey() + ": " + category.getValue());
        }
        getMainUtil().sendConsoleMessage("Registering Particles...");
        particlesConfig = new Particles(configAPI);
        particlesConfig.loadParticles();
        for (Map.Entry<NamespacedKey, Particle> particleEntry : Particles.getParticles().entrySet()) {
            getMainUtil().sendConsoleWarning("  - " + particleEntry.getKey() + " -> " + particleEntry.getValue().getParticle());
        }
        particleEffectsConfig = new ParticleEffects(configAPI);
        particleEffectsConfig.loadEffects();
        for (Map.Entry<NamespacedKey, ParticleEffect> effectEntry : ParticleEffects.getEffects().entrySet()) {
            getMainUtil().sendConsoleWarning("  - " + effectEntry.getKey() + " -> " + effectEntry.getValue().getParticles());
        }
        CustomItems.initiateMissingBlockEffects();
    }

    public void onDisable() {
        mainUtil.getConfigAPI().saveConfigs();
        WolfyUtilities.getCustomItems().save();
        particlesConfig.setParticles();
        particlesConfig.save();
        particleEffectsConfig.setEffects();
        particleEffectsConfig.save();
    }

    public static Main getInstance() {
        return instance;
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
                mainUtil.sendPlayerMessage(p, "             &nVersion:&r&b " + getDescription().getVersion());
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

    public static int getMcUpdateVersionNumber() {
        return mcUpdateVersionNumber;
    }

    public static String getMcUpdateVersion() {
        return mcUpdateVersion;
    }
}
