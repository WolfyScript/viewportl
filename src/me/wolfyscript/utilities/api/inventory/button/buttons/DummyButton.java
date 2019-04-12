package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class DummyButton extends Button {

    /*
    This Button acts as a dummy, it will not run the action, even if you set one for the ButtonState!
     */

    public DummyButton(WolfyUtilities api, ButtonState state) {
        super(api, ButtonType.DUMMY, state);
    }

    @Override
    public void execute(GuiHandler guiHandler, int slot, InventoryView inventoryView, Inventory inventory, Player player) {
        //NOTHING
    }

    @Override
    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help) {
        super.render(guiHandler, slot, inventory, help);
    }
}
