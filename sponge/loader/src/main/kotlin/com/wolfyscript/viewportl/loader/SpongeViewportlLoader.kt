package com.wolfyscript.viewportl.loader

import com.google.inject.Inject
import com.google.inject.Injector
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
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
class SpongeViewportlLoader @Inject constructor(
    private val injector: Injector,
    private val pluginContainer: PluginContainer,
) {

    @Listener
    fun onConstructPlugin(event: ConstructPluginEvent) {
        ScafallProvider.get().platformManager.registerImplementationModule(
            Key.key("scafall", "viewportl"),
            Viewportl::class.java,
            this.javaClass.classLoader,
            "viewportl-sponge.innerjar",
            "com.wolfyscript.viewportl.sponge.SpongeViewportlModule"
        )
    }

    @Listener
    fun onServerStarting(event: StartingEngineEvent<Server>) {
    }

    @Listener
    fun onServerStopping(event: StoppingEngineEvent<Server>) {
    }

    @Listener
    fun onRegisterCommands(event: RegisterCommandEvent<Command.Parameterized>) {

    }
}