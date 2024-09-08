package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.ClickType
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.inventory.Slot
import org.spongepowered.api.item.inventory.menu.ClickTypes
import org.spongepowered.api.registry.RegistryTypes
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class ClickInteractionDetailsImpl(
    nativeClickType: org.spongepowered.api.item.inventory.menu.ClickType<*>,
    nativeSlot: Slot,
    override val rawSlot: Int,
    override val hotbarButton: Int
) : ClickInteractionDetails {

    override val slot: Int = nativeSlot[Keys.SLOT_INDEX].getOrNull() ?: throw IllegalStateException()
    override val clickType: ClickType = clickTypeMapping[nativeClickType] ?: throw IllegalStateException("Unexpected click type: " + nativeClickType.key(RegistryTypes.CLICK_TYPE))

    override fun onSlotValueUpdate(slot: Int, consumer: Consumer<ItemStack?>) {

    }

    override fun callSlotValueUpdate(slot: Int, itemStack: ItemStack?) {

    }

    override var valid: Boolean = true

    override val isShift: Boolean
        get() = clickType === ClickType.SHIFT_PRIMARY || clickType === ClickType.SHIFT_SECONDARY

    override val isSecondary: Boolean
        get() = clickType === ClickType.SECONDARY || clickType === ClickType.SHIFT_SECONDARY

    override val isPrimary: Boolean
        get() = clickType === ClickType.PRIMARY || clickType === ClickType.SHIFT_PRIMARY

    companion object {
        private val clickTypeMapping: Map<org.spongepowered.api.item.inventory.menu.ClickType<*>, ClickType> = buildMap {
            this[ClickTypes.CLICK_LEFT.get()] = ClickType.PRIMARY
            this[ClickTypes.CLICK_RIGHT.get()] = ClickType.SECONDARY
            this[ClickTypes.KEY_THROW_ONE.get()] = ClickType.DROP
            this[ClickTypes.KEY_THROW_ALL.get()] = ClickType.CONTROL_DROP
            this[ClickTypes.SHIFT_CLICK_LEFT.get()] = ClickType.SHIFT_PRIMARY
            this[ClickTypes.SHIFT_CLICK_RIGHT.get()] = ClickType.SHIFT_SECONDARY
            this[ClickTypes.CLICK_MIDDLE.get()] = ClickType.MIDDLE
            this[ClickTypes.KEY_SWAP.get()] = ClickType.NUMBER_KEY
            this[ClickTypes.DOUBLE_CLICK.get()] = ClickType.DOUBLE_CLICK
            this[ClickTypes.CLICK_LEFT_OUTSIDE.get()] = ClickType.CONTAINER_BORDER_PRIMARY
            this[ClickTypes.CLICK_RIGHT_OUTSIDE.get()] = ClickType.CONTAINER_BORDER_SECONDARY
        }
    }
}
