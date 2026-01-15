/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.wolfyscript.scafall.spigot.api.wrappers.utils.snapshot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.viewportl.common.gui.ViewImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.InvUIInteractionHandler
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.compose.modifier.SlotInputModifierNode
import net.minecraft.core.component.DataComponents
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class SpigotLikeInvUIInteractionHandler(val bukkitPlugin: Plugin) :
    InvUIInteractionHandler<InventoryUIInteractionContext>() {

    override var isBusy: Boolean = false

    override fun onViewOpened(view: View) {
        super.onViewOpened(view)
    }

    override fun dispose() {
        super.dispose()
    }

    override fun onClick(context: InventoryUIInteractionContext) {
        val event = context.event as? InventoryClickEvent ?: return
        val clickedTopInv = event.clickedInventory == event.view.topInventory

        event.isCancelled = true // prevent any interaction, handle normal click & slot input separately

        isBusy = true
        val root = (runtime.views[event.whoClicked.uniqueId] as? ViewImpl)?.root ?: return

        val originalCursor = event.cursor
        val originalSlotStack = event.currentItem

        var newCursor: ItemStack? = originalCursor.clone()
        var newSlotStack: ItemStack? = originalSlotStack?.clone()

        if (clickedTopInv) {
            onClick(event.slot, root)

            if (event.action == InventoryAction.PICKUP_FROM_BUNDLE) {
                originalSlotStack?.wrap()?.unwrap()?.let { stack ->
                    val bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS)
                    if (bundleContents != null) {
                        onScrollSelectSubmit(event.slot, root, bundleContents.selectedItem)
                    }
                }
            }

            val slotInput = getSlotInputFor(event.slot, root)
            if (slotInput != null) {
                val action = event.action
                when (action) {
                    // place/collect stack
                    InventoryAction.PLACE_ONE -> {
                        if (slotInput.canPlace(originalCursor.snapshot())) {
                            if (newSlotStack == null) {
                                newSlotStack = newCursor?.let {
                                    val copy = it.clone()
                                    copy.amount = 1
                                    copy
                                }
                                newCursor?.apply {
                                    amount -= 1
                                }
                            } else if (newCursor != null && newCursor.isSimilar(newSlotStack)) {
                                newSlotStack.amount += 1
                                newCursor.amount -= 1
                            }
                        }
                    }

                    InventoryAction.PLACE_ALL -> {
                        if (slotInput.canPlace(originalCursor.snapshot())) {
                            if (newSlotStack == null) {
                                newSlotStack = newCursor?.clone()
                            } else {
                                newSlotStack.amount += newCursor?.amount ?: 0
                            }
                            newCursor = null
                        }
                    }

                    InventoryAction.PLACE_SOME -> {
                        if (newSlotStack != null && slotInput.canPlace(originalCursor.snapshot())) {
                            val amountToAdd = newCursor?.amount ?: 0

                            val newSlotAmount =
                                (newSlotStack.amount + amountToAdd).coerceAtMost(newSlotStack.maxStackSize)
                            val newCursorAmount = newSlotAmount - newSlotStack.amount

                            newSlotStack.amount = newSlotAmount
                            newCursor?.amount = newCursorAmount
                        }
                    }

                    InventoryAction.PICKUP_ONE -> {
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            if (newCursor == null || newCursor.type == Material.AIR) {
                                newCursor = newSlotStack?.clone()
                                newSlotStack = null
                            } else {
                                newCursor.amount += 1
                                newSlotStack?.apply {
                                    amount -= 1
                                }
                            }
                        }
                    }

                    InventoryAction.PICKUP_SOME -> {
                        /*
                        Doesn't seem to be called ever...
                        apparently it is called when a full stack is picked up, but with items left in the inventory?!
                        */
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            if (newCursor == null || newCursor.type == Material.AIR) {
                                newCursor = newSlotStack
                            } else {
                                newCursor.amount += newSlotStack?.amount ?: 0
                            }
                        }
                    }

                    InventoryAction.PICKUP_HALF -> {
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            val amount = newSlotStack?.amount ?: 0
                            val splitAmount = amount.div(2)

                            if (newCursor == null || newCursor.type == Material.AIR) {
                                newCursor = newSlotStack?.clone()
                            }
                            newCursor?.amount = amount - splitAmount
                            newSlotStack?.amount = splitAmount
                        }
                    }

                    InventoryAction.PICKUP_ALL -> {
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            if (newCursor == null || newCursor.type == Material.AIR) {
                                newCursor = newSlotStack?.clone()
                            } else {
                                // IDK if this can actually happen, but just in case it does
                                newCursor.amount += newSlotStack?.amount ?: 0
                            }
                            newSlotStack = null
                        }
                    }

                    InventoryAction.SWAP_WITH_CURSOR -> {
                        if (
                            (originalSlotStack == null || slotInput.canTake(originalSlotStack.snapshot())) &&
                            slotInput.canPlace(originalCursor.snapshot())
                        ) {
                            val originalSlot = newSlotStack?.clone()
                            newSlotStack = newCursor?.clone()
                            newCursor = originalSlot
                        }
                    }

                    // Dropping the item out of the inventory using the drop keybind (default: Q)
                    InventoryAction.DROP_ONE_SLOT -> {
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            newSlotStack?.apply {
                                amount -= 1
                            }
                        }
                    }

                    InventoryAction.DROP_ALL_SLOT -> {
                        if (originalSlotStack != null && slotInput.canTake(originalSlotStack.snapshot())) {
                            newSlotStack = null
                        }
                    }

                    // Hotbar swapping using the number keys
                    InventoryAction.HOTBAR_SWAP -> {
                        val clickedStack = newSlotStack?.clone()
                        val target = event.whoClicked.inventory.getItem(event.hotbarButton)
                        if (
                            (clickedStack == null || slotInput.canTake(clickedStack.snapshot())) &&
                            (target == null || slotInput.canPlace(target.snapshot()))
                        ) {
                            newSlotStack = target
                            event.whoClicked.inventory.setItem(event.hotbarButton, clickedStack)
                        }
                    }

                    // Moving items between inventories using shift clicking
                    InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                        if (newSlotStack != null) {
                            moveToOtherInventory(
                                newSlotStack,
                                event.view,
                                event.view.topInventory,
                                event.view.bottomInventory
                            ) { i, stack ->
                                getSlotInputFor(i, root)
                            }
                        }
                    }

                    InventoryAction.COLLECT_TO_CURSOR -> {
                        if (newCursor != null) {
                            // calculate the slots that should be collected. Skip Buttons and other components that invalidate the transaction
                            collectToCursor(newCursor, event.view) { index, stack ->
                                getSlotInputFor(index, root)
                            }
                        }
                        newSlotStack = null
                    }

                    else -> {
                        /* Click action not handled */
                        isBusy = false
                        return
                    }
                }

                // Apply calculated slot changes
                event.view.setCursor(newCursor)
                event.currentItem = newSlotStack

                slotInput.onValueChange(newSlotStack?.snapshot() ?: net.minecraft.world.item.ItemStack.EMPTY.snapshot())
            }
            isBusy = false
        } else {
            // Handle some clicks that occur in the bottom inv and may affect the top inv
            // e.g. shift-click, collect to cursor
            // In this case the slot input is not available and the target needs to be determined
            when (event.action) {
                InventoryAction.COLLECT_TO_CURSOR -> {

                }

                InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                    // Move from Bottom Inv to Top Inv
                    // The slotResult here is sitting in the bottom inventory, so no need to update it
                    if (newSlotStack != null) {
                        moveToOtherInventory(
                            newSlotStack,
                            event.view,
                            event.view.bottomInventory,
                            event.view.topInventory
                        ) { index, stack ->
                            getSlotInputFor(index, root)
                        }

                        // Apply calculated slot changes
                        event.currentItem = newSlotStack
                    }
                }

                else -> {
                    event.isCancelled = false
                }
            }
            isBusy = false
        }
    }

    private fun moveToOtherInventory(
        stackToMove: ItemStack,
        inventoryView: InventoryView,
        moveFrom: Inventory,
        moveTo: Inventory,
        topInvSlotProvider: (Int, ItemStack?) -> SlotInputModifierNode?,
    ) {
        var amount = stackToMove.amount
        val fromTopInv = inventoryView.topInventory == moveFrom

        val moveToContents = moveTo.storageContents

        for (index in moveToContents.size - 1 downTo 0) {
            if (amount <= 0) {
                break
            }

            val stack = moveToContents[index]
            val slot = if (fromTopInv) { null } else { topInvSlotProvider(index, stack) }

            if (
                stack == null ||
                stack.type == Material.AIR ||
                !stackToMove.isSimilar(stack) ||
                (!fromTopInv && slot == null)
            ) {
                continue
            }

            val canAdd = (stack.maxStackSize - stack.amount).coerceAtMost(amount)
            if (canAdd <= 0) {
                continue
            }

            // Apply changes
            amount -= canAdd
            stack.amount += canAdd
            slot?.onValueChange(stack.snapshot())
        }

        if (amount > 0) {
            // There are still items left to move, look for empty slots
            for (index in 0 until moveToContents.size) {
                if (amount <= 0) {
                    break
                }
                val stack = moveToContents[index]
                if (stack != null && stack.type != Material.AIR) {
                    continue
                }
                val slot = if (fromTopInv) { null } else { topInvSlotProvider(index, stack) }
                if (!fromTopInv && slot == null) {
                    continue
                }

                // Apply new stack with the maximum amount possible
                val newStack = stackToMove.clone()
                newStack.amount = amount.coerceAtMost(stackToMove.maxStackSize)
                amount -= newStack.amount
                if (fromTopInv) {
                    moveTo.setItem(index, newStack) // Storage contents always comes first in player inv (start at index 0)
                } else {
                    slot?.onValueChange(newStack.snapshot())
                }
            }
        }

        // Update clicked stack
        stackToMove.amount = amount
    }

    private fun collectToCursor(
        cursor: ItemStack,
        inventoryView: InventoryView,
        topInvPredicate: (Int, ItemStack) -> SlotInputModifierNode?,
    ) {
        for ((index, stack) in inventoryView.topInventory.contents.withIndex()) {
            if (stack == null || stack.type == Material.AIR) {
                continue
            }
            var remainingAmount = cursor.maxStackSize - cursor.amount
            if (remainingAmount <= 0) {
                return
            }
            val slot = topInvPredicate(index, stack)
            if (slot != null && cursor.isSimilar(stack)) {
                if (remainingAmount > stack.amount) {
                    remainingAmount = stack.amount
                }
                stack.amount -= remainingAmount
                cursor.amount += remainingAmount
                slot.onValueChange(stack.snapshot())
            }
        }
        for (stack in inventoryView.bottomInventory.contents) {
            if (stack == null || stack.type == Material.AIR) {
                continue
            }
            var remainingAmount = cursor.maxStackSize - cursor.amount
            if (remainingAmount <= 0) {
                return
            }
            if (cursor.isSimilar(stack)) {
                if (remainingAmount > stack.amount) {
                    remainingAmount = stack.amount
                }
                stack.amount -= remainingAmount
                cursor.amount += remainingAmount
            }
        }
    }

    override fun onDrag(context: InventoryUIInteractionContext) {
        val event = context.event as? InventoryDragEvent ?: return
        val view = runtime.views[event.whoClicked.uniqueId]
        val root = (view as? ViewImpl)?.root ?: return
        val topInvSize = view.properties.dimensions.inventorySize

        // TODO: WIP
        // Go through each slot that is affected, and call them separately
        // (Sponge allows us to invalidate single slot transactions of the drag event! Keep the same API behaviour on Spigot, but cancel the entire event!)
        for (rawSlot in event.rawSlots) {
            if (rawSlot < topInvSize) {
                // Slot is in top inventory
                val slotIndex = event.view.convertSlot(rawSlot)
                val slot = getSlotInputFor(slotIndex, root)
                if (slot == null) {
                    event.isCancelled = true // Cancel the entire event if only one fails!
                    return
                }
            }
        }
        if (!event.isCancelled) {
            for ((rawSlot, stack) in event.newItems) {
                if (rawSlot < topInvSize) {
                    val slotIndex = event.view.convertSlot(rawSlot)
                    val slot = getSlotInputFor(slotIndex, root)
                    if (slot != null) {
                        slot.onValueChange(stack.snapshot())
                    }
                }
            }
        }
    }

}