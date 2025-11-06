package com.wolfyscript.viewportl.common.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack

object ViewportlCommands {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        InputCommand.register(dispatcher)
        ExampleCommand.register(dispatcher)
    }

}