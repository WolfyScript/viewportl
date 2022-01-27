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

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * ButtonState represents the state of a Button.
 * <br>
 * It contains the ItemStack and language keys required to render the Item in the inventory.
 * <p>
 * The rendering can be manipulated using the {@link ButtonRender} method that returns the ItemStack that will be rendered.
 * <p>
 * To execute code on a Button click you need to use the {@link ButtonAction} method, which is called each time the button is clicked.
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonState<C extends CustomCache> {

    public static final String NAME_KEY = ".name";
    public static final String LORE_KEY = ".lore";
    public static final String BUTTON_WINDOW_KEY = "inventories.%s.%s.items.%s";
    public static final String BUTTON_CLUSTER_KEY = "inventories.%s.global_items.%s";

    private WolfyUtilities wolfyUtilities;
    private String clusterID;
    private String key;
    private final ItemStack presetIcon;
    private ItemStack icon;
    private ButtonAction<C> action;
    private ButtonRender<C> buttonRender;
    private ButtonPreRender<C> prepareRender;
    private ButtonPostAction<C> postAction;

    /**
     * Creates a new ButtonState of the specified key and icon.
     *
     * @param key  The key of the button.
     * @param icon The preset ItemStack, that is used as the icon.
     * @param <C>  The type of cache.
     * @return A new instance of the ButtonState.
     */
    public static <C extends CustomCache> ButtonState<C> of(String key, ItemStack icon) {
        return new ButtonState<>(key, icon);
    }

    /**
     * Creates a new ButtonState of the specified key and icon.
     *
     * @param key  The key of the button.
     * @param icon The icon type of the button.
     * @param <C>  The type of cache.
     * @return A new instance of the ButtonState.
     */
    public static <C extends CustomCache> ButtonState<C> of(String key, Material icon) {
        return new ButtonState<>(key, icon);
    }

    /**
     * Creates a new ButtonState with a global button key and icon.
     *
     * @param key  The key of the button. namespace = cluster, key = button
     * @param icon The preset ItemStack, that is used as the icon.
     * @param <C>  The type of cache.
     * @return A new instance of the ButtonState.
     */
    public static <C extends CustomCache> ButtonState<C> of(NamespacedKey key, ItemStack icon) {
        return new ButtonState<>(key, icon);
    }

    /**
     * Creates a new ButtonState with a global button key and icon.
     *
     * @param key
     * @param icon
     * @param <C>
     * @return A new instance of the ButtonState.
     */
    public static <C extends CustomCache> ButtonState<C> of(NamespacedKey key, Material icon) {
        return new ButtonState<>(key, icon);
    }

    /**
     * Sets the action callback, that is called when the button is clicked.
     *
     * @param action The action callback.
     * @return This button state for chaining.
     */
    public ButtonState<C> with(@Nullable ButtonAction<C> action) {
        this.action = action;
        return this;
    }

    /**
     * Sets the render callback, that is called when the button is rendered.
     *
     * @param buttonRender The render callback.
     * @return This button state for chaining.
     */
    public ButtonState<C> with(@Nullable ButtonRender<C> buttonRender) {
        this.buttonRender = buttonRender;
        return this;
    }

    /**
     * Sets the render callback, that is called right before the button is rendered.
     *
     * @param prepareRender The pre-render callback.
     * @return This button state for chaining.
     */
    public ButtonState<C> with(@Nullable ButtonPreRender<C> prepareRender) {
        this.prepareRender = prepareRender;
        return this;
    }

    /**
     * Sets the action callback, that is called 1 tick after the button was clicked.
     *
     * @param postAction The post-action callback.
     * @return This button state for chaining.
     */
    public ButtonState<C> with(@Nullable ButtonPostAction<C> postAction) {
        this.postAction = postAction;
        return this;
    }

    public ButtonState(String key, ItemStack presetIcon) {
        this(key, presetIcon, null, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this.key = key;
        this.clusterID = null;
        this.presetIcon = presetIcon;
        this.action = action;
        this.postAction = postAction;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
    }

    /**
     * Constructors with Key and, Materials instead of ItemStacks
     */
    public ButtonState(String key, Material presetIcon) {
        this(key, presetIcon, null, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, Material)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, Material)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, Material)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, Material)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(String, Material)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), action, postAction, prepareRender, render);
    }

    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon) {
        this(buttonKey, presetIcon, null, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action) {
        this(buttonKey, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, ItemStack)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this.action = action;
        this.postAction = postAction;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
        this.presetIcon = presetIcon;
        this.clusterID = buttonKey.getNamespace();
        this.key = buttonKey.getKey();
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon) {
        this(buttonKey, presetIcon, null, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action) {
        this(buttonKey, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link #of(NamespacedKey, Material)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(buttonKey, new ItemStack(presetIcon), action, postAction, prepareRender, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon) {
        this(clusterID, key, presetIcon, null, null);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, null, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack, ButtonAction)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon, @Nullable ButtonAction<C> action) {
        this(clusterID, key, presetIcon, action, null);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack, ButtonAction, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack, ButtonAction, ButtonPreRender, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, ItemStack, ButtonAction, ButtonPostAction, ButtonPreRender, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this.action = action;
        this.postAction = postAction;
        this.prepareRender = prepareRender;
        this.buttonRender = render;
        this.presetIcon = presetIcon;
        this.clusterID = clusterID;
        this.key = key;
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon) {
        this(clusterID, key, presetIcon, null, null);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, null, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material, ButtonAction)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon, @Nullable ButtonAction<C> action) {
        this(clusterID, key, presetIcon, action, null);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material, ButtonAction, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material, ButtonAction, ButtonPreRender, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(clusterID, key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Use {@link #ButtonState(NamespacedKey, Material, ButtonAction, ButtonPostAction, ButtonPreRender, ButtonRender)} instead!
     */
    @Deprecated
    public ButtonState(String clusterID, String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(clusterID, key, new ItemStack(presetIcon), action, postAction, prepareRender, render);
    }

    /*
    Not linked to language file
     */
    @Deprecated
    public ButtonState(ItemStack presetIcon, String displayName, String[] normalLore, ButtonAction<C> action) {
        this.action = action;
        this.presetIcon = presetIcon;
        this.icon = ItemUtils.createItem(presetIcon, displayName, normalLore);
    }

    @Deprecated
    public ButtonState(Material presetIcon, String displayName, String[] normalLore, ButtonAction<C> action) {
        this(new ItemStack(presetIcon), displayName, normalLore, action);
    }

    public void init(GuiCluster<C> cluster) {
        this.wolfyUtilities = cluster.getWolfyUtilities();
        //For backwards compatibility!
        if (this.clusterID == null) {
            this.clusterID = cluster.getId();
        }
        createIcon(null);
    }

    public void init(GuiWindow<C> window) {
        this.wolfyUtilities = window.wolfyUtilities;
        createIcon(window);
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    private void createIcon(GuiWindow<C> window) {
        if (key != null && !key.isEmpty()) {
            this.icon = ItemUtils.createItem(presetIcon, getName(window), getLore(window));
        }
    }

    public Component getName(GuiWindow<C> window) {
        return getName(window, List.of());
    }

    public Component getName(GuiWindow<C> window, List<Template> templates) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_CLUSTER_KEY + NAME_KEY, clusterID, key), true, templates);
        }
        return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_WINDOW_KEY + NAME_KEY, window.getNamespacedKey().getNamespace(), window.getNamespacedKey().getKey(), key), true, templates);
    }

    public List<Component> getLore(GuiWindow<C> window) {
        return getLore(window, List.of());
    }

    public List<Component> getLore(GuiWindow<C> window, List<Template> templates) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_CLUSTER_KEY + LORE_KEY, clusterID, key), true, templates);
        }
        return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_WINDOW_KEY + LORE_KEY, window.getNamespacedKey().getNamespace(), window.getNamespacedKey().getKey(), key), true, templates);
    }

    public ButtonAction<C> getAction() {
        return action;
    }

    public ButtonState<C> setAction(ButtonAction<C> action) {
        this.action = action;
        return this;
    }

    public ButtonRender<C> getRenderAction() {
        return buttonRender;
    }

    public ButtonState<C> setRenderAction(ButtonRender<C> renderAction) {
        this.buttonRender = renderAction;
        return this;
    }

    public ButtonPreRender<C> getPrepareRender() {
        return prepareRender;
    }

    public ButtonState<C> setPrepareRender(ButtonPreRender<C> prepareRender) {
        this.prepareRender = prepareRender;
        return this;
    }

    public ButtonPostAction<C> getPostAction() {
        return postAction;
    }

    public ButtonState<C> setPostAction(ButtonPostAction<C> postAction) {
        this.postAction = postAction;
        return this;
    }
}
