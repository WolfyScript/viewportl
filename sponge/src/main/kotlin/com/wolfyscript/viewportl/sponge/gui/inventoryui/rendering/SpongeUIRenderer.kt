package com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.scafall.sponge.api.wrappers.world.items.ItemStackWrapper
import com.wolfyscript.scafall.sponge.api.wrappers.world.items.SpongeItemStackConfig
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.CachedNodeRenderProperties
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.UIRenderer
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import net.kyori.adventure.text.Component
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.Sponge
import org.spongepowered.api.item.inventory.Container
import org.spongepowered.api.item.inventory.ContainerType
import org.spongepowered.api.item.inventory.ContainerTypes
import org.spongepowered.api.item.inventory.menu.InventoryMenu
import org.spongepowered.api.item.inventory.type.ViewableInventory
import java.util.*
import kotlin.jvm.optionals.getOrNull

class SpongeUIRenderer(runtime: ViewRuntime) : UIRenderer<SpongeUIRenderContext>(runtime) {

    private var inventoryMenu: InventoryMenu? = null

    override fun createContext(): SpongeUIRenderContext {
        return SpongeUIRenderContext(this)
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (slot in slots) {
            inventoryMenu?.inventory()?.set(slot, null)
        }
    }

    override fun changeWindow(window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime, null)
        val carrier = GuiCarrier(guiHolder)
        inventoryMenu = ViewableInventory.builder()
            .type { getInventoryType(window) }
            .completeStructure()
            .carrier(carrier)
            .plugin(runtime.viewportl.scafall.corePlugin.into<SpongePluginWrapper>().plugin) // TODO
            .build()
            .asMenu()
        carrier.menu = inventoryMenu
    }

    override fun render() {
        if (inventoryMenu == null) return

        val context = createContext()
        cachedProperties[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
        context.setSlotOffset(0)

        renderChildren(0, context)

        runtime.viewers.forEach { viewer ->
            Sponge.server().player(viewer).ifPresent { player ->
                if (player.openInventory().flatMap { it.currentMenu() }.orElse(null) != inventoryMenu) {
                    inventoryMenu?.open(player)
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

    override fun updateTitle(component: Component?) {
        inventoryMenu?.inventory()?.viewers()?.forEach {
            updateTitle(it.uniqueId(), component)
        }
    }

    private fun updateTitle(
        player: UUID,
        component: Component?
    ) {
        Sponge.server().player(player).getOrNull()?.openInventory()?.getOrNull()
            ?.let { obj: Container -> obj.currentMenu() }
            ?.ifPresent { inventoryMenu -> inventoryMenu.setTitle(component) }
    }

    fun setStack(i: Int, itemStackConfig: ItemStackConfig?) {
        if (itemStackConfig == null) {
            inventoryMenu?.inventory()?.set(i, null)
            return
        }
        require(itemStackConfig is SpongeItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                SpongeItemStackConfig::class.java.name,
                itemStackConfig.javaClass.name
            )
        }

        renderStack(i, itemStackConfig.constructItemStack())
    }

    fun renderStack(position: Int, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        require(itemStack is ItemStackWrapper) {
            String.format(
                "Cannot render stack! Invalid stack config type! Expected '%s' but received '%s'.",
                ItemStackWrapper::class.java.name,
                itemStack.javaClass.name
            )
        }

        setNativeStack(position, itemStack.ref)
    }

    fun renderStack(position: Int, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        require(itemStackConfig is SpongeItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                SpongeItemStackConfig::class.java.name,
                itemStackConfig.javaClass.name
            )
        }

        setNativeStack(
            position,
            (itemStackConfig.constructItemStack(
                EvalContext(),
                runtime.viewportl.scafall.adventure.miniMsg,
                itemStackContext.resolvers
            ) as ItemStackWrapper).ref
        )
    }

    private fun setNativeStack(i: Int, itemStack: org.spongepowered.api.item.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        if (itemStack == null) {
            inventoryMenu?.inventory()?.set(i, null)
            return
        }
        inventoryMenu?.inventory()?.set(i, itemStack)
    }

}