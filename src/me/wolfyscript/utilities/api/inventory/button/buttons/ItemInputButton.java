package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

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
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        ItemStack item = content.getOrDefault(guiHandler, new ItemStack(Material.AIR));
        HashMap<String, Object> values = new HashMap<>();
        if(getState().getAction() instanceof ButtonActionRender){
            ((ButtonActionRender) getState().getAction()).render(values, guiHandler, player, item);
        }else if(getState().getRenderAction() != null){
            getState().getRenderAction().render(values, guiHandler, player, item);
        }
        ItemMeta meta = item.getItemMeta();
        if(meta.hasDisplayName()){
            String name = meta.getDisplayName();
            for(Map.Entry<String, Object> entry : values.entrySet()){
                name = name.replace(entry.getKey(), String.valueOf(entry.getValue()));
            }
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }
}
