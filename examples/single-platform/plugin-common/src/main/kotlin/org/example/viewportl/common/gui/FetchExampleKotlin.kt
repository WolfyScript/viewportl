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
import com.wolfyscript.scafall.parsed
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.elements.RouterScope
import com.wolfyscript.viewportl.gui.reactivity.Trigger
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import java.net.MalformedURLException

class FetchExampleKotlin {

    companion object {

        fun register(manager: GuiAPIManager) {
            manager.registerGui("example_fetch") {
                /**
                 * Everything in this section is called **async** and only once.
                 * It constructs the component tree and reactive graph as specified.
                 **/
                size = 9 * 3

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

            val fetch = createTrigger()
            val fetched by resourceAsync<Int>(input = { fetch.track(); emptyList<Any>() }) { viewportl, runtime ->
                Thread.sleep(4000);
                if (Math.random() > 0.5) {
                    return@resourceAsync Result.failure(MalformedURLException());
                }
                return@resourceAsync Result.success(20)
            }

            show({ fetched.isEmpty }, {
                show({ fetched.get().isFailure }, {
                    button(
                        icon = {
                            stack("written_book") {
                                set(ItemStackDataKeys.CUSTOM_NAME, "<green><b>Fetched ${fetched.get().getOrDefault(0)}".deserialize())
                            }
                        },
                        styles = { position = PropertyPosition.slot(16) },
                    )
                    refetch(fetch)
                }) {
                    button(
                        icon = {
                            stack("barrier") {
                                set(ItemStackDataKeys.CUSTOM_NAME, "<red><b>Failed to fetch".deserialize())
                            }
                        },
                        styles = { position = PropertyPosition.slot(16) },
                    )
                    refetch(fetch)
                }
            }) {
               button(
                   icon = {
                       stack("paper") {
                           set(ItemStackDataKeys.CUSTOM_NAME, "<red><b>Fetching...".deserialize())
                       }
                   },
                   styles = { position = PropertyPosition.slot(16) },
               )
            }
        }

        private fun ComponentScope.refetch(fetchTrigger: Trigger) {
            button(
                icon = {
                    stack("cyan_concrete") {
                        set(ItemStackDataKeys.CUSTOM_NAME, "<cyan><b>Fetch Again".deserialize())
                    }
                },
                styles = { position = PropertyPosition.slot(15) },
                onClick = {
                    fetchTrigger.update()
                }
            )
        }

    }
}