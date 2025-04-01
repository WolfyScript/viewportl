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

import com.wolfyscript.scafall.data.ItemStackDataKeys
import com.wolfyscript.scafall.deserialize
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.elements.RouterScope
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.text.Component

/**
 * A GUI Example, that shows how nested routing is possible.
 *
 * A GUI can have multiple routers nested into each other.
 * Whenever the path of a router changes, the router clears all the child components.
 * Therefor, components that are added outside a Route View are not reset and kept between path changes.
 */
class NestedRoutingExampleKotlin {

    companion object {

        fun register(manager: GuiAPIManager) {
            manager.registerGui("nested_routing") {
                size = 9 * 6

                router {
                    // Note: Routes (and sub-routes) are constructed at setup. It is not possible to create new routes at runtime!
                    route({ }) {
                        entry(this@router)
                    }
                    route({ this / "main" }) {
                        mainView(this@registerGui, this@router)
                    }
                }
            }
        }

        private fun ComponentScope.mainView(window: WindowScope, mainRouter: RouterScope) {
            var page: Int by createSignal(1)

            window.title {
                Component.text("Main View")
            }

            button(
                icon = {
                    stack("barrier") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<red><b>Close".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(0) },
                onClick = { mainRouter.openPrevious() }
            )

            router {
                // Creates components in the outer component scope, while having access to the router scope
                button(
                    icon = {
                        stack("green_concrete") {
                            set(ItemStackDataKeys.CUSTOM_NAME, "<green><b>Next Page".deserialize())
                        }
                    },
                    styles = { position = PropertyPosition.slot(53) },
                    onClick = {
                        page = (++page).coerceAtMost(3)
                        openRoute { this / "page$page" }
                    }
                )
                button(
                    icon = {
                        stack("red_concrete") {
                            set(ItemStackDataKeys.CUSTOM_NAME, "<green><b>Previous Page".deserialize())
                        }
                    },
                    styles = { position = PropertyPosition.slot(45) },
                    onClick = {
                        page = (--page).coerceAtLeast(1)
                        openRoute { this / "page$page" }
                    }
                )

                // Setup sub routes.
                initialPath { this / "page1" }
                route({ this / "page1" }) { page1(this@router) }
                route({ this / "page2" }) { page2(this@router) }
                route({ this / "page3" }) { page3(this@router) }
            }
        }

        private fun ComponentScope.entry(routerScope: RouterScope) {
            button(
                icon = {
                    stack("green_concrete") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<green><b>Open Main View".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(13) },
                onClick = {
                    routerScope.openSubRoute { this / "main" }
                }
            )
        }

        private fun ComponentScope.page1(router: RouterScope) {
            button(
                icon = {
                    stack("paper") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<aqua>Page 1".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(31) }
            )
        }

        private fun ComponentScope.page2(router: RouterScope) {
            button(
                icon = {
                    stack("written_book") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<aqua>Page 2".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(31) }
            )
        }

        private fun ComponentScope.page3(router: RouterScope) {
            button(
                icon = {
                    stack("name_tag") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<aqua>Page 3".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(31) }
            )
        }
    }
}