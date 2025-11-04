package com.wolfyscript.viewportl.common.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object ExampleCommand {

    private const val EXAMPLE_ID_ARG = "example_id"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("viewportl").then(
                Commands.literal("example")
                    .then(
                        Commands.argument(EXAMPLE_ID_ARG, StringArgumentType.string()).executes { ctx ->
                            val viewportl = ScafallProvider.get().viewportl
                            val executor = ctx.source.player ?: return@executes 0
                            val args = StringArgumentType.getString(ctx, EXAMPLE_ID_ARG)

                            return@executes 1
                        }.suggests { context, builder ->



                            return@suggests builder.buildFuture()
                        }
                    )
            )
        )
    }

}