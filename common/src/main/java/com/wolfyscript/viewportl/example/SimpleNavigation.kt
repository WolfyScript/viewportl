package com.wolfyscript.viewportl.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.viewportl.foundation.Button
import com.wolfyscript.viewportl.foundation.Column
import com.wolfyscript.viewportl.foundation.Icon
import com.wolfyscript.viewportl.foundation.NavKey
import com.wolfyscript.viewportl.foundation.NavigationRoot
import com.wolfyscript.viewportl.foundation.Row
import com.wolfyscript.viewportl.ui.layout.Alignment
import com.wolfyscript.viewportl.ui.layout.Arrangement
import com.wolfyscript.viewportl.ui.layout.slots
import com.wolfyscript.viewportl.ui.modifier.Modifier
import com.wolfyscript.viewportl.ui.modifier.fillMaxSize
import com.wolfyscript.viewportl.ui.modifier.fillMaxWidth
import com.wolfyscript.viewportl.ui.modifier.height
import com.wolfyscript.viewportl.ui.viewProperties
import com.wolfyscript.viewportl.viewportl
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

object Home : NavKey
object Settings : NavKey
object About : NavKey

@Composable
fun SimpleNavigation() {
    val backstack = remember { mutableStateListOf<NavKey>(Home) }

    NavigationRoot(
        backstack = backstack,
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
        Button(onClick = { backstack.add(Settings) }) {
            Icon(stack = btnSettingsStack)
        }

        Button(onClick = { backstack.add(About) }) {
            Icon(stack = btnAboutStack)
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
            Icon(stack = iconAboutInfoStack)
        }

        Button(onClick = { backstack.removeLastOrNull() }) {
            Icon(stack = btnBackStack)
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
            Icon(stack = iconSettingsInfoStack)
        }
        Button(
            onClick = {
                backstack.removeLastOrNull()
            }
        ) {
            Icon(stack = btnBackStack)
        }
    }
}

private val btnSettingsStack = ItemStack(Items.REDSTONE).apply {
    set(DataComponents.ITEM_NAME, "Settings".deser().vanilla())
}.snapshot()
private val btnAboutStack = ItemStack(Items.PAPER).apply {
    set(DataComponents.ITEM_NAME, "About".deser().vanilla())
}.snapshot()
private val btnBackStack = ItemStack(Items.BARRIER).apply {
    set(DataComponents.ITEM_NAME, "Back".deser().vanilla())
}.snapshot()
private val iconSettingsInfoStack = ItemStack(Items.ANVIL).apply {
    set(DataComponents.ITEM_NAME, "Nah...".deser().vanilla())
    set(
        DataComponents.LORE,
        ItemLore(
            listOf(
                "...nothing to configure here!".deser().vanilla()
            )
        )
    )
}.snapshot()
private val iconAboutInfoStack = ItemStack(Items.BOOKSHELF).apply {
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
}.snapshot()
