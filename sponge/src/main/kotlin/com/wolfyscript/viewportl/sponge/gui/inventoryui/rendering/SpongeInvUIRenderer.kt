package com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.scafall.sponge.api.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.CachedNodeRenderProperties
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ItemStackContext
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.model.ModelGraph
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import net.kyori.adventure.text.Component
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.Sponge
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

    override fun onWindowOpen(runtime: ViewRuntime, window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val carrier = GuiCarrier(guiHolder)

        inventory = ViewableInventory.builder()
            .type { getInventoryType(window) }
            .completeStructure()
            .carrier(carrier)
            .plugin(runtime.viewportl.scafall.corePlugin.into<SpongePluginWrapper>().plugin)
            .build();

        window.title?.let {
            title = it
        }
        carrier.inventory = inventory
    }

    override fun render(runtime: ViewRuntime, model: ModelGraph) {
        if (inventory == null) return

        val context = createContext()
        computed[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
        context.setSlotOffset(0)

        renderChildren(model, 0, context)

        runtime.viewers.forEach { viewer ->
            Sponge.server().player(viewer).ifPresent { player ->
                if (player.openInventory().orElse(null) != inventory) {
                    player.openInventory(inventory, title)
                }
            }
        }
    }

    private fun getInventoryType(window: Window): ContainerType {
        return window.type?.let { type ->
            when (type) {
                WindowType.CUSTOM -> window.size?.let { size ->
                    val rows: Int = size / 9
                    ContainerTypes.registry().findValue<ContainerType?>(ResourceKey.minecraft("generic_9x$rows"))
                        .getOrNull()
                } ?: ContainerTypes.GENERIC_9X6.get()

                WindowType.HOPPER -> ContainerTypes.HOPPER.get()
                WindowType.DROPPER, WindowType.DISPENSER -> ContainerTypes.GENERIC_3X3.get()
            }
        } ?: window.size?.let { size ->
            val rows: Int = size / 9
            ContainerTypes.registry().findValue<ContainerType?>(ResourceKey.minecraft("generic_9x$rows")).getOrNull()
        } ?: ContainerTypes.GENERIC_9X6.get()
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