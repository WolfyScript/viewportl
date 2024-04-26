package com.wolfyscript.utilities.bukkit

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.chat.BukkitChat
import com.wolfyscript.utilities.bukkit.chat.BukkitChat.ChatListener
import com.wolfyscript.utilities.bukkit.commands.*
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit
import com.wolfyscript.utilities.bukkit.config.WUConfig
import com.wolfyscript.utilities.bukkit.gui.example.TestGUI
import com.wolfyscript.utilities.bukkit.gui.interaction.GUIInventoryListener
import com.wolfyscript.utilities.bukkit.json.serialization.*
import com.wolfyscript.utilities.bukkit.listeners.EquipListener
import com.wolfyscript.utilities.bukkit.listeners.PersistentStorageListener
import com.wolfyscript.utilities.bukkit.listeners.PlayerListener
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomDurabilityListener
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomItemDataListener
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomItemPlayerListener
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomParticleListener
import com.wolfyscript.utilities.bukkit.nbt.*
import com.wolfyscript.utilities.bukkit.network.messages.MessageFactory
import com.wolfyscript.utilities.bukkit.network.messages.MessageHandler
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage
import com.wolfyscript.utilities.bukkit.persistent.player.CustomPlayerData
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.utilities.bukkit.world.items.CustomItemBlockData
import com.wolfyscript.utilities.bukkit.world.items.CustomItemData
import com.wolfyscript.utilities.bukkit.world.items.actions.*
import com.wolfyscript.utilities.bukkit.world.items.meta.*
import com.wolfyscript.utilities.bukkit.world.particles.animators.*
import com.wolfyscript.utilities.bukkit.world.particles.shapes.*
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerLinear
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerPi
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerRandom
import com.wolfyscript.utilities.config.jackson.*
import com.wolfyscript.utilities.eval.operator.*
import com.wolfyscript.utilities.eval.value_provider.*
import com.wolfyscript.utilities.gui.components.*
import com.wolfyscript.utilities.nbt.*
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.ItemStackConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import java.util.logging.Logger

/**
 * The core implementation of WolfyUtils.<br></br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br></br>
 *
 *
 * If you want to use the plugin specific API, see [com.wolfyscript.utilities.WolfyUtils] & [WolfyUtilsBukkit]
 */
abstract class WolfyCoreCommon(@JvmField val plugin: WolfyCoreCommonBootstrap) : WolfyCore() {

    private val pluginWolfyUtilsInstances: MutableMap<String, WolfyUtilsBukkit> = HashMap()
    val wolfyUtilsInstanceList: List<WolfyUtilsBukkit>
        /**
         * Returns an unmodifiable List of all available [WolfyUtilsBukkit] instances.
         *
         * @return A list containing all the created API instances.
         */
        get() = java.util.List.copyOf(pluginWolfyUtilsInstances.values)

    private val jsonMapperModules: MutableList<SimpleModule> = ArrayList()

    /**
     * Gets the [Reflections] instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    final override val wolfyUtils: WolfyUtilsBukkit = getOrCreate(plugin)

    init {
        wolfyUtils.chat.chatPrefix = Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY))
    }

    private var config: WUConfig? = null

    val messageHandler: MessageHandler = MessageHandler(this)

    @JvmField
    val messageFactory: MessageFactory = MessageFactory(this)
    /**
     * The [CompatibilityManagerBukkit], that manages the plugins compatibility features.
     *
     * @return The [CompatibilityManagerBukkit].
     */
    val compatibilityManager: CompatibilityManager = CompatibilityManagerBukkit(this)

    @JvmField
    val persistentStorage: PersistentStorage = PersistentStorage(this)

    /**
     * Gets the [BukkitRegistries] object, that contains all info about available registries.
     *
     * @return The [BukkitRegistries] object, to access registries.
     */
    override val registries: BukkitRegistries = BukkitRegistries(this)

    val logger: Logger = plugin.logger

    override val reflections: Reflections
        get() = plugin.reflections

    /**
     * Gets or create the [WolfyUtilsBukkit] instance for the specified plugin.<br></br>
     * In case init is enabled it will directly initialize the event listeners and possibly other things.<br></br>
     * **This will call [WolfyUtilsBukkit.initialize] directly, so only use it inside your onEnable()!**
     *
     * @param plugin The plugin to get the instance for.
     * @return The WolfyUtilities instance for the plugin.
     */
    fun getOrCreate(plugin: Plugin): WolfyUtilsBukkit {
        return pluginWolfyUtilsInstances.computeIfAbsent(plugin.name) { WolfyUtilsBukkit(this, plugin) }
    }

    /**
     * Checks if the specified plugin has an API instance associated with it.
     *
     * @param plugin The plugin to check.
     * @return True in case the API is available; false otherwise.
     */
    fun has(plugin: Plugin): Boolean {
        return pluginWolfyUtilsInstances.containsKey(plugin.name)
    }

    open fun load() {
        val injector = Guice.createInjector(Stage.DEVELOPMENT, Module { binder: Binder ->
            binder.bind(WolfyCore::class.java).toInstance(this)
            binder.requestStaticInjection(KeyedTypeIdResolver::class.java)
        })

        // Jackson Serializer
        logger.info("Register JSON de-/serializers")
        val module = SimpleModule()
        ItemStackSerialization.create(module)
        ColorSerialization.create(module)
        DustOptionsSerialization.create(module)
        LocationSerialization.create(module)
        // ParticleContentSerialization.create(module);
        PotionEffectTypeSerialization.create(module)
        PotionEffectSerialization.create(module)
        VectorSerialization.create(module)

        // Register implementation types to use for de/serialization
        module.addAbstractTypeMapping(ItemStackConfig::class.java, BukkitItemStackConfig::class.java)

        module.addAbstractTypeMapping(ButtonIcon::class.java, ButtonImpl.DynamicIcon::class.java)

        // Add module to WU Modules and register it to the old JacksonUtil.
        jsonMapperModules.add(module)
        JacksonUtil.registerModule(module)

        // De-/Serializer Modifiers that handle type references in JSON
        val keyReferenceModule = SimpleModule()
        keyReferenceModule.setSerializerModifier(OptionalKeyReference.SerializerModifier())
        keyReferenceModule.setDeserializerModifier(OptionalKeyReference.DeserializerModifier(this))
        jsonMapperModules.add(keyReferenceModule)
        JacksonUtil.registerModule(keyReferenceModule)

        val valueReferenceModule = SimpleModule()
        valueReferenceModule.setSerializerModifier(OptionalValueSerializer.SerializerModifier())
        valueReferenceModule.setDeserializerModifier(OptionalValueDeserializer.DeserializerModifier())
        jsonMapperModules.add(valueReferenceModule)
        JacksonUtil.registerModule(valueReferenceModule)

        // Create Global WUCore Mapper and apply modules
        val mapper = applyWolfyUtilsJsonMapperModules(HoconMapper())
        wolfyUtils.jacksonMapperUtil.applyWolfyUtilsInjectableValues(mapper, InjectableValues.Std())
        wolfyUtils.jacksonMapperUtil.globalMapper = mapper

        // Initialise all the Registers
        logger.info("Register JSON Operators")
        val operators = registries.operators
        operators.register(BoolOperatorConst::class.java)
        // Compare operators
        operators.register(ComparisonOperatorEqual::class.java)
        operators.register(ComparisonOperatorNotEqual::class.java)
        operators.register(ComparisonOperatorGreater::class.java)
        operators.register(ComparisonOperatorGreaterEqual::class.java)
        operators.register(ComparisonOperatorLess::class.java)
        operators.register(ComparisonOperatorLessEqual::class.java)
        // Logical
        operators.register(LogicalOperatorAnd::class.java)
        operators.register(LogicalOperatorOr::class.java)
        operators.register(LogicalOperatorNot::class.java)

        logger.info("Register JSON Value Providers")
        val valueProviders = registries.valueProviders
        // Custom
        valueProviders.register(ValueProviderConditioned::class.java as Class<ValueProviderConditioned<*>?>)
        // Primitive
        valueProviders.register(ValueProviderShortConst::class.java)
        valueProviders.register(ValueProviderShortVar::class.java)
        valueProviders.register(ValueProviderIntegerConst::class.java)
        valueProviders.register(ValueProviderIntegerVar::class.java)
        valueProviders.register(ValueProviderLongConst::class.java)
        valueProviders.register(ValueProviderLongVar::class.java)
        valueProviders.register(ValueProviderFloatConst::class.java)
        valueProviders.register(ValueProviderFloatVar::class.java)
        valueProviders.register(ValueProviderDoubleConst::class.java)
        valueProviders.register(ValueProviderDoubleVar::class.java)
        valueProviders.register(ValueProviderStringConst::class.java)
        valueProviders.register(ValueProviderStringVar::class.java)
        // Arrays
        valueProviders.register(ValueProviderByteArrayConst::class.java)
        valueProviders.register(ValueProviderIntArrayConst::class.java)

        logger.info("Register CustomItem NBT Checks")
        val nbtChecks = registries.customItemNbtChecks
        nbtChecks.register(AttributesModifiersMeta.KEY, AttributesModifiersMeta::class.java)
        nbtChecks.register(CustomDamageMeta.KEY, CustomDamageMeta::class.java)
        nbtChecks.register(CustomDurabilityMeta.KEY, CustomDurabilityMeta::class.java)
        nbtChecks.register(CustomItemTagMeta.KEY, CustomItemTagMeta::class.java)
        nbtChecks.register(CustomModelDataMeta.KEY, CustomModelDataMeta::class.java)
        nbtChecks.register(DamageMeta.KEY, DamageMeta::class.java)
        nbtChecks.register(EnchantMeta.KEY, EnchantMeta::class.java)
        nbtChecks.register(FlagsMeta.KEY, FlagsMeta::class.java)
        nbtChecks.register(LoreMeta.KEY, LoreMeta::class.java)
        nbtChecks.register(NameMeta.KEY, NameMeta::class.java)
        nbtChecks.register(PlayerHeadMeta.KEY, PlayerHeadMeta::class.java)
        nbtChecks.register(PotionMeta.KEY, PotionMeta::class.java)
        nbtChecks.register(RepairCostMeta.KEY, RepairCostMeta::class.java)
        nbtChecks.register(UnbreakableMeta.KEY, UnbreakableMeta::class.java)

        logger.info("Register CustomItem Actions")
        val customItemActions = registries.customItemActions
        customItemActions.register(ActionCommand.KEY, ActionCommand::class.java)
        customItemActions.register(ActionParticleAnimation.KEY, ActionParticleAnimation::class.java)
        customItemActions.register(ActionSound.KEY, ActionSound::class.java)

        logger.info("Register CustomItem Events")
        val customItemEvents = registries.customItemEvents
        customItemEvents.register(EventPlayerInteract.KEY, EventPlayerInteract::class.java)
        customItemEvents.register(EventPlayerConsumeItem.KEY, EventPlayerConsumeItem::class.java)
        customItemEvents.register(EventPlayerInteractEntity.KEY, EventPlayerInteractEntity::class.java)
        customItemEvents.register(EventPlayerInteractAtEntity.KEY, EventPlayerInteractAtEntity::class.java)
        customItemEvents.register(EventPlayerItemBreak.KEY, EventPlayerItemBreak::class.java)
        customItemEvents.register(EventPlayerItemDamage.KEY, EventPlayerItemDamage::class.java)
        customItemEvents.register(EventPlayerItemDrop.KEY, EventPlayerItemDrop::class.java)
        customItemEvents.register(EventPlayerItemHandSwap.KEY, EventPlayerItemHandSwap::class.java)
        customItemEvents.register(EventPlayerItemHeld.KEY, EventPlayerItemHeld::class.java)

        logger.info("Register Particle Animators")
        val particleAnimators = registries.particleAnimators
        particleAnimators.register(AnimatorBasic.KEY, AnimatorBasic::class.java)
        particleAnimators.register(AnimatorSphere.KEY, AnimatorSphere::class.java)
        particleAnimators.register(AnimatorCircle.KEY, AnimatorCircle::class.java)
        particleAnimators.register(AnimatorVectorPath.KEY, AnimatorVectorPath::class.java)
        particleAnimators.register(AnimatorShape.KEY, AnimatorShape::class.java)

        logger.info("Register Particle Shapes")
        val particleShapes = registries.particleShapes
        particleShapes.register(ShapeSquare.KEY, ShapeSquare::class.java)
        particleShapes.register(ShapeCircle.KEY, ShapeCircle::class.java)
        particleShapes.register(ShapeSphere.KEY, ShapeSphere::class.java)
        particleShapes.register(ShapeCube.KEY, ShapeCube::class.java)
        particleShapes.register(ShapeIcosahedron.KEY, ShapeIcosahedron::class.java)
        particleShapes.register(ShapeComplexRotation.KEY, ShapeComplexRotation::class.java)
        particleShapes.register(ShapeComplexCompound.KEY, ShapeComplexCompound::class.java)

        logger.info("Register Particle Timers")
        val particleTimers = registries.particleTimer
        particleTimers.register(TimerLinear.KEY, TimerLinear::class.java)
        particleTimers.register(TimerRandom.KEY, TimerRandom::class.java)
        particleTimers.register(TimerPi.KEY, TimerPi::class.java)

        logger.info("Register Custom Block Data")
        val customBlockData = registries.customBlockData
        customBlockData.register(CustomItemBlockData.ID, CustomItemBlockData::class.java)

        logger.info("Register Custom Player Data")
        val customPlayerDataReg = registries.customPlayerData
        customPlayerDataReg.register(PlayerParticleEffectData::class.java)

        logger.info("Register NBT Tag Configs")
        val nbtTagConfigs = registries.nbtTagConfigs
        // Primitives
        nbtTagConfigs.register(NBTTagConfigBoolean::class.java)
        nbtTagConfigs.register(NBTTagConfigString::class.java)
        // Primitive Numerals
        nbtTagConfigs.register(NBTTagConfigByte::class.java)
        nbtTagConfigs.register(NBTTagConfigByteArray::class.java)
        nbtTagConfigs.register(NBTTagConfigShort::class.java)
        nbtTagConfigs.register(NBTTagConfigInt::class.java)
        nbtTagConfigs.register(NBTTagConfigIntArray::class.java)
        nbtTagConfigs.register(NBTTagConfigLong::class.java)
        nbtTagConfigs.register(NBTTagConfigFloat::class.java)
        nbtTagConfigs.register(NBTTagConfigDouble::class.java)
        // Lists
        nbtTagConfigs.register(NBTTagConfigListCompound::class.java)
        nbtTagConfigs.register(NBTTagConfigListInt::class.java)
        nbtTagConfigs.register(NBTTagConfigListIntArray::class.java)
        nbtTagConfigs.register(NBTTagConfigListLong::class.java)
        nbtTagConfigs.register(NBTTagConfigListFloat::class.java)
        nbtTagConfigs.register(NBTTagConfigListDouble::class.java)
        nbtTagConfigs.register(NBTTagConfigListString::class.java)

        logger.info("Register NBT Query Nodes")
        val nbtQueryNodes = registries.nbtQueryNodes
        nbtQueryNodes.register(QueryNodeCompound::class.java)
        nbtQueryNodes.register(QueryNodeBoolean::class.java)
        //Primitives
        nbtQueryNodes.register(QueryNodeByte::class.java)
        nbtQueryNodes.register(QueryNodeShort::class.java)
        nbtQueryNodes.register(QueryNodeInt::class.java)
        nbtQueryNodes.register(QueryNodeLong::class.java)
        nbtQueryNodes.register(QueryNodeDouble::class.java)
        nbtQueryNodes.register(QueryNodeFloat::class.java)
        nbtQueryNodes.register(QueryNodeString::class.java)
        //Arrays
        nbtQueryNodes.register(QueryNodeByteArray::class.java)
        nbtQueryNodes.register(QueryNodeIntArray::class.java)
        //Lists
        nbtQueryNodes.register(QueryNodeListInt::class.java)
        nbtQueryNodes.register(QueryNodeListLong::class.java)
        nbtQueryNodes.register(QueryNodeListDouble::class.java)
        nbtQueryNodes.register(QueryNodeListFloat::class.java)
        nbtQueryNodes.register(QueryNodeListString::class.java)
        nbtQueryNodes.register(QueryNodeListCompound::class.java)

        // Register GUI things
        val guiComponentBuilders = registries.guiComponentTypes
        guiComponentBuilders.register(ButtonImpl::class.java)
        guiComponentBuilders.register(StackInputSlotImpl::class.java)
        guiComponentBuilders.register(ComponentGroupImpl::class.java)
        guiComponentBuilders.register(OutletImpl::class.java)

        // Register the Registries to resolve type references in JSON
        KeyedTypeIdResolver.registerTypeRegistry(CustomItemData::class.java, registries.customItemDataTypeRegistry)
        KeyedTypeIdResolver.registerTypeRegistry(Meta::class.java, nbtChecks)
        KeyedTypeIdResolver.registerTypeRegistry(Animator::class.java, particleAnimators)
        KeyedTypeIdResolver.registerTypeRegistry(Shape::class.java, particleShapes)
        KeyedTypeIdResolver.registerTypeRegistry(Timer::class.java, particleTimers)
        KeyedTypeIdResolver.registerTypeRegistry(Action::class.java, customItemActions)
        KeyedTypeIdResolver.registerTypeRegistry(Event::class.java, customItemEvents)
        KeyedTypeIdResolver.registerTypeRegistry(Operator::class.java, operators)
        KeyedTypeIdResolver.registerTypeRegistry(ValueProvider::class.java, valueProviders)
        KeyedTypeIdResolver.registerTypeRegistry(QueryNode::class.java, nbtQueryNodes)
        KeyedTypeIdResolver.registerTypeRegistry(CustomBlockData::class.java, customBlockData)
        KeyedTypeIdResolver.registerTypeRegistry(CustomPlayerData::class.java, registries.customPlayerData)
        KeyedTypeIdResolver.registerTypeRegistry(com.wolfyscript.utilities.gui.components.Component::class.java, registries.guiComponentTypes)
    }

    open fun enable() {
        wolfyUtils.initialize()
        wolfyUtils.logger.info("Minecraft version: " + ServerVersion.getVersion().version)
        wolfyUtils.logger.info("WolfyUtils version: " + ServerVersion.getWUVersion().version)
        if (WolfyUtils.isDevEnv) {
            wolfyUtils.logger.info("> Dev-Environment <")
        }
        this.config = WUConfig(wolfyUtils.configAPI, plugin)
        compatibilityManager.init()

        //Load Language
        wolfyUtils.translations.loadLangFile("en_US")

        registerListeners()
        registerCommands()

        val testGUI = TestGUI(this)
        testGUI.initWithConfig()

        CreativeModeTab.init()
    }

    open fun disable() {
        wolfyUtils.configAPI.saveConfigs()
        wolfyUtils.logger.info("Save stored Custom Items")
    }

    protected fun registerCommands() {
        registerDynamicCommands(
            ChatActionCommand(this),
            InputCommand(this),
            InfoCommand(this),
            SpawnParticleAnimationCommand(this),
            SpawnParticleEffectCommand(this),
            QueryDebugCommand(this),
            GuiExampleCommand(this)
        )
    }

    protected fun registerDynamicCommands(vararg cmds: Command?) {
        var commandMap: CommandMap? = null
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            commandMap = commandMapField[Bukkit.getServer()] as CommandMap
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        if (commandMap == null) {
            logger.severe("Failed to register Commands: Failed to access CommandMap!")
            return
        }
        for (cmd in cmds) {
            commandMap.register("wolfyutils", cmd!!)
        }
    }

    private fun registerListeners() {
        Bukkit.getPluginManager().registerEvents(ChatListener(), plugin)
        Bukkit.getPluginManager().registerEvents(CustomDurabilityListener(this), plugin)
        Bukkit.getPluginManager().registerEvents(CustomParticleListener(this), plugin)
        Bukkit.getPluginManager().registerEvents(CustomItemPlayerListener(this), plugin)
        Bukkit.getPluginManager().registerEvents(
            EquipListener(
                this
            ), plugin
        )
        Bukkit.getPluginManager().registerEvents(PlayerListener(), plugin)
        Bukkit.getPluginManager().registerEvents(GUIInventoryListener(), plugin)
        Bukkit.getPluginManager().registerEvents(PersistentStorageListener(this), plugin)
        Bukkit.getPluginManager().registerEvents(CustomItemDataListener(this), plugin)
    }

    /**
     * Handles JUnit test startup
     */
    private fun onJUnitTests() {
        registerCommands()
    }

    override val chat: BukkitChat
        get() = wolfyUtils.chat

    override fun <M : ObjectMapper?> applyWolfyUtilsJsonMapperModules(mapper: M): M {
        mapper!!.registerModules(jsonMapperModules)
        return mapper
    }

    companion object {
        private val classes: MutableMap<String, Boolean> = HashMap()

        protected var INSTANCE : WolfyCoreCommon? = null

        @JvmStatic
        @get:Deprecated("")
        internal val instance: WolfyCoreCommon
            /**
             * Gets an instance of the core plugin.
             * **Only use this if necessary! First try to get the instance via your [WolfyUtilsBukkit] instance!**
             *
             * @return The instance of the core.
             */
            get() {
                if (INSTANCE == null) throw IllegalStateException("Trying to access WolfyCore before it was initialised!")
                return INSTANCE!!
            }

        /**
         * Check if the specific class exists.
         *
         * @param path The path to the class to check for.
         * @return If the class exists.
         */
        @JvmStatic
        fun hasClass(path: String): Boolean {
            if (classes.containsKey(path)) {
                return classes[path]!!
            }
            try {
                Class.forName(path)
                classes[path] = true
                return true
            } catch (e: Exception) {
                classes[path] = false
                return false
            }
        }
    }
}


