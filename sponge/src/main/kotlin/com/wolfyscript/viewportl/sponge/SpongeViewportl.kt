package com.wolfyscript.viewportl.sponge

import com.wolfyscript.viewportl.common.CommonViewportl
import com.wolfyscript.viewportl.common.gui.GuiAPIManagerImpl
import com.wolfyscript.viewportl.common.registry.CommonViewportlRegistries
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.registry.ViewportlRegistries
import com.wolfyscript.viewportl.sponge.gui.factories.GuiFactoryImpl
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer
import java.lang.invoke.MethodHandles

class SpongeViewportl(val plugin: PluginContainer) : CommonViewportl() {

    override val guiManager: GuiAPIManager = GuiAPIManagerImpl(this)
    override val guiFactory: GuiFactory = GuiFactoryImpl()
    override val registries: ViewportlRegistries = CommonViewportlRegistries(this)

    override fun onInit() {

    }

    fun initServer() {

    }

    fun init() {
        val eventManager = Sponge.eventManager()
        eventManager.registerListeners(plugin, this, MethodHandles.lookup())
    }

    fun enable() {

    }

    fun unload() {

    }

    @Listener
    fun onCommandRegister(event: RegisterCommandEvent<Command.Parameterized>) {
        val inputParam = Parameter.remainingJoinedStrings().key("input").build()

        event.register(
            plugin, Command.builder()
                .addParameter(inputParam)
                .executor { context: CommandContext ->
                    val sender = context.contextCause().first(ServerPlayer::class.java).get()
                    val input = context.requireOne(inputParam)

                    guiManager.getViewManagersFor(sender.uniqueId())
                        .map { runtime -> Pair(runtime, runtime.window) }
                        .filter { pair -> pair.second != null && pair.second!!.onTextInput != null }
                        .forEach { pair ->
                            val runtime = pair.first
                            val window = pair.second
                            scafall.scheduler.syncTask(this.scafall.modInfo) {
                                window!!.onTextInput!!.run(
                                    sender.wrap(),
                                    runtime,
                                    input,
                                    input.split(" ").toTypedArray()
                                )
                                window.onTextInput = null
                                window.onTextInputTabComplete = null
                                runtime.open()
                            }
                        }
                    CommandResult.success()
                } //.permission("wolfyutils.command.gui")
                .executionRequirements { cause -> cause.contextCause().first(ServerPlayer::class.java).isPresent }
                .build(), "io")

    }

}