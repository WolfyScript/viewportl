package org.example.viewportl

import com.google.inject.Inject
import com.google.inject.Injector
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl
import org.example.viewportl.common.gui.CounterExampleKotlin
import org.example.viewportl.common.gui.FetchExampleKotlin
import org.example.viewportl.common.gui.NestedRoutingExampleKotlin
import org.example.viewportl.common.gui.StackEditorExampleKotlin
import org.example.viewportl.common.gui.StackSlotsExampleKotlin
import org.spongepowered.api.Server
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.entity.living.player.server.ServerPlayer
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
        val viewportl = ScafallProvider.get().viewportl

        val manager = viewportl.guiManager
        CounterExampleKotlin.register(manager)
        FetchExampleKotlin.register(manager)
        NestedRoutingExampleKotlin.register(manager)
        StackEditorExampleKotlin.register(manager)
        StackSlotsExampleKotlin.register(manager)
    }

    @Listener
    fun onServerStarting(event: StartingEngineEvent<Server>) {

    }

    @Listener
    fun onServerStopping(event: StoppingEngineEvent<Server>) {

    }

    @Listener
    fun onRegisterCommands(event: RegisterCommandEvent<Command.Parameterized>) {
        val viewportl = ScafallProvider.get().viewportl

        // Register a simple command
        // When possible, all commands should be registered within a command register event
        val guiParam = Parameter.string().key("gui").optional().build()
        event.register(
            pluginContainer, Command.builder()
                .addParameter(guiParam)
                .executor { context: CommandContext ->
                    val gui = if (context.hasAny(guiParam)) context.requireOne(guiParam) else "example_counter"

                    context.cause().first(ServerPlayer::class.java)
                        .ifPresent { serverPlayer: ServerPlayer ->
                            viewportl.guiManager.createViewAndOpen(gui, serverPlayer.uniqueId())
                        }
                    CommandResult.success()
                } //.permission("wolfyutils.command.gui")
                //.executionRequirements(cause -> cause.context().get(EventContextKeys.PLAYER).isPresent())
                .build(), "gui_example")

        event.register(
            pluginContainer, Command.builder()
                .addParameter(guiParam)
                .executor { context: CommandContext ->
                    val gui = if (context.hasAny(guiParam)) context.requireOne(guiParam) else "example_counter"
                    viewportl.guiManager.clearFromCache(gui)
                    CommandResult.success()
                } //.permission("wolfyutils.command.gui")
                //.executionRequirements(cause -> cause.context().get(EventContextKeys.PLAYER).isPresent())
                .build(), "gui_clear")
    }

}