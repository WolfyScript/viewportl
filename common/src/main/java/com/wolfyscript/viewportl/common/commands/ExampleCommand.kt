package com.wolfyscript.viewportl.common.commands

import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import com.wolfyscript.viewportl.example.Counter
import com.wolfyscript.viewportl.example.SlotGridTest
import com.wolfyscript.viewportl.example.SlotInputTest
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
                                    playerRuntime.setNewView(
                                        exampleId,
                                        size = 36,
                                        content = when (exampleId) {
                                            Key.viewportl("counter") -> {
                                                { Counter() }
                                            }

                                            Key.viewportl("slot_input_test") -> {
                                                { SlotInputTest() }
                                            }

                                            Key.viewportl("slot_grid_test") -> {
                                                { SlotGridTest() }
                                            }

                                            else -> {
                                                {}
                                            }
                                        })
                                    playerRuntime.openView()
                                }
                            }

                            return@executes 1
                        }.suggests { context, builder ->
                            builder.suggest("viewportl:counter")
                                .suggest("viewportl:slot_input_test")
                            return@suggests builder.buildFuture()
                        }
                    )
            )
        )
    }

}