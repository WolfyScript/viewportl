package com.wolfyscript.utilities.compatibility.plugins.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

class MagicStackIdentifier implements StackIdentifier, com.wolfyscript.utilities.bukkit.compatibility.plugins.magic.MagicStackIdentifier {

    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("magic");

    private final String itemKey;
    private final MagicAPI magicAPI;

    public MagicStackIdentifier(String itemKey) {
        this(Bukkit.getPluginManager().getPlugin("Magic") instanceof MagicAPI api ? api : null, itemKey);
    }

    public MagicStackIdentifier(MagicAPI magicAPI, String itemKey) {
        Preconditions.checkNotNull(magicAPI, "No MagicAPI specified when creating a MagicStackIdentifier");
        this.magicAPI = magicAPI;
        this.itemKey = itemKey;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = Objects.requireNonNullElse(magicAPI.getController().createItem(itemKey), ItemUtils.AIR);
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        return Objects.equals(magicAPI.getController().getItemKey(other), itemKey);
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<MagicStackIdentifier> {

        private final MagicAPI magicAPI;

        public Parser(MagicAPI magicAPI) {
            this.magicAPI = magicAPI;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MagicStackIdentifier> from(ItemStack itemStack) {
            if(magicAPI.isBrush(itemStack) || magicAPI.isSpell(itemStack) || magicAPI.isUpgrade(itemStack) || magicAPI.isWand(itemStack)) {
                return Optional.of(new MagicStackIdentifier(magicAPI.getItemKey(itemStack)));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey key() {
            return ID;
        }
    }

}
