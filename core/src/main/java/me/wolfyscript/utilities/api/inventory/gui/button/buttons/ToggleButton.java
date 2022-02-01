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

package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * This Button toggles between two states and executes the corresponding action!
 * The actions are not allowed to be null!
 * You can add a empty action, but then you should consider using a normal Button!
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ToggleButton<C extends CustomCache> extends Button<C> {

    private final Pair<ButtonState<C>, ButtonState<C>> states;
    private final boolean defaultState;
    private final StateFunction<C> stateFunction;
    private final HashMap<GuiHandler<C>, Boolean> settings;

    /**
     * @param id            The id of the Button
     * @param defaultState  The state to use when the Button is first rendered and haven't been clicked before.
     * @param stateFunction The {@link StateFunction} to set the state of the Button depending on the player, cached data, etc.
     * @param state         The {@link ButtonState} that is rendered if the state is true.
     * @param state2        The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, boolean defaultState, @Nullable ToggleButton.StateFunction<C> stateFunction, @NotNull ButtonState<C> state, @NotNull ButtonState<C> state2) {
        super(id, ButtonType.TOGGLE);
        this.defaultState = defaultState;
        states = new Pair<>(state, state2);
        settings = new HashMap<>();
        this.stateFunction = stateFunction == null ? (cache, guiHandler, player, inventory, slot) -> settings.getOrDefault(guiHandler, defaultState) : stateFunction;
    }

    /**
     * @param id           The id of the Button
     * @param defaultState The state to use when the Button is first rendered and haven't been clicked before.
     * @param state        The {@link ButtonState} that is rendered if the state is true.
     * @param state2       The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, boolean defaultState, @NotNull ButtonState<C> state, @NotNull ButtonState<C> state2) {
        this(id, defaultState, null, state, state2);
    }

    /**
     * @param id            The id of the Button
     * @param stateFunction The {@link StateFunction} to set the state of the Button depending on the player, cached data, etc.
     * @param state         The {@link ButtonState} that is rendered if the state is true.
     * @param state2        The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, @Nullable ToggleButton.StateFunction<C> stateFunction, @NotNull ButtonState<C> state, @NotNull ButtonState<C> state2) {
        this(id, false, stateFunction, state, state2);
    }

    /**
     * @param id     The id of the Button
     * @param state  The {@link ButtonState} that is rendered if the state is true.
     * @param state2 The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, @NotNull ButtonState<C> state, @NotNull ButtonState<C> state2) {
        this(id, false, null, state, state2);
    }

    public void setState(GuiHandler<C> guiHandler, boolean enabled) {
        settings.put(guiHandler, enabled);
    }

    public ButtonState<C> getState(GuiHandler<C> guiHandler) {
        return Boolean.TRUE.equals(settings.getOrDefault(guiHandler, defaultState)) ? states.getKey() : states.getValue();
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        states.getKey().init(guiWindow);
        states.getValue().init(guiWindow);
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        states.getKey().init(guiCluster);
        states.getValue().init(guiCluster);
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        ButtonState<C> state = getState(guiHandler);
        if (state.getPostAction() != null) {
            state.getPostAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, event);
        }
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        boolean result = getState(guiHandler).getAction().execute(guiHandler.getCustomCache(), guiHandler, player, inventory, this, slot, event);
        settings.put(guiHandler, !settings.getOrDefault(guiHandler, defaultState));
        return result;
    }

    @Override
    public void preRender(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean help) {
        boolean state = stateFunction.run(guiHandler.getCustomCache(), guiHandler, player, inventory, slot);
        settings.put(guiHandler, state);
        ButtonState<C> buttonState = state ? states.getKey() : states.getValue();
        if (buttonState.getPrepareRender() != null) {
            buttonState.getPrepareRender().prepare(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, help);
        }
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        applyItem(guiHandler, player, guiInventory, inventory, getState(guiHandler), slot, help);
    }

    public interface StateFunction<C extends CustomCache> {

        /**
         * Used to set the state for the {@link ToggleButton} depending on data from the cache or player, etc.
         *
         * @param cache      The current cache of the GuiHandler
         * @param guiHandler The current GuiHandler.
         * @param player     The current Player.
         * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
         * @param slot       The slot in which the button is rendered.
         * @return a boolean indicating the state of the button.
         */
        boolean run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot);

    }

    public static class Builder<C extends CustomCache> extends Button.Builder<C, ToggleButton<C>> {

        protected boolean defaultState;
        protected StateFunction<C> stateFunction;
        protected ButtonState.Builder<C> enabledStateBuilder;
        protected ButtonState.Builder<C> disabledStateBuilder;

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ToggleButton<C>>) (Object) ToggleButton.class);
            this.enabledStateBuilder = ButtonState.of(window);
            this.disabledStateBuilder = ButtonState.of(window);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ToggleButton<C>>) (Object) ToggleButton.class);
            this.enabledStateBuilder = ButtonState.of(cluster);
            this.disabledStateBuilder = ButtonState.of(cluster);
        }

        public Builder<C> enabledState(Consumer<ButtonState.Builder<C>> builderConsumer) {
            builderConsumer.accept(enabledStateBuilder);
            return this;
        }

        public Builder<C> disabledState(Consumer<ButtonState.Builder<C>> builderConsumer) {
            builderConsumer.accept(disabledStateBuilder);
            return this;
        }

        public Builder<C> stateFunction(StateFunction<C> stateFunction) {
            this.stateFunction = stateFunction;
            return this;
        }

        public Builder<C> defaultState(boolean defaultState) {
            this.defaultState = defaultState;
            return this;
        }

        @Override
        public ToggleButton<C> create() {
            return new ToggleButton<>(key, defaultState, stateFunction, enabledStateBuilder.create(), disabledStateBuilder.create());
        }
    }
}
