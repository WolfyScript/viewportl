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

import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This object is used to store all relevant data for the Player using the GUI.<br>
 * The object persists across player disconnecting/rejoining, so that the state of the GUIs and cache are persistent as long as the server is running.
 * <br>
 * <br>
 * <strong>Data it stores per player:</strong>
 * <ul>
 *     <li>{@link CustomCache}</li>
 *     <li>Active {@link GuiCluster}</li>
 *     <li>The GUI history</li>
 *     <li>Possible active {@link ChatInputAction} and {@link ChatTabComplete}</li>
 *     <li>Other necessary data.</li>
 * </ul>
 * <br>
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class GuiHandler<C extends CustomCache> implements Listener {

    private final WolfyUtilities api;
    private final InventoryAPI<C> invAPI;
    private final UUID uuid;
    private final Map<GuiCluster<C>, List<GuiWindow<C>>> clusterHistory;
    private ChatInputAction<C> chatInputAction = null;
    private ChatTabComplete<C> chatTabComplete = null;
    private GuiCluster<C> cluster = null;
    private boolean isWindowOpen = false;
    private boolean helpEnabled = false;
    private boolean switchWindow = false;
    boolean openedPreviousWindow = false;

    private final C customCache;

    public GuiHandler(Player player, WolfyUtilities api, InventoryAPI<C> invAPI, C customCache) {
        this.api = api;
        this.invAPI = invAPI;
        this.uuid = player.getUniqueId();
        this.customCache = customCache;
        this.clusterHistory = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    /**
     * Used internally to prevent issues when switching to another window inventory.
     * @param switchWindow If it currently switches to another window.
     */
    void setSwitchWindow(boolean switchWindow) {
        this.switchWindow = switchWindow;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public InventoryAPI<C> getInvAPI() {
        return invAPI;
    }

    /**
     * This method only returns null if the player is offline or not found!<br>
     * If called directly in {@link GuiWindow#onUpdateSync(GuiUpdate)}, {@link GuiWindow#onUpdateAsync(GuiUpdate)}, {@link ButtonAction}, {@link ButtonRender}, etc. the player should always be available.<br>
     * <strong>However, if called a few ticks later or in a scheduler, the returned value might be null, as the player might have disconnected.</strong>
     *
     * @return The active player of this handler, or null if the players is not found/offline.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     *
     * @return If the player is available. True if available, else false.
     * @see #getPlayer() More info about the player availability.
     */
    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    /**
     * @return The active {@link GuiCluster}
     */
    public GuiCluster<C> getCluster() {
        return cluster;
    }

    /**
     * Changes the current active {@link GuiCluster}
     * @param currentGuiCluster The new active cluster.
     * @deprecated This method should not be used, as it causes issues with the active {@link GuiWindow}s, etc.! Instead, make use of {@link #openCluster(String)} or {@link #openCluster(GuiCluster)}!
     */
    @Deprecated(forRemoval = true)
    public void setCluster(GuiCluster<C> currentGuiCluster) {
        this.cluster = currentGuiCluster;
    }

    /**
     * @return If the {@link GuiWindow} is open.
     */
    public boolean isWindowOpen() {
        return isWindowOpen;
    }

    /**
     * Reloads the specified {@link GuiWindow}<br>
     * This will remove the latest window in history and reopen the specified {@link GuiWindow}.<br>
     *
     * @param windowKey The {@link NamespacedKey} of the window, to reload and replace the active window with.
     */
    public void reloadWindow(NamespacedKey windowKey) {
        List<GuiWindow<C>> history = getHistory(invAPI.getGuiCluster(windowKey.getNamespace()));
        history.remove(history.get(0));
        openWindow(windowKey);
    }

    /**
     * @param clusterId The id of the cluster to get the active window for.
     * @return The active {@link GuiWindow} of this handler for the specified {@link GuiCluster}; Null if there isn't any active window.
     */
    public GuiWindow<C> getWindow(String clusterId) {
        return getWindow(invAPI.getGuiCluster(clusterId));
    }

    /**
     * @param cluster The cluster to get the active window for.
     * @return The active {@link GuiWindow} of this handler for the specified {@link GuiCluster}; Null if there isn't any active window.
     */
    @Nullable
    public GuiWindow<C> getWindow(GuiCluster<C> cluster) {
        List<GuiWindow<C>> history = getHistory(cluster);
        return !history.isEmpty() ? invAPI.getGuiWindow(history.get(0).getNamespacedKey()) : null;
    }

    /**
     * @return The active {@link GuiWindow} of this handler; Null if there isn't any active window.
     */
    @Nullable
    public GuiWindow<C> getWindow() {
        return getWindow(getCluster());
    }

    /**
     * @param clusterId The id of the {@link GuiCluster}. (See {@linkplain GuiCluster#getId()})
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(String clusterId) {
        return getPreviousWindow(invAPI.getGuiCluster(clusterId));
    }

    /**
     * @param cluster The {@link GuiCluster}
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(GuiCluster<C> cluster) {
        return getPreviousWindow(cluster, 1);
    }

    /**
     * @param clusterID The id of the cluster.
     * @param stepsBack The amount of steps to go back in history.
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(String clusterID, int stepsBack) {
        return getPreviousWindow(invAPI.getGuiCluster(clusterID), stepsBack);
    }

    /**
     * @param cluster The {@link GuiCluster}
     * @param stepsBack The amount of steps to go back in history.
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(GuiCluster<C> cluster, int stepsBack) {
        List<GuiWindow<C>> history = getHistory(cluster);
        if (stepsBack < history.size()) {
            return invAPI.getGuiWindow(history.get(stepsBack).getNamespacedKey());
        }
        return null;
    }

    /**
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow() {
        return getPreviousWindow(getCluster());
    }

    /**
     * @param stepsBack The amount of steps to go back in history.
     * @return The previous {@link GuiWindow} in history that was opened, or null if there isn't one.
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(int stepsBack) {
        return getPreviousWindow(getCluster(), stepsBack);
    }

    public void openPreviousWindow() {
        openPreviousWindow(getCluster());
    }

    public void openPreviousWindow(String clusterID) {
        openPreviousWindow(invAPI.getGuiCluster(clusterID));
    }

    public void openPreviousWindow(GuiCluster<C> cluster) {
        openPreviousWindow(cluster, 1);
    }

    public void openPreviousWindow(int stepsBack) {
        openPreviousWindow(getCluster(), stepsBack);
    }

    public void openPreviousWindow(String clusterID, int stepsBack) {
        openPreviousWindow(invAPI.getGuiCluster(clusterID), stepsBack);
    }

    public void openPreviousWindow(GuiCluster<C> cluster, int stepsBack) {
        openedPreviousWindow = true;
        List<GuiWindow<C>> history = getHistory(cluster);
        if (stepsBack < history.size()) {
            if (stepsBack > 0) {
                history.subList(0, stepsBack).clear();
            }
            openWindow(history.get(0).getNamespacedKey());
        } else {
            history.clear();
            openCluster(cluster);
        }
    }

    public Map<GuiCluster<C>, List<GuiWindow<C>>> getClusterHistory() {
        return clusterHistory;
    }

    /**
     * @param cluster The {@link GuiCluster} to get the history for.
     * @return A list of the {@link GuiCluster} history, or an empty list if non-existing.
     */
    public List<GuiWindow<C>> getHistory(GuiCluster<C> cluster) {
        return clusterHistory.computeIfAbsent(cluster, c -> new ArrayList<>());
    }

    private void setHistory(GuiCluster<C> cluster, List<GuiWindow<C>> history) {
        clusterHistory.put(cluster, history);
    }

    /**
     * Opens the {@link GuiWindow} of the specified key.<br>
     * This method will use the current active {@link GuiCluster#getId()}. Make sure the targeted {@link GuiWindow} is part of that cluster!
     *
     * @param windowKey The key of the {@link GuiWindow}.
     */
    public void openWindow(String windowKey) {
        openWindow(new NamespacedKey(getCluster().getId(), windowKey));
    }

    /**
     * Opens the specified {@link GuiWindow} of the specified key.<br>
     * This key must be the same or similar to the one from {@link GuiWindow#getNamespacedKey()}.
     *
     * @param windowNamespaceKey The key of the {@link GuiWindow}.
     */
    public void openWindow(@NotNull NamespacedKey windowNamespaceKey) {
        openWindow(invAPI.getGuiWindow(windowNamespaceKey));
    }

    /**
     * Opens the {@link GuiWindow} for this GuiHandler.
     *
     * @param window The {@link GuiWindow} to open.
     */
    public void openWindow(GuiWindow<C> window) {
        if (getPlayer() == null) {
            isWindowOpen = false;
            return;
        }
        if (window == null) {
            getPlayer().closeInventory();
            isWindowOpen = false;
            return;
        }
        final GuiCluster<C> cluster = window.getCluster();
        Player player1 = getPlayer();
        if (api.getPermissions().hasPermission(player1, (api.getPlugin().getName() + ".inv." + window.getNamespacedKey().toString(".")))) {
            var currentWindow = getWindow(cluster);
            if (currentWindow == null || !currentWindow.getNamespacedKey().equals(window.getNamespacedKey())) {
                getHistory(cluster).add(0, window);
            }
            this.cluster = cluster;
            isWindowOpen = true;
            window.create(this);
            return;
        }
        api.getChat().sendMessage(player1, "§4You don't have the permission §c" + (api.getPlugin().getName() + ".inv." + window.getNamespacedKey().toString(".")));
    }

    /**
     * Opens the current GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster() {
        openCluster(getCluster());
    }

    /**
     * Opens the GuiCluster with the latest GuiWindow that was open.
     *
     * @param clusterID The cluster key of the cluster to open.
     */
    public void openCluster(String clusterID) {
        openCluster(invAPI.getGuiCluster(clusterID));
    }

    /**
     * Opens the GuiCluster with the latest GuiWindow that was open.
     *
     * @param cluster The {@link GuiCluster} to open.
     */
    public void openCluster(GuiCluster<C> cluster) {
        if (cluster == null) return;
        GuiWindow<C> window = getWindow(cluster);
        if (window != null) {
            openWindow(window.getNamespacedKey());
        } else {
            openWindow(cluster.getEntry());
        }
    }

    /**
     * @return If there is currently an active {@link ChatInputAction}, that will be called on chat input.
     */
    public boolean isChatEventActive() {
        return getChatInputAction() != null;
    }

    /**
     * @return The active {@link ChatInputAction} or null if not active.
     */
    @Nullable
    public ChatInputAction<C> getChatInputAction() {
        return chatInputAction;
    }

    /**
     * Set the {@link ChatInputAction} to be called on next chat input.
     *
     * @param chatInputAction The new {@link ChatInputAction}
     */
    public void setChatInputAction(ChatInputAction<C> chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    /**
     * @return The active {@link ChatTabComplete} or null if not active.
     */
    @Nullable
    public ChatTabComplete<C> getChatTabComplete() {
        return chatTabComplete;
    }

    /**
     * Set the {@link ChatTabComplete} to be used on the next Chat Input.
     * Requires a {@link ChatInputAction} to be called!
     *
     * @param chatTabComplete The new {@link ChatTabComplete}
     */
    public void setChatTabComplete(ChatTabComplete<C> chatTabComplete) {
        this.chatTabComplete = chatTabComplete;
    }

    /**
     * @return If there is currently an active {@link ChatInputAction}, that will be used for the next chat input.
     */
    public boolean hasChatTabComplete() {
        return chatTabComplete != null;
    }

    /**
     * Sets both the {@link ChatInputAction} as well as the {@link ChatTabComplete}.
     *
     * @param chatInputAction The new {@link ChatInputAction}
     * @param chatTabComplete The new {@link ChatTabComplete}
     */
    public void setChatInput(@Nullable ChatInputAction<C> chatInputAction, @Nullable ChatTabComplete<C> chatTabComplete) {
        setChatInputAction(chatInputAction);
        setChatTabComplete(chatTabComplete);
    }

    /**
     * Cancels the current active ChatInputAction and ChatTabComplete.
     * After this is called no chat input will execute any actions anymore.
     */
    public void cancelChatInput() {
        setChatInput(null, null);
    }

    /**
     * @deprecated Name is misleading with the introduction of additional ChatTabComplete
     */
    @Deprecated
    public void cancelChatInputAction() {
        cancelChatInput();
    }

    /**
     * Closes the current open window.
     */
    public void close() {
        var player = getPlayer();
        if (player != null) player.closeInventory();
    }

    /**
     * @return If help is enabled for this GuiHandler.
     */
    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    /**
     * Set if the help is enabled.
     *
     * @param helpEnabled The new help value.
     */
    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    /**
     * @return The instance of the {@link CustomCache}
     */
    public C getCustomCache() {
        return customCache;
    }

    public final boolean onChat(Player player, String msg, String[] args) {
        if (isChatEventActive()) {
            return chatInputAction.onChat(this, player, msg, args);
        }
        return true;
    }

    final void setButton(GuiWindow<C> guiWindow, int slot, String id) {
        customCache.setButton(guiWindow, slot, id);
    }

    final Button<C> getButton(GuiWindow<C> guiWindow, int slot) {
        String id = customCache.getButtons(guiWindow).get(slot);
        if (id != null && id.contains(":")) {
            return invAPI.getButton(NamespacedKey.of(id));
        }
        return guiWindow.getButton(id);
    }

    /**
     * Called when the inventory is closed.
     *
     * @param guiInventory The {@link GUIInventory} that is closed.
     * @param event        The {@link InventoryCloseEvent} that caused this action.
     */
    public void onClose(GUIInventory<C> guiInventory, InventoryCloseEvent event) {
        if (!clusterHistory.isEmpty() && !switchWindow) {
            if (guiInventory.getWindow().onClose(this, guiInventory, event.getView())) {
                this.openCluster();
            } else {
                this.isWindowOpen = false;
            }
        }
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/wua") && !event.getMessage().startsWith("/wui") && event.getPlayer().getUniqueId().equals(uuid) && isChatEventActive()) {
            cancelChatInput();
        }
    }
}
