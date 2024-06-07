package com.wolfyscript.utilities.gui.example

import com.wolfyscript.utilities.eval.value_provider.provider
import com.wolfyscript.utilities.gui.GuiAPIManager
import com.wolfyscript.utilities.gui.Window
import com.wolfyscript.utilities.gui.components.ComponentGroup
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.gui.router.Router
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

fun ComponentGroup.counterMainMenu(window: Window, router: Router) {
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

fun ComponentGroup.counter(window: Window, router: Router) {
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
