package com.wolfyscript.viewportl.common.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import com.wolfyscript.viewportl.example.counter.Counter
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument

object ExampleCommand {

    private const val EXAMPLE_ID_ARG = "example_id"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("viewportl").then(
                Commands.literal("example")
                    .then(
                        Commands.argument(EXAMPLE_ID_ARG, ResourceLocationArgument.id()).executes { ctx ->
                            val viewportl = ScafallProvider.get().viewportl
                            val executor = ctx.source.player ?: return@executes 0
                            val exampleId = ResourceLocationArgument.getId(ctx, EXAMPLE_ID_ARG).toKey()

                            ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().modInfo) {
                                viewportl.guiManager.getViewRuntime(executor.uuid).let { playerRuntime ->
                                    playerRuntime.joinViewer(executor.uuid)
                                    playerRuntime.createNewWindow(
                                        exampleId,
                                        size = 36,
                                        content = when (exampleId) {
                                            Key.viewportl("counter") -> {
                                                { Counter() }
                                            }

                                            else -> {
                                                {}
                                            }
                                        })
                                    playerRuntime.open()
                                }
                            }

                            return@executes 1
                        }.suggests { context, builder ->
                            builder.suggest("viewportl:counter")
                            return@suggests builder.buildFuture()
                        }
                    )
            )
        )
    }

}