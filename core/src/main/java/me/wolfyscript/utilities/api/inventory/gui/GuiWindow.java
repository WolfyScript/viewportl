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

package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.compatibility.plugins.PlaceholderAPIIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.reflection.InventoryUpdate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * The GuiWindow represents an Inventory GUI in-game.
 * <p>
 * The {@link #onInit()} method is used for initialization of the buttons and other data required for the GUI.
 * </p>
 * <p>
 * The methods {@link #onUpdateSync(GuiUpdate)} and {@link #onUpdateAsync(GuiUpdate)} are used to render the window for specific players.
 * {@link GuiUpdate} contains all the required data, like which player it is, the cache of that player and more.
 * This way you can make the GUI contain the specific data.
 * See {@link GuiUpdate} for more information on how to render buttons etc.
 * </p>
 *
 * @param <C> The type of the {@link CustomCache}.
 */
public abstract class GuiWindow<C extends CustomCache> implements Listener, ButtonBuilder<C> {

    public final WolfyUtilities wolfyUtilities;
    protected final WindowButtonBuilder buttonBuilder;

    final HashMap<String, Button<C>> buttons = new HashMap<>();
    private final GuiCluster<C> cluster;
    private final NamespacedKey namespacedKey;
    private boolean forceSyncUpdate;
    private int titleUpdatePeriod = -1;
    private int titleUpdateDelay = 20;

    //Inventory
    private final InventoryType inventoryType;
    private final int size;

    /**
     * @param cluster The parent {@link GuiCluster} of this window.
     * @param key     The key for this window. This must be comply with the {@link NamespacedKey} key pattern!
     * @param size    The size of the window. Must be a multiple of 9 and less or equal to 54.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, int size) {
        this(cluster, key, size, false);
    }

    /**
     * @param cluster         The parent {@link GuiCluster} of this window.
     * @param key             The key for this window. This must be comply with the {@link NamespacedKey} key pattern!
     * @param size            The size of the window. Must be a multiple of 9 and less or equal to 54.
     * @param forceSyncUpdate If the window should only allow sync code and no async code.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, int size, boolean forceSyncUpdate) {
        this(cluster, key, null, size, forceSyncUpdate);
    }

    /**
     * @param cluster       The parent {@link GuiCluster} of this window.
     * @param key           The key for this window. This must be comply with the {@link NamespacedKey} key pattern!
     * @param inventoryType The type of the window.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType) {
        this(cluster, key, inventoryType, false);
    }

    /**
     * @param cluster         The parent {@link GuiCluster} of this window.
     * @param key             The key for this window. This must be comply with the {@link NamespacedKey} key pattern!
     * @param inventoryType   The type of the window.
     * @param forceSyncUpdate If the window should only allow sync code and no async code.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType, boolean forceSyncUpdate) {
        this(cluster, key, inventoryType, 0, forceSyncUpdate);
    }

    private GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType, int size, boolean forceSyncUpdate) {
        this.cluster = cluster;
        this.wolfyUtilities = cluster.wolfyUtilities;
        this.namespacedKey = new NamespacedKey(cluster.getId(), key);
        this.inventoryType = inventoryType;
        this.size = size;
        this.forceSyncUpdate = forceSyncUpdate;
        this.buttonBuilder = new WindowButtonBuilder();
        Bukkit.getPluginManager().registerEvents(this, wolfyUtilities.getPlugin());
    }

    /**
     * @return The {@link WolfyUtilities} that this window belongs to.
     */
    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    /**
     * @return The Chat of the API.
     */
    public Chat getChat() {
        return wolfyUtilities.getChat();
    }

    /**
     * @return The {@link InventoryType} of this window. Representing the type of the inventory.
     */
    public InventoryType getInventoryType() {
        return inventoryType;
    }

    /**
     * @return The size of this window, representing the size of the inventory.
     */
    public int getSize() {
        return size;
    }

    /**
     * This method is called when the inventory is initiated.
     * It's used to register Buttons and optionally other processing on start-up.
     */
    public abstract void onInit();

    /**
     * This method is called each time the gui is updated.
     *
     * @param update The {@link GuiUpdate} instance, that contains all the data of the action that caused this update.
     */
    public abstract void onUpdateSync(GuiUpdate<C> update);

    /**
     * Called each time the title of the window is updated.<br>
     * By default, that only happens once, when the player opens the inventory.<br>
     * Using {@link #setTitleUpdateDelay(int)} and {@link #setTitleUpdatePeriod(int)} you can set the frequency at which the title is updated.<br>
     * When enabled this will be called every specified period.
     *
     * @param originalTitle The original title from the language file.
     * @param inventory     The inventory instance, which title is updated. Null when the inventory is opened for the first time!
     * @param guiHandler    The handler that the inventory belongs to.
     * @return The new modified title. Color codes using & will be converted.
     */
    public String onUpdateTitle(String originalTitle, @Nullable GUIInventory<C> inventory, GuiHandler<C> guiHandler) {
        return originalTitle;
    }

    /**
     * This method is called after the {@link #onUpdateSync(GuiUpdate)} is done.
     * It will be run by the scheduler Async, so be careful with using Bukkit methods!
     * Bukkit methods are not Thread safe!
     * <p>
     * If {@link #isForceSyncUpdate()} is enabled then this method is forced to be updated sync too and will act just like {@link #onUpdateSync(GuiUpdate)}!
     *
     * @param update The {@link GuiUpdate} instance, that contains all the data of the action that caused this update.
     */
    public abstract void onUpdateAsync(GuiUpdate<C> update);

    /**
     * This method allows you to execute code when this window is closed and block players from closing the GUI.
     *
     * @param guiHandler   the gui handler that caused this close event.
     * @param guiInventory The {@link GUIInventory} that is being closed.
     * @param transaction  the inventory view of the player.
     * @return true if the gui close should be cancelled.
     */
    public boolean onClose(GuiHandler<C> guiHandler, GUIInventory<C> guiInventory, InventoryView transaction) {
        return false;
    }

    void create(GuiHandler<C> guiHandler) {
        update(null, guiHandler, null, null, true);
    }

    void update(GUIInventory<C> inventory, HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event) {
        update(inventory, inventory.getGuiHandler(), postExecuteBtns, event, false);
    }

    private void update(GUIInventory<C> inventory, GuiHandler<C> guiHandler, HashMap<Integer, Button<C>> postExecuteBtns, InventoryInteractEvent event, boolean openInventory) {
        Bukkit.getScheduler().runTask(guiHandler.getApi().getPlugin(), () -> {
            GuiUpdate<C> guiUpdate = new GuiUpdate<>(inventory, guiHandler, this);
            guiUpdate.postExecuteButtons(postExecuteBtns, event);
            callUpdate(guiHandler, guiUpdate, openInventory);
        });
    }

    private void callUpdate(GuiHandler<C> guiHandler, GuiUpdate<C> guiUpdate, boolean openInventory) {
        if (!guiHandler.isChatEventActive()) {
            onUpdateSync(guiUpdate);
            Runnable runnable = () -> openInventory(guiHandler, guiUpdate, openInventory);
            if (forceSyncUpdate) {
                runnable.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(wolfyUtilities.getPlugin(), runnable);
            }
        }
    }

    private void openInventory(GuiHandler<C> guiHandler, GuiUpdate<C> guiUpdate, boolean openInventory) {
        onUpdateAsync(guiUpdate);
        guiUpdate.applyChanges();
        if (openInventory) {
            Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> {
                var inv = guiUpdate.getInventory();
                guiHandler.setSwitchWindow(true);
                guiHandler.getPlayer().openInventory(inv);
                guiHandler.setSwitchWindow(false);
                if (titleUpdatePeriod > -1) {
                    guiHandler.setWindowUpdateTask(Bukkit.getScheduler().runTaskTimer(wolfyUtilities.getPlugin(), () -> {
                        var player = guiHandler.getPlayer();
                        if (player != null) {
                            String title = onUpdateTitle(getInventoryName(), inv, guiHandler);
                            PlaceholderAPIIntegration integration = wolfyUtilities.getCore().getCompatibilityManager().getPlugins().getIntegration("PlaceholderAPI", PlaceholderAPIIntegration.class);
                            if (integration != null) {
                                title = integration.setPlaceholders(player, integration.setBracketPlaceholders(player, title));
                            }
                            InventoryUpdate.updateInventory(wolfyUtilities.getCore(), player, ChatColor.convert(title));
                        }
                    }, titleUpdateDelay, titleUpdatePeriod));
                }
            });
        }
    }

    /**
     * The NamespacedKey consists of the namespace and key representing this window.
     * <br>
     * namespace: cluster key
     * <br>
     * key: window key.
     *
     * @return The NamespacedKey of this Window, consisting of the cluster key and this window key.
     */
    public final NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    /**
     * @return The parent {@link GuiCluster} of this window.
     */
    public final GuiCluster<C> getCluster() {
        return cluster;
    }

    /**
     * Register a Button to this window.
     * If the id is already in use it will replace the existing button with the new one.
     *
     * @param button The button to register.
     */
    public final void registerButton(Button<C> button) {
        button.init(this);
        buttons.put(button.getId(), button);
    }

    /**
     * @param id The id of the button.
     * @return The button if it exists, else null.
     */
    @Nullable
    public final Button<C> getButton(String id) {
        return buttons.get(id);
    }

    /**
     * @param id The id of the button.
     * @return If the button exists. True if it exists, else false.
     */
    public final boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    /**
     * Opens the chat, send the player the defined message and waits for the input of the player.
     * When the player sends a message the inputAction method is executed.
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param msg         The message that should be sent to the player.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     * @deprecated This uses the legacy chat format. {@link #openChat(GuiHandler, Component, ChatInputAction)} should be used instead.
     */
    @Deprecated
    public void openChat(GuiHandler<C> guiHandler, String msg, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendMessage(guiHandler.getPlayer(), msg);
    }


    /**
     * Opens the chat, send the player the defined message and waits for the input of the player.
     * When the player sends a message the inputAction method is executed.
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param msg         The message that should be sent to the player.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     */
    public void openChat(GuiHandler<C> guiHandler, Component msg, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    /**
     * Opens the chat, send the player the defined message, which is set inside of the language under "inventories.&#60;guiCluster&#62;.global_messages.&#60;msgKey&#62;"
     * Then it waits for the player's input.
     * When the player sends the message the inputAction method is executed.
     *
     * @param guiCluster  The {@link GuiCluster} of the message.
     * @param msgKey      The key of the message.
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     */
    public void openChat(GuiCluster<C> guiCluster, String msgKey, GuiHandler<C> guiHandler, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        var chat = wolfyUtilities.getChat();
        chat.sendMessage(guiHandler.getPlayer(), chat.translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey));
    }

    /**
     * Opens the chat, send the player the defined message, which is set inside of the language under "inventories.&#60;guiCluster&#62;.&#60;guiWindow&#62;.&#60;msgKey&#62;"
     * Then it waits for the player's input.
     * When the player sends the message the inputAction method is executed
     *
     * @param msgKey      The key of the message.
     * @param guiHandler  the {@link GuiHandler} it should be opened for.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     */
    public void openChat(String msgKey, GuiHandler<C> guiHandler, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendKey(guiHandler.getPlayer(), getNamespacedKey(), msgKey);
    }

    /**
     * Opens the chat, send the player the defined action messages and waits for the input of the player.
     * When the player sends the message the inputAction method is executed
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param clickData   The {@link ClickData} to be send to the player.
     * @param inputAction The {@link ChatInputAction} to be executed when the player types in the chat.
     * @deprecated This uses the legacy chat format. {@link #openChat(GuiHandler, Component, ChatInputAction)} should be used instead.
     */
    @Deprecated
    public void openActionChat(GuiHandler<C> guiHandler, ClickData clickData, ChatInputAction<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        guiHandler.getApi().getChat().sendActionMessage(guiHandler.getPlayer(), clickData);
    }

    /**
     * Send message to the player without closing the window.
     *
     * @param guiHandler The {@link GuiHandler} this message should be sent to.
     * @param msgKey     The key of the message.
     */
    public final void sendMessage(GuiHandler<C> guiHandler, String msgKey) {
        sendMessage(guiHandler.getPlayer(), msgKey);
    }

    /**
     * @param player The Player this message should be send to.
     * @param msgKey The key of the message.
     */
    public final void sendMessage(Player player, String msgKey) {
        wolfyUtilities.getChat().sendKey(player, getNamespacedKey(), msgKey);
    }

    /**
     * @param guiHandler   The {@link GuiHandler} that this message should be sent to.
     * @param msgKey       The key of the message.
     * @param replacements The replacement strings to replace specific strings with values.
     */
    @SafeVarargs
    @Deprecated
    public final void sendMessage(GuiHandler<C> guiHandler, String msgKey, Pair<String, String>... replacements) {
        wolfyUtilities.getChat().sendKey(guiHandler.getPlayer(), getNamespacedKey(), msgKey, replacements);
    }

    /**
     * @param player       The Player this message should be sent to.
     * @param msgKey       The key of the message.
     * @param replacements The replacement strings to replace specific strings with values.
     */
    @SafeVarargs
    @Deprecated
    public final void sendMessage(Player player, String msgKey, Pair<String, String>... replacements) {
        wolfyUtilities.getChat().sendKey(player, getNamespacedKey(), msgKey, replacements);
    }

    /**
     * @param guiHandler The {@link GuiHandler} that this message should be sent to.
     * @param msgKey     The key of the message.
     * @param templates  The placeholders and values to replace in the message.
     */
    public final void sendMessage(GuiHandler<C> guiHandler, String msgKey, List<Template> templates) {
        wolfyUtilities.getChat().sendKey(guiHandler.getPlayer(), getNamespacedKey(), msgKey, templates);
    }

    /**
     * @param player    The Player this message should be sent to.
     * @param msgKey    The key of the message.
     * @param templates The placeholders and values to replace in the message.
     */
    public final void sendMessage(Player player, String msgKey, List<Template> templates) {
        wolfyUtilities.getChat().sendKey(player, getNamespacedKey(), msgKey, templates);
    }

    /**
     * @return The inventory name of this Window.
     */
    protected String getInventoryName() {
        return ChatColor.convert(wolfyUtilities.getLanguageAPI().replaceKeys("$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_name$"));
    }

    /**
     * @return The help information of this window.
     */
    public List<String> getHelpInformation() {
        List<String> values = new ArrayList<>();
        for (String value : wolfyUtilities.getLanguageAPI().replaceKey("$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_help$")) {
            values.add(ChatColor.convert(value));
        }
        return values;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br>
     * It should be enabled when using {@link ItemInputButton}
     * to make sure that no item could be duplicated, because of tick lag!
     *
     * @return If the forced sync feature is enabled.
     */
    public boolean isForceSyncUpdate() {
        return forceSyncUpdate;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br>
     * It should be enabled when using {@link ItemInputButton}
     * to make sure that no item could be duplicated, because of tick lag!
     *
     * @param forceSyncUpdate New forced sync value.
     */
    public void setForceSyncUpdate(boolean forceSyncUpdate) {
        this.forceSyncUpdate = forceSyncUpdate;
    }

    /**
     * Sets the initial delay (in ticks), after which the title is updated.
     *
     * @param titleUpdateDelay The initial delay in ticks.
     */
    public void setTitleUpdateDelay(int titleUpdateDelay) {
        this.titleUpdateDelay = titleUpdateDelay;
    }

    /**
     * Gets the initial delay the update task waits after the inventory was opened.
     *
     * @return The initial delay in ticks.
     */
    public int getTitleUpdateDelay() {
        return titleUpdateDelay;
    }

    /**
     * Sets the period delay (in ticks). The title is updated each period.<br>
     * <b>This can cause flickering of the inventory! The shorter the period, the more noticeable!</b><br>
     * A period delay of -1 will completely disable the update task.
     *
     * @param titleUpdatePeriod The delay between each title update in ticks. Default: -1 = disabled.
     */
    public void setTitleUpdatePeriod(int titleUpdatePeriod) {
        this.titleUpdatePeriod = titleUpdatePeriod;
    }

    /**
     * Gets the current period delay between each title update.<br>
     * A period delay of -1 means that the update task is disabled.
     *
     * @return The delay between each title update. Default: -1
     */
    public int getTitleUpdatePeriod() {
        return titleUpdatePeriod;
    }

    public ButtonBuilder<C> getButtonBuilder() {
        return buttonBuilder;
    }

    public class WindowButtonBuilder implements ButtonBuilder<C> {

        @Override
        public ChatInputButton.Builder<C> chatInput(String id) {
            return new ChatInputButton.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ActionButton.Builder<C> action(String id) {
            return new ActionButton.Builder<>(GuiWindow.this, id);
        }

        @Override
        public DummyButton.Builder<C> dummy(String id) {
            return new DummyButton.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ItemInputButton.Builder<C> itemInput(String id) {
            return new ItemInputButton.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ToggleButton.Builder<C> toggle(String id) {
            return new ToggleButton.Builder<>(GuiWindow.this, id);
        }
    }

}
