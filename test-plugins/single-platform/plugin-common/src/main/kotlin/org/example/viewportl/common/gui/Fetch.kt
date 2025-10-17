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

import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.core.component.DataComponents.CUSTOM_NAME
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.net.MalformedURLException

class Fetch {

    companion object {

        fun register(manager: GuiAPIManager) {
            manager.registerGui("fetch") {
                /**
                 * Everything in this section is called **async** and only once.
                 * It constructs the component tree and reactive graph as specified.
                 **/
                size = 9 * 3
                title = "Fetch Test".deser()

                router {
                    route({ }) {
                        mainMenu(this@registerGui)
                    }
                }

            }
        }

        private fun ComponentScope.mainMenu(window: WindowScope) {
            window.title { // Update the title with the Count
                Component.text("Fetcher Main Menu").decorate(TextDecoration.BOLD)
            }

            val (fetchedSignal, refetch) = resourceAsync(null, Unit.javaClass as Class<Unit?>, { }) { unit, viewportl, runtime ->
                Thread.sleep(4000)
                if (Math.random() > 0.5) {
                    return@resourceAsync Result.failure(MalformedURLException());
                }
                return@resourceAsync Result.success(20)
            }
            var fetched by fetchedSignal

            show({ fetched != null }, {
                show({ fetched!!.isSuccess }, {
                    button(
                        icon = {
                            stack = ItemStack(Items.WRITTEN_BOOK).apply {
                                set(
                                    CUSTOM_NAME,
                                    "<green><b>Fetched ${fetched!!.getOrDefault(0)}".deser().vanilla()
                                )
                            }.wrap()
                        },
                        styles = { position = PropertyPosition.slot(14) },
                    )
                    refetch(refetch)
                }) {
                    button(
                        icon = {
                            stack = ItemStack(Items.BARRIER).apply {
                                set(CUSTOM_NAME, "<red><b>Failed to fetch".deser().vanilla())
                            }.wrap()
                        },
                        styles = { position = PropertyPosition.slot(14) },
                    )
                    refetch(refetch)
                }
            }) {
                button(
                    icon = {
                        stack = ItemStack(Items.PAPER).apply {
                            set(CUSTOM_NAME, "<red><b>Fetching...".deser().vanilla())
                        }.wrap()
                    },
                    styles = { position = PropertyPosition.slot(14) },
                )
            }
        }

        private fun ComponentScope.refetch(refetch: () -> Unit) {
            button(
                icon = {
                    stack = ItemStack(Items.CYAN_CONCRETE).apply {
                        set(CUSTOM_NAME, "<aqua><b>Fetch Again".deser().vanilla())
                    }.wrap()
                },
                styles = { position = PropertyPosition.slot(12) },
                onClick = { refetch() }
            )
        }

    }
}