package me.wolfyscript.utilities.api.inventory.button.buttons;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;

public class ToggleButton extends Button {

    private ButtonState[] states;
    private HashMap<GuiHandler, Boolean> settings;

    /*
    This Button toggles between two states and executes the corresponding action!
    The actions are not allowed to be null!
        You can add a empty action however, but then you should consider using a normal Button!
     */

    public ToggleButton(WolfyUtilities api, @NotNull ButtonState state, @NotNull ButtonState state2) {
        super(api, ButtonType.TOGGLE, null);
        states = new ButtonState[]{state, state2};
        settings = new HashMap<>();
    }

    @Override
    public void init(WolfyUtilities api) {
        states[0].init(api);
        states[1].init(api);
    }

    @Override
    public void execute(GuiHandler guiHandler, Inventory inventory, int slot, InventoryClickEvent event) {
        states[settings.getOrDefault(guiHandler, true) ? 0 : 1].getAction().run(guiHandler, inventory, slot, event);
        settings.put(guiHandler, !settings.getOrDefault(guiHandler, true));
    }

    @Override
    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help) {
        InventoryAPI invAPI = guiHandler.getApi().getInventoryAPI();
        boolean setting = settings.getOrDefault(guiHandler, true);
        ButtonState buttonState = states[setting ? 0 : 1];
        inventory.setItem(slot, invAPI.getItem(buttonState.getIcon().getNamespace(), buttonState.getIcon().getKey(), help));
    }
}
