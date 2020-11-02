package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.custom_items.api_references.*;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.MessageChannelHandler;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemCategory;
import me.wolfyscript.utilities.api.utils.json.gson.GsonUtil;
import me.wolfyscript.utilities.api.utils.json.gson.serialization.*;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.json.jackson.serialization.APIReferenceSerialization;
import me.wolfyscript.utilities.api.utils.json.jackson.serialization.VectorSerialization;
import me.wolfyscript.utilities.api.utils.particles.Particle;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.api.utils.particles.Particles;
import me.wolfyscript.utilities.main.commands.InputCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomDurabilityListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomParticleListener;
import me.wolfyscript.utilities.main.messages.InputButtonMessage;
import me.wolfyscript.utilities.main.messages.WolfyUtilitiesVerifyMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;

public class WUPlugin extends JavaPlugin {

    private static WUPlugin instance;

    private static int mcUpdateVersionNumber;
    private static String mcUpdateVersion;

    private static WolfyUtilities wolfyUtilities;
    private static MainConfiguration mainConfig;

    private static Particles particlesConfig;
    private static ParticleEffects particleEffectsConfig;

    private MessageChannelHandler messageChannelHandler;

    public static void loadParticleEffects() throws IOException {
        wolfyUtilities.sendConsoleMessage("Loading Particles...");
        particlesConfig = new Particles(instance);
        particlesConfig.load();
        for (Map.Entry<NamespacedKey, Particle> particleEntry : Particles.getParticles().entrySet()) {
            wolfyUtilities.sendDebugMessage("  - " + particleEntry.getKey() + " -> " + particleEntry.getValue());
        }
        particleEffectsConfig = new ParticleEffects(instance);
        particleEffectsConfig.load();
        for (Map.Entry<NamespacedKey, ParticleEffect> effectEntry : ParticleEffects.getEffects().entrySet()) {
            wolfyUtilities.sendDebugMessage("  - " + effectEntry.getKey() + " -> " + effectEntry.getValue().getParticles());
        }
        CustomItems.initiateMissingBlockEffects();
    }

    public static WUPlugin getInstance() {
        return instance;
    }

    public static WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void onLoad() {
        instance = this;
        String pkgname = WUPlugin.getInstance().getServer().getClass().getPackage().getName();
        mcUpdateVersion = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
        mcUpdateVersionNumber = Integer.parseInt(mcUpdateVersion);

        //Jackson Serializer
        SimpleModule module = new SimpleModule();
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.ItemStackSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.ColorSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.DustOptionsSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.LocationSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.ParticleContentSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.ParticleEffectSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.ParticleSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.PotionEffectTypeSerialization.create(module);
        me.wolfyscript.utilities.api.utils.json.jackson.serialization.PotionEffectSerialization.create(module);
        VectorSerialization.create(module);

        //Reference Deserializer
        APIReferenceSerialization.create(module);
        module.addDeserializer(ItemsAdderRef.class, new ItemsAdderRef.Serialization());
        module.addDeserializer(MMOItemsRef.class, new MMOItemsRef.Serialization());
        module.addDeserializer(MythicMobsRef.class, new MythicMobsRef.Serialization());
        module.addDeserializer(OraxenRef.class, new OraxenRef.Serialization());
        module.addDeserializer(VanillaRef.class, new VanillaRef.Serialization());
        module.addDeserializer(WolfyUtilitiesRef.class, new WolfyUtilitiesRef.Serialization());
        JacksonUtil.registerModule(module);

        //GSON serializations
        GsonUtil.registerTypeHierarchyAdapter(CustomItem.class, new CustomItemSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ItemMeta.class, new ItemMetaSerialization());
        GsonUtil.registerTypeHierarchyAdapter(Location.class, new LocationSerialization());
        GsonUtil.registerTypeHierarchyAdapter(Color.class, new ColorSerialization());
        GsonUtil.registerTypeHierarchyAdapter(org.bukkit.Particle.DustOptions.class, new DustOptionsSerialization());
        GsonUtil.registerTypeHierarchyAdapter(Particle.class, new ParticleSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ParticleEffect.class, new ParticleEffectSerialization());
        GsonUtil.registerTypeHierarchyAdapter(ParticleContent.class, new ParticleContentSerialization());

        //Register custom item data
    }

    public void onEnable() {
        wolfyUtilities = WolfyUtilities.getOrCreateAPI(instance);
        wolfyUtilities.setCONSOLE_PREFIX("[WU] ");
        wolfyUtilities.setCHAT_PREFIX("§8[§3WU§8] §7");

        ConfigAPI configAPI = wolfyUtilities.getConfigAPI();
        LanguageAPI languageAPI = wolfyUtilities.getLanguageAPI();

        mainConfig = new MainConfiguration(configAPI);
        configAPI.registerConfig(mainConfig);

        saveResource("lang/en_US.json", true);
        try {
            languageAPI.setActiveLanguage(new Language(this, "en_US"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WolfyUtilities.getCustomItems().load();
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new WolfyUtilities(this), this);
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand());
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand());
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand());

        Metrics metrics = new Metrics(this, 5114);

        try {
            ItemCategory.init();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        saveResource("particles/scripts/flame_spiral_down.js", true);
        saveResource("particles/README.txt", true);

        try {
            loadParticleEffects();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageChannelHandler = new MessageChannelHandler(this, new NamespacedKey("wolfyutilities", "main"));
        messageChannelHandler.registerMessage(1, WolfyUtilitiesVerifyMessage.class, (message, output) -> {
            output.writeBoolean(message.hasWolfyUtilities());
            output.writeUTF(message.getVersion());
        }, in -> new WolfyUtilitiesVerifyMessage(false, ""), (message, player) -> {
            if (player.hasPermission("wolfyutilities.network.verify_plugin")) {
                messageChannelHandler.sendTo(player, new WolfyUtilitiesVerifyMessage(true, getDescription().getVersion()));
            }
        });

        messageChannelHandler.registerMessage(2, InputButtonMessage.class, (inputButtonMessage, output) -> {
            output.writeUTF(inputButtonMessage.getButtonID());
            output.writeUTF(inputButtonMessage.getMessage());
        }, null, (inputButtonMessage, player) -> {
        });

        //System.out.println("TestItem: "+ ItemUtils.serializeItemStack(new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_UNBREAKABLE).setDisplayName("LUL").addLoreLine("Test Item").create()));
    }

    public void onDisable() {
        wolfyUtilities.getConfigAPI().saveConfigs();
        WolfyUtilities.getCustomItems().save();
        try {
            particlesConfig.save(false);
            particleEffectsConfig.save(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wolfyutils")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                wolfyUtilities.sendPlayerMessage(p, "~*~*~*~*&8[&3&lWolfyUtilities&8]&7~*~*~*~*~");
                wolfyUtilities.sendPlayerMessage(p, "");
                wolfyUtilities.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
                wolfyUtilities.sendPlayerMessage(p, "        ------------------");
                wolfyUtilities.sendPlayerMessage(p, "");
                wolfyUtilities.sendPlayerMessage(p, "             &nVersion:&r&b " + getDescription().getVersion());
                wolfyUtilities.sendPlayerMessage(p, "");
                wolfyUtilities.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
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

    public String getMcUpdateVersion() {
        return mcUpdateVersion;
    }

    public static ParticleEffects getParticleEffects() {
        return particleEffectsConfig;
    }

    public static Particles getParticles() {
        return particlesConfig;
    }

    public MessageChannelHandler getPacketHandler() {
        return messageChannelHandler;
    }
}
