package com.wolfyscript.viewportl.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxSize
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.viewportl
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

object Home : NavKey
object Settings : NavKey
object About : NavKey

@Composable
fun Navigation() {
    val backstack = remember { mutableStateListOf<NavKey>(Home) }

    NavHost(
        backstack = backstack,
        onBack = { backstack.removeLastOrNull() },
    ) {
        composable<Home> { Home(backstack) }
        composable<Settings> { Settings(backstack) }
        composable<About> { About(backstack) }
    }
}

@Composable
private fun Home(backstack: SnapshotStateList<NavKey>) {

    viewProperties(Key.viewportl("home")) {
        title("<b>Home")
        size(9.slots, 3.slots)
    }

    Row(
        Modifier.fillMaxWidth().height(3.slots),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                backstack.add(Settings)
            }
        ) {
            Icon(stack = ItemStack(Items.REDSTONE).apply { set(DataComponents.ITEM_NAME, "Settings".deser().vanilla()) }
                .snapshot())
        }

        Button(
            onClick = {
                backstack.add(About)
            }
        ) {
            Icon(stack = ItemStack(Items.PAPER).apply { set(DataComponents.ITEM_NAME, "About".deser().vanilla()) }
                .snapshot())
        }
    }
}

@Composable
private fun About(backstack: SnapshotStateList<NavKey>) {
    viewProperties(Key.viewportl("about")) {
        title("<b>About")
        size(9.slots, 4.slots)
    }

    Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            Icon(stack = ItemStack(Items.BOOKSHELF).apply {
                set(DataComponents.ITEM_NAME, "The About Page".deser().vanilla())
                set(
                    DataComponents.LORE,
                    ItemLore(
                        listOf(
                            "This example shows how".deser().vanilla(),
                            "a simple Navigation".deser().vanilla(),
                            "can be used.".deser().vanilla()
                        )
                    )
                )
            }.snapshot())
        }

        Button(
            onClick = {
                backstack.removeLastOrNull()
            }
        ) {
            Icon(stack = ItemStack(Items.BARRIER).apply { set(DataComponents.ITEM_NAME, "Back".deser().vanilla()) }
                .snapshot())
        }
    }
}

@Composable
private fun Settings(backstack: SnapshotStateList<NavKey>) {
    viewProperties(Key.viewportl("settings")) {
        title("<b>Settings")
        size(9.slots, 6.slots)
    }

    Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            Icon(stack = ItemStack(Items.ANVIL).apply {
                set(DataComponents.ITEM_NAME, "Nah...".deser().vanilla())
                set(
                    DataComponents.LORE,
                    ItemLore(
                        listOf(
                            "...nothing to configure here!".deser().vanilla()
                        )
                    )
                )
            }.snapshot())
        }

        Button(
            onClick = {
                backstack.removeLastOrNull()
            }
        ) {
            Icon(stack = ItemStack(Items.BARRIER).apply { set(DataComponents.ITEM_NAME, "Back".deser().vanilla()) }
                .snapshot())
        }
    }
}