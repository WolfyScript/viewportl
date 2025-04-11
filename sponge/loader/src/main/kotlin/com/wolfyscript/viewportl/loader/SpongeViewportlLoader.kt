package com.wolfyscript.viewportl.loader

import com.google.inject.Inject
import com.google.inject.Injector
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.viewportl.Viewportl
import org.spongepowered.api.Server
import org.spongepowered.api.command.Command
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.api.event.lifecycle.StartingEngineEvent
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("viewportl")
class SpongeViewportlLoader @Inject constructor(private val injector: Injector, private val pluginContainer: PluginContainer) {

    lateinit var module: Module<Viewportl>

    @Listener
    fun onConstructPlugin(event: ConstructPluginEvent) {
        val bootstrap = ScafallLoader.loadImplementationModule(Viewportl::class.java, "viewportl-sponge.innerjar", "com.wolfyscript.viewportl.InternalModuleBootstrap")
        module = bootstrap.loadImplementationModule(Viewportl::class.java, "com.wolfyscript.viewportl.sponge.Module", PluginContainer::class.java, pluginContainer)
    }

    @Listener
    fun onServerStarting(event: StartingEngineEvent<Server>) {
        module.onLoad()
    }

    @Listener
    fun onServerStopping(event: StoppingEngineEvent<Server>) {
        module.onUnload()
    }

    @Listener
    fun onRegisterCommands(event: RegisterCommandEvent<Command.Parameterized>) {

    }
}