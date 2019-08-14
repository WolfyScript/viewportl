package me.wolfyscript.utilities.api.inventory.button.buttons;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ToggleButton extends Button {

    private ButtonState[] states;
    private HashMap<GuiHandler, Boolean> settings;

    /*
        This Button toggles between two states and executes the corresponding action!
        The actions are not allowed to be null!
        You can add a empty action however, but then you should consider using a normal Button!
     */

    public ToggleButton(String id, @NotNull ButtonState state, @NotNull ButtonState state2) {
        super(id, ButtonType.TOGGLE);
        states = new ButtonState[]{state, state2};
        settings = new HashMap<>();
    }

    @Override
    public void init(GuiWindow guiWindow) {
        states[0].init(guiWindow);
        states[1].init(guiWindow);
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        states[0].init(windowKey, api);
        states[1].init(windowKey, api);
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        boolean result = states[settings.getOrDefault(guiHandler, true) ? 0 : 1].getAction().run(guiHandler, player, inventory, slot, event);
        settings.put(guiHandler, !settings.getOrDefault(guiHandler, true));
        return result;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        ButtonState state = states[settings.getOrDefault(guiHandler, true) ? 0 : 1];
        ItemStack item = state.getIcon(help);
        HashMap<String, Object> values = new HashMap<>();
        if(state.getAction() instanceof ButtonActionRender){
            item = ((ButtonActionRender) state.getAction()).render(values, guiHandler, player, item);
        }else if(state.getRenderAction() != null){
            item = state.getRenderAction().render(values, guiHandler, player, item);
        }
        ItemMeta meta = item.getItemMeta();
        if(meta != null && meta.hasDisplayName()){
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
