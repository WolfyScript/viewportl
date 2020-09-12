package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

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

    public DummyButton(String id, ItemStack itemStack) {
        this(id, new ButtonState(id, itemStack));
    }

    public DummyButton(String id, Material itemStack) {
        this(id, new ButtonState(id, itemStack));
    }

    public DummyButton(String id, ItemStack itemStack, ButtonRender render) {
        this(id, new ButtonState(id, itemStack, render));
    }

    public DummyButton(String id, Material itemStack, ButtonRender render) {
        this(id, new ButtonState(id, itemStack, render));
    }

    @Override
    public boolean execute(GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        //NOTHING
        return true;
    }

    @Override
    public void render(GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, boolean help) throws IOException {
        super.render(guiHandler, player, inventory, slot, help);
    }
}
