package com.wolfyscript.viewportl.fabric.server.ui

import com.wolfyscript.viewportl.fabric.server.ui.inventory.FabricInvUIInteractionHandler
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.math.min

class CustomUIContainerMenu(
    player: Player,
    val container: CustomUIContainer,
    id: Int,
) : ChestMenu(getMenuType(container.size), id, player.inventory, container, container.size / 9) {

    override fun addSlot(slot: Slot): Slot {
        if (slot.container is CustomUIContainer) {
            return super.addSlot(CustomUISlot(slot))
        }
        return super.addSlot(slot)
    }

    override fun moveItemStackTo(itemStack: ItemStack, startSlot: Int, endSlot: Int, backwards: Boolean): Boolean {
        return safeMoveItemStackTo(itemStack, startSlot, endSlot, backwards)
    }

    override fun clicked(slotIndex: Int, buttonNum: Int, containerInput: ContainerInput, player: Player) {
        super.clicked(slotIndex, buttonNum, containerInput, player)

        // Clicks are only accepted in top container
        if (slotIndex < 0 || slotIndex >= container.containerSize) {
            return
        }
        val clickedSlot = slots[slotIndex]

        val interactionHandler = (container.runtime.interactionHandler as? FabricInvUIInteractionHandler)
        interactionHandler?.clicked(container.view.viewer, slotIndex)
    }

    /**
     * Copy of [moveItemStackTo] that makes sure [Slot.mayPlace] is called before manipulating any target slot.
     * (the [moveItemStackTo] doesn't call [Slot.mayPlace] when stacking on an existing item in the target slot)
     */
    private fun safeMoveItemStackTo(itemStack: ItemStack, startSlot: Int, endSlot: Int, backwards: Boolean): Boolean {
        var anythingChanged = false
        var destSlot: Int = if (backwards) {
            endSlot - 1
        } else {
            startSlot
        }

        if (itemStack.isStackable) {
            while (!itemStack.isEmpty) {
                if (backwards) {
                    if (destSlot < startSlot) {
                        break
                    }
                } else if (destSlot >= endSlot) {
                    break
                }

                val slot = this.slots[destSlot]
                val target = slot.item
                if (!target.isEmpty &&
                    slot.mayPlace(itemStack) &&
                    ItemStack.isSameItemSameComponents(itemStack, target)
                ) {
                    val totalStack: Int = target.count + itemStack.count
                    val maxStackSize = slot.getMaxStackSize(target)
                    if (totalStack <= maxStackSize) {
                        itemStack.count = 0
                        target.count = totalStack
                        slot.setChanged()
                        anythingChanged = true
                    } else if (target.count < maxStackSize) {
                        itemStack.shrink(maxStackSize - target.count)
                        target.count = maxStackSize
                        slot.setChanged()
                        anythingChanged = true
                    }
                }

                if (backwards) {
                    --destSlot
                } else {
                    ++destSlot
                }
            }
        }

        if (!itemStack.isEmpty) {
            destSlot = if (backwards) {
                endSlot - 1
            } else {
                startSlot
            }

            while (true) {
                if (backwards) {
                    if (destSlot < startSlot) {
                        break
                    }
                } else if (destSlot >= endSlot) {
                    break
                }

                val slot = this.slots[destSlot]
                val target = slot.item
                if (target.isEmpty && slot.mayPlace(itemStack)) {
                    val maxStackSize = slot.getMaxStackSize(itemStack)
                    slot.setByPlayer(itemStack.split(min(itemStack.count, maxStackSize)))
                    slot.setChanged()
                    anythingChanged = true
                    break
                }

                if (backwards) {
                    --destSlot
                } else {
                    ++destSlot
                }
            }
        }

        return anythingChanged
    }

}

fun getMenuType(size: Int): MenuType<*> {
    return when (size) {
        9 -> MenuType.GENERIC_9x1
        9 * 2 -> MenuType.GENERIC_9x2
        9 * 3 -> MenuType.GENERIC_9x3
        9 * 4 -> MenuType.GENERIC_9x4
        9 * 5 -> MenuType.GENERIC_9x5
        9 * 6 -> MenuType.GENERIC_9x6
        else -> MenuType.GENERIC_9x3
    }
}

/**
 * Wraps the specified [slot] to support custom [com.wolfyscript.viewportl.ui.modifier.SlotInputModifier] behaviour
 * and prevent any other interactions in the [CustomUIContainerMenu] by default.
 */
class CustomUISlot(val slot: Slot) : Slot(slot.container, slot.index, slot.x, slot.y) {

    init {
        require(slot.container is CustomUIContainer)
    }

    val customContainer: CustomUIContainer
        get() = slot.container as CustomUIContainer

    private val interactionHandler: FabricInvUIInteractionHandler?
        get() = (customContainer.runtime.interactionHandler as? FabricInvUIInteractionHandler)

    override fun mayPlace(itemStack: ItemStack): Boolean {
        interactionHandler?.findSlotInputAt(customContainer.view.viewer, slot.containerSlot)?.let { input ->
            return input.canPlace(itemStack.snapshot())
        }
        return false
    }

    override fun mayPickup(player: Player): Boolean {
        interactionHandler?.findSlotInputAt(player.uuid, slot.containerSlot)?.let { input ->
            return input.canTake(item.snapshot())
        }
        return false
    }

    override fun setChanged() {
        updateSlotInput()
        slot.setChanged()
    }

    private fun updateSlotInput() {
        val slotInput = interactionHandler?.findSlotInputAt(customContainer.view.viewer, slot.containerSlot)
        if (slotInput != null) {
            slotInput.onValueChange(item.snapshot())
        }
    }

    override fun getItem(): ItemStack {
        return slot.item
    }

    override fun set(itemStack: ItemStack) {
        container.setItem(slot.containerSlot, itemStack)
        this.setChanged()
    }

    override fun setByPlayer(itemStack: ItemStack) {
        this.setByPlayer(itemStack, item)
    }

    override fun setByPlayer(itemStack: ItemStack, previous: ItemStack) {
        this.set(itemStack)
    }

    override fun getMaxStackSize(): Int {
        return slot.maxStackSize
    }

    override fun getMaxStackSize(itemStack: ItemStack): Int {
        return slot.maxStackSize
    }

    override fun allowModification(player: Player): Boolean {
        return slot.allowModification(player)
    }

    override fun tryRemove(amount: Int, maxAmount: Int, player: Player): Optional<ItemStack> {
        return slot.tryRemove(amount, maxAmount, player)
    }

    override fun hasItem(): Boolean {
        return slot.hasItem()
    }

    override fun getContainerSlot(): Int {
        return slot.containerSlot
    }

    override fun getNoItemIcon(): Identifier? {
        return slot.noItemIcon
    }

    override fun equals(other: Any?): Boolean {
        return slot.equals(other)
    }

    override fun hashCode(): Int {
        return slot.hashCode()
    }

    override fun safeTake(amount: Int, maxAmount: Int, player: Player): ItemStack {
        return super.safeTake(amount, maxAmount, player)
    }

    override fun isHighlightable(): Boolean {
        return slot.isHighlightable
    }

    override fun isActive(): Boolean {
        return slot.isActive
    }

    override fun isFake(): Boolean {
        return slot.isFake
    }

    override fun toString(): String {
        return slot.toString()
    }

    override fun onQuickCraft(picked: ItemStack, original: ItemStack) {
        slot.onQuickCraft(picked, original)
    }

    override fun onTake(player: Player, carried: ItemStack) {
        this.setChanged()
    }

    override fun remove(amount: Int): ItemStack {
        return slot.remove(amount)
    }

    override fun safeInsert(inputStack: ItemStack, inputAmount: Int): ItemStack {
        return super.safeInsert(inputStack, inputAmount)
    }

    override fun safeInsert(stack: ItemStack): ItemStack {
        return super.safeInsert(stack)
    }

    override fun checkTakeAchievements(carried: ItemStack) {}

    override fun onQuickCraft(picked: ItemStack, count: Int) {}

    override fun onSwapCraft(count: Int) {}

}
