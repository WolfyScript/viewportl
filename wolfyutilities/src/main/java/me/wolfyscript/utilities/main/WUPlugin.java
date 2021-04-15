package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.*;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.MessageChannelHandler;
import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagList;
import me.wolfyscript.utilities.main.commands.ChatActionCommand;
import me.wolfyscript.utilities.main.commands.InputCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleAnimationCommand;
import me.wolfyscript.utilities.main.commands.SpawnParticleEffectCommand;
import me.wolfyscript.utilities.main.listeners.BlockListener;
import me.wolfyscript.utilities.main.listeners.EquipListener;
import me.wolfyscript.utilities.main.listeners.GUIInventoryListener;
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
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.serialization.*;
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
    private Metrics metrics;
    private MessageChannelHandler messageChannelHandler;

    public WUPlugin() {
        super();
        instance = this;
        this.wolfyUtilities = WolfyUtilities.get(this, false);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.chat = wolfyUtilities.getChat();
        chat.setCONSOLE_PREFIX("[WU] ");
        chat.setIN_GAME_PREFIX("§8[§3WU§8] §7");
    }

    public static WUPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        //Jackson Serializer
        getLogger().info("Register json serializer/deserializer");
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
    }

    @Override
    public void onEnable() {
        this.wolfyUtilities.initialize();
        getLogger().info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        getLogger().info("WolfyUtilities version: " + ServerVersion.getWUVersion().getVersion());
        this.metrics = new Metrics(this, 5114);

        // Register plugin CustomItem API ReferenceParser
        getLogger().info("Register API references");
        CustomItem.registerAPIReferenceParser(new VanillaRef.Parser());
        CustomItem.registerAPIReferenceParser(new WolfyUtilitiesRef.Parser());
        CustomItem.registerAPIReferenceParser(new OraxenRef.Parser());
        CustomItem.registerAPIReferenceParser(new ItemsAdderRef.Parser());
        CustomItem.registerAPIReferenceParser(new MythicMobsRef.Parser());
        CustomItem.registerAPIReferenceParser(new MMOItemsRef.Parser());

        LanguageAPI languageAPI = wolfyUtilities.getLanguageAPI();

        saveResource("lang/en_US.json", true);
        languageAPI.setActiveLanguage(new Language(this, "en_US"));

        WorldUtils.load();
        PlayerUtils.loadStores();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, WorldUtils::load, 300000, 300000);

        registerListeners();
        registerCommands();
        registerPluginMessages();

        CreativeModeTab.init();

        loadParticleEffects();

        ParticleEffects.load();
        try {
            File file = new File(getDataFolder(), "test_animation.json");
            JacksonUtil.getObjectWriter(true).writeValue(file, Registry.PARTICLE_ANIMATIONS.get(new NamespacedKey("wolfyutilities", "flame_circle")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Used to test the NBT Tag API!
        testNBTAPI(false);
    }

    @Override
    public void onDisable() {
        wolfyUtilities.getConfigAPI().saveConfigs();
        PlayerUtils.saveStores();
        WorldUtils.save();
    }

    public void loadParticleEffects() {
        getLogger().info("Loading Particles");
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

    private void registerPluginMessages() {
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

    public MessageChannelHandler getPacketHandler() {
        return messageChannelHandler;
    }

    private void testNBTAPI(boolean test) {
        if (!test) return;
        ItemBuilder itemBuilder = new ItemBuilder(Material.DIAMOND_SWORD);
        itemBuilder.addLoreLine("Test");
        itemBuilder.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        ItemStack itemStack = itemBuilder.create();

        NBTUtil nbt = wolfyUtilities.getNmsUtil().getNBTUtil();
        NBTItem nbtItem = nbt.getItem(itemStack);

        nbtItem.setTag("test_string", nbt.getTag().ofString("Test String!"));

        NBTCompound compound = nbtItem.getCompound();
        compound.setInt("Test_Int", 10);

        NBTCompound wolfyCompound = nbt.getTag().compound();
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

        NBTTagList customList = nbt.getTag().list();
        customList.add(0, nbt.getTag().ofIntArray(new int[]{4, 543654, 235, 223, 423, 32}));
        customList.add(0, nbt.getTag().ofIntArray(new int[]{543, 345, 76, 21, 8, 65, 456, 4}));
        customList.add(0, nbt.getTag().ofIntArray(new int[]{897, 567, 98, 899, 878712, 12}));
        wolfyCompound.set("IntArrayList", customList);

        NBTCompound nestedComp = nbt.getTag().compound();
        nestedComp.setString("LUL", "xD this is a nested Text!");
        nestedComp.setBoolean("Funny", false);
        wolfyCompound.set("Nested", nestedComp);

        compound.set("wolfy", wolfyCompound);

        getLogger().info("Item: ");
        getLogger().info("Tag: " + nbtItem.getCompound().toString());
        getLogger().info("Keys: ");
        getLogger().info("    - " + String.join("\n    - ", nbtItem.getKeys()));

        ItemStack newItem = nbtItem.create();
        NBTItem newNBTItem = nbt.getItem(newItem);
        getLogger().info("New Item: ");
        getLogger().info("Tag: " + nbtItem.getCompound().toString());
        getLogger().info("Item Keys: ");
        for (String key : newNBTItem.getKeys()) {
            getLogger().info(" - " + key + " = " + newNBTItem.getTag(key));
        }

        NBTCompound wolfyComp = newNBTItem.getCompound("wolfy");
        if (wolfyComp != null) {
            getLogger().info("Wolfy Values: ");
            getLogger().info("    Byte = " + wolfyComp.getByte("Byte"));
            getLogger().info("    Boolean = " + wolfyComp.getBoolean("Boolean"));
            getLogger().info("    Double = " + wolfyComp.getDouble("Double"));
            getLogger().info("    Float = " + wolfyComp.getFloat("Float"));
            getLogger().info("    Int = " + wolfyComp.getInt("Int"));
            getLogger().info("    Long = " + wolfyComp.getLong("Long"));
            getLogger().info("    Short = " + wolfyComp.getShort("Short"));
            getLogger().info("    String = " + wolfyComp.getString("String"));
            getLogger().info("    ByteArray = " + Arrays.toString(wolfyComp.getByteArray("ByteArray")));
            getLogger().info("    IntArray = " + Arrays.toString(wolfyComp.getIntArray("IntArray")));
            getLogger().info("    LongArray = " + Arrays.toString(wolfyComp.getLongArray("LongArray")));
            getLogger().info("    Nested = " + wolfyComp.get("Nested"));
            NBTTagList nbtTagList = (NBTTagList) wolfyComp.get("IntArrayList");
            getLogger().info("    IntArrayList = " + nbtTagList);
            for (int i = 0; i < nbtTagList.size(); i++) {
                getLogger().info("       - " + nbtTagList.getTag(i));
            }
        }
    }
}
