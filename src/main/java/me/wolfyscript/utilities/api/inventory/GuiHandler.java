package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;
import me.wolfyscript.utilities.api.inventory.events.GuiCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class GuiHandler<T extends CustomCache> implements Listener {

    private WolfyUtilities api;
    private InventoryAPI invAPI;
    private Player player;
    private boolean changingInv = false;
    private ChatInputAction chatInputAction = null;

    private HashMap<String, List<String>> clusterHistory = new HashMap<>();
    private String currentGuiCluster = "";
    private boolean isWindowOpen = false;
    private boolean helpEnabled = false;

    private T customCache;

    public GuiHandler(Player player, WolfyUtilities api, T customCache) {
        this.api = api;
        this.invAPI = api.getInventoryAPI();
        this.player = player;
        this.customCache = customCache;
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    public boolean isChangingInv() {
        return changingInv;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    @Nullable
    public Player getPlayer() {
        return player.getPlayer();
    }

    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    public String getCurrentGuiCluster() {
        return currentGuiCluster;
    }

    public void setCurrentGuiCluster(String currentGuiCluster) {
        this.currentGuiCluster = currentGuiCluster;
    }

    /*
    Return true if a GuiWindow is open.
    */
    public boolean isWindowOpen() {
        return isWindowOpen;
    }

    public boolean verifyInventory(Inventory inventory){
        return isWindowOpen() && getCurrentInv() != null && getCurrentInv().getInventory(this).equals(inventory);
    }

    /*
    Reloads the GuiWindow of the GuiCluster.
     */
    public void reloadInv(String clusterID, String guiWindowID) {
        List<String> history = clusterHistory.getOrDefault(clusterID, new ArrayList<>());
        history.remove(history.get(history.size() - 1));
        clusterHistory.put(clusterID, history);
        changeToInv(clusterID, guiWindowID);
    }

    /*
    Gets the current GuiWindow. If the Gui isn't opened then the latest GuiWindow is returned.
     */
    @Nullable
    public GuiWindow getCurrentInv(String clusterID) {
        if (clusterHistory.get(clusterID) != null && clusterHistory.get(clusterID).size() > 0) {
            return invAPI.getGuiWindow(clusterID, clusterHistory.get(clusterID).get(clusterHistory.get(clusterID).size() - 1));
        }
        return null;
    }

    @Nullable
    public GuiWindow getCurrentInv(){
        return getCurrentInv(getCurrentGuiCluster());
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow getPreviousInv(String clusterID) {
        return getPreviousInv(clusterID, 2);
    }

    /*
    Gets the previous GuiWindow that was open. If there is non null is returned!
     */
    @Nullable
    public GuiWindow getPreviousInv(String clusterID, int stepsBack) {
        if (clusterHistory.get(clusterID) != null && clusterHistory.get(clusterID).size() > stepsBack) {
            return invAPI.getGuiWindow(clusterID, clusterHistory.get(clusterID).get(clusterHistory.get(clusterID).size() - (stepsBack+1)));
        }
        return null;
    }

    public GuiWindow getPreviousInv(){
        return getPreviousInv(getCurrentGuiCluster());
    }

    public GuiWindow getPreviousInv(int stepsBack){
        return getPreviousInv(getCurrentGuiCluster(), stepsBack);
    }

    public void openPreviousInv() {
        openPreviousInv(getCurrentGuiCluster());
    }

    public void openPreviousInv(String clusterID) {
        openPreviousInv(clusterID, 1);
    }

    public void openPreviousInv(int stepsBack) {
        openPreviousInv(getCurrentGuiCluster(), stepsBack);
    }

    public void openPreviousInv(String clusterID, int stepsBack) {
        String previousInv = getPreviousInv(stepsBack).getNamespace();
        List<String> history = clusterHistory.getOrDefault(clusterID, new ArrayList<>());
        for(int i = 0; i < stepsBack; i++){
            history.remove(history.size()-1);
        }
        clusterHistory.put(clusterID, history);
        changeToInv(clusterID, previousInv);
    }

    /*
    Opens the specific GuiWindow in the current GuiCluster.
     */
    public void changeToInv(String guiWindowID) {
        changeToInv(getCurrentGuiCluster(), guiWindowID);
    }

    /*
    Opens the specific GuiWindow in the specific GuiCluster.
     */
    public void changeToInv(@Nonnull String clusterID, @Nonnull String guiWindowID) {
        Bukkit.getScheduler().runTask(getApi().getPlugin(), () -> {
            Player player1 = getPlayer();
            changingInv = true;
            player1.closeInventory();
            if (WolfyUtilities.hasPermission(player1, getApi().getPlugin().getDescription().getName().toLowerCase(Locale.ROOT) + ".inv." + clusterID.toLowerCase(Locale.ROOT) + "." + guiWindowID.toLowerCase(Locale.ROOT))) {
                List<String> history = clusterHistory.getOrDefault(clusterID, new ArrayList<>());
                if (getCurrentInv(clusterID) == null || !getCurrentInv(clusterID).getNamespace().equals(guiWindowID)) {
                    history.add(guiWindowID);
                }
                clusterHistory.put(clusterID, history);
                if (api.getInventoryAPI().getGuiWindow(clusterID, guiWindowID) != null) {
                    currentGuiCluster = clusterID;
                    isWindowOpen = true;
                    GuiWindow guiWindow = api.getInventoryAPI().getGuiWindow(clusterID, guiWindowID);

                    GuiUpdateEvent event = new GuiUpdateEvent(this, guiWindow);
                    Bukkit.getPluginManager().callEvent(event);
                    api.getInventoryAPI().getGuiWindow(clusterID, guiWindowID).setCachedInventorie(this, event.getInventory());
                    player1.openInventory(event.getInventory());
                }
            } else {
                api.sendPlayerMessage(player1, "§4You don't have the permission §c" + getApi().getPlugin().getDescription().getName().toLowerCase() + ".inv." + clusterID.toLowerCase(Locale.ROOT) + "." + guiWindowID.toLowerCase(Locale.ROOT));
            }
            changingInv = false;
        });
    }

    /*
    Opens the current GuiCluster with the latest GuiWindow that was open.
     */
    public void openCluster() {
        openCluster(getCurrentGuiCluster());
    }

    /*
    Opens the GuiCluster with the latest GuiWindow that was open.
     */

    public void openCluster(String clusterID) {
        String guiWindowID = invAPI.getGuiCluster(clusterID).getMainMenu();
        if (getCurrentInv(clusterID) != null) {
            guiWindowID = getCurrentInv(clusterID).getNamespace();
        }
        changeToInv(clusterID, guiWindowID);
    }

    public boolean isChatEventActive() {
        return getChatInputAction() != null;
    }

    public ChatInputAction getChatInputAction() {
        return chatInputAction;
    }

    public void setChatInputAction(ChatInputAction chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    public void close() {
        this.isWindowOpen = false;
        this.player.closeInventory();
    }

    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setButton(GuiWindow guiWindow, int slot, String id) {
        TreeMap<Integer, String> buttons = customCache.getButtons(guiWindow);
        buttons.put(slot, id);
        customCache.setButtons(guiWindow, buttons);
    }

    public Button getButton(GuiWindow guiWindow, int slot) {
        String id = customCache.getButtons(guiWindow).get(slot);
        if (id != null && !id.isEmpty() && id.contains(":")) {
            return api.getInventoryAPI().getButton(id.split(":")[0], id.split(":")[1]);
        }
        return guiWindow.getButton(id);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        Player eventPlayer = (Player) event.getPlayer();
        if (player.equals(eventPlayer)) {
            if (!clusterHistory.isEmpty() && isWindowOpen()) {
                if (!changingInv) {
                    String cluster = getCurrentGuiCluster();
                    GuiWindow guiWindow = getCurrentInv();
                    GuiCloseEvent closeEvent = new GuiCloseEvent(cluster, guiWindow, this, event.getView());
                    Bukkit.getPluginManager().callEvent(closeEvent);
                    if(closeEvent.isCancelled()){
                        Bukkit.getScheduler().runTask(getApi().getPlugin(), (Runnable) this::openCluster);
                    }else{
                        this.isWindowOpen = false;
                    }
                }
            }
        }
    }

    public void cancelChatEvent() {
        setChatInputAction(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.getUniqueId().equals(this.player.getUniqueId())){
            this.player = player;
        }
    }

    public T getCustomCache() {
        return customCache;
    }
}
