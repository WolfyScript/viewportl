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

import com.wolfyscript.scafall.eval.value_provider.provider
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.components.RouterScope
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.text.Component

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
class NestedRoutingExampleKotlin {

    companion object {

        fun registerNestedRoutingExample(manager: GuiAPIManager) {
            manager.registerGui("nested_routing") {
                /**
                 * Everything in this section is called **async** and only once.
                 * It constructs the component tree and reactive graph as specified.
                 **/
                size = 9 * 6

                router {
                    route({ }) {
                        mainMenu(this@router)
                    }
                    route(
                        path = { this / "main" },
                        subRoutes = {
                            route({ this / "page1" }, {
                                button(
                                    icon = { stack("written_book") { name = "<aqua>Page 1".provider() } },
                                    styles = { position = PropertyPosition.slot(30) }
                                )
                            }, routeConfig = {})
                            route({ this / "page2" }, {
                                button(
                                    icon = { stack("written_book") { name = "<aqua>Page 2".provider() } },
                                    styles = { position = PropertyPosition.slot(30) }
                                )
                            }, {})
                            route({ this / "page3" }, {
                                button(
                                    icon = { stack("written_book") { name = "<aqua>Page 3".provider() } },
                                    styles = { position = PropertyPosition.slot(30) }
                                )
                            }, {})
                        },
                        view = {
                            var page: Int by createSignal(1)

                            title {
                                Component.text("Main View")
                            }

                            button(
                                icon = { stack("green_concrete") { name = "<green><b>Next Page".provider() } },
                                styles = { position = PropertyPosition.slot(0) },
                                onClick = {
                                    page = page++.coerceAtMost(3)
                                    openRoute { this / "main" / "page$page" }
                                }
                            )
                            button(
                                icon = { stack("red_concrete") { name = "<green><b>Previous Page".provider() } },
                                styles = { position = PropertyPosition.slot(45) },
                                onClick = {
                                    page = page++.coerceAtLeast(1)
                                    openRoute { this / "main" / "page$page" }
                                }
                            )

                            outlet()
                        }
                    )
                }

            }
        }

        private fun ComponentScope.mainMenu(routerScope: RouterScope) {
            button(
                icon = {
                    stack("green_concrete") {
                        name = "<green><b>Open Main View".provider()
                    }
                },
                styles = { position = PropertyPosition.slot(13) },
                onClick = {
                    routerScope.openSubRoute { this / "main" }
                }
            )
        }

    }
}