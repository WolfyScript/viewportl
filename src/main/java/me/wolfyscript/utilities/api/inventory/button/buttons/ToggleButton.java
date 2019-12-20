package me.wolfyscript.utilities.api.inventory.button.buttons;

import javax.annotation.Nonnull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToggleButton extends Button {

    private ButtonState[] states;
    private boolean defaultState;
    private HashMap<GuiHandler, Boolean> settings;

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
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        boolean result = states[settings.getOrDefault(guiHandler, defaultState) ? 0 : 1].getAction().run(guiHandler, player, inventory, slot, event);
        settings.put(guiHandler, !settings.getOrDefault(guiHandler, defaultState));
        return result;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        applyItem(guiHandler, player, inventory, states[settings.getOrDefault(guiHandler, defaultState) ? 0 : 1], slot, help);
    }
}
