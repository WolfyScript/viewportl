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

import com.wolfyscript.utilities.eval.value_provider.provider
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import com.wolfyscript.viewportl.gui.router.Router
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
                resourcePath = "com/wolfyscript/utilities/gui/example/example_counter"
                size = 9 * 3

                routes {
                    route({ }, { counterMainMenu(this@registerGui, this@routes) })
                    route({ this / "main" }, { counter(this@registerGui, this@routes) })
                }
            }
        }

        private fun ComponentGroup.counterMainMenu(window: Window, router: Router) {
            window.title { // Update the title with the Count
                Component.text("Counter Main Menu").decorate(TextDecoration.BOLD)
            }

            button("open") {
                properties {
                    position = PropertyPosition.slot(13)
                }
                icon {
                    stack("green_concrete") {
                        name = "<green><b>Open Counter".provider()
                    }
                }

                onClick {
                    router.openSubRoute { this / "main" }
                }
            }
        }

        private fun ComponentGroup.counter(window: Window, router: Router) {
            //configuredBy("main_menu.conf")
            // Called when the path matches, but only once, when the route was changed

            // Use signals that provide a simple value storage & synchronisation.
            val count = window.createSignal(0)
            count.tagName("count")

            window.title { // Update the title with the Count
                Component.text("Counter: ").decorate(TextDecoration.BOLD)
                    .append(Component.text(count.get() ?: 0).color(NamedTextColor.BLUE))
            }

            // Update the count periodically (every second)
            interval(20) {
                count.update { value -> value + 1 }
            }

            button("back") {
                properties {
                    position = PropertyPosition.slot(0)
                }
                icon {
                    stack("barrier") {
                        name = "<red><b>Back".provider()
                    }
                }

                onClick {
                    router.openPrevious()
                }
            }

            button("count_up") {
                properties {
                    position = PropertyPosition.slot(4)
                }
                icon {
                    stack("green_concrete") {
                        name = "<green><b>Count Up".provider()
                    }
                }

                onClick {
                    count.update { old -> old + 1 }
                }
            }

            button("counter") {
                properties {
                    position = PropertyPosition.slot(13)
                }
                icon {
                    stack("redstone") {
                        name = "<!italic>Clicked <b><count></b> times!".provider()
                    }
                    resolvers {
                        Placeholder.parsed("count", (count.get() ?: 0).toString())
                    }
                }
            }

            button("count_down") {
                properties {
                    position = PropertyPosition.slot(22)
                }
                icon {
                    stack("red_concrete") {
                        name = "<red><b>Count Down".provider()
                    }
                }

                onClick {
                    count.update { old -> old - 1 }
                }
            }
            // Sometimes we want to render components dependent on signals
            whenever { count.get() != 0 } then {
                properties {
                    position = PropertyPosition.slot(10)
                }
                // This section is run just once up on the initial construction too, not when the condition changes
                button("reset") {
                    properties {
                        position = PropertyPosition.slot(10)
                    }
                    icon {
                        stack("tnt") {
                            name = "<b><red>Reset Clicks!".provider()
                        }
                    }

                    onClick {
                        count.set(0) // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                    }
                    sound = Sound.sound(
                        Key.key("minecraft:entity.dragon_fireball.explode"),
                        Sound.Source.MASTER,
                        0.25f,
                        1f
                    )
                }
            }
        }
    }
}