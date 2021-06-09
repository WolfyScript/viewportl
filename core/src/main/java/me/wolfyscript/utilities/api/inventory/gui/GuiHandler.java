package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This object is used to store all relevant data for the Player using the GUI.
 * <br>
 * It stores the:
 * - cache,
 * - the current {@link GuiCluster},
 * - the GUI history,
 * - possible active {@link ChatInputAction} and {@link ChatTabComplete},
 * - and other necessary data.
 * <br>
 *
 * @param <C>
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

    void setSwitchWindow(boolean switchWindow) {
        this.switchWindow = switchWindow;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public InventoryAPI<C> getInvAPI() {
        return invAPI;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    public GuiCluster<C> getCluster() {
        return cluster;
    }

    public void setCluster(GuiCluster<C> currentGuiCluster) {
        this.cluster = currentGuiCluster;
    }

    /*
    Return true if a GuiWindow is open.
    */
    public boolean isWindowOpen() {
        return isWindowOpen;
    }

    /*
    Reloads the GuiWindow of the GuiCluster.
     */
    public void reloadWindow(NamespacedKey namespacedKey) {
        List<GuiWindow<C>> history = getHistory(invAPI.getGuiCluster(namespacedKey.getNamespace()));
        history.remove(history.get(history.size() - 1));
        openWindow(namespacedKey);
    }

    public GuiWindow<C> getWindow(String clusterID) {
        return getWindow(invAPI.getGuiCluster(clusterID));
    }

    /*
    Gets the current GuiWindow. If the Gui isn't opened then the latest GuiWindow is returned.
     */
    @Nullable
    public GuiWindow<C> getWindow(GuiCluster<C> cluster) {
        List<GuiWindow<C>> history = getHistory(cluster);
        if (!history.isEmpty()) {
            return invAPI.getGuiWindow(history.get(history.size() - 1).getNamespacedKey());
        }
        return null;
    }

    @Nullable
    public GuiWindow<C> getWindow() {
        return getWindow(getCluster());
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(String clusterID) {
        return getPreviousWindow(invAPI.getGuiCluster(clusterID));
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(GuiCluster<C> cluster) {
        return getPreviousWindow(cluster, 2);
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow<C> getPreviousWindow(String clusterID, int stepsBack) {
        return getPreviousWindow(invAPI.getGuiCluster(clusterID), stepsBack);
    }

    public GuiWindow<C> getPreviousWindow(GuiCluster<C> cluster, int stepsBack) {
        List<GuiWindow<C>> history = getHistory(cluster);
        if (history.size() > stepsBack) {
            return invAPI.getGuiWindow(history.get(history.size() - (stepsBack + 1)).getNamespacedKey());
        }
        return null;
    }

    public GuiWindow<C> getPreviousWindow() {
        return getPreviousWindow(getCluster());
    }

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
        long size = history.size() - (long) stepsBack;
        if (size > 0) {
            setHistory(cluster, history.stream().limit(size).collect(Collectors.toList()));
            List<GuiWindow<C>> updatedHistory = getHistory(cluster);
            openWindow(updatedHistory.get(updatedHistory.size() - 1).getNamespacedKey());
        } else {
            history.clear();
            openCluster(cluster);
        }
    }

    public Map<GuiCluster<C>, List<GuiWindow<C>>> getClusterHistory() {
        return clusterHistory;
    }

    public List<GuiWindow<C>> getHistory(GuiCluster<C> cluster) {
        return clusterHistory.computeIfAbsent(cluster, c -> new ArrayList<>());
    }

    private void setHistory(GuiCluster<C> cluster, List<GuiWindow<C>> history) {
        clusterHistory.put(cluster, history);
    }

    /*
        Opens the specific GuiWindow in the current GuiCluster.
         */
    public void openWindow(String guiWindowID) {
        openWindow(new NamespacedKey(getCluster().getId(), guiWindowID));
    }


    public void openWindow(@NotNull NamespacedKey namespacedKey) {
        openWindow(invAPI.getGuiWindow(namespacedKey));
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
            List<GuiWindow<C>> history = getHistory(cluster);
            if (getWindow(cluster) == null || !Objects.equals(getWindow(cluster), window)) {
                history.add(window);
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
        NamespacedKey guiWindowID = cluster.getEntry();
        GuiWindow<C> window = getWindow(cluster);
        if (window != null) {
            guiWindowID = window.getNamespacedKey();
        }
        openWindow(guiWindowID);
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
