package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemInputButton extends ActionButton {

    private HashMap<GuiHandler, ItemStack> content;

    /*
    This Button acts as a container for Items.
    It saves the placed in item and can also execute
    an action on each click.
     */

    public ItemInputButton(String id, ButtonState state) {
        super(id, ButtonType.ITEM_SLOT, state);
        this.content = new HashMap<>();
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        content.put(guiHandler, inventory.getItem(slot) != null ? inventory.getItem(slot).clone() : new ItemStack(Material.AIR));
        return super.execute(guiHandler, player, inventory, slot, event);
    }

    @Override
    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help) {
        ItemStack itemStack = content.getOrDefault(guiHandler, new ItemStack(Material.AIR));
        inventory.setItem(slot, itemStack);
    }
}
