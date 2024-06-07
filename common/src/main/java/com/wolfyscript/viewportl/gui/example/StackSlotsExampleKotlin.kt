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

package com.wolfyscript.viewportl.gui.example

import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer

fun registerStackSlotsExample(manager: GuiAPIManager) {
    manager.registerGui("stack_editor") {
        size = 9 * 1

        routes {
            route({}, {
                /*
                 This whole construction is only called upon the initiation and creates a reactivity graph
                 from the signals and effects used and only updates the necessary parts at runtime.
                 */

                val stacks = createSignal {
                    val wolfyUtils = this.wolfyUtils
                    mutableListOf<ItemStack>().apply {
                        for (i in 0 until 9) {
                            wolfyUtils.core.platform.items.createStackConfig(wolfyUtils, "air").constructItemStack()?.let {
                                this.add(it)
                            }
                        }
                    }
                }

                slot("stack_slot_0") {
                    properties {
                        position = PropertyPosition.slot(0)
                    }

                    onValueChange = Consumer { v ->
                        stacks.update {
                            val newStack = v ?: this@registerGui.wolfyUtils.core.platform.items.createStackConfig(wolfyUtils, "air").constructItemStack()
                            if (newStack != null) {
                                it[0] = newStack
                            }
                            it
                        }
                    }
                    value = stacks.get()?.get(0)
                }

            })
        }
    }
}
