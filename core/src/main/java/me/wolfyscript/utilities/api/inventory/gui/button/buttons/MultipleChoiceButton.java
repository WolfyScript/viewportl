package me.wolfyscript.utilities.api.inventory.gui.button.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MultipleChoiceButton<C extends CustomCache> extends Button<C> {

    private final List<ButtonState<C>> states;
    private final HashMap<GuiHandler<C>, Integer> settings;

    /*
    This Button goes through ech of the States.
    Each click the index increases by 1.
    After the index reached the size of the States it is reset to 0!
     */
    public MultipleChoiceButton(String id, @Nonnull ButtonState<C>... states) {
        super(id, ButtonType.CHOICES);
        this.states = Arrays.asList(states);
        settings = new HashMap<>();
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        for (ButtonState<C> btnState : states) {
            btnState.init(guiWindow);
        }
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        for (ButtonState<C> btnState : states) {
            btnState.init(windowKey, api);
        }
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && setting < states.size()) {
            ButtonState<C> btnState = states.get(setting);
            setting++;
            if (setting >= states.size()) {
                settings.put(guiHandler, 0);
            } else {
                settings.put(guiHandler, setting);
            }
            return btnState.getAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, slot, event);
        }
        return true;
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && setting < states.size()) {
            ButtonState<C> btnState = states.get(setting);
            if (btnState.getPostAction() != null) {
                btnState.getPostAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, event);
            }
        }
    }

    @Override
    public void preRender(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean help) {
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && states.size() > setting) {
            if (states.get(setting).getPrepareRender() != null) {
                states.get(setting).getPrepareRender().prepare(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, help);
            }
        }
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && states.size() > setting) {
            applyItem(guiHandler, player, guiInventory, inventory, states.get(setting), slot, help);
        }
    }

    public void setState(GuiHandler<C> guiHandler, int state) {
        this.settings.put(guiHandler, state);
    }
}
