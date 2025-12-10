package com.wolfyscript.viewportl.common.commands

import androidx.compose.runtime.Composable
import com.mojang.brigadier.CommandDispatcher
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import com.wolfyscript.viewportl.example.Counter
import com.wolfyscript.viewportl.example.SimpleNavigation
import com.wolfyscript.viewportl.example.SlotGridTest
import com.wolfyscript.viewportl.example.SlotInputTest
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument

object ExampleCommand {

    private const val EXAMPLE_ID_ARG = "example_id"

    private val examples: Map<Key, @Composable () -> Unit> = mapOf(
        Key.viewportl("counter") to { Counter() },
        Key.viewportl("slot_input") to { SlotInputTest() },
        Key.viewportl("slot_grid") to { SlotGridTest() },
        Key.viewportl("simple_navigation") to { SimpleNavigation() },
    )

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
                                        content = examples[exampleId] ?: {}
                                    )
                                    playerRuntime.openView()
                                }
                            }

                            return@executes 1
                        }.suggests { context, builder ->
                            for (key in examples.keys) {
                                builder.suggest(key.toString())
                            }
                            return@suggests builder.buildFuture()
                        }
                    )
            )
        )
    }

}