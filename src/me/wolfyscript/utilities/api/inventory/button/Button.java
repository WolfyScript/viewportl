package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class Button {

    private ButtonType type;
    private ButtonState state;

    public Button(WolfyUtilities api, ButtonType type, ButtonState state){
        this.type = type;
        this.state = state;
        init(api);
    }

    public Button(WolfyUtilities api, ButtonState state){
        this(api, ButtonType.NORMAL, state);
    }

    public void init(WolfyUtilities api){
        state.init(api);
    }

    public void execute(GuiHandler guiHandler, int slot, InventoryView inventoryView, Inventory inventory, Player player){
        if(!type.equals(ButtonType.DUMMY) && state.getAction() != null){
            state.getAction().run(guiHandler.getApi(), player, inventoryView, inventory, guiHandler);
        }
    }

    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help){
        InventoryAPI invAPI = guiHandler.getApi().getInventoryAPI();
        inventory.setItem(slot, invAPI.getItem(state.getIcon().getNamespace(), state.getIcon().getKey(), help));
    }

    public ButtonType getType() {
        return type;
    }

    public ButtonState getState() {
        return state;
    }
}
