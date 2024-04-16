package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.wolfyscript.utilities.Copyable;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.collection.RandomCollection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Acts as a wrapper for {@link StackIdentifier}, that links to an external ItemStack (like other Plugins).
 * This keeps track of the original ItemStack, as a fallback, and the parser used to get the wrapped {@link StackIdentifier}.
 * Additionally, it stores the amount, and other extra settings.
 * <br>
 * This is usually stored in JSON (HOCON) files, while the {@link StackIdentifier} is not.
 */
public class StackReference implements Copyable<StackReference> {

    private final WolfyCoreCommon core;
    private final int amount;
    private final double weight;
    /**
     * Used to store the original stack
     */
    private final ItemStack stack;
    /**
     * Used to store the previous parser result
     */
    private StackIdentifier identifier;
    private StackIdentifierParser<?> parser;

    public StackReference(WolfyCoreCommon core, NamespacedKey parser, double weight, int amount, ItemStack item) {
        this.amount = amount;
        this.weight = weight;
        this.core = core;
        setParser(core.getRegistries().getStackIdentifierParsers().get(parser));
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    public StackReference(WolfyCoreCommon core, @NotNull StackIdentifier identifier, double weight, int amount, ItemStack item) {
        this.amount = amount;
        this.weight = weight;
        this.core = core;
        setParser(core.getRegistries().getStackIdentifierParsers().get(identifier.key()));
        this.stack = item;
        this.identifier = identifier;
    }

    private StackReference(StackReference stackReference) {
        this.weight = stackReference.weight;
        this.amount = stackReference.amount;
        this.core = stackReference.core;
        setParser(stackReference.parser);
        this.stack = stackReference.stack;
        this.identifier = parseIdentifier();
    }

    private void setParser(StackIdentifierParser<?> parser) {
        this.parser = parser != null ? parser : core.getRegistries().getStackIdentifierParsers().get(BukkitStackIdentifier.ID);
    }

    private StackIdentifier parseIdentifier() {
        Optional<? extends StackIdentifier> identifierOptional = parser.from(stack);
        if (identifierOptional.isPresent()) return identifierOptional.get();
        return core.getRegistries().getStackIdentifierParsers().get(BukkitStackIdentifier.ID).from(stack).orElseThrow();
    }

    /**
     * Gets the currently wrapped StackIdentifier, parsed by the current {@link StackIdentifierParser}
     *
     * @return The currently wrapped StackIdentifier
     */
    public StackIdentifier identifier() {
        return identifier;
    }

    public boolean matches(ItemStack other) {
        return matches(other, true, false);
    }

    public boolean matches(ItemStack other, boolean exact) {
        return matches(other, exact, false);
    }

    public boolean matches(ItemStack other, boolean exact, boolean ignoreAmount) {
        return identifier().matches(other, amount, exact, ignoreAmount);
    }

    /**
     * Convenience method to get the stack the identifier points to.<br>
     * This is the same as <code>{@link #identifier() identifier()}.{@link StackIdentifier#stack(ItemCreateContext) stack}({@link ItemCreateContext#of(StackReference) ItemCreateContext.of}({@link StackReference this}).{@link ItemCreateContext.Builder#build() build()})</code>
     *
     * @return The stack the {@link #identifier()} points to
     */
    public ItemStack referencedStack() {
        return identifier().stack(ItemCreateContext.of(this).build());
    }

    /**
     * Convenience method to get the stack the identifier points to.<br>
     * This is the same as <code>{@link #identifier() identifier()}.{@link StackIdentifier#stack(ItemCreateContext) stack}({@link ItemCreateContext#of(StackReference) ItemCreateContext.of}({@link StackReference this}).{@link ItemCreateContext.Builder#build() build()})</code>
     *
     * @param contextBuild provides a {@link ItemCreateContext.Builder} with this reference already applied
     * @return The stack the {@link #identifier()} points to
     */
    public ItemStack referencedStack(Consumer<ItemCreateContext.Builder> contextBuild) {
        ItemCreateContext.Builder builder = ItemCreateContext.of(this);
        contextBuild.accept(builder);
        return identifier().stack(builder.build());
    }

    /**
     * Gets the <b>ORIGINAL</b> stack, from which this reference was created from!<br>
     * For the linked stack from for example an external plugin use {@link #identifier()}!
     *
     * @return The <b>ORIGINAL</b> stack this reference was created from
     * @see #identifier() Get the StackIdentifier pointing to the external stack
     * @see #referencedStack() Get the externally referenced ItemStack
     */
    @JsonGetter("stack")
    public ItemStack originalStack() {
        return stack;
    }

    /**
     * Gets the weight associated with this reference inside a collection.<br>
     * For example inside of a {@link RandomCollection<StackReference>}
     *
     * @return The weight of this reference
     */
    @JsonGetter("weight")
    public double weight() {
        return weight;
    }

    /**
     * Gets the stack amount for the referenced ItemStack
     *
     * @return The stack amount of the referenced ItemStack
     */
    @JsonGetter("amount")
    public int amount() {
        return amount;
    }

    /**
     * Gets the currently used {@link StackIdentifierParser}
     *
     * @return The current {@link StackIdentifierParser}
     */
    public StackIdentifierParser<?> parser() {
        return parser;
    }

    /**
     * Swaps the current parser with the specified parser and parses the original stack to get the new StackIdentifier.
     *
     * @param parser The new parser to use to get the StackIdentifier
     */
    public void swapParser(StackIdentifierParser<?> parser) {
        setParser(parser);
        this.identifier = parseIdentifier();
    }

    /**
     * Gets the id of the current parser
     *
     * @return The id of the current parser
     */
    @JsonGetter("parser")
    private NamespacedKey parserId() {
        return parser.key();
    }

    @Override
    public StackReference copy() {
        return new StackReference(this);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <br><br>
     * <h3>Stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * <p>
     * The stack is shrunk by the specified amount (<strong><code>{@link #amount()} * totalAmount</code></strong>).<br>
     * For applying stackable replacements it calls the stackReplacement function with the already shrunken stack and this reference.<br>
     * Default behaviour can be found here:
     * <ul>
     *     <li>{@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)}</li>
     *     <li>{@link #shrinkUnstackableItem(ItemStack, boolean)}</li>
     * </ul>
     * </p>
     * <h3>Un-stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
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
    public ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<StackIdentifier, ItemStack, ItemStack> stackReplacement) {
        return identifier().shrink(stack, count * amount(), useRemains, stackReplacement);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <h3>Stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * The stack is shrunk by the specified amount (<strong><code>{@link #amount()} * count</code></strong>)
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
     * <h3>Un-stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
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
    public ItemStack shrink(ItemStack stack, int count, boolean useRemains, @Nullable final Inventory inventory, @Nullable final Player player, @Nullable final Location location) {
        return identifier().shrink(stack, count * amount(), useRemains, inventory, player, location);
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
    public ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains) {
        return identifier().shrinkUnstackableItem(stack, useRemains);
    }

}
