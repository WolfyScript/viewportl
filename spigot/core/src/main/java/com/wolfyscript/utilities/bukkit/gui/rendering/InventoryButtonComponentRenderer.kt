package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.eval.context.EvalContext
import com.wolfyscript.utilities.gui.ItemStackContext
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.rendering.ComponentRenderer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class InventoryButtonComponentRenderer : ComponentRenderer<Button, InvGUIRenderContext> {

    override fun key(): NamespacedKey = BukkitNamespacedKey("wolfyutils", "inventory/button")

    override fun render(context: InvGUIRenderContext, component: Button) {

        context.renderer.renderStack(context.currentOffset(), component.icon().stack, object: ItemStackContext {

            override fun resolvers(): TagResolver = component.icon().resolvers

            override fun miniMessage(): MiniMessage = context.renderer.runtime.wolfyUtils.chat.miniMessage

            override fun evalContext(): EvalContext = EvalContext()

        })

    }
}