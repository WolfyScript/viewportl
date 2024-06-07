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

package com.wolfyscript.viewportl.gui.example;

import com.wolfyscript.viewportl.gui.GuiAPIManager;
import com.wolfyscript.viewportl.gui.Window;
import com.wolfyscript.viewportl.gui.components.Button;
import com.wolfyscript.viewportl.gui.components.ComponentGroup;
import com.wolfyscript.viewportl.gui.interaction.InteractionResult;
import com.wolfyscript.viewportl.gui.reactivity.Signal;
import com.wolfyscript.viewportl.gui.rendering.PropertyPosition;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.Objects;

/**
 * A Counter GUI Example, that allows the viewer:
 * <ul>
 *     <li>to click one Button to increase the count,</li>
 *     <li>another button to decrease the count,</li>
 *     <li>and a button to reset the count to 0.</li>
 * </ul>
 * <p>
 * The reset Button is only displayed, when the count is not 0.<br>
 * Whenever the GUI is open the count is increased periodically every second, without requiring any input.
 * <br>
 * The count is displayed in the title of the Inventory and in the item name of the button in the middle.
 * Those parts are automatically updated when the count changes.
 */
public class CounterExampleJava {

    /**
     * Stores the count value so that it persists when the GUI is closed.
     */
    private static class CounterStore {

        private int count = 0;

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static void register(GuiAPIManager manager) {
        manager.registerGui("example_counter", window -> {
            window.setSize(9 * 3);

            window.routes(router -> {
                router.route(
                        route -> route.div("users").div("id").times(Integer.class),
                        view -> CounterExampleJava.mainMenu(window, view),
                        subRoutes -> { /* No sub routes */ }
                );
            });
        });
    }

    /**
     * Almost all the functions can be written the way you want.
     * You can have everything in one function, or split into different functions, like I did here.
     * If you only have a single inventory it isn't helping a lot, but for multi-window GUIs this makes it a lot cleaner.
     *
     * @param window The WindowBuilder to use for the main menu
     */
    static void mainMenu(Window window, ComponentGroup group) {
        // This is only called upon creation of the component. So this is not called when the signal is updated!

        // Use signals that provide a simple value storage & synchronisation.
        Signal<Integer> count = window.createSignal(Integer.TYPE, r -> 0);
        count.tagName("count");

        window.title(prevTitle ->  // Update the title with the Count
                Component.text("Counter: ").decorate(TextDecoration.BOLD)
                        .append(Component.text(Objects.requireNonNullElse(count.get(), 0))
                                .color(NamedTextColor.BLUE))
        );

        // Updates the count periodically (every second increases it by 1)
//        group.addIntervalTask(() -> {
//            count.update(integer -> ++integer);
//        }, 20);

        group.button("count_up", bb -> countUpButton(bb, count));
        group.button("counter", bb -> bb.icon(ib -> ib.resolvers(() -> Placeholder.parsed("count", String.valueOf(count.get())))));
        group.button("count_down", bb -> countDownButton(bb, count));
        // Sometimes we want to render components dependent on signals
        group.whenever(() -> Objects.equals(count.get(), 0))
                .then(builder -> builder.button("reset", bb -> resetButton(bb, count)));
    }

    /**
     * Since all the components are declared statically as well we can easily move them into their own functions
     **/
    static void countDownButton(Button bb, Signal<Integer> count) {
        bb.properties(props -> {
            props.setPosition(PropertyPosition.Companion.slot(22));
        });
        bb.icon(icon -> {
            icon.stack("red_concrete", stack -> {
                stack.name("<red><b>Count Down");
            });
        });

        bb.setOnClick(interactionDetails -> {
            count.update(old -> --old);
        });
    }

    static void countUpButton(Button bb, Signal<Integer> count) {
        bb.properties(props -> {
            props.setPosition(PropertyPosition.Companion.slot(4));
        });
        bb.icon(icon -> {
            icon.stack("green_concrete", stack -> {
                stack.name("<green><b>Count Up");
            });
        });

        bb.setOnClick(interactionDetails -> {
            count.update(old -> ++old);
        });
    }

    static void resetButton(Button bb, Signal<Integer> count) {
        bb.properties(props -> {
            props.setPosition(PropertyPosition.Companion.slot(10));
        });
        bb.icon(icon -> {
            icon.stack("tnt", stack -> {
                stack.name("<b><red>Reset Clicks!");
            });
        });

        bb.setOnClick(interactionDetails -> {
            count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
        });
        bb.setSound(Sound.sound(
                Key.key("minecraft:entity.dragon_fireball.explode"),
                Sound.Source.MASTER,
                0.25f,
                1
        ));
    }

}
