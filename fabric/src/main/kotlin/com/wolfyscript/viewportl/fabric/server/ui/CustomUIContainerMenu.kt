package com.wolfyscript.viewportl.fabric.server.ui

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class CustomUIContainerMenu(
    player: Player,
    container: CustomUIContainer,
    id: Int,
) : AbstractContainerMenu(getMenuType(container.size), id) {

    val menu = ChestMenu(
        getMenuType(container.size),
        id,
        player.inventory,
        container,
        container.size / 9
    )

    override fun quickMoveStack(
        player: Player,
        i: Int,
    ): ItemStack {
        return menu.quickMoveStack(player, i)
    }

    override fun stillValid(player: Player): Boolean {
        return menu.stillValid(player)
    }

    override fun clicked(i: Int, j: Int, clickType: ClickType, player: Player) {
        super.clicked(i, j, clickType, player)
    }
}

fun getMenuType(size: Int): MenuType<*> {
    return when (size) {
        9 -> MenuType.GENERIC_9x1
        9 * 2 -> MenuType.GENERIC_9x2
        9 * 3 -> MenuType.GENERIC_9x3
        9 * 4 -> MenuType.GENERIC_9x4
        9 * 5 -> MenuType.GENERIC_9x5
        9 * 6 -> MenuType.GENERIC_9x6
        else -> MenuType.GENERIC_9x3
    }
}