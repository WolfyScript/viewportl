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

package com.wolfyscript.viewportl.spigot.gui.inventoryui.interaction

import com.wolfyscript.scafall.spigot.api.wrappers.wrap
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.UIInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class UIInteractionHandler(private val runtime: ViewRuntimeImpl) : UIInteractionHandler(runtime) {

    companion object {

        init {
            registerComponentInteractionHandler(ButtonImpl::class.java, InventoryButtonInteractionHandler())
            registerComponentInteractionHandler(SlotImpl::class.java, InventoryStackSlotInteractionHandler())
        }

    }

    override fun onClick(details: ClickInteractionDetails) {
        details as ClickInteractionDetailsImpl

        val event = details.clickEvent
        val clickedTopInv = event.clickedInventory == event.view.topInventory

        val originalCursor = event.cursor
        val originalSlotStack = event.currentItem

        var cursor: ItemStack? = originalCursor.clone()
        var slotResult: ItemStack? = originalSlotStack?.clone()

        testMainClickTransaction(details)

        if (!details.valid) {
            return
        }

        val action = event.action
        when (action) {
            // place/collect stack
            InventoryAction.PLACE_ONE -> {
                if (slotResult == null) {
                    slotResult = cursor?.let {
                        val copy = it.clone()
                        copy.amount = 1
                        copy
                    }
                    cursor?.apply {
                        amount -= 1
                    }
                } else if (cursor != null && cursor.isSimilar(slotResult)) {
                    slotResult.amount += 1
                    cursor.amount -= 1
                }
            }

            InventoryAction.PLACE_ALL -> {
                if (slotResult == null) {
                    slotResult = cursor?.clone()
                } else {
                    slotResult.amount += cursor?.amount ?: 0
                }
                cursor = null
            }

            InventoryAction.PLACE_SOME -> {
                if (slotResult != null) {
                    val amountToAdd = cursor?.amount ?: 0

                    val newSlotAmount = (slotResult.amount + amountToAdd).coerceAtMost(slotResult.maxStackSize)
                    val newCursorAmount = newSlotAmount - slotResult.amount

                    slotResult.amount = newSlotAmount
                    cursor?.amount = newCursorAmount
                }
            }

            InventoryAction.PICKUP_ONE -> {
                if (cursor == null || cursor.type == Material.AIR) {
                    cursor = slotResult?.clone()
                    slotResult = null
                } else {
                    cursor.amount += 1
                    slotResult?.apply {
                        amount -= 1
                    }
                }
            }

            InventoryAction.PICKUP_SOME -> {
                /*
                 * Doesn't seem to be called ever...
                 * apparently it is called when a full stack is picked up, but with items left in the inventory?!
                 */
                if (cursor == null || cursor.type == Material.AIR) {
                    cursor = slotResult
                } else {
                    cursor.amount += slotResult?.amount ?: 0
                }
            }

            InventoryAction.PICKUP_HALF -> {
                val amount = slotResult?.amount ?: 0
                val splitAmount = amount.div(2)

                if (cursor == null || cursor.type == Material.AIR) {
                    cursor = slotResult?.clone()
                }
                cursor?.amount = amount - splitAmount
                slotResult?.amount = splitAmount
            }

            InventoryAction.PICKUP_ALL -> {
                if (cursor == null || cursor.type == Material.AIR) {
                    cursor = slotResult?.clone()
                } else {
                    // IDK if this can actually happen, but just in case it does
                    cursor.amount += slotResult?.amount ?: 0
                }
                slotResult = null
            }

            InventoryAction.COLLECT_TO_CURSOR -> {
                details.clickEvent.isCancelled = true

                if (details.valid) { // Only continue to collect items when main component allows it
                    // Clear the slot, it was moved to the cursor
                    slotResult = null
                    details.callSlotValueUpdate(event.rawSlot, null)

                    if (cursor != null) {
                        // calculate the slots that should be collected. Skip Buttons and other components that invalidate the transaction
                        collectToCursor(cursor, event.view, { index, stack ->
                            testChildClickTransaction(index, event.view, details)
                        }) { rawSlot, stack ->
                            details.callSlotValueUpdate(rawSlot, stack?.wrap())
                        }
                    }

                    // Apply calculated slot changes
                    details.clickEvent.view.setCursor(cursor)
                    details.clickEvent.currentItem = null
                }
                return
            }

            InventoryAction.SWAP_WITH_CURSOR -> {
                val originalSlot = slotResult?.clone()
                slotResult = cursor?.clone()
                cursor = originalSlot
            }

            // Moving items between inventories using shift clicking
            InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                details.clickEvent.isCancelled = true

                if (details.valid) { // Only move items when parent transaction is allowed
                    if (clickedTopInv) {
                        // Move from Top Inv to Bottom Inv
                        // So no need to test and update child transactions in bottom inv
                        if (slotResult != null) {
                            moveToOtherInventory(
                                slotResult,
                                event.view,
                                event.view.topInventory,
                                event.view.bottomInventory,
                                { _, _ -> true }) { _, _ -> }
                            details.callSlotValueUpdate(event.rawSlot, slotResult.wrap())

                            // Apply calculated slot changes
                            details.clickEvent.currentItem = slotResult
                        }
                    } else {
                        // Move from Bottom Inv to Top Inv
                        // The slotResult here is sitting in the bottom inventory, so no need to update it
                        if (slotResult != null) {
                            moveToOtherInventory(
                                slotResult,
                                event.view,
                                event.view.bottomInventory,
                                event.view.topInventory,
                                { index, stack ->
                                    testChildClickTransaction(index, event.view, details)
                                }) { rawSlot, stack ->
                                details.callSlotValueUpdate(rawSlot, stack?.wrap())
                            }

                            // Apply calculated slot changes
                            details.clickEvent.currentItem = slotResult
                        }
                    }
                }
                return
            }

            // Dropping the item out of the inventory using the drop keybind (default: Q)
            InventoryAction.DROP_ONE_SLOT -> {
                slotResult?.apply {
                    amount -= 1
                }
            }

            InventoryAction.DROP_ALL_SLOT -> {
                slotResult = null
            }

            // Hotbar swapping using the number keys
            InventoryAction.HOTBAR_SWAP, InventoryAction.HOTBAR_MOVE_AND_READD -> {
                if (details.valid) {
                    val clickedStack = slotResult?.clone()
                    slotResult = event.whoClicked.inventory.getItem(event.hotbarButton)
                    event.whoClicked.inventory.setItem(event.hotbarButton, clickedStack)
                }
            }

            else -> {
                /* Click action not handled */
                return
            }
        }

        details.clickEvent.isCancelled = true

        if (details.valid) {
            // Apply calculated slot changes
            details.clickEvent.view.setCursor(cursor)
            details.clickEvent.currentItem = slotResult

            // Notify listeners (e.g. components)
            details.callSlotValueUpdate(event.rawSlot, slotResult?.wrap())
        }
    }

    private fun testMainClickTransaction(details: ClickInteractionDetailsImpl) {
        if (details.clickEvent.clickedInventory == details.clickEvent.view.bottomInventory) {
            details.validate() // Allow any bottom inventory interaction
            return
        }
        // Top inventory clicked
        val node = slotNodes[details.slot]?.let { runtime.model.getNode(it) }
        if (node == null) {
            details.invalidate()
            return
        }
        val transaction = ClickTransactionImpl(
            details.clickType,
            details.slot,
            details.rawSlot,
            details.isShift,
            details.isPrimary,
            details.isSecondary,
            details.hotbarButton
        )
        // Invalidate the action by default
        details.invalidate()
        transaction.invalidate()

        val component = node.nativeComponent
        getComponentInteractionHandler(component.javaClass)?.onClick(runtime, component, details, transaction)

        if (transaction.valid) {
            details.validate() // Validate it as wished by the transaction
        }
    }

    private fun testChildClickTransaction(
        rawSlot: Int,
        inventoryView: InventoryView,
        details: ClickInteractionDetails
    ): Boolean {
        if (rawSlot >= inventoryView.topInventory.size) {
            return true
        }
        val slot = inventoryView.convertSlot(rawSlot)
        val node = slotNodes[slot]?.let { runtime.model.getNode(it) }
        if (node != null) {
            val transaction = ClickTransactionImpl(
                details.clickType,
                slot,
                rawSlot,
                details.isShift,
                details.isPrimary,
                details.isSecondary,
                details.hotbarButton
            )
            transaction.invalidate() // Invalidate the action by default

            val component = node.nativeComponent
            getComponentInteractionHandler(component.javaClass)?.onClick(runtime, component, details, transaction)

            return transaction.valid
        }
        return false
    }

    private fun moveToOtherInventory(
        stackToMove: ItemStack,
        inventoryView: InventoryView,
        moveFrom: Inventory,
        moveTo: Inventory,
        stackPredicate: (Int, ItemStack?) -> Boolean,
        onStackUpdated: (Int, ItemStack?) -> Unit
    ) {
        var amount = stackToMove.amount
        val slotOffset = if (inventoryView.topInventory == moveTo) {
            0
        } else {
            moveFrom.size
        }
        val storageSize =
            moveTo.storageContents.size // Do not include armor, off-hand, etc. to get the proper slot position

        for (i in (storageSize - 1) downTo 0) {
            if (amount <= 0) {
                break
            }
            val rawSlot = slotOffset + i
            val slot = inventoryView.convertSlot(rawSlot)

            val stack = moveTo.getItem(slot)
            if (stack == null || stack.type == Material.AIR) {
                continue
            }
            if (!stackToMove.isSimilar(stack) || !stackPredicate(rawSlot, stack)) {
                continue
            }

            val canAdd = (stack.maxStackSize - stack.amount).coerceAtMost(amount)
            if (canAdd <= 0) {
                continue
            }

            // Apply changes
            amount -= canAdd
            stack.amount += canAdd
            onStackUpdated(rawSlot, stack)
        }
        if (amount > 0) {
            // There are still items left to move, look for empty slots
            for (i in (storageSize - 1) downTo 0) {
                if (amount <= 0) {
                    break
                }
                val rawSlot = slotOffset + i
                val slot = inventoryView.convertSlot(rawSlot)

                val stack = moveTo.getItem(slot)
                if (stack != null && stack.type != Material.AIR) {
                    continue
                }
                if (!stackPredicate(rawSlot, stack)) {
                    continue
                }

                // Apply new stack with the maximum amount possible
                val newStack = stackToMove.clone()
                newStack.amount = amount.coerceAtMost(stackToMove.maxStackSize)
                amount -= newStack.amount
                moveTo.setItem(slot, newStack)
                onStackUpdated(rawSlot, newStack)
            }
        }

        // Update clicked stack
        stackToMove.amount = amount
    }

    private fun collectToCursor(
        cursor: ItemStack,
        inventoryView: InventoryView,
        stackPredicate: (Int, ItemStack) -> Boolean,
        onStackUpdated: (Int, ItemStack?) -> Unit
    ) {
        for (i in 0 until inventoryView.countSlots()) {
            val stack = inventoryView.getItem(i) ?: continue
            var canAdd = cursor.maxStackSize - cursor.amount
            if (canAdd <= 0) {
                return
            }
            if (cursor.isSimilar(stack) && stackPredicate(i, stack)) {
                if (canAdd > stack.amount) {
                    canAdd = stack.amount
                }
                stack.amount -= canAdd
                cursor.amount += canAdd
                onStackUpdated(i, stack)
            }
        }
    }

    override fun onDrag(details: DragInteractionDetails) {
        val topInvSize = runtime.getCurrentMenu().map { it.size ?: 0 }.orElse(0)

        // Go through each slot that is affected, and call them separately
        // (Sponge allows us to invalidate single slot transactions of the drag event! Keep the same API behaviour on Spigot, but cancel the entire event!)
        for (rawSlot in details.rawSlots) {
            if (rawSlot < topInvSize) {
                // Slot is in top inventory
                val node = slotNodes[rawSlot]?.let { runtime.model.getNode(it) }

                if (node == null) {
                    details.invalidate() // Cancel the entire event if only one fails!
                    return // TODO: Should we skip the next slots? On Spigot that would make sense, but on Sponge the next slots may be valid!
                }

                val transaction = DragTransactionImpl(
                    rawSlot,
                    details.inventorySlots,
                    details.rawSlots,
                    details.type
                )

                val component = node.nativeComponent
                getComponentInteractionHandler(component.javaClass)?.onDrag(runtime, component, details, transaction)

                if (!transaction.valid) {
                    details.invalidate() // Again, cancel the entire event!
                }

            }
        }
    }

}