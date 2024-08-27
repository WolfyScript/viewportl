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

package org.example.viewportl.guis

import com.wolfyscript.scafall.data.ItemStackDataKeys
import com.wolfyscript.scafall.deserialize
import com.wolfyscript.scafall.eval.value_provider.provider
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.components.match
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import java.util.function.Consumer

class StackEditorExampleKotlin {

    companion object {

        class StackEditorStore {
            private var stack: ItemStack? = null

            fun setStack(stack: ItemStack?) {
                this.stack = stack
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

        fun registerStackEditor(manager: GuiAPIManager) {
            manager.registerGui("stack_editor") {
                /*
                 * This whole construction is only called upon the initiation and creates a reactivity graph
                 * from the signals and effects used and only updates the necessary parts at runtime.
                 */
                size = 9 * 6
                val optionsPos = PropertyPosition.slot(18)

                routes {
                    route({}, {
                        val stackToEdit = createSignal(StackEditorStore())
                        val selectedTab = createSignal(Tab.NONE)

                        slot("stack_slot") {
                            styles {
                                position = PropertyPosition.slot(4)
                            }

                            onValueChange = Consumer { v ->
                                stackToEdit.update {
                                    it.setStack(v)
                                    return@update it
                                }
                            }
                            value {
                                stackToEdit.get()?.getStack()
                            }
                        }
                        // Tab selectors
                        button("display_name_tab_selector") {
                            styles {
                                position = PropertyPosition.slot(1)
                            }
                            icon {
                                stack("name_tag") {
                                    name = "<gold><b>Edit Display Name".provider()
                                }
                            }

                            onClick {
                                selectedTab.set(Tab.DISPLAY_NAME)
                            }
                        }
                        button("lore_tab_selector") {
                            styles {
                                position = PropertyPosition.slot(2)
                            }
                            icon {
                                stack("book") {
                                    name = "<gold><b>Edit Lore".provider()
                                }
                            }

                            onClick {
                                selectedTab.set(Tab.LORE)
                            }
                        }

                        // The whenever statement only updates the 'then' or 'orElse' sections when absolutely necessary!
                        // The sections are only called when the condition changes.
                        whenever {
                            val stack = stackToEdit.get()?.getStack()
                            stack == null || stack.item.value == "air"
                        } then {
                            // Called once whenever the condition changes from false to true
                            // Empty component! Perhaps add a note that the item is missing!
                        } orElse {
                            styles {
                                position = optionsPos
                            }
                            // Called once whenever the condition changes from true to false
                            // Whenever the stack is available we can show the selected tab
                            match({ selectedTab.get() }) {
                                case({ this?.equals(Tab.DISPLAY_NAME) ?: false }) {
                                    styles {
                                        position = optionsPos
                                    }
                                    displayNameTab(this@registerGui, stackToEdit)
                                }
                                case({ this?.equals(Tab.LORE) ?: false }) {
                                    styles {
                                        position = optionsPos
                                    }
                                    group("lore_tab") {
                                        button("edit_lore") {
                                            styles {
                                                position = PropertyPosition.slot(21)
                                            }
                                            icon {
                                                stack("writable_book") {
                                                    name = "<green><b>Edit Lore".provider()
                                                }
                                            }
                                        }
                                        button("clear_lore") {
                                            styles {
                                                position = PropertyPosition.slot(23)
                                            }
                                            icon {
                                                stack("red_concrete") {
                                                    name = "<red><b>Clear Lore".provider()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }

        private fun ComponentGroup.displayNameTab(window: Window, stackToEdit: ReadWriteSignal<StackEditorStore>) {
            group("display_name_tab") {
                styles {
                    position = PropertyPosition.slot(9)
                }
                button("set_display_name") {
                    styles {
                        position = PropertyPosition.slot(21)
                    }
                    icon {
                        stack("green_concrete") {
                            name = "<green><b>Set Display Name".provider()
                        }
                    }

                    onClick {
                        window.onTextInput { _, _, s, _ ->
                            stackToEdit.update { store ->
                                store.getStack()?.data()
                                    ?.set(
                                        ItemStackDataKeys.CUSTOM_NAME,
                                        s.deserialize()
                                    )
                                store
                            }
                            true
                        }
                    }
                }
                button("reset_display_name") {
                    styles {
                        position = PropertyPosition.slot(23)
                    }
                    icon {
                        stack("red_concrete") {
                            name = "<red><b>Reset Display Name".provider()
                        }
                    }
                    onClick {
                        stackToEdit.update { store ->
                            store.getStack()?.data()?.remove(ItemStackDataKeys.CUSTOM_NAME)
                            store
                        }
                    }
                }
            }
        }
    }
}
