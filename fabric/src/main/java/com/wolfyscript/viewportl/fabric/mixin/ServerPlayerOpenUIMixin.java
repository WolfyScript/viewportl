package com.wolfyscript.viewportl.fabric.mixin;

import com.mojang.authlib.GameProfile;
import com.wolfyscript.viewportl.fabric.inject.OpenInventoryUIExt;
import com.wolfyscript.viewportl.fabric.server.ui.CustomUIContainer;
import com.wolfyscript.viewportl.fabric.server.ui.CustomUIContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerOpenUIMixin extends Player implements OpenInventoryUIExt {

    private ServerPlayerOpenUIMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    protected abstract void nextContainerCounter();

    @Shadow
    private int containerCounter;

    @Shadow
    protected abstract void initMenu(AbstractContainerMenu abstractContainerMenu);

    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Shadow
    public abstract void closeContainer();

    @Override
    public void viewportl$openUIContainer(CustomUIContainer container) {
        if (containerMenu != inventoryMenu) {
            closeContainer();
        }

        nextContainerCounter();
        var menu = new CustomUIContainerMenu((ServerPlayer) (Object) this, container, containerCounter);

        if (!isImmobile()) {
            connection.send(new ClientboundOpenScreenPacket(
                containerCounter,
                menu.getType(),
                (Component) container.getTitle()
            ));
        }

        containerMenu = menu;
        initMenu(menu);
    }
}
