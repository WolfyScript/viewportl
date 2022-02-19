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
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

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

    public static <C extends CustomCache> ButtonState.Builder<C> of(GuiWindow<C> window, String key) {
        return new Builder<>(window, key);
    }

    public static <C extends CustomCache> ButtonState.Builder<C> of(GuiCluster<C> cluster, String key) {
        return new Builder<>(cluster, key);
    }

    @Deprecated
    public ButtonState(String key, ItemStack presetIcon) {
        Preconditions.checkArgument(key != null && !key.isBlank(), "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = key;
        this.clusterID = null;
        this.presetIcon = presetIcon;
    }

    @Deprecated
    public ButtonState(String key, Material presetIcon) {
        this(key, new ItemStack(presetIcon));
    }

    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon) {
        Preconditions.checkArgument(buttonKey != null, "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = buttonKey.getKey();
        this.clusterID = buttonKey.getNamespace();
        this.presetIcon = presetIcon;
    }

    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon) {
        this(buttonKey, new ItemStack(presetIcon));
    }

    /*
     * The deprecated constructor should no longer be used.
     * They are collapsed, so they are not in the way.
     */
    //region Deprecated constructors

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
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
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action) {
        this(key, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(String key, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(key, new ItemStack(presetIcon), action, postAction, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action) {
        this(buttonKey, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
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
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action) {
        this(buttonKey, presetIcon, action, null);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
     */
    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon, @Nullable ButtonAction<C> action, @Nullable ButtonPreRender<C> prepareRender, @Nullable ButtonRender<C> render) {
        this(buttonKey, presetIcon, action, null, prepareRender, render);
    }

    /**
     * @deprecated Old usage! Use the {@link Builder} via {@link #of(GuiWindow, String)} or {@link #of(GuiCluster, String)} instead.
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
    //endregion

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

    @Deprecated
    public ButtonState<C> setAction(ButtonAction<C> action) {
        this.action = action;
        return this;
    }

    public ButtonRender<C> getRenderAction() {
        return buttonRender;
    }

    @Deprecated
    public ButtonState<C> setRenderAction(ButtonRender<C> renderAction) {
        this.buttonRender = renderAction;
        return this;
    }

    public ButtonPreRender<C> getPrepareRender() {
        return prepareRender;
    }

    @Deprecated
    public ButtonState<C> setPrepareRender(ButtonPreRender<C> prepareRender) {
        this.prepareRender = prepareRender;
        return this;
    }

    public ButtonPostAction<C> getPostAction() {
        return postAction;
    }

    @Deprecated
    public ButtonState<C> setPostAction(ButtonPostAction<C> postAction) {
        this.postAction = postAction;
        return this;
    }

    /**
     * The builder provides an easy solution to create ButtonStates.<br>
     * <p>
     * You can get an instance of this builder via {@link ButtonState#of(GuiWindow, String)} or {@link ButtonState#of(GuiCluster, String)}.<br>
     * It can also be accessed via the button builders:
     * <ul>
     *     <li>{@linkplain me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton.Builder#state(Consumer)}</li>
     *     <li>{@linkplain  me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton.Builder#enabledState(Consumer)} or {@linkplain me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton.Builder#disabledState(Consumer)}</li>
     *     <li>{@linkplain me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton.Builder#addState(Consumer)}</li>
     * </ul>
     * When the instance is provided via the Button builder, then the default key is equal to the button key.
     * </p>
     *
     * @param <C> The CustomCache type
     */
    public static class Builder<C extends CustomCache> {

        private final WolfyUtilities api;
        private final InventoryAPI<C> invApi;
        private GuiWindow<C> window;
        private GuiCluster<C> cluster;
        private String key;
        private ItemStack icon;
        private ButtonAction<C> action;
        private ButtonRender<C> render;
        private ButtonPreRender<C> preRender;
        private ButtonPostAction<C> postAction;

        private Builder(GuiWindow<C> window, String key) {
            this(window.getCluster().getInventoryAPI(), window.getWolfyUtilities(), key);
            this.window = window;
        }

        private Builder(GuiCluster<C> cluster, String key) {
            this(cluster.getInventoryAPI(), cluster.getWolfyUtilities(), key);
            this.cluster = cluster;
        }

        private Builder(InventoryAPI<C> invApi, WolfyUtilities api, String key) {
            this.api = api;
            this.invApi = invApi;
            this.key = key;
        }

        /**
         * Sets the cluster of the ButtonState.<br>
         * This overrides the previous cluster or window.<br>
         *
         * That can be useful to make use of states from global cluster buttons inside the {@link GuiWindow}.
         *
         * @param cluster The cluster to switch to.
         * @return This button state builder for chaining.
         */
        public Builder<C> cluster(GuiCluster<C> cluster) {
            this.cluster = cluster;
            return this;
        }

        /**
         * Sets the key of the ButtonState (The location in the language where it fetches the name & lore from).
         *
         * @param key The key of the state.
         * @return This button state builder for chaining.
         */
        public Builder<C> key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the key of the ButtonState (The location in the language where it fetches the name & lore from).<br>
         * The NamespacedKey must be from a button in a {@link GuiCluster}. <br>
         * The namespace is equal to the id of the cluster, and the key is equal to the button id.<br>
         *
         * @throws IllegalArgumentException if there is no cluster for the specified namespace.
         * @param buttonKey The namespaced key of the button.
         * @return This button state builder for chaining.
         */
        public Builder<C> key(NamespacedKey buttonKey) {
            String clusterID = buttonKey.getNamespace();
            this.cluster = invApi.getGuiCluster(clusterID);
            Preconditions.checkArgument(this.cluster != null, "Error setting key of ButtonState: Cluster \"" + clusterID + "\" does not exist!");
            this.key(buttonKey.getKey());
            return this;
        }

        /**
         * Appends a sub-key to the end of the current key separated by a '.'.
         * <p>e.g.:
         * <code><pre>ButtonState.of(window, "button_id").subKey("enabled").create();<br></pre></code>
         * (key == "button_id.enabled")
         * </p><br><br>
         * This is useful for multi state buttons, that all have a parent language node with sub nodes for the different states.
         * @param subKey The sub-key to append to the current key.
         * @return This button state builder for chaining.
         */
        public Builder<C> subKey(String subKey) {
            this.key += "." + subKey;
            return this;
        }

        /**
         * Sets the icon of the ButtonState.
         *
         * @param icon The material to use as the icon.
         * @return This button state builder for chaining.
         */
        public Builder<C> icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon of the ButtonState.
         *
         * @param icon The ItemStack to use as the icon.
         * @return This button state builder for chaining.
         */
        public Builder<C> icon(Material icon) {
            this.icon = new ItemStack(icon);
            return this;
        }

        /**
         * Sets the action callback, that is called when the button is clicked.
         *
         * @param action The action callback.
         * @return This button state for chaining.
         */
        public Builder<C> action(@Nullable ButtonAction<C> action) {
            this.action = action;
            return this;
        }

        /**
         * Sets the render callback, that is called when the button is rendered.
         *
         * @param buttonRender The render callback.
         * @return This button state for chaining.
         */
        public Builder<C> render(@Nullable ButtonRender<C> buttonRender) {
            this.render = buttonRender;
            return this;
        }

        /**
         * Sets the render callback, that is called right before the button is rendered.
         *
         * @param prepareRender The pre-render callback.
         * @return This button state for chaining.
         */
        public Builder<C> preRender(@Nullable ButtonPreRender<C> prepareRender) {
            this.preRender = prepareRender;
            return this;
        }

        /**
         * Sets the action callback, that is called 1 tick after the button was clicked.
         *
         * @param postAction The post-action callback.
         * @return This button state for chaining.
         */
        public Builder<C> postAction(@Nullable ButtonPostAction<C> postAction) {
            this.postAction = postAction;
            return this;
        }

        /**
         * Creates a ButtonState with the previously configured settings.
         *
         * @return A new ButtonState instance with the configured settings.
         */
        public ButtonState<C> create() {
            ButtonState<C> state;
            if (cluster == null) {
                state = new ButtonState<>(key, icon);
            } else {
                state = new ButtonState<>(new NamespacedKey(cluster.getId(), key), icon);
            }
            state.prepareRender = preRender;
            state.buttonRender = render;
            state.action = action;
            state.postAction = postAction;
            if (cluster != null) {
                state.init(cluster);
            } else {
                state.init(window);
            }
            return state;
        }

    }
}
