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

import com.wolfyscript.scafall.deserialize
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.elements.RouterScope
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

/**
 * A Counter GUI Example, that allows the viewer:
 *
 *  * to click one Button to increase the count,
 *  * another button to decrease the count,
 *  * and a button to reset the count to 0.
 *
 *
 *
 * The reset Button is only displayed, when the count is not 0.<br></br>
 * Whenever the GUI is open the count is increased periodically every second, without requiring any input.
 * <br></br>
 * The count is displayed in the title of the Inventory and in the item name of the button in the middle.
 * Those parts are automatically updated when the count changes.
 */
class CounterExampleKotlin {

    companion object {

        fun registerExampleCounter(manager: GuiAPIManager) {
            manager.registerGui("example_counter") {
                /**
                 * Everything in this section is called **async** and only once.
                 * It constructs the component tree and reactive graph as specified.
                 **/
                size = 9 * 3

                router {
                    route({ }) {
                        mainMenu(this@registerGui, this@router)
                    }
                    route({ this / "main" }) {
                        counterMenu(this@registerGui, this@router)
                    }
                }

            }
        }

        private fun ComponentScope.mainMenu(window: WindowScope, routerScope: RouterScope) {
            window.title { // Update the title with the Count
                Component.text("Counter Main Menu").decorate(TextDecoration.BOLD)
            }

            button(
                icon = {
                    stack("green_concrete") {
                        data().set(it.customName, "<green><b>Open Counter".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(13) },
                onClick = {
                    routerScope.openSubRoute { this / "main" }
                }
            )
        }

        /**
         * This function setups the counter menu
         */
        private fun ComponentScope.counterMenu(window: WindowScope, routerScope: RouterScope) {
            // Use signals to notify components to update when the value changes
            var count: Int by createSignal(0)

            window.title { // Update the title with the Count
                Component.text("Counter: ").decorate(TextDecoration.BOLD)
                    .append(Component.text(count).color(NamedTextColor.BLUE))
            }

            // Update the count periodically (every second)
            interval(20) {
                count += 1
            }

            button(
                icon = {
                    stack("barrier") {
                        data().set(it.customName, "<red><b>Back".deserialize())
                    }
                },
                styles = {
                    position = PropertyPosition.slot(0)
                },
                onClick = { routerScope.openPrevious() }
            )

            button(
                styles = {
                    position = PropertyPosition.slot(4)
                },
                icon = {
                    stack("green_concrete") {
                        data().set(it.customName, "<green><b>Count Up".deserialize())
                    }
                },
                onClick = {
                    count += 1
                }
            )

            button(
                styles = {
                    position = PropertyPosition.slot(13)
                },
                icon = {
                    stack("redstone") {
                        data().set(it.customName, "<!italic>Clicked <b><count></b> times!".deserialize(tagResolver = Placeholder.parsed("count", count.toString())))
                    }
                },
            )

            button(
                styles = {
                    position = PropertyPosition.slot(22)
                },
                icon = {
                    stack("red_concrete") {
                        data().set(it.customName, "<red><b>Count Down".deserialize())
                    }
                },
                onClick = { count -= 1 }
            )

            // Sometimes we want to render components dependent on signals
            val render by createMemo<Boolean>(false) { count != 0 }
            show(
                condition = { render },
                fallback = { }
            ) {
                group(
                    styles = { position = PropertyPosition.slot(10) },
                ) {
                    button(
                        styles = {
                            position = PropertyPosition.slot(10)
                        },
                        icon = {
                            stack("tnt") {
                                data().set(it.customName, "<b><red>Reset Clicks!".deserialize())
                            }
                        },
                        onClick = {
                            count =
                                0 // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                        },
                        sound = Sound.sound(
                            Key.key("minecraft:entity.dragon_fireball.explode"),
                            Sound.Source.MASTER,
                            0.25f,
                            1f
                        )
                    )
                }
            }

        }
    }
}