package com.wolfyscript.viewportl.fabric.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(AbstractContainerMenu.class)
public interface ContainerMenuQuickcraftAccessor {

    @Accessor("quickcraftStatus")
    int viewportl$getStatus();

    @Accessor("quickcraftStatus")
    void viewportl$setStatus(int status);

    @Accessor("quickcraftSlots")
    Set<Slot> viewportl$getSlots();

    @Accessor("quickcraftType")
    int viewportl$getType();

    @Accessor("quickcraftType")
    void viewportl$setType(int type);

    @Invoker("tryItemClickBehaviourOverride")
    boolean viewportl$tryItemClickBehaviourOverride(Player player, ClickAction button, Slot slot, ItemStack slotStack, ItemStack carried);

}
