/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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

package com.wolfyscript.utilities.gui.example

import com.wolfyscript.utilities.data.ItemStackDataKeys
import com.wolfyscript.utilities.eval.value_provider.provider
import com.wolfyscript.utilities.gui.GuiAPIManager
import com.wolfyscript.utilities.gui.interaction.InteractionResult
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.components.ComponentGroup
import com.wolfyscript.utilities.gui.components.match
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer

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
    manager.registerGui("stack_editor") {
        size = 9 * 6

        routes {
            route({}, {
                /*
                 This whole construction is only called upon the initiation and creates a reactivity graph
                 from the signals and effects used and only updates the necessary parts at runtime.
                 */

                val stackToEdit = createSignal { StackEditorStore() }
                val selectedTab = createSignal(Tab.NONE)

                slot("stack_slot") {
                    properties {
                        position = PropertyPosition.slot(4)
                    }

                    onClick = InteractionCallback { _, _ -> InteractionResult.cancel(false) }
                    onValueChange = Consumer { v ->
                        stackToEdit.update {
                            it.setStack(v)
                            it
                        }
                    }
                    value = stackToEdit.get()?.getStack()
                }
                // Tab selectors
                button("display_name_tab_selector") {
                    properties {
                        position = PropertyPosition.slot(1)
                    }
                    icon {
                        stack("name_tag") {
                            name = "<gold><b>Edit Display Name".provider()
                        }
                    }

                    onClick = InteractionCallback { _, _ ->
                        selectedTab.set(Tab.DISPLAY_NAME)
                        InteractionResult.cancel(true)
                    }
                }
                button("lore_tab_selector") {
                    properties {
                        position = PropertyPosition.slot(2)
                    }
                    icon {
                        stack("book") {
                            name = "<gold><b>Edit Lore".provider()
                        }
                    }

                    onClick = InteractionCallback { _, _ ->
                        selectedTab.set(Tab.LORE)
                        InteractionResult.cancel(true)
                    }
                }

                // The whenever statement only updates the 'then' or 'orElse' sections when absolutely necessary!
                // The sections are only called when the condition changes.
                whenever {
                    val stack = stackToEdit.get()?.getStack()
                    stack == null || stack.item == null || stack.item.key == "air"
                } then {
                    // Called once whenever the condition changes from false to true
                    // Empty component! Perhaps add a note that the item is missing!
                } orElse {
                    // Called once whenever the condition changes from true to false
                    // Whenever the stack is available we can show the selected tab
                    match({ selectedTab.get() }) {
                        case({ equals(Tab.DISPLAY_NAME) }) { displayNameTab(stackToEdit) }
                        case({ equals(Tab.LORE) }) {
                            group("lore_tab") {
                                button("edit_lore") {
                                    properties {
                                        position = PropertyPosition.slot(12)
                                    }
                                    icon {
                                        stack("writable_book") {
                                            name = "<green><b>Edit Lore".provider()
                                        }
                                    }

                                    onClick = InteractionCallback { _, _ -> InteractionResult.cancel(true) }
                                }
                                button("clear_lore") {
                                    properties {
                                        position = PropertyPosition.slot(14)
                                    }
                                    icon {
                                        stack("red_concrete") {
                                            name = "<red><b>Clear Lore".provider()
                                        }
                                    }

                                    onClick = InteractionCallback { _, _ -> InteractionResult.cancel(true) }
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}

fun ComponentGroup.displayNameTab(stackToEdit: Signal<StackEditorStore>) {
    group("display_name_tab") {
        button("set_display_name") {
            properties {
                position = PropertyPosition.slot(12)
            }
            icon {
                stack("green_concrete") {
                    name = "<green><b>Set Display Name".provider()
                }
            }

            onClick = InteractionCallback { runtime, _ ->
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
            properties {
                position = PropertyPosition.slot(14)
            }
            icon {
                stack("red_concrete") {
                    name = "<red><b>Reset Display Name".provider()
                }
            }

            onClick = InteractionCallback { _, _ ->
                stackToEdit.update { store ->
                    store?.getStack()?.data()?.remove(ItemStackDataKeys.CUSTOM_NAME)
                    store
                }
                InteractionResult.cancel(true)
            }
        }
    }
}
