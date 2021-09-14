package me.wolfyscript.utilities.util.events;

import me.wolfyscript.utilities.api.inventory.custom_items.ArmorType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final EquipMethod equipType;
    private final ArmorType type;
    private final CustomItem oldCustomArmorPiece;
    private final CustomItem newCustomArmorPiece;
    private final ItemStack oldArmorPiece;
    private final ItemStack newArmorPiece;

    public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece) {
        this(player, equipType, type, oldArmorPiece, newArmorPiece, oldArmorPiece == null ? null : CustomItem.getByItemStack(oldArmorPiece), newArmorPiece == null ? null : CustomItem.getByItemStack(newArmorPiece));
    }

    /**
     * Constructor for the ArmorEquipEvent.
     *
     * @param player        The player who put on / removed the armor.
     * @param type          The ArmorType of the armor added
     * @param oldArmorPiece The ItemStack of the armor removed.
     * @param newArmorPiece The ItemStack of the armor added.
     */
    public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece, final CustomItem oldCustomArmorPiece, final CustomItem newCustomArmorPiece) {
        super(player);
        this.equipType = equipType;
        this.type = type;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
        this.oldCustomArmorPiece = oldCustomArmorPiece;
        this.newCustomArmorPiece = newCustomArmorPiece;
        this.cancel = !canBeEquipped();

        var equipmentSlot = type.getEquipmentSlot();
        if (!ItemUtils.isAirOrNull(newArmorPiece)) {
            //Equipping new armor!
            if (!ItemUtils.isAirOrNull(newCustomArmorPiece) && newCustomArmorPiece.hasEquipmentSlot(equipmentSlot)) {
                PlayerUtils.stopActiveParticleEffect(getPlayer(), equipmentSlot);
                newCustomArmorPiece.getParticleContent().spawn(player, equipmentSlot);
            }
        } else {
            PlayerUtils.stopActiveParticleEffect(getPlayer(), equipmentSlot);
        }
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public final boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled.
     */
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    public final ArmorType getType() {
        return type;
    }

    /**
     * Returns the last equipped armor piece, could be a piece of armor, {@link org.bukkit.Material#AIR}, or null.
     */
    public final ItemStack getOldArmorPiece() {
        return oldArmorPiece;
    }

    /**
     * Returns the newly equipped armor, could be a piece of armor, {@link org.bukkit.Material#AIR}, or null.
     */
    public final ItemStack getNewArmorPiece() {
        return newArmorPiece;
    }

    public CustomItem getOldCustomArmorPiece() {
        return oldCustomArmorPiece;
    }

    public CustomItem getNewCustomArmorPiece() {
        return newCustomArmorPiece;
    }

    /**
     * Gets the method used to either equip or unequip an armor piece.
     */
    public EquipMethod getMethod() {
        return equipType;
    }

    /**
     * Checks if the the item of the event can be equipped.
     * Empty or null items are treated as equipable!
     *
     * @return true if the item can be equipped or is null or AIR.
     */
    public boolean canBeEquipped() {
        if (type != null) {
            if (!ItemUtils.isAirOrNull(newArmorPiece)) {
                return (ItemUtils.isEquipable(newArmorPiece.getType(), type) && (ItemUtils.isAirOrNull(newCustomArmorPiece) || !newCustomArmorPiece.isBlockVanillaEquip()))
                        || !ItemUtils.isAirOrNull(newCustomArmorPiece) && newCustomArmorPiece.hasEquipmentSlot(type.getEquipmentSlot());
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if the item can be equipped in vanilla mc.
     */
    public boolean canBeEquippedVanilla() {
        if (type != null) {
            return !ItemUtils.isAirOrNull(newArmorPiece) && ItemUtils.isEquipable(newArmorPiece.getType(), type);
        }
        return false;
    }

    public enum EquipMethod {
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        DRAG,
        /**
         * When you manually equip or unequip the item.
         */
        PICK_DROP,
        /**
         * When you right click an armor piece in the hotbar without the inventory open to equip.
         */
        HOTBAR,
        /**
         * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * TODO: When in range of a dispenser that shoots an armor piece to equip.
         */
        DISPENSER,
        /**
         * TODO: When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * TODO: When you die causing all armor to unequip
         */
        DEATH,
        /**
         * When you drop the item using out of the inventory
         */
        DROP
    }
}
