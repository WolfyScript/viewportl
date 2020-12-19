package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Button<C extends CustomCache> {

    private final String id;
    private final ButtonType type;

    public Button(String id, ButtonType type) {
        this.id = id;
        this.type = type;
    }

    public Button(String id) {
        this(id, ButtonType.NORMAL);
    }

    public abstract void init(GuiWindow<C> guiWindow);

    public abstract void init(String clusterID, WolfyUtilities api);

    public abstract boolean execute(GuiHandler<C> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) throws IOException;

    public abstract void postExecute(GuiHandler<C> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException;

    public abstract void prepareRender(GuiHandler<C> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, boolean help);

    public abstract void render(GuiHandler<C> guiHandler, Player player, Inventory inventory, int slot, boolean help) throws IOException;

    public ButtonType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    protected void applyItem(GuiHandler<C> guiHandler, Player player, Inventory inventory, ButtonState<C> state, int slot, boolean help) {
        ItemStack item = state.getIcon();
        HashMap<String, Object> values = new HashMap<>();
        values.put("%wolfyutilities.help%", guiHandler.getWindow().getHelpInformation());
        values.put("%plugin.version%", guiHandler.getApi().getPlugin().getDescription().getVersion());
        if (state.getRenderAction() != null) {
            item = state.getRenderAction().render(values, guiHandler, player, item, slot, help);
        }
        inventory.setItem(slot, replaceKeysWithValue(item, values));
    }

    protected ItemStack replaceKeysWithValue(ItemStack itemStack, HashMap<String, Object> values) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String name = meta.getDisplayName();
                List<String> lore = meta.getLore();
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        List<Object> list = (List<Object>) entry.getValue();
                        if (meta.hasLore()) {
                            for (int i = 0; i < lore.size(); i++) {
                                if (!lore.get(i).contains(entry.getKey())) continue;
                                lore.set(i, list.size() > 0 ? lore.get(i).replace(entry.getKey(), ChatColor.convert(String.valueOf(list.get(list.size() - 1)))) : "");
                                if (list.size() > 1) {
                                    for (int j = list.size() - 2; j >= 0; j--) {
                                        lore.add(i, ChatColor.convert(String.valueOf(list.get(j))));
                                    }
                                }
                            }
                        }
                    } else if (entry.getValue() != null) {
                        name = name.replace(entry.getKey(), ChatColor.convert(String.valueOf(entry.getValue())));
                        if (lore != null && !lore.isEmpty()) {
                            lore = lore.stream().map(s -> s.replace(entry.getKey(), ChatColor.convert(String.valueOf(entry.getValue())))).collect(Collectors.toList());
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
