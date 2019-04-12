package me.wolfyscript.utilities.api.inventory.button.buttons;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MultipleChoiceButton extends Button {

    private List<ButtonState> states;
    private HashMap<GuiHandler, Integer> settings;

    /*
    This Button goes through ech of the States.
    Each click the index increases by 1.
    After the index reached the size of the States it is reset to 0!
     */

    public MultipleChoiceButton(WolfyUtilities api, @NotNull ButtonState... states) {
        super(api, ButtonType.CHOICES, null);
        this.states = Arrays.asList(states);
        settings = new HashMap<>();
    }

    @Override
    public void init(WolfyUtilities api) {
        for(ButtonState btnState : states){
            btnState.init(api);
        }
    }

    @Override
    public void execute(GuiHandler guiHandler, int slot, InventoryView inventoryView, Inventory inventory, Player player) {
        int setting = settings.getOrDefault(guiHandler, 0);
        if(states != null && states.size() > setting){
            ButtonState btnState = states.get(setting);
            btnState.getAction().run(guiHandler.getApi(), player, inventoryView, inventory, guiHandler);
            setting++;
            if(setting >= states.size()){
                settings.put(guiHandler, 0);
            }
        }
    }

    @Override
    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help) {
        InventoryAPI invAPI = guiHandler.getApi().getInventoryAPI();
        int setting = settings.getOrDefault(guiHandler, 0);
        if(states != null && states.size() > setting){
            inventory.setItem(slot, invAPI.getItem(states.get(setting).getIcon().getNamespace(), states.get(setting).getIcon().getKey(), help));
        }
    }
}
