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

package org.example.viewportl.common.gui

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition

class StackSlots {

    companion object {

        fun register(manager: GuiAPIManager) {
            manager.registerGui("stack_grid") {
                size = 9 * 1

                router {
                    route({}) {
                        /*
                         This whole construction is only called upon the initiation and creates a reactivity graph
                         from the signals and effects used and only updates the necessary parts at runtime.
                         */

                        val stacks by createSignal {
                            mutableListOf<ItemStack>().apply {
                                for (i in 0 until 9) {
                                    add(runtime.viewportl.scafall.factories.itemsFactory.createStack(Key.minecraft("air")))
                                }
                            }
                        }

                        for (i in 0 until 9) {
                            slot (
                                value = { stacks[i] },
                                styles = {
                                    position = PropertyPosition.slot(i)
                                },
                                onValueChange = { v ->
                                    stacks[i] = v ?: runtime.viewportl.scafall.factories.itemsFactory.createStack(Key.minecraft("air"))
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}
