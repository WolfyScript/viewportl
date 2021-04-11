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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;

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
    public ToggleButton(String id, boolean defaultState, @Nullable ToggleButton.StateFunction<C> stateFunction, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
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
    public ToggleButton(String id, boolean defaultState, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, defaultState, null, state, state2);
    }

    /**
     * @param id            The id of the Button
     * @param stateFunction The {@link StateFunction} to set the state of the Button depending on the player, cached data, etc.
     * @param state         The {@link ButtonState} that is rendered if the state is true.
     * @param state2        The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, @Nullable ToggleButton.StateFunction<C> stateFunction, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, false, stateFunction, state, state2);
    }

    /**
     * @param id     The id of the Button
     * @param state  The {@link ButtonState} that is rendered if the state is true.
     * @param state2 The {@link ButtonState} that is rendered if the state is false.
     */
    public ToggleButton(String id, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, false, null, state, state2);
    }

    public void setState(GuiHandler<C> guiHandler, boolean enabled) {
        settings.put(guiHandler, enabled);
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        states.getKey().init(guiWindow);
        states.getValue().init(guiWindow);
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        super.init(guiCluster);
        states.getKey().init(guiCluster);
        states.getValue().init(guiCluster);
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        ButtonState<C> state = settings.getOrDefault(guiHandler, defaultState) ? states.getKey() : states.getValue();
        if (state.getPostAction() != null) {
            state.getPostAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, event);
        }
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        boolean result = (settings.getOrDefault(guiHandler, defaultState) ? states.getKey() : states.getValue()).getAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, slot, event);
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
        applyItem(guiHandler, player, guiInventory, inventory, settings.getOrDefault(guiHandler, defaultState) ? states.getKey() : states.getValue(), slot, help);
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
}
