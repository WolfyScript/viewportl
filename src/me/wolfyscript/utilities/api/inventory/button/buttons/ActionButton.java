package me.wolfyscript.utilities.api.inventory.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ActionButton extends Button {

    private String id;
    private ButtonType type;
    private ButtonState state;

    public ActionButton(String id, ButtonType type, ButtonState state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public ActionButton(String id, ButtonState state) {
        this(id, ButtonType.NORMAL, state);
    }

    public void init(GuiWindow guiWindow) {
        state.init(guiWindow);
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        state.init(windowKey, api);
    }

    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().run(guiHandler, player, inventory, slot, event);
        }
        return true;
    }

    public void render(GuiHandler guiHandler, int slot, Inventory inventory, boolean help) {
        InventoryAPI invAPI = guiHandler.getApi().getInventoryAPI();
        inventory.setItem(slot, state.getIcon(help));
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
