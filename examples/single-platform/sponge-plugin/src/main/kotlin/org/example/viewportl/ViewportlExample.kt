package org.example.viewportl

import com.google.inject.Inject
import com.google.inject.Injector
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.sponge.init
import com.wolfyscript.viewportl.sponge.instance
import org.example.viewportl.guis.CounterExampleKotlin
import org.example.viewportl.guis.StackEditorExampleKotlin
import org.example.viewportl.guis.StackSlotsExampleKotlin
import org.spongepowered.api.Server
import org.spongepowered.api.command.Command
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.api.event.lifecycle.StartingEngineEvent
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("viewportl_example")
class ViewportlExample @Inject constructor(private val injector: Injector, private val pluginContainer: PluginContainer) {

    @Listener
    fun onConstructPlugin(event: ConstructPluginEvent) {
        Viewportl.init(pluginContainer) // Init the viewportl instance (& Scafall if not yet initiated!)

        // From this point onward you can get the instances via
        val viewportl = Viewportl.instance
        val scafall = ScafallProvider.get()

        // then register the guis
        val manager = viewportl.guiManager
        CounterExampleKotlin.registerExampleCounter(manager)
        StackEditorExampleKotlin.registerStackEditor(manager)
        StackSlotsExampleKotlin.registerStackSlotsExample(manager)

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