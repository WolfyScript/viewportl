package com.wolfyscript.utilities.gui.example;

import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.WindowBuilder;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.reactivity.Signal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Optional;

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
public class CounterExample {

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
        manager.registerGuiFromFiles("example_counter", router -> router.window(CounterExample::mainMenu));
    }

    /**
     * Almost all the functions can be written the way you want.
     * You can have everything in one function, or split into different functions, like I did here.
     * If you only have a single inventory it isn't helping a lot, but for multi-window GUIs this makes it a lot cleaner.
     *
     * @param window The WindowBuilder to use for the main menu
     */
    static void mainMenu(WindowBuilder window) {
        // This is only called upon creation of the component. So this is not called when the signal is updated!

        // Use signals that provide a simple value storage & synchronisation. Signals are not persistent and will get destroyed when the GUI is closed!
        Signal<Integer> countSignal = window.createSignal(Integer.class, r -> 0);

        // Optionally, sync your data with the gui using custom data stores. This makes it possible to store persistent data.
        Signal<Integer> count = window.createStore(guiViewManager -> new CounterStore(), CounterStore::getCount, CounterStore::setCount);
        count.tagName("count");

        window.size(9 * 3);

        window.addIntervalTask(() -> {
            count.update(integer -> ++integer); // Updates the count periodically (every second increases it by 1)
        }, 20);

        window.titleSignals(count);

        window.button("count_down", bb -> countDownButton(bb, count));
        // Sometimes we want to render components dependent on signals
        window.whenever(() -> count.get() != 0)
                .then(builder -> builder.button("reset", bb -> resetButton(bb, count)))
                .elseNone();
        // The state of a component is only reconstructed if the slot it is positioned at changes.
        // Here the slot will always have the same type of component, so the state is created only once.
        window.button("count_up", bb -> countUpButton(bb, count))
                .button("counter", bb -> bb.icon(ib -> ib.updateOnSignals(count)));
    }

    /**
     * Since all the components are declared statically as well we can easily move them into their own functions
     **/
    static void countDownButton(ButtonBuilder bb, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.update(old -> --old);
            return InteractionResult.cancel(true);
        });
    }

    static void countUpButton(ButtonBuilder bb, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.update(old -> ++old);
            return InteractionResult.cancel(true);
        }).animation(animationBuilder -> animationBuilder
                // Here we specify the frames to render after each other
                // So it first renders the cyan_concrete for a tick, then lime_concrete for a tick
                .frame(frame -> frame.duration(1).stack("cyan_concrete", conf -> {
                }))
                .frame(frame -> frame.duration(1).stack("lime_concrete", conf -> {
                }))
        );
    }

    static void resetButton(ButtonBuilder bb, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
            return InteractionResult.cancel(true);
        }).sound(holder -> Optional.of(Sound.sound(Key.key("minecraft:entity.dragon_fireball.explode"), Sound.Source.MASTER, 0.25f, 1)));
    }

}
