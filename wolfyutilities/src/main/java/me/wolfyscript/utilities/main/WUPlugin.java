package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.MessageChannelHandler;
import me.wolfyscript.utilities.main.commands.InputCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleAnimationCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.PlayerListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomDurabilityListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomParticleListener;
import me.wolfyscript.utilities.main.messages.InputButtonMessage;
import me.wolfyscript.utilities.main.messages.WolfyUtilitiesVerifyMessage;
import me.wolfyscript.utilities.main.particles.ParticleEffects;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.serialization.*;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class WUPlugin extends JavaPlugin {

    private static WUPlugin instance;

    public static WUPlugin getInstance() {
        return instance;
    }

    private WolfyUtilities wolfyUtilities;
    private Chat chat;

    private MessageChannelHandler messageChannelHandler;

    public void loadParticleEffects() throws IOException {
        getLogger().info("Loading Particles");
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    public void onDisable() {
        wolfyUtilities.getConfigAPI().saveConfigs();
        PlayerUtils.saveStores();
        WorldUtils.save();
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void onLoad() {
        instance = this;
        ServerVersion.setWUVersion(getDescription().getVersion());

        //Jackson Serializer
        SimpleModule module = new SimpleModule();
        ItemStackSerialization.create(module);
        ColorSerialization.create(module);
        DustOptionsSerialization.create(module);
        LocationSerialization.create(module);
        ParticleContentSerialization.create(module);
        PotionEffectTypeSerialization.create(module);
        PotionEffectSerialization.create(module);
        VectorSerialization.create(module);

        //Reference Deserializer
        APIReferenceSerialization.create(module);
        JacksonUtil.registerModule(module);

        //Register custom item data

    }

    public void onEnable() {
        getLogger().info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        getLogger().info("WolfyUtilities version: " + ServerVersion.getWUVersion().getVersion());
        wolfyUtilities = WolfyUtilities.get(this);
        this.chat = wolfyUtilities.getChat();
        chat.setCONSOLE_PREFIX("[WU] ");
        chat.setIN_GAME_PREFIX("§8[§3WU§8] §7");

        // Register plugin CustomItem API ReferenceParser
        getLogger().info("Register API references");
        CustomItem.registerAPIReferenceParser(new VanillaRef.Parser());
        CustomItem.registerAPIReferenceParser(new WolfyUtilitiesRef.Parser());
        CustomItem.registerAPIReferenceParser(new OraxenRef.Parser());
        CustomItem.registerAPIReferenceParser(new MythicMobsRef.Parser());
        CustomItem.registerAPIReferenceParser(new MMOItemsRef.Parser());

        LanguageAPI languageAPI = wolfyUtilities.getLanguageAPI();

        saveResource("lang/en_US.json", true);
        languageAPI.setActiveLanguage(new Language(this, "en_US"));

        WorldUtils.load();
        PlayerUtils.loadStores();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, WorldUtils::load, 300000, 300000);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand(wolfyUtilities));
        Bukkit.getServer().getPluginCommand("particle_animation").setExecutor(new SpawnParticleAnimationCommand(wolfyUtilities));
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand());
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand());

        Metrics metrics = new Metrics(this, 5114);
        CreativeModeTab.init();

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

        ParticleEffects.load();
        try {
            File file = new File(getDataFolder(), "test_animation.json");
            JacksonUtil.getObjectWriter(true).writeValue(file, Registry.PARTICLE_ANIMATIONS.get(new NamespacedKey("wolfyutilities", "flame_circle")));
            ParticleAnimation animation = JacksonUtil.getObjectMapper().readValue(file, ParticleAnimation.class);
            //System.out.println("Loaded: " + animation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("TestItem: "+ ItemUtils.serializeItemStack(new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_UNBREAKABLE).setDisplayName("LUL").addLoreLine("Test Item").create()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wolfyutils")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                chat.sendMessage(p, "~*~*~*~*&8[&3&lWolfyUtilities&8]&7~*~*~*~*~");
                chat.sendMessage(p, "");
                chat.sendMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
                chat.sendMessage(p, "        ------------------");
                chat.sendMessage(p, "");
                chat.sendMessage(p, "             &nVersion:&r&b " + getDescription().getVersion());
                chat.sendMessage(p, "");
                chat.sendMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
                return true;
            }
        }
        return true;
    }

    public MessageChannelHandler getPacketHandler() {
        return messageChannelHandler;
    }
}
