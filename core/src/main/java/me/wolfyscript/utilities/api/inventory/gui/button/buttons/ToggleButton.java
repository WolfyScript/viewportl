package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
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
import java.util.function.Function;

/**
 * This Button toggles between two states and executes the corresponding action!
 * The actions are not allowed to be null!
 * You can add a empty action, but then you should consider using a normal Button!
 *
 * @param <C>
 */
public class ToggleButton<C extends CustomCache> extends Button<C> {

    private final Pair<ButtonState<C>, ButtonState<C>> states;
    private final boolean defaultState;
    private final Function<GuiUpdate<C>, Boolean> stateFunction;
    private final HashMap<GuiHandler<C>, Boolean> settings;

    public ToggleButton(String id, boolean defaultState, @Nullable Function<GuiUpdate<C>, Boolean> stateFunction, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        super(id, ButtonType.TOGGLE);
        this.defaultState = defaultState;
        states = new Pair<>(state, state2);
        settings = new HashMap<>();
        this.stateFunction = stateFunction;
    }

    public ToggleButton(String id, boolean defaultState, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, defaultState, null, state, state2);
    }

    public ToggleButton(String id, @Nullable Function<GuiUpdate<C>, Boolean> stateFunction, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, false, stateFunction, state, state2);
    }

    public ToggleButton(String id, @Nonnull ButtonState<C> state, @Nonnull ButtonState<C> state2) {
        this(id, false, null, state, state2);
    }

    public void setState(GuiUpdate<C> update) {
        if (stateFunction != null) {
            setState(update.getGuiHandler(), stateFunction.apply(update));
        }
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
    public void init(String windowKey, WolfyUtilities api) {
        states.getKey().init(windowKey, api);
        states.getValue().init(windowKey, api);
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
        ButtonState<C> state = settings.getOrDefault(guiHandler, defaultState) ? states.getKey() : states.getValue();
        if (state.getPrepareRender() != null) {
            state.getPrepareRender().prepare(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, help);
        }
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        applyItem(guiHandler, player, guiInventory, inventory, settings.getOrDefault(guiHandler, defaultState) ? states.getKey() : states.getValue(), slot, help);
    }
}
