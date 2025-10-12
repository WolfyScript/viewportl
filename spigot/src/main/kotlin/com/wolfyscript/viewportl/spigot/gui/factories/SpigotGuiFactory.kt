package com.wolfyscript.viewportl.spigot.gui.factories

import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.spigot.gui.inventorygui.rendering.SpigotInvUIRenderer
import com.wolfyscript.viewportl.spigotlike.gui.factories.SpigotLikeGuiFactoryImpl
import org.bukkit.plugin.Plugin

class SpigotGuiFactory(bukkitPlugin: Plugin) : SpigotLikeGuiFactoryImpl(bukkitPlugin) {

    override fun createInventoryRenderer(): Renderer<*> {
        return SpigotInvUIRenderer()
    }

}