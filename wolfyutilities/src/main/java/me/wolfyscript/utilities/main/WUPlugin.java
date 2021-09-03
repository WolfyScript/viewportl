package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.console.Console;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.*;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagList;
import me.wolfyscript.utilities.main.commands.ChatActionCommand;
import me.wolfyscript.utilities.main.commands.InputCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleAnimationCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.configs.WUConfig;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.GUIInventoryListener;
import me.wolfyscript.utilities.main.listeners.PlayerListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomDurabilityListener;
import me.wolfyscript.utilities.main.listeners.custom_item.CustomParticleListener;
import me.wolfyscript.utilities.main.messages.MessageFactory;
import me.wolfyscript.utilities.main.messages.MessageHandler;
import me.wolfyscript.utilities.main.particles.ParticleEffects;
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.serialization.*;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.TimeSupplier;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class WUPlugin extends JavaPlugin {

    private static WUPlugin instance;

    private final WolfyUtilities wolfyUtilities;

    private final Chat chat;
    private final Console console;
    private Metrics metrics;
    private WUConfig config;

    private final MessageHandler messageHandler;
    private final MessageFactory messageFactory;

    public WUPlugin() {
        super();
        instance = this;
        this.wolfyUtilities = WolfyUtilities.get(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.chat = wolfyUtilities.getChat();
        this.console = wolfyUtilities.getConsole();
        chat.setInGamePrefix("§8[§3WU§8] §7");

        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
    }

    public static WUPlugin getInstance() {
        return instance;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    @Override
    public void onLoad() {
        //Jackson Serializer
        getLogger().info("Register json serializer/deserializer");
        var module = new SimpleModule();
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

        //Register meta settings providers
        getLogger().info("Register Meta Setting providers");
        Registry.MetaRegistry meta = Registry.META_PROVIDER;
        meta.register(NamespacedKey.wolfyutilties("attributes_modifiers"), AttributesModifiersMeta.class);
        meta.register(NamespacedKey.wolfyutilties("custom_damage"), CustomDamageMeta.class);
        meta.register(NamespacedKey.wolfyutilties("custom_durability"), CustomDurabilityMeta.class);
        meta.register(NamespacedKey.wolfyutilties("customitem_tag"), CustomItemTagMeta.class);
        meta.register(NamespacedKey.wolfyutilties("custom_model_data"), CustomModelDataMeta.class);
        meta.register(NamespacedKey.wolfyutilties("damage"), DamageMeta.class);
        meta.register(NamespacedKey.wolfyutilties("enchant"), EnchantMeta.class);
        meta.register(NamespacedKey.wolfyutilties("flags"), FlagsMeta.class);
        meta.register(NamespacedKey.wolfyutilties("lore"), LoreMeta.class);
        meta.register(NamespacedKey.wolfyutilties("name"), NameMeta.class);
        meta.register(NamespacedKey.wolfyutilties("player_head"), PlayerHeadMeta.class);
        meta.register(NamespacedKey.wolfyutilties("potion"), PotionMeta.class);
        meta.register(NamespacedKey.wolfyutilties("repair_cost"), RepairCostMeta.class);
        meta.register(NamespacedKey.wolfyutilties("unbreakable"), UnbreakableMeta.class);

        KeyedTypeIdResolver.registerTypeRegistry(Animator.class, ClassRegistry.PARTICLE_ANIMATORS);
        KeyedTypeIdResolver.registerTypeRegistry(TimeSupplier.class, ClassRegistry.PARTICLE_TIMER);
    }

    @Override
    public void onEnable() {
        this.wolfyUtilities.initialize();
        console.info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        console.info("WolfyUtilities version: " + ServerVersion.getWUVersion().getVersion());
        console.info("Environment: " + WolfyUtilities.getENVIRONMENT());
        this.config = new WUConfig(wolfyUtilities.getConfigAPI(), this);

        this.metrics = new Metrics(this, 5114);

        // Register plugin CustomItem API ReferenceParser
        console.info("Register API references");
        registerAPIReference(new VanillaRef.Parser());
        registerAPIReference(new WolfyUtilitiesRef.Parser());
        registerAPIReference(new OraxenRef.Parser());
        registerAPIReference(new ItemsAdderRef.Parser());
        registerAPIReference(new MythicMobsRef.Parser());
        registerAPIReference(new MMOItemsRef.Parser());

        var languageAPI = wolfyUtilities.getLanguageAPI();

        saveResource("lang/en_US.json", true);
        languageAPI.setActiveLanguage(new Language(this, "en_US"));

        WorldUtils.load();
        PlayerUtils.loadStores();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, WorldUtils::save, 6000, 6000);

        registerListeners();
        registerCommands();

        CreativeModeTab.init();

        loadParticleEffects();
        //Used to test the NBT Tag API!
        testNBTAPI(false);
    }

    private void registerAPIReference(APIReference.Parser<?> parser) {
        if (parser instanceof VanillaRef.Parser || parser instanceof WolfyUtilitiesRef.Parser || config.isAPIReferenceEnabled(parser)) {
            CustomItem.registerAPIReferenceParser(parser);
        }
    }

    @Override
    public void onDisable() {
        wolfyUtilities.getConfigAPI().saveConfigs();
        PlayerUtils.saveStores();
        console.info("Save stored Custom Items");
        WorldUtils.save();
    }

    public void loadParticleEffects() {
        console.info("Loading Particles");
        ParticleEffects.load();
        try {
            File file = new File(getDataFolder(), "test_animation.json");
            JacksonUtil.getObjectWriter(true).writeValue(file, Registry.PARTICLE_EFFECTS.get(NamespacedKey.wolfyutilties("flame_sphere")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new Chat.ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIInventoryListener(), this);
    }

    private void registerCommands() {
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand(wolfyUtilities));
        Bukkit.getServer().getPluginCommand("particle_animation").setExecutor(new SpawnParticleAnimationCommand(wolfyUtilities));
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand());
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand());
        Bukkit.getServer().getPluginCommand("wua").setExecutor(new ChatActionCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wolfyutils") && sender instanceof Player) {
            chat.sendMessages((Player) sender, "——————— &3&lWolfyUtilities &7———————",
                    "",
                    "&7Author: &l" + String.join(", ", getDescription().getAuthors()),
                    "",
                    "&7Version: &l" + ServerVersion.getWUVersion().getVersion(),
                    "",
                    "———————————————————————");
        }
        return true;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    private void testNBTAPI(boolean test) {
        if (!test) return;
        var itemBuilder = new ItemBuilder(Material.DIAMOND_SWORD);
        itemBuilder.addLoreLine("Test");
        itemBuilder.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        var itemStack = itemBuilder.create();

        var nbt = wolfyUtilities.getNmsUtil().getNBTUtil();
        var nbtItem = nbt.getItem(itemStack);

        nbtItem.setTag("test_string", nbt.getTag().ofString("Test String!"));

        var compound = nbtItem.getCompound();
        compound.setInt("Test_Int", 10);

        var wolfyCompound = nbt.getTag().compound();
        wolfyCompound.setByte("Byte", (byte) 4);
        wolfyCompound.setBoolean("Boolean", true);
        wolfyCompound.setDouble("Double", 2d);
        wolfyCompound.setFloat("Float", 7f);
        wolfyCompound.setInt("Int", 9);
        wolfyCompound.setLong("Long", 9999);
        wolfyCompound.setShort("Short", (short) 200);
        wolfyCompound.setString("String", "TestString");
        wolfyCompound.setByteArray("ByteArray", new byte[]{9, 9, 5, 2, 3});
        wolfyCompound.setIntArray("IntArray", new int[]{9, 3543, 2134, 123});
        wolfyCompound.setLongArray("LongArray", new long[]{54, 65, 23244343, 1000000000000000000L});

        /*
        var customList = nbt.getTag().list();
        customList.add(0, nbt.getTag().ofIntArray(new int[]{4, 543654, 235, 223, 423, 32}));
        customList.add(0, nbt.getTag().ofIntArray(new int[]{543, 345, 76, 21, 8, 65, 456, 4}));
        customList.add(0, nbt.getTag().ofIntArray(new int[]{897, 567, 98, 899, 878712, 12}));
        wolfyCompound.set("IntArrayList", customList);
        //*/
        var nestedComp = nbt.getTag().compound();
        nestedComp.setString("LUL", "xD this is a nested Text!");
        nestedComp.setBoolean("Funny", false);
        wolfyCompound.set("Nested", nestedComp);

        compound.set("wolfy", wolfyCompound);

        console.info("Item: ");
        console.info("Tag: " + nbtItem.getCompound().toString());
        console.info("Keys: ");
        console.info("    - " + String.join("\n    - ", nbtItem.getKeys()));

        ItemStack newItem = nbtItem.create();
        var newNBTItem = nbt.getItem(newItem);
        console.info("New Item: ");
        console.info("Tag: " + nbtItem.getCompound().toString());
        console.info("Item Keys: ");
        for (String key : newNBTItem.getKeys()) {
            console.info(" - " + key + " = " + newNBTItem.getTag(key));
        }

        var wolfyComp = newNBTItem.getCompound("wolfy");
        if (wolfyComp != null) {
            console.info("Wolfy Values: ");
            console.info("    Byte = " + wolfyComp.getByte("Byte"));
            console.info("    Boolean = " + wolfyComp.getBoolean("Boolean"));
            console.info("    Double = " + wolfyComp.getDouble("Double"));
            console.info("    Float = " + wolfyComp.getFloat("Float"));
            console.info("    Int = " + wolfyComp.getInt("Int"));
            console.info("    Long = " + wolfyComp.getLong("Long"));
            console.info("    Short = " + wolfyComp.getShort("Short"));
            console.info("    String = " + wolfyComp.getString("String"));
            console.info("    ByteArray = " + Arrays.toString(wolfyComp.getByteArray("ByteArray")));
            console.info("    IntArray = " + Arrays.toString(wolfyComp.getIntArray("IntArray")));
            console.info("    LongArray = " + Arrays.toString(wolfyComp.getLongArray("LongArray")));
            console.info("    Nested = " + wolfyComp.get("Nested"));
            var nbtTagList = (NBTTagList) wolfyComp.get("IntArrayList");
            console.info("    IntArrayList = " + nbtTagList);
            if (nbtTagList != null) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    console.info("       - " + nbtTagList.getTag(i));
                }
            }
        }
    }
}
