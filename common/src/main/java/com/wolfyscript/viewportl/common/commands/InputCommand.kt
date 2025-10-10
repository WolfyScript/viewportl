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

                    viewportl.guiManager.getViewManagersFor(executor.uuid)
                        .map { runtime -> Pair(runtime, runtime.window) }
                        .filter { pair -> pair.second != null && pair.second!!.onTextInput != null }
                        .forEach { pair ->
                            val runtime = pair.first
                            val window = pair.second

                            ScafallProvider.get().scheduler.syncTask(ScafallProvider.get().modInfo) {
                                window!!.onTextInput!!.run(
                                    executor.wrap(),
                                    runtime,
                                    args,
                                    args.split(" ").toTypedArray()
                                )
                                window.onTextInput = null
                                window.onTextInputTabComplete = null
                                runtime.open()
                            }
                        }

                    return@executes 1
                }.suggests { ctx, builder ->
                    val viewportl = ScafallProvider.get().viewportl
                    val executor = ctx.source.player ?: return@suggests builder.buildFuture()
                    val args = StringArgumentType.getString(ctx, INPUT_ARG)

                    viewportl.guiManager.getViewManagersFor(executor.uuid)
                        .map { viewManager -> Pair(viewManager, viewManager.window) }
                        .filter { pair -> pair.second != null && pair.second!!.onTextInputTabComplete != null }
                        .findFirst()
                        .map { pair ->
                            val runtime = pair.first
                            val window = pair.second
                            window!!.onTextInputTabComplete!!.apply(
                                executor.wrap(),
                                runtime,
                                args,
                                args.split(" ").toTypedArray()
                            )
                        }.orElse(listOf())
                    return@suggests builder.buildFuture()
                })
        )
    }

}