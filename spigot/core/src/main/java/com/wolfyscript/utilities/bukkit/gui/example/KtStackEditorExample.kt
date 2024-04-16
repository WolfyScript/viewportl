package com.wolfyscript.utilities.bukkit.gui.example

import com.wolfyscript.utilities.data.ItemStackDataKeys
import com.wolfyscript.utilities.gui.GuiAPIManager
import com.wolfyscript.utilities.gui.InteractionResult
import com.wolfyscript.utilities.gui.components.ComponentGroupBuilder
import com.wolfyscript.utilities.gui.components.match
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.platform.adapters.ItemStack

class StackEditorStore {
    private var stack: ItemStack? = null

    fun setStack(stack: ItemStack?) {
        this.stack = stack;
    }

    fun getStack(): ItemStack? {
        return this.stack
    }
}

private enum class Tab {
    NONE,
    DISPLAY_NAME,
    LORE,
}

fun register(manager: GuiAPIManager) {
    manager.registerGuiFromFiles("stack_editor") {
        window {
            /*
             This whole construction is only called upon the initiation and creates a reactivity graph
             from the signals and effects used and only updates the necessary parts at runtime.
             */

            size(9 * 6)

            // Persistent data stores
            val stackToEdit = createSignal { StackEditorStore() }
            // Weak data signals
            val selectedTab = createSignal(Tab.NONE)

            whenever {
                val stack = stackToEdit.get()?.getStack()
                stack == null || stack.item == null || stack.item.key == "air"
            } then {
                // Empty component! Perhaps add a note that the item is missing!
            } orElse {
                // Whenever the stack is available we can show the selected tab
                match({ selectedTab.get() }) {
                    case({ equals(Tab.DISPLAY_NAME) }) { displayNameTab(stackToEdit) }
                    case({ equals(Tab.LORE) }) {
                        group("lore_tab") {
                            button("edit_lore") {
                                interact { _, _ -> InteractionResult.cancel(true) }
                            }
                            button("clear_lore") {
                                interact { _, _ -> InteractionResult.cancel(true) }
                            }
                        }
                    }
                }
            }
            slot("stack_slot") {
                interact { _, _ -> InteractionResult.cancel(false) }
                onValueChange { v ->
                    stackToEdit.update {
                        it.setStack(v)
                        it
                    }
                }
                value { stackToEdit.get()?.getStack() }
            }
            button("display_name_tab_selector") {
                interact { _, _ ->
                    selectedTab.set(Tab.DISPLAY_NAME)
                    InteractionResult.cancel(true)
                }
            }
            button("lore_tab_selector") {
                interact { _, _ ->
                    selectedTab.set(Tab.LORE)
                    InteractionResult.cancel(true)
                }
            }
        }
    }
}

fun ComponentGroupBuilder.displayNameTab(stackToEdit: Signal<StackEditorStore>) {
    group("display_name_tab") {
        button("set_display_name") {
            interact { runtime, _ ->
                runtime.setTextInputCallback { _, _, s, _ ->
                    stackToEdit.update { store ->
                        store?.getStack()?.data()
                            ?.set(ItemStackDataKeys.CUSTOM_NAME, runtime.wolfyUtils.chat.miniMessage.deserialize(s))
                        store
                    }
                    true
                }
                InteractionResult.cancel(true)
            }
        }
        button("reset_display_name") {
            interact { _, _ ->
                stackToEdit.update { store ->
                    store?.getStack()?.data()?.remove(ItemStackDataKeys.CUSTOM_NAME)
                    store
                }
                InteractionResult.cancel(true)
            }
        }
    }
}
