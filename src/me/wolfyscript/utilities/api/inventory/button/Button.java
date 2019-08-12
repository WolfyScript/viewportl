package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class Button {

    private String id;
    private ButtonType type;

    public Button(String id, ButtonType type) {
        this.id = id;
        this.type = type;
    }

    public Button(String id) {
        this(id, ButtonType.NORMAL);
    }

    public abstract void init(GuiWindow guiWindow);

    public abstract void init(String windowKey, WolfyUtilities api);

    public abstract boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event);

    public abstract void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help);

    public ButtonType getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
