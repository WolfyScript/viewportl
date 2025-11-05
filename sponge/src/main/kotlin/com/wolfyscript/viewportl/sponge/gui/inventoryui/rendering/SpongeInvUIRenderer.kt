package com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.sponge.SpongeViewportl
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import com.wolfyscript.viewportl.viewportl
import net.kyori.adventure.text.Component
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.item.inventory.ContainerType
import org.spongepowered.api.item.inventory.ContainerTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.menu.InventoryMenu
import org.spongepowered.api.item.inventory.type.ViewableInventory
import kotlin.jvm.optionals.getOrNull

class SpongeInvUIRenderer : InvUIRenderer<SpongeInvUIRenderContext>(SpongeInvUIRenderContext::class.java) {

    private var inventory: Inventory? = null
    private var title: Component? = null
    private var inventoryMenu: InventoryMenu? = null

    override fun createContext(): SpongeInvUIRenderContext {
        return SpongeInvUIRenderContext(this)
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (slot in slots) {
            inventory?.set(slot, org.spongepowered.api.item.inventory.ItemStack.empty())
        }
    }

    override fun subDrawScope(
        offset: Offset,
        width: Int,
        height: Int,
    ): InventoryDrawScope {
        TODO("Not yet implemented")
    }

    override fun onWindowOpen(runtime: ViewRuntime, window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val carrier = GuiCarrier(guiHolder)

        inventory = ViewableInventory.builder()
            .type { getInventoryType(window) }
            .completeStructure()
            .carrier(carrier)
            .plugin((ScafallProvider.get().viewportl as SpongeViewportl).plugin)
            .build()

        window.title?.let {
            title = it
        }
        carrier.inventory = inventory
    }

    private fun getInventoryType(window: Window): ContainerType {
        return when (window.type) {
            WindowType.CUSTOM -> window.size.let { size ->
                val rows: Int = size / 9
                ContainerTypes.registry().findValue<ContainerType?>(ResourceKey.minecraft("generic_9x$rows"))
                    .getOrNull()
            } ?: ContainerTypes.GENERIC_9X6.get()

            WindowType.HOPPER -> ContainerTypes.HOPPER.get()
            WindowType.DROPPER, WindowType.DISPENSER -> ContainerTypes.GENERIC_3X3.get()
        }
    }

    fun setStack(i: Int, itemStackConfig: ItemStackConfig?) {
        if (itemStackConfig == null) {
            inventory?.set(i, null)
            return
        }

        renderStack(i, itemStackConfig.constructItemStack())
    }

    fun renderStack(position: Int, itemStack: ScafallItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        setNativeStack(position, itemStack.unwrap())
    }

    private fun setNativeStack(i: Int, itemStack: org.spongepowered.api.item.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        inventory?.set(i, itemStack ?: org.spongepowered.api.item.inventory.ItemStack.empty())
    }

}