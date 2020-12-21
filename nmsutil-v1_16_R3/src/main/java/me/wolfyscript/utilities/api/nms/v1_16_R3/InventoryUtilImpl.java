package me.wolfyscript.utilities.api.nms.v1_16_R3;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_16_R3.inventory.util.GUIInventoryCreator;
import net.minecraft.server.v1_16_R3.Container;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, type);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, type, title);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, size);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title) {
        return GUIInventoryCreator.INSTANCE.createInventory(guiHandler, window, null, size, title);
    }

    public void silentlyCloseInventory(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        if (entityPlayer.playerConnection != null) {
            Container container = entityPlayer.activeContainer;
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, container.getType(), CraftChatMessage.fromString("Test")[0]));
        }
    }
}
