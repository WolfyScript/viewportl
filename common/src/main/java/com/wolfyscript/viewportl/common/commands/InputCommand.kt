package com.wolfyscript.viewportl.common.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object InputCommand {

    private const val INPUT_ARG = "input"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("io")
                .then(Commands.argument(INPUT_ARG, StringArgumentType.greedyString()).executes { ctx ->
                    val viewportl = ScafallProvider.get().viewportl
                    val executor = ctx.source.player ?: return@executes 0
                    val args = StringArgumentType.getString(ctx, INPUT_ARG)

                    viewportl.guiManager.getViewRuntime(executor.uuid).let { runtime ->
                        runtime.view?.let { window ->
                            ScafallProvider.get().scheduler.syncTask(ScafallProvider.get().modInfo) {
                                window.onTextInput?.run(
                                    executor.wrap(),
                                    runtime,
                                    args,
                                    args.split(" ").toTypedArray()
                                )
                                window.onTextInput = null
                                window.onTextInputTabComplete = null
                                // TODO: reopen view
                            }
                        }
                    }

                    return@executes 1
                }.suggests { ctx, builder ->
                    val viewportl = ScafallProvider.get().viewportl
                    val executor = ctx.source.player ?: return@suggests builder.buildFuture()
                    val args = StringArgumentType.getString(ctx, INPUT_ARG)

                    viewportl.guiManager.getViewRuntime(executor.uuid).let { runtime ->
                        runtime.view?.onTextInputTabComplete?.apply(
                            executor.wrap(),
                            runtime,
                            args,
                            args.split(" ").toTypedArray()
                        )
                    }
                    return@suggests builder.buildFuture()
                })
        )
    }

}