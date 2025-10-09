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
import com.wolfyscript.scafall.adventure.parsed
import com.wolfyscript.scafall.adventure.text
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.viewportl.gui.GuiAPIManager
import com.wolfyscript.viewportl.gui.WindowScope
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.elements.RouterScope
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
import com.wolfyscript.viewportl.gui.reactivity.createMemo
import com.wolfyscript.viewportl.gui.reactivity.createSignal
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

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
class Counter {

    class CounterStore(val reactiveSource: ReactiveSource) : ReactiveSource by reactiveSource {

        var count: Int by createSignal(0)

    }

    companion object {

        fun register(manager: GuiAPIManager) {
            manager.registerGui("counter") {
                /**
                 * Everything in this section is called **async** and only once.
                 * It constructs the component tree and reactive graph as specified.
                 **/
                size = 9 * 3
                title = "<b>Counter".deser()

                // Use signals to notify components to update when the value changes
                var countStore = CounterStore(this)

                router {
                    route({ }) {
                        mainMenu(this@registerGui, this@router)
                    }
                    route({ this / "main" }) {
                        counterMenu(countStore, this@registerGui, this@router)
                    }
                }

            }
        }

        private fun ComponentScope.mainMenu(window: WindowScope, routerScope: RouterScope) {
            window.title {
                "<dark_gray><b>Counter Main Menu".deser()
            }

            button(
                icon = {
                    stack = openCounterIcon
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
        private fun ComponentScope.counterMenu(
            counterStore: CounterStore,
            window: WindowScope,
            routerScope: RouterScope,
        ) {
            window.title {
                "<dark_gray><b>Counter".deser()
            }
            // Update the count periodically (every second)
            interval(20) {
                counterStore.count += 1
            }

            button(
                icon = { stack = backIcon },
                styles = {
                    position = PropertyPosition.slot(0)
                },
                onClick = { routerScope.openPrevious() }
            )

            button(
                styles = {
                    position = PropertyPosition.slot(6)
                },
                icon = { stack = countUpIcon },
                onClick = {
                    counterStore.count += 1
                }
            )

            button(
                styles = {
                    position = PropertyPosition.slot(13)
                },
                icon = {
                    stack = ItemStack(Items.REDSTONE).apply {
                        set(
                            DataComponents.CUSTOM_NAME,
                            "<!italic>Score: <b><count></b>".deser("count".parsed(counterStore.count)).vanilla()
                        )
                    }.wrap()
                },
            )

            button(
                styles = {
                    position = PropertyPosition.slot(24)
                },
                icon = { stack = countDownIcon },
                onClick = {
                    counterStore.count -= 1
                }
            )

            // Sometimes we want to render components dependent on signals
            val render by createMemo<Boolean>(false) { counterStore.count != 0 }
            show(
                condition = { render },
                {
                    button(
                        styles = {
                            position = PropertyPosition.slot(11)
                        },
                        icon = { stack = resetIcon },
                        onClick = {
                            // Changes the value of the signal and prompts the listener of the signal to re-render.
                            counterStore.count = 0
                        },
                        sound = Sound.sound(
                            net.kyori.adventure.key.Key.key("minecraft:entity.dragon_fireball.explode"),
                            Sound.Source.MASTER,
                            0.25f,
                            1f
                        )
                    )
                }
            ) { }

        }

        /*
         * Construct static icons ones. No need to reparse. Plus, have them all in one place.
         */

        private val openCounterIcon = ItemStack(Items.GREEN_CONCRETE).apply {
            // Using Minecraft Components directly (no conversion, fastest)
            set(
                DataComponents.CUSTOM_NAME,
                Component.literal("Open Counter").withStyle(ChatFormatting.BOLD)
                    .withStyle(ChatFormatting.GREEN)
            )
            // Using Adventure Builder (adventure -> gson -> minecraft, 2 conversions, slower)
            set(
                DataComponents.CUSTOM_NAME, "Open Counter".text(
                    Style.style(NamedTextColor.GREEN, TextDecoration.BOLD)
                ).vanilla()
            )
            // or minimessage (minimessage -> adventure -> gson -> minecraft, 3 conversions, slowest)
            set(DataComponents.CUSTOM_NAME, "<green><b>Open Counter".deser().vanilla())
        }.wrap()

        private val resetIcon = ItemStack(Items.TNT).apply {
            set(DataComponents.CUSTOM_NAME, "<b><red>Reset Clicks!".deser().vanilla())
        }.wrap()

        private val countDownIcon = ItemStack(Items.RED_CONCRETE).apply {
            set(DataComponents.CUSTOM_NAME, "<red><b>Count Down".deser().vanilla())
        }.wrap()

        private val countUpIcon = ItemStack(Items.GREEN_CONCRETE).apply {
            set(DataComponents.CUSTOM_NAME, "<green><b>Count Up".deser().vanilla())
        }.wrap()

        private val backIcon = ItemStack(Items.BARRIER).apply {
            set(DataComponents.CUSTOM_NAME, "<red><b>Back".deser().vanilla())
        }.wrap()

    }
}