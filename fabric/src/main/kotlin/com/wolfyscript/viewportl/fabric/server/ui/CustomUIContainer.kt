package com.wolfyscript.viewportl.fabric.server.ui

import com.wolfyscript.viewportl.runtime.UIRuntime
import com.wolfyscript.viewportl.runtime.View
import net.kyori.adventure.text.Component
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class CustomUIContainer(
    val size: Int,
    val view: View,
    val runtime: UIRuntime,
    val title: Component
) : Container {

    private val items: NonNullList<ItemStack> = NonNullList.withSize(size, ItemStack.EMPTY)

    override fun getContainerSize(): Int = size

    override fun isEmpty(): Boolean = items.all { it.isEmpty }

    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(items, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }
        return itemStack
    }

    override fun canPlaceItem(slot: Int, itemStack: ItemStack): Boolean {
        return super.canPlaceItem(slot, itemStack)
    }

    override fun canTakeItem(into: Container, slot: Int, itemStack: ItemStack): Boolean {
        return super.canTakeItem(into, slot, itemStack)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(items, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        this.setChanged()
    }

    override fun setChanged() { }

    override fun stillValid(player: Player): Boolean = true

    override fun clearContent() {
        items.clear()
    }

}