/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.inventory.gui.button;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public abstract class Button<C extends CustomCache> {

    private final String id;
    private final ButtonType type;

    /**
     * Creates a Button with the specified id and type.
     *
     * @param id   The id of the button. Must be unique in the window or cluster.
     * @param type The type of the button. Default: {@link ButtonType#NORMAL}
     */
    protected Button(String id, ButtonType type) {
        Preconditions.checkArgument(id != null && !id.isBlank(), "Button id cannot be null or empty!");
        this.id = id;
        this.type = type == null ? ButtonType.NORMAL : type;
    }

    /**
     * Creates a Button with the specified id.
     *
     * @param id The id of the button. Must be unique in the window or cluster.
     */
    protected Button(String id) {
        this(id, ButtonType.NORMAL);
    }

    /**
     * Called when registered locally inside the {@link GuiWindow#onInit()}.
     *
     * @param guiWindow The {@link GuiWindow} this button is registered in.
     */
    public abstract void init(GuiWindow<C> guiWindow);

    /**
     * Called when registered globally inside the {@link GuiCluster#onInit()}
     *
     * @param guiCluster The {@link GuiCluster} this button is registered in.
     */
    public abstract void init(GuiCluster<C> guiCluster);

    public abstract boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException;

    public abstract void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException;

    public abstract void preRender(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean help);

    public abstract void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) throws IOException;

    @NotNull
    public ButtonType getType() {
        return type;
    }

    @NotNull
    public String getId() {
        return id;
    }

    protected void applyItem(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ButtonState<C> state, ItemStack item, int slot, boolean help) {
        var window = guiHandler.getWindow();
        List<Template> templates = new LinkedList<>();
        if (state.getRenderAction() instanceof CallbackButtonRender<C> renderTemplates) {
            //No longer set default templates, that should be purely managed by the plugin.
            item = renderTemplates.render(templates, guiHandler.getCustomCache(), guiHandler, player, guiInventory, item, slot);
            item = ItemUtils.applyNameAndLore(item, state.getName(window, templates), state.getLore(window, templates));
        } else {
            //Using the legacy placeholder system, with backwards compatibility of the new system.
            HashMap<String, Object> values = new HashMap<>();
            values.put("%plugin.version%", guiHandler.getApi().getPlugin().getDescription().getVersion());
            if (guiHandler.getWindow() != null) {
                values.put("%wolfyutilities.help%", guiHandler.getWindow().getHelpInformation());
            }
            if (state.getRenderAction() != null) {
                item = state.getRenderAction().render(values, guiHandler.getCustomCache(), guiHandler, player, guiInventory, item, slot, help);
            }
            //Backwards compatibility of the new system.
            templates = values.entrySet().stream().map(entry -> {
                var key = entry.getKey();
                var value = entry.getValue();
                if (value instanceof String text) {
                    return Template.of(key, text);
                } else if (value instanceof List<?> list && !list.isEmpty()) {
                    Component component = Component.empty();
                    for (Object object : list) {
                        if (object instanceof Component component1) {
                            component.append(component1);
                        } else {
                            component.append(BukkitComponentSerializer.legacy().deserialize(String.valueOf(object)));
                        }
                    }
                    return Template.of(key, component);
                } else if (value instanceof Component component) {
                    return Template.of(key, component);
                }
                return null;
            }).filter(Objects::nonNull).toList();
            item = ItemUtils.applyNameAndLore(item, state.getName(window, templates), state.getLore(window, templates));
            //Legacy key replacements.
            item = replaceKeysWithValue(item, values);
        }
        inventory.setItem(slot, item);
    }

    protected void applyItem(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ButtonState<C> state, int slot, boolean help) {
        applyItem(guiHandler, player, guiInventory, inventory, state, state.getIcon(), slot, help);
    }

    /**
     * Legacy method to replace placeholders in the ItemStack lore and name.
     *
     * @param itemStack The ItemStack
     * @param values The placeholder keys and values.
     * @return The ItemStack with replaced lore and name.
     */
    @Deprecated
    protected ItemStack replaceKeysWithValue(ItemStack itemStack, Map<String, Object> values) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String name = meta.getDisplayName();
                List<String> lore = meta.getLore();
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (entry.getValue() instanceof List<?> list) {
                        if (lore != null) {
                            for (int i = 0; i < lore.size(); i++) {
                                if (!lore.get(i).contains(entry.getKey())) continue;
                                lore.set(i, !list.isEmpty() ? lore.get(i).replace(entry.getKey(), ChatColor.convert(String.valueOf(list.get(list.size() - 1)))) : "");
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

    public static abstract class Builder<C extends CustomCache, B extends Button<C>, T extends Builder<C, B, T>> {

        protected final Class<B> buttonType;
        protected final Class<T> builderType;
        protected final WolfyUtilities api;
        protected final InventoryAPI<C> invApi;
        protected GuiWindow<C> window;
        protected GuiCluster<C> cluster;
        protected final String key;

        protected Builder(GuiWindow<C> window, String key, Class<B> buttonType) {
            this(window.getCluster().getInventoryAPI(), window.getWolfyUtilities(), key, buttonType);
            this.window = window;
        }

        protected Builder(GuiCluster<C> cluster, String key, Class<B> buttonType) {
            this(cluster.getInventoryAPI(), cluster.getWolfyUtilities(), key, buttonType);
            this.cluster = cluster;
        }

        private Builder(InventoryAPI<C> invApi, WolfyUtilities api, String key, Class<B> buttonType) {
            this.api = api;
            this.invApi = invApi;
            this.buttonType = buttonType;
            this.key = key;
            this.builderType = (Class<T>) getClass();
        }

        public abstract B create();

        protected T inst() {
            return builderType.cast(this);
        }

    }
}
