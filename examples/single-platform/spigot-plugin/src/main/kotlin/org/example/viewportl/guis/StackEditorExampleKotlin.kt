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
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition

class StackEditorExampleKotlin {

    companion object {

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

                router {
                    route({}) {
                        var stackToEdit: ItemStack? by createSignal(null)
                        var selectedTab: Tab by createSignal(Tab.NONE)

                        slot(
                            value = { stackToEdit },
                            styles = {
                                position = PropertyPosition.slot(4)
                            },
                            onValueChange = { v ->
                                stackToEdit = v
                            }
                        )
                        // Tab selectors
                        button(
                            styles = {
                                position = PropertyPosition.slot(1)
                            },
                            icon = {
                                stack("name_tag") {
                                    name = "<gold><b>Edit Display Name".provider()
                                }
                            },
                            onClick = { selectedTab = Tab.DISPLAY_NAME }
                        )
                        button(
                            styles = { position = PropertyPosition.slot(2) },
                            icon = {
                                stack("book") {
                                    name = "<gold><b>Edit Lore".provider()
                                }
                            },
                            onClick = { selectedTab = Tab.LORE }
                        )

                        val isAir by createMemo<Boolean>(true) {
                            val stack = stackToEdit
                            stack == null || stack.item.value == "air"
                        }

                        show(condition = { !isAir }, fallback = {
                            // Called once whenever the condition changes from false to true (Item becomes air)
                            // Empty component! Perhaps add a note that the item is missing!
                        }) {
                            group(styles = { position = optionsPos }) {
                                val memoizedTab by createMemo<Tab>(Tab.NONE) { selectedTab }
                                createEffect {
                                    when (memoizedTab) {
                                        Tab.DISPLAY_NAME -> displayNameTab(this@registerGui, { stackToEdit })
                                        Tab.LORE -> {
                                            button(
                                                styles = {
                                                    position = PropertyPosition.slot(21)
                                                },
                                                icon = {
                                                    stack("writable_book") {
                                                        name = "<green><b>Edit Lore".provider()
                                                    }
                                                }
                                            )
                                            button(
                                                styles = {
                                                    position = PropertyPosition.slot(23)
                                                },
                                                icon = {
                                                    stack("red_concrete") {
                                                        name = "<red><b>Clear Lore".provider()
                                                    }
                                                }
                                            )
                                        }

                                        Tab.NONE -> {
                                            // render nothing!
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun ComponentScope.displayNameTab(
            window: WindowScope,
            stackToEdit: () -> ItemStack?
        ) {
            group(
                styles = {
                    position = PropertyPosition.slot(9)
                },
            ) {
                button(
                    styles = {
                        position = PropertyPosition.slot(21)
                    },
                    icon = {
                        stack("green_concrete") {
                            name = "<green><b>Set Display Name".provider()
                        }
                    },
                    onClick = {
                        window.onTextInput { _, _, s, _ ->
                            stackToEdit()?.data()?.set(ItemStackDataKeys.CUSTOM_NAME, s.deserialize())
                            true
                        }
                    }
                )
                button(
                    styles = {
                        position = PropertyPosition.slot(23)
                    },
                    icon = {
                        stack("red_concrete") {
                            name = "<red><b>Reset Display Name".provider()
                        }
                    },
                    onClick = { stackToEdit()?.data()?.remove(ItemStackDataKeys.CUSTOM_NAME) }
                )
            }
        }
    }
}
