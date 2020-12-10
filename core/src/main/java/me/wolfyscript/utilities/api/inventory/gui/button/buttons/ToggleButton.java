package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;

public class ToggleButton extends Button {

    private final ButtonState[] states;
    private final boolean defaultState;
    private final HashMap<GuiHandler, Boolean> settings;

    /*
        This Button toggles between two states and executes the corresponding action!
        The actions are not allowed to be null!
        You can add a empty action however, but then you should consider using a normal Button!
     */

    public ToggleButton(String id, boolean defaultState, @Nonnull ButtonState state, @Nonnull ButtonState state2) {
        super(id, ButtonType.TOGGLE);
        this.defaultState = defaultState;
        states = new ButtonState[]{state, state2};
        settings = new HashMap<>();
    }

    public ToggleButton(String id, @Nonnull ButtonState state, @Nonnull ButtonState state2) {
        this(id, false, state, state2);
    }

    public void setState(GuiHandler guiHandler, boolean enabled){
        settings.put(guiHandler, enabled);
    }

    @Override
    public void init(GuiWindow guiWindow) {
        states[0].init(guiWindow);
        states[1].init(guiWindow);
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        states[0].init(windowKey, api);
        states[1].init(windowKey, api);
    }

    @Override
    public void postExecute(GuiHandler<?> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, InventoryClickEvent event) throws IOException {

    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) throws IOException {
        boolean result = states[settings.getOrDefault(guiHandler, defaultState) ? 0 : 1].getAction().run(guiHandler, player, inventory, slot, event);
        settings.put(guiHandler, !settings.getOrDefault(guiHandler, defaultState));
        return result;
    }

    @Override
    public void prepareRender(GuiHandler<?> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        ButtonState state = states[settings.getOrDefault(guiHandler, defaultState) ? 0 : 1];
        if (state.getPrepareRender() != null) {
            state.getPrepareRender().prepare(guiHandler, player, inventory, itemStack, slot, help);
        }
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        applyItem(guiHandler, player, inventory, states[settings.getOrDefault(guiHandler, defaultState) ? 0 : 1], slot, help);
    }
}
