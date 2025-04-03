package com.wolfyscript.viewportl.sponge

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.scafall.sponge.api.wrappers.wrap
import com.wolfyscript.viewportl.Viewportl
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
import java.lang.invoke.MethodHandles

class SpongeViewportl : Viewportl {

    override val guiManager: GuiAPIManager = GuiAPIManagerImpl(this)
    override val guiFactory: GuiFactory = GuiFactoryImpl()
    override val registries: ViewportlRegistries = CommonViewportlRegistries(this)
    val spongePlugin = scafall.corePlugin.into<SpongePluginWrapper>().plugin

    fun init() {
        val eventManager = Sponge.eventManager()
        eventManager.registerListeners(spongePlugin, this, MethodHandles.lookup())
    }

    @Listener
    fun onCommandRegister(event: RegisterCommandEvent<Command.Parameterized>) {
        val inputParam = Parameter.remainingJoinedStrings().key("input").build()

        event.register(
            spongePlugin, Command.builder()
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
                            scafall.scheduler.syncTask(this.scafall.corePlugin, Runnable {
                                window!!.onTextInput!!.run(sender.wrap(), runtime, input, input.split(" ").toTypedArray())
                                window.onTextInput = null
                                window.onTextInputTabComplete = null
                                runtime.open()
                            })
                        }
                    CommandResult.success()
                } //.permission("wolfyutils.command.gui")
//                .executionRequirements(cause -> cause.context().get(EventContextKeys.PLAYER).isPresent())
                .build(), "io")

    }

}