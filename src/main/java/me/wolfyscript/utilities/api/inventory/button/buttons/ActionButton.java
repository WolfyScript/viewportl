package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ActionButton extends Button {

    private final String id;
    private final ButtonType type;
    private final ButtonState state;

    public ActionButton(String id, ButtonType type, ButtonState state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public ActionButton(String id, ButtonState state) {
        this(id, ButtonType.NORMAL, state);
    }

    public ActionButton(String id, ItemStack itemStack) {
        this(id, new ButtonState(id, itemStack));
    }

    public ActionButton(String id, ItemStack itemStack, ButtonAction action) {
        this(id, itemStack, action, null);
    }

    public ActionButton(String id, ItemStack itemStack, ButtonRender render) {
        this(id, itemStack, null, render);
    }

    public ActionButton(String id, ItemStack itemStack, ButtonAction action, ButtonRender render) {
        this(id, new ButtonState(id, itemStack, action, render));
    }

    public ActionButton(String id, Material material) {
        this(id, new ButtonState(id, material));
    }

    public ActionButton(String id, Material material, ButtonAction action) {
        this(id, material, action, null);
    }

    public ActionButton(String id, Material material, ButtonRender render) {
        this(id, material, null, render);
    }

    public ActionButton(String id, Material material, ButtonAction action, ButtonRender render) {
        this(id, new ButtonState(id, material, action, render));
    }

    public void init(GuiWindow guiWindow) {
        state.init(guiWindow);
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        state.init(windowKey, api);
    }

    public boolean execute(GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) throws IOException {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().run(guiHandler, player, inventory, slot, event);
        }
        return true;
    }

    public void render(GuiHandler<?> guiHandler, Player player, Inventory inventory, int slot, boolean help) throws IOException {
        applyItem(guiHandler, player, inventory, state, slot, help);
    }

    public ButtonType getType() {
        return type;
    }

    public ButtonState getState() {
        return state;
    }

    public String getId() {
        return id;
    }
}
