package com.wolfyscript.utilities.bukkit.gui.example

import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.viewportl.gui.example.CounterExampleKotlin
import com.wolfyscript.viewportl.gui.example.StackEditorExampleKotlin
import com.wolfyscript.viewportl.gui.example.StackSlotsExampleKotlin

class TestGUI(private val core: WolfyCore) {

    fun initWithConfig() {
        val manager = core.wolfyUtils.guiManager
        CounterExampleKotlin.registerExampleCounter(manager)
        StackEditorExampleKotlin.registerStackEditor(manager)
        StackSlotsExampleKotlin.registerStackSlotsExample(manager)
    }

}
