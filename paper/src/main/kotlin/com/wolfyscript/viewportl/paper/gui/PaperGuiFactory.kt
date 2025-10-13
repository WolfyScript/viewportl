package com.wolfyscript.viewportl.paper.gui

import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.paper.gui.inventorygui.rendering.PaperInvUIRenderer
import com.wolfyscript.viewportl.spigotlike.gui.factories.SpigotLikeGuiFactoryImpl
import org.bukkit.plugin.Plugin

class PaperGuiFactory(bukkitPlugin: Plugin) : SpigotLikeGuiFactoryImpl(bukkitPlugin) {

    override fun createInventoryRenderer(): Renderer<*> {
        return PaperInvUIRenderer()
    }

}