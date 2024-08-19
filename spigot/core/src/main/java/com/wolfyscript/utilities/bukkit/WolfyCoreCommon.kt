package com.wolfyscript.utilities.bukkit

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.commands.*
import com.wolfyscript.utilities.bukkit.config.WUConfig
import com.wolfyscript.utilities.bukkit.gui.example.TestGUI
import com.wolfyscript.utilities.bukkit.json.serialization.*
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.viewportl.commands.GuiExampleCommand
import com.wolfyscript.viewportl.commands.InputCommand
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.registry.guiComponents
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

    private var config: WUConfig? = null

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
        })

        // Jackson Serializer
        logger.info("Register JSON de-/serializers")
        val module = SimpleModule()
        // ParticleContentSerialization.create(module);

        // Register implementation types to use for de/serialization
        module.addAbstractTypeMapping(ButtonIcon::class.java, ButtonImpl.DynamicIcon::class.java)

        // Add module to WU Modules and register it to the old JacksonUtil.
        jsonMapperModules.add(module)

        // De-/Serializer Modifiers that handle type references in JSON
        val keyReferenceModule = SimpleModule()
        jsonMapperModules.add(keyReferenceModule)

        // Create Global WUCore Mapper and apply modules
        val mapper = applyWolfyUtilsJsonMapperModules(HoconMapper())
        wolfyUtils.jacksonMapperUtil.applyWolfyUtilsInjectableValues(mapper, InjectableValues.Std())
        wolfyUtils.jacksonMapperUtil.globalMapper = mapper

        // Register GUI things
        val guiComponentBuilders = ScafallProvider.get().registries.guiComponents
        guiComponentBuilders.register(ButtonImpl::class.java)
        guiComponentBuilders.register(StackInputSlotImpl::class.java)
        guiComponentBuilders.register(ComponentGroupImpl::class.java)
        guiComponentBuilders.register(OutletImpl::class.java)

    }

    open fun enable() {
        wolfyUtils.initialize()
        wolfyUtils.logger.info("Minecraft version: " + ServerVersion.getVersion().version)
        wolfyUtils.logger.info("WolfyUtils version: " + ServerVersion.getWUVersion().version)
        if (WolfyUtils.isDevEnv) {
            wolfyUtils.logger.info("> Dev-Environment <")
        }
        this.config = WUConfig(wolfyUtils.configAPI, plugin)

        //Load Language
        wolfyUtils.translations.loadLangFile("en_US")

        registerListeners()
        registerCommands()

        val testGUI = TestGUI(this)
        testGUI.initWithConfig()
    }

    open fun disable() {
        wolfyUtils.configAPI.saveConfigs()
        wolfyUtils.logger.info("Save stored Custom Items")
    }

    protected fun registerCommands() {
        registerDynamicCommands(
            InputCommand(this),
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
        Bukkit.getPluginManager().registerEvents(com.wolfyscript.viewportl.gui.interaction.GUIInventoryListener(), plugin)
    }

    /**
     * Handles JUnit test startup
     */
    private fun onJUnitTests() {
        registerCommands()
    }

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


