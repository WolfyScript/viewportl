package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuiHandler<C extends CustomCache> implements Listener, InventoryHolder {

    private final WolfyUtilities api;
    private final InventoryAPI<C> invAPI;
    private final UUID uuid;
    private final HashMap<GuiCluster<C>, List<GuiWindow<C>>> clusterHistory = new HashMap<>();
    private ChatInputAction<C> chatInputAction = null;
    private GuiCluster<C> cluster = null;
    private boolean isWindowOpen = false;
    private boolean helpEnabled = false;
    private boolean switchWindow = false;

    private final C customCache;

    public GuiHandler(Player player, WolfyUtilities api, InventoryAPI<C> invAPI, C customCache) {
        this.api = api;
        this.invAPI = invAPI;
        this.uuid = player.getUniqueId();
        this.customCache = customCache;
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

    public boolean verifyInventory(Inventory inventory) {
        if (isWindowOpen() && getWindow() != null) {
            Inventory windowInv = getWindow().getInventory(this);
            return windowInv == inventory;
        }
        return false;
    }

    /*
    Reloads the GuiWindow of the GuiCluster.
     */
    public void reloadWindow(NamespacedKey namespacedKey) {
        GuiCluster<C> cluster = invAPI.getGuiCluster(namespacedKey.getNamespace());
        List<GuiWindow<C>> history = clusterHistory.getOrDefault(cluster, new ArrayList<>());
        history.remove(history.get(history.size() - 1));
        clusterHistory.put(cluster, history);
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
        if (clusterHistory.get(cluster) != null && clusterHistory.get(cluster).size() > 0) {
            return invAPI.getGuiWindow(clusterHistory.get(cluster).get(clusterHistory.get(cluster).size() - 1).getNamespacedKey());
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
        List<GuiWindow<C>> history = clusterHistory.getOrDefault(cluster, new ArrayList<>());
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
        openPreviousWindow(invAPI.getGuiCluster(clusterID));
    }

    public void openPreviousWindow(GuiCluster<C> cluster, int stepsBack) {
        List<GuiWindow<C>> history = clusterHistory.getOrDefault(cluster, new ArrayList<>());
        for (int i = 0; i < stepsBack; i++) {
            if (!history.isEmpty()) {
                history.remove(history.size() - 1);
            }
        }
        clusterHistory.put(cluster, history);
        if (history.isEmpty()) {
            openCluster(cluster);
        } else {
            openWindow(history.get(history.size() - 1).getNamespacedKey());
        }
    }

    public HashMap<GuiCluster<C>, List<GuiWindow<C>>> getClusterHistory() {
        return clusterHistory;
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

    /*
    Opens the specific GuiWindow in the specific GuiCluster.
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
        Bukkit.getScheduler().runTask(getApi().getPlugin(), () -> {
            Player player1 = getPlayer();
            if (api.getPermissions().hasPermission(player1, (api.getPlugin().getName() + ".inv." + window.getNamespacedKey().toString(".")))) {
                List<GuiWindow<C>> history = clusterHistory.getOrDefault(cluster, new ArrayList<>());
                if (getWindow(cluster) == null || !Objects.equals(getWindow(cluster), window)) {
                    history.add(window);
                }
                clusterHistory.put(cluster, history);
                this.cluster = cluster;
                isWindowOpen = true;
                window.update(this, null, null, true);
                return;
            }
            api.getChat().sendPlayerMessage(player1, "§4You don't have the permission §c" + (api.getPlugin().getName() + ".inv." + window.getNamespacedKey().toString(".")));
        });
    }

    /*
    Opens the current GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster() {
        openCluster(getCluster());
    }

    /**
     * Opens the GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster(String clusterID) {
        openCluster(invAPI.getGuiCluster(clusterID));
    }

    /**
     * Opens the GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster(GuiCluster<C> cluster) {
        NamespacedKey guiWindowID = cluster.getEntry();
        if (getWindow(cluster) != null) {
            guiWindowID = getWindow(cluster).getNamespacedKey();
        }
        openWindow(guiWindowID);
    }

    public boolean isChatEventActive() {
        return getChatInputAction() != null;
    }

    public ChatInputAction<C> getChatInputAction() {
        return chatInputAction;
    }

    public void setChatInputAction(ChatInputAction<C> chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    public boolean onChat(Player player, String msg, String[] args) {
        if (isChatEventActive()) {
            return chatInputAction.onChat(this, player, msg, args);
        }
        return true;
    }

    public void close() {
        this.isWindowOpen = false;
        Player player = getPlayer();
        if (player != null) player.closeInventory();
    }

    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setButton(GuiWindow<C> guiWindow, int slot, String id) {
        customCache.setButton(guiWindow, slot, id);
    }

    public Button<C> getButton(GuiWindow<C> guiWindow, int slot) {
        String id = customCache.getButtons(guiWindow).get(slot);
        if (id != null && !id.isEmpty() && id.contains(":")) {
            return invAPI.getButton(id.split(":")[0], id.split(":")[1]);
        }
        return guiWindow.getButton(id);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer().getUniqueId().equals(uuid)) {
            if (!clusterHistory.isEmpty() && isWindowOpen() && !switchWindow) {
                if (getWindow().onClose(this, event.getView())) {
                    Bukkit.getScheduler().runTask(getApi().getPlugin(), (Runnable) this::openCluster);
                } else {
                    this.isWindowOpen = false;
                }
            }
        }
    }

    public void cancelChatEvent() {
        setChatInputAction(null);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/wua")) {
            Player player = event.getPlayer();
            if (player.getUniqueId().equals(uuid)) {
                if (isChatEventActive()) {
                    cancelChatEvent();
                }
            }
        }
    }

    public C getCustomCache() {
        return customCache;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
