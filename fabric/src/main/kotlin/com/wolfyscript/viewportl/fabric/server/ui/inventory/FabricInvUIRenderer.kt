package com.wolfyscript.viewportl.fabric.server.ui.inventory

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.fabric.inject.OpenInventoryUIExt
import com.wolfyscript.viewportl.fabric.server.ui.CustomUIContainer
import com.wolfyscript.viewportl.runtime.UIRuntime
import com.wolfyscript.viewportl.runtime.View
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.modifier.InventoryDrawScope
import net.kyori.adventure.text.Component
import net.minecraft.world.item.ItemStack

class FabricInvUIRenderer : InvUIRenderer<FabricInvUIRenderContext>(FabricInvUIRenderContext::class.java) {

    internal var container: CustomUIContainer = createInventoryUI(27, Component.text("Inventory UI"))

    override fun createContext(): FabricInvUIRenderContext {
        return FabricInvUIRenderContext(this)
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (i in 0 until container.containerSize) {
            container.setItem(i, ItemStack.EMPTY)
        }
    }

    override fun subDrawScope(
        offset: Offset,
        width: Int,
        height: Int,
    ): InventoryDrawScope {
        return CacheInventoryDrawScope(offset, width, height, container)
    }

    override fun onViewInit(
        runtime: UIRuntime,
        view: View,
    ) {
        initInventoryUI(runtime, view)
    }

    override fun onViewChange(
        runtime: UIRuntime,
        view: View,
    ) {
        initInventoryUI(runtime, view)
    }

    private fun initInventoryUI(runtime: UIRuntime, view: View) {
        val finalTitle = view.properties.title.component ?: Component.text("Inventory UI")
        container = createInventoryUI(view.properties.dimensions.inventorySize, finalTitle)

        ScafallProvider.get().server?.minecraftServer?.playerList?.let { playerList ->
            runtime.viewers.forEach {
                playerList.getPlayer(it)?.let { player ->
                    (player as OpenInventoryUIExt).`viewportl$openUIContainer`(container)
                }
            }
        }

    }
}