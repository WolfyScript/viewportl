package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
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
        if (!getType().equals(ButtonType.DUMMY) && getState().getAction() != null) {
            return getState().getAction().run(guiHandler, player, inventory, slot, event);
        }
        return false;
    }

    @Override
    public void prepareRender(GuiHandler<?> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        content.put(guiHandler, itemStack != null ? itemStack.clone() : new ItemStack(Material.AIR));
        super.prepareRender(guiHandler, player, inventory, itemStack, slot, help);
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) throws IOException {
        ItemStack item = content.getOrDefault(guiHandler, new ItemStack(Material.AIR));
        HashMap<String, Object> values = new HashMap<>();
        if (getState().getRenderAction() != null) {
            item = getState().getRenderAction().render(values, guiHandler, player, item, slot, help);
        }
        inventory.setItem(slot, replaceKeysWithValue(item, values));
    }
}
