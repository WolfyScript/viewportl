package com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.scafall.sponge.api.wrappers.unwrap
import com.wolfyscript.scafall.sponge.api.wrappers.world.items.SpongeItemStackConfig
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.CachedNodeRenderProperties
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ItemStackContext
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import net.kyori.adventure.text.Component
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.Sponge
import org.spongepowered.api.item.inventory.ContainerType
import org.spongepowered.api.item.inventory.ContainerTypes
import org.spongepowered.api.item.inventory.menu.InventoryMenu
import org.spongepowered.api.item.inventory.type.ViewableInventory
import kotlin.jvm.optionals.getOrNull

class SpongeInvUIRenderer : InvUIRenderer<SpongeInvUIRenderer, SpongeInvUIRenderContext>() {

    companion object {
        init {
            registerComponentRenderer(SpongeInvUIRenderer::class.java, Key.parse(StaticNamespacedKey.KeyBuilder.createKeyString(
                ButtonImpl::class.java)), InventoryButtonComponentRenderer())
            registerComponentRenderer(SpongeInvUIRenderer::class.java, Key.parse(StaticNamespacedKey.KeyBuilder.createKeyString(
                SlotImpl::class.java)), InventorySlotComponentRenderer())
        }
    }

    private var inventoryMenu: InventoryMenu? = null

    override fun createContext(): SpongeInvUIRenderContext {
        return SpongeInvUIRenderContext(this)
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (slot in slots) {
            inventoryMenu?.inventory()?.set(slot, org.spongepowered.api.item.inventory.ItemStack.empty())
        }
    }

    override fun onWindowOpen(window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val carrier = GuiCarrier(guiHolder)
        inventoryMenu = ViewableInventory.builder()
            .type { getInventoryType(window) }
            .completeStructure()
            .carrier(carrier)
            .plugin(runtime.viewportl.scafall.corePlugin.into<SpongePluginWrapper>().plugin) // TODO
            .build()
            .asMenu()
        window.title?.let {
            inventoryMenu?.setTitle(it)
        }
        carrier.menu = inventoryMenu
    }

    override fun render() {
        if (inventoryMenu == null) return

        val context = createContext()
        computed[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
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
        // TODO: Fix issue?! Report ClassCastException: class org.spongepowered.common.network.SpongeServerEngineConnection cannot be cast to class net.minecraft.server.network.ServerGamePacketListenerImpl (org.spongepowered.common.network.SpongeServerEngineConnection and net.minecraft.server.network.ServerGamePacketListenerImpl are in unnamed module of loader cpw.mods.modlauncher.TransformingClassLoader @9d7ccfe)
//        inventoryMenu?.setTitle(component)
    }

    fun setStack(i: Int, itemStackConfig: ItemStackConfig?) {
        if (itemStackConfig == null) {
            inventoryMenu?.inventory()?.set(i, null)
            return
        }

        renderStack(i, itemStackConfig.constructItemStack())
    }

    fun renderStack(position: Int, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        setNativeStack(position, itemStack.unwrap())
    }

    fun renderStack(position: Int, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        setNativeStack(
            position,
            itemStackConfig.constructItemStack(
                EvalContext(),
                runtime.viewportl.scafall.adventure.miniMsg,
                itemStackContext.resolvers
            )?.unwrap()
        )
    }

    private fun setNativeStack(i: Int, itemStack: org.spongepowered.api.item.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        inventoryMenu?.inventory()?.set(i, itemStack ?: org.spongepowered.api.item.inventory.ItemStack.empty())
    }

}