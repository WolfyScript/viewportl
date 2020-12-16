package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.*;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.MessageChannelHandler;
import me.wolfyscript.utilities.api.particles.ParticleEffects;
import me.wolfyscript.utilities.api.particles.Particles;
import me.wolfyscript.utilities.main.commands.InputCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomDurabilityListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomParticleListener;
import me.wolfyscript.utilities.main.messages.InputButtonMessage;
import me.wolfyscript.utilities.main.messages.WolfyUtilitiesVerifyMessage;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemCategory;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.serialization.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class WUPlugin extends JavaPlugin {

    private static WUPlugin instance;
    public static WUPlugin getInstance() {
        return instance;
    }

    private WolfyUtilities wolfyUtilities;
    private Chat chat;

    private Particles particlesConfig;

    private ParticleEffects particleEffectsConfig;

    private MessageChannelHandler messageChannelHandler;

    public void loadParticleEffects() throws IOException {
        chat.sendConsoleMessage("Loading Particles...");
        particlesConfig = new Particles(wolfyUtilities);
        particlesConfig.load(false);
        particleEffectsConfig = new ParticleEffects(wolfyUtilities);
        particleEffectsConfig.load(false);
        CustomItems.initiateMissingBlockEffects();
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

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public void onLoad() {
        instance = this;

        //Jackson Serializer
        SimpleModule module = new SimpleModule();
        ItemStackSerialization.create(module);
        ColorSerialization.create(module);
        DustOptionsSerialization.create(module, wolfyUtilities);
        LocationSerialization.create(module, wolfyUtilities);
        ParticleContentSerialization.create(module);
        ParticleEffectSerialization.create(module);
        ParticleSerialization.create(module);
        PotionEffectTypeSerialization.create(module);
        PotionEffectSerialization.create(module);
        VectorSerialization.create(module, wolfyUtilities);

        //Reference Deserializer
        APIReferenceSerialization.create(module);
        JacksonUtil.addDeserializer(module, ItemsAdderRef.class, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            return new ItemsAdderRef(node.asText());
        });
        JacksonUtil.addDeserializer(module, MMOItemsRef.class, (p, d) -> null);
        JacksonUtil.addDeserializer(module, MythicMobsRef.class, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            return new MythicMobsRef(node.asText());
        });
        JacksonUtil.addDeserializer(module, OraxenRef.class, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            return new OraxenRef(node.asText());
        });
        JacksonUtil.addDeserializer(module, VanillaRef.class, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            ObjectMapper objectMapper = JacksonUtil.getObjectMapper();
            return new VanillaRef(objectMapper.convertValue(node, ItemStack.class));
        });
        JacksonUtil.addDeserializer(module, WolfyUtilitiesRef.class, (p, d) -> {
            JsonNode node = p.readValueAsTree();
            return new WolfyUtilitiesRef(NamespacedKey.getByString(node.asText()));
        });
        JacksonUtil.registerModule(module);

        //Register custom item data
    }

    public void onEnable() {
        wolfyUtilities = WolfyUtilities.get(this);
        this.chat = wolfyUtilities.getChat();
        chat.setCONSOLE_PREFIX("[WU] ");
        chat.setIN_GAME_PREFIX("§8[§3WU§8] §7");

        ConfigAPI configAPI = wolfyUtilities.getConfigAPI();
        LanguageAPI languageAPI = wolfyUtilities.getLanguageAPI();

        /*
        mainConfig = new MainConfiguration(configAPI);
        configAPI.registerConfig(mainConfig);
         */

        saveResource("lang/en_US.json", true);
        languageAPI.setActiveLanguage(new Language(this, "en_US"));

        WolfyUtilities.getCustomItems().load();
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new WolfyUtilities(this), this);
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand(wolfyUtilities));
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand());
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand());

        Metrics metrics = new Metrics(this, 5114);

        try {
            ItemCategory.init();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        saveResource("particles/scripts/flame_spiral_down.js", true);
        saveResource("particles/particles.json", true);
        saveResource("particles/particle_effects.json", true);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wolfyutils")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                chat.sendPlayerMessage(p, "~*~*~*~*&8[&3&lWolfyUtilities&8]&7~*~*~*~*~");
                chat.sendPlayerMessage(p, "");
                chat.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
                chat.sendPlayerMessage(p, "        ------------------");
                chat.sendPlayerMessage(p, "");
                chat.sendPlayerMessage(p, "             &nVersion:&r&b " + getDescription().getVersion());
                chat.sendPlayerMessage(p, "");
                chat.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
                return true;
            }
        }
        return true;
    }

    public ParticleEffects getParticleEffects() {
        return particleEffectsConfig;
    }

    public Particles getParticles() {
        return particlesConfig;
    }

    public MessageChannelHandler getPacketHandler() {
        return messageChannelHandler;
    }
}
