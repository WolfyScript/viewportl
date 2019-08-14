package me.wolfyscript.utilities.api.inventory.button.buttons;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleChoiceButton extends Button {

    private List<ButtonState> states;
    private HashMap<GuiHandler, Integer> settings;

    /*
    This Button goes through ech of the States.
    Each click the index increases by 1.
    After the index reached the size of the States it is reset to 0!
     */

    public MultipleChoiceButton(String id, @NotNull ButtonState... states) {
        super(id, ButtonType.CHOICES);
        this.states = Arrays.asList(states);
        settings = new HashMap<>();
    }

    @Override
    public void init(GuiWindow guiWindow) {
        for (ButtonState btnState : states) {
            btnState.init(guiWindow);
        }
    }

    @Override
    public void init(String windowKey, WolfyUtilities api) {
        for (ButtonState btnState : states) {
            btnState.init(windowKey, api);
        }
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && states.size() > setting) {
            ButtonState btnState = states.get(setting);
            setting++;
            if (setting >= states.size()) {
                settings.put(guiHandler, 0);
            }
            return btnState.getAction().run(guiHandler, player, inventory, slot, event);
        }
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        InventoryAPI invAPI = guiHandler.getApi().getInventoryAPI();
        int setting = settings.getOrDefault(guiHandler, 0);
        if (states != null && states.size() > setting) {
            ItemStack item = states.get(setting).getIcon(help);
            HashMap<String, Object> values = new HashMap<>();
            if(states.get(setting).getAction() instanceof ButtonActionRender){
                item = ((ButtonActionRender) states.get(setting).getAction()).render(values, guiHandler, player, item);
            }else if(states.get(setting).getRenderAction() != null){
                item = states.get(setting).getRenderAction().render(values, guiHandler, player, item);
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
}
