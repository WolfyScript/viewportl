/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.main;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilCore;
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
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference;
import me.wolfyscript.utilities.util.json.jackson.serialization.*;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.animators.AnimatorBasic;
import me.wolfyscript.utilities.util.particles.animators.AnimatorCircle;
import me.wolfyscript.utilities.util.particles.animators.AnimatorSphere;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import me.wolfyscript.utilities.util.particles.timer.TimerPi;
import me.wolfyscript.utilities.util.particles.timer.TimerRandom;
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

import java.util.Arrays;

public class WUPlugin extends WolfyUtilCore {

    @Deprecated
    private static WUPlugin instance;

    private final Chat chat;
    private final Console console;
    private Metrics metrics;
    private WUConfig config;

    private final MessageHandler messageHandler;
    private final MessageFactory messageFactory;

    public WUPlugin() {
        super();
        instance = this;
        this.chat = api.getChat();
        this.console = api.getConsole();
        chat.setInGamePrefix("§8[§3WU§8] §7");
        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
    }

    @Deprecated
    public static WUPlugin getInstance() {
        return instance;
    }

    public WolfyUtilities getWolfyUtilities() {
        return api;
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
        //ParticleContentSerialization.create(module);
        PotionEffectTypeSerialization.create(module);
        PotionEffectSerialization.create(module);
        VectorSerialization.create(module);

        //Reference Deserializer
        APIReferenceSerialization.create(module);
        JacksonUtil.registerModule(module);

        var beanModifiers = new SimpleModule();
        beanModifiers.setSerializerModifier(new OptionalKeyReference.SerializerModifier());
        beanModifiers.setDeserializerModifier(new OptionalKeyReference.DeserializerModifier());
        JacksonUtil.registerModule(beanModifiers);

        //Register custom item data

        //Register meta settings providers
        getLogger().info("Register CustomItem meta checks");
        ClassRegistry.NBT_CHECKS.register(AttributesModifiersMeta.KEY, AttributesModifiersMeta.class);
        ClassRegistry.NBT_CHECKS.register(CustomDamageMeta.KEY, CustomDamageMeta.class);
        ClassRegistry.NBT_CHECKS.register(CustomDurabilityMeta.KEY, CustomDurabilityMeta.class);
        ClassRegistry.NBT_CHECKS.register(CustomItemTagMeta.KEY, CustomItemTagMeta.class);
        ClassRegistry.NBT_CHECKS.register(CustomModelDataMeta.KEY, CustomModelDataMeta.class);
        ClassRegistry.NBT_CHECKS.register(DamageMeta.KEY, DamageMeta.class);
        ClassRegistry.NBT_CHECKS.register(EnchantMeta.KEY, EnchantMeta.class);
        ClassRegistry.NBT_CHECKS.register(FlagsMeta.KEY, FlagsMeta.class);
        ClassRegistry.NBT_CHECKS.register(LoreMeta.KEY, LoreMeta.class);
        ClassRegistry.NBT_CHECKS.register(NameMeta.KEY, NameMeta.class);
        ClassRegistry.NBT_CHECKS.register(PlayerHeadMeta.KEY, PlayerHeadMeta.class);
        ClassRegistry.NBT_CHECKS.register(PotionMeta.KEY, PotionMeta.class);
        ClassRegistry.NBT_CHECKS.register(RepairCostMeta.KEY, RepairCostMeta.class);
        ClassRegistry.NBT_CHECKS.register(UnbreakableMeta.KEY, UnbreakableMeta.class);

        ClassRegistry.PARTICLE_ANIMATORS.register(AnimatorBasic.KEY, AnimatorBasic.class);
        ClassRegistry.PARTICLE_ANIMATORS.register(AnimatorSphere.KEY, AnimatorSphere.class);
        ClassRegistry.PARTICLE_ANIMATORS.register(AnimatorCircle.KEY, AnimatorCircle.class);

        ClassRegistry.PARTICLE_TIMER.register(TimerLinear.KEY, TimerLinear.class);
        ClassRegistry.PARTICLE_TIMER.register(TimerRandom.KEY, TimerRandom.class);
        ClassRegistry.PARTICLE_TIMER.register(TimerPi.KEY, TimerPi.class);

        KeyedTypeIdResolver.registerTypeRegistry(Meta.class, ClassRegistry.NBT_CHECKS);
        KeyedTypeIdResolver.registerTypeRegistry(Animator.class, ClassRegistry.PARTICLE_ANIMATORS);
        KeyedTypeIdResolver.registerTypeRegistry(Timer.class, ClassRegistry.PARTICLE_TIMER);
    }

    @Override
    public void onEnable() {
        compatibilityManager.init();
        this.api.initialize();
        console.info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        console.info("WolfyUtilities version: " + ServerVersion.getWUVersion().getVersion());
        console.info("Environment: " + WolfyUtilities.getENVIRONMENT());
        this.config = new WUConfig(api.getConfigAPI(), this);

        this.metrics = new Metrics(this, 5114);

        // Register plugin CustomItem API ReferenceParser
        console.info("Register API references");
        registerAPIReference(new VanillaRef.Parser());
        registerAPIReference(new WolfyUtilitiesRef.Parser());
        registerAPIReference(new MythicMobsRef.Parser());
        registerAPIReference(new MagicRef.Parser());

        var languageAPI = api.getLanguageAPI();

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

    public void registerAPIReference(APIReference.Parser<?> parser) {
        if (parser instanceof VanillaRef.Parser || parser instanceof WolfyUtilitiesRef.Parser || config.isAPIReferenceEnabled(parser)) {
            CustomItem.registerAPIReferenceParser(parser);
        }
    }

    @Override
    public void onDisable() {
        api.getConfigAPI().saveConfigs();
        PlayerUtils.saveStores();
        console.info("Save stored Custom Items");
        WorldUtils.save();
    }

    public void loadParticleEffects() {
        console.info("Initiating Particles");
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new Chat.ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIInventoryListener(), this);
    }

    private void registerCommands() {
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand(api));
        Bukkit.getServer().getPluginCommand("particle_animation").setExecutor(new SpawnParticleAnimationCommand(api));
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand(this));
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand(this));
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

        var nbt = api.getNmsUtil().getNBTUtil();
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
