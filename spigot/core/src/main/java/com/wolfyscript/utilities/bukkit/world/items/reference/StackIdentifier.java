package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

public interface StackIdentifier extends Keyed {

    /**
     * Creates an item stack, this identifier points to, using the provided {@link ItemCreateContext}.<br>
     * The context can be used to create player specific stacks, specify the stack amount, and more.
     *
     * @param context The context for the item stack creation
     * @return The item stack, that this identifier points to, created using the provided context
     */
    ItemStack stack(ItemCreateContext context);

    /**
     * Checks if the specified item stack matches the reference stack from this identifier.
     *
     * @param other The other stack to check for a match
     * @return true if the other stack matches this identifier
     */
    default boolean matches(ItemStack other) {
        return matches(other, true, false);
    }

    default boolean matches(ItemStack other, boolean exact) {
        return matches(other, exact, false);
    }

    default boolean matches(ItemStack other, boolean exact, boolean ignoreAmount) {
        return matches(other, 1, exact, ignoreAmount);
    }

    boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount);

    /**
     * Gets the optional permission string this identifier requires
     *
     * @return Optional containing the permission string, or empty if not available
     */
    default Optional<String> permission() {
        return Optional.empty();
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <br><br>
     * <h3>Stackable  ({@link #stack(ItemCreateContext)}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * <p>
     * The stack is shrunk by the specified amount (<strong><code>{@link #stack(ItemCreateContext)}.{@link ItemStack#getAmount() getAmount()} * count</code></strong>).<br>
     * For applying stackable replacements it calls the stackReplacement function with the already shrunken stack and this reference.<br>
     * Default behaviour can be found here:
     * <ul>
     *     <li>{@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)}</li>
     *     <li>{@link #shrinkUnstackableItem(ItemStack, boolean)}</li>
     * </ul>
     * </p>
     * <h3>Un-stackable  ({@link #stack(ItemCreateContext)}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
     * <p>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
     * </p>
     *
     * @param stack            The input ItemStack, that is also going to be edited.
     * @param count            The amount of this custom item that should be removed from the input.
     * @param useRemains       If the Item should be replaced by the default craft remains.
     * @param stackReplacement Behaviour of how to apply the replacements of stackable items.
     * @return The manipulated stack, default remain, or custom remains.
     */
    default ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<StackIdentifier, ItemStack, ItemStack> stackReplacement) {
        if (stack(ItemCreateContext.empty(count)).getMaxStackSize() == 1 && stack.getAmount() == 1) {
            return shrinkUnstackableItem(stack, useRemains);
        }
        int amount = stack.getAmount() - count;
        if (amount <= 0) {
            stack = new ItemStack(Material.AIR);
        } else {
            stack.setAmount(amount);
        }
        return stackReplacement.apply(this, stack);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <h3>Stackable  ({@link #stack(ItemCreateContext)}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * The stack is shrunk by the specified amount (<strong><code>{@link #stack(ItemCreateContext)}.{@link ItemStack#getAmount() getAmount()} * count</code></strong>)
     * <p>
     * If this stack has craft remains:<br>
     * <ul>
     *   <li><b>Location: </b>Used as the drop location for remaining items. <br>May be overridden by options below.</li>
     *   <li>
     *     <b>Player: </b>Adds items to the players inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>Player location is used as the drop location for remaining items.</li>
     *   <li>
     *     <b>Inventory:</b> Adds items to the inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>If location not available yet: uses inventory location as drop location for remaining items.
     *   </li>
     * </ul>
     * All remaining items that cannot be added to player or the other inventory are dropped at the specified location.<br>
     * <strong>Warning! If you do not provide a location via <code>player</code>, <code>inventory</code>, or <code>location</code>, then the remaining items are discarded!</strong><br>
     * For custom behaviour see {@link #shrink(ItemStack, int, boolean, BiFunction)}.
     * </p>
     * </p>
     * <p>
     * <h3>Un-stackable  ({@link #stack(ItemCreateContext)}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack      The input ItemStack, that is also going to be edited.
     * @param count      The amount of this custom item that should be removed from the input.
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @param inventory  The optional inventory to add the replacements to. (Only for stackable items)
     * @param player     The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location   The location where the replacements should be dropped. (Only for stackable items)
     * @return The manipulated stack, default remain, or custom remains.
     */
    default ItemStack shrink(ItemStack stack, int count, boolean useRemains, @Nullable final Inventory inventory, @Nullable final Player player, @Nullable final Location location) {
        return shrink(stack, count, useRemains, (customItem, resultStack) -> CustomItem.craftRemain(stack(ItemCreateContext.empty(count)))
                .map(material -> useRemains ? new ItemStack(material) : null)
                .map(replacement -> {
                    var originalStack = resultStack;
                    int replacementAmount = count;
                    if (ItemUtils.isAirOrNull(originalStack)) {
                        int returnableAmount = Math.min(replacement.getMaxStackSize(), replacementAmount);
                        replacementAmount -= returnableAmount;
                        originalStack = replacement.clone();
                        originalStack.setAmount(replacementAmount);
                    }
                    if (replacementAmount > 0) {
                        replacement.setAmount(replacementAmount);
                        Location loc = location;
                        if (player != null) {
                            replacement = player.getInventory().addItem(replacement).get(0);
                            loc = player.getLocation();
                        }
                        if (inventory != null && replacement != null) {
                            replacement = inventory.addItem(replacement).get(0);
                            if (loc == null) loc = inventory.getLocation();
                        }
                        if (loc != null && replacement != null && loc.getWorld() != null) {
                            loc.getWorld().dropItemNaturally(loc.add(0.5, 1.0, 0.5), replacement);
                        }
                    }
                    return originalStack;
                }).orElse(resultStack));
    }

    /**
     * Shrinks the specified stack and returns the manipulated or replaced item!
     * <p>
     *     This firstly checks for custom replacements (remains) and sets it as the result.<br>
     *     Then handles damaging of the stack, if there is a specified durability cost.<br>
     *     In case the stack breaks due damage it is replaced by the result, specified earlier.
     * </p>
     *
     * @param stack      The stack to shrink
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @return The manipulated (damaged) stack, default remain, or custom remains.
     */
    default ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains) {
        return CustomItem.craftRemain(stack(ItemCreateContext.empty(1))).map(material -> useRemains ? new ItemStack(material) : null).orElse(new ItemStack(Material.AIR));
    }

    default StackIdentifierParser<?> parser() {
        return ((WolfyCoreCommon) WolfyCore.getInstance()).getRegistries().getStackIdentifierParsers().get(key());
    }

    @Override
    NamespacedKey key();

}
