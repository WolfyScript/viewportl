package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> content.put(guiHandler, inventory.getItem(slot) != null ? inventory.getItem(slot).clone() : new ItemStack(Material.AIR)), 1);
        if (!getType().equals(ButtonType.DUMMY) && getState().getAction() != null) {
            return getState().getAction().run(guiHandler, player, inventory, slot, event);
        }
        return false;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        ItemStack item = content.getOrDefault(guiHandler, new ItemStack(Material.AIR));
        HashMap<String, Object> values = new HashMap<>();
        if(getState().getAction() instanceof ButtonActionRender){
            item = ((ButtonActionRender) getState().getAction()).render(values, guiHandler, player, item, slot, help);
        }else if(getState().getRenderAction() != null){
            item = getState().getRenderAction().render(values, guiHandler, player, item, slot, help);
        }
        inventory.setItem(slot, replaceKeysWithValue(item, values));
    }
}
