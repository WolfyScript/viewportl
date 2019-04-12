package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class Button {

    private ButtonType type;
    private ButtonAction action;

    public Button(ButtonType type, ButtonAction action){
        this.type = type;
        this.action = action;
    }

    public void execute(WolfyUtilities api, Player player, InventoryView inventoryView, Inventory inventory, GuiHandler guiHandler){
        if(!type.equals(ButtonType.DUMMY) && action != null){
            action.run(api, player, inventoryView, inventory, guiHandler);
        }
    }

    public ButtonType getType() {
        return type;
    }

    public ButtonAction getAction() {
        return action;
    }
}
