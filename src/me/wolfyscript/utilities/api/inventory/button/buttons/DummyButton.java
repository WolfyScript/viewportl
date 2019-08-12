package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class DummyButton extends ActionButton {

    /*
    This Button acts as a dummy, it will not run the action, even if you set one for the ButtonState!
     */

    public DummyButton(String id, ButtonState state) {
        super(id, ButtonType.DUMMY, state);
    }

    public DummyButton(String id) {
        super(id, ButtonType.DUMMY, null);
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        //NOTHING
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        super.render(guiHandler,player, inventory, slot, help);
    }
}
