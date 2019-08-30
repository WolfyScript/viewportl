package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
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

    protected ItemStack replaceKeysWithValue(ItemStack itemStack, HashMap<String, Object> values){
        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null && meta.hasDisplayName()){
            String name = meta.getDisplayName();
            List<String> lore = meta.getLore();
            for(Map.Entry<String, Object> entry : values.entrySet()){
                name = name.replace(entry.getKey(), String.valueOf(entry.getValue()));
                if (meta.hasLore()) {
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, lore.get(i).replace(entry.getKey(), String.valueOf(entry.getValue())));
                    }
                }
            }
            meta.setDisplayName(name);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
