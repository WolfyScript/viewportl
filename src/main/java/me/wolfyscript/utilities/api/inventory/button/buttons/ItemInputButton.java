package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;

public class ItemInputButton extends ActionButton {

    private final HashMap<GuiHandler, ItemStack> content;

    /*
    This Button acts as a container for Items.
    It saves the placed in item and can also execute
    an action on each click.
     */

    public ItemInputButton(String id, ButtonState state) {
        super(id, ButtonType.ITEM_SLOT, state);
        this.content = new HashMap<>();
    }

    public ItemInputButton(String id, ItemStack itemStack) {
        this(id, new ButtonState(id, itemStack));
    }

    public ItemInputButton(String id, Material material) {
        this(id, new ButtonState(id, material));
    }

    public ItemInputButton(String id, ItemStack itemStack, ButtonAction action) {
        this(id, new ButtonState(id, itemStack, action));
    }

    public ItemInputButton(String id, ItemStack itemStack, ButtonRender render) {
        this(id, new ButtonState(id, itemStack, render));
    }

    public ItemInputButton(String id, ItemStack itemStack, ButtonAction action, ButtonRender render) {
        this(id, new ButtonState(id, itemStack, action, render));
    }

    public ItemInputButton(String id, Material material, ButtonAction action) {
        this(id, new ButtonState(id, material, action));
    }

    public ItemInputButton(String id, Material material, ButtonRender render) {
        this(id, new ButtonState(id, material, render));
    }

    public ItemInputButton(String id, Material material, ButtonAction action, ButtonRender render) {
        this(id, new ButtonState(id, material, action, render));
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) throws IOException {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> content.put(guiHandler, inventory.getItem(slot) != null ? inventory.getItem(slot).clone() : new ItemStack(Material.AIR)), 1);
        if (!getType().equals(ButtonType.DUMMY) && getState().getAction() != null) {
            return getState().getAction().run(guiHandler, player, inventory, slot, event);
        }
        return false;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) throws IOException {
        ItemStack item = content.getOrDefault(guiHandler, new ItemStack(Material.AIR));
        HashMap<String, Object> values = new HashMap<>();
        if (getState().getAction() instanceof ButtonActionRender) {
            item = ((ButtonActionRender) getState().getAction()).render(values, guiHandler, player, item, slot, help);
        } else if (getState().getRenderAction() != null) {
            item = getState().getRenderAction().render(values, guiHandler, player, item, slot, help);
        }
        inventory.setItem(slot, replaceKeysWithValue(item, values));
    }
}
