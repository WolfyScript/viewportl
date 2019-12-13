package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public abstract void init(String clusterID, WolfyUtilities api);

    public abstract boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event);

    public abstract void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help);

    public ButtonType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    protected void applyItem(GuiHandler guiHandler, Player player, Inventory inventory, ButtonState state, int slot, boolean help){
        ItemStack item = state.getIcon(help);
        HashMap<String, Object> values = new HashMap<>();
        values.put("%wolfyutilities.help%", guiHandler.getCurrentInv().getHelpInformation());
        values.put("%plugin.version%", guiHandler.getApi().getPlugin().getDescription().getVersion());
        if(state.getAction() instanceof ButtonActionRender){
            item = ((ButtonActionRender) state.getAction()).render(values, guiHandler, player, item, slot, help);
        }else if(state.getRenderAction() != null){
            item = state.getRenderAction().render(values, guiHandler, player, item, slot, help);
        }
        inventory.setItem(slot, replaceKeysWithValue(item, values));
    }

    protected ItemStack replaceKeysWithValue(ItemStack itemStack, HashMap<String, Object> values){
        if(itemStack != null){
            ItemMeta meta = itemStack.getItemMeta();
            if(meta != null && meta.hasDisplayName()){
                String name = meta.getDisplayName();
                List<String> lore = meta.getLore();
                for(Map.Entry<String, Object> entry : values.entrySet()){
                    if(entry.getValue() instanceof List){
                        List<Object> list = (List<Object>) entry.getValue();
                        if (meta.hasLore()) {
                            for (int i = 0; i < lore.size(); i++) {
                                if(lore.get(i).contains(entry.getKey())){
                                    if(list.size() > 0){
                                        lore.set(i, lore.get(i).replace(entry.getKey(), WolfyUtilities.translateColorCodes(String.valueOf(list.get(list.size()-1)))));
                                    }else{
                                        lore.set(i, "");
                                    }
                                    if(list.size() > 1){
                                        for(int j = list.size()-2; j >= 0; j--){
                                            lore.add(i, WolfyUtilities.translateColorCodes(String.valueOf(list.get(j))));
                                        }
                                    }
                                }
                            }
                        }
                    }else if (entry.getValue() != null){
                        name = name.replace(entry.getKey(), WolfyUtilities.translateColorCodes(String.valueOf(entry.getValue())));
                        if (meta.hasLore()) {
                            for (int i = 0; i < lore.size(); i++) {
                                lore.set(i, lore.get(i).replace(entry.getKey(), WolfyUtilities.translateColorCodes(String.valueOf(entry.getValue()))));
                            }
                        }
                    }
                }
                meta.setDisplayName(name);
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
        }
        return itemStack;
    }
}
